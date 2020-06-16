package com.custom.blockchain.constants;

import java.io.File;

public class SystemConstants {

	public static final String COIN_ROOT_WINDOWS_DIRECTORY = System.getProperty("user.home") + File.separator
			+ "AppData" + File.separator + "Local" + File.separator + "%s" + File.separator;

	public static final String COIN_ROOT_UNIX_DIRECTORY = System.getProperty("user.home") + File.separator + "%s"
			+ File.separator;

	public static final String LEVEL_DB_BLOCKS_DIRECTORY = "blocks" + File.separator;

	public static final int LEVEL_DB_BLOCKS_BLOCK_SIZE = 2 * 1024;

	public static final String LEVEL_DB_CHAINSTATE_DIRECTORY = "chainstate" + File.separator;

	public static final int LEVEL_DB_CHAINSTATE_BLOCK_SIZE = 2 * 1024;

	public static final String LEVEL_DB_PEERS_DIRECTORY = "peers" + File.separator;

	public static final String LEVEL_DB_MEMPOOL_DIRECTORY = "mempool" + File.separator;

	public static final Integer DIFFICULTY_ADJUSTMENT_BLOCK = 1000;

	public static final Integer MAX_NETWORK_SIZE_PACKAGE = 1000000;

}
