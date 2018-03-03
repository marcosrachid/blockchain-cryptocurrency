package com.custom.blockchain.wallet.selection;

import org.springframework.stereotype.Component;

import com.custom.blockchain.wallet.Wallet;

@Component
public class CurrentWallet {

	private Wallet wallet;

	public Wallet getCurrentWallet() {
		return wallet;
	}

	public void setCurrentWallet(Wallet wallet) {
		this.wallet = wallet;
	}

}
