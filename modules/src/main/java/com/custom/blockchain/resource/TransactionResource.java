package com.custom.blockchain.resource;

import static com.custom.blockchain.costants.LogMessages.REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
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

import com.custom.blockchain.handler.TransactionHandler;
import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.resource.dto.response.ResponseDTO;

/**
 * 
 * @author marcosrachid
 *
 */
@RestController
@RequestMapping(value = "/transaction")
public class TransactionResource {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionResource.class);

	private TransactionHandler transactionHandler;

	public TransactionResource(final TransactionHandler transactionHandler) {
		this.transactionHandler = transactionHandler;
	}

	/**
	 * 
	 * @param funds
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> sendFunds(@Valid @RequestBody RequestSendFundsDTO funds) throws Exception {
		LOG.debug(REQUEST, funds);
		return ResponseEntity.status(HttpStatus.ACCEPTED).contentType(APPLICATION_JSON_UTF8)
				.body(new ResponseDTO(transactionHandler.sendFunds(funds)));
	}

}
