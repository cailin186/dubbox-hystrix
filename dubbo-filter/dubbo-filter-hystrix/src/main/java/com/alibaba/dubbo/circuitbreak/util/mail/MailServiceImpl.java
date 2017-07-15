package com.alibaba.dubbo.circuitbreak.util.mail;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.dubbo.circuitbreak.util.DateUtil;
import com.alibaba.dubbo.circuitbreak.util.HttpRequestUtil;
import com.alibaba.dubbo.circuitbreak.util.ProfileUtil;

import java.util.HashMap;
import java.util.Map;

public class MailServiceImpl {
	private static Log LOGGER = LogFactory.getLog(MailServiceImpl.class);
	
	private static String sendMail = "true";

	
	public static String getSendMail() {
		return sendMail;
	}

	public static void setSendMail(String sendMail) {
		MailServiceImpl.sendMail = sendMail;
	}

	/**
	 * 新发邮件方法
	 * @param sendToArray
	 * @param subject
	 * @param content
	 */
	public static void sendMail(String[] sendToArray,String subject ,String content) {
		if (sendMail.equals("true")) {
			String url = ProfileUtil.getProperty("sendcloud.url");
			Map params = new HashMap();
			params.put("apiUser", ProfileUtil.getProperty("sendcloud.apiUser"));
			params.put("apiKey", ProfileUtil.getProperty("sendcloud.apiKey"));
			params.put("from", ProfileUtil.getProperty("sendcloud.apiUser"));
			
			if(null != sendToArray && sendToArray.length>0) {
				StringBuffer to = new StringBuffer();
				for(String s : sendToArray) {
					to.append(s).append(";");
				}
				params.put("to", to);
				params.put("subject", subject);
				String date = DateUtil.parseNowDateToString();
				content = "当前时间: "+date +"<br/>" +content;
				params.put("html", content);
				try {
					String temp = HttpRequestUtil.doPost(url, params, "utf-8");
					if(null != temp && temp.indexOf("false") > 0) {
						   LOGGER.error("mail send failed:"+temp);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
