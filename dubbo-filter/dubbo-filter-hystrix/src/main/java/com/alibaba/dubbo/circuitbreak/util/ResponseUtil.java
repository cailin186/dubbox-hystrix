package com.alibaba.dubbo.circuitbreak.util;

public class ResponseUtil {

	public static String success(String message) {
		return "{\"code\":\"10000\",\"message\":\"" + message + "\"}";
	}

	public static String fail(String message) {
		return "{\"code\":\"10001\",\"message\":\"" + message + "\"}";
	}

}
