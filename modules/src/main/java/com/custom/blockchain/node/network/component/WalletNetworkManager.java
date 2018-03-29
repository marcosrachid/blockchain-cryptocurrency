package com.custom.blockchain.node.network.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.component.PeerFinder;
import com.custom.blockchain.node.network.peer.component.PeerListener;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Profile("!miner")
@Component
public class WalletNetworkManager extends AbstractNetworkManager {

	public WalletNetworkManager(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final PeerFinder peerFinder, final PeerListener peerListener, final PeerSender peerSender) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.peerFinder = peerFinder;
		this.peerListener = peerListener;
		this.peerSender = peerSender;
	}

}
