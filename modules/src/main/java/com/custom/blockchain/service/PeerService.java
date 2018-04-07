package com.custom.blockchain.service;

import static com.custom.blockchain.peer.PeerStateManagement.PEERS;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.custom.blockchain.data.peers.PeersDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.peer.Peer;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class PeerService {

	private PeersDB peersDB;

	public PeerService(final PeersDB peersDB) {
		this.peersDB = peersDB;
	}

	/**
	 * 
	 * @param ip
	 * @param serverPort
	 * @throws BusinessException
	 */
	public void addPeer(String ip, int serverPort) throws BusinessException {
		Peer peer = new Peer(ip, serverPort);
		if (PEERS.contains(peer)) {
			throw new BusinessException(String.format("Peer %s is already registered on peer's list", peer));
		}
		peer.setCreateDatetime(LocalDateTime.now());
		PEERS.add(peer);
		peersDB.put(peer.getIp(), peer);
	}

	/**
	 * 
	 * @param peer
	 * @throws BusinessException
	 */
	public void addPeer(Peer peer) throws BusinessException {
		if (PEERS.contains(peer)) {
			throw new BusinessException(String.format("Peer %s is already registered on peer's list", peer));
		}
		peer.setCreateDatetime(LocalDateTime.now());
		PEERS.add(peer);
		peersDB.put(peer.getIp(), peer);
	}

}
