package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * 
 * @author marcosrachid
 *
 */
public class ResponseWalletDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String publicKey;
	private String privateKey;

	public ResponseWalletDTO(String publicKey, String privateKey) {
		super();
		this.publicKey = publicKey;
		this.privateKey = privateKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(publicKey).append(privateKey).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseWalletDTO other = (ResponseWalletDTO) obj;
		return new EqualsBuilder().append(publicKey, other.publicKey).append(privateKey, other.privateKey).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("publicKey", publicKey).build();
	}

}
