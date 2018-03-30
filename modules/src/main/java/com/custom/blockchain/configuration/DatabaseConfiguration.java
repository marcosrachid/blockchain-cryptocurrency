package com.custom.blockchain.configuration;

import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_BLOCKS_BLOCK_SIZE;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_BLOCKS_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_BLOCK_SIZE;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_MEMPOOL_DIRECTORY;
import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_PEERS_DIRECTORY;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custom.blockchain.util.OsUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Configuration
public class DatabaseConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(DatabaseConfiguration.class);

	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	@Bean("BlockDB")
	public DB createBlockIndex(@Value("${application.blockchain.coinName}") String coinName) throws IOException {
		LOG.info("[Crypto] Creating Block database connection...");
		Options options = new Options();
		options.blockSize(LEVEL_DB_BLOCKS_BLOCK_SIZE);
		String path = String.format(OsUtil.getRootDirectory() + LEVEL_DB_BLOCKS_DIRECTORY, coinName);
		LOG.debug("[Crypto] Path of block index leveldb: " + path);
		return factory.open(new File(path), options);
	}

	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	@Bean("ChainStateDB")
	public DB createChainState(@Value("${application.blockchain.coinName}") String coinName) throws IOException {
		LOG.info("[Crypto] Creating ChainState database connection...");
		Options options = new Options();
		options.blockSize(LEVEL_DB_CHAINSTATE_BLOCK_SIZE);
		String path = String.format(OsUtil.getRootDirectory() + LEVEL_DB_CHAINSTATE_DIRECTORY, coinName);
		LOG.debug("[Crypto] Path of chainstate leveldb: " + path);
		return factory.open(new File(path), options);
	}

	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	@Bean("PeersDB")
	public DB createPeers(@Value("${application.blockchain.coinName}") String coinName) throws IOException {
		LOG.info("[Crypto] Creating Peers database connection...");
		Options options = new Options();
		String path = String.format(OsUtil.getRootDirectory() + LEVEL_DB_PEERS_DIRECTORY, coinName);
		LOG.debug("[Crypto] Path of chainstate leveldb: " + path);
		return factory.open(new File(path), options);
	}

	/**
	 * 
	 * @param coinName
	 * @return
	 * @throws IOException
	 */
	@Bean("MempoolDB")
	public DB createMempool(@Value("${application.blockchain.coinName}") String coinName) throws IOException {
		LOG.info("[Crypto] Creating Mempool database connection...");
		Options options = new Options();
		String path = String.format(OsUtil.getRootDirectory() + LEVEL_DB_MEMPOOL_DIRECTORY, coinName);
		LOG.debug("[Crypto] Path of chainstate leveldb: " + path);
		return factory.open(new File(path), options);
	}

}
