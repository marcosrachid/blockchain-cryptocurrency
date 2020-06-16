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
public class RequestImportDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String privateKey;

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(privateKey).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestImportDTO other = (RequestImportDTO) obj;
		return new EqualsBuilder().append(privateKey, other.privateKey).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).build();
	}

}
