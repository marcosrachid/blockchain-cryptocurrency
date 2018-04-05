package com.custom.blockchain.node.network.server;

import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.server.dispatcher.ServiceDispatcher;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.peer.Peer;
import com.custom.blockchain.util.PeerUtil;

/**
 * 
 * @author marcosrachid
 *
 */
public class SocketThread extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(SocketThread.class);

	private static boolean ACTIVE = true;

	private BlockchainProperties blockchainProperties;

	private ServiceDispatcher serviceDispatcher;

	private Socket client;

	private Peer peer;

	private boolean isRunning;

	public SocketThread(BlockchainProperties blockchainProperties, ServiceDispatcher serviceDispatcher, Socket client) {
		this.blockchainProperties = blockchainProperties;
		this.serviceDispatcher = serviceDispatcher;
		this.client = client;
		isRunning = true;
	}

	@Override
	public void run() {
		LOG.trace("[Crypto] Connection Received from: " + client.toString());

		try {
			while (ACTIVE && isRunning) {
				BlockchainRequest request = PeerUtil.receive(client.getInputStream());
				LOG.trace("[Crypto] Request: " + request);

				if (request == null) {
					close();
					break;
				}

				registerThread(request);

				if (!request.getSignature().equals(blockchainProperties.getNetworkSignature())) {
					LOG.error("[Crypto] Received an invalid signature from peer [" + peer + "]");
				} else {
					serviceDispatcher.launch(client.getOutputStream(), peer, request);
				}
			}
		} catch (IOException | IllegalArgumentException | SecurityException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
		} finally {
			try {
				if (!client.isClosed())
					client.close();
			} catch (IOException e) {
				LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
			}
		}
	}

	/**
	 * 
	 * @param request
	 */
	public void send(BlockchainRequest request) {
		LOG.debug("[Crypto] Trying to send a service[" + request.getService().getService() + "] request with arguments["
				+ request.getArguments() + "] to client[" + client.toString() + "]");
		try {
			PeerUtil.send(blockchainProperties, client.getOutputStream(), request);
		} catch (IOException e) {
			LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
		}
	}

	public static void inactivate() {
		ACTIVE = false;
	}

	/**
	 * 
	 * @param request
	 */
	private void registerThread(BlockchainRequest request) {
		if (peer == null) {
			peer = new Peer(client.getInetAddress().getHostAddress(), request.getResponsePort());
			SOCKET_THREADS.put(peer, this);
		}
	}

	/**
	 * 
	 */
	private void close() {
		LOG.info("[Crypto] Stoping socket thread...");
		isRunning = false;
		if (peer != null)
			SOCKET_THREADS.remove(peer);
	}

}