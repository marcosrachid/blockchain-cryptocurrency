package com.custom.blockchain.signature;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.springframework.stereotype.Component;

import com.custom.blockchain.signature.exception.SignatureException;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.WalletUtil;
import com.custom.blockchain.wallet.Wallet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class SignatureManager {

	private ObjectMapper objectMapper;

	public SignatureManager(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 * @param transaction
	 * @param wallet
	 * @throws JsonProcessingException
	 */
	public void generateSignature(final SimpleTransaction transaction, Wallet wallet) {
		String data;
		try {
			data = WalletUtil.getStringFromKey(transaction.getSender())
					+ objectMapper.writeValueAsString(transaction.getOutputs())
					+ transaction.getValue().setScale(8).toString();
		} catch (JsonProcessingException e) {
			throw new SignatureException("Could not read transaction outputs");
		}
		transaction.setSignature(applyECDSASig(wallet.getPrivateKey(), data));
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 * @throws JsonProcessingException
	 */
	public boolean verifiySignature(SimpleTransaction transaction) {
		String data;
		try {
			data = WalletUtil.getStringFromKey(transaction.getSender())
					+ objectMapper.writeValueAsString(transaction.getOutputs())
					+ transaction.getValue().setScale(8).toString();
		} catch (JsonProcessingException e) {
			return false;
		}
		return verifyECDSASig(transaction.getSender(), data, transaction.getSignature());
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
