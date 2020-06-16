package com.custom.blockchain.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 
 * @author marcosrachid
 *
 */
public class NumberUtil extends NumberUtils {

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static byte[] toByteArray(BigDecimal value) {
		return value.unscaledValue().toByteArray();
	}

	/**
	 * 
	 * @param value
	 * @return
	 */
	public static BigDecimal toBigDecimal(byte[] value) {
		if (value == null)
			return BigDecimal.ZERO;
		BigInteger v = new BigInteger(value);
		return new BigDecimal(v, 0);
	}
}
