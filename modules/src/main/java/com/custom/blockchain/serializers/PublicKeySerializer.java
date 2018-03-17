package com.custom.blockchain.serializers;

import java.io.IOException;
import java.security.PublicKey;

import com.custom.blockchain.util.WalletUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PublicKeySerializer extends StdSerializer<PublicKey> {

	private static final long serialVersionUID = 1L;

	protected PublicKeySerializer() {
		super(PublicKey.class);
	}

	@Override
	public void serialize(PublicKey publicKey, JsonGenerator jg, SerializerProvider sp) throws IOException {
		jg.writeString(WalletUtil.getStringFromKey(publicKey));
	}

}
