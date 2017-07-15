package com.alibaba.dubbo.circuitbreak.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

/**
 * <pre>
 * HTTP请求代理类
 * System.setProperty("sun.net.client.defaultConnectTimeout", String.valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
 * System.setProperty("sun.net.client.defaultReadTimeout", String.valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
 * </pre>
 *
 * <pre> 增加httpclient的get请求.解决用http的get请求无法识别的问题20151119</pre>
 * @author cailin
 * @version 1.1
 */
public class HttpRequestUtil
{
    /**
     * 连接超时
     */
    private static int connectTimeOut = 50000;
    /**
     * 读取数据超时
     */
    private static int readTimeOut = 50000;
    /**
     * 请求编码
     */
    private static String requestEncoding = "UTF-8";
    private static Logger logger = Logger.getLogger(HttpRequestUtil.class);

    /**
     * <pre>
     * 发送带参数的GET的HTTP请求
     * </pre>
     * @param reqUrl HTTP请求URL
     * @param parameters 参数映射表
     * @return HTTP响应的字符串
     */
    public static String doGet(String reqUrl, Map parameters,
            String recvEncoding)
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();)
            {
                Entry element = (Entry) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(),HttpRequestUtil.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0)
            {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            //jdk1.5
            url_con.setConnectTimeout(connectTimeOut);
            url_con.setReadTimeout(readTimeOut);
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer temp = new StringBuffer();
            String crlf=System.getProperty("line.separator");
            while (tempLine != null)
            {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        }
        catch (IOException e)
        {
            logger.error("网络故障", e);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    /**
     * <pre>
     * 发送不带参数的GET的HTTP请求
     * </pre>
     *
     * @param reqUrl HTTP请求URL
     * @return HTTP响应的字符串
     */
    public static String doGet(String reqUrl, String recvEncoding)
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuffer params = new StringBuffer();
            String queryUrl = reqUrl;
            int paramIndex = reqUrl.indexOf("?");

            if (paramIndex > 0)
            {
                queryUrl = reqUrl.substring(0, paramIndex);
                String parameters = reqUrl.substring(paramIndex + 1, reqUrl.length());
                String[] paramArray = parameters.split("&");
                for (int i = 0; i < paramArray.length; i++)
                {
                    String string = paramArray[i];
                    int index = string.indexOf("=");
                    if (index > 0)
                    {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1, string
                                .length());
                        params.append(parameter);
                        params.append("=");
                        params.append(URLEncoder.encode(value,HttpRequestUtil.requestEncoding));
                        params.append("&");
                    }
                }

                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(queryUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            url_con.setConnectTimeout(connectTimeOut);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            url_con.setReadTimeout(readTimeOut);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer temp = new StringBuffer();
            String crlf=System.getProperty("line.separator");
            while (tempLine != null)
            {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        }
        catch (IOException e)
        {
            logger.error("网络故障", e);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    /**
     * <pre>
     * 发送带参数的POST的HTTP请求
     * </pre>
     *
     * @param reqUrl HTTP请求URL
     * @param parameters 参数映射表
     * @return HTTP响应的字符串
     */
    public static String doPost(String reqUrl, Map parameters,
            String recvEncoding)
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();)
            {
                Entry element = (Entry) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(),HttpRequestUtil.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0)
            {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            url_con.setConnectTimeout(connectTimeOut);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            url_con.setReadTimeout(readTimeOut);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer tempStr = new StringBuffer();
            String crlf=System.getProperty("line.separator");
            while (tempLine != null)
            {
                tempStr.append(tempLine);
                tempStr.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = tempStr.toString();
            rd.close();
            in.close();
        }
        catch (IOException e)
        {
            logger.error("网络故障", e);
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }
        return responseContent;
    }
    /**
     * @return 连接超时(毫秒)
     * @see com.founder.util.HttpRequestUtil#connectTimeOut
     */
    public static int getConnectTimeOut()
    {
        return HttpRequestUtil.connectTimeOut;
    }

    /**
     * @return 读取数据超时(毫秒)
    *  @see com.founder.util.HttpRequestUtil#readTimeOut
     */
    public static int getReadTimeOut()
    {
        return HttpRequestUtil.readTimeOut;
    }

    /**
     * @return 请求编码
     * @see com.founder.util.HttpRequestUtil#requestEncoding
     */
    public static String getRequestEncoding()
    {
        return requestEncoding;
    }

    /**
     * @param connectTimeOut 连接超时(毫秒)
     */
    public static void setConnectTimeOut(int connectTimeOut)
    {
        HttpRequestUtil.connectTimeOut = connectTimeOut;
    }

    /**
     * @param readTimeOut 读取数据超时(毫秒)
     */
    public static void setReadTimeOut(int readTimeOut)
    {
        HttpRequestUtil.readTimeOut = readTimeOut;
    }

    /**
     * @param requestEncoding 请求编码
     */
    public static void setRequestEncoding(String requestEncoding)
    {
        HttpRequestUtil.requestEncoding = requestEncoding;
    }
    
   

}