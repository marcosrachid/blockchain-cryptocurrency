package com.custom.blockchain.handler;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.custom.blockchain.resource.dto.request.RequestBalanceDTO;
import com.custom.blockchain.resource.dto.response.ResponseBalanceDTO;
import com.custom.blockchain.service.WalletService;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.wallet.Wallet;

@Component
public class WalletHandler {

	private WalletService walletService;

	public WalletHandler(final WalletService walletService) {
		this.walletService = walletService;
	}

	public ResponseBalanceDTO getBalance(String privateKey) {
		Wallet wallet = walletService.getWallet(privateKey);
		BigDecimal balance = walletService.getBalance(privateKey);
		return new ResponseBalanceDTO(TransactionUtil.getStringFromKey(wallet.getPublicKey()), balance);
	}

	public List<ResponseBalanceDTO> getBalances(RequestBalanceDTO privateKeys) {
		List<ResponseBalanceDTO> balances = new ArrayList<>();
		for (String privateKey : privateKeys) {
			Wallet wallet = walletService.getWallet(privateKey);
			BigDecimal balance = walletService.getBalance(privateKey);
			balances.add(new ResponseBalanceDTO(TransactionUtil.getStringFromKey(wallet.getPublicKey()), balance));
		}
		return balances;
	}
}
