package com.custom.blockchain.wallet;

import static com.custom.blockchain.constants.Properties.UNSPENT_TRANSACTIONS_OUTPUT;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionInput;
import com.custom.blockchain.transaction.TransactionOutput;

public class Wallet {
	public PrivateKey privateKey;
	public PublicKey publicKey;

	public Map<String, TransactionOutput> unspentTransactionsOutput = new HashMap<String, TransactionOutput>();

	public Wallet() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public BigDecimal getBalance() {
		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : UNSPENT_TRANSACTIONS_OUTPUT.entrySet()) {
			TransactionOutput unspentTransactionOutput = item.getValue();
			if (unspentTransactionOutput.isMine(publicKey)) {
				unspentTransactionsOutput.put(unspentTransactionOutput.id, unspentTransactionOutput);
				total = total.add(unspentTransactionOutput.value);
			}
		}
		return total;
	}

	public Transaction sendFunds(PublicKey _recipient, BigDecimal value) {
		if (getBalance().compareTo(value) < 0) {
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : unspentTransactionsOutput.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total = total.add(UTXO.value);
			inputs.add(new TransactionInput(UTXO.id));
			if (total.compareTo(value) > 0)
				break;
		}

		Transaction newTransaction = new Transaction(publicKey, _recipient, value, inputs);
		newTransaction.generateSignature(privateKey);

		for (TransactionInput input : inputs) {
			unspentTransactionsOutput.remove(input.transactionOutputId);
		}
		return newTransaction;
	}
}
