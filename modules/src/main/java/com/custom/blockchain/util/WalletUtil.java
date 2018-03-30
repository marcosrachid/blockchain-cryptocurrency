package com.custom.blockchain.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.interfaces.ECPrivateKey;
import org.bouncycastle.jce.interfaces.ECPublicKey;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.bouncycastle.jce.spec.ECPublicKeySpec;
import org.bouncycastle.math.ec.ECPoint;

/**
 * 
 * @author marcosrachid
 *
 */
public class WalletUtil {

	/**
	 * 
	 * @param key
	 * @return
	 */
	public static String getStringFromKey(Key key) {
		if (key != null)
			return Base64.getEncoder().encodeToString(key.getEncoded());
		return null;
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
		KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
		PrivateKey privKey = kf.generatePrivate(new PKCS8EncodedKeySpec(keyEncoded));
		return privKey;
	}

	/**
	 * 
	 * @param publicKey
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getPublicKeyFromString(String publicKey)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] keyEncoded = Base64.getDecoder().decode(publicKey);
		KeyFactory kf = KeyFactory.getInstance("ECDSA", "BC");
		PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(keyEncoded));
		return pubKey;
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

}
