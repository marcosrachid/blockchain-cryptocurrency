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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.util.TransactionUtil;

/**
 * 
 * @author marcosrachid
 *
 */
public class Wallet {

	private PrivateKey privateKey;
	private PublicKey publicKey;

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

	/**
	 * 
	 * @throws Exception
	 */
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

	/**
	 * 
	 * @param privateKeyString
	 * @throws Exception
	 */
	private void generateKeyPair(String privateKeyString) throws Exception {
		try {
			privateKey = TransactionUtil.getPrivateKeyFromString(privateKeyString);
			publicKey = TransactionUtil.getPublicKeyFromPrivateKey(privateKey);
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(privateKey).append(publicKey).hashCode();
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
		return new EqualsBuilder().append(privateKey, other.privateKey).append(publicKey, other.publicKey).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("privateKey", TransactionUtil.getStringFromKey(privateKey))
				.append("publicKey", TransactionUtil.getStringFromKey(publicKey)).build();
	}
}
