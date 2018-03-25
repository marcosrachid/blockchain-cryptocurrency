package com.custom.blockchain.node.network.request;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.request.arguments.GenericArguments;

public class BlockchainRequest implements Serializable {

	private static final long serialVersionUID = -2775259804853170049L;

	private String signature;
	private Service service;
	private GenericArguments arguments;

	public BlockchainRequest() {
		super();
	}

	public BlockchainRequest(String signature, Service service, GenericArguments arguments) {
		super();
		this.signature = signature;
		this.service = service;
		this.arguments = arguments;
	}

	public BlockchainRequest(String signature, Service service) {
		super();
		this.signature = signature;
		this.service = service;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public Service getService() {
		return service;
	}

	public void setService(Service service) {
		this.service = service;
	}

	public GenericArguments getArguments() {
		return arguments;
	}

	public void setArguments(GenericArguments arguments) {
		this.arguments = arguments;
	}

	public boolean hasArguments() {
		return arguments != null;
	}

	public static BlockchainRequestBuilder createBuilder() {
		return new BlockchainRequestBuilder();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(signature).append(service).append(arguments).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockchainRequest other = (BlockchainRequest) obj;
		return new EqualsBuilder().append(signature, other.signature).append(service, other.service)
				.append(arguments, other.arguments).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("signature", signature).append("service", service)
				.append("arguments", arguments).build();
	}

	public static class BlockchainRequestBuilder {

		private BlockchainRequest instance;

		private BlockchainRequestBuilder() {
			this.instance = new BlockchainRequest();
		}

		public BlockchainRequestBuilder withSignature(String signature) {
			this.instance.signature = signature;
			return this;
		}

		public BlockchainRequestBuilder withService(Service service) {
			this.instance.service = service;
			return this;
		}

		public BlockchainRequestBuilder withArguments(GenericArguments arguments) {
			this.instance.arguments = arguments;
			return this;
		}

		public BlockchainRequest build() {
			return instance;
		}

	}

}
