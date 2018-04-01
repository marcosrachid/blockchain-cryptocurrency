package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.component.ServiceDispatcher;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.util.PeerUtil;

@Component
public class PeerListener {

	private static final Logger LOG = LoggerFactory.getLogger(PeerListener.class);

	private BlockchainProperties blockchainProperties;

	private ServiceDispatcher serviceDispatcher;

	// Listener variables
	private ServerSocket server;
	private boolean isRunning;

	public PeerListener(final BlockchainProperties blockchainProperties, final ServiceDispatcher serviceDispatcher) {
		this.blockchainProperties = blockchainProperties;
		this.serviceDispatcher = serviceDispatcher;
	}

	/**
	 * 
	 * @param peer
	 */
	public void listen() {
		isRunning = true;
		LISTENING_THREAD = new Thread(new Runnable() {
			public void run() {
				try {
					start();
				} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					LOG.error("[Crypto] Could not start server: " + e.getMessage());
					isRunning = false;
					if (!server.isClosed()) {
						try {
							server.close();
						} catch (IOException e1) {
						}
					}
				}
			}
		});

		LISTENING_THREAD.start();
	}

	/**
	 * 
	 * @param peer
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void start() throws IOException, NoSuchMethodException, SecurityException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException {
		LOG.info("[Crypto] Server Starting");
		server = new ServerSocket(blockchainProperties.getNetworkPort());
		LOG.info("[Crypto] Server Started port: " + blockchainProperties.getNetworkPort());

		Socket clientSocket;

		while (isRunning) {
			clientSocket = server.accept();
			LOG.trace("[Crypto] Connection Received from: " + clientSocket.toString());

			BlockchainRequest request = PeerUtil.receive(clientSocket.getInputStream());
			LOG.trace("[Crypto] Request: " + request);

			Peer newPeer = new Peer(clientSocket.getInetAddress().getHostAddress(), clientSocket.getLocalPort());

			if (!request.getSignature().equals(blockchainProperties.getNetworkSignature())) {
				LOG.error("[Crypto] Received an invalid signature from peer [" + newPeer + "]");
				clientSocket.close();
				continue;
			}

			serviceDispatcher.launch(clientSocket, newPeer, request);

			clientSocket.close();
		}
	}

	/**
	 * 
	 * @param peer
	 */
	public void stop() {
		isRunning = false;
	}

}
