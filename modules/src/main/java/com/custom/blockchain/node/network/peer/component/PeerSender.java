package com.custom.blockchain.node.network.peer.component;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;

@Component
public class PeerSender {

	private static final Logger LOG = LoggerFactory.getLogger(PeerSender.class);

	private BlockchainProperties blockchainProperties;

	private Socket socket;

	public PeerSender(final BlockchainProperties blockchainProperties) {
		this.blockchainProperties = blockchainProperties;
	}

	/**
	 * 
	 * @param peer
	 */
	public boolean connect(Peer peer) {
		long duration = System.currentTimeMillis();
		try {
			socket = new Socket();
			socket.connect(new InetSocketAddress(peer.getIp(), peer.getServerPort()), 1000);
			return true;
		} catch (Exception e) {
			LOG.debug("[Crypto] Connect attempt duration measured in ms: " + (System.currentTimeMillis() - duration));
			LOG.debug("[Crypto] Connect failed reason: " + e.getMessage());
			return false;
		}
	}

	/**
	 * 
	 * @param data
	 */
	public void send(BlockchainRequest blockchainRequest) {
		LOG.trace("[Crypto] Sending request[" + blockchainRequest + "] to client["
				+ socket.getInetAddress().getHostAddress() + "]");
		if (socket != null) {
			blockchainRequest.setSignature(blockchainProperties.getNetworkSignature());
			blockchainRequest.setResponsePort(blockchainProperties.getNetworkPort());
			try {
				OutputStream outputStream = socket.getOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(outputStream);
				oos.writeObject(blockchainRequest);
				oos.flush();
			} catch (IOException e) {
				LOG.trace("[Crypto] Send failed reason: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 */
	public void close() {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				LOG.trace("[Crypto] Close failed reason: " + e.getMessage());
			}
		}
	}

}
