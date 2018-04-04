package com.custom.blockchain.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
	public Socket connect(Peer peer) {
		try {
			return new Socket(peer.getIp(), peer.getServerPort());
		} catch (IOException e) {
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
