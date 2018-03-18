package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.NodeStateManagement.LISTENING;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.component.ServiceDispatcher;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.exception.PeerException;
import com.custom.blockchain.service.PeerService;

@Component
public class Peer2Peer {

	private static final Logger LOG = LoggerFactory.getLogger(Peer2Peer.class);

	private BlockchainProperties blockchainProperties;

	private PeerService peerService;

	private ServiceDispatcher serviceDispatcher;

	private Socket socket;
	private ServerSocket server;
	private DataInputStream inputStreamClient;
	private DataOutputStream outputStreamClient;
	private Thread peerThread;

	public Peer2Peer(final BlockchainProperties blockchainProperties, final PeerService peerService,
			final ServiceDispatcher serviceDispatcher) {
		this.blockchainProperties = blockchainProperties;
		this.peerService = peerService;
		this.serviceDispatcher = serviceDispatcher;
	}

	/**
	 * 
	 * @param peer
	 */
	public void listen() {
		LISTENING = true;
		peerThread = new Thread(new Runnable() {
			public void run() {
				try {
					start();
				} catch (IOException e) {
					LOG.error("[Crypto] Could not start server: " + e.getMessage());
					LISTENING = false;
				}
			}
		});

		peerThread.start();
	}

	/**
	 * 
	 * @param peer
	 * @throws IOException
	 */
	private void start() throws IOException {
		LOG.info("[Crypto] Server Starting");
		server = new ServerSocket(blockchainProperties.getNetworkPort());
		LOG.info("[Crypto] Server Started port: " + blockchainProperties.getNetworkPort());
		String service;

		Socket clientSocket;

		DataInputStream input;
		DataOutputStream output;

		while (LISTENING) {

			clientSocket = server.accept();
			input = new DataInputStream(clientSocket.getInputStream());
			output = new DataOutputStream(clientSocket.getOutputStream());

			LOG.debug("[Crypto] Connection Received from: " + clientSocket.toString());

			service = input.readUTF();
			LOG.debug("[Crypto] " + service);
			Peer newPeer = new Peer(clientSocket.getInetAddress().getHostAddress(), clientSocket.getLocalPort());

			if (service.contains(Service.PING.getService())) {
				output.writeUTF(Service.PONG.getService());
				try {
					peerService.addPeer(newPeer);
				} catch (PeerException e) {
					throw new NetworkException(
							String.format("Could not register new peer [%s]: %s", newPeer, e.getMessage()));
				}
			} else if (service.contains(Service.PONG.getService())) {
				LOG.debug(String.format("[Crypto] node [%s] successfully answered", clientSocket.toString()));
			} else {
				output.writeUTF(serviceDispatcher.launch(service));
			}

			output.close();
			input.close();
		}
	}

	/**
	 * 
	 * @param peer
	 */
	public void stop() {
		LISTENING = false;
	}

	/**
	 * 
	 * @param peer
	 */
	public void connect(Peer peer) {
		try {
			socket = new Socket(peer.getIp(), peer.getServerPort());
			outputStreamClient = new DataOutputStream(socket.getOutputStream());
			inputStreamClient = new DataInputStream(socket.getInputStream());

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	public String receive() {
		String data = null;
		try {
			data = inputStreamClient.readUTF();
			inputStreamClient.close();
			return data;
		} catch (IOException e) {
			e.printStackTrace();
			return data;
		}
	}

	/**
	 * 
	 * @param data
	 */
	public void send(String data) {
		try {
			outputStreamClient.writeUTF(data);
			outputStreamClient.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	public void close() {
		try {
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
