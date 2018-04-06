package com.custom.blockchain.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.resource.dto.response.ResponsePropertiesDTO;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NodeHandler {

	@Value("${application.blockchain.coinName}")
	private String coinName;

	@Value("${application.blockchain.version}")
	private String version;

	private BlockchainProperties blockchainProperties;

	private CurrentPropertiesBlockDB currentPropertiesBlockDB;

	public NodeHandler(final BlockchainProperties blockchainProperties,
			final CurrentPropertiesBlockDB currentPropertiesBlockDB) {
		this.blockchainProperties = blockchainProperties;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
	}

	public ResponsePropertiesDTO getProperties() throws BusinessException {
		try {
			return new ResponsePropertiesDTO(coinName, version, blockchainProperties, currentPropertiesBlockDB);
		} catch (IOException e) {
			throw new BusinessException("Could not read properties data");
		}
	}

}
