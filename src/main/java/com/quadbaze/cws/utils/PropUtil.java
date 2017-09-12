package com.quadbaze.cws.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropUtil {

	private static Properties getProperties(String fileName, String key){
		Properties properties = new Properties();
		try {
			final InputStream input = PropUtil.class.getClassLoader().getResourceAsStream(fileName);
			try{
				properties.load(input);
			}finally{
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		return properties;
	}
	
	public static String getProperty(String fileName, String key) {
		Properties properties = getProperties(fileName, key);
		return properties != null ? properties.getProperty(key) : null;
	}
	
	public static Properties getProperties(String fileName) {
		return getProperties(fileName, null);
	}
	
}
