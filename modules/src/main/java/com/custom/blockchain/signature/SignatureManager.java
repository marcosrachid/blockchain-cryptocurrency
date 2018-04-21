package com.custom.blockchain.signature;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.DigestUtil;
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

	private static final Logger LOG = LoggerFactory.getLogger(SignatureManager.class);

	private ObjectMapper objectMapper;

	public SignatureManager(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * 
	 * @param transaction
	 * @param wallet
	 * @throws BusinessException
	 * @throws JsonProcessingException
	 */
	public void generateSignature(final SimpleTransaction transaction, Wallet wallet) throws BusinessException {
		String data;
		try {
			data = WalletUtil.getStringFromKey(transaction.getSender())
					+ objectMapper.writeValueAsString(transaction.getOutputs())
					+ transaction.getValue().toPlainString();
			LOG.trace("[Crypto] raw data to update: {}", data);
		} catch (JsonProcessingException e) {
			LOG.error("[Crypto] Signature error : {}", e.getMessage(), e);
			throw new BusinessException("Could not read transaction outputs");
		}
		transaction.setSignature(applyECDSASig(wallet.getPrivateKey(), DigestUtil.applySha256(data)));
	}

	/**
	 * 
	 * @param transaction
	 * @return
	 * @throws JsonProcessingException
	 */
	public boolean verifySignature(SimpleTransaction transaction) {
		String data;
		try {
			data = WalletUtil.getStringFromKey(transaction.getSender())
					+ objectMapper.writeValueAsString(transaction.getOutputs())
					+ transaction.getValue().toPlainString();
			LOG.trace("[Crypto] raw data to update: {}", data);
		} catch (JsonProcessingException e) {
			LOG.error("[Crypto] Signature error : {}", e.getMessage(), e);
			return false;
		}
		return verifyECDSASig(transaction.getSender(), DigestUtil.applySha256(data), transaction.getSignature());
	}

	/**
	 * 
	 * @param privateKey
	 * @param input
	 * @return
	 */
	public static byte[] applyECDSASig(PrivateKey privateKey, String input) {
		LOG.debug("[Crypto] data to update: {}", input);
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
			LOG.error("[Crypto] Signature error : {}", e.getMessage(), e);
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
		LOG.debug("[Crypto] data to update: {}", data);
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		} catch (Exception e) {
			LOG.error("[Crypto] Signature error : {}", e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

}
