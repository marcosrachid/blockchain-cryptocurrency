package com.custom.blockchain.service;

import static com.custom.blockchain.constants.BlockchainConstants.UNSPENT_TRANSACTIONS_OUTPUT;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.wallet.Wallet;

@Service
public class WalletService {

	public BigDecimal getBalance(Wallet wallet) {
		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : UNSPENT_TRANSACTIONS_OUTPUT.entrySet()) {
			TransactionOutput unspentTransactionOutput = item.getValue();
			if (unspentTransactionOutput.isMine(wallet.getPublicKey())) {
				wallet.getUnspentTransactionsOutput().put(unspentTransactionOutput.id, unspentTransactionOutput);
				total = total.add(unspentTransactionOutput.value);
			}
		}
		return total;
	}

}
