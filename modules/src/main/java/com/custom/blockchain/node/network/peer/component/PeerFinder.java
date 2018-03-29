package com.custom.blockchain.node.network.peer.component;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.request.BlockchainRequest;
import com.custom.blockchain.util.FileUtil;
import com.custom.blockchain.util.PeerUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class PeerFinder {

	private static final String INVALID_LOCALHOST = "localhost";

	private static final String INVALID_LOCALHOST_IP = "127.0.0.1";

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
		if (PeerUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds()))
			return;
		findFromFile();
		findFromDNS();
		findFromPeers();
		findMockedPeers();
		try {
			FileUtil.savePeer(blockchainProperties.getCoinName(), this.objectMapper.writeValueAsString(PEERS));
		} catch (IOException e) {
			throw new NetworkException("It was not possible deserialize peer request");
		}
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
		for (Peer p : PeerUtil.getConnectedPeers()) {
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
