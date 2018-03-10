package com.custom.blockchain.network.client;

import com.custom.blockchain.network.Messages;
import com.custom.blockchain.util.TransactionUtil;

public class ClientDispatcher {

	public static void launch(String msg) {
		TransactionUtil.blockTransactions();

		char prefix = msg.charAt(0);
		Messages message = Messages.mapPrefix(prefix);
		switch (message) {
		case TRANSACTION_UPDATE:
			break;
		case BLOCK_UPDATE:
			break;
		case SEED_UPDATE:
			break;
		}
	}

}
