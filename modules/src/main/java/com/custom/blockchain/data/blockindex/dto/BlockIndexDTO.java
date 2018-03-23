package com.custom.blockchain.data.blockindex.dto;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class BlockIndexDTO implements Serializable {

	private static final long serialVersionUID = -8185184930400153030L;

	private String fileName;

	private Integer index;

	public BlockIndexDTO() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Integer getIndex() {
		return index;
	}

	public void setIndex(Integer index) {
		this.index = index;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(fileName).append(index).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockIndexDTO other = (BlockIndexDTO) obj;
		return new EqualsBuilder().append(fileName, other.fileName).append(index, other.index).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("fileName", fileName).append("index", index).build();
	}

}
