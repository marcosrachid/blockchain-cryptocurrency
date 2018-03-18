package com.custom.blockchain.node.network.client;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.util.StringUtil;

public class Client implements Runnable {

	private static final Logger LOG = LoggerFactory.getLogger(Client.class);

	private Peer peer;

	public Client(Peer peer) {
		this.peer = peer;
	}

	@Override
	public void run() {
		LOG.info("[Crypto] Client for Peer {} starting connection...", peer);
		try (MulticastSocket clientSocket = new MulticastSocket(peer.getServerPort())) {
			InetAddress address = InetAddress.getByName(peer.getIp());
			byte[] buf = new byte[256];
			clientSocket.joinGroup(address);
			while (clientSocket.isConnected()) {
				PEERS_STATUS.put(peer, true);
				DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
				clientSocket.receive(msgPacket);
				String msg = new String(buf, 0, buf.length);
				if (StringUtil.isNotEmpty(msg))
					ClientDispatcher.launch(msg);
			}
			PEERS_STATUS.put(peer, false);
			LOG.info("[Crypto] Client for Peer {} ending connection...", peer);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

}
