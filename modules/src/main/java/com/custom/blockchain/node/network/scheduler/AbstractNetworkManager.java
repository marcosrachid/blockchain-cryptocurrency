package com.custom.blockchain.node.network.scheduler;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;
import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;
import static com.custom.blockchain.peer.PeerStateManagement.REMOVED_PEERS;

import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.component.PeerFinder;
import com.custom.blockchain.node.network.server.Server;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.peer.Peer;
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
		if (LISTENING_THREAD == null || !LISTENING_THREAD.isAlive()) {
			LOG.debug("[Crypto] Starting socket listener...");
			this.peerListener.listen();
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 5000)
	public synchronized void checkPeersConnection() {
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())) {
			return;
		}
		LOG.debug("[Crypto] Checking connections...");
		for (SocketThread socketThread : SOCKET_THREADS.values()) {
			socketThread.send(BlockchainRequest.createBuilder()
					.withSignature(blockchainProperties.getNetworkSignature()).withService(Service.PING).build());
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void getState() {
		LOG.debug("[Crypto] Getting state from connected peers...");
		LOG.debug("[Crypto] peers: " + ConnectionUtil.getConnectedPeers());
		for (SocketThread socketThread : SOCKET_THREADS.values()) {
			socketThread.send(BlockchainRequest.createBuilder().withService(Service.GET_STATE).build());
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 1000)
	public synchronized void getBlocks() {
		Iterator<Peer> peers = ConnectionUtil.getConnectedPeers().iterator();
		while (peers.hasNext() && BLOCKS_QUEUE.size() > 0) {
			Peer p = peers.next();
			LOG.debug("[Crypto] Trying to send a service[" + Service.GET_BLOCK.getService() + "] request to peer[" + p
					+ "]");
			for (SocketThread socketThread : SOCKET_THREADS.values()) {
				socketThread.send(
						BlockchainRequest.createBuilder().withSignature(blockchainProperties.getNetworkSignature())
								.withService(Service.GET_BLOCK).withArguments(BLOCKS_QUEUE.peek()).build());
			}
		}
	}

	/**
	 * 
	 */
	@Scheduled(cron = "0 0 * * * *")
	public synchronized void emptyRemovedPeers() {
		REMOVED_PEERS.clear();
	}

}
