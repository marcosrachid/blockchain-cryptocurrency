package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainImutableProperties.UTXOs;

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
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionInput;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.transaction.exception.TransactionException;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.util.components.BlockManagement;
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

	private BlockManagement blockManagement;

	public TransactionService(final BlockManagement blockManagement) {
		this.blockManagement = blockManagement;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 * @throws Exception
	 */
	public Transaction sendFunds(Wallet from, PublicKey to, BigDecimal fromBalance, BigDecimal value) throws Exception {
		if (fromBalance.compareTo(value) < 0) {
			throw new TransactionException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput UTXO = item.getValue();
			if (UTXO.isMine(from.getPublicKey())) {
				total = total.add(UTXO.getValue());
				inputs.add(new TransactionInput(UTXO.getId()));
				if (total.compareTo(value) > 0)
					break;
			}
		}

		Transaction newTransaction = new Transaction(from.getPublicKey(), to, value, inputs);
		generateSignature(newTransaction, from);

		return newTransaction;
	}

	/**
	 * 
	 * @param block
	 * @param transaction
	 * @throws TransactionException
	 */
	public void addTransaction(Transaction transaction) throws TransactionException {
		Block block = blockManagement.getCurrentBlock();
		if (transaction == null)
			throw new TransactionException("Non existent transaction");
		processTransaction(transaction);
		block.getTransactions().add(transaction);
		LOG.debug("Transaction Successfully added to Block");
	}

	/**
	 * 
	 * @param transaction
	 * @param minimunTransaction
	 * @return
	 * @throws TransactionException
	 */
	private boolean processTransaction(Transaction transaction) throws TransactionException {

		if (verifiySignature(transaction) == false) {
			throw new TransactionException("Transaction Signature failed to verify");
		}

		for (TransactionInput i : transaction.getInputs()) {
			i.setUnspentTransactionOutput(UTXOs.get(i.getTransactionOutputId()));
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
			UTXOs.put(o.getId(), o);
		}

		for (TransactionInput i : transaction.getInputs()) {
			if (i.getUnspentTransactionOutput() == null)
				continue;
			UTXOs.remove(i.getUnspentTransactionOutput().getId());
		}

		return true;
	}

	/**
	 * 
	 * @param transaction
	 * @param wallet
	 */
	public void generateSignature(Transaction transaction, Wallet wallet) {
		String data = TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString();
		transaction.setSignature(TransactionUtil.applyECDSASig(wallet.getPrivateKey(), data));
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 */
	private boolean verifiySignature(Transaction transaction) {
		String data = TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString();
		return TransactionUtil.verifyECDSASig(transaction.getSender(), data, transaction.getSignature());
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 */
	private String calulateHash(Transaction transaction) {
		Transaction.sequence++;
		return DigestUtil.applySha256(TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString() + Transaction.sequence);
	}

}
