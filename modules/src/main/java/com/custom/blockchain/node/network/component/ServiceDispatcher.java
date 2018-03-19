package com.custom.blockchain.node.network.component;

import static com.custom.blockchain.node.network.peer.PeerStateManagement.PEERS_STATUS;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.node.network.Service;
import com.custom.blockchain.node.network.exception.NetworkException;
import com.custom.blockchain.node.network.peer.Peer;
import com.custom.blockchain.node.network.peer.component.PeerSender;
import com.custom.blockchain.node.network.peer.exception.PeerException;
import com.custom.blockchain.service.PeerService;

@Component
public class ServiceDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceDispatcher.class);

	private BlockchainProperties blockchainProperties;

	private PeerService peerService;

	private PeerSender peerSender;

	private Socket clientSocket;

	private Peer peer;

	public ServiceDispatcher(final BlockchainProperties blockchainProperties, final PeerService peerService,
			final PeerSender peerSender) {
		this.blockchainProperties = blockchainProperties;
		this.peerService = peerService;
		this.peerSender = peerSender;
	}

	public void launch(Socket clientSocket, Peer peer, String command) throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		this.clientSocket = clientSocket;
		this.peer = peer;
		this.getClass().getDeclaredMethod(command).invoke(this);
	}

	/**
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("unused")
	private void ping() {
		try {
			DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream());

			LOG.debug("[Crypto] Found a " + Service.PING.getService() + " event from peer [" + peer + "]");
			peerSender.connect(peer);
			peerSender.send(blockchainProperties.getNetworkSignature() + "#" + Service.PONG.getService());
			peerSender.close();
		} catch (IOException e) {
			throw new NetworkException(
					String.format("Could not send pong service to peer [%s]: %s", peer, e.getMessage()));
		}
		try {
			peerService.addPeer(peer);
		} catch (PeerException e) {
			throw new NetworkException(String.format("Could not register new peer [%s]: %s", peer, e.getMessage()));
		}
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void pong() {
		LOG.debug("[Crypto] Found a " + Service.PONG.getService() + " event");
		LOG.debug(String.format("[Crypto] node [%s] successfully answered", clientSocket.toString()));
		PEERS_STATUS.put(peer, true);
	}

}
