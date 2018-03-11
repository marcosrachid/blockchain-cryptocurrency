package com.custom.blockchain.util;

import static com.custom.blockchain.costants.LogMessagesConstants.JSON_FORMAT;
import static com.custom.blockchain.costants.SystemConstants.BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.PEERS_FILE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author marcosrachid
 *
 */
public class FileUtil {

	private static final Logger LOG = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 
	 * @param coinName
	 * @return
	 */
	public static boolean isBlockchainStarted(String coinName) {
		String path = String.format(OsUtil.getRootDirectory() + BLOCKS_DIRECTORY, coinName);
		File folder = new File(path);
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().matches("blk.*\\.json")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param peerJson
	 * @throws IOException
	 */
	public static void addPeer(String coinName, String peerJson) throws IOException {
		LOG.debug(JSON_FORMAT, "PEER", peerJson);

		String path = String.format(OsUtil.getRootDirectory(), coinName);
		File file = new File(path + PEERS_FILE);
		FileWriter writer = new FileWriter(file, false);
		PrintWriter printer = new PrintWriter(writer);
		printer.write(peerJson);
		printer.close();
	}
	
	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	public static String readPeer(String coinName) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		return new String(Files.readAllBytes(Paths.get(path + PEERS_FILE)));
	}

	/**
	 * 
	 * @param transactionJson
	 */
	public static void addUnminedTransaction(String coinName, String transactionJson) {
		LOG.debug(JSON_FORMAT, "TRANSACTION", transactionJson);
	}

}
