package com.custom.blockchain.service;

import static com.custom.blockchain.properties.BlockchainImutableProperties.UTXOs;
import static com.custom.blockchain.properties.BlockchainMutableProperties.CURRENT_WALLET;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.wallet.Wallet;
import com.custom.blockchain.wallet.exception.WalletException;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class WalletService {

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
		PublicKey key = TransactionUtil.getPublicKeyFromString(publicKey);
		BigDecimal total = BigDecimal.ZERO;
		for (Map.Entry<String, TransactionOutput> item : UTXOs.entrySet()) {
			TransactionOutput unspentTransactionOutput = item.getValue();
			if (unspentTransactionOutput.isMine(key)) {
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
		CURRENT_WALLET = wallet;
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public Wallet getCurrentWallet() throws Exception {
		Wallet wallet = CURRENT_WALLET;
		if (wallet == null) {
			throw new WalletException("No wallet selected yet.");
		}
		return wallet;
	}

}
