package com.custom.blockchain.service;

import static com.custom.blockchain.wallet.WalletStateManagement.CURRENT_WALLET;

import java.math.BigDecimal;
import java.security.PublicKey;

import org.iq80.leveldb.DBIterator;
import org.springframework.stereotype.Service;

import com.custom.blockchain.data.ChainstateDB;
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

	private ChainstateDB chainstateDb;

	public WalletService(final ChainstateDB chainstateDb) {
		this.chainstateDb = chainstateDb;
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
		BigDecimal total = BigDecimal.ZERO;
		DBIterator iterator = chainstateDb.iterator();
		while (iterator.hasNext()) {
			TransactionOutput utxo = chainstateDb.next(iterator);
			if (utxo.isMine(key))
				total = utxo.getValue().add(total);
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
