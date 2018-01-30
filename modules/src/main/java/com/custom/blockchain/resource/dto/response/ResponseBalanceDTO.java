package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResponseBalanceDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String publicKey;
	private BigDecimal balance;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(publicKey).append(balance).hashCode();
	}

	@Override	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseBalanceDTO other = (ResponseBalanceDTO) obj;
		return new EqualsBuilder().append(publicKey, other.publicKey).append(balance, other.balance).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("publicKey", publicKey).append("balance", balance).build();
	}
}
