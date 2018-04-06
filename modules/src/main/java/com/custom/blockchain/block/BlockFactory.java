package com.custom.blockchain.block;

import java.math.BigDecimal;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockFactory {

	/**
	 * 
	 * @return
	 */
	public static PropertiesBlock getStartPropertiesBlock(BigDecimal minimunTransaction, BigDecimal coinLimit,
			BigDecimal miningTimeRate, BigDecimal reward, Long blockSize, String coinbase) {
		return new PropertiesBlock(minimunTransaction, coinLimit, miningTimeRate, reward, blockSize, coinbase);
	}

	/**
	 * 
	 * @param previousBlock
	 * @return
	 */
	public static PropertiesBlock getPropertiesBlock(BigDecimal minimunTransaction, BigDecimal coinLimit,
			BigDecimal miningTimeRate, BigDecimal reward, Long blockSize, String coinbase,
			AbstractBlock previousBlock) {
		return new PropertiesBlock(minimunTransaction, coinLimit, miningTimeRate, reward, blockSize, coinbase,
				previousBlock);
	}

	/**
	 * 
	 * @param previousBlock
	 * @return
	 */
	public static TransactionsBlock getBlock(AbstractBlock previousBlock, PropertiesBlock propertiesBlock) {
		return new TransactionsBlock(previousBlock, propertiesBlock);
	}

}
