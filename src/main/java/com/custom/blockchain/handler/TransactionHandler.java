package com.custom.blockchain.handler;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.resource.dto.response.ResponseReciepientDTO;
import com.custom.blockchain.resource.dto.response.ResponseTransaction;
import com.custom.blockchain.resource.dto.response.ResponseTransactions;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.service.WalletService;
import com.custom.blockchain.transaction.SimpleTransaction;
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

	/**
	 * 
	 * @param funds
	 * @return
	 * @throws Exception
	 */
	public ResponseTransaction sendFunds(RequestSendFundsDTO funds) throws Exception {
		Wallet currentWallet = walletService.getCurrentWallet();
		PublicKey receipientPublicKey = WalletUtil.getPublicKeyFromString(funds.getReciepientPublicKey());
		BigDecimal currentBalance = walletService.getBalance(WalletUtil.getStringFromKey(currentWallet.getPublicKey()));
		SimpleTransaction newTransaction = transactionService.sendFunds(currentWallet, receipientPublicKey,
				currentBalance, funds.getValue());
		return new ResponseTransaction(newTransaction.getTransactionId(),
				WalletUtil.getStringFromKey(newTransaction.getSender()),
				WalletUtil.getStringFromKey(newTransaction.getOutputs().get(0).getReciepient()), funds.getValue());
	}

	/**
	 * 
	 * @param funds
	 * @return
	 * @throws Exception
	 */
	public ResponseTransactions sendFunds(RequestSendFundsDTO.RequestSendFundsListDTO funds) throws Exception {
		Wallet currentWallet = walletService.getCurrentWallet();
		BigDecimal currentBalance = walletService.getBalance(WalletUtil.getStringFromKey(currentWallet.getPublicKey()));
		final List<ResponseReciepientDTO> reciepientList = new ArrayList<>();
		BigDecimal totalSentFunds = BigDecimal.ZERO;
		for (RequestSendFundsDTO f : funds) {
			reciepientList.add(new ResponseReciepientDTO(f.getReciepientPublicKey(), f.getValue()));
			totalSentFunds = totalSentFunds.add(f.getValue());
		}
		SimpleTransaction newTransaction = transactionService.sendFunds(currentWallet, currentBalance, totalSentFunds,
				funds);
		return new ResponseTransactions(newTransaction.getTransactionId(),
				WalletUtil.getStringFromKey(newTransaction.getSender()), reciepientList);
	}
}
