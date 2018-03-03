package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainImutableProperties.UNSPENT_TRANSACTIONS_OUTPUT;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.wallet.Wallet;
import com.custom.blockchain.wallet.exception.WalletException;
import com.custom.blockchain.wallet.selection.CurrentWallet;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class WalletService {

	private CurrentWallet currentWallet;

	public WalletService(final CurrentWallet currentWallet) {
		this.currentWallet = currentWallet;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Wallet createWallet() throws Exception {
		return new Wallet();
	}

	/**
	 * 
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public Wallet getWalletFromStorage(String publicKey) throws Exception {
		return new Wallet();
	}

	/**
	 * 
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public Wallet getWalletFromPrivateKey(String privateKey) throws Exception {
		return new Wallet(privateKey);
	}

	/**
	 * 
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getBalance(String publicKey) throws Exception {
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

	/**
	 * 
	 * @param wallet
	 */
	public void useNewWallet(Wallet wallet) {
		this.currentWallet.setCurrentWallet(wallet);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Wallet getCurrentWallet() throws Exception {
		Wallet wallet = this.currentWallet.getCurrentWallet();
		if (wallet == null) {
			throw new WalletException("No wallet selected yet.");
		}
		return wallet;
	}

}
