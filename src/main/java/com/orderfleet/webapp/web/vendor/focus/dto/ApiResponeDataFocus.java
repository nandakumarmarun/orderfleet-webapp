package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "Voucher_No", "iStatus", "obj", "sMessage" })
public class ApiResponeDataFocus {

	@JsonProperty("Voucher_No")
	private String voucherNo;

	@JsonProperty("iStatus")
	private int iStatus;

	@JsonProperty("obj")
	private String obj;

	@JsonProperty("sMessage")
	private String sMessage;

	@JsonProperty("Voucher_No")
	public String getVoucherNo() {
		return voucherNo;
	}

	@JsonProperty("Voucher_No")
	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	@JsonProperty("iStatus")
	public int getiStatus() {
		return iStatus;
	}

	@JsonProperty("iStatus")
	public void setiStatus(int iStatus) {
		this.iStatus = iStatus;
	}

	@JsonProperty("obj")
	public String getObj() {
		return obj;
	}

	@JsonProperty("obj")
	public void setObj(String obj) {
		this.obj = obj;
	}

	public String getsMessage() {
		return sMessage;
	}

	@JsonProperty("sMessage")
	public void setsMessage(String sMessage) {
		this.sMessage = sMessage;
	}

	@Override
	public String toString() {
		return "ApiResponeDataFocus [voucherNo=" + voucherNo + ", iStatus=" + iStatus + ", obj=" + obj + ", sMessage="
				+ sMessage + "]";
	}
}
