package com.custom.blockchain;

import java.security.Security;
import java.util.ArrayList;

import com.custom.blockchain.block.Block;
import com.custom.blockchain.transaction.Transaction;
import com.custom.blockchain.util.TransactionUtil;
import com.custom.blockchain.wallet.Wallet;

public class Node {

	public static ArrayList<Block> blockchain = new ArrayList<Block>();
	public static int difficulty = 5;
	public static Wallet walletA;
	public static Wallet walletB;

	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		walletA = new Wallet();
		walletB = new Wallet();

		System.out.println("Private and public keys:");
		System.out.println(TransactionUtil.getStringFromKey(walletA.privateKey));
		System.out.println(TransactionUtil.getStringFromKey(walletA.publicKey));
		
		Transaction transaction = new Transaction(walletA.publicKey, walletB.publicKey, 5, null);
		transaction.generateSignature(walletA.privateKey);
		
		System.out.println("Is signature verified");
		System.out.println(transaction.verifiySignature());
	}
}
