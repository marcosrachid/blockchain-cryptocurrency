package com.custom.blockchain.network.client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {

	private static Thread thread;

	public static void start() {
		Runnable runnable = () -> {
			try (MulticastSocket clientSocket = new MulticastSocket(8888)) {
				InetAddress address = InetAddress.getByName("224.0.0.3");
				byte[] buf = new byte[256];
				clientSocket.joinGroup(address);
				while (true) {
					DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
					clientSocket.receive(msgPacket);
					String msg = new String(buf, 0, buf.length);
					// TODO: case for each message type
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		};

		thread = new Thread(runnable);
		thread.start();
	}

	public static void end() {
		thread.interrupt();
	}

}
