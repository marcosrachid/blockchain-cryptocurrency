package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.SERVICES;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.REMOVED_PEERS;

import java.io.IOException;
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
import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.node.network.request.arguments.BlockArguments;
import com.custom.blockchain.node.network.request.arguments.BlockResponseArguments;
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

	private BlockchainProperties blockchainProperties;

	private PeerSender peerSender;

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockChainstateDB;

	private MempoolDB mempoolDB;

	private BlockStateManagement blockStateManagement;

	private Socket clientSocket;

	private Peer peer;

	public ServiceDispatcher(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final BlockDB blockDB, final CurrentBlockDB currentBlockChainstateDB, final MempoolDB mempoolDB,
			final BlockStateManagement blockStateManagement, final PeerSender peerSender) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.blockDB = blockDB;
		this.currentBlockChainstateDB = currentBlockChainstateDB;
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
		this.clientSocket = clientSocket;
		this.peer = peer;
		if (!SERVICES.stream().map(s -> s.getService()).collect(Collectors.toList())
				.contains(request.getService().getService()))
			return;
		if (request.hasArguments()) {
			LOG.trace("[Crypto] Request with arguments: " + request.getService().getService() + " - "
					+ request.getArguments());
			this.getClass().getDeclaredMethod(request.getService().getService(), request.getArguments().getClass())
					.invoke(this, request.getArguments());
		} else {
			LOG.trace("[Crypto] Request without arguments: " + request.getService().getService());
			this.getClass().getDeclaredMethod(request.getService().getService()).invoke(this);
		}
	}

	/**
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void ping() {
		LOG.trace("[Crypto] Found a " + Service.PING.getService() + " event from peer [" + peer + "]");
		simpleSend(BlockchainRequest.createBuilder().withService(Service.PONG).build());
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
		PEERS_STATUS.put(peer, LocalDateTime.now());
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void pong() {
		LOG.trace("[Crypto] Found a " + Service.PONG.getService() + " event");
		LOG.trace(String.format("[Crypto] node [%s] successfully answered", clientSocket.toString()));
		simpleSend(BlockchainRequest.createBuilder().withService(Service.PING).build());
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
		PEERS_STATUS.put(peer, LocalDateTime.now());
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getState() {
		LOG.trace("[Crypto] Found a " + Service.GET_STATE.getService() + " event");
		simpleSend(BlockchainRequest.createBuilder().withService(Service.GET_STATE_RESPONSE)
				.withArguments(new StateResponseArguments(currentBlockChainstateDB.get().getHeight())).build());
	}

	/**
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getStateResponse(StateResponseArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_STATE_RESPONSE.getService() + " event");
		Long peerCurrentBlock = args.getCurrentBlock();
		LOG.debug("[Crypto] peer [" + peer + "] current block [" + peerCurrentBlock + "]");
		Long currentMappedHeight = (currentBlockChainstateDB.get().getHeight() + BLOCKS_QUEUE.size());
		if (peerCurrentBlock > currentMappedHeight) {
			LOG.info("[Crypto] Found new block from peer [" + peer + "], requesting block...");
			long blockNumber = peerCurrentBlock - currentBlockChainstateDB.get().getHeight();
			for (long i = (currentMappedHeight + 1); i <= peerCurrentBlock; i++) {
				BLOCKS_QUEUE.add(new BlockArguments(i));
			}
		}
	}

	/**
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlock(BlockArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_BLOCK.getService() + " event");
		simpleSend(BlockchainRequest.createBuilder().withService(Service.GET_BLOCK_RESPONSE)
				.withArguments(new BlockResponseArguments(blockDB.get(args.getHeight()))).build());
	}

	/**
	 * 
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlockResponse(BlockResponseArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_BLOCK_RESPONSE.getService() + " event");
		Block block = args.getBlock();
		try {
			blockStateManagement.validateBlock(block);
			blockStateManagement.foundBlock(block);
			BLOCKS_QUEUE.poll();
		} catch (BlockException e) {
			throw new NetworkException("Block[" + block + "] was identified as invalid: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getPeers() {
		LOG.trace("[Crypto] Found a " + Service.GET_PEERS.getService() + " event");
		simpleSend(BlockchainRequest.createBuilder().withService(Service.GET_PEERS_RESPONSE)
				.withArguments(new PeerResponseArguments(ConnectionUtil.getConnectedPeers())).build());
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getPeersResponse(PeerResponseArguments peers) {
		LOG.trace("[Crypto] Found a " + Service.GET_PEERS_RESPONSE.getService() + " event");
		JavaType collectionPeerClass = objectMapper.getTypeFactory().constructCollectionType(Set.class, Peer.class);
		Set<Peer> requestPeers = peers.getPeers();
		requestPeers.removeAll(REMOVED_PEERS);
		requestPeers.removeAll(PEERS);
		requestPeers.forEach(p -> p.setCreateDatetime(LocalDateTime.now()));
		PEERS.addAll(requestPeers);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getTransactions() {
		LOG.trace("[Crypto] Found a " + Service.GET_TRANSACTIONS.getService() + " event");
		Set<SimpleTransaction> mempoolTransactions = new HashSet<>();
		DBIterator iterator = mempoolDB.iterator();
		while (iterator.hasNext()) {
			mempoolTransactions.add(mempoolDB.next(iterator));
		}
		simpleSend(BlockchainRequest.createBuilder().withService(Service.GET_TRANSACTIONS_RESPONSE)
				.withArguments(new TransactionsResponseArguments(mempoolTransactions)).build());

	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getTransactionsResponse(TransactionsResponseArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_TRANSACTIONS_RESPONSE.getService() + " event");
		for (SimpleTransaction t : args.getTransactions()) {
			mempoolDB.put(t.getTransactionId(), t);
		}
	}

	/**
	 * 
	 * @param request
	 */
	private void simpleSend(BlockchainRequest request) {
		request.setSignature(blockchainProperties.getNetworkSignature());
		peerSender.connect(peer);
		peerSender.send(request);
		peerSender.close();
	}

}
