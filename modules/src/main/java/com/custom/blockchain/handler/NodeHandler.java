package com.custom.blockchain.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.node.component.NodeFork;
import com.custom.blockchain.resource.dto.request.RequestForkDTO;
import com.custom.blockchain.resource.dto.request.RequestPropertiesBlockDTO;
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

	private CurrentBlockDB currentBlockDB;

	private CurrentPropertiesBlockDB currentPropertiesBlockDB;

	private NodeFork nodeFork;

	public NodeHandler(final BlockchainProperties blockchainProperties, final CurrentBlockDB currentBlockDB,
			final CurrentPropertiesBlockDB currentPropertiesBlockDB, final NodeFork nodeFork) {
		this.blockchainProperties = blockchainProperties;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.nodeFork = nodeFork;
	}

	public ResponsePropertiesDTO getProperties() throws BusinessException {
		try {
			return new ResponsePropertiesDTO(coinName, version, blockchainProperties, currentPropertiesBlockDB);
		} catch (IOException e) {
			throw new BusinessException("Could not read properties data");
		}
	}

	/**
	 * 
	 * @param forkRequest
	 * @throws BusinessException
	 */
	public void fork(RequestForkDTO forkRequest) throws BusinessException {
		AbstractBlock currentBlock = currentBlockDB.get();
		if (currentBlock.getHeight().compareTo(forkRequest.getHeight()) >= 0) {
			throw new BusinessException("Requested height to be forked has already been used");
		}
		RequestPropertiesBlockDTO forkProperties = forkRequest.getPropertiesBlock();
		PropertiesBlock oldPropertiesBlock = currentPropertiesBlockDB.get();
		PropertiesBlock newPropertiesBlock = getNewPropertiesBlock(forkProperties, oldPropertiesBlock);
		nodeFork.enqueueFork(forkRequest.getHeight(), newPropertiesBlock);
	}

	/**
	 * 
	 * @param forkProperties
	 * @param oldPropertiesBlock
	 * @return
	 */
	private PropertiesBlock getNewPropertiesBlock(RequestPropertiesBlockDTO forkProperties,
			PropertiesBlock oldPropertiesBlock) {
		return new PropertiesBlock(
				(forkProperties.getMinimunTransaction() == null) ? oldPropertiesBlock.getMinimunTransaction()
						: forkProperties.getMinimunTransaction(),
				(forkProperties.getCoinLimit() == null) ? oldPropertiesBlock.getCoinLimit()
						: forkProperties.getCoinLimit(),
				(forkProperties.getMiningTimeRate() == null) ? oldPropertiesBlock.getMiningTimeRate()
						: forkProperties.getMiningTimeRate(),
				(forkProperties.getReward() == null) ? oldPropertiesBlock.getReward() : forkProperties.getReward(),
				(forkProperties.getBlockSize() == null) ? oldPropertiesBlock.getBlockSize()
						: forkProperties.getBlockSize(),
				(forkProperties.getCoinbase() == null) ? oldPropertiesBlock.getCoinbase()
						: forkProperties.getCoinbase());
	}

}
