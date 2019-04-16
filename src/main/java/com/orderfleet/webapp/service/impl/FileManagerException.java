package com.orderfleet.webapp.service.impl;

public class FileManagerException extends Exception {

	private static final long serialVersionUID = 1L;

	private String errorMessage;

	public FileManagerException(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
}