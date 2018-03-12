package com.custom.blockchain.data.exception;

/**
 * 
 * @author marcosrachid
 *
 */
public class LevelDBException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LevelDBException(String msg) {
		super(msg);
	}

}
