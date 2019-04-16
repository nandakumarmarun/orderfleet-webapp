package com.orderfleet.webapp.web.rest.dto;


public class InventoryClosingDetailProductDTO {
	
	private Long id;
	
	private String productProfileName;

	private double initial;
	
	private double closing;
	
	private String invntoryClosingHeaderPid;

	
	public InventoryClosingDetailProductDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public InventoryClosingDetailProductDTO(Long id, String productProfileName, double initial, double closing,
			String invntoryClosingHeaderPid) {
		super();
		this.id = id;
		this.productProfileName = productProfileName;
		this.initial = initial;
		this.closing = closing;
		this.invntoryClosingHeaderPid = invntoryClosingHeaderPid;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProductProfileName() {
		return productProfileName;
	}

	public void setProductProfileName(String productProfileName) {
		this.productProfileName = productProfileName;
	}

	public double getInitial() {
		return initial;
	}

	public void setInitial(double initial) {
		this.initial = initial;
	}

	public double getClosing() {
		return closing;
	}

	public void setClosing(double closing) {
		this.closing = closing;
	}

	public String getInvntoryClosingHeaderPid() {
		return invntoryClosingHeaderPid;
	}

	public void setInvntoryClosingHeaderPid(String invntoryClosingHeaderPid) {
		this.invntoryClosingHeaderPid = invntoryClosingHeaderPid;
	}

	@Override
	public String toString() {
		return "InventoryClosingDetailProductDTO [id=" + id + ", productProfileName=" + productProfileName + ", initial="
				+ initial + ", closing=" + closing + ", invntoryClosingHeaderPid=" + invntoryClosingHeaderPid + "]";
	}
	
	
}
