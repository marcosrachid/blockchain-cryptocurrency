package com.custom.blockchain.node.network.peer.component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import org.springframework.stereotype.Component;

import com.custom.blockchain.node.network.peer.Peer;

@Component
public class PeerSender {

	private Socket socket;
	private DataInputStream inputStreamClient;
	private DataOutputStream outputStreamClient;

	public PeerSender() {
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
