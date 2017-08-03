/**
 * 配置文件读取工具 
 * Copyright (c) 2002
 * All rights reserved.
 * @author cailin
 * @version 1.2
 * Date:2009-7-18
 */
package com.alibaba.dubbo.circuitbreak.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProfileUtil {
	private static final Logger LOG = LoggerFactory.getLogger(ProfileUtil.class);
	/**
	 * update ch: 类文件的资源加载一次就行
	 */
	private static final Properties pros = new Properties();

	static {
		try {
			FileInputStream fis = new FileInputStream(ProfileUtil.class.getResource("/mail.properties").getPath());
			pros.load(fis);
		}catch(Exception e) {
			e.printStackTrace();
			LOG.error(e.getMessage(), e);
		}
	}
	public static String getProperty(String fileName, String pro) {
		Properties ssoProp = new Properties();
		FileInputStream ssoFis;
		try {
			ssoFis = new FileInputStream(ProfileUtil.class.getResource("/" + fileName).getPath());
			ssoProp.load(ssoFis);
		} catch (FileNotFoundException e) {
			LOG.error("FileNotFoundException{}", e);
		} catch (InvalidPropertiesFormatException e) {
			LOG.error("InvalidPropertiesFormatException{}", e);
		} catch (IOException e) {
			LOG.error("IOException{}", e);
		}

		String properties = ssoProp.getProperty(pro);
		return properties;
	}

	public static String getProperty(String pro) {
		String properties = pros.getProperty(pro);
		return properties;
	}

	public static void setProperty(String key, Object value) {
		try {
			PropertiesConfiguration configuration = new PropertiesConfiguration("mail.properties");
			configuration.setProperty(key, value);
			configuration.save();
		} catch (ConfigurationException e) {
			LOG.error("ConfigurationException{}", e);
		}

	}

	public static void main(String[] args) {

	}
}
