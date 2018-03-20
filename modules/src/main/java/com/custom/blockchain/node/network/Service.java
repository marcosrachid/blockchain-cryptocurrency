package com.custom.blockchain.node.network;

public enum Service {

	PING("ping"), PONG("pong"), GET_STATE("getState"), GET_STATE_RESPONSE("getStateResponse"), GET_BLOCK(
			"getBlock"), GET_BLOCK_RESPONSE("getBlockResponse"), GET_PEERS("getPeers"), GET_PEERS_RESPONSE(
					"getPeersResponse"), GET_TRANSACTIONS("getTransactions"), GET_TRANSACTIONS_RESPONSE(
							"getTransactionsResponse");

	private String service;

	private Service(String service) {
		this.service = service;
	}

	public String getService() {
		return this.service;
	}

}
