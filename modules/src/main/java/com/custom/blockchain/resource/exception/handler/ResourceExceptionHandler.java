package com.custom.blockchain.resource.exception.handler;

import static com.custom.blockchain.costants.LogMessagesConstants.ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.custom.blockchain.block.exception.BlockException;
import com.custom.blockchain.node.network.peer.exception.PeerException;
import com.custom.blockchain.resource.dto.response.ResponseDTO;
import com.custom.blockchain.resource.dto.response.ResponseDTO.ResponseDTOBuilder;
import com.custom.blockchain.resource.dto.response.ResponseErrorsDTO;
import com.custom.blockchain.resource.dto.response.ResponseFieldErrorsDTO;
import com.custom.blockchain.transaction.exception.TransactionException;
import com.custom.blockchain.wallet.exception.WalletException;

/**
 * 
 * @author marcosrachid
 *
 */
@ControllerAdvice(basePackages = "com.custom.blockchain.resource")
public class ResourceExceptionHandler {

	private static final Logger LOG = LoggerFactory.getLogger(ResourceExceptionHandler.class);

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	public ResponseEntity<ResponseDTO> processMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
		LOG.debug(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		return ResponseEntity.status(METHOD_NOT_ALLOWED).body(ResponseDTO.createBuilder()
				.withError(new ResponseErrorsDTO(METHOD_NOT_ALLOWED.value(), e.getMessage())).build());
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ResponseDTO> handleFieldsException(MethodArgumentNotValidException e) {
		LOG.debug(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		ResponseDTOBuilder builder = ResponseDTO.createBuilder();
		e.getBindingResult().getFieldErrors().forEach(ex -> {
			builder.withError(new ResponseFieldErrorsDTO(BAD_REQUEST.value(), ex.getDefaultMessage(), ex.getField()));
		});
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON_UTF8).body(builder.build());
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(BlockException.class)
	public ResponseEntity<ResponseDTO> handleBlockException(BlockException e) {
		LOG.debug(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON_UTF8).body(ResponseDTO.createBuilder()
				.withError(new ResponseErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(TransactionException.class)
	public ResponseEntity<ResponseDTO> handleTransactionException(TransactionException e) {
		LOG.debug(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON_UTF8).body(ResponseDTO.createBuilder()
				.withError(new ResponseErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(WalletException.class)
	public ResponseEntity<ResponseDTO> handleWalletException(WalletException e) {
		LOG.debug(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON_UTF8).body(ResponseDTO.createBuilder()
				.withError(new ResponseErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(PeerException.class)
	public ResponseEntity<ResponseDTO> handlePeerException(PeerException e) {
		LOG.debug(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		return ResponseEntity.status(BAD_REQUEST).contentType(APPLICATION_JSON_UTF8).body(ResponseDTO.createBuilder()
				.withError(new ResponseErrorsDTO(BAD_REQUEST.value(), e.getMessage())).build());
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDTO> handleException(Exception e) {
		LOG.error(ERROR, e.getMessage(), ExceptionUtils.getStackTrace(e));
		return ResponseEntity.status(INTERNAL_SERVER_ERROR).contentType(APPLICATION_JSON_UTF8)
				.body(ResponseDTO.createBuilder()
						.withError(new ResponseErrorsDTO(INTERNAL_SERVER_ERROR.value(), e.getMessage())).build());
	}

}
