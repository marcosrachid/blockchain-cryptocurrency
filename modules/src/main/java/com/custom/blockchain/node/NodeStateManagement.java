package com.custom.blockchain.node;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.arguments.BlockArguments;
import com.custom.blockchain.node.network.server.SocketThread;

/**
 * 
 * @author marcosrachid
 *
 */
public class NodeStateManagement {

	public static final Integer DIFFICULTY_ADJUSTMENT_BLOCK = 1000;

	public static final Set<Service> SERVICES = new HashSet<>();

	public static final Queue<BlockArguments> BLOCKS_QUEUE = new LinkedList<>();

	public static Thread LISTENING_THREAD = null;

	public static Thread MINING_THREAD = null;

	public static final Map<Peer, SocketThread> SOCKET_THREADS = new HashMap<>();

}
