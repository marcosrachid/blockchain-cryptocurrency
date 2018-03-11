package com.custom.blockchain.properties;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerCollection;
import com.custom.blockchain.transaction.Transaction;

/**
 * 
 * @author marcosrachid
 *
 */
public class BlockchainImutableProperties {

	public static final Long BLK_DAT_MAX_FILE_SIZE = 134217728L;

	public static final LinkedList<Transaction> TRANSACTION_MEMPOOL = new LinkedList<Transaction>();
	
	public static final PeerCollection PEERS = new PeerCollection();
	
	public static final Map<Peer, Boolean> PEERS_STATUS = new HashMap<>();

	public static final String GENESIS_TX_ID = "0";

	public static final String GENESIS_PREVIOUS_HASH = "0";

}
