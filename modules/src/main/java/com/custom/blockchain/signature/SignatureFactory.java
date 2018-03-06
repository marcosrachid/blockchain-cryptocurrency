package com.custom.blockchain.signature;

import java.security.PrivateKey;
import java.security.Signature;

import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.wallet.Wallet;

public class SignatureFactory {

	/**
	 * 
	 * @param transaction
	 * @param wallet
	 */
	public static void generateSignature(final SimpleTransaction transaction, Wallet wallet) {
		String data = TransactionUtil.getStringFromKey(transaction.getSender())
				+ TransactionUtil.getStringFromKey(transaction.getReciepient())
				+ transaction.getValue().setScale(8).toString();
		transaction.setSignature(applyECDSASig(wallet.getPrivateKey(), data));
	}

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

}
