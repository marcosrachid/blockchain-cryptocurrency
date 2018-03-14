package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;
import java.math.BigDecimal;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResponseReciepientDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String reciepientPublicKey;
	private BigDecimal value;

	public ResponseReciepientDTO(String reciepientPublicKey, BigDecimal value) {
		super();
		this.reciepientPublicKey = reciepientPublicKey;
		this.value = value;
	}

	public String getReciepientPublicKey() {
		return reciepientPublicKey;
	}

	public void setReciepientPublicKey(String reciepientPublicKey) {
		this.reciepientPublicKey = reciepientPublicKey;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(reciepientPublicKey).append(value).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseReciepientDTO other = (ResponseReciepientDTO) obj;
		return new EqualsBuilder().append(reciepientPublicKey, other.reciepientPublicKey).append(value, other.value)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("reciepientPublicKey", reciepientPublicKey).append("value", value)
				.build();
	}

}
