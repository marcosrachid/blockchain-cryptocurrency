package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.component.ServiceDispatcher;
import com.custom.blockchain.node.network.exception.NetworkException;
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
				} catch (IOException e) {
					isRunning = false;
					if (!server.isClosed()) {
						try {
							server.close();
						} catch (IOException e1) {
						}
					}
					LOG.error("[Crypto] Client error : {}", e);
					throw new NetworkException("[Crypto] Server error: " + e.getMessage());
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
	private void start() throws IOException {
		LOG.info("[Crypto] Server Starting");
		server = new ServerSocket(blockchainProperties.getNetworkPort());
		LOG.info("[Crypto] Server Started port: " + blockchainProperties.getNetworkPort());

		while (isRunning) {
			Socket client = server.accept();
			new ClientThread(client).start();
		}
	}

	/**
	 * 
	 * @param peer
	 */
	public void stop() {
		LOG.info("[Crypto] Stoping listener...");
		isRunning = false;
		if (server != null && !server.isClosed()) {
			try {
				server.close();
			} catch (IOException e1) {
				LOG.error("[Crypto] Could not close server...");
			}
		}
	}

	class ClientThread extends Thread {

		Socket client;

		public ClientThread(Socket client) {
			this.client = client;
		}

		@Override
		public void run() {
			LOG.trace("[Crypto] Connection Received from: " + client.toString());

			try {
				ObjectOutputStream sender = new ObjectOutputStream(client.getOutputStream());
				BlockchainRequest request = PeerUtil.receive(client.getInputStream());
				LOG.trace("[Crypto] Request: " + request);

				Peer newPeer = new Peer(client.getInetAddress().getHostAddress(), request.getResponsePort());

				if (!request.getSignature().equals(blockchainProperties.getNetworkSignature())) {
					LOG.error("[Crypto] Received an invalid signature from peer [" + newPeer + "]");
					client.getInputStream().close();
					client.close();
				}

				serviceDispatcher.launch(sender, newPeer, request);

				sender.close();
				client.getInputStream().close();
				client.close();
			} catch (IOException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				LOG.error("[Crypto] Client error : {}", e);
				throw new NetworkException("[Crypto] Client error: " + e.getMessage());
			}
		}
	}

}
