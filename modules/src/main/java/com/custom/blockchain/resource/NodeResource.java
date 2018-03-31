package com.custom.blockchain.resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.resource.dto.response.ResponseDTO;
import com.custom.blockchain.service.NodeService;

/**
 * 
 * @author marcos
 *
 */
@RestController
@RequestMapping(value = "/node")
public class NodeResource {

	private NodeService nodeService;

	public NodeResource(final NodeService nodeService) {
		this.nodeService = nodeService;
	}

	/**
	 * 
	 * @param publicKeys
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/difficulty", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getCurrentWallet() throws Exception {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(nodeService.getCurrentDifficulty()));
	}

}
