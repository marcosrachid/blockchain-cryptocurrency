package com.custom.blockchain.handler;

import org.springframework.stereotype.Component;

import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.resource.dto.response.ResponseTransaction;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.service.WalletService;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class TransactionHandler {

	private TransactionService transactionService;
	
	private WalletService walletService;

	public TransactionHandler(final TransactionService transactionService, final WalletService walletService) {
		this.transactionService = transactionService;
		this.walletService = walletService;
	}
	
	public ResponseTransaction sendFunds(RequestSendFundsDTO funds) {
		return new ResponseTransaction();
	}
}
