package com.custom.blockchain.node.network.scheduler;

import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.node.component.PeerFinder;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
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
			final CurrentPropertiesChainstateDB currentPropertiesBlockDB, final PeerFinder peerFinder,
			final Server peerListener) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.peerFinder = peerFinder;
		this.peerListener = peerListener;
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void getTransactions() {
		LOG.debug("[Crypto] Getting transactions request from connected peers...");
		for (SocketThread socketThread : SOCKET_THREADS.values()) {
			socketThread.send(BlockchainRequest.createBuilder().withService(Service.GET_TRANSACTIONS).build());
		}
	}

}
