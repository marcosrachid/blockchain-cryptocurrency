package com.custom.blockchain.resource;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.handler.BlockHandler;

/**
 * 
 * @author marcosrachid
 *
 */
@Profile("!miner")
@RestController
public class BlockResource {
	
	private BlockHandler blockHandler;
	
	public BlockResource(final BlockHandler blockHandler) {
		this.blockHandler = blockHandler;
	}

}
