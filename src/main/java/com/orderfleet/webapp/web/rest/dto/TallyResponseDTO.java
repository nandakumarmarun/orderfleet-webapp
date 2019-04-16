package com.orderfleet.webapp.web.rest.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * a TallyResponseDTO.
 *
 * @author Sarath
 * @since Mar 4, 2017
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class TallyResponseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String status;

	private String description;

	private Object body;

	public TallyResponseDTO() {
		super();
	}

	public TallyResponseDTO(String status, String description, Object body) {
		super();
		this.status = status;
		this.description = description;
		this.body = body;
	}

	public String getStatus() {
		return status;
	}

	public TallyResponseDTO setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public TallyResponseDTO setDescription(String description) {
		this.description = description;
		return this;
	}

	public Object getBody() {
		return body;
	}

	public TallyResponseDTO setBody(Object body) {
		this.body = body;
		return this;
	}

	@Override
	public String toString() {
		return "TallyResponse [status=" + status + ", description=" + description + ", body=" + body + "]";
	}

}