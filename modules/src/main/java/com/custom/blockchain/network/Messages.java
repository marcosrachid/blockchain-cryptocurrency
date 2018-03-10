package com.custom.blockchain.network;

import com.custom.blockchain.network.exception.NetworkException;

public enum Messages {

	TRANSACTION_UPDATE('t'), BLOCK_UPDATE('b'), PEER_UPDATE('p');

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
		case 'p':
			return PEER_UPDATE;
		}
		throw new NetworkException("Non mapped prefix on message");
	}

	public char getPrefix() {
		return prefix;
	}

}
