package com.custom.blockchain.configuration;

import static com.custom.blockchain.costants.SystemConstants.LEVEL_DB_CHAINSTATE_DIRECTORY;
import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
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

	@Bean("ChainStateDB")
	public DB createChainState(@Value("${application.name}") String coinName) throws IOException {
		Options options = new Options();
		return factory.open(
				new File(String.format(OsUtil.getRootDirectory() + LEVEL_DB_CHAINSTATE_DIRECTORY, coinName)), options);
	}

}
