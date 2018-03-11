package com.custom.blockchain.service;

import static com.custom.blockchain.costants.ChainConstants.PEERS;
import static com.custom.blockchain.costants.ChainConstants.PEERS_STATUS;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.exception.PeerException;
import com.custom.blockchain.util.FileUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class PeerService {

	@Value("${application.name:'Rachid Coin'}")
	private String coinName;

	public ObjectMapper objectMapper;

	public PeerService(final ObjectMapper objectMapper) {
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
		PEERS.addPeer(peer);
		PEERS_STATUS.remove(peer);
		try {
			FileUtil.addPeer(coinName, this.objectMapper.writeValueAsString(PEERS.getList()));
		} catch (IOException e) {
			throw new PeerException("It was not possible deserialize peer request");
		}
	}

}
