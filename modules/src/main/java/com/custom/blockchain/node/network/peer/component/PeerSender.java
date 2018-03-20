package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.costants.ChainConstants.REQUEST_PARAM_SEPARATOR;
import static com.custom.blockchain.costants.ChainConstants.REQUEST_SEPARATOR;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.Peer;

@Component
public class PeerSender {

	private BlockchainProperties blockchainProperties;

	private Socket socket;
	private DataInputStream inputStreamClient;
	private DataOutputStream outputStreamClient;

	public PeerSender(final BlockchainProperties blockchainProperties) {
		this.blockchainProperties = blockchainProperties;
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
	public void send(String service) {
		try {
			outputStreamClient.writeUTF(blockchainProperties.getNetworkSignature() + REQUEST_SEPARATOR + service);
			outputStreamClient.flush();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param data
	 */
	public void send(String service, String... args) {
		try {
			outputStreamClient.writeUTF(blockchainProperties.getNetworkSignature() + REQUEST_SEPARATOR + service
					+ REQUEST_SEPARATOR + String.join(REQUEST_PARAM_SEPARATOR, args));
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
