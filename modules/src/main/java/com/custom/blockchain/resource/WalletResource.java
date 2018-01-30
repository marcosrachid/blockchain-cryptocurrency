package com.custom.blockchain.resource;

import static com.custom.blockchain.costants.LogMessages.REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.handler.WalletHandler;
import com.custom.blockchain.resource.dto.request.RequestBalanceDTO;
import com.custom.blockchain.resource.dto.response.ResponseDTO;

@RestController
public class WalletResource {

	private static final Logger LOG = LoggerFactory.getLogger(WalletResource.class);

	private WalletHandler walletHandler;

	public WalletResource(final WalletHandler walletHandler) {
		this.walletHandler = walletHandler;
	}

	@RequestMapping(value = "/balance/{private-key}", method = RequestMethod.GET, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getBalance(@PathParam("private-key") String privateKey) {
		return ResponseEntity.ok(new ResponseDTO(walletHandler.getBalance(privateKey)));
	}

	@RequestMapping(value = "/balances", method = RequestMethod.POST, consumes = APPLICATION_JSON_UTF8_VALUE, produces = APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<ResponseDTO> getBalances(@Valid @RequestBody RequestBalanceDTO privateKeys) {
		LOG.debug(REQUEST, privateKeys);
		return ResponseEntity.ok(new ResponseDTO(walletHandler.getBalances(privateKeys)));
	}

}
