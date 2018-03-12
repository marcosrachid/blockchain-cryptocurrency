package com.custom.blockchain.resource.dto.request;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author marcosrachid
 *
 */
public class RequestBalanceDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String publicKey;

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(publicKey).hashCode();
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
		return new EqualsBuilder().append(publicKey, other.publicKey).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("publicKey", publicKey).build();
	}

}
