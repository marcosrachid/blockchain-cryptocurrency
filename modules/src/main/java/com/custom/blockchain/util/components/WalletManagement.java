package com.custom.blockchain.util.components;

import org.springframework.stereotype.Component;

import com.custom.blockchain.wallet.Wallet;

@Component
public class WalletManagement {

	private Wallet currentWallet;

	public Wallet getCurrentWallet() {
		return currentWallet;
	}

	public void setCurrentWallet(Wallet currentWallet) {
		this.currentWallet = currentWallet;
	}

}
