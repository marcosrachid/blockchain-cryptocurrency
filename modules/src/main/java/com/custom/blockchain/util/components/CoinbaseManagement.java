package com.custom.blockchain.util.components;

import java.security.PrivateKey;

import org.springframework.stereotype.Component;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class CoinbaseManagement {

	private PrivateKey privateKey;

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}
	
}
