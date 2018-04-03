package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.SERVICES;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.REMOVED_PEERS;

import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
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
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.node.network.request.arguments.BlockArguments;
import com.custom.blockchain.node.network.request.arguments.BlockResponseArguments;
import com.custom.blockchain.node.network.request.arguments.InvalidBlockArguments;
import com.custom.blockchain.node.network.request.arguments.PeerResponseArguments;
import com.custom.blockchain.node.network.request.arguments.StateResponseArguments;
import com.custom.blockchain.node.network.request.arguments.TransactionsResponseArguments;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.ConnectionUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class ServiceDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceDispatcher.class);

	private ObjectMapper objectMapper;

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private MempoolDB mempoolDB;

	private BlockStateManagement blockStateManagement;

	private PeerSender peerSender;

	public ServiceDispatcher(final ObjectMapper objectMapper, final BlockDB blockDB,
			final CurrentBlockDB currentBlockDB, final MempoolDB mempoolDB,
			final BlockStateManagement blockStateManagement, final PeerSender peerSender) {
		this.objectMapper = objectMapper;
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.mempoolDB = mempoolDB;
		this.blockStateManagement = blockStateManagement;
		this.peerSender = peerSender;
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param request
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void launch(Socket clientSocket, Peer peer, BlockchainRequest request) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		LOG.trace("[Crypto] Service: " + request.getService() + ", Arguments: " + request.getArguments());
		if (!SERVICES.stream().map(s -> s.getService()).collect(Collectors.toList())
				.contains(request.getService().getService()))
			return;
		if (request.hasArguments()) {
			LOG.trace("[Crypto] Request with arguments: " + request.getService().getService() + " - "
					+ request.getArguments());
			this.getClass().getDeclaredMethod(request.getService().getService(), Socket.class, Peer.class,
					request.getArguments().getClass()).invoke(this, clientSocket, peer, request.getArguments());
		} else {
			LOG.trace("[Crypto] Request without arguments: " + request.getService().getService());
			this.getClass().getDeclaredMethod(request.getService().getService(), Socket.class, Peer.class).invoke(this,
					clientSocket, peer);
		}
		PEERS_STATUS.put(peer, LocalDateTime.now());
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void ping(Socket clientSocket, Peer peer) {
		LOG.trace("[Crypto] Found a " + Service.PING.getService() + " event from peer [" + peer + "]");
		simpleSend(peer, BlockchainRequest.createBuilder().withService(Service.PONG).build());
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
	 * @param clientSocket
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void pong(Socket clientSocket, Peer peer) {
		LOG.trace("[Crypto] Found a " + Service.PONG.getService() + " event");
		LOG.trace("[Crypto] node [" + clientSocket.toString() + "] successfully answered");
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
	 * @param clientSocket
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getState(Socket clientSocket, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_STATE.getService() + " event");
		simpleSend(peer, BlockchainRequest.createBuilder().withService(Service.GET_STATE_RESPONSE)
				.withArguments(new StateResponseArguments(currentBlockDB.get().getHeight())).build());
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getStateResponse(Socket clientSocket, Peer peer, StateResponseArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_STATE_RESPONSE.getService() + " event");
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
	 * @param clientSocket
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlock(Socket clientSocket, Peer peer, BlockArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK.getService() + " event");
		Block block = blockDB.get(args.getHeight());
		LOG.debug("[Crypto] Found block[" + block + "] to be sent");
		simpleSend(peer, BlockchainRequest.createBuilder().withService(Service.GET_BLOCK_RESPONSE)
				.withArguments(new BlockResponseArguments(block)).build());
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlockResponse(Socket clientSocket, Peer peer, BlockResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK_RESPONSE.getService() + " event");
		Block block = args.getBlock();
		try {
			blockStateManagement.validateBlock(block);
			blockStateManagement.foundBlock(block);
			BLOCKS_QUEUE.poll();
		} catch (BusinessException e) {
			LOG.error("Block[" + block + "] was identified as invalid: " + e.getMessage());
			throw new NetworkException("Block[" + block + "] was identified as invalid: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getInvalidBlock(Socket clientSocket, Peer peer, InvalidBlockArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_INVALID_BLOCK.getService() + " event");
		Block block = blockDB.get(args.getHeight());
		blockStateManagement.removeBlock(block);
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getPeers(Socket clientSocket, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS.getService() + " event");
		simpleSend(peer,
				BlockchainRequest.createBuilder().withService(Service.GET_PEERS_RESPONSE)
						.withArguments(new PeerResponseArguments(ConnectionUtil.getConnectedPeers().stream()
								.filter(p -> !p.equals(peer)).collect(Collectors.toSet())))
						.build());
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param peers
	 */
	@SuppressWarnings("unused")
	private void getPeersResponse(Socket clientSocket, Peer peer, PeerResponseArguments peers) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS_RESPONSE.getService() + " event");
		JavaType collectionPeerClass = objectMapper.getTypeFactory().constructCollectionType(Set.class, Peer.class);
		Set<Peer> requestPeers = peers.getPeers();
		requestPeers.removeAll(REMOVED_PEERS);
		requestPeers.removeAll(PEERS);
		requestPeers.forEach(p -> p.setCreateDatetime(LocalDateTime.now()));
		PEERS.addAll(requestPeers);
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getTransactions(Socket clientSocket, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS.getService() + " event");
		Set<SimpleTransaction> mempoolTransactions = new HashSet<>();
		DBIterator iterator = mempoolDB.iterator();
		while (iterator.hasNext()) {
			mempoolTransactions.add(mempoolDB.next(iterator));
		}
		simpleSend(peer, BlockchainRequest.createBuilder().withService(Service.GET_TRANSACTIONS_RESPONSE)
				.withArguments(new TransactionsResponseArguments(mempoolTransactions)).build());

	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getTransactionsResponse(Socket clientSocket, Peer peer, TransactionsResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS_RESPONSE.getService() + " event");
		for (SimpleTransaction t : args.getTransactions()) {
			mempoolDB.put(t.getTransactionId(), t);
		}
	}

	/**
	 * 
	 * @param peer
	 * @param request
	 */
	private void simpleSend(Peer peer, BlockchainRequest request) {
		LOG.debug("[Crypto] Trying to send a service[" + request.getService().getService() + "] request to peer[" + peer
				+ "]");
		if (peerSender.connect(peer)) {
			peerSender.send(request);
			peerSender.close();
		}
	}

}
