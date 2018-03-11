package com.custom.blockchain.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.custom.blockchain.network.client.component.ClientManagement;
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
		if (ClientManagement.peers.contains(peer)) {
			throw new PeerException(String.format("Peer %s is already registered on peer's list", peer));
		}
		ClientManagement.peers.addPeer(peer);
		try {
			FileUtil.addPeer(coinName, this.objectMapper.writeValueAsString(ClientManagement.peers.getList()));
		} catch (IOException e) {
			throw new PeerException("It was not possible deserialize peer request");
		}
	}

}
