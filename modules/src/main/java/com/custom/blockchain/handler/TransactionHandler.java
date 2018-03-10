package com.custom.blockchain.handler;

import java.math.BigDecimal;
import java.security.PublicKey;

import org.springframework.stereotype.Component;

import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.resource.dto.response.ResponseTransaction;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.service.WalletService;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;

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

	public ResponseTransaction sendFunds(RequestSendFundsDTO funds) throws Exception {
		TransactionUtil.checkTransactionBlocked();
		Wallet currentWallet = walletService.getCurrentWallet();
		PublicKey receipientPublicKey = WalletUtil.getPublicKeyFromString(funds.getReciepientPublicKey());
		BigDecimal currentBalance = walletService.getBalance(WalletUtil.getStringFromKey(currentWallet.getPublicKey()));
		SimpleTransaction newTransaction = transactionService.sendFunds(currentWallet, receipientPublicKey,
				currentBalance, funds.getValue());
		return new ResponseTransaction(newTransaction.getTransactionId(),
				WalletUtil.getStringFromKey(newTransaction.getSender()),
				WalletUtil.getStringFromKey(newTransaction.getReciepient()), funds.getValue());
	}
}
