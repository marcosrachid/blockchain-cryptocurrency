package com.custom.blockchain.resource;

import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.resource.dto.response.ResponseDTO;
import com.custom.blockchain.service.BlockService;

/**
 * 
 * @author marcosrachid
 *
 */
@RestController
@RequestMapping(value = "/block")
public class BlockResource {

	private BlockService blockService;

	public BlockResource(final BlockService blockService) {
		this.blockService = blockService;
	}

	/**
	 * 
	 * @param height
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/{height:[0-9]+}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getBlockByHeight(@PathVariable("height") Long height) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(blockService.findBlockByHeight(height)));
	}
	
	/**
	 * 
	 * @param hash
	 * @return
	 * @throws BusinessException
	 */
	@RequestMapping(value = "/{hash:[a-zA-Z0-9]{64}}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getBlockByHash(@PathVariable("hash") String hash) throws BusinessException {
		return ResponseEntity.status(HttpStatus.OK).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(blockService.findBlockByHash(hash)));
	}

}
