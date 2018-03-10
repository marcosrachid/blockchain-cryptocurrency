package com.custom.blockchain.costants;

import java.io.File;

public class SystemConstants {

	public static final String COIN_ROOT_WINDOWS_DIRECTORY = System.getProperty("user.home") + File.separator
			+ "AppData" + File.separator + "Local" + File.separator + "%s";

	public static final String COIN_ROOT_UNIX_DIRECTORY = System.getProperty("user.home") + File.separator + "%s";

	public static final String BLOCKS_DIRECTORY = File.separator + "blocks";

	public static final String LEVEL_DB_BLOCKS_INDEX_DIRECTORY = BLOCKS_DIRECTORY + File.separator + "index";
	
	public static final String LEVEL_DB_CHAINSTATE_DIRECTORY = File.separator + "chainstate";
	
	public static final int MAXIMUM_SEEDS = 8;

}
