package com.custom.blockchain.util;

import static com.custom.blockchain.costants.ChainConstants.BLK_DAT_MAX_FILE_SIZE;
import static com.custom.blockchain.costants.SystemConstants.BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.BLOCK_FILE;
import static com.custom.blockchain.costants.SystemConstants.MEMPOOL_FILE;
import static com.custom.blockchain.costants.SystemConstants.PEERS_FILE;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.FileUtils;

import com.custom.blockchain.block.exception.BlockException;

/**
 * 
 * @author marcosrachid
 *
 */
public class FileUtil {

	private static final String EMPTY_LIST = "[]";

	/**
	 * 
	 * @param coinName
	 * @return
	 */
	public static boolean isBlockchainStarted(String coinName) {
		String path = String.format(OsUtil.getRootDirectory() + BLOCKS_DIRECTORY, coinName);
		File folder = new File(path);
		for (File file : folder.listFiles()) {
			if (file.isFile() && file.getName().matches("blk.*\\.dat")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param coinName
	 * @param blockJson
	 * @throws IOException
	 */
	public static boolean isCurrentFileFull(String coinName, String blockJson, Long fileNumber) throws IOException {
		if (blockJson.length() > BLK_DAT_MAX_FILE_SIZE) {
			readBlocksFile(coinName, fileNumber + 1);
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @param peerJson
	 * @throws IOException
	 * @throws BlockException
	 */
	public static void addBlock(String coinName, String blockJson, Long fileNumber) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		String fileName = String.format(BLOCK_FILE, fileNumber);
		File file = new File(path + BLOCKS_DIRECTORY + fileName);
		FileUtils.writeByteArrayToFile(file, compress(blockJson));
	}

	/**
	 * 
	 * @param coinName
	 * @param fileNumber
	 * @return
	 * @throws IOException
	 */
	public static String readBlocksFile(String coinName, Long fileNumber) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		String fileName = String.format(BLOCK_FILE, fileNumber);
		File file = new File(path + BLOCKS_DIRECTORY + fileName);
		if (!file.exists()) {
			FileUtils.writeByteArrayToFile(file, compress(EMPTY_LIST));
		}
		byte[] content = Files.readAllBytes(file.toPath());
		return decompress(content);
	}

	/**
	 * 
	 * @param peerJson
	 * @throws IOException
	 */
	public static void addPeer(String coinName, String peerJson) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		File file = new File(path + PEERS_FILE);
		FileUtils.writeByteArrayToFile(file, compress(peerJson));
	}

	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	public static String readPeer(String coinName) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		File file = new File(path + PEERS_FILE);
		if (!file.exists()) {
			FileUtils.writeByteArrayToFile(file, compress(EMPTY_LIST));
		}
		byte[] content = Files.readAllBytes(file.toPath());
		return decompress(content);
	}

	/**
	 * 
	 * @param transactionJson
	 * @throws IOException
	 */
	public static void addUnminedTransaction(String coinName, String transactionJson) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		File file = new File(path + MEMPOOL_FILE);
		FileUtils.writeByteArrayToFile(file, compress(transactionJson));
	}

	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	public static String readUnminedTransaction(String coinName) throws IOException {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		File file = new File(path + MEMPOOL_FILE);
		if (!file.exists()) {
			FileUtils.writeByteArrayToFile(file, compress(EMPTY_LIST));
		}
		byte[] content = Files.readAllBytes(file.toPath());
		return decompress(content);
	}

	/**
	 * 
	 * @param s
	 * @return
	 * @throws IOException
	 */
	private static byte[] compress(String s) throws IOException {
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
	private static String decompress(byte[] compressed) throws UnsupportedEncodingException, IOException {
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
	private static boolean isCompressed(final byte[] compressed) {
		return (compressed[0] == (byte) (GZIPInputStream.GZIP_MAGIC))
				&& (compressed[1] == (byte) (GZIPInputStream.GZIP_MAGIC >> 8));
	}

}
