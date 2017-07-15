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
import org.apache.log4j.Logger;

public class ProfileUtil {
	private static Logger logger = Logger.getLogger(ProfileUtil.class);

	public static String getProperty(String fileName, String pro) {
		Properties ssoProp = new Properties();
		FileInputStream ssoFis;
		try {
			ssoFis = new FileInputStream(ProfileUtil.class.getResource("/" + fileName).getPath());
			ssoProp.load(ssoFis);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException{}", e);
		} catch (InvalidPropertiesFormatException e) {
			logger.error("InvalidPropertiesFormatException{}", e);
		} catch (IOException e) {
			logger.error("IOException{}", e);
		}

		String properties = ssoProp.getProperty(pro);
		return properties;
	}

	public static String getProperty(String pro) {
		Properties ssoProp = new Properties();
		FileInputStream ssoFis;
		try {
			ssoFis = new FileInputStream(ProfileUtil.class.getResource("/mail.properties").getPath());
			ssoProp.load(ssoFis);
		} catch (FileNotFoundException e) {
			logger.error("FileNotFoundException{}", e);
		} catch (InvalidPropertiesFormatException e) {
			logger.error("InvalidPropertiesFormatException{}", e);
		} catch (IOException e) {
			logger.error("IOException{}", e);
		}

		String properties = ssoProp.getProperty(pro);
		return properties;
	}

	public static void setProperty(String key, Object value) {
		try {
			PropertiesConfiguration configuration = new PropertiesConfiguration("mail.properties");
			configuration.setProperty(key, value);
			configuration.save();
		} catch (ConfigurationException e) {
			logger.error("ConfigurationException{}", e);
		}

	}

	public static void main(String[] args) {

	}
}
