package com.custom.blockchain.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.resource.dto.request.RequestBalanceDTO;
import com.custom.blockchain.resource.dto.response.ResponseBalanceDTO;
import com.custom.blockchain.resource.dto.response.ResponseWalletDTO;
import com.custom.blockchain.service.WalletService;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.wallet.Wallet;

@Component
public class WalletHandler {
	
	private static final Logger LOG = LoggerFactory.getLogger(WalletHandler.class);

	private WalletService walletService;

	public WalletHandler(final WalletService walletService) {
		this.walletService = walletService;
	}

	public ResponseBalanceDTO getBalance(String publicKey) throws Exception {
		Wallet wallet = walletService.getWalletFromStorage(publicKey);
		BigDecimal balance = walletService.getBalance(publicKey);
		return new ResponseBalanceDTO(TransactionUtil.getStringFromKey(wallet.getPublicKey()), balance);
	}

	public List<ResponseBalanceDTO> getBalances(RequestBalanceDTO publicKeys) throws Exception {
		List<ResponseBalanceDTO> balances = new ArrayList<>();
		for (String publicKey : publicKeys) {
			Wallet wallet = walletService.getWalletFromStorage(publicKey);
			BigDecimal balance = walletService.getBalance(publicKey);
			balances.add(new ResponseBalanceDTO(TransactionUtil.getStringFromKey(wallet.getPublicKey()), balance));
		}
		return balances;
	}

	public ResponseWalletDTO createWallet() throws Exception {
		Wallet wallet = walletService.createWallet();
		LOG.debug("PublicKey - Encoded: {}, String: {}", wallet.getPublicKey().getEncoded(), TransactionUtil.getStringFromKey(wallet.getPublicKey()));
		LOG.debug("PrivateKey - Encoded: {}, String: {}", wallet.getPrivateKey().getEncoded(), TransactionUtil.getStringFromKey(wallet.getPrivateKey()));
		// TODO add to storage if not exist
		return new ResponseWalletDTO(TransactionUtil.getStringFromKey(wallet.getPublicKey()),
				TransactionUtil.getStringFromKey(wallet.getPrivateKey()));
	}

	public ResponseWalletDTO importWallet(String privateKey) throws Exception {
		Wallet wallet = walletService.getWalletFromPrivateKey(privateKey);
		// TODO add to storage if not exist
		return new ResponseWalletDTO(null,
				TransactionUtil.getStringFromKey(wallet.getPrivateKey()));
	}
}
