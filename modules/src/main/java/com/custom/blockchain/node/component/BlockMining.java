package com.custom.blockchain.node.component;

import static com.custom.blockchain.node.NodeStateManagement.MINING_THREAD;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.service.BlockService;

@Profile("miner")
@Component
public class BlockMining {

	private static final Logger LOG = LoggerFactory.getLogger(BlockMining.class);

	private BlockService blockService;

	public BlockMining(final BlockService blockService) {
		this.blockService = blockService;
	}

	@Scheduled(fixedRate = 5000)
	public void mine() {
		if (MINING_THREAD == null || !MINING_THREAD.isAlive())
			run();
	}

	private void run() {
		MINING_THREAD = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					blockService.mineBlock();
				} catch (BusinessException e) {
					LOG.error("[Crypto] Could not mine: " + e.getMessage());
				}
			}

		});

		MINING_THREAD.start();
	}

}
