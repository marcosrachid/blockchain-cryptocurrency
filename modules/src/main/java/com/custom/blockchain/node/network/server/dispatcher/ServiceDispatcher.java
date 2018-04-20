package com.custom.blockchain.node.network.server.dispatcher;

import static com.custom.blockchain.constants.SystemConstants.MAX_NETWORK_SIZE_PACKAGE;
import static com.custom.blockchain.peer.PeerStateManagement.PEERS;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.iq80.leveldb.DBIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.AbstractBlock;
import com.custom.blockchain.block.BlockStateManagement;
import com.custom.blockchain.block.PropertiesBlock;
import com.custom.blockchain.block.TransactionsBlock;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.chainstate.CurrentBlockChainstateDB;
import com.custom.blockchain.data.chainstate.CurrentPropertiesChainstateDB;
import com.custom.blockchain.data.mempool.MempoolDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.exception.ForkException;
import com.custom.blockchain.node.NodeStateManagement;
import com.custom.blockchain.node.component.ForcedNodeFork;
import com.custom.blockchain.node.network.server.request.BlockchainRequest;
import com.custom.blockchain.node.network.server.request.arguments.BlockArguments;
import com.custom.blockchain.node.network.server.request.arguments.BlockResponseArguments;
import com.custom.blockchain.node.network.server.request.arguments.InvalidBlockArguments;
import com.custom.blockchain.node.network.server.request.arguments.PeerResponseArguments;
import com.custom.blockchain.node.network.server.request.arguments.StateResponseArguments;
import com.custom.blockchain.node.network.server.request.arguments.TransactionsResponseArguments;
import com.custom.blockchain.peer.Peer;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.util.ConnectionUtil;
import com.custom.blockchain.util.ObjectUtil;
import com.custom.blockchain.util.PeerUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class ServiceDispatcher {

	private static final Logger LOG = LoggerFactory.getLogger(ServiceDispatcher.class);

	private BlockchainProperties blockchainProperties;

	private BlockDB blockDB;

	private CurrentBlockChainstateDB currentBlockDB;

	private CurrentPropertiesChainstateDB currentPropertiesBlockDB;

	private MempoolDB mempoolDB;

	private BlockStateManagement blockStateManagement;

	private ForcedNodeFork nodeFork;

	public ServiceDispatcher(final BlockchainProperties blockchainProperties, final BlockDB blockDB,
			final CurrentBlockChainstateDB currentBlockDB, final CurrentPropertiesChainstateDB currentPropertiesBlockDB,
			final MempoolDB mempoolDB, final BlockStateManagement blockStateManagement, final ForcedNodeFork nodeFork) {
		this.blockchainProperties = blockchainProperties;
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.mempoolDB = mempoolDB;
		this.blockStateManagement = blockStateManagement;
		this.nodeFork = nodeFork;
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param request
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public void launch(final OutputStream sender, Peer peer, BlockchainRequest request) throws IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		LOG.trace("[Crypto] Service: " + request.getService() + ", Arguments: " + request.getArguments());
		if (ConnectionUtil.isPeerConnectionsFull(blockchainProperties.getNetworkMaximumSeeds())
				&& !ConnectionUtil.getConnectedPeers().contains(peer)) {
			LOG.trace("[Crypto] Connections peer pool is full");
			return;
		}
		if (request.hasArguments()) {
			LOG.trace("[Crypto] Request with arguments: " + request.getService().getService() + " - "
					+ request.getArguments());
			this.getClass().getDeclaredMethod(request.getService().getService(), OutputStream.class, Peer.class,
					request.getArguments().getClass()).invoke(this, sender, peer, request.getArguments());
		} else {
			LOG.trace("[Crypto] Request without arguments: " + request.getService().getService());
			this.getClass().getDeclaredMethod(request.getService().getService(), OutputStream.class, Peer.class)
					.invoke(this, sender, peer);
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getState(OutputStream sender, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_STATE.getService() + " event from peer [" + peer + "]");
		AbstractBlock currentBlock = currentBlockDB.get();
		PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(), blockchainProperties.getNetworkPort(),
				sender,
				BlockchainRequest.createBuilder().withService(Service.GET_STATE_RESPONSE)
						.withArguments(new StateResponseArguments(currentBlock.getHeight(), currentBlock.getHash(),
								currentBlock.getTimeStamp()))
						.build());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getStateResponse(OutputStream sender, Peer peer, StateResponseArguments args) {
		LOG.trace("[Crypto] Found a " + Service.GET_STATE_RESPONSE.getService() + " event from peer [" + peer + "]");
		Long peerCurrentBlock = args.getCurrentBlock();
		LOG.debug("[Crypto] peer [" + peer + "] current block [" + peerCurrentBlock + "]");
		AbstractBlock currentBlock = currentBlockDB.get();
		NodeStateManagement.updateIfBigger(peerCurrentBlock);
		if (peerCurrentBlock > currentBlock.getHeight()) {
			LOG.info("[Crypto] Found new block from peer [" + peer + "], requesting block...");
			PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(), blockchainProperties.getNetworkPort(),
					sender, BlockchainRequest.createBuilder().withService(Service.GET_BLOCK)
							.withArguments(new BlockArguments(currentBlock.getHeight() + 1, peerCurrentBlock)).build());
		} else if (peerCurrentBlock.equals(currentBlock.getHeight())
				&& !currentBlock.getHash().equals(args.getHash())) {
			discardingBlock(sender, currentBlock, currentBlock.getTimeStamp().compareTo(args.getTimestamp()),
					currentBlock.getHeight());
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlock(OutputStream sender, Peer peer, BlockArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK.getService() + " event from peer [" + peer + "]");
		List<AbstractBlock> blocks = new ArrayList<>();
		for (long i = args.getStartHeight(); i <= args.getPeerHeight(); i++) {
			blocks.add(blockDB.get(i));
			try {
				if (ObjectUtil.sizeof(blocks) > MAX_NETWORK_SIZE_PACKAGE)
					break;
			} catch (IOException e) {
				LOG.error("[Crypto] Could not get blocks to be sent size: " + e.getMessage(), e);
			}
		}
		LOG.debug("[Crypto] Found block[" + blocks + "] to be sent");
		PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(), blockchainProperties.getNetworkPort(),
				sender, BlockchainRequest.createBuilder().withService(Service.GET_BLOCK_RESPONSE)
						.withArguments(new BlockResponseArguments(blocks)).build());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getBlockResponse(OutputStream sender, Peer peer, BlockResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_BLOCK_RESPONSE.getService() + " event from peer [" + peer + "]");
		Collection<AbstractBlock> blocks = args.getBlocks();
		Long currentMappedHeight = currentBlockDB.get().getHeight();
		for (AbstractBlock block : blocks) {
			try {
				if (block.getHeight().equals(currentMappedHeight)) {
					LOG.debug("[Crypto] block already found");
					continue;
				}
				if (block instanceof PropertiesBlock) {
					blockStateManagement.foundBlock((PropertiesBlock) block);
					return;
				}
				blockStateManagement.validateBlock((TransactionsBlock) block);
				blockStateManagement.foundBlock((TransactionsBlock) block);
				if (nodeFork.checkFork(block.getHeight())) {
					blockStateManagement.foundBlock(nodeFork.pollFork());
				}
			} catch (ForkException e) {
				LOG.info("Fork identified on Block[" + block + "]");
				discardingBlock(sender, block, e.getMyBlockDiscarded(), e.getHeightBranchToRemove());
				break;
			} catch (BusinessException e) {
				LOG.error("[Crypto] Block[" + block + "] was identified as invalid: " + e.getMessage(), e);
				LOG.info("[Crypto] Discarded block[" + block + "]");
				PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(),
						blockchainProperties.getNetworkPort(), sender,
						BlockchainRequest.createBuilder().withService(Service.GET_INVALID_BLOCK)
								.withArguments(new InvalidBlockArguments(block.getHeight())).build());
				break;
			}
		}
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getInvalidBlock(OutputStream sender, Peer peer, InvalidBlockArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_INVALID_BLOCK.getService() + " event from peer [" + peer + "]");
		blockStateManagement.removeBlockBranch(args.getHeight());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getPeers(OutputStream sender, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS.getService() + " event from peer [" + peer + "]");
		PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(), blockchainProperties.getNetworkPort(),
				sender,
				BlockchainRequest.createBuilder().withService(Service.GET_PEERS_RESPONSE)
						.withArguments(new PeerResponseArguments(ConnectionUtil.getConnectedPeers().stream()
								.filter(p -> !p.equals(peer)).collect(Collectors.toSet())))
						.build());
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getPeersResponse(OutputStream sender, Peer peer, PeerResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_PEERS_RESPONSE.getService() + " event from peer [" + peer + "]");
		Set<Peer> requestPeers = args.getPeers();
		requestPeers.removeAll(PEERS);
		PEERS.addAll(requestPeers);
	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 */
	@SuppressWarnings("unused")
	private void getTransactions(OutputStream sender, Peer peer) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS.getService() + " event from peer [" + peer + "]");
		AbstractBlock currentBlock = currentBlockDB.get();
		if (!NodeStateManagement.isSynchronized(currentBlock.getHeight())) {
			LOG.debug("[Crypto] Node is not synchronized to send transactions");
			return;
		}
		Set<SimpleTransaction> mempoolTransactions = new HashSet<>();
		DBIterator iterator = mempoolDB.iterator();
		while (iterator.hasNext()) {
			mempoolTransactions.add(mempoolDB.next(iterator));
		}
		PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(), blockchainProperties.getNetworkPort(),
				sender, BlockchainRequest.createBuilder().withService(Service.GET_TRANSACTIONS_RESPONSE)
						.withArguments(new TransactionsResponseArguments(mempoolTransactions)).build());

	}

	/**
	 * 
	 * @param sender
	 * @param peer
	 * @param args
	 */
	@SuppressWarnings("unused")
	private void getTransactionsResponse(OutputStream sender, Peer peer, TransactionsResponseArguments args) {
		LOG.debug("[Crypto] Found a " + Service.GET_TRANSACTIONS_RESPONSE.getService() + " event from peer [" + peer
				+ "]");
		for (SimpleTransaction t : args.getTransactions()) {
			mempoolDB.put(t.getTransactionId(), t);
		}
	}

	/**
	 * 
	 * @param sender
	 * @param block
	 * @param compare
	 * @param height
	 */
	private void discardingBlock(OutputStream sender, AbstractBlock block, Integer compare, Long height) {
		LOG.info("[Crypto] Fork identified on Block[" + block + "]");
		switch (compare) {
		case -1:
			PeerUtil.send(currentPropertiesBlockDB.get().getNetworkSignature(), blockchainProperties.getNetworkPort(),
					sender, BlockchainRequest.createBuilder().withService(Service.GET_INVALID_BLOCK)
							.withArguments(new InvalidBlockArguments(height)).build());
			break;
		case 0:
		case 1:
			blockStateManagement.removeBlockBranch(height);
			break;
		}
	}

}
