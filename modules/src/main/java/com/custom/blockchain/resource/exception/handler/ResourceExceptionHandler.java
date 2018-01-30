package com.custom.blockchain.resource.exception.handler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.resource.dto.response.ErrorsDTO;
import com.custom.blockchain.resource.dto.response.ResponseDTO;
import com.custom.blockchain.transaction.exception.TransactionException;
import com.custom.blockchain.wallet.exception.WalletException;

@ControllerAdvice(basePackages = "com.custom.blockchain.resource")
public class ResourceExceptionHandler {

	@ExceptionHandler(BlockException.class)
	public ResponseEntity<ResponseDTO> handleBlockException(BlockException e) {
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON).body(
				ResponseDTO.createBuilder().withError(new ErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}
	
	@ExceptionHandler(TransactionException.class)
	public ResponseEntity<ResponseDTO> handleTransactionException(TransactionException e) {
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON).body(
				ResponseDTO.createBuilder().withError(new ErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}
	
	@ExceptionHandler(WalletException.class)
	public ResponseEntity<ResponseDTO> handleWalletException(WalletException e) {
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON).body(
				ResponseDTO.createBuilder().withError(new ErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> handleException(Exception e) {
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).contentType(APPLICATION_JSON).body(
				ResponseDTO.createBuilder().withError(new ErrorsDTO(INTERNAL_SERVER_ERROR.value(), e.getMessage())).build());
	}

}
