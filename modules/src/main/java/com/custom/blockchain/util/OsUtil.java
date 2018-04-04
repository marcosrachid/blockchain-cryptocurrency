package com.custom.blockchain.util;

import static com.custom.blockchain.costants.SystemConstants.COIN_ROOT_UNIX_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.COIN_ROOT_WINDOWS_DIRECTORY;

public final class OsUtil {

	private static String OS = System.getProperty("os.name").toLowerCase();

	public static boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

	public static String getRootDirectory() {
		if (isWindows()) {
			return COIN_ROOT_WINDOWS_DIRECTORY;
		} else {
			return COIN_ROOT_UNIX_DIRECTORY;
		}
	}

}
