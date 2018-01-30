package com.custom.blockchain.resource.dto.request;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class RequestBalanceDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> privateKeys;

	public List<String> getPrivateKey() {
		return privateKeys;
	}

	public void setPrivateKey(List<String> privateKey) {
		this.privateKeys = privateKey;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(privateKeys).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestBalanceDTO other = (RequestBalanceDTO) obj;
		return new EqualsBuilder().append(privateKeys, other.privateKeys).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("privateKey", privateKeys).build();
	}
}
