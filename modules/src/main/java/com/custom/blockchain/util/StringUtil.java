package com.custom.blockchain.util;

import org.apache.commons.lang3.StringUtils;

public class StringUtil extends StringUtils {

	public static String getDificultyString(int difficulty) {
		return repeat('0', difficulty);
	}
	
}
