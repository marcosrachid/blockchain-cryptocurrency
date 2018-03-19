package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerFinder;
import com.custom.blockchain.node.network.peer.component.PeerListener;
import com.custom.blockchain.node.network.peer.component.PeerSender;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NetworkManager {

	private static final Logger LOG = LoggerFactory.getLogger(NetworkManager.class);

	private BlockchainProperties blockchainProperties;

	private PeerFinder peerFinder;

	private PeerListener peerListener;

	private PeerSender peerSender;

	public NetworkManager(final BlockchainProperties blockchainProperties, final PeerFinder peerFinder,
			final PeerListener peerListener, final PeerSender peerSender) {
		this.blockchainProperties = blockchainProperties;
		this.peerFinder = peerFinder;
		this.peerListener = peerListener;
		this.peerSender = peerSender;
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 300000)
	public synchronized void searchPeers() {
		if (getConnectedPeersNumber() >= blockchainProperties.getNetworkMaximumSeeds()) {
			return;
		}
		this.peerFinder.findPeers();
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void startServer() {
		if (LISTENING_THREAD == null || !LISTENING_THREAD.isAlive())
			this.peerListener.listen();
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void pingPeers() {
		for (Peer p : PEERS) {
			if (getConnectedPeersNumber() >= blockchainProperties.getNetworkMaximumSeeds()) {
				return;
			}
			LOG.debug("[Crypto] Ping peer [" + p + "]");
			PEERS_STATUS.put(p, false);
			this.peerSender.connect(p);
			this.peerSender.send(blockchainProperties.getNetworkSignature() + "#" + Service.PING.getService());
			this.peerSender.close();
		}
		;
	}

	/**
	 * 
	 * @return
	 */
	private int getConnectedPeersNumber() {
		return PEERS_STATUS.values().stream().filter(v -> v.equals(true)).collect(Collectors.toList()).size();
	}

}
