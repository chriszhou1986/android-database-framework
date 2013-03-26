package com.wu.databasedemo.db;

import java.util.Collection;

final class Utility {

	public static byte parseByte(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			byte result = Byte.parseByte(value.trim());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static short parseShort(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			short result = Short.parseShort(value.trim());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int parseInt(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			int result = Integer.parseInt(value.trim());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long parseLong(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			long result = Long.parseLong(value.trim());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static float parseFloat(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			float result = Float.parseFloat(value);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static double parseDouble(String value) {
		if (value == null || value.trim().length() == 0) {
			return 0;
		}
		try {
			double result = Double.parseDouble(value.trim());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean text2Boolean(String value) {
		if (value == null || value.trim().length() == 0) {
			return false;
		}
		if ("1".equals(value) || "true".equalsIgnoreCase(value)) {
			return true;
		}
		return false;
	}

	public static char parseChar(String value) {
		if (value == null || value.trim().length() == 0) {
			return ' ';
		}
		if (value.length() == 1) {
			return value.charAt(0);
		}
		return ' ';
	}

	public static int size(Collection<?> coll) {
		if (coll == null) {
			return 0;
		}
		return coll.size();
	}

}
