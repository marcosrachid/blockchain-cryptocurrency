package com.custom.blockchain.wallet;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.transaction.TransactionOutput;

public class Wallet {
	private PrivateKey privateKey;
	private PublicKey publicKey;

	public Map<String, TransactionOutput> unspentTransactionsOutput = new HashMap<String, TransactionOutput>();

	public Wallet() {
		generateKeyPair();
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public Map<String, TransactionOutput> getUnspentTransactionsOutput() {
		return unspentTransactionsOutput;
	}

	public void setUnspentTransactionsOutput(Map<String, TransactionOutput> unspentTransactionsOutput) {
		this.unspentTransactionsOutput = unspentTransactionsOutput;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(privateKey).append(publicKey).append(unspentTransactionsOutput).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wallet other = (Wallet) obj;
		return new EqualsBuilder().append(privateKey, other.privateKey).append(publicKey, other.publicKey)
				.append(unspentTransactionsOutput, other.unspentTransactionsOutput).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("privateKey", privateKey).append("publicKey", publicKey)
				.append("unspentTransactionsOutput", unspentTransactionsOutput).build();
	}

	private void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
