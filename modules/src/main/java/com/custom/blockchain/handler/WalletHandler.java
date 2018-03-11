package com.custom.blockchain.handler;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.resource.dto.response.ResponseBalanceDTO;
import com.custom.blockchain.resource.dto.response.ResponseWalletDTO;
import com.custom.blockchain.service.WalletService;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class WalletHandler {

	private static final Logger LOG = LoggerFactory.getLogger(WalletHandler.class);

	private WalletService walletService;

	public WalletHandler(final WalletService walletService) {
		this.walletService = walletService;
	}

	/**
	 * 
	 * @param publicKey
	 * @return
	 * @throws Exception
	 */
	public ResponseBalanceDTO getBalance(String publicKey) throws Exception {
		Wallet wallet = walletService.getWalletFromStorage(publicKey);
		BigDecimal balance = walletService.getBalance(publicKey);
		return new ResponseBalanceDTO(WalletUtil.getStringFromKey(wallet.getPublicKey()), balance);
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ResponseWalletDTO createWallet() throws Exception {
		Wallet wallet = walletService.createWallet();
		walletService.useNewWallet(wallet);
		LOG.debug("[Crypto] PublicKey - Encoded: {}, String: {}", wallet.getPublicKey().getEncoded(),
				WalletUtil.getStringFromKey(wallet.getPublicKey()));
		LOG.debug("[Crypto] PrivateKey - Encoded: {}, String: {}", wallet.getPrivateKey().getEncoded(),
				WalletUtil.getStringFromKey(wallet.getPrivateKey()));
		return new ResponseWalletDTO(WalletUtil.getStringFromKey(wallet.getPublicKey()),
				WalletUtil.getStringFromKey(wallet.getPrivateKey()));
	}

	/**
	 * 
	 * @param privateKey
	 * @return
	 * @throws Exception
	 */
	public ResponseWalletDTO importWallet(String privateKey) throws Exception {
		Wallet wallet = walletService.getWalletFromPrivateKey(privateKey);
		walletService.useNewWallet(wallet);
		return new ResponseWalletDTO(WalletUtil.getStringFromKey(wallet.getPublicKey()),
				WalletUtil.getStringFromKey(wallet.getPrivateKey()));
	}

	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	public ResponseWalletDTO getCurrentWallet() throws Exception {
		Wallet wallet = walletService.getCurrentWallet();
		return new ResponseWalletDTO(WalletUtil.getStringFromKey(wallet.getPublicKey()),
				WalletUtil.getStringFromKey(wallet.getPrivateKey()));
	}

	/**
	 * 
	 * @param publicKeys
	 * @return
	 * @throws Exception
	 */
	public ResponseBalanceDTO getCurrentWalletBalance() throws Exception {
		Wallet wallet = walletService.getCurrentWallet();
		BigDecimal balance = walletService.getBalance(WalletUtil.getStringFromKey(wallet.getPublicKey()));
		return new ResponseBalanceDTO(WalletUtil.getStringFromKey(wallet.getPublicKey()), balance);
	}
}
