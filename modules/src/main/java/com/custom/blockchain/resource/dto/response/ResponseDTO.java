package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object data;
	private List<ErrorsDTO> errors;
	
	public ResponseDTO(Object data) {
		super();
		this.data = data;
	}

	public ResponseDTO(Object data, List<ErrorsDTO> errors) {
		super();
		this.data = data;
		this.errors = errors;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public List<ErrorsDTO> getErrors() {
		return errors;
	}

	public void setErrors(List<ErrorsDTO> errors) {
		this.errors = errors;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(data).append(errors).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseDTO other = (ResponseDTO) obj;
		return new EqualsBuilder().append(data, other.data).append(errors, other.errors).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("data", data).append("errors", errors).build();
	}

}
