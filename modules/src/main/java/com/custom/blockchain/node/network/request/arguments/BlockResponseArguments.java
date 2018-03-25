package com.custom.blockchain.node.network.request.arguments;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.block.Block;

public class BlockResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Block block;

	public BlockResponseArguments() {
		super();
	}

	public BlockResponseArguments(Block block) {
		super();
		this.block = block;
	}

	public Block getBlock() {
		return block;
	}

	public void setBlock(Block block) {
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
