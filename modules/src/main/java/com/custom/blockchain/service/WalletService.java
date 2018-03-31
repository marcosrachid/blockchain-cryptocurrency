package com.custom.blockchain.service;

import static com.custom.blockchain.wallet.WalletStateManagement.CURRENT_WALLET;

import java.math.BigDecimal;
import java.security.PublicKey;
import java.util.List;

import org.springframework.stereotype.Service;

import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.custom.blockchain.wallet.exception.WalletException;

/**
 * 
 * @author marcosrachid
 *
 */
@Service
public class WalletService {

	private UTXOChainstateDB utxoChainstateDb;

	public WalletService(final UTXOChainstateDB utxoChainstateDb) {
		this.utxoChainstateDb = utxoChainstateDb;
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
		PublicKey key = WalletUtil.getPublicKeyFromString(publicKey);
		List<TransactionOutput> outputs = utxoChainstateDb.get(key);
		BigDecimal total = BigDecimal.ZERO;
		for (TransactionOutput o : outputs) {
			total = total.add(o.getValue());
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
