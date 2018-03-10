package com.custom.blockchain.network.peer;

import com.custom.blockchain.network.exception.NetworkException;

public class PeerFinder {

	/**
	 * 
	 * @param peers
	 */
	public static void findPeers(final PeerCollection peers) {
		Peer peer = new Peer("224.0.0.3", 8888);
		peers.addPeer(peer);
		if (peers.isEmpty()) {
			throw new NetworkException("No peers available on network");
		}
	}

}
