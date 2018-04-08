package com.custom.blockchain.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author marcosrachid
 *
 */
public final class StringUtil extends StringUtils {

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
	
	/**
	 * 
	 * @param b
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static int sizeof(byte[] b) throws UnsupportedEncodingException {
		return b.length;
	}

	/**
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	public static byte[] compress(String s) throws IOException {
		if ((s == null) || (s.length() == 0)) {
			return null;
		}
		ByteArrayOutputStream obj = new ByteArrayOutputStream();
		GZIPOutputStream gzip = new GZIPOutputStream(obj);
		gzip.write(s.getBytes("UTF-8"));
		gzip.flush();
		gzip.close();
		return obj.toByteArray();
	}

	/**
	 * 
	 * @param compressed
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	public static String decompress(byte[] compressed) throws UnsupportedEncodingException, IOException {
		final StringBuilder outStr = new StringBuilder();
		if ((compressed == null) || (compressed.length == 0)) {
			return "";
		}
		if (isCompressed(compressed)) {
			final GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(compressed));
			final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(gis, "UTF-8"));

			String line;
			while ((line = bufferedReader.readLine()) != null) {
				outStr.append(line);
			}
		} else {
			outStr.append(compressed);
		}
		return outStr.toString();
	}

	/**
	 * 
	 * @param compressed
	 * @return
	 */
	public static boolean isCompressed(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
				&& (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}

}
