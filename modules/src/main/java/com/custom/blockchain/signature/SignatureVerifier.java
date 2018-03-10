package com.custom.blockchain.signature;

import java.security.PublicKey;
import java.security.Signature;

import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.WalletUtil;

/**
 * 
 * @author marcosrachid
 *
 */
public class SignatureVerifier {

	/**
	 * 
	 * @param transaction
	 * @return
	 */
	public static boolean verifiySignature(SimpleTransaction transaction) {
		String data = WalletUtil.getStringFromKey(transaction.getSender())
				+ WalletUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString();
		return verifyECDSASig(transaction.getSender(), data, transaction.getSignature());
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

}
