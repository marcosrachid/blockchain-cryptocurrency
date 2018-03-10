package com.custom.blockchain.network.client;

import static com.custom.blockchain.costants.SystemConstants.MAXIMUM_SEEDS;

import java.util.HashMap;
import java.util.Map;

import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerCollection;
import com.custom.blockchain.network.peer.PeerFinder;
import com.custom.blockchain.network.peer.PeerIterator;

public class ClientManagement {

	public static final PeerCollection peers = new PeerCollection();
	public static final Map<Peer, Boolean> peersStatus = new HashMap<>();

	private static Thread thread;

	public static void searchActions() {
		if (peersStatus.size() >= MAXIMUM_SEEDS)
			return;

		Runnable runnable = () -> {
			PeerFinder.findPeers(peers);

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

	public static void end() {
		thread.interrupt();
	}

}
