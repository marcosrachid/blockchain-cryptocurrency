package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.LISTENING;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.component.Peer2Peer;
import com.custom.blockchain.node.network.peer.component.PeerFinder;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NetworkManager {

	private static Thread thread;

	private BlockchainProperties blockchainProperties;

	private PeerFinder peerFinder;

	private Peer2Peer peer2peer;

	public NetworkManager(final BlockchainProperties blockchainProperties, final PeerFinder peerFinder,
			final Peer2Peer peer2peer) {
		this.blockchainProperties = blockchainProperties;
		this.peerFinder = peerFinder;
		this.peer2peer = peer2peer;
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 300000)
	public void searchPeers() {
		if (getConnectedPeersNumber() >= blockchainProperties.getNetworkMaximumSeeds()) {
			return;
		}

		Runnable runnable = () -> {
			this.peerFinder.findPeers();
		};

		thread = new Thread(runnable);
		thread.start();
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public void startServer() {
		if (!LISTENING)
			this.peer2peer.listen();
	}

	/**
	 * 
	 * @return
	 */
	private int getConnectedPeersNumber() {
		return PEERS_STATUS.values().stream().filter(v -> v.equals(true)).collect(Collectors.toList()).size();
	}

}
