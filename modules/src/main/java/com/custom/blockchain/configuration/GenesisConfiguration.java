package com.custom.blockchain.configuration;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.service.BlockService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class GenesisConfiguration {

	private static final Logger LOG = LoggerFactory.getLogger(GenesisConfiguration.class);

	@Value("${arg.genesis:#{null}}")
	private String genesis;

	@Bean("StartingProperties")
	public PropertiesBlock getGenesis(final ObjectMapper objectMapper, final BlockService blockService)
			throws Exception {
		PropertiesBlock propertiesBlock = null;
		try {
			if (genesis == null) {
				File file = new ClassPathResource("config/default.json").getFile();
				propertiesBlock = objectMapper.readValue(file, PropertiesBlock.class);
			} else {
				propertiesBlock = objectMapper.readValue(genesis.getBytes(), PropertiesBlock.class);
			}
			propertiesBlock.setHash(blockService.calculateHash(propertiesBlock));
			return propertiesBlock;
		} catch (IOException e) {
			LOG.debug("[Crypto] Error getting json properties: {}", e.getMessage(), e);
			throw new Exception("Could not open a valid property");
		}
	}

}
