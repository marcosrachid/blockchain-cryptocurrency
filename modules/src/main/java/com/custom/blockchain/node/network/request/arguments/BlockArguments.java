package com.custom.blockchain.node.network.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class BlockArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Long height;

	public BlockArguments() {
		super();
	}

	public BlockArguments(Long height) {
		super();
		this.height = height;
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).hashCode();
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
		return new EqualsBuilder().append(height, other.height).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("height", height).build();
	}

}
