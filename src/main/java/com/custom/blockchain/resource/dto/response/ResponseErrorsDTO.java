package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author marcosrachid
 *
 */
public class ResponseErrorsDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private Integer code;

	@NotNull
	private String message;

	public ResponseErrorsDTO(String message) {
		super();
		this.message = message;
	}

	public ResponseErrorsDTO(Integer code, String message) {
		super();
		this.code = code;
		this.message = message;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(code).append(message).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseErrorsDTO other = (ResponseErrorsDTO) obj;
		return new EqualsBuilder().append(code, other.code).append(message, other.message).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("code", code).append("message", message).build();
	}

}
