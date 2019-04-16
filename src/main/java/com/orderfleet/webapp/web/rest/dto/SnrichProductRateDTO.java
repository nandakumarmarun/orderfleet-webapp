package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.SnrichProductRate;
import com.orderfleet.webapp.domain.enums.OrderProPaymentMode;

public class SnrichProductRateDTO {

	private String pid;
	private String snrichProductPid;
	private String snrichProductName;
	private OrderProPaymentMode orderProPaymentMode;
	private double rate;
	
	
	public SnrichProductRateDTO() {
		super();
	}
	
	
	public SnrichProductRateDTO(SnrichProductRate snrichProductRate) {
		super();
		this.pid = snrichProductRate.getPid();
		this.snrichProductPid = snrichProductRate.getSnrichProduct().getPid();
		this.snrichProductName = snrichProductRate.getSnrichProduct().getName();
		this.orderProPaymentMode = snrichProductRate.getOrderProPaymentMode();
		this.rate = snrichProductRate.getRate();
	}



	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getSnrichProductPid() {
		return snrichProductPid;
	}
	public void setSnrichProductPid(String snrichProductPid) {
		this.snrichProductPid = snrichProductPid;
	}
	public String getSnrichProductName() {
		return snrichProductName;
	}
	public void setSnrichProductName(String snrichProductName) {
		this.snrichProductName = snrichProductName;
	}
	public OrderProPaymentMode getOrderProPaymentMode() {
		return orderProPaymentMode;
	}
	public void setOrderProPaymentMode(OrderProPaymentMode orderProPaymentMode) {
		this.orderProPaymentMode = orderProPaymentMode;
	}
	public double getRate() {
		return rate;
	}
	public void setRate(double rate) {
		this.rate = rate;
	}
	
	
}
