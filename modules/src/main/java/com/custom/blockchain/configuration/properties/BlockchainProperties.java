package com.custom.blockchain.configuration.properties;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import com.custom.blockchain.peer.Peer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@Validated
@ConfigurationProperties(prefix = "application.blockchain")
public class BlockchainProperties {

	private ObjectMapper objectMapper;

	@NotNull
	@Min(1025)
	@Max(65536)
	private Integer networkPort;

	@NotNull
	@Min(0)
	private Integer networkMaximumSeeds;

	private List<String> networkMockedPeers;

	private String miner;

	public BlockchainProperties(final ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
		if (networkMockedPeers == null)
			networkMockedPeers = new ArrayList<String>();
	}

	public Integer getNetworkPort() {
		return networkPort;
	}

	public void setNetworkPort(Integer networkPort) {
		this.networkPort = networkPort;
	}

	public Integer getNetworkMaximumSeeds() {
		return networkMaximumSeeds;
	}

	public void setNetworkMaximumSeeds(Integer networkMaximumSeeds) {
		this.networkMaximumSeeds = networkMaximumSeeds;
	}

	public List<String> getNetworkMockedPeers() {
		return networkMockedPeers;
	}

	public List<Peer> getNetworkMockedPeersMapped() throws JsonParseException, JsonMappingException, IOException {
		List<Peer> peers = new ArrayList<>();
		for (String p : networkMockedPeers) {
			peers.add(objectMapper.readValue(p, Peer.class));
		}
		return peers;
	}

	public void setNetworkMockedPeers(List<String> networkMockedPeers) {
		this.networkMockedPeers = networkMockedPeers;
	}

	public String getMiner() {
		return miner;
	}

	public void setMiner(String miner) {
		this.miner = miner;
	}

}
