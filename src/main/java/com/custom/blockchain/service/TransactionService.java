package com.custom.blockchain.service;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionInput;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class TransactionService {

	private CurrentPropertiesChainstateDB currentPropertiesBlockDB;

	private UTXOChainstateDB utxoChainstateDb;

	private MempoolDB mempoolDB;

	private SignatureManager signatureManager;

	public TransactionService(final CurrentPropertiesChainstateDB currentPropertiesBlockDB,
			final UTXOChainstateDB utxoChainstateDb, final MempoolDB mempoolDB,
			final SignatureManager signatureManager) {
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.utxoChainstateDb = utxoChainstateDb;
		this.mempoolDB = mempoolDB;
		this.signatureManager = signatureManager;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 * @throws Exception
	 */
	public SimpleTransaction sendFunds(Wallet from, PublicKey to, BigDecimal fromBalance, BigDecimal value)
			throws Exception {
		if (from.getPublicKey().equals(to)) {
			throw new BusinessException("Transaction can't have the same Sender and Receiver");
		}
		if (value.compareTo(currentPropertiesBlockDB.get().getMinimunTransaction()) < 0) {
			throw new BusinessException("Transaction sent funds are too low. Transaction Discarded");
		}
		if (fromBalance.compareTo(value) < 0) {
			throw new BusinessException("Not Enough funds to send transaction. Transaction Discarded");
		}

		SimpleTransaction newTransaction = new SimpleTransaction(from.getPublicKey(), value);
		newTransaction.setTransactionId(
				calulateHash(newTransaction, Collections.singletonList(WalletUtil.getStringFromKey(to))));
		newTransaction.getOutputs().add(new TransactionOutput(to, value, newTransaction.getTransactionId()));
		signatureManager.generateSignature(newTransaction, from);

		mempoolDB.put(newTransaction.getTransactionId(), newTransaction);

		return newTransaction;
	}

	/**
	 * 
	 * @param from
	 * @param fromBalance
	 * @param totalSentFunds
	 * @param funds
	 * @return
	 * @throws Exception
	 */
	public SimpleTransaction sendFunds(Wallet from, BigDecimal fromBalance, BigDecimal totalSentFunds,
			RequestSendFundsDTO.RequestSendFundsListDTO funds) throws Exception {
		if (funds.stream()
				.filter(f -> f.getReciepientPublicKey().equals(WalletUtil.getStringFromKey(from.getPublicKey())))
				.findFirst().isPresent()) {
			throw new BusinessException("Transaction can't have the same Sender and Receiver");
		}
		if (totalSentFunds.compareTo(currentPropertiesBlockDB.get().getMinimunTransaction()) < 0) {
			throw new BusinessException("Transaction sent funds are too low. Transaction Discarded");
		}
		if (fromBalance.compareTo(totalSentFunds) < 0) {
			throw new BusinessException("Not Enough funds to send transaction. Transaction Discarded");
		}

		SimpleTransaction newTransaction = new SimpleTransaction(from.getPublicKey(), totalSentFunds);
		newTransaction.setTransactionId(calulateHash(newTransaction,
				funds.stream().map(f -> f.getReciepientPublicKey()).collect(Collectors.toList())));
		for (RequestSendFundsDTO f : funds) {
			PublicKey reciepient;
			try {
				reciepient = WalletUtil.getPublicKeyFromString(f.getReciepientPublicKey());
				newTransaction.getOutputs()
						.add(new TransactionOutput(reciepient, f.getValue(), newTransaction.getTransactionId()));
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
				throw new BusinessException("Invalid reciepient public key [" + f.getReciepientPublicKey() + "]");
			}
		}
		signatureManager.generateSignature(newTransaction, from);

		mempoolDB.put(newTransaction.getTransactionId(), newTransaction);

		return newTransaction;
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 */
	public String calulateHash(RewardTransaction transaction) {
		Transaction.sequence++;
		return DigestUtil.applySha256(
				DigestUtil.applySha256(transaction.getCoinbase() + transaction.getValue().setScale(8).toString()
						+ transaction.getDifficulty() + transaction.getTimeStamp() + Transaction.sequence));
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 * @throws JsonProcessingException
	 */
	public String calulateHash(SimpleTransaction transaction, List<String> to) {
		Transaction.sequence++;
		return DigestUtil.applySha256(WalletUtil.getStringFromKey(transaction.getSender()) + to
				+ transaction.getValue().toPlainString() + transaction.getTimeStamp() + Transaction.sequence);
	}

	/**
	 * 
	 * @param transactions
	 */
	public void addTransactionsUtxo(Collection<Transaction> transactions) {
		for (Transaction transaction : transactions) {
			if (transaction instanceof SimpleTransaction)
				addToUtxo((SimpleTransaction) transaction);
			if (transaction instanceof RewardTransaction)
				addToUtxo((RewardTransaction) transaction);
		}
	}

	/**
	 * 
	 * @param transactions
	 */
	public void removeTransactionsUtxo(Collection<Transaction> transactions) {
		for (Transaction transaction : transactions) {
			if (transaction instanceof SimpleTransaction)
				removeUtxo((SimpleTransaction) transaction);
			if (transaction instanceof RewardTransaction)
				removeUtxo((RewardTransaction) transaction);
		}
	}

	/**
	 * 
	 * @param transactions
	 */
	public void mempoolChargeback(Collection<Transaction> transactions) {
		for (SimpleTransaction transaction : transactions.stream().filter(t -> t instanceof SimpleTransaction)
				.map(t -> (SimpleTransaction) t).collect(Collectors.toSet())) {
			transaction.setInputs(new ArrayList<TransactionInput>());
			mempoolDB.put(transaction.getTransactionId(), (SimpleTransaction) transaction);
		}
	}

	/**
	 * 
	 * @param transaction
	 */
	private void addToUtxo(SimpleTransaction transaction) {
		TransactionOutput leftOverTransaction = transaction.getOutputs().stream()
				.filter(o -> o.getReciepient().equals(transaction.getSender())).findFirst().get();
		utxoChainstateDb.leftOver(leftOverTransaction.getReciepient(), leftOverTransaction);

		for (TransactionOutput output : transaction.getOutputs().stream()
				.filter(o -> !o.getReciepient().equals(transaction.getSender())).collect(Collectors.toList())) {
			utxoChainstateDb.add(output.getReciepient(), output);
		}

		mempoolDB.delete(transaction.getTransactionId());
	}

	/**
	 * 
	 * @param transaction
	 */
	private void addToUtxo(RewardTransaction transaction) {
		TransactionOutput output = transaction.getOutput();
		utxoChainstateDb.add(output.getReciepient(), output);
	}

	/**
	 * 
	 * @param transaction
	 */
	private void removeUtxo(SimpleTransaction transaction) {

	}

	/**
	 * 
	 * @param transaction
	 */
	private void removeUtxo(RewardTransaction transaction) {

	}

}
