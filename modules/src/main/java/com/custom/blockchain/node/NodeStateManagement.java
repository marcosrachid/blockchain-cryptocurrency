package com.custom.blockchain.node;

import java.util.HashMap;
import java.util.Map;

import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.peer.Peer;

/**
 * 
 * @author marcosrachid
 *
 */
public class NodeStateManagement {

	public static Long BIGGEST_HEIGHT = 0L;

	public static Thread SERVER_THREAD = null;

	public static Thread MINING_THREAD = null;

	public static final Map<Peer, SocketThread> SOCKET_THREADS = new HashMap<>();

	/**
	 * 
	 * @param currentNodeHeight
	 * @return
	 */
	public static boolean isSynchronized(Long currentNodeHeight) {
		return BIGGEST_HEIGHT > 0L && currentNodeHeight >= BIGGEST_HEIGHT;
	}

	public static void updateIfBigger(Long foundHeight) {
		if (foundHeight > BIGGEST_HEIGHT)
			BIGGEST_HEIGHT = foundHeight;
	}

}
