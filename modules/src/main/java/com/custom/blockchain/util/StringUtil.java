package com.custom.blockchain.util;

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author marcosrachid
 *
 */
public class StringUtil extends StringUtils {

	/**
	 * 
	 * @param difficulty
	 * @return
	 */
	public static String getDificultyString(int difficulty) {
		return repeat('0', difficulty);
	}

	/**
	 * 
	 * @param strings
	 * @return
	 */
	public static int getBiggestLength(String... strings) {
		int biggest = 0;
		for (String s : strings) {
			biggest = (biggest > s.length()) ? biggest : s.length();
		}
		return biggest;
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static char getFirstCharacter(String string) {
		return string.charAt(0);
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	public static String removeFirstCharacter(String string) {
		return string.substring(1);
	}

	/**
	 * 
	 * @param string
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int sizeof(String string) throws UnsupportedEncodingException {
		byte[] b = string.getBytes("UTF-8");
		return b.length;
	}

}
