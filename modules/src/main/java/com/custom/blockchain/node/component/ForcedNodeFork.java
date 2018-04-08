package com.custom.blockchain.node.component;

import org.springframework.stereotype.Component;

import com.custom.blockchain.block.PropertiesBlock;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class ForcedNodeFork {

	private Long height = null;

	private PropertiesBlock propertiesBlock = null;

	/**
	 * 
	 * @param height
	 * @param propertiesBlock
	 */
	public void enqueueFork(Long height, PropertiesBlock propertiesBlock) {
		this.propertiesBlock = propertiesBlock;
	}

	/**
	 * 
	 * @param height
	 * @return
	 */
	public boolean checkFork(Long height) {
		if (height == null)
			return false;
		return this.height.equals(height);
	}

	/**
	 * 
	 * @return
	 */
	public PropertiesBlock pollFork() {
		PropertiesBlock block = propertiesBlock;
		height = null;
		propertiesBlock = null;
		return block;
	}

}
