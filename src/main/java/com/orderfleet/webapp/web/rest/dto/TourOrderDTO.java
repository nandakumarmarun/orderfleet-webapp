package com.orderfleet.webapp.web.rest.dto;

/**
 *
 * @author Sarath
 * @since Nov 12, 2016
 */
public class TourOrderDTO {

	private String voucherNo;
	private String itemCode;
	private double quantity;
	private double dicountPercentage;

	public TourOrderDTO() {
		super();
	}

	public TourOrderDTO(String voucherNo, String itemCode, double quantity, double dicountPercentage) {
		super();
		this.voucherNo = voucherNo;
		this.itemCode = itemCode;
		this.quantity = quantity;
		this.dicountPercentage = dicountPercentage;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getDicountPercentage() {
		return dicountPercentage;
	}

	public void setDicountPercentage(double dicountPercentage) {
		this.dicountPercentage = dicountPercentage;
	}

}
