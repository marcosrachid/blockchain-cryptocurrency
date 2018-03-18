package com.custom.blockchain.node.network.client.component;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.client.Client;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerFinder;

@Component
public class ClientManager {

	private static Thread thread;

	private BlockchainProperties blockchainProperties;

	private PeerFinder peerFinder;

	public ClientManager(final BlockchainProperties blockchainProperties, final PeerFinder peerFinder) {
		this.blockchainProperties = blockchainProperties;
		this.peerFinder = peerFinder;
	}

	@Scheduled(fixedRate = 300000)
	public void searchActions() {
		if (getConnectedPeersNumber() >= blockchainProperties.getNetworkMaximumSeeds()) {
			return;
		}

		Runnable runnable = () -> {
			this.peerFinder.findPeers();

			Iterator<Peer> iterator = PEERS.iterator();
			while (iterator.hasNext() && getConnectedPeersNumber() < blockchainProperties.getNetworkMaximumSeeds()) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
				}
				Peer peer = iterator.next();
				if (!PEERS_STATUS.containsKey(peer)) {
					Client client = new Client(peer);
					Thread thread = new Thread(client);
					thread.start();
				}
			}
		};

		thread = new Thread(runnable);
		thread.start();
	}

	private int getConnectedPeersNumber() {
		return PEERS_STATUS.values().stream().filter(v -> v.equals(true)).collect(Collectors.toList()).size();
	}

}
