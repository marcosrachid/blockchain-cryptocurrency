package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerFinder;
import com.custom.blockchain.node.network.peer.component.PeerListener;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.request.BlockchainRequest;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NetworkManager {

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
			PEERS_STATUS.put(p, false);
			this.peerSender.connect(p);
			this.peerSender.send(BlockchainRequest.createBuilder()
					.withSignature(blockchainProperties.getNetworkSignature()).withService(Service.PING).build());
			this.peerSender.close();
		}
		;
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 60000)
	public synchronized void getState() {
		for (Peer p : getConnectedPeers()) {
			this.peerSender.connect(p);
			this.peerSender.send(BlockchainRequest.createBuilder()
					.withSignature(blockchainProperties.getNetworkSignature()).withService(Service.GET_STATE).build());
			this.peerSender.close();
		}
	}

	/**
	 * 
	 */
	@Scheduled(fixedRate = 1000)
	public synchronized void getBlocks() {
		Iterator<Peer> peers = getConnectedPeers().iterator();
		while (peers.hasNext() && BLOCKS_QUEUE.size() > 0) {
			this.peerSender.connect(peers.next());
			this.peerSender
					.send(BlockchainRequest.createBuilder().withSignature(blockchainProperties.getNetworkSignature())
							.withService(Service.GET_BLOCK).withArguments(BLOCKS_QUEUE.peek()).build());
			this.peerSender.close();
		}
	}

	/**
	 * 
	 * @return
	 */
	private Set<Peer> getConnectedPeers() {
		return PEERS_STATUS.entrySet().stream().filter(entry -> entry.getValue().equals(true)).map(e -> e.getKey())
				.collect(Collectors.toSet());
	}

	/**
	 * 
	 * @return
	 */
	private int getConnectedPeersNumber() {
		return getConnectedPeers().size();
	}

}
