package com.custom.blockchain.block;

import java.io.IOException;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.blockindex.CurrentFileBlockIndexDB;
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

	private ObjectMapper objectMapper;

	private CurrentBlockChainstateDB currentBlockChainstateDB;

	private CurrentFileBlockIndexDB currentFileBlockIndexDB;

	private Block nextBlock;

	public BlockStateManagement(final BlockchainProperties blockchainProperties, final ObjectMapper objectMapper,
			final CurrentBlockChainstateDB currentBlockChainstateDB, CurrentFileBlockIndexDB currentFileBlockIndexDB) {
		this.blockchainProperties = blockchainProperties;
		this.objectMapper = objectMapper;
		this.currentBlockChainstateDB = currentBlockChainstateDB;
		this.currentFileBlockIndexDB = currentFileBlockIndexDB;
	}

	/**
	 * 
	 * @param block
	 * @throws BlockException
	 */
	public void foundBlock(Block block) throws BlockException {
		try {
			currentBlockChainstateDB.put(block);
			nextBlock = BlockFactory.getBlock(block);
			Long fileNumber = currentFileBlockIndexDB.get();
			String jsonBlockList = addBlockToCurrentFile(block, fileNumber);
			if (FileUtil.isCurrentFileFull(blockchainProperties.getCoinName(), jsonBlockList, fileNumber)) {
				fileNumber++;
				currentFileBlockIndexDB.put(fileNumber);
				jsonBlockList = addBlockToCurrentFile(block, fileNumber);
			}
			FileUtil.addBlock(blockchainProperties.getCoinName(), jsonBlockList, fileNumber);
		} catch (IOException e) {
			throw new BlockException("Could not register new Block");
		}
	}

	/**
	 * 
	 * @param jsonBlock
	 * @throws BlockException
	 */
	public void foundBlock(String jsonBlock) throws BlockException {
		try {
			Block block = objectMapper.readValue(jsonBlock, Block.class);
			foundBlock(block);
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
	 * @param fileNumber
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	private String addBlockToCurrentFile(Block block, Long fileNumber) throws IOException {
		JavaType collectionBlockClass = objectMapper.getTypeFactory().constructCollectionType(Set.class, Block.class);
		Set<Block> blockList = objectMapper.readValue(
				FileUtil.readBlocksFile(blockchainProperties.getCoinName(), fileNumber), collectionBlockClass);
		blockList.add(block);
		return objectMapper.writeValueAsString(blockList);
	}

}
