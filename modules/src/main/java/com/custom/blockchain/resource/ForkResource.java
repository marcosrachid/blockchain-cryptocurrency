package com.custom.blockchain.resource;

import static com.custom.blockchain.costants.LogMessagesConstants.REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.handler.NodeHandler;
import com.custom.blockchain.resource.dto.request.RequestForkDTO;

/**
 * 
 * @author marcosrachid
 *
 */
@Profile("!miner")
@RestController
@RequestMapping(value = "/fork")
public class ForkResource {

	private static final Logger LOG = LoggerFactory.getLogger(ForkResource.class);

	private NodeHandler nodeHandler;

	public ForkResource(final NodeHandler nodeHandler) {
		this.nodeHandler = nodeHandler;
	}

	/**
	 * 
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(method = RequestMethod.POST, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Void> fork(@Valid @RequestBody RequestForkDTO forkRequest) throws BusinessException {
		LOG.debug(REQUEST, forkRequest);
		nodeHandler.fork(forkRequest);
		return new ResponseEntity<Void>(HttpStatus.OK);
	}

}
