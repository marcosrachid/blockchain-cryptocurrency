package com.custom.blockchain.node.network.server.dispatcher;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.SERVICES;
import static com.custom.blockchain.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.peer.PeerStateManagement.REMOVED_PEERS;

import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.node.network.server.request.arguments.BlockArguments;
import com.custom.blockchain.node.network.server.request.arguments.BlockResponseArguments;
import com.custom.blockchain.node.network.server.request.arguments.InvalidBlockArguments;
import com.custom.blockchain.node.network.server.request.arguments.PeerResponseArguments;
import com.custom.blockchain.node.network.server.request.arguments.StateResponseArguments;
import com.custom.blockchain.node.network.server.request.arguments.TransactionsResponseArguments;
import com.custom.blockchain.peer.Peer;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.ConnectionUtil;
import com.custom.blockchain.util.PeerUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class ServiceDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceDispatcher.class);

	private BlockchainProperties blockchainProperties;

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private MempoolDB mempoolDB;

	private BlockStateManagement blockStateManagement;

	public ServiceDispatcher(final BlockchainProperties blockchainProperties, final BlockDB blockDB,
			final CurrentBlockDB currentBlockDB, final MempoolDB mempoolDB,
			final BlockStateManagement blockStateManagement) {
		this.blockchainProperties = blockchainProperties;
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.mempoolDB = mempoolDB;
		this.blockStateManagement = blockStateManagement;
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param request
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void launch(final OutputStream sender, Peer peer, BlockchainRequest request) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		LOG.trace("[Crypto] Service: " + request.getService() + ", Arguments: " + request.getArguments());
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())
				&& !ConnectionUtil.getConnectedPeers().contains(peer)) {
			LOG.trace("[Crypto] Connections peer pool is full");
			return;
		}
		if (!SERVICES.stream().map(s -> s.getService()).collect(Collectors.toList())
				.contains(request.getService().getService())) {
			LOG.trace("[Crypto] Node is not responsible for this service[" + request.getService().getService() + "]");
			return;
		}
		if (request.hasArguments()) {
			LOG.trace("[Crypto] Request with arguments: " + request.getService().getService() + " - "
					+ request.getArguments());
			this.getClass().getDeclaredMethod(request.getService().getService(), OutputStream.class, Peer.class,
					request.getArguments().getClass()).invoke(this, sender, peer, request.getArguments());
		} else {
			LOG.trace("[Crypto] Request without arguments: " + request.getService().getService());
			this.getClass().getDeclaredMethod(request.getService().getService(), OutputStream.class, Peer.class)
					.invoke(this, sender, peer);
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void ping(OutputStream sender, Peer peer) {
		LOG.trace("[Crypto] Found a " + Service.PING.getService() + " event from peer [" + peer + "]");
		PeerUtil.send(blockchainProperties, sender,
				BlockchainRequest.createBuilder().withService(Service.PONG).build());
		Optional<Peer> foundPeer = PEERS.stream().filter(p -> p.equals(peer)).findFirst();
		if (foundPeer.isPresent()) {
			Peer p = foundPeer.get();
			p.setLastConnected(LocalDateTime.now());
			PEERS.add(p);
		} else {
			peer.setCreateDatetime(LocalDateTime.now());
			peer.setLastConnected(LocalDateTime.now());
			PEERS.add(peer);
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void pong(OutputStream sender, Peer peer) {
		LOG.trace("[Crypto] Found a " + Service.PONG.getService() + " event from peer [" + peer + "]");
		Optional<Peer> foundPeer = PEERS.stream().filter(p -> p.equals(peer)).findFirst();
		if (foundPeer.isPresent()) {
			Peer p = foundPeer.get();
			p.setLastConnected(LocalDateTime.now());
			PEERS.add(p);
		} else {
			peer.setCreateDatetime(LocalDateTime.now());
			peer.setLastConnected(LocalDateTime.now());
			PEERS.add(peer);
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getState(OutputStream sender, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_STATE.getService() + " event from peer [" + peer + "]");
		PeerUtil.send(blockchainProperties, sender,
				BlockchainRequest.createBuilder().withService(Service.GET_STATE_RESPONSE)
						.withArguments(new StateResponseArguments(currentBlockDB.get().getHeight())).build());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getStateResponse(OutputStream sender, Peer peer, StateResponseArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_STATE_RESPONSE.getService() + " event from peer [" + peer + "]");
		Long peerCurrentBlock = args.getCurrentBlock();
		LOG.debug("[Crypto] peer [" + peer + "] current block [" + peerCurrentBlock + "]");
		Long currentMappedHeight = (currentBlockDB.get().getHeight() + BLOCKS_QUEUE.size());
		if (peerCurrentBlock > currentMappedHeight) {
			LOG.info("[Crypto] Found new block from peer [" + peer + "], requesting block...");
			long blockNumber = peerCurrentBlock - currentBlockDB.get().getHeight();
			for (long i = (currentMappedHeight + 1); i <= peerCurrentBlock; i++) {
				BLOCKS_QUEUE.add(new BlockArguments(i));
			}
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlock(OutputStream sender, Peer peer, BlockArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK.getService() + " event from peer [" + peer + "]");
		Block block = blockDB.get(args.getHeight());
		LOG.debug("[Crypto] Found block[" + block + "] to be sent");
		PeerUtil.send(blockchainProperties, sender, BlockchainRequest.createBuilder()
				.withService(Service.GET_BLOCK_RESPONSE).withArguments(new BlockResponseArguments(block)).build());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlockResponse(OutputStream sender, Peer peer, BlockResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK_RESPONSE.getService() + " event from peer [" + peer + "]");
		Block block = args.getBlock();
		try {
			blockStateManagement.validateBlock(block);
			blockStateManagement.foundBlock(block);
			BLOCKS_QUEUE.poll();
		} catch (BusinessException e) {
			LOG.error("Block[" + block + "] was identified as invalid: " + e.getMessage(), e);
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getInvalidBlock(OutputStream sender, Peer peer, InvalidBlockArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_INVALID_BLOCK.getService() + " event from peer [" + peer + "]");
		Block block = blockDB.get(args.getHeight());
		blockStateManagement.removeBlock(block);
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getPeers(OutputStream sender, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS.getService() + " event from peer [" + peer + "]");
		PeerUtil.send(blockchainProperties, sender, BlockchainRequest.createBuilder()
				.withService(Service.GET_PEERS_RESPONSE).withArguments(new PeerResponseArguments(ConnectionUtil
						.getConnectedPeers().stream().filter(p -> !p.equals(peer)).collect(Collectors.toSet())))
				.build());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getPeersResponse(OutputStream sender, Peer peer, PeerResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS_RESPONSE.getService() + " event from peer [" + peer + "]");
		Set<Peer> requestPeers = args.getPeers();
		requestPeers.removeAll(REMOVED_PEERS);
		requestPeers.removeAll(PEERS);
		requestPeers.forEach(p -> p.setCreateDatetime(LocalDateTime.now()));
		PEERS.addAll(requestPeers);
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getTransactions(OutputStream sender, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS.getService() + " event from peer [" + peer + "]");
		Set<SimpleTransaction> mempoolTransactions = new HashSet<>();
		DBIterator iterator = mempoolDB.iterator();
		while (iterator.hasNext()) {
			mempoolTransactions.add(mempoolDB.next(iterator));
		}
		PeerUtil.send(blockchainProperties, sender,
				BlockchainRequest.createBuilder().withService(Service.GET_TRANSACTIONS_RESPONSE)
						.withArguments(new TransactionsResponseArguments(mempoolTransactions)).build());

	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getTransactionsResponse(OutputStream sender, Peer peer, TransactionsResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS_RESPONSE.getService() + " event from peer [" + peer
				+ "]");
		for (SimpleTransaction t : args.getTransactions()) {
			mempoolDB.put(t.getTransactionId(), t);
		}
	}

}
