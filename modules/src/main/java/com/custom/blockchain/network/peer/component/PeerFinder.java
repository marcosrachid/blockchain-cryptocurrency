package com.custom.blockchain.network.peer.component;

import static com.custom.blockchain.properties.BlockchainImutableProperties.PEERS;
import static com.custom.blockchain.properties.BlockchainImutableProperties.PEERS_STATUS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.network.exception.NetworkException;
import com.custom.blockchain.network.peer.Peer;
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

	@Value("${application.name:'Rachid Coin'}")
	private String coinName;

	@Value("${application.blockchain.network.maximum-seeds}")
	private Integer maximumSeeds;

	public ObjectMapper objectMapper;

	public PeerFinder(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 */
	public void findPeers() {
		findFromFile();
		findFromDNS();
		findFromPeers();
	}

	/**
	 * 
	 */
	private void findFromFile() {
		if (isPeerConnectionsFull())
			return;
		List<Peer> filePeers;
		try {
			filePeers = objectMapper.readValue(FileUtil.readPeer(coinName), new TypeReference<List<Peer>>() {
			});
			for (Peer peer : new ArrayList<Peer>(filePeers)) {
				if (PEERS_STATUS.containsKey(peer) && !PEERS_STATUS.get(peer))
					filePeers.remove(peer);
			}
		} catch (IOException e) {
			throw new NetworkException("Could not read peers data: " + e.getMessage());
		}
		for (Peer peer : filePeers) {
			PEERS.addPeer(peer);
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
	 * @return
	 */
	private boolean isPeerConnectionsFull() {
		return PEERS_STATUS.values().stream().filter(v -> v.equals(true)).collect(Collectors.toList())
				.size() >= maximumSeeds;
	}

}
