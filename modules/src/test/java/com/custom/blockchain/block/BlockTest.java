package com.custom.blockchain.block;

import static com.custom.blockchain.constants.BlockchainConstants.BLOCKCHAIN;
import static com.custom.blockchain.constants.BlockchainConstants.DIFFICULTY;

import org.junit.Test;

import com.google.gson.GsonBuilder;

public class BlockTest {

//	@Test
//	public void test() {
//		BLOCKCHAIN.add(new Block("0"));
//		System.out.println("Trying to Mine block 1... ");
//		BLOCKCHAIN.get(0).mineBlock(DIFFICULTY);
//
//		BLOCKCHAIN.add(new Block(BLOCKCHAIN.get(BLOCKCHAIN.size() - 1).hash));
//		System.out.println("Trying to Mine block 2... ");
//		BLOCKCHAIN.get(1).mineBlock(DIFFICULTY);
//
//		BLOCKCHAIN.add(new Block(BLOCKCHAIN.get(BLOCKCHAIN.size() - 1).hash));
//		System.out.println("Trying to Mine block 3... ");
//		BLOCKCHAIN.get(2).mineBlock(DIFFICULTY);
//
//		System.out.println("\nBlockchain is Valid: " + isChainValid());
//
//		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(BLOCKCHAIN);
//		System.out.println("\nThe block chain: ");
//		System.out.println(blockchainJson);
//		BLOCKCHAIN.removeIf(b -> true);
//	}
//
//	private static Boolean isChainValid() {
//		Block currentBlock;
//		Block previousBlock;
//		String hashTarget = new String(new char[DIFFICULTY]).replace('\0', '0');
//
//		for (int i = 1; i < BLOCKCHAIN.size(); i++) {
//			currentBlock = BLOCKCHAIN.get(i);
//			previousBlock = BLOCKCHAIN.get(i - 1);
//			if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
//				System.out.println("Current Hashes not equal");
//				return false;
//			}
//			if (!previousBlock.hash.equals(currentBlock.previousHash)) {
//				System.out.println("Previous Hashes not equal");
//				return false;
//			}
//			if (!currentBlock.hash.substring(0, DIFFICULTY).equals(hashTarget)) {
//				System.out.println("This block hasn't been mined");
//				return false;
//			}
//		}
//		return true;
//	}
}
