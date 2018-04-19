package com.custom.blockchain.node.network.server.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class StateResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Long currentBlock;
	private String hash;
	private Long timestamp;

	public StateResponseArguments() {
		super();
	}

	public StateResponseArguments(Long currentBlock, String hash, Long timestamp) {
		super();
		this.currentBlock = currentBlock;
		this.hash = hash;
		this.timestamp = timestamp;
	}

	public Long getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(Long currentBlock) {
		this.currentBlock = currentBlock;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(currentBlock).append(hash).append(timestamp).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StateResponseArguments other = (StateResponseArguments) obj;
		return new EqualsBuilder().append(currentBlock, other.currentBlock).append(hash, other.hash)
				.append(timestamp, other.timestamp).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(getClass().getSimpleName()).append("currentBlock", currentBlock).append("hash", hash)
				.append("timestamp", timestamp).build();
	}
}
