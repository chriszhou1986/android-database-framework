package com.wu.databasedemo.db;

import org.json.JSONException;
import org.json.JSONObject;

public class Util {
	
	public static String boolean2Text(boolean value) {
		if (value) {
			return "1";
		}
		return "0";
	}
	
	public static boolean text2Boolean(String value) {
		if (value == null || value.length() == 0) {
			return false;
		}
		if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
			return true;
		}
		return false;
	}

	/**
	 * 把字符串parse成double，若传入字符串为空或转换时异常则返回0。
	 */
	public static double parseDouble(String str) {
		if (str == null || str.length() == 0 || str.trim().length() == 0) {
			return 0;
		}
		try {
			double result = Double.parseDouble(str.trim());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 把字符串parse成int，若传入字符串为空或转换时异常则返回0。
	 */
	public static int parseInt(String str) {
		double temp = parseDouble(str);
		return (int) (temp + 0.5);
	}
	
	public static long parseLong(String str) {
		if (str == null || str.length() == 0 || str.trim().length() == 0) {
			return 0;
		}
		try {
			long result = Long.parseLong(str.trim());
			return result;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static float parseFloat(String str) {
		if (str == null || str.length() == 0 || str.trim().length() == 0) {
			return 0;
		}
		try {
			float result = Float.parseFloat(str);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

}
