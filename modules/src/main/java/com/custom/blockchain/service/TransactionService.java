package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainMutableProperties.CURRENT_BLOCK;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.data.chainstate.ChainstateDB;
import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionInput;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.transaction.exception.TransactionException;
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

	private static final Logger LOG = LoggerFactory.getLogger(TransactionService.class);

	@Value("${application.blockchain.minimun.transaction}")
	private BigDecimal minimunTransaction;

	private ChainstateDB chainstateDb;

	private SignatureManager signatureManager;

	public TransactionService(final ChainstateDB chainstateDb, final SignatureManager signatureManager) {
		this.chainstateDb = chainstateDb;
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
		if (fromBalance.compareTo(value) < 0) {
			throw new TransactionException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		List<TransactionOutput> UTXOsSender = getUnspentTransactionOutput(from.getPublicKey());
		UTXOsSender.forEach(u -> {
			inputs.add(new TransactionInput(u.getId()));
		});

		SimpleTransaction newTransaction = new SimpleTransaction(from.getPublicKey(), value, inputs);
		newTransaction.setTransactionId(calulateHash(newTransaction));
		newTransaction.getOutputs().add(new TransactionOutput(to, value, newTransaction.getTransactionId()));
		signatureManager.generateSignature(newTransaction, from);

		// TODO: change to mempool and process on mining
		// TRANSACTION_MEMPOOL.add(newTransaction);
		addTransaction(newTransaction);

		return newTransaction;
	}

	/**
	 * 
	 * @param from
	 * @param to
	 * @param value
	 * @throws Exception
	 */
	public SimpleTransaction sendFunds(Wallet from, BigDecimal fromBalance, BigDecimal totalSentFunds,
			RequestSendFundsDTO.RequestSendFundsListDTO funds) throws Exception {
		if (fromBalance.compareTo(totalSentFunds) < 0) {
			throw new TransactionException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		List<TransactionOutput> UTXOsSender = getUnspentTransactionOutput(from.getPublicKey());
		UTXOsSender.forEach(u -> {
			inputs.add(new TransactionInput(u.getId()));
		});

		SimpleTransaction newTransaction = new SimpleTransaction(from.getPublicKey(), totalSentFunds, inputs);
		newTransaction.setTransactionId(calulateHash(newTransaction));
		for (RequestSendFundsDTO f : funds) {
			PublicKey reciepient;
			try {
				reciepient = WalletUtil.getPublicKeyFromString(f.getReciepientPublicKey());
				newTransaction.getOutputs()
						.add(new TransactionOutput(reciepient, f.getValue(), newTransaction.getTransactionId()));
			} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
				throw new TransactionException("Invalid reciepient public key [" + f.getReciepientPublicKey() + "]");
			}
		}
		signatureManager.generateSignature(newTransaction, from);

		// TODO: change to mempool and process on mining
		// TRANSACTION_MEMPOOL.add(newTransaction);
		addTransaction(newTransaction);

		return newTransaction;
	}

	// TODO: move method to BlockService futurely
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

	// TODO: move method to BlockService futurely
	/**
	 * 
	 * @param transaction
	 * @param minimunTransaction
	 * @return
	 * @throws TransactionException
	 */
	private boolean processTransaction(SimpleTransaction transaction) throws TransactionException {

		if (signatureManager.verifiySignature(transaction) == false) {
			throw new TransactionException("Transaction Signature failed to verify");
		}

		for (TransactionInput i : transaction.getInputs()) {
			i.setUnspentTransactionOutput(chainstateDb.get("c" + i.getTransactionOutputId()));
		}

		if (transaction.getInputsValue().compareTo(minimunTransaction) < 0) {
			throw new TransactionException("Transaction Inputs too small: " + transaction.getInputsValue());
		}

		// TODO: mining reward
		BigDecimal leftOver = transaction.getInputsValue().subtract(transaction.getValue());
		transaction.getOutputs()
				.add(new TransactionOutput(transaction.getSender(), leftOver, transaction.getTransactionId()));

		for (TransactionOutput o : transaction.getOutputs()) {
			chainstateDb.put("c" + o.getId(), o);
		}

		for (TransactionInput i : transaction.getInputs()) {
			chainstateDb.delete("c" + i.getUnspentTransactionOutput().getId());
		}

		// TODO remove transaction from mempool

		return true;
	}

	/**
	 * 
	 * @param publicKey
	 * @return
	 */
	private List<TransactionOutput> getUnspentTransactionOutput(PublicKey publicKey) {
		List<TransactionOutput> UTXOs = new ArrayList<>();
		DBIterator iterator = chainstateDb.iterator();
		while (iterator.hasNext()) {
			TransactionOutput UTXO = chainstateDb.next(iterator);
			if (UTXO.isMine(publicKey))
				UTXOs.add(UTXO);
		}
		return UTXOs;
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 * @throws JsonProcessingException
	 */
	private String calulateHash(SimpleTransaction transaction) throws JsonProcessingException {
		Transaction.sequence++;
		return DigestUtil.applySha256(WalletUtil.getStringFromKey(transaction.getSender())
				+ transaction.getValue().setScale(8).toString() + transaction.getTimeStamp() + Transaction.sequence);
	}

}
