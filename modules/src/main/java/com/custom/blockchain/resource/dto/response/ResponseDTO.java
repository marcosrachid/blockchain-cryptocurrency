package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Object data;
	private List<ResponseErrorsDTO> errors;

	public ResponseDTO() {
		super();
		this.data = null;
		this.errors = new ArrayList<ResponseErrorsDTO>();
	}

	public ResponseDTO(Object data) {
		super();
		this.data = data;
		this.errors = new ArrayList<ResponseErrorsDTO>();
	}

	public ResponseDTO(Object data, List<ResponseErrorsDTO> errors) {
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

	public List<ResponseErrorsDTO> getErrors() {
		return errors;
	}

	public void setErrors(List<ResponseErrorsDTO> errors) {
		this.errors = errors;
	}

	public static ResponseDTOBuilder createBuilder() {
		return new ResponseDTOBuilder();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(data).hashCode();
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
		return new EqualsBuilder().append(data, other.data).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("data", data).append("errors", errors).build();
	}

	public static class ResponseDTOBuilder {

		private ResponseDTO dto = null;

		private ResponseDTOBuilder() {
			dto = new ResponseDTO();
		}

		public ResponseDTOBuilder withError(ResponseErrorsDTO error) {
			dto.errors.add(error);
			return this;
		}

		public ResponseDTO build() {
			return dto;
		}

	}

}
