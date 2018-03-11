package com.custom.blockchain.costants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

	public static final List<Block> BLOCKCHAIN = new ArrayList<Block>();

	public static final Map<String, TransactionOutput> UTXOS = new HashMap<String, TransactionOutput>();

	public static final LinkedList<Transaction> TRANSACTION_MEMPOOL = new LinkedList<Transaction>();

	public static final PeerCollection PEERS = new PeerCollection();

	public static final Map<Peer, Boolean> PEERS_STATUS = new HashMap<>();

}
