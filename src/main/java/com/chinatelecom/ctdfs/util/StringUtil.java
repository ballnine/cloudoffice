package com.chinatelecom.ctdfs.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StringUtil {
	public static Boolean notNull(String obj) {
		if(obj!=null&&obj.length()>0) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 判断字符串是否为null对象或是空白字符
	 * 
	 * @param str 字符串
	 * @return boolean
	 */
	public static boolean isEmpty(String str) {
		return (str == null) || (str.trim().length() == 0);
	}
	
	/**
	 * 日期转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String dateToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = format.format(date);
		return str;
	}
	
	/**
	 * 用指定的字符分割字符串
	 * 
	 * @param str   String 原始字符串
	 * @param split String 分割符
	 * @return ArrayList:
	 * @author daive
	 */
	public static List<String> splitStr(String str, String split) {
		if (str == null || str.trim().equals("") || split == null || split.trim().equals("")) {
			return null;
		}
		List<String> list = new ArrayList<String>();
		while (str.indexOf(split) != -1) {
			int index = str.indexOf(split);
			String sub = str.substring(0, index);
			str = str.substring(index + 1);
			list.add(sub);
		}
		if (str != null && !str.equals("")) {
			list.add(str);
		}
		return list;
	}
	
	/**
	 * 年月日转换成字符串
	 * 
	 * @param date
	 * @return str
	 */
	public static String dayToStr(Date date) {

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String str = format.format(date);
		return str;
	}
}
