package com.custom.blockchain.util;

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
	
	public static int getBiggestLength(String... strings) {
		int biggest = 0;
		for (String s : strings) {
			biggest = (biggest > s.length()) ? biggest : s.length();
		}
		return biggest;
	}
	
}
