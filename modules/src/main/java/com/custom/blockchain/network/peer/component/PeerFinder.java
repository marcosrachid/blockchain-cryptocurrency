package com.custom.blockchain.network.peer.component;

import static com.custom.blockchain.costants.SystemConstants.PEERS_FILE;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.custom.blockchain.network.exception.NetworkException;
import com.custom.blockchain.network.peer.Peer;
import com.custom.blockchain.network.peer.PeerCollection;
import com.custom.blockchain.util.OsUtil;
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

	public ObjectMapper objectMapper;

	public PeerFinder(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 * @param peers
	 */
	public void findPeers(final PeerCollection peers) {
		String path = String.format(OsUtil.getRootDirectory(), coinName);
		List<Peer> filePeers;
		try {
			filePeers = objectMapper.readValue(new File(path + PEERS_FILE), new TypeReference<List<Peer>>(){});
		} catch (IOException e) {
			throw new NetworkException("Could not read peers data: " + e.getMessage());
		}
		for (Peer peer : filePeers) {
			peers.addPeer(peer);
		}
	}
	
	public void findFromDNS(final PeerCollection peers) {
		
	}

}
