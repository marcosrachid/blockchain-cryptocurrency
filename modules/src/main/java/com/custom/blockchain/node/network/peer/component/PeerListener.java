package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.costants.ChainConstants.REQUEST_SEPARATOR;
import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;

import java.io.DataInputStream;
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

@Component
public class PeerListener {

	private static final Logger LOG = LoggerFactory.getLogger(PeerListener.class);

	private BlockchainProperties blockchainProperties;

	private ServiceDispatcher serviceDispatcher;

	// Listener variables
	private ServerSocket server;
	private Thread peerThread;
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
		peerThread = new Thread(new Runnable() {
			public void run() {
				try {
					start();
				} catch (IOException | NoSuchMethodException | SecurityException | IllegalAccessException
						| IllegalArgumentException | InvocationTargetException e) {
					LOG.error("[Crypto] Could not start server: " + e.getMessage());
					if (!server.isClosed()) {
						try {
							server.close();
						} catch (IOException e1) {
						}
					}
				}
			}
		});

		LISTENING_THREAD = peerThread;
		peerThread.start();
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
		String[] service;

		Socket clientSocket;

		DataInputStream input;

		while (isRunning) {
			clientSocket = server.accept();
			input = new DataInputStream(clientSocket.getInputStream());

			LOG.info("[Crypto] Connection Received from: " + clientSocket.toString());

			String request = input.readUTF();
			LOG.trace("[Crypto] Raw Request: " + request);
			service = request.split(REQUEST_SEPARATOR);
			Peer newPeer = new Peer(clientSocket.getInetAddress().getHostAddress(), clientSocket.getLocalPort());

			if (service.length < 2 || !service[0].equals(blockchainProperties.getNetworkSignature())) {
				LOG.error("[Crypto] Received an invalid signature from peer [" + newPeer + "]");
				input.close();
				continue;
			}

			if (service.length == 3)
				serviceDispatcher.launch(clientSocket, newPeer, service[1], service[2]);
			else
				serviceDispatcher.launch(clientSocket, newPeer, service[1]);

			input.close();
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
