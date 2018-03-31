package com.custom.blockchain.node.network.component;

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
		for (Peer p : ConnectionUtil.getConnectedPeers()) {
			this.peerSender.connect(p);
			this.peerSender
					.send(BlockchainRequest.createBuilder().withSignature(blockchainProperties.getNetworkSignature())
							.withService(Service.GET_TRANSACTIONS).build());
			this.peerSender.close();
		}
	}

}
