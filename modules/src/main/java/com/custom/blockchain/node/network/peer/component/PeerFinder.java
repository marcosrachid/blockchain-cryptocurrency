package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.util.ConnectionUtil;

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

	private PeerSender peerSender;

	private PeersDB peersDB;

	public PeerFinder(final BlockchainProperties blockchainProperties, final PeerSender peerSender, PeersDB peersDB) {
		this.blockchainProperties = blockchainProperties;
		this.peerSender = peerSender;
		this.peersDB = peersDB;
	}

	/**
	 * 
	 */
	public void findPeers() {
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

	/**
	 * 
	 */
	private void findFromFile() {
		DBIterator iterator = peersDB.iterator();
		while (iterator.hasNext()) {
			Peer peer = peersDB.next(iterator);
			if (!PEERS.contains(peer))
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
			LOG.debug("[Crypto] Trying to send a service[" + Service.GET_PEERS.getService() + "] request to peer[" + p
					+ "]");
			if (this.peerSender.connect(p)) {
				this.peerSender.send(BlockchainRequest.createBuilder().withService(Service.GET_PEERS).build());
				this.peerSender.close();
			}
		}
	}

	/**
	 * 
	 */
	private void findMockedPeers() {
		try {
			for (Peer p : blockchainProperties.getNetworkMockedPeersMapped()) {
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
	private void addPeer(Peer peer) {
		InetAddress hostname;
		try {
			hostname = InetAddress.getByName(peer.getIp());
			if (!hostname.equals(InetAddress.getByName(INVALID_LOCALHOST))
					&& !hostname.equals(InetAddress.getByName(INVALID_LOCALHOST_IP))
					&& !hostname.equals(InetAddress.getLocalHost())) {
				peer.setCreateDatetime(LocalDateTime.now());
				PEERS.add(peer);
			}
		} catch (UnknownHostException e) {
			throw new NetworkException("Could read peer " + peer.getIp() + " as an InetAddress");
		}
	}

}
