package com.custom.blockchain.block;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.DIFFICULTY_ADJUSTMENT_BLOCK;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.block.CurrentPropertiesBlockDB;
import com.custom.blockchain.exception.BusinessException;
import com.custom.blockchain.exception.ForkException;
import com.custom.blockchain.node.component.DifficultyAdjustment;
import com.custom.blockchain.node.network.server.SocketThread;
import com.custom.blockchain.node.network.server.request.arguments.BlockArguments;
import com.custom.blockchain.service.BlockService;
import com.custom.blockchain.service.TransactionService;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.BlockUtil;
import com.custom.blockchain.util.StringUtil;
import com.custom.blockchain.util.TransactionUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockStateManagement {

	private static final Logger LOG = LoggerFactory.getLogger(BlockStateManagement.class);

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private CurrentPropertiesBlockDB currentPropertiesBlockDB;

	private BlockService blockService;

	private TransactionService transactionService;

	private SignatureManager signatureManager;

	private DifficultyAdjustment difficultyAdjustment;

	private TransactionsBlock nextBlock;

	public BlockStateManagement(final BlockDB blockDB, final CurrentBlockDB currentBlockDB,
			final CurrentPropertiesBlockDB currentPropertiesBlockDB, final BlockService blockService,
			final TransactionService transactionService, final SignatureManager signatureManager,
			final DifficultyAdjustment difficultyAdjustment) {
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.currentPropertiesBlockDB = currentPropertiesBlockDB;
		this.blockService = blockService;
		this.transactionService = transactionService;
		this.signatureManager = signatureManager;
		this.difficultyAdjustment = difficultyAdjustment;
	}

	/**
	 * 
	 * @param block
	 * @throws BusinessException
	 * @throws ForkException
	 */
	public void validateBlock(TransactionsBlock block) throws BusinessException, ForkException {
		LOG.info("[Crypto] Starting block validation...");
		LOG.trace("[Crypto] Validating if block from peer was not from the expected height...");
		if (!BLOCKS_QUEUE.peek().getHeight().equals(block.getHeight())) {
			throw new BusinessException("Block from peer was not from the expected height");
		}
		LOG.trace("[Crypto] Validating if block is on a different difficulty from protocol...");
		TransactionsBlock currentBlock = BlockUtil.getLastTransactionBlock(blockDB, currentBlockDB.get());
		Integer difficulty = currentBlock.getRewardTransaction().getDifficulty();
		if (block.getHeight() % DIFFICULTY_ADJUSTMENT_BLOCK == 0) {
			difficulty = difficultyAdjustment.adjust();
		}
		if (!difficulty.equals(block.getRewardTransaction().getDifficulty())) {
			throw new BusinessException("Block is on a different difficulty from protocol");
		}
		LOG.trace("[Crypto] Validating if block was procedurely mined...");
		String target = StringUtil.getDificultyString(difficulty.intValue());
		if (!block.getHash().startsWith(target)) {
			throw new BusinessException("Block was not procedurely mined");
		}
		LOG.trace("[Crypto] Validating if block tree does not correspond to its merkle root...");
		if (!TransactionUtil.getMerkleRoot(block.getTransactions()).equals(block.getMerkleRoot())) {
			throw new BusinessException("Block tree does not correspond to its merkle root");
		}
		LOG.trace("[Crypto] Validating if block has more then one reward transaction...");
		List<RewardTransaction> rewardList = block.getTransactions().stream()
				.filter(t -> t instanceof RewardTransaction).map(t -> (RewardTransaction) t)
				.collect(Collectors.toList());
		if (rewardList.size() != 1) {
			throw new BusinessException("Block has one reward transaction");
		}
		LOG.trace("[Crypto] Validating if block has an unexpected reward value...");
		RewardTransaction reward = rewardList.get(0);
		PropertiesBlock propertiesBlock = currentPropertiesBlockDB.get();
		if (reward.getValue().compareTo(propertiesBlock.getReward()) != 0) {
			throw new BusinessException("Block has an unexpected reward value[" + reward.getValue() + "]");
		}

		Set<Transaction> transactions = block.getTransactions().stream().filter(t -> t instanceof SimpleTransaction)
				.collect(Collectors.toSet());
		for (Transaction t : transactions) {
			SimpleTransaction transaction = (SimpleTransaction) t;

			if (transaction.getValue().compareTo(propertiesBlock.getMinimunTransaction()) < 0) {
				throw new BusinessException(
						"Identified transaction[" + transaction.getTransactionId() + "] with low sent funds");
			}

			if (transaction.getInputsValue().compareTo(transaction.getOutputsValue()) != 0) {
				throw new BusinessException("Identified transaction[" + transaction.getTransactionId()
						+ "] with  Inputs total[" + transaction.getInputsValue().toPlainString()
						+ "] value that differs from Transaction Outputs total["
						+ transaction.getOutputsValue().toPlainString() + "] value");
			}

			Optional<TransactionOutput> leftOverTransaction = transaction.getOutputs().stream()
					.filter(o -> o.getReciepient().equals(transaction.getSender())).findFirst();
			if (!leftOverTransaction.isPresent()) {
				throw new BusinessException("Identified transaction[" + transaction.getTransactionId()
						+ "] has no left over transaction from sender");
			}

			if (!leftOverTransaction.get().getValue()
					.equals(transaction.getInputsValue().subtract(transaction.getOutputsValue()))) {
				throw new BusinessException("Identified transaction[" + transaction.getTransactionId()
						+ "] has a unexpected leftOver output value[" + leftOverTransaction.get().getValue() + "]");
			}

			if (signatureManager.verifySignature(transaction) == false) {
				throw new BusinessException("Identified transaction[" + transaction.getTransactionId()
						+ "] with Signature failed to verify");
			}
		}

		LOG.trace("[Crypto] Validating if block hash incompatible with current blockchain...");
		if (!blockService.isBlockCompatible(block)) {
			throw new ForkException(currentBlock.getTimeStamp().compareTo(block.getTimeStamp()),
					currentBlock.getHeight(), "Block hash incompatible with current blockchain");
		}
	}

	/**
	 * 
	 * @param block
	 * @throws BusinessException
	 */
	public void foundBlock(PropertiesBlock block) {
		LOG.info("[Crypto] Found new block: " + block);
		blockDB.put(block.getHeight(), block);
		currentBlockDB.put(block);
		currentPropertiesBlockDB.put(block);
		nextBlock = BlockFactory.getBlock(block, currentPropertiesBlockDB.get());
		SocketThread.inactivate();
	}

	/**
	 * 
	 * @param block
	 * @throws BusinessException
	 */
	public void foundBlock(TransactionsBlock block) {
		LOG.info("[Crypto] Found new block: " + block);
		blockDB.put(block.getHeight(), block);
		currentBlockDB.put(block);
		transactionService.addTransactionsUtxo(block.getTransactions());
		nextBlock = BlockFactory.getBlock(block, currentPropertiesBlockDB.get());
	}

	/**
	 * 
	 * @param block
	 */
	public void removeBlockBranch(Long height) {
		LOG.info("[Crypto] Removing forked blockchain branch from height: " + height);
		AbstractBlock currentBlock = currentBlockDB.get();
		BLOCKS_QUEUE.clear();
		for (long i = currentBlock.getHeight(); i >= height; i--) {
			AbstractBlock block = blockDB.get(i);
			transactionService.removeTransactionsUtxo(((TransactionsBlock) block).getTransactions());
		}
		for (long i = height; i <= currentBlock.getHeight(); i++) {
			AbstractBlock block = blockDB.get(i);
			transactionService.mempoolChargeback(((TransactionsBlock) block).getTransactions());
			blockDB.delete(i);
			BLOCKS_QUEUE.add(new BlockArguments(i));
		}
		currentBlockDB.put(blockDB.get(height - 1));
	}

	/**
	 * 
	 * @return
	 */
	public TransactionsBlock getNextBlock() {
		AbstractBlock currentBlock = currentBlockDB.get();
		LOG.trace("[Crypto] Retrieving current Block: " + currentBlock);
		if (nextBlock == null) {
			nextBlock = BlockFactory.getBlock(currentBlock, currentPropertiesBlockDB.get());
			nextBlock.setHash(blockService.calculateHash(nextBlock));
		}
		return nextBlock;
	}

}
