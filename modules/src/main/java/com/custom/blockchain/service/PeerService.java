package com.custom.blockchain.service;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS;
import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.exception.PeerException;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class PeerService {

	private BlockchainProperties blockchainProperties;

	private ObjectMapper objectMapper;

	public PeerService(final BlockchainProperties blockchainProperties, final ObjectMapper objectMapper) {
		this.blockchainProperties = blockchainProperties;
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 * @param ip
	 * @param serverPort
	 * @throws PeerException
	 */
	public void addPeer(String ip, int serverPort) throws PeerException {
		Peer peer = new Peer(ip, serverPort);
		if (PEERS.contains(peer) && PEERS_STATUS.containsKey(peer) && PEERS_STATUS.get(peer)) {
			throw new PeerException(String.format("Peer %s is already registered on peer's list", peer));
		}
		if (PEERS.contains(peer) && !PEERS_STATUS.containsKey(peer)) {
			throw new PeerException(String.format("Peer %s is on queue to try a connection", peer));
		}
		PEERS.add(peer);
		PEERS_STATUS.remove(peer);
		try {
			FileUtil.addPeer(blockchainProperties.getCoinName(), this.objectMapper.writeValueAsString(PEERS));
		} catch (IOException e) {
			throw new PeerException("It was not possible deserialize peer request");
		}
	}

}
