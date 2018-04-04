package com.custom.blockchain.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;

/**
 * 
 * @author marcosrachid
 *
 */
public final class PeerUtil {

	private static final Logger LOG = LoggerFactory.getLogger(PeerUtil.class);

	public PeerUtil() {
	}

	/**
	 * 
	 * @param peer
	 */
	public static Socket connect(Peer peer) {
		long duration = System.currentTimeMillis();
		try {
			Socket socket = new Socket();
			socket.connect(new InetSocketAddress(peer.getIp(), peer.getServerPort()), 1000);
			return socket;
		} catch (Exception e) {
			LOG.debug("[Crypto] Connect attempt duration measured in ms: " + (System.currentTimeMillis() - duration));
			LOG.debug("[Crypto] Connect failed reason: " + e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * @param blockchainProperties
	 * @param sender
	 * @param blockchainRequest
	 */
	public static void send(BlockchainProperties blockchainProperties, ObjectOutputStream sender,
			BlockchainRequest blockchainRequest) {
		LOG.trace("[Crypto] Sending request[" + blockchainRequest + "]");
		blockchainRequest.setSignature(blockchainProperties.getNetworkSignature());
		blockchainRequest.setResponsePort(blockchainProperties.getNetworkPort());
		try {
			sender.writeObject(blockchainRequest);
			sender.flush();
		} catch (IOException e) {
			LOG.trace("[Crypto] Send failed reason: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param inputStream
	 * @return
	 */
	public static BlockchainRequest receive(InputStream inputStream) {
		try {
			ObjectInputStream ois = new ObjectInputStream(inputStream);
			return (BlockchainRequest) ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			return null;
		}
	}

	/**
	 * 
	 * @param blockchainRequest
	 * @param outputStream
	 * @return
	 */
	public static boolean send(BlockchainRequest blockchainRequest, OutputStream outputStream) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(blockchainRequest);
			oos.flush();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * 
	 * @param socket
	 * @return
	 */
	public static boolean close(Socket socket) {
		try {
			socket.close();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

}
