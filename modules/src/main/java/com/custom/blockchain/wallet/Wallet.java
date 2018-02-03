package com.custom.blockchain.wallet;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.TransactionUtil;

public class Wallet {

	private PrivateKey privateKey;
	private PublicKey publicKey;

	public Map<String, TransactionOutput> unspentTransactionsOutput = new HashMap<String, TransactionOutput>();

	public Wallet() throws Exception {
		generateKeyPair();
	}

	public Wallet(String privateKey) throws Exception {
		generateKeyPair(privateKey);
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

	private void generateKeyPair() throws Exception {
		try {
			KeyPairGenerator keyGen;
			keyGen = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom random;
			random = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			keyGen.initialize(ecSpec, random);
			KeyPair keyPair = keyGen.generateKeyPair();
			privateKey = keyPair.getPrivate();
			publicKey = keyPair.getPublic();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new Exception(e.getMessage());
		}
	}

	private void generateKeyPair(String privateKeyString) throws Exception {
		try {
			privateKey = TransactionUtil.getPrivateKeyFromString(privateKeyString);
			publicKey = TransactionUtil.getPublicKeyFromPrivateKey(privateKey);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			throw new Exception(e.getMessage());
		}
	}
}
