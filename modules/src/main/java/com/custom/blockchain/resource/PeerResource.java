package com.custom.blockchain.resource;

import static com.custom.blockchain.costants.LogMessagesConstants.REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.handler.PeerHandler;
import com.custom.blockchain.resource.dto.request.RequestPeerImportDTO;

/**
 * 
 * @author marcosrachid
 *
 */
@RestController
@RequestMapping(value = "/peer")
public class PeerResource {

	private static final Logger LOG = LoggerFactory.getLogger(PeerResource.class);

	private PeerHandler peerHandler;

	public PeerResource(final PeerHandler peerHandler) {
		this.peerHandler = peerHandler;
	}

	@RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> sendFunds(@Valid @RequestBody RequestPeerImportDTO peer) throws Exception {
		LOG.debug(REQUEST, peer);
		peerHandler.addPeer(peer);
		return new ResponseEntity<Void>(HttpStatus.CREATED);
	}

}
