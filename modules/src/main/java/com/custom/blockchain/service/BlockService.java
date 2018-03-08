package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainMutableProperties.DIFFICULTY;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class BlockService {

	private static final Logger LOG = LoggerFactory.getLogger(BlockService.class);

	/**
	 * 
	 * @param block
	 */
	public void mineBlock(Block block) {
		block.setMerkleRoot(TransactionUtil.getMerkleRoot(block.getTransactions()));
		String target = StringUtil.getDificultyString(DIFFICULTY);
		while (!block.getHash().substring(0, DIFFICULTY).equals(target)) {
			block.setNonce(block.getNonce() + 1);
			block.calculateHash();
		}
		// TODO: CREATE BLOCK .dat
		LOG.debug("Block Mined!!! : " + block.getHash());
	}

}
