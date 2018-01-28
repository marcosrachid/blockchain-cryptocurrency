package com.custom.blockchain.transaction;

import static com.custom.blockchain.constants.BlockchainConstants.BLOCKCHAIN;
import static com.custom.blockchain.constants.BlockchainConstants.DIFFICULTY;
import static com.custom.blockchain.constants.BlockchainConstants.UNSPENT_TRANSACTIONS_OUTPUT;

import java.math.BigDecimal;
import java.security.Security;
import java.util.HashMap;

import org.junit.Test;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.wallet.Wallet;

public class TransactionTest {

//	public static Wallet walletA;
//	public static Wallet walletB;
//	public static Transaction genesisTransaction;
//
//	@Test
//	public void test() {
//		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
//
//		walletA = new Wallet();
//		walletB = new Wallet();
//		Wallet coinbase = new Wallet();
//
//		genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, new BigDecimal(100f), null);
//		genesisTransaction.generateSignature(coinbase.privateKey);
//		genesisTransaction.transactionId = GENESIS_TRANSACTION_ID;
//		genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.reciepient, genesisTransaction.value,
//				genesisTransaction.transactionId));
//		UNSPENT_TRANSACTIONS_OUTPUT.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
//
//		System.out.println("Creating and Mining Genesis block... ");
//		Block genesis = new Block("0");
//		genesis.addTransaction(genesisTransaction);
//		addBlock(genesis);
//
//		Block block1 = new Block(genesis.hash);
//		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
//		block1.addTransaction(walletA.sendFunds(walletB.publicKey, new BigDecimal(40f)));
//		addBlock(block1);
//		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//		System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//		Block block2 = new Block(block1.hash);
//		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
//		block2.addTransaction(walletA.sendFunds(walletB.publicKey, new BigDecimal(1000f)));
//		addBlock(block2);
//		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//		System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//		Block block3 = new Block(block2.hash);
//		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
//		block3.addTransaction(walletB.sendFunds(walletA.publicKey, new BigDecimal(20f)));
//		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
//		System.out.println("WalletB's balance is: " + walletB.getBalance());
//
//		isChainValid();
//		BLOCKCHAIN.removeIf(b -> true);
//	}
//
//	public static Boolean isChainValid() {
//		Block currentBlock;
//		Block previousBlock;
//		String hashTarget = new String(new char[DIFFICULTY]).replace('\0', '0');
//		HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
//		tempUTXOs.put(genesisTransaction.outputs.get(0).id, genesisTransaction.outputs.get(0));
//
//		for (int i = 1; i < BLOCKCHAIN.size(); i++) {
//
//			currentBlock = BLOCKCHAIN.get(i);
//			previousBlock = BLOCKCHAIN.get(i - 1);
//			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
//				System.out.println("#Current Hashes not equal");
//				return false;
//			}
//			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
//				System.out.println("#Previous Hashes not equal");
//				return false;
//			}
//			if (!currentBlock.hash.substring(0, DIFFICULTY).equals(hashTarget)) {
//				System.out.println("#This block hasn't been mined");
//				return false;
//			}
//
//			TransactionOutput tempOutput;
//			for (int t = 0; t < currentBlock.transactions.size(); t++) {
//				Transaction currentTransaction = currentBlock.transactions.get(t);
//
//				if (!currentTransaction.verifiySignature()) {
//					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
//					return false;
//				}
//				if (currentTransaction.getInputsValue().compareTo(currentTransaction.getOutputsValue()) != 0) {
//					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
//					return false;
//				}
//
//				for (TransactionInput input : currentTransaction.inputs) {
//					tempOutput = tempUTXOs.get(input.transactionOutputId);
//
//					if (tempOutput == null) {
//						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
//						return false;
//					}
//
//					if (input.unspentTransactionOutput.value != tempOutput.value) {
//						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
//						return false;
//					}
//
//					tempUTXOs.remove(input.transactionOutputId);
//				}
//
//				for (TransactionOutput output : currentTransaction.outputs) {
//					tempUTXOs.put(output.id, output);
//				}
//
//				if (currentTransaction.outputs.get(0).reciepient != currentTransaction.reciepient) {
//					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
//					return false;
//				}
//				if (currentTransaction.outputs.get(1).reciepient != currentTransaction.sender) {
//					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
//					return false;
//				}
//
//			}
//
//		}
//		System.out.println("Blockchain is valid");
//		return true;
//	}
//
//	public static void addBlock(Block newBlock) {
//		newBlock.mineBlock(DIFFICULTY);
//		BLOCKCHAIN.add(newBlock);
//	}

}
