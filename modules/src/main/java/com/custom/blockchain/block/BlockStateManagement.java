package com.custom.blockchain.block;

import org.springframework.stereotype.Component;

import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.data.chainstate.GenesisBlockChainstateDB;
import com.custom.blockchain.data.chainstate.PreviewBlockChainstateDB;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockStateManagement {

	private GenesisBlockChainstateDB genesisBlockChainstateDB;

	private PreviewBlockChainstateDB previewBlockChainstateDB;

	private CurrentBlockChainstateDB currentBlockChainstateDB;

	public BlockStateManagement(final GenesisBlockChainstateDB genesisBlockChainstateDB,
			final PreviewBlockChainstateDB previewBlockChainstateDB,
			final CurrentBlockChainstateDB currentBlockChainstateDB) {
		this.genesisBlockChainstateDB = genesisBlockChainstateDB;
		this.previewBlockChainstateDB = previewBlockChainstateDB;
		this.currentBlockChainstateDB = currentBlockChainstateDB;
	}

	/**
	 * 
	 * @param block
	 */
	public void saveGenesis(Block block) {
		genesisBlockChainstateDB.put(block);
		currentBlockChainstateDB.put(block);
	}

	/**
	 * 
	 */
	public void foundBlock() {
		Block previewBlock = currentBlockChainstateDB.get();
		previewBlockChainstateDB.put(previewBlock);
		currentBlockChainstateDB.put(BlockFactory.getBlock(previewBlock));
	}

}
