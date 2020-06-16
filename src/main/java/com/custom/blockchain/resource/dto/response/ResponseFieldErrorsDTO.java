package com.custom.blockchain.resource.dto.response;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author marcosrachid
 *
 */
public class ResponseFieldErrorsDTO extends ResponseErrorsDTO {

	private static final long serialVersionUID = 1L;

	private String field;

	public ResponseFieldErrorsDTO(String message) {
		super(message);
	}

	public ResponseFieldErrorsDTO(Integer code, String message) {
		super(code, message);
	}

	public ResponseFieldErrorsDTO(Integer code, String message, String field) {
		super(code, message);
		this.field = field;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getCode()).append(getMessage()).append(field).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseFieldErrorsDTO other = (ResponseFieldErrorsDTO) obj;
		return new EqualsBuilder().append(getCode(), other.getCode()).append(getMessage(), other.getMessage())
				.append(field, other.field).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("code", getCode()).append("message", getMessage())
				.append("field", field).build();
	}

}
