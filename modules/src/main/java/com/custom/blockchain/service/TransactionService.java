package com.custom.blockchain.service;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.resource.dto.request.RequestSendFundsDTO;
import com.custom.blockchain.signature.SignatureManager;
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

	private BlockchainProperties blockchainProperties;

	private UTXOChainstateDB utxoChainstateDb;

	private MempoolDB mempoolDB;

	private SignatureManager signatureManager;

	public TransactionService(final BlockchainProperties blockchainProperties, final UTXOChainstateDB utxoChainstateDb,
			final MempoolDB mempoolDB, final BlockStateManagement blockStateManagement,
			final SignatureManager signatureManager) {
		this.blockchainProperties = blockchainProperties;
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
		if (value.compareTo(blockchainProperties.getMinimunTransaction()) < 0) {
			throw new BusinessException("Transaction sent funds are too low. Transaction Discarded");
		}
		if (fromBalance.compareTo(value) < 0) {
			throw new BusinessException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		List<TransactionOutput> UTXOsSender = utxoChainstateDb.get(from.getPublicKey());
		UTXOsSender.forEach(u -> {
			inputs.add(new TransactionInput(u));
		});

		SimpleTransaction newTransaction = new SimpleTransaction(from.getPublicKey(), value, inputs);
		newTransaction.setTransactionId(calulateHash(newTransaction));
		newTransaction.getOutputs().add(new TransactionOutput(to, value, newTransaction.getTransactionId()));
		signatureManager.generateSignature(newTransaction, from);

		mempoolDB.put(newTransaction.getTransactionId(), newTransaction);

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
		if (totalSentFunds.compareTo(blockchainProperties.getMinimunTransaction()) < 0) {
			throw new BusinessException("Transaction sent funds are too low. Transaction Discarded");
		}
		if (fromBalance.compareTo(totalSentFunds) < 0) {
			throw new BusinessException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		List<TransactionOutput> UTXOsSender = utxoChainstateDb.get(from.getPublicKey());
		UTXOsSender.forEach(u -> {
			inputs.add(new TransactionInput(u));
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
	 * @throws JsonProcessingException
	 */
	private String calulateHash(SimpleTransaction transaction) throws JsonProcessingException {
		Transaction.sequence++;
		return DigestUtil.applySha256(WalletUtil.getStringFromKey(transaction.getSender())
				+ transaction.getValue().setScale(8).toString() + transaction.getTimeStamp() + Transaction.sequence);
	}

}
