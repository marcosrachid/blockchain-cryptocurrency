package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainImutableProperties.UNSPENT_TRANSACTIONS_OUTPUT;
import static com.custom.blockchain.properties.GenesisProperties.GENESIS_PREVIOUS_HASH;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.block.management.BlockManagement;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionInput;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.transaction.exception.TransactionException;
import com.custom.blockchain.util.DigestUtil;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.wallet.Wallet;

@Service
public class TransactionService {

	private BlockManagement blockManagement;

	private WalletService walletService;

	public TransactionService(final BlockManagement blockManagement, final WalletService walletService) {
		this.blockManagement = blockManagement;
		this.walletService = walletService;
	}

	public void sendFunds(Wallet from, Wallet to, BigDecimal value) throws TransactionException {
		if (walletService.getBalance(from.getPrivateKey().getFormat()).compareTo(value) < 0) {
			throw new TransactionException("Not Enough funds to send transaction. Transaction Discarded");
		}

		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : from.getUnspentTransactionsOutput().entrySet()) {
			TransactionOutput UTXO = item.getValue();
			total = total.add(UTXO.getValue());
			inputs.add(new TransactionInput(UTXO.getId()));
			if (total.compareTo(value) > 0)
				break;
		}

		Transaction newTransaction = new Transaction(from.getPublicKey(), to.getPublicKey(), value, inputs);
		generateSignature(newTransaction, from);

		for (TransactionInput input : inputs) {
			from.getUnspentTransactionsOutput().remove(input.getTransactionOutputId());
		}

		addTransaction(blockManagement.getCurrentBlock(), newTransaction);
	}

	private void addTransaction(Block block, Transaction transaction) throws TransactionException {
		if (transaction == null)
			throw new TransactionException("Non existent transaction");
		if ((block.getPreviousHash() != GENESIS_PREVIOUS_HASH)) {
			processTransaction(transaction, this.blockManagement.getGenesisBlock().getMinimunTransaction());
		}
		block.getTransactions().add(transaction);
		System.out.println("Transaction Successfully added to Block");
	}

	private boolean processTransaction(Transaction transaction, BigDecimal minimunTransaction)
			throws TransactionException {

		if (verifiySignature(transaction) == false) {
			throw new TransactionException("Transaction Signature failed to verify");
		}

		for (TransactionInput i : transaction.getInputs()) {
			i.setUnspentTransactionOutput(UNSPENT_TRANSACTIONS_OUTPUT.get(i.getTransactionOutputId()));
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
			UNSPENT_TRANSACTIONS_OUTPUT.put(o.getId(), o);
		}

		for (TransactionInput i : transaction.getInputs()) {
			if (i.getUnspentTransactionOutput() == null)
				continue;
			UNSPENT_TRANSACTIONS_OUTPUT.remove(i.getUnspentTransactionOutput().getId());
		}

		return true;
	}

	private void generateSignature(Transaction transaction, Wallet wallet) {
		String data = TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString();
		transaction.setSignature(TransactionUtil.applyECDSASig(wallet.getPrivateKey(), data));
	}

	private boolean verifiySignature(Transaction transaction) {
		String data = TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString();
		return TransactionUtil.verifyECDSASig(transaction.getSender(), data, transaction.getSignature());
	}

	private String calulateHash(Transaction transaction) {
		Transaction.sequence++;
		return DigestUtil.applySha256(TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString() + Transaction.sequence);
	}

}
