package com.custom.blockchain.block;

import static com.custom.blockchain.node.NodeStateManagement.BLOCKS_QUEUE;
import static com.custom.blockchain.node.NodeStateManagement.DIFFICULTY_ADJUSTMENT_BLOCK;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.configuration.properties.BlockchainProperties;
import com.custom.blockchain.data.block.BlockDB;
import com.custom.blockchain.data.block.CurrentBlockDB;
import com.custom.blockchain.data.chainstate.UTXOChainstateDB;
import com.custom.blockchain.signature.SignatureManager;
import com.custom.blockchain.transaction.RewardTransaction;
import com.custom.blockchain.transaction.SimpleTransaction;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.transaction.TransactionOutput;
import com.custom.blockchain.util.TransactionUtil;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class BlockStateManagement {

	private static final Logger LOG = LoggerFactory.getLogger(BlockStateManagement.class);

	private BlockchainProperties blockchainProperties;

	private BlockDB blockDB;

	private CurrentBlockDB currentBlockDB;

	private UTXOChainstateDB utxoChainstateDb;

	private SignatureManager signatureManager;

	private Block nextBlock;

	public BlockStateManagement(final BlockchainProperties blockchainProperties, final BlockDB blockDB,
			final CurrentBlockDB currentBlockDB, final UTXOChainstateDB utxoChainstateDb,
			final SignatureManager signatureManager) {
		this.blockchainProperties = blockchainProperties;
		this.blockDB = blockDB;
		this.currentBlockDB = currentBlockDB;
		this.utxoChainstateDb = utxoChainstateDb;
		this.signatureManager = signatureManager;
	}

	/**
	 * 
	 * @param block
	 * @throws BlockException
	 */
	public void validateBlock(Block block) throws BlockException {
		if (!BLOCKS_QUEUE.peek().getHeight().equals(block.getHeight())) {
			throw new BlockException("Block from peer was not from the expected height");
		}
		if (block.getHeight() % DIFFICULTY_ADJUSTMENT_BLOCK == 0) {
			// TODO: adjust difficulty
		} else if (!currentBlockDB.get().getRewardTransaction().getDifficulty()
				.equals(block.getRewardTransaction().getDifficulty())) {
			throw new BlockException("Block is on a different difficulty from environment");
		}
		if (!TransactionUtil.getMerkleRoot(block.getTransactions()).equals(block.getMerkleRoot())) {
			throw new BlockException("Block tree does not correspond to its merkle root");
		}
		Stream<Transaction> rewardStream = block.getTransactions().stream().filter(t -> t instanceof RewardTransaction);
		if (rewardStream.collect(Collectors.toList()).size() > 1) {
			throw new BlockException("Block has more then one reward transaction");
		}
		Optional<Transaction> optionalReward = rewardStream.findFirst();
		if (!optionalReward.isPresent()) {
			throw new BlockException("Block has no reward transaction");
		}
		Transaction reward = optionalReward.get();
		if (reward.getValue().equals(blockchainProperties.getReward())) {
			throw new BlockException("Block has an unexpected reward value[" + reward.getValue() + "]");
		}

		Set<Transaction> transactions = block.getTransactions().stream().filter(t -> t instanceof SimpleTransaction)
				.collect(Collectors.toSet());
		for (Transaction t : transactions) {
			SimpleTransaction transaction = (SimpleTransaction) t;

			if (transaction.getValue().compareTo(blockchainProperties.getMinimunTransaction()) < 0) {
				throw new BlockException(
						"Identified transaction[" + transaction.getTransactionId() + "] with low sent funds");
			}

			if (transaction.getInputsValue().compareTo(transaction.getOutputsValue()) != 0) {
				throw new BlockException("Identified transaction[" + transaction.getTransactionId()
						+ "] with  Inputs total[" + transaction.getInputsValue().toPlainString()
						+ "] value that differs from Transaction Outputs total["
						+ transaction.getOutputsValue().toPlainString() + "] value");
			}

			Optional<TransactionOutput> leftOverTransaction = transaction.getOutputs().stream()
					.filter(o -> o.getReciepient().equals(transaction.getSender())).findFirst();
			if (!leftOverTransaction.isPresent()) {
				throw new BlockException("Identified transaction[" + transaction.getTransactionId()
						+ "] has no left over transaction from sender");
			}

			if (!leftOverTransaction.get().getValue()
					.equals(transaction.getInputsValue().subtract(transaction.getOutputsValue()))) {
				throw new BlockException("Identified transaction[" + transaction.getTransactionId()
						+ "] has a unexpected leftOver output value[" + leftOverTransaction.get().getValue() + "]");
			}

			if (signatureManager.verifySignature(transaction) == false) {
				throw new BlockException("Identified transaction[" + transaction.getTransactionId()
						+ "] with Signature failed to verify");
			}
		}
	}

	/**
	 * 
	 * @param block
	 * @throws BlockException
	 */
	public void foundBlock(Block block) {
		LOG.info("[Crypto] Found new block: " + block);
		blockDB.put(block.getHash(), block);
		currentBlockDB.put(block);
		utxo(block.getTransactions());
		nextBlock = BlockFactory.getBlock(block);
	}

	/**
	 * 
	 * @return
	 */
	public Block getNextBlock() {
		Block currentBlock = currentBlockDB.get();
		LOG.trace("[Crypto] Retrieving current Block: " + currentBlock);
		if (nextBlock == null) {
			nextBlock = BlockFactory.getBlock(currentBlock);
		}
		return nextBlock;
	}

	/**
	 * 
	 * @param transactions
	 */
	private void utxo(Set<Transaction> transactions) {
		for (Transaction transaction : transactions) {
			if (transaction instanceof SimpleTransaction)
				addToUtxo((SimpleTransaction) transaction);
			if (transaction instanceof RewardTransaction)
				addToUtxo((RewardTransaction) transaction);
		}
	}

	/**
	 * 
	 * @param transaction
	 */
	private void addToUtxo(SimpleTransaction transaction) {
		TransactionOutput leftOverTransaction = transaction.getOutputs().stream()
				.filter(o -> o.getReciepient().equals(transaction.getSender())).findFirst().get();
		utxoChainstateDb.leftOver(leftOverTransaction.getReciepient(), leftOverTransaction);

		for (TransactionOutput output : transaction.getOutputs().stream()
				.filter(o -> !o.getReciepient().equals(transaction.getSender())).collect(Collectors.toList())) {
			utxoChainstateDb.add(output.getReciepient(), output);
		}
	}

	/**
	 * 
	 * @param transaction
	 */
	private void addToUtxo(RewardTransaction transaction) {
		TransactionOutput output = transaction.getOutput();
		utxoChainstateDb.add(output.getReciepient(), output);
	}

}
