package com.custom.blockchain.resource.dto.request;

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
public class RequestForkDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	private Long height;

	@NotNull
	private RequestPropertiesBlockDTO propertiesBlock;

	public RequestForkDTO() {
	}

	public Long getHeight() {
		return height;
	}

	public void setHeight(Long height) {
		this.height = height;
	}

	public RequestPropertiesBlockDTO getPropertiesBlock() {
		return propertiesBlock;
	}

	public void setPropertiesBlock(RequestPropertiesBlockDTO propertiesBlock) {
		this.propertiesBlock = propertiesBlock;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(height).append(propertiesBlock).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestForkDTO other = (RequestForkDTO) obj;
		return new EqualsBuilder().append(height, other.height).append(propertiesBlock, other.propertiesBlock)
				.isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("height", height).append("propertiesBlock", propertiesBlock).build();
	}

}
