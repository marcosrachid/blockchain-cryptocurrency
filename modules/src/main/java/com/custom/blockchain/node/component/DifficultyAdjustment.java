package com.custom.blockchain.node.component;

import static com.custom.blockchain.node.NodeStateManagement.DIFFICULTY_ADJUSTMENT_BLOCK;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.util.BlockUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class DifficultyAdjustment {

	private static final Logger LOG = LoggerFactory.getLogger(DifficultyAdjustment.class);

	private static final int MIN_DIFFICULTY = 0;

	private static final int MAX_DIFFICULTY = 32;

	private static final BigDecimal MARGIN_OF_ERROR = new BigDecimal(0.2);

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private CurrentPropertiesBlockDB currentPropertiesBlockDB;

	public DifficultyAdjustment(final BlockDB blockDB, final CurrentBlockDB currentBlockDB,
			final CurrentPropertiesBlockDB currentPropertiesBlockDB) {
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
	}

	/**
	 * 
	 * @return
	 */
	public Integer adjust() {
		LOG.info("[Crypto] Difficulty adjustment every " + DIFFICULTY_ADJUSTMENT_BLOCK + " blocks starting...");
		AbstractBlock currentBlock = currentBlockDB.get();
		TransactionsBlock currentTransactionBlock = BlockUtil.getLastTransactionBlock(blockDB, currentBlock);
		Integer difficulty = currentTransactionBlock.getRewardTransaction().getDifficulty();
		List<Long> timestamps = new ArrayList<>();
		List<Long> differences = new ArrayList<>();
		mapTimestamps(DIFFICULTY_ADJUSTMENT_BLOCK - 1, currentBlock, timestamps);
		for (int i = 0; i < timestamps.size(); i++) {
			if (i % 2 != 0)
				differences.add(timestamps.get(i) - timestamps.get(i - 1));
		}
		BigDecimal average = new BigDecimal(differences.stream().mapToDouble(t -> t).average().getAsDouble(),
				MathContext.DECIMAL64);
		BigDecimal miningTimeRate = currentPropertiesBlockDB.get().getMiningTimeRate();
		BigDecimal top = miningTimeRate.multiply(BigDecimal.ONE.add(MARGIN_OF_ERROR));
		BigDecimal bottom = miningTimeRate.multiply(BigDecimal.ONE.subtract(MARGIN_OF_ERROR));
		if (average.compareTo(top) > 0 && difficulty.compareTo(MIN_DIFFICULTY) > 0)
			return (difficulty - 1);
		if (average.compareTo(bottom) < 0 && difficulty.compareTo(MAX_DIFFICULTY) < 0)
			return (difficulty + 1);
		return difficulty;

	}

	/**
	 * 
	 * @param adjustmentLoop
	 * @param block
	 * @param timestamps
	 */
	private void mapTimestamps(Integer adjustmentLoop, AbstractBlock block, final List<Long> timestamps) {
		if (adjustmentLoop == 0)
			return;
		timestamps.add(block.getTimeStamp());
		adjustmentLoop--;
		mapTimestamps(adjustmentLoop, blockDB.get(block.getHeight() - 1), timestamps);
	}

}
