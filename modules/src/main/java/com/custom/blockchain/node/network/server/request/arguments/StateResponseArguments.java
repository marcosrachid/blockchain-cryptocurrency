package com.custom.blockchain.node.network.server.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class StateResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Long currentBlock;

	public StateResponseArguments() {
		super();
	}

	public StateResponseArguments(Long currentBlock) {
		super();
		this.currentBlock = currentBlock;
	}

	public Long getCurrentBlock() {
		return currentBlock;
	}

	public void setCurrentBlock(Long currentBlock) {
		this.currentBlock = currentBlock;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(currentBlock).hashCode();
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
		return new EqualsBuilder().append(currentBlock, other.currentBlock).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("currentBlock", currentBlock).build();
	}
}
