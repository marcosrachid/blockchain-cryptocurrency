package com.custom.blockchain.node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.request.arguments.BlockArguments;

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

}
