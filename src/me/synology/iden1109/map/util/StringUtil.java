package me.synology.iden1109.map.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtil {

	private static final Logger LOG = LoggerFactory.getLogger(StringUtil.class);
	
	public static String replace(String target){
		return target.trim().replaceAll("臺", "台");
	}
	
	public static String decode(String s) {
		try {
			return URLDecoder.decode(s, "UTF-8").trim();
		} catch (UnsupportedEncodingException e) {
			LOG.error("Unsupported Encoding Exception", e.toString());
		}
		return s;
		
	}
}
