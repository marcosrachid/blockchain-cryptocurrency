package com.custom.blockchain.node.network.server.request.arguments;

import java.util.Collection;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.block.AbstractBlock;

public class BlockResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Collection<AbstractBlock> blocks;

	public BlockResponseArguments() {
		super();
	}

	public BlockResponseArguments(Collection<AbstractBlock> blocks) {
		this.blocks = blocks;
	}

	public Collection<AbstractBlock> getBlocks() {
		return blocks;
	}

	public void setBlocks(Collection<AbstractBlock> blocks) {
		this.blocks = blocks;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(blocks).hashCode();
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
		return new EqualsBuilder().append(blocks, other.blocks).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(getClass().getSimpleName()).append("block", blocks).build();
	}

}
