package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainImutableProperties.UNSPENT_TRANSACTIONS_OUTPUT;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.custom.blockchain.resource.dto.request.RequestBalanceDTO;
import com.custom.blockchain.resource.dto.response.ResponseBalanceDTO;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.wallet.Wallet;

@Service
public class WalletService {

	public BigDecimal getBalance(String privateKey) {
		// TODO: find wallet info and transactions on blockchain
		Wallet wallet = new Wallet();
		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : UNSPENT_TRANSACTIONS_OUTPUT.entrySet()) {
			TransactionOutput unspentTransactionOutput = item.getValue();
			if (unspentTransactionOutput.isMine(wallet.getPublicKey())) {
				wallet.getUnspentTransactionsOutput().put(unspentTransactionOutput.getId(), unspentTransactionOutput);
				total = total.add(unspentTransactionOutput.getValue());
			}
		}
		return total;
	}

	public List<ResponseBalanceDTO> getBalances(RequestBalanceDTO privateKeys) {
		List<ResponseBalanceDTO> balances = new ArrayList<>();
		for (String privateKey : privateKeys.getPrivateKey()) {
			// TODO: find wallet info and transactions on blockchain
			ResponseBalanceDTO responseBalanceDTO = new ResponseBalanceDTO();
			balances.add(responseBalanceDTO);
		}
		return balances;
	}

}
