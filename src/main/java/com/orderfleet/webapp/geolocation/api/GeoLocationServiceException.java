package com.orderfleet.webapp.geolocation.api;

public class GeoLocationServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Constructs a LocationAPIException with the given detail message.
	 * 
	 * @param message
	 *            The detail message of the LocationAPIException.
	 */
	public GeoLocationServiceException(String message) {
		super(message);
	}

	/**
	 * Constructs a LocationAPIException with the given root cause.
	 * 
	 * @param cause
	 *            The root cause of the LocationAPIException.
	 */
	public GeoLocationServiceException(Throwable cause) {
		super(cause);
	}

	/**
	 * Constructs a LocationAPIException with the given detail message and root
	 * cause.
	 * 
	 * @param message
	 *            The detail message of the LocationAPIException.
	 * @param cause
	 *            The root cause of the LocationAPIException.
	 */
	public GeoLocationServiceException(String message, Throwable cause) {
		super(message, cause);
	}

}
