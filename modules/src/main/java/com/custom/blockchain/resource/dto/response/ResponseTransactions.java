package com.custom.blockchain.resource.dto.response;

import java.io.Serializable;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class ResponseTransactions implements Serializable {

	private static final long serialVersionUID = 1L;

	private String transactionId;
	private String senderPublicKey;
	private List<ResponseReciepientDTO> reciepientList;

	public ResponseTransactions(String transactionId, String senderPublicKey,
			List<ResponseReciepientDTO> reciepientList) {
		super();
		this.transactionId = transactionId;
		this.senderPublicKey = senderPublicKey;
		this.reciepientList = reciepientList;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public String getSenderPublicKey() {
		return senderPublicKey;
	}

	public void setSenderPublicKey(String senderPublicKey) {
		this.senderPublicKey = senderPublicKey;
	}

	public List<ResponseReciepientDTO> getReciepientList() {
		return reciepientList;
	}

	public void setReciepientList(List<ResponseReciepientDTO> reciepientList) {
		this.reciepientList = reciepientList;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactionId).append(senderPublicKey).append(reciepientList).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ResponseTransactions other = (ResponseTransactions) obj;
		return new EqualsBuilder().append(transactionId, other.transactionId)
				.append(senderPublicKey, other.senderPublicKey).append(reciepientList, other.reciepientList).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("transactionId", transactionId)
				.append("senderPublicKey", senderPublicKey).append("reciepientList", reciepientList).build();
	}

}
