package com.custom.blockchain.costants;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerCollection;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;

/**
 * 
 * @author marcosrachid
 *
 */
public class ChainConstants {

	public static Set<Block> BLOCKCHAIN = null;

	public static Map<String, TransactionOutput> UTXOS = new HashMap<>();

	public static Set<Transaction> TRANSACTION_MEMPOOL = null;

	public static final PeerCollection PEERS = new PeerCollection();

	public static final Map<Peer, Boolean> PEERS_STATUS = new HashMap<>();

}
