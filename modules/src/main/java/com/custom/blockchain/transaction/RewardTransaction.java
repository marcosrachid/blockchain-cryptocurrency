package com.custom.blockchain.transaction;

import static com.custom.blockchain.properties.BlockchainMutableProperties.DIFFICULTY;

import java.math.BigDecimal;
import java.security.PublicKey;

import com.custom.blockchain.util.DigestUtil;

public class RewardTransaction extends Transaction {

	private String coinbase;

	private TransactionOutput output;

	public RewardTransaction(String coinbase, PublicKey to, BigDecimal value) {
		generateCoinbase();
		coinbase += coinbase;
		this.reciepient = to;
		this.value = value;
	}

	public RewardTransaction(PublicKey to, BigDecimal value) {
		generateCoinbase();
		this.reciepient = to;
		this.value = value;
	}

	public String getCoinbase() {
		return coinbase;
	}

	public void setCoinbase(String coinbase) {
		this.coinbase = coinbase;
	}

	public TransactionOutput getOutput() {
		return output;
	}

	public void setOutput(TransactionOutput output) {
		this.output = output;
	}

	@Override
	public BigDecimal getInputsValue() {
		return BigDecimal.ZERO;
	}

	@Override
	public BigDecimal getOutputsValue() {
		return output.getValue();
	}

	private void generateCoinbase() {
		coinbase = DigestUtil.applySha256(Long.toString(DIFFICULTY) + Transaction.sequence);
	}

}
