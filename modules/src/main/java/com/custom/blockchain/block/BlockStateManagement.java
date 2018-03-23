package com.custom.blockchain.block;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockStateManagement {

	private BlockchainProperties blockchainProperties;

	private CurrentBlockChainstateDB currentBlockChainstateDB;

	private ObjectMapper objectMapper;

	private Block nextBlock;

	public BlockStateManagement(final BlockchainProperties blockchainProperties,
			final CurrentBlockChainstateDB currentBlockChainstateDB, final ObjectMapper objectMapper) {
		this.blockchainProperties = blockchainProperties;
		this.currentBlockChainstateDB = currentBlockChainstateDB;
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 * @param block
	 * @throws BlockException
	 */
	public void foundBlock(Block block) throws BlockException {
		currentBlockChainstateDB.put(block);
		nextBlock = BlockFactory.getBlock(block);
		try {
			String jsonBlockList = addBlockToCurrentFile(block);
			if (FileUtil.isCurrentFileFull(blockchainProperties.getCoinName(), jsonBlockList)) {
				jsonBlockList = addBlockToCurrentFile(block);
			}
			FileUtil.addBlock(blockchainProperties.getCoinName(), jsonBlockList);
		} catch (IOException e) {
			throw new BlockException("Could not register new Block");
		}
	}

	/**
	 * 
	 * @return
	 */
	public Block getNextBlock() {
		return nextBlock;
	}

	/**
	 * 
	 * @param block
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private String addBlockToCurrentFile(Block block) throws IOException {
		JavaType collectionBlockClass = objectMapper.getTypeFactory().constructCollectionType(Set.class, Block.class);
		Set<Block> blockList = objectMapper.readValue(FileUtil.readCurrentBlock(blockchainProperties.getCoinName()),
				collectionBlockClass);
		blockList.add(block);
		return objectMapper.writeValueAsString(blockList);
	}

}
