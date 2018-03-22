package com.custom.blockchain.costants;

import java.io.File;

public class SystemConstants {

	public static final String TEMP_DIRECTORY = System.getProperty("java.io.tmpdir");

	public static final String COIN_ROOT_WINDOWS_DIRECTORY = System.getProperty("user.home") + File.separator
			+ "AppData" + File.separator + "Local" + File.separator + "%s" + File.separator;

	public static final String COIN_ROOT_UNIX_DIRECTORY = System.getProperty("user.home") + File.separator + "%s"
			+ File.separator;

	public static final String BLOCKS_DIRECTORY = "blocks" + File.separator;

	public static final String LEVEL_DB_BLOCK_INDEX_DIRECTORY = BLOCKS_DIRECTORY + "index" + File.separator;

	public static final String LEVEL_DB_CHAINSTATE_DIRECTORY = "chainstate" + File.separator;

	public static final String BLOCK_FILE = "blk%s.dat";

	public static final String MEMPOOL_FILE = "mempool.dat";

	public static final String PEERS_FILE = "peers.dat";

}
