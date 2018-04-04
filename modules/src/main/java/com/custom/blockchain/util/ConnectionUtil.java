package com.custom.blockchain.util;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.node.network.peer.Peer;

/**
 * 
 * @author marcosrachid
 *
 */
public final class ConnectionUtil {

	private static final Logger LOG = LoggerFactory.getLogger(ConnectionUtil.class);

	/**
	 * 
	 * @return
	 */
	public static Set<Peer> getConnectedPeers() {
		Set<Peer> peers = PEERS_STATUS.entrySet().stream()
				.filter(entry -> entry.getValue().isAfter(LocalDateTime.now().minusMinutes(1))).map(e -> e.getKey())
				.collect(Collectors.toSet());
		LOG.debug("[Crypto] Peers connected: " + peers);
		return peers;
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
