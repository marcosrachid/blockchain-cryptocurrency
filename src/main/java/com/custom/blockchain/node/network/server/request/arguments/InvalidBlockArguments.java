package com.custom.blockchain.node.network.server.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class InvalidBlockArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Long height;

	public InvalidBlockArguments() {
		super();
	}

	public InvalidBlockArguments(Long height) {
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
		InvalidBlockArguments other = (InvalidBlockArguments) obj;
		return new EqualsBuilder().append(height, other.height).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("height", height).build();
	}

}
