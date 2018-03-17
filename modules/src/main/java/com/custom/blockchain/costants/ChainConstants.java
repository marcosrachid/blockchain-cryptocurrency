package com.custom.blockchain.costants;

import java.util.HashMap;
import java.util.Map;

import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerCollection;

/**
 * 
 * @author marcosrachid
 *
 */
public class ChainConstants {

	public static final Long BLK_DAT_MAX_FILE_SIZE = 134217728L;

	public static final String GENESIS_TX_ID = "0";

	public static final String GENESIS_PREVIOUS_HASH = "0";

	public static final PeerCollection PEERS = new PeerCollection();

	public static final Map<Peer, Boolean> PEERS_STATUS = new HashMap<>();

}
