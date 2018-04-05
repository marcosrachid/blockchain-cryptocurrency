package com.custom.blockchain.node.network.server.request.arguments;

import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.custom.blockchain.peer.Peer;

public class PeerResponseArguments implements GenericArguments {

	private static final long serialVersionUID = 1L;

	private Set<Peer> peers;

	public PeerResponseArguments() {
		super();
	}

	public PeerResponseArguments(Set<Peer> peers) {
		super();
		this.peers = peers;
	}

	public Set<Peer> getPeers() {
		return peers;
	}

	public void setPeers(Set<Peer> peers) {
		this.peers = peers;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(peers).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PeerResponseArguments other = (PeerResponseArguments) obj;
		return new EqualsBuilder().append(peers, other.peers).isEquals();
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("peers", peers).build();
	}

}
