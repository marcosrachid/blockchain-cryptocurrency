package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.component.ServiceDispatcher;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.util.ConnectionUtil;
import com.custom.blockchain.util.PeerUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class PeerFinder {

	private static final Logger LOG = LoggerFactory.getLogger(PeerFinder.class);

	private static final String INVALID_LOCALHOST = "localhost";

	private static final String INVALID_LOCALHOST_IP = "127.0.0.1";

	private BlockchainProperties blockchainProperties;

	private ServiceDispatcher serviceDispatcher;

	private PeersDB peersDB;

	public PeerFinder(final BlockchainProperties blockchainProperties, final ServiceDispatcher serviceDispatcher,
			PeersDB peersDB) {
		this.blockchainProperties = blockchainProperties;
		this.serviceDispatcher = serviceDispatcher;
		this.peersDB = peersDB;
	}

	/**
	 * 
	 */
	public void findPeers() {
		removeDisconnected();
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
			return;
		findFromFile();
		findFromDNS();
		findFromPeers();
		findMockedPeers();
		LOG.debug("[Crypto] Peers found: " + PEERS);
		for (Peer p : PEERS) {
			peersDB.put(p.getIp(), p);
		}
	}

	private void removeDisconnected() {
		// TODO: implement
	}

	/**
	 * 
	 */
	private void findFromFile() {
		DBIterator iterator = peersDB.iterator();
		while (iterator.hasNext()) {
			Peer peer = peersDB.next(iterator);
			connect(peer);
			addPeer(peer);
		}
	}

	/**
	 * 
	 */
	private void findFromDNS() {
		// TODO: MAYBE
	}

	/**
	 * 
	 */
	private void findFromPeers() {
		for (Peer p : ConnectionUtil.getConnectedPeers()) {
			if (!ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
				SOCKET_THREADS.get(p).send(BlockchainRequest.createBuilder().withService(Service.GET_PEERS).build());
		}
	}

	/**
	 * 
	 */
	private void findMockedPeers() {
		try {
			for (Peer p : blockchainProperties.getNetworkMockedPeersMapped()) {
				connect(p);
				addPeer(p);
			}
		} catch (IOException e) {
			throw new NetworkException("Could not read peers data: " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param peer
	 */
	private void connect(Peer peer) {
		Socket client = null;
		if (!SOCKET_THREADS.containsKey(peer)
				&& !ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())) {
			client = PeerUtil.connect(peer);
		}
		if (client != null) {
			SocketThread socketThread = new SocketThread(blockchainProperties, serviceDispatcher, client);
			socketThread.start();
			SOCKET_THREADS.put(peer, socketThread);
		}
	}

	/**
	 * 
	 * @param peer
	 */
	private void addPeer(Peer peer) {
		InetAddress hostname;
		try {
			hostname = InetAddress.getByName(peer.getIp());
			if (!hostname.equals(InetAddress.getByName(INVALID_LOCALHOST))
					&& !hostname.equals(InetAddress.getByName(INVALID_LOCALHOST_IP))
					&& !hostname.equals(InetAddress.getLocalHost())) {
				PEERS.add(peer);
			}
		} catch (UnknownHostException e) {
			throw new NetworkException("Could read peer " + peer.getIp() + " as an InetAddress");
		}
	}

}
