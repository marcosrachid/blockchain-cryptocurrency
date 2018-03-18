package com.custom.blockchain.serializers;

import java.io.IOException;

import com.custom.blockchain.node.network.peer.Peer;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class PeerDeserializer extends StdDeserializer<Peer> {

	private static final long serialVersionUID = 1L;

	protected PeerDeserializer() {
		super(Peer.class);
	}

	@Override
	public Peer deserialize(JsonParser jp, DeserializationContext ctx) throws IOException, JsonProcessingException {
		jp.nextValue();
		String ip = jp.getText();
		jp.nextValue();
		Integer serverPort = jp.getIntValue();
		return new Peer(ip, serverPort);
	}

}
