package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class PeerFinder {

	private BlockchainProperties blockchainProperties;

	public ObjectMapper objectMapper;

	public PeerFinder(final BlockchainProperties blockchainProperties, final ObjectMapper objectMapper) {
		this.blockchainProperties = blockchainProperties;
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 */
	public void findPeers() {
		findFromFile();
		findFromDNS();
		findFromPeers();
		findMockedPeers();
	}

	/**
	 * 
	 */
	private void findFromFile() {
		if (isPeerConnectionsFull())
			return;
		List<Peer> filePeers;
		try {
			filePeers = objectMapper.readValue(FileUtil.readPeer(blockchainProperties.getCoinName()),
					new TypeReference<List<Peer>>() {
					});
			for (Peer peer : new ArrayList<Peer>(filePeers)) {
				if (PEERS_STATUS.containsKey(peer) && !PEERS_STATUS.get(peer))
					filePeers.remove(peer);
			}
		} catch (IOException e) {
			throw new NetworkException("Could not read peers data: " + e.getMessage());
		}
		for (Peer peer : filePeers) {
			addPeer(peer);
		}

	}

	/**
	 * 
	 */
	private void findFromDNS() {
		if (isPeerConnectionsFull())
			return;
	}

	/**
	 * 
	 */
	private void findFromPeers() {
		if (isPeerConnectionsFull())
			return;
	}

	/**
	 * 
	 */
	private void findMockedPeers() {
		if (isPeerConnectionsFull())
			return;
		for (String p : blockchainProperties.getNetworkMockedPeers()) {
			try {
				addPeer(objectMapper.readValue(p, Peer.class));
			} catch (IOException e) {
				throw new NetworkException("Could not read peers data: " + e.getMessage());
			}
		}
	}

	/**
	 * 
	 * @return
	 */
	private boolean isPeerConnectionsFull() {
		return PEERS_STATUS.values().stream().filter(v -> v.equals(true)).collect(Collectors.toList())
				.size() >= blockchainProperties.getNetworkMaximumSeeds();
	}

	/**
	 * 
	 * @param peer
	 */
	private void addPeer(Peer peer) {
		InetAddress hostname;
		try {
			hostname = InetAddress.getByName(peer.getIp());
			if (!hostname.equals(InetAddress.getLocalHost())) {
				PEERS.add(peer);
			}
		} catch (UnknownHostException e) {
			throw new NetworkException("Could read peer " + peer.getIp() + " as an InetAddress");
		}
	}

}
