package com.custom.blockchain.util;

import static com.custom.blockchain.costants.SystemConstants.BLOCKS_DIRECTORY;

import java.io.File;

public class FileUtil {

	public static boolean isBlockchainStarted(String coinName) {
		File folder = new File(String.format(OsUtil.getRootDirectory() + BLOCKS_DIRECTORY, coinName));
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().matches("blk.*\\.dat")) {
				return true;
			}
		}
		return false;
	}

}
