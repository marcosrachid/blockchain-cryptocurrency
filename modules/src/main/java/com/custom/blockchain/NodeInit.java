package com.custom.blockchain;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/**
 * 
 * @author marcosrachid
 *
 */
@Component
public class NodeInit {

	/**
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	@PostConstruct
	public void environment() throws NoSuchAlgorithmException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		Security.setProperty("crypto.policy", "unlimited");
	}

}
