package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.block.BlockStateManagement.PREVIOUS_BLOCK;
import static com.custom.blockchain.node.NodeStateManagement.SERVICES;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.peer.exception.PeerException;
import com.custom.blockchain.service.PeerService;

@Component
public class ServiceDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceDispatcher.class);

	private PeerService peerService;

	private PeerSender peerSender;

	private Socket clientSocket;

	private Peer peer;

	public ServiceDispatcher(final PeerService peerService, final PeerSender peerSender) {
		this.peerService = peerService;
		this.peerSender = peerSender;
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
		if (arg == null)
			this.getClass().getDeclaredMethod(command).invoke(this);
		else
			this.getClass().getDeclaredMethod(command, String.class).invoke(this, arg);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void ping() {
		LOG.debug("[Crypto] Found a " + Service.PING.getService() + " event from peer [" + peer + "]");
		peerSender.connect(peer);
		peerSender.send(Service.PONG.getService());
		peerSender.close();
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
		peerSender.connect(peer);
		peerSender.send(Service.GET_STATE_RESPONSE.getService(), Long.toString(PREVIOUS_BLOCK.getHeight()));
		peerSender.close();
	}

	/**
	 * 
	 * @param state
	 */
	@SuppressWarnings("unused")
	private void getStateResponse(String currentBlock) {
		LOG.debug("[Crypto] Found a " + Service.GET_STATE_RESPONSE.getService() + " event");
		LOG.debug("[Crypto] peer [" + peer + "] current block [" + currentBlock + "]");
	}

	/**
	 * 
	 * @param block
	 */
	@SuppressWarnings("unused")
	private void getBlock(String height) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK.getService() + " event");
	}

	/**
	 * 
	 * @param block
	 */
	@SuppressWarnings("unused")
	private void getBlockResponse(String block) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK_RESPONSE.getService() + " event");
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getPeers() {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS.getService() + " event");
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void getPeersResponse(String peers) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS_RESPONSE.getService() + " event");
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

}
