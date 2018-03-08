package com.custom.blockchain.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server extends DatagramSocket {

	private static Server instance = null;

	private Server() throws SocketException {
		super();
	}

	public static void sendMessage(String message) {
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.submit(() -> {
			try {
				send(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	private static void send(String fullMessage) throws IOException, InterruptedException {
		if (instance == null) {
			instance = new Server();
		}
		InetAddress addr = InetAddress.getByName("224.0.0.3"); // TODO: remove
																// mocked,
																// search on
																// SEED DNS
																// SERVER
		DatagramPacket msgPacket = new DatagramPacket(fullMessage.getBytes(), fullMessage.getBytes().length, addr,
				8888);
		instance.send(msgPacket);
		Thread.sleep(500);
	}

}
