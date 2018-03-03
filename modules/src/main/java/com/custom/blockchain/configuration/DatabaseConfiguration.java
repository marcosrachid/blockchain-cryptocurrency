package com.custom.blockchain.configuration;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.custom.blockchain.util.OsUtil;

@Configuration
public class DatabaseConfiguration {

	@Bean("LevelDB")
	public DB createDatabase(@Value("${application.name}") String coinName) throws IOException {
		Options options = new Options();
		String homeDir = System.getProperty("user.home");
		if (OsUtil.isWindows())
			return factory.open(new File(homeDir + File.separator + "AppData" + File.separator + "Local"
					+ File.separator + coinName + File.separator + "data"), options);
		else
			return factory.open(new File(homeDir + File.separator + coinName + File.separator + "data"), options);
	}

}
