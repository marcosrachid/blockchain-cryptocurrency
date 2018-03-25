package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.databind.JavaType;
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

	private PeerSender peerSender;

	public PeerFinder(final BlockchainProperties blockchainProperties, final ObjectMapper objectMapper,
			final PeerSender peerSender) {
		this.blockchainProperties = blockchainProperties;
		this.objectMapper = objectMapper;
		this.peerSender = peerSender;
	}

	/**
	 * 
	 */
	public void findPeers() {
		if (isPeerConnectionsFull())
			return;
		findFromFile();
		findFromDNS();
		findFromPeers();
		findMockedPeers();
	}

	/**
	 * 
	 */
	private void findFromFile() {
		List<Peer> filePeers;
		try {
			JavaType collectionPeerClass = objectMapper.getTypeFactory().constructCollectionType(List.class,
					Peer.class);
			filePeers = objectMapper.readValue(FileUtil.readPeer(blockchainProperties.getCoinName()),
					collectionPeerClass);
			for (Peer peer : new ArrayList<Peer>(filePeers)) {
				if (PEERS.contains(peer))
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
		// TODO: MAYBE
	}

	/**
	 * 
	 */
	private void findFromPeers() {
		for (Peer p : getConnectedPeers()) {
			this.peerSender.connect(p);
			this.peerSender.send(BlockchainRequest.createBuilder()
					.withSignature(blockchainProperties.getNetworkSignature()).withService(Service.GET_PEERS).build());
			this.peerSender.close();
		}
	}

	/**
	 * 
	 */
	private void findMockedPeers() {
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

	/**
	 * 
	 * @return
	 */
	private Set<Peer> getConnectedPeers() {
		return PEERS_STATUS.entrySet().stream().filter(entry -> entry.getValue().equals(true)).map(e -> e.getKey())
				.collect(Collectors.toSet());
	}

}
