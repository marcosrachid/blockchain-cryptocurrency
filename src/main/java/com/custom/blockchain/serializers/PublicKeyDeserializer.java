package com.custom.blockchain.serializers;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PublicKeyDeserializer extends StdDeserializer<PublicKey> {

	private static final long serialVersionUID = 1L;

	protected PublicKeyDeserializer() {
		super(PublicKey.class);
	}

	@Override
	public PublicKey deserialize(JsonParser jp, DeserializationContext ctx)
			throws IOException, JsonProcessingException {
		try {
			return WalletUtil.getPublicKeyFromString(jp.readValueAs(String.class));
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {
			return null;
		}
	}

}
