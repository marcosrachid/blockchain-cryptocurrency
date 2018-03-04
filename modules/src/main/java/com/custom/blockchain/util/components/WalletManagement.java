package com.custom.blockchain.util.components;

import org.springframework.stereotype.Component;

import com.custom.blockchain.wallet.Wallet;

@Component
public class WalletManagement {

	private Wallet coinbase;
	private Wallet currentWallet;

	public Wallet getCoinbase() {
		return coinbase;
	}

	public void setCoinbase(Wallet coinbase) {
		this.coinbase = coinbase;
	}

	public Wallet getCurrentWallet() {
		return currentWallet;
	}

	public void setCurrentWallet(Wallet currentWallet) {
		this.currentWallet = currentWallet;
	}

}
