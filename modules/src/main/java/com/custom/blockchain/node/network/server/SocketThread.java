package com.custom.blockchain.node.network.server;

import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.component.ServiceDispatcher;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.util.PeerUtil;

public class SocketThread extends Thread {

	private static final Logger LOG = LoggerFactory.getLogger(SocketThread.class);

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
			while (isRunning) {
				BlockchainRequest request = PeerUtil.receive(client.getInputStream());
				LOG.trace("[Crypto] Request: " + request);
				if (request == null)
					continue;

				registerThread(request);

				if (!request.getSignature().equals(blockchainProperties.getNetworkSignature())) {
					LOG.error("[Crypto] Received an invalid signature from peer [" + peer + "]");
				} else {
					serviceDispatcher.launch(client.getOutputStream(), peer, request);
				}
			}
		} catch (IOException | IllegalArgumentException | SecurityException | IllegalAccessException
				| InvocationTargetException | NoSuchMethodException e) {
			try {
				if (!client.isClosed())
					client.close();
			} catch (IOException ex) {
				LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
				throw new NetworkException("[Crypto] Client error: " + e.getMessage());
			}
		} finally {
			try {
				if (!client.isClosed())
					client.close();
			} catch (IOException e) {
				LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
				throw new NetworkException("[Crypto] Client error: " + e.getMessage());
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
			throw new NetworkException("[Crypto] Client error: " + e.getMessage());
		}
	}

	/**
	 * 
	 */
	public void interrupt() {
		LOG.info("[Crypto] Stoping socket thread...");
		isRunning = false;
		if (client != null && !client.isClosed()) {
			try {
				client.close();
			} catch (IOException e1) {
				LOG.error("[Crypto] Could not close thread...");
			}
		}
		if (peer != null)
			SOCKET_THREADS.remove(peer);
	}

	private void registerThread(BlockchainRequest request) {
		if (peer == null) {
			peer = new Peer(client.getInetAddress().getHostAddress(), request.getResponsePort());
			SOCKET_THREADS.put(peer, this);
		}
	}
}