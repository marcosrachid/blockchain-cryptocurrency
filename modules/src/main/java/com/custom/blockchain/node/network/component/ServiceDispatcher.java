package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.DIFFICULTY_ADJUSTMENT_BLOCK;
import static com.custom.blockchain.node.NodeStateManagement.SERVICES;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.peer.exception.PeerException;
import com.custom.blockchain.service.PeerService;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ServiceDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceDispatcher.class);

	private ObjectMapper objectMapper;

	private BlockchainProperties blockchainProperties;

	private PeerService peerService;

	private PeerSender peerSender;

	private CurrentBlockChainstateDB currentBlockChainstateDB;

	private BlockStateManagement blockStateManagement;

	private Socket clientSocket;

	private Peer peer;

	public ServiceDispatcher(final ObjectMapper objectMapper, final BlockchainProperties blockchainProperties,
			final PeerService peerService, final PeerSender peerSender,
			final CurrentBlockChainstateDB currentBlockChainstateDB, final BlockStateManagement blockStateManagement) {
		this.objectMapper = objectMapper;
		this.blockchainProperties = blockchainProperties;
		this.peerService = peerService;
		this.peerSender = peerSender;
		this.currentBlockChainstateDB = currentBlockChainstateDB;
		this.blockStateManagement = blockStateManagement;
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param command
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void launch(Socket clientSocket, Peer peer, String command) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LOG.debug("[Crypto] Service: " + command);
		this.clientSocket = clientSocket;
		this.peer = peer;
		if (!SERVICES.stream().map(s -> s.getService()).collect(Collectors.toList()).contains(command))
			return;
		this.getClass().getDeclaredMethod(command).invoke(this);
	}

	/**
	 * 
	 * @param clientSocket
	 * @param peer
	 * @param command
	 * @param args
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public void launch(Socket clientSocket, Peer peer, String command, String arg) throws NoSuchMethodException,
			SecurityException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		LOG.debug("[Crypto] Service: " + command + ", Arguments: " + arg);
		this.clientSocket = clientSocket;
		this.peer = peer;
		if (!SERVICES.stream().map(s -> s.getService()).collect(Collectors.toList()).contains(command))
			return;
		this.getClass().getDeclaredMethod(command, String.class).invoke(this, arg);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void ping() {
		LOG.debug("[Crypto] Found a " + Service.PING.getService() + " event from peer [" + peer + "]");
		simpleSend(Service.PONG);
		try {
			peerService.addPeer(peer);
		} catch (PeerException e) {
			throw new NetworkException(String.format("Could not register new peer [%s]: %s", peer, e.getMessage()));
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void pong() {
		LOG.debug("[Crypto] Found a " + Service.PONG.getService() + " event");
		LOG.debug(String.format("[Crypto] node [%s] successfully answered", clientSocket.toString()));
		PEERS_STATUS.put(peer, true);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getState() {
		LOG.debug("[Crypto] Found a " + Service.GET_STATE.getService() + " event");
		simpleSend(Service.GET_STATE_RESPONSE, Long.toString(currentBlockChainstateDB.get().getHeight()));
	}

	/**
	 * 
	 * @param state
	 */
	@SuppressWarnings("unused")
	private void getStateResponse(String currentBlock) {
		LOG.debug("[Crypto] Found a " + Service.GET_STATE_RESPONSE.getService() + " event");
		LOG.debug("[Crypto] peer [" + peer + "] current block [" + currentBlock + "]");
		Long currentMappedHeight = (currentBlockChainstateDB.get().getHeight() + BLOCKS_QUEUE.size());
		if (Long.valueOf(currentBlock) > currentMappedHeight) {
			LOG.info("[Crypto] Found new block from peer [" + peer + "], requesting block...");
			long blockNumber = Long.valueOf(currentBlock) - currentBlockChainstateDB.get().getHeight();
			for (long i = (currentMappedHeight + 1); i <= Long.valueOf(currentBlock); i++) {
				BLOCKS_QUEUE.add(i);
			}
		}
	}

	/**
	 * 
	 * @param block
	 */
	@SuppressWarnings("unused")
	private void getBlock(String height) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK.getService() + " event");
		// simpleSend(Service.GET_BLOCK_RESPONSE,
		// objectMapper.writeValueAsString(arg0));
	}

	/**
	 * 
	 * @param block
	 */
	@SuppressWarnings("unused")
	private void getBlockResponse(String jsonBlock) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK_RESPONSE.getService() + " event");
		try {
			Block block = objectMapper.readValue(jsonBlock, Block.class);
			if (BLOCKS_QUEUE.peek().equals(block.getHeight())) {
				blockStateManagement.foundBlock(block);
				BLOCKS_QUEUE.poll();
			}
			if (block.getHeight() % DIFFICULTY_ADJUSTMENT_BLOCK == 0) {
				// TODO: adjust difficulty
			}
		} catch (BlockException | IOException e) {
			throw new NetworkException("Block[" + jsonBlock + "] found error: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getPeers() {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS.getService() + " event");
		try {
			simpleSend(Service.GET_PEERS_RESPONSE, FileUtil.readPeer(blockchainProperties.getCoinName()));
		} catch (IOException e) {
			throw new NetworkException("Could not get peer list: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getPeersResponse(String peers) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS_RESPONSE.getService() + " event");
		JavaType collectionPeerClass = objectMapper.getTypeFactory().constructCollectionType(Set.class, Peer.class);
		try {
			Set<Peer> requestPeers = objectMapper.readValue(peers, collectionPeerClass);
			Set<Peer> nodePeers = objectMapper.readValue(FileUtil.readPeer(blockchainProperties.getCoinName()),
					collectionPeerClass);
			nodePeers.addAll(requestPeers);
			FileUtil.addPeer(blockchainProperties.getCoinName(), objectMapper.writeValueAsString(nodePeers));
		} catch (IOException e) {
			throw new NetworkException("Could not read peers data: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getTransactions() {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS.getService() + " event");
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getTransactionsResponse(String transactions) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS_RESPONSE.getService() + " event");
	}

	/**
	 * 
	 * @param service
	 */
	private void simpleSend(Service service) {
		peerSender.connect(peer);
		peerSender.send(service.getService());
		peerSender.close();
	}

	/**
	 * 
	 * @param service
	 * @param param
	 */
	private void simpleSend(Service service, String param) {
		peerSender.connect(peer);
		peerSender.send(service.getService(), param);
		peerSender.close();
	}

}
