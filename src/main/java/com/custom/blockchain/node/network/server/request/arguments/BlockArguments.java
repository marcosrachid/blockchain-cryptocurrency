package com.custom.blockchain.node.network.server.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class BlockArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Long startHeight;
	private Long peerHeight;

	public BlockArguments() {
		super();
	}

	public BlockArguments(Long startHeight, Long peerHeight) {
		super();
		this.startHeight = startHeight;
		this.peerHeight = peerHeight;
	}

	public Long getStartHeight() {
		return startHeight;
	}

	public void setStartHeight(Long startHeight) {
		this.startHeight = startHeight;
	}

	public Long getPeerHeight() {
		return peerHeight;
	}

	public void setPeerHeight(Long peerHeight) {
		this.peerHeight = peerHeight;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(startHeight).append(peerHeight).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockArguments other = (BlockArguments) obj;
		return new EqualsBuilder().append(startHeight, other.startHeight).append(peerHeight, other.peerHeight).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(getClass().getSimpleName()).append("startHeight", startHeight).append("peerHeight", peerHeight).build();
	}

}
