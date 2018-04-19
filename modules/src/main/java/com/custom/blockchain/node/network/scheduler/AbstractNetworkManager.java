package com.custom.blockchain.node.network.scheduler;

import static com.custom.blockchain.node.NodeStateManagement.SERVER_THREAD;
import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.node.component.PeerFinder;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.util.ConnectionUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
public abstract class AbstractNetworkManager {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkManager.class);

	protected ObjectMapper objectMapper;

	protected BlockchainProperties blockchainProperties;

	protected CurrentPropertiesChainstateDB currentPropertiesBlockDB;

	protected PeerFinder peerFinder;

	protected Server peerListener;

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void searchPeers() {
		LOG.debug("[Crypto] Executing search for new peers...");
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())) {
			return;
		}
		this.peerFinder.findPeers();
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 5000)
	public synchronized void startServer() {
		SocketThread.activate();
		if (!ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())
				&& (SERVER_THREAD == null || !SERVER_THREAD.isAlive())) {
			LOG.debug("[Crypto] Starting socket listener...");
			this.peerListener.listen();
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 5000)
	public synchronized void getState() {
		LOG.debug("[Crypto] Getting state from connected peers...");
		LOG.debug("[Crypto] peers: " + ConnectionUtil.getConnectedPeers());
		for (SocketThread socketThread : SOCKET_THREADS.values()) {
			socketThread.send(BlockchainRequest.createBuilder().withService(Service.GET_STATE).build());
		}
	}

}
