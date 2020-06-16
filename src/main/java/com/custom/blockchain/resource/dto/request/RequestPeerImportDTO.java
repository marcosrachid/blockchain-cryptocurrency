package com.custom.blockchain.resource.dto.request;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 
 * @author marcosrachid
 *
 */
public class RequestPeerImportDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotEmpty
	private String ip;

	@NotNull
	private int serverPort;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int port) {
		this.serverPort = port;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(ip).append(serverPort).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RequestPeerImportDTO other = (RequestPeerImportDTO) obj;
		return new EqualsBuilder().append(ip, other.ip).append(serverPort, other.serverPort).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("ip", ip).append("serverPort", serverPort).build();
	}

}
