package com.custom.blockchain.block;

import static com.custom.blockchain.properties.GenesisProperties.GENESIS_PREVIOUS_HASH;

import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author marcosrachid
 *
 */
public class Genesis extends Block {

	private static final long serialVersionUID = 1L;

	private BigDecimal minimunTransaction;

	public Genesis() {
		super(GENESIS_PREVIOUS_HASH);
	}

	public BigDecimal getMinimunTransaction() {
		return minimunTransaction;
	}

	public void setMinimunTransaction(BigDecimal minimunTransaction) {
		this.minimunTransaction = minimunTransaction;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(hash).append(merkleRoot).append(nonce).append(previousHash)
				.append(timeStamp).append(transactions).append(minimunTransaction).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Genesis other = (Genesis) obj;
		return new EqualsBuilder().append(hash, other.hash).append(merkleRoot, other.merkleRoot)
				.append(nonce, other.nonce).append(previousHash, other.previousHash).append(timeStamp, other.timeStamp)
				.append(transactions, other.transactions).append(minimunTransaction, other.minimunTransaction)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("hash", hash).append("merkleRoot", merkleRoot).append("nonce", nonce)
				.append("previousHash", previousHash).append("timeStamp", timeStamp)
				.append("transactions", transactions).append("minimunTransaction", minimunTransaction).build();
	}

}
