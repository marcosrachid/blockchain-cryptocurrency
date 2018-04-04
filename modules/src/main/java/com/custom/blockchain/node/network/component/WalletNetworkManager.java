package com.custom.blockchain.node.network.component;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.component.PeerFinder;
import com.custom.blockchain.node.network.server.Server;
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
			final PeerFinder peerFinder, final Server peerListener) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.peerFinder = peerFinder;
		this.peerListener = peerListener;
	}

}
