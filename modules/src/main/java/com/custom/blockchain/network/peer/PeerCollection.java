package com.custom.blockchain.network.peer;

import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author marcosrachid
 *
 */
public class PeerCollection {

	private static Set<Peer> peers = new HashSet<>();

	public void addPeer(Peer peer) {
		peers.add(peer);
	}

	public void removePeer(Peer peer) {
		peers.remove(peer);
	}

	public boolean isEmpty() {
		return peers.isEmpty();
	}
	
	public boolean contains(Peer peer) {
		return peers.contains(peer);
	}
	
	public Set<Peer> getList() {
		return peers;
	}

	public PeerIterator iterator() {
		return new PeerIterator(peers);
	}

}
