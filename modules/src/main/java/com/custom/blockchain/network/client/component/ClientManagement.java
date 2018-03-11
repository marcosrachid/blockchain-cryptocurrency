package com.custom.blockchain.network.client.component;

import static com.custom.blockchain.costants.SystemConstants.MAXIMUM_SEEDS;
import static com.custom.blockchain.properties.BlockchainImutableProperties.PEERS;
import static com.custom.blockchain.properties.BlockchainImutableProperties.PEERS_STATUS;

import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.custom.blockchain.network.client.Client;
import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerIterator;
import com.custom.blockchain.network.peer.component.PeerFinder;

@Component
public class ClientManagement {

	private static Thread thread;

	private PeerFinder peerFinder;

	public ClientManagement(final PeerFinder peerFinder) {
		this.peerFinder = peerFinder;
	}

	@Scheduled(fixedRate = 300000)
	public void searchActions() {
		if (getConnectedPeersNumber() >= MAXIMUM_SEEDS) {
			return;
		}

		Runnable runnable = () -> {
			this.peerFinder.findPeers();

			PeerIterator iterator = PEERS.iterator();
			while (iterator.hasNext() && getConnectedPeersNumber() < MAXIMUM_SEEDS) {
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
