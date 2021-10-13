package com.iandna.gateway.config.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommonJsonException extends RuntimeException {
	
	private static final Log logger = LogFactory.getLog (CommonJsonException.class);
	
	private String code;
	private String message;
	public CommonJsonException(String code, String message) {
		super(code);
		logger.error(code + " : " + message);
		this.code = code;
		this.message = message;
	}
	
	public String getCode() {
		return this.code;
	}
	
	public String getMessage() {
		return this.message;
	}
}
