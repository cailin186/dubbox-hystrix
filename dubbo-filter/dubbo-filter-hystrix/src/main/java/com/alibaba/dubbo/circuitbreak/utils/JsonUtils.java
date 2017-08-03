package com.alibaba.dubbo.circuitbreak.utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Description: jackson工具类
 * @author caohui
 */
public final class JsonUtils {

	private JsonUtils() {}
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	static {
		objectMapper.setSerializationInclusion(Include.NON_NULL);
		objectMapper.setTimeZone(TimeZone.getDefault());
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
	}
	
	public static String toJson(Object obj) {
		try{
			StringWriter sw = new StringWriter();
			objectMapper.writeValue(sw, obj);
			return sw.toString();
		}catch(IOException e) {
			return null;
		}
	}
	
	public static JavaType getCollectionType(Class<?> collectionClass, Class<?> ... elementClass) {
		return objectMapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
	}
	
	public static <T> T parseObject(String value, Class<?> collectionClass, Class<?> ... elementClasses) {
		try {
			return objectMapper.readValue(value, getCollectionType(collectionClass, elementClasses));
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	public static <T> T parseObject(byte[] value, Class<?> collectionClass, Class<?> ... elementsClasses) {
		try {
			return objectMapper.readValue(value, getCollectionType(collectionClass, elementsClasses));
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> T parseObject(String value, Class<T> type) {
		try {
			return objectMapper.readValue(value, type);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
