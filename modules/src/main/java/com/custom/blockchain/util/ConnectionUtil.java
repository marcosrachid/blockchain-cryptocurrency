package com.custom.blockchain.util;

import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;

import java.util.Set;

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
		Set<Peer> peers = SOCKET_THREADS.keySet();
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
