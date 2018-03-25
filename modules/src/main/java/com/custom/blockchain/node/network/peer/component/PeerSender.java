package com.custom.blockchain.node.network.peer.component;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;

@Component
public class PeerSender {

	private Socket socket;

	public PeerSender() {
	}

	/**
	 * 
	 * @param peer
	 */
	public void connect(Peer peer) {
		try {
			socket = new Socket(peer.getIp(), peer.getServerPort());

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param data
	 */
	public void send(BlockchainRequest blockchainRequest) {
		try {
			OutputStream outputStream = socket.getOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(outputStream);
			oos.writeObject(blockchainRequest);
			oos.flush();

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
