package com.custom.blockchain.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.custom.blockchain.network.peer.Peer;

public class Server extends DatagramSocket {

	private static Server instance = null;

	private Server() throws SocketException {
		super();
	}

	public static void sendMessage(Peer peer, String message) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			try {
				send(peer, message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static void send(Peer peer, String fullMessage) throws IOException, InterruptedException {
		if (instance == null) {
			instance = new Server();
		}
		InetAddress addr = InetAddress.getByName(peer.getIp());
		DatagramPacket msgPacket = new DatagramPacket(fullMessage.getBytes(), fullMessage.getBytes().length, addr,
				peer.getServerPort());
		instance.send(msgPacket);
		Thread.sleep(500);
	}

}
