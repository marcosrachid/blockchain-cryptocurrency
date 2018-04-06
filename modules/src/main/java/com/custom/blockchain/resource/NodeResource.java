package com.custom.blockchain.resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.handler.NodeHandler;
import com.custom.blockchain.resource.dto.response.ResponseDTO;
import com.custom.blockchain.service.NodeService;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * 
 * @author marcos
 *
 */
@RestController
@RequestMapping(value = "/node")
public class NodeResource {

	private NodeHandler nodeHandler;

	private NodeService nodeService;

	public NodeResource(final NodeHandler nodeHandler, final NodeService nodeService) {
		this.nodeHandler = nodeHandler;
		this.nodeService = nodeService;
	}

	/**
	 * 
	 * @return
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/properties", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getProperties() throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(nodeHandler.getProperties()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/state/block", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getCurrentBlock() {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(nodeService.getCurrentBlock()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/state/difficulty", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getCurrentDifficulty() {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(nodeService.getCurrentDifficulty()));
	}

	/**
	 * 
	 * @return
	 */
	@RequestMapping(value = "/state/height", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getCurrentHeight() {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(nodeService.getCurrentHeight()));
	}

}
