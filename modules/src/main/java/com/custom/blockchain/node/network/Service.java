package com.custom.blockchain.node.network;

public enum Service {

	GET_STATE("getState"), GET_BLOCK("getBlock"), GET_PEERS("getPeers"), GET_TRANSACTIONS("getTransactions"), UNKNOWN(
			"unknown"), PING("ping"), PONG("pong");

	private String service;

	private Service(String service) {
		this.service = service;
	}

	public String getService() {
		return this.service;
	}

	public String getService(String param) {
		return String.format(this.service, param);
	}

}
