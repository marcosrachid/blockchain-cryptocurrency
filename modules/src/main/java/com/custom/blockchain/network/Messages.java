package com.custom.blockchain.network;

import com.custom.blockchain.network.exception.NetworkException;

public enum Messages {

	TRANSACTION_UPDATE('t'), BLOCK_UPDATE('b'), SEED_UPDATE('s');

	private char prefix;

	Messages(char prefix) {
		this.prefix = prefix;
	}

	public static Messages mapPrefix(char prefix) {
		switch (prefix) {
		case 't':
			return TRANSACTION_UPDATE;
		case 'b':
			return BLOCK_UPDATE;
		case 's':
			return SEED_UPDATE;
		}
		throw new NetworkException("Non mapped prefix on message");
	}

	public char getPrefix() {
		return prefix;
	}

}
