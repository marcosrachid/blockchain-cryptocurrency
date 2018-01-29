package com.custom.blockchain.resource;

import javax.websocket.server.PathParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.custom.blockchain.resource.dto.response.ResponseDTO;
import com.custom.blockchain.service.WalletService;

@RestController
public class WalletResource {

	private WalletService walletService;

	public WalletResource(final WalletService walletService) {
		this.walletService = walletService;
	}

	@RequestMapping(value = "/balance/{private-key}", method = RequestMethod.GET)
	public ResponseEntity<ResponseDTO> getBalance(@PathParam("private-key") String privateKey) {
		return ResponseEntity.ok(new ResponseDTO(walletService.getBalance(privateKey)));
	}

}
