package com.custom.blockchain.network.peer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PeerIterator {

	private List<Peer> peers;
	private int position = 0;

	public PeerIterator(Set<Peer> peers) {
		this.peers = new ArrayList<Peer>(peers);
	}

	public boolean hasNext() {
		if (position < peers.size()) {
			return true;
		}
		return false;
	}

	public Peer next() {
		Peer peer = peers.get(position);
		position++;
		return peer;
	}

	public Peer first() {
		return peers.get(0);
	}

	public Peer last() {
		return peers.get(peers.size() - 1);
	}

}
