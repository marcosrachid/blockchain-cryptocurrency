package com.custom.blockchain.node.network.server;

import static com.custom.blockchain.node.NodeStateManagement.SERVER_THREAD;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.node.network.server.dispatcher.ServiceDispatcher;
import com.custom.blockchain.util.ConnectionUtil;

@Component
public class Server {

	private static final Logger LOG = LoggerFactory.getLogger(Server.class);

	private BlockchainProperties blockchainProperties;

	private CurrentPropertiesChainstateDB currentPropertiesBlockDB;

	private ServiceDispatcher serviceDispatcher;

	private ServerSocket server;

	private boolean isRunning;

	public Server(final BlockchainProperties blockchainProperties,
			final CurrentPropertiesChainstateDB currentPropertiesBlockDB, final ServiceDispatcher serviceDispatcher) {
		this.blockchainProperties = blockchainProperties;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.serviceDispatcher = serviceDispatcher;
	}

	/**
	 * 
	 */
	public void listen() {
		isRunning = true;
		SERVER_THREAD = new Thread(new Runnable() {
			public void run() {
				try {
					start();
				} catch (IOException e) {
					isRunning = false;
					LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
				} finally {
					try {
						if (!server.isClosed())
							server.close();
					} catch (IOException e) {
						LOG.error("[Crypto] Client error : {}", e.getMessage(), e);
					}
				}
			}
		});

		SERVER_THREAD.start();
	}

	/**
	 * 
	 * @throws IOException
	 */
	private void start() throws IOException {
		LOG.info("[Crypto] Server Starting");
		server = new ServerSocket(blockchainProperties.getNetworkPort());
		LOG.info("[Crypto] Server Started port: " + blockchainProperties.getNetworkPort());

		while (!ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()) && isRunning) {
			Socket client = server.accept();
			new SocketThread(blockchainProperties, currentPropertiesBlockDB, serviceDispatcher, client).start();
		}
	}

	/**
	 * 
	 */
	public void stop() {
		LOG.info("[Crypto] Stoping listener...");
		isRunning = false;
	}

}
