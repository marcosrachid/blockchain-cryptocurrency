package com.custom.blockchain.service;

import static com.custom.blockchain.costants.ChainConstants.UTXOS;
import static com.custom.blockchain.properties.BlockchainMutableProperties.CURRENT_BLOCK;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.signature.SignatureFactory;
import com.custom.blockchain.signature.SignatureVerifier;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionInput;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.transaction.exception.TransactionException;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class TransactionService {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

	@Value("${application.blockchain.minimun.transaction}")
	private BigDecimal minimunTransaction;

	/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 * @throws Exception
	 */
	public SimpleTransaction sendFunds(Wallet from, PublicKey to, BigDecimal fromBalance, BigDecimal value)
			throws Exception {
		if (fromBalance.compareTo(value) < 0) {
			throw new TransactionException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : UTXOS.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(from.getPublicKey())) {
				total = total.add(UTXO.getValue());
				inputs.add(new TransactionInput(UTXO.getId()));
				if (total.compareTo(value) > 0)
					break;
			}
		}

		SimpleTransaction newTransaction = new SimpleTransaction(from.getPublicKey(), to, value, inputs);
		SignatureFactory.generateSignature(newTransaction, from);

		// TODO: change to mempool and process on mining
		// TRANSACTION_MEMPOOL.add(newTransaction);
		addTransaction(newTransaction);

		return newTransaction;
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws TransactionException
	 */
	public void addTransaction(SimpleTransaction transaction) throws TransactionException {
		Block block = CURRENT_BLOCK;
		if (transaction == null)
			throw new TransactionException("Non existent transaction");
		processTransaction(transaction);
		block.getTransactions().add(transaction);
		LOG.info("[Crypto] Transaction Successfully added to Block");
	}

	/**
	 * 
	 * @param transaction
	 * @param minimunTransaction
	 * @return
	 * @throws TransactionException
	 */
	private boolean processTransaction(SimpleTransaction transaction) throws TransactionException {

		if (SignatureVerifier.verifiySignature(transaction) == false) {
			throw new TransactionException("Transaction Signature failed to verify");
		}

		for (TransactionInput i : transaction.getInputs()) {
			i.setUnspentTransactionOutput(UTXOS.get(i.getTransactionOutputId()));
		}

		if (transaction.getInputsValue().compareTo(minimunTransaction) < 0) {
			throw new TransactionException("Transaction Inputs too small: " + transaction.getInputsValue());
		}

		BigDecimal leftOver = transaction.getInputsValue().subtract(transaction.getValue());
		transaction.setTransactionId(calulateHash(transaction));
		transaction.getOutputs().add(new TransactionOutput(transaction.getReciepient(), transaction.getValue(),
				transaction.getTransactionId()));
		transaction.getOutputs()
				.add(new TransactionOutput(transaction.getSender(), leftOver, transaction.getTransactionId()));

		for (TransactionOutput o : transaction.getOutputs()) {
			UTXOS.put(o.getId(), o);
		}

		for (TransactionInput i : transaction.getInputs()) {
			if (i.getUnspentTransactionOutput() == null)
				continue;
			UTXOS.remove(i.getUnspentTransactionOutput().getId());
		}

		return true;
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 */
	private String calulateHash(SimpleTransaction transaction) {
		Transaction.sequence++;
		return DigestUtil.applySha256(WalletUtil.getStringFromKey(transaction.getSender())
				+ WalletUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString() + Transaction.sequence);
	}

}
