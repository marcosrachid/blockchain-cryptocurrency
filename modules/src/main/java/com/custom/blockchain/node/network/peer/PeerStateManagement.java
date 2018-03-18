package com.custom.blockchain.node.network.peer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PeerStateManagement {

	public static final Set<Peer> PEERS = new HashSet<>();

	public static final Map<Peer, Boolean> PEERS_STATUS = new HashMap<>();
}
