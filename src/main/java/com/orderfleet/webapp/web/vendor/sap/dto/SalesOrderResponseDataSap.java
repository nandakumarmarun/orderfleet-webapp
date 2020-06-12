package com.orderfleet.webapp.web.vendor.sap.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "DocNum", "DocEntity", "StatusCode", "StatusMessage" })
public class SalesOrderResponseDataSap {

	@JsonProperty("DocNum")
	private String docNum;
	@JsonProperty("DocEntity")
	private String docEntity;
	@JsonProperty("StatusCode")
	private int statusCode;
	@JsonProperty("StatusMessage")
	private String statusMessage;

	@JsonProperty("DocNum")
	public String getDocNum() {
		return docNum;
	}

	@JsonProperty("DocNum")
	public void setDocNum(String docNum) {
		this.docNum = docNum;
	}

	@JsonProperty("DocEntity")
	public String getDocEntity() {
		return docEntity;
	}

	@JsonProperty("DocEntity")
	public void setDocEntity(String docEntity) {
		this.docEntity = docEntity;
	}

	@JsonProperty("StatusCode")
	public int getStatusCode() {
		return statusCode;
	}

	@JsonProperty("StatusCode")
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	@JsonProperty("StatusMessage")
	public String getStatusMessage() {
		return statusMessage;
	}

	@JsonProperty("StatusMessage")
	public void setStatusMessage(String statusMessage) {
		this.statusMessage = statusMessage;
	}

	@Override
	public String toString() {
		return "SalesOrderResponseDataSap [statusCode=" + statusCode + ", statusMessage=" + statusMessage + "]";
	}

}
