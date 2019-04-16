package com.orderfleet.webapp.service.exception;

public class TaskSubmissionPostSaveException extends Exception {

	private static final long serialVersionUID = 1L;

	public TaskSubmissionPostSaveException(String message) {
		super(message);
	}

	public TaskSubmissionPostSaveException(String message, Throwable throwable) {
		super(message, throwable);
	}

}
