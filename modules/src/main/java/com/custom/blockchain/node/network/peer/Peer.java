package com.custom.blockchain.node.network.peer;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.node.network.Service;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author marcosrachid
 *
 */
public class Peer implements Serializable {

	private static final long serialVersionUID = 1L;

	private String ip;
	private int serverPort;
	private LocalDateTime createDatetime;
	private LocalDateTime lastConnected;

	@JsonIgnore
	private List<Service> services;

	public Peer() {
		this.services = new ArrayList<>();
	}

	public Peer(String ip, int serverPort) {
		this.ip = ip;
		this.serverPort = serverPort;
		this.services = new ArrayList<>();
	}

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

	public LocalDateTime getCreateDatetime() {
		return createDatetime;
	}

	public void setCreateDatetime(LocalDateTime createDatetime) {
		this.createDatetime = createDatetime;
	}

	public LocalDateTime getLastConnected() {
		return lastConnected;
	}

	public void setLastConnected(LocalDateTime lastConnected) {
		this.lastConnected = lastConnected;
	}

	public void addService(Service service) {
		this.services.add(service);
	}

	public void removeService(Service service) {
		this.services.remove(service);
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
		Peer other = (Peer) obj;
		return new EqualsBuilder().append(ip, other.ip).append(serverPort, other.serverPort).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("ip", ip).append("serverPort", serverPort).build();
	}

}