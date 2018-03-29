package com.custom.blockchain.node.network.peer;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PeerStateManagement {

	public static final Set<Peer> PEERS = new HashSet<>();

	public static final Set<Peer> REMOVED_PEERS = new HashSet<>();

	public static final Map<Peer, LocalDateTime> PEERS_STATUS = new HashMap<>();
}
