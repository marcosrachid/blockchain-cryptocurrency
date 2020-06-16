package com.custom.blockchain.resource.dto.request;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author marcosrachid
 *
 */
public class RequestSendFundsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String reciepientPublicKey;

	@NotNull
	private BigDecimal value;

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
		RequestSendFundsDTO other = (RequestSendFundsDTO) obj;
		return new EqualsBuilder().append(reciepientPublicKey, other.reciepientPublicKey).append(value, other.value)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("reciepientPublicKey", reciepientPublicKey).append("value", value)
				.build();
	}

	public static class RequestSendFundsListDTO extends ArrayList<RequestSendFundsDTO> {

		private static final long serialVersionUID = 1L;
		
	}

}
