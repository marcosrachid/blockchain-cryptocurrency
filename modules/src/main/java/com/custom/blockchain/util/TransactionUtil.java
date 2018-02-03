package com.custom.blockchain.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.custom.blockchain.transaction.Transaction;

/**
 * 
 * @author marcosrachid
 *
 */
public class TransactionUtil {

	private static final Logger LOG = LoggerFactory.getLogger(TransactionUtil.class);

	/**
	 * 
	 * @param privateKey
	 * @param input
	 * @return
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		Signature dsa;
		byte[] output = new byte[0];
		try {
			dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			byte[] strByte = input.getBytes();
			dsa.update(strByte);
			byte[] realSig = dsa.sign();
			output = realSig;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return output;
	}

	/**
	 * 
	 * @param publicKey
	 * @param data
	 * @param signature
	 * @return
	 */
	public static boolean verifyECDSASig(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * 
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getPrivateKeyFromString(String privateKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] keyEncoded = Base64.getDecoder().decode(privateKey);
		LOG.debug("privateKey - Encoded: {}, String: {}", keyEncoded, privateKey);
		KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
		PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(keyEncoded));
		return privKey;
	}

	/**
	 * 
	 * @param privateKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKeyFromPrivateKey(PrivateKey privateKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "BC");
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec("prime192v1");

		ECPoint Q = ecSpec.getG().multiply(((ECPrivateKey) privateKey).getD());
		byte[] publicDerBytes = Q.getEncoded(false);

		ECPoint point = ecSpec.getCurve().decodePoint(publicDerBytes);
		ECPublicKeySpec pubSpec = new ECPublicKeySpec(point, ecSpec);
		ECPublicKey publicKeyGenerated = (ECPublicKey) keyFactory.generatePublic(pubSpec);
		return publicKeyGenerated;
	}

	/**
	 * 
	 * @param transactions
	 * @return
	 */
	public static String getMerkleRoot(List<Transaction> transactions) {
		int count = transactions.size();
		List<String> previousTreeLayer = new ArrayList<String>();
		for (Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.getTransactionId());
		}
		List<String> treeLayer = previousTreeLayer;
		while (count > 1) {
			treeLayer = new ArrayList<String>();
			for (int i = 1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(DigestUtil.applySha256(previousTreeLayer.get(i - 1) + previousTreeLayer.get(i)));
			}
			count = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		String merkleRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
		return merkleRoot;
	}

}
