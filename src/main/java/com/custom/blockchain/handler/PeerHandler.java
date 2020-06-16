package com.custom.blockchain.handler;

import org.springframework.stereotype.Component;

import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.resource.dto.request.RequestPeerImportDTO;
import com.custom.blockchain.service.PeerService;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class PeerHandler {

	public PeerService peerService;

	public PeerHandler(final PeerService peerService) {
		this.peerService = peerService;
	}

	/**
	 * 
	 * @param peer
	 * @throws BusinessException
	 */
	public void addPeer(RequestPeerImportDTO peer) throws BusinessException {
		this.peerService.addPeer(peer.getIp(), peer.getServerPort());
	}

}
