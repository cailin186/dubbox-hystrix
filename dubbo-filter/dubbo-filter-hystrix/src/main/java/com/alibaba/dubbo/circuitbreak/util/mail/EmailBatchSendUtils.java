package com.alibaba.dubbo.circuitbreak.util.mail;

import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.alibaba.dubbo.circuitbreak.util.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by wangxing01 on 2017/1/23.
 */
public class EmailBatchSendUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailBatchSendUtils.class);

    //实例容器
    private static  Map<String, EmailBatchSendUtils> instances = Maps.newConcurrentMap();
    //邮件队列
    private static Map<String, Queue<String>> queues = Maps.newConcurrentMap();
    //邮件队列最后的发送时间
    private static Map<String, Long> lastSendTime = Maps.newConcurrentMap();
    //需要发送的邮箱用户
    private Set<String> sendToArray;
    //发送的邮箱主题
    private  String subject;
    //邮件队列发送的
    private static Integer queueSize = 100;

    //固定时间发送邮件的间隔时间（精确到秒，默认5分钟）
    private static Long fixSendTimeGap = 60 * 5L;
    
    //收件人
    
    private static String[] mailRecipients =null;


    static {
        //启动定时发送邮件的线程
        new Thread(new FixTimeEmailSender()).start();
    }

    
    private EmailBatchSendUtils(String subject) {
        this.subject = subject;
        new Thread(new EmailSender()).start();
    }

    public static EmailBatchSendUtils getInstance(String subject) {
        EmailBatchSendUtils instance = instances.get(subject);
        if (null == instance ) {
            synchronized (instances) {
                instance = instances.get(subject);
                if (null == instance) {
                        EmailBatchSendUtils newInstance = new EmailBatchSendUtils(subject);
                        instances.put(subject, newInstance);
                        return newInstance;
                } else{
                    if(!isBatchSendObjectEquals(instance,subject)) {
                        EmailBatchSendUtils newInstance = new EmailBatchSendUtils(subject);
                        instances.put(subject, newInstance);
                        return newInstance;
                    } else {
                        return instance;
                    }
                }
            }
        }
        return instance;
    }

    private static boolean isBatchSendObjectEquals(EmailBatchSendUtils emailSender, String subject) {
        if (null == emailSender) return false;
        if (!subject.equals((emailSender.subject))) return false;
        return true;
    }
    
    public  static void sendMail(String mailRecipient,String subject,String content){
        String mailSubject = getLocalHostAddress()+":"+subject;
        if(mailRecipient.indexOf(",")>0){
            mailRecipients = mailRecipient.split(",");
    		EmailBatchSendUtils emailBatchSendUtils = EmailBatchSendUtils.getInstance(mailSubject);
            emailBatchSendUtils.addToMailQueue(mailSubject, content);
        }else{
        	LOGGER.error("mailRecipient is not valid{}",mailRecipients);
        }

    }
    
    /**
     * getHostAddress:获取本地ip地址和机器名
     *
     * @return 172.16.0.10[hostname]
     * @author cailin
     * Date:2017年1月10日上午10:36:45
     */
    private static String getLocalHostAddress() {
        String hostAddr = "";
        try {
            InetAddress addr = InetAddress.getLocalHost();
            String ip = addr.getHostAddress().toString();
            String address = addr.getHostName().toString();
            hostAddr = ip + "[" + address + "]";
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostAddr;
    }

    public void addToMailQueue(String subject, String content) {
        Queue<String> queue = queues.get(subject);
        if (null == queue) {
            synchronized (queues) {
                queue = queues.get(subject);
                if (null == queue) {
                    queue =  new ConcurrentLinkedQueue<String>();
                    queue.add(content);
                    lastSendTime.put(subject, System.currentTimeMillis()/1000);
                    queues.put(subject, queue);
                    queues.notifyAll();
                }
            }
        }
        if (null == instances.get(subject)) {
            throw new IllegalArgumentException("the subject: { " + subject + "} do not have email sender");
        }
        synchronized (queue) {
            queue.add(content);
            LOGGER.info("subject: {}, the queue size is : {}", subject, queue.size());
            String size =  ProfileUtil.getProperty("email.queue.size");
            if(StringUtils.isNotEmpty(size)) {
                queueSize = Integer.parseInt(size);
            }
            if (queue.size() >= queueSize) {
                queue.notifyAll();
            }
        }
    }

    private class EmailSender implements Runnable {
        @Override
        public void run() {
            while (true) {
                Queue<String> queue = queues.get(subject);
                while (true) {
                    if (null == queue) {
                        try {
                            queues.wait();
                        } catch (Throwable e) {
                        }
                        queue = queues.get(subject);
                    } else {
                        break;
                    }
                }
                synchronized (queue) {
                    String size =  ProfileUtil.getProperty("email.queue.size");
                    if(StringUtils.isNotEmpty(size)) {
                        queueSize = Integer.parseInt(size);
                    }
                    if (queue.size() >= queueSize) {
                        LOGGER.error("when send the subject: {}, the queue size is : {}", subject, queue.size());
                        try {
                            String[] allDatas = new String[queue.size()];
                            queue.toArray(allDatas);
                            List<Object[]> groupDatas =  ArrayUtil.splitArray(allDatas, queueSize);
                    
                            for (Object[] datas : groupDatas) {
                                String content = Joiner.on("</br>").join(datas);
                                MailServiceImpl.sendMail(mailRecipients, subject, content);
                                //将最后的发送时间更新
                                lastSendTime.put(subject, System.currentTimeMillis()/1000);
                            }
                            queue.clear();
                        } catch (Exception e) {
                              e.printStackTrace();
                        }

                    } else {
                        try {
                            queue.wait();
                        } catch (Throwable e) {
                        }
                    }
                }
            }
        }

		
    }


    private static class FixTimeEmailSender implements Runnable {
        @Override
        public void run() {
            while (true) {
                String timeInterval =  ProfileUtil.getProperty("email.fixsend.time.interval");
                if(StringUtils.isNotEmpty(timeInterval)) {
                    fixSendTimeGap = Long.parseLong(timeInterval);
                }
                synchronized (queues) {
                    Set<String> queueKeys = queues.keySet();
                    for (String key : queueKeys) {
                        Queue<String> queue = queues.get(key);
                        if (queue.size() == 0) continue;

                        Long time = lastSendTime.get(key);
                        if (null == time) continue;

                        try {
                            if ((time + fixSendTimeGap) <= System.currentTimeMillis()/1000) {
                                EmailBatchSendUtils sender = instances.get(key);
                                if (null == sender) continue;

                                LOGGER.error("when fix time sender send the subject: {}, the queue size is : {}", key, queue.size());
                                String[] allDatas = new String[queue.size()];
                                queue.toArray(allDatas);
                                List<Object[]> groupDatas =  ArrayUtil.splitArray(allDatas, queueSize);
                                for (Object[] datas : groupDatas) {
                                    String content = Joiner.on("</br>").join(datas);
      
                                    MailServiceImpl.sendMail(mailRecipients, key, content);
                                }
                                queue.clear();
                                //将最后的发送时间更新
                                lastSendTime.put(key, System.currentTimeMillis()/1000);
                            }
                        } catch (Exception e) {
                            LOGGER.error("FixTimeEmailSender exception",e);
                        }
                    }
                }
                try {
                    Thread.sleep(fixSendTimeGap * 1000);
                } catch (Exception e) {

                }

            }
        }
    }
    private static  String[] builderMailRecipients() {
		
		String[] mailRecipientsConfig=null;
		if(mailRecipients.length>0){
			return mailRecipients;
		}else{
			String mailTo = ProfileUtil.getProperty("monitor.mail.to") == null ? "cailin@51talk.com" : ProfileUtil.getProperty("monitor.mail.to");
			mailRecipientsConfig = mailTo.split(",");
			mailRecipients = mailRecipientsConfig;
		}

		return mailRecipients;
	}
    



}
