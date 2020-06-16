package com.custom.blockchain.exception;

import org.springframework.http.HttpStatus;

/**
 * 
 * @author marcosrachid
 *
 */
public class BusinessException extends Exception {

	private static final long serialVersionUID = 1L;

	private HttpStatus status = HttpStatus.BAD_REQUEST;

	public BusinessException(String msg) {
		super(msg);
	}

	public BusinessException(HttpStatus status, String msg) {
		super(msg);
		this.status = status;
	}

	public HttpStatus getStatus() {
		return status;
	}

}
