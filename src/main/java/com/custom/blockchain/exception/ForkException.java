package com.custom.blockchain.exception;

public class ForkException extends Exception {

	private static final long serialVersionUID = 1L;

	private Long heightBranchToRemove;

	private int myBlockDiscarded;

	public ForkException(int myBlockDiscarded, Long heightBranchToRemove, String msg) {
		super(msg);
		this.myBlockDiscarded = myBlockDiscarded;
		this.heightBranchToRemove = heightBranchToRemove;
	}

	public int getMyBlockDiscarded() {
		return myBlockDiscarded;
	}

	public Long getHeightBranchToRemove() {
		return heightBranchToRemove;
	}

}