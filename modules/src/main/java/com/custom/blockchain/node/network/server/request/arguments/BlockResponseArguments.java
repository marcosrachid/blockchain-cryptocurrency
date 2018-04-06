package com.custom.blockchain.node.network.server.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.TransactionsBlock;

public class BlockResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private AbstractBlock block;

	public BlockResponseArguments() {
		super();
	}

	public BlockResponseArguments(AbstractBlock block) {
		super();
		this.block = block;
	}

	public AbstractBlock getBlock() {
		return block;
	}

	public void setBlock(TransactionsBlock block) {
		this.block = block;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(block).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockResponseArguments other = (BlockResponseArguments) obj;
		return new EqualsBuilder().append(block, other.block).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("block", block).build();
	}

}
