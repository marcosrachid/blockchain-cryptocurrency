package com.custom.blockchain.network.client.component;

import static com.custom.blockchain.costants.SystemConstants.MAXIMUM_SEEDS;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.custom.blockchain.network.client.Client;
import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerCollection;
import com.custom.blockchain.network.peer.PeerIterator;
import com.custom.blockchain.network.peer.component.PeerFinder;

@Component
public class ClientManagement {

	public static final PeerCollection peers = new PeerCollection();
	public static final Map<Peer, Boolean> peersStatus = new HashMap<>();

	private static Thread thread;
	
	private PeerFinder peerFinder;
	
	public ClientManagement(final PeerFinder peerFinder) {
		this.peerFinder = peerFinder;
	}

	public void searchActions() {
		if (peersStatus.size() >= MAXIMUM_SEEDS)
			return;

		Runnable runnable = () -> {
			this.peerFinder.findPeers(peers);

			PeerIterator iterator = peers.iterator();
			while (iterator.hasNext() && peersStatus.size() < MAXIMUM_SEEDS) {
				Peer peer = iterator.next();
				if (!peersStatus.containsKey(peer)) {
					Client client = new Client(peer);
					Thread thread = new Thread(client);
					thread.start();
					peersStatus.put(peer, true);
				}
			}
			end();
			searchActions();
		};

		thread = new Thread(runnable);
		thread.start();
	}

	public void end() {
		thread.interrupt();
	}

}
