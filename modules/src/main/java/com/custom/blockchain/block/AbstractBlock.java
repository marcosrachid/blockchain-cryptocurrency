package com.custom.blockchain.block;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

/**
 * 
 * @author marcosrachid
 *
 */
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = PropertiesBlock.class, name = "properties"),
		@Type(value = TransactionsBlock.class, name = "transactions") })
public abstract class AbstractBlock implements Serializable {

	private static final long serialVersionUID = 1L;

	protected static final String GENESIS_PREVIOUS_HASH = "0";

	protected Long height;
	protected String hash = "0";
	protected String previousHash;
	protected Long timeStamp;

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).append(hash).append(previousHash).append(timeStamp).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractBlock other = (AbstractBlock) obj;
		return new EqualsBuilder().append(hash, other.hash).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("height", height).append("hash", hash)
				.append("previousHash", previousHash).append("timeStamp", timeStamp).build();
	}

}
