package com.custom.blockchain.node.network.server;

import static com.custom.blockchain.node.NodeStateManagement.LISTENING_THREAD;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.component.ServiceDispatcher;
import com.custom.blockchain.node.network.exception.NetworkException;

@Component
public class Server {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	private BlockchainProperties blockchainProperties;

	private ServiceDispatcher serviceDispatcher;

	private ServerSocket server;

	private boolean isRunning;

	public Server(final BlockchainProperties blockchainProperties, final ServiceDispatcher serviceDispatcher) {
		this.blockchainProperties = blockchainProperties;
		this.serviceDispatcher = serviceDispatcher;
	}

	/**
	 * 
	 */
	public void listen() {
		isRunning = true;
		LISTENING_THREAD = new Thread(new Runnable() {
			public void run() {
				try {
					start();
				} catch (IOException e) {
					isRunning = false;
					LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
					throw new NetworkException("[Crypto] Server error: " + e.getMessage());
				} finally {
					try {
						if (!server.isClosed())
							server.close();
					} catch (IOException e) {
						LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
						throw new NetworkException("[Crypto] Server error: " + e.getMessage());
					}
				}
			}
		});

		LISTENING_THREAD.start();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void start() throws IOException {
		LOG.info("[Crypto] Server Starting");
		server = new ServerSocket(blockchainProperties.getNetworkPort());
		LOG.info("[Crypto] Server Started port: " + blockchainProperties.getNetworkPort());

		while (isRunning) {
			Socket client = server.accept();
			new SocketThread(blockchainProperties, serviceDispatcher, client).start();
		}
	}

	/**
	 * 
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

}
