package com.custom.blockchain.node;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import com.custom.blockchain.node.network.Service;

/**
 * 
 * @author marcosrachid
 *
 */
public class NodeStateManagement {

	public static final Integer DIFFICULTY_ADJUSTMENT_BLOCK = 2016;

	public static final Set<Service> SERVICES = new HashSet<>();

	public static final Queue<Long> BLOCKS_QUEUE = new LinkedList<>();

	public static Integer DIFFICULTY = 5;

	public static boolean BLOCKED = false;

	public static Thread LISTENING_THREAD = null;

}
