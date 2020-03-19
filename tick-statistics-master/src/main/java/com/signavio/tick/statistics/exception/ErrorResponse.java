package com.signavio.tick.statistics.exception;

import java.util.Date;

public class ErrorResponse {

	private Date timestamp;
	private Integer errorCode;
	private String errorDesc;

	public ErrorResponse() {
		super();
	}

	public ErrorResponse(Date timestamp, Integer errorCode, String errorDesc) {
		super();
		this.timestamp = timestamp;
		this.errorCode = errorCode;
		this.errorDesc = errorDesc;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorDesc() {
		return errorDesc;
	}

	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
	

}
