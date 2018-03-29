package com.custom.blockchain.util;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import com.custom.blockchain.node.network.peer.Peer;

/**
 * 
 * @author marcosrachid
 *
 */
public class PeerUtil {

	/**
	 * 
	 * @return
	 */
	public static Set<Peer> getConnectedPeers() {
		return PEERS_STATUS.entrySet().stream()
				.filter(entry -> entry.getValue().isAfter(LocalDateTime.now().minusMinutes(1))).map(e -> e.getKey())
				.collect(Collectors.toSet());
	}

	/**
	 * 
	 * @return
	 */
	public static int getConnectedPeersNumber() {
		return getConnectedPeers().size();
	}

	/**
	 * 
	 * @return
	 */
	public static boolean isPeerConnectionsFull(int networkMaximumSeeds) {
		return getConnectedPeersNumber() >= networkMaximumSeeds;
	}

}
