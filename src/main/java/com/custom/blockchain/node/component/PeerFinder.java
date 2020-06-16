package com.custom.blockchain.node.component;

import static com.custom.blockchain.node.NodeStateManagement.SOCKET_THREADS;
import static com.custom.blockchain.peer.PeerStateManagement.PEERS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.dispatcher.Service;
import com.custom.blockchain.node.network.server.dispatcher.ServiceDispatcher;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.peer.Peer;
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

	private CurrentPropertiesChainstateDB currentPropertiesBlockDB;

	private ServiceDispatcher serviceDispatcher;

	private PeersDB peersDB;

	private List<Peer> hasPeer = new ArrayList<>();

	public PeerFinder(final BlockchainProperties blockchainProperties,
			final CurrentPropertiesChainstateDB currentPropertiesBlockDB, final ServiceDispatcher serviceDispatcher,
			final PeersDB peersDB) {
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.blockchainProperties = blockchainProperties;
		this.serviceDispatcher = serviceDispatcher;
		this.peersDB = peersDB;
	}

	/**
	 * 
	 */
	public void findPeers() {
		findFromFile();
		findFromDNS();
		findFromPeers();
		findMockedPeers();
		LOG.debug("[Crypto] Peers found: " + PEERS);
		for (Peer p : PEERS) {
			if (!hasPeer.contains(p))
				p.setCreateDatetime(LocalDateTime.now());
			peersDB.put(p.getIp(), p);
		}
	}

	/**
	 * 
	 */
	private void findFromFile() {
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
			return;
		hasPeer.clear();
		DBIterator iterator = peersDB.iterator();
		while (iterator.hasNext()) {
			Peer peer = peersDB.next(iterator);
			hasPeer.add(peer);
			connect(peer);
			addPeer(peer);
		}
	}

	/**
	 * 
	 */
	private void findFromDNS() {
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
			return;
		// TODO: MAYBE
	}

	/**
	 * 
	 */
	private void findFromPeers() {
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
			return;
		for (Peer p : ConnectionUtil.getConnectedPeers()) {
			if (!ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
				SOCKET_THREADS.get(p).send(BlockchainRequest.createBuilder().withService(Service.GET_PEERS).build());
		}
	}

	/**
	 * 
	 */
	private void findMockedPeers() {
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
			return;
		try {
			for (Peer p : blockchainProperties.getNetworkMockedPeersMapped()) {
				connect(p);
				addPeer(p);
			}
		} catch (IOException e) {
			LOG.error("Could not read peers data: " + e.getMessage(), e);
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
			SocketThread socketThread = new SocketThread(blockchainProperties, currentPropertiesBlockDB,
					serviceDispatcher, client);
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
			LOG.error("Could read peer " + peer.getIp() + " as an InetAddress", e);
		}
	}

}
