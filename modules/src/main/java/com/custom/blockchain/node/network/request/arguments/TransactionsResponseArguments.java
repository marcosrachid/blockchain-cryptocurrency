package com.custom.blockchain.node.network.request.arguments;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.transaction.SimpleTransaction;

public class TransactionsResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Set<SimpleTransaction> transactions;

	public TransactionsResponseArguments() {
		super();
	}

	public TransactionsResponseArguments(Set<SimpleTransaction> transactions) {
		super();
		this.transactions = transactions;
	}

	public Set<SimpleTransaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(Set<SimpleTransaction> transactions) {
		this.transactions = transactions;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(transactions).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransactionsResponseArguments other = (TransactionsResponseArguments) obj;
		return new EqualsBuilder().append(transactions, other.transactions).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("transactions", transactions).build();
	}

}
