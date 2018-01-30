package com.custom.blockchain.handler;

import org.springframework.stereotype.Component;

import com.custom.blockchain.service.BlockService;

@Component
public class BlockHandler {

	private BlockService blockService;

	public BlockHandler(final BlockService blockService) {
		this.blockService = blockService;
	}
}
