package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.REMOVED_PEERS;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

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
public abstract class AbstractNetworkManager {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractNetworkManager.class);

	protected ObjectMapper objectMapper;

	protected BlockchainProperties blockchainProperties;

	protected PeerFinder peerFinder;

	protected PeerListener peerListener;

	protected PeerSender peerSender;

	/**
	 * 
	 */
	@Scheduled(fixedRate = 300000)
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
		if (LISTENING_THREAD == null || !LISTENING_THREAD.isAlive())
			LOG.debug("[Crypto] Starting socket listener...");
		this.peerListener.listen();
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void checkPeersConnection() {
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())) {
			return;
		}
		LOG.debug("[Crypto] Checking connections...");
		Set<Peer> disconectedPeers = new HashSet<>(PEERS);
		disconectedPeers.removeAll(ConnectionUtil.getConnectedPeers());
		for (Peer p : disconectedPeers) {
			if ((p.getLastConnected() == null && p.getCreateDatetime().isBefore(LocalDateTime.now().minusDays(1)))
					|| (p.getLastConnected() != null
							&& p.getLastConnected().isBefore(LocalDateTime.now().minusMonths(1)))) {
				REMOVED_PEERS.add(p);
				continue;
			}
			if (this.peerSender.connect(p)) {
				this.peerSender.send(BlockchainRequest.createBuilder()
						.withSignature(blockchainProperties.getNetworkSignature()).withService(Service.PING).build());
				this.peerSender.close();
			}
		}
		for (Peer r : REMOVED_PEERS) {
			PEERS.remove(r);
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void getState() {
		LOG.debug("[Crypto] Getting state from connected peers...");
		for (Peer p : ConnectionUtil.getConnectedPeers()) {
			if (this.peerSender.connect(p)) {
				this.peerSender.send(
						BlockchainRequest.createBuilder().withSignature(blockchainProperties.getNetworkSignature())
								.withService(Service.GET_STATE).build());
				this.peerSender.close();
			}
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 1000)
	public synchronized void getBlocks() {
		Iterator<Peer> peers = ConnectionUtil.getConnectedPeers().iterator();
		while (peers.hasNext() && BLOCKS_QUEUE.size() > 0) {
			if (this.peerSender.connect(peers.next())) {
				this.peerSender.send(
						BlockchainRequest.createBuilder().withSignature(blockchainProperties.getNetworkSignature())
								.withService(Service.GET_BLOCK).withArguments(BLOCKS_QUEUE.peek()).build());
				this.peerSender.close();
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
