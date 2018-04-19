package com.custom.blockchain.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.peer.Peer;

/**
 * 
 * @author marcosrachid
 *
 */
public class NodeStateManagement {

	public static final Set<Service> SERVICES = new HashSet<>();

	public static Thread SERVER_THREAD = null;

	public static Thread MINING_THREAD = null;

	public static final Map<Peer, SocketThread> SOCKET_THREADS = new HashMap<>();

}
