package com.custom.blockchain.serializers;

import java.io.IOException;

import com.custom.blockchain.node.network.peer.Peer;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class PeerSerializer extends StdSerializer<Peer> {

	private static final long serialVersionUID = 1L;

	protected PeerSerializer() {
		super(Peer.class);
	}

	@Override
	public void serialize(Peer peer, JsonGenerator jg, SerializerProvider sp) throws IOException {
		jg.writeStartObject();
		jg.writeStringField("ip", peer.getIp());
		jg.writeNumberField("serverPort", peer.getServerPort());
		jg.writeEndObject();
	}

}
