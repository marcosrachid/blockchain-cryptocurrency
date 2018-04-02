package com.custom.blockchain.node.network.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerFinder;
import com.custom.blockchain.node.network.peer.component.PeerListener;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.util.ConnectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Profile("miner")
@Component
public class MinerNetworkManager extends AbstractNetworkManager {

	private static final Logger LOG = LoggerFactory.getLogger(MinerNetworkManager.class);

	public MinerNetworkManager(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final PeerFinder peerFinder, final PeerListener peerListener, final PeerSender peerSender) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.peerFinder = peerFinder;
		this.peerListener = peerListener;
		this.peerSender = peerSender;
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void getTransactions() {
		LOG.debug("[Crypto] Getting transactions request from connected peers...");
		for (Peer p : ConnectionUtil.getConnectedPeers()) {
			LOG.debug("[Crypto] Trying to send a service[" + Service.GET_TRANSACTIONS.getService()
					+ "] request to peer[" + p + "]");
			if (this.peerSender.connect(p)) {
				this.peerSender.send(BlockchainRequest.createBuilder().withService(Service.GET_TRANSACTIONS).build());
				this.peerSender.close();
			}
		}
	}

}
