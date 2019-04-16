package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 *
 * @author Sarath
 * @since Nov 14, 2016
 */
public class TourDetails {
	private String voucherNo;
	private Long voucherId;
	private String tourDate;
	private String placeCode;
	private String customerCode;
	private String repCode;
	private int newDealer;
	private int newOrder;
	private String orderRemarkCode;
	private String orderRemarkName;
	private String placeName;
	private String payment;
	private String paymentRemarkCode;
	private String paymentRemarkName;
	private String specialRemark;
	private List<TourOrderDTO> tourOrderList;
	private List<TourPaymentDTO> tourPaymentList;

	public TourDetails() {
		super();
	}

	public TourDetails(String voucherNo, Long voucherId, String tourDate, String placeCode, String customerCode,
			String repCode, int newDealer, int newOrder, String orderRemarkCode, String orderRemarkName,
			String placeName, String payment, String paymentRemarkCode, String paymentRemarkName, String specialRemark,
			List<TourOrderDTO> tourOrderList, List<TourPaymentDTO> tourPaymentList) {
		super();
		this.voucherNo = voucherNo;
		this.voucherId = voucherId;
		this.tourDate = tourDate;
		this.placeCode = placeCode;
		this.customerCode = customerCode;
		this.repCode = repCode;
		this.newDealer = newDealer;
		this.newOrder = newOrder;
		this.orderRemarkCode = orderRemarkCode;
		this.orderRemarkName = orderRemarkName;
		this.placeName = placeName;
		this.payment = payment;
		this.paymentRemarkCode = paymentRemarkCode;
		this.paymentRemarkName = paymentRemarkName;
		this.specialRemark = specialRemark;
		this.tourOrderList = tourOrderList;
		this.tourPaymentList = tourPaymentList;
	}

	public String getVoucherNo() {
		return voucherNo;
	}

	public void setVoucherNo(String voucherNo) {
		this.voucherNo = voucherNo;
	}

	public Long getVoucherId() {
		return voucherId;
	}

	public void setVoucherId(Long voucherId) {
		this.voucherId = voucherId;
	}

	public String getTourDate() {
		return tourDate;
	}

	public void setTourDate(String tourDate) {
		this.tourDate = tourDate;
	}

	public String getPlaceCode() {
		return placeCode;
	}

	public void setPlaceCode(String placeCode) {
		this.placeCode = placeCode;
	}

	public String getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
	}

	public String getRepCode() {
		return repCode;
	}

	public void setRepCode(String repCode) {
		this.repCode = repCode;
	}

	public int getNewDealer() {
		return newDealer;
	}

	public void setNewDealer(int newDealer) {
		this.newDealer = newDealer;
	}

	public int getNewOrder() {
		return newOrder;
	}

	public void setNewOrder(int newOrder) {
		this.newOrder = newOrder;
	}

	public String getOrderRemarkCode() {
		return orderRemarkCode;
	}

	public void setOrderRemarkCode(String orderRemarkCode) {
		this.orderRemarkCode = orderRemarkCode;
	}

	public String getOrderRemarkName() {
		return orderRemarkName;
	}

	public void setOrderRemarkName(String orderRemarkName) {
		this.orderRemarkName = orderRemarkName;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getPayment() {
		return payment;
	}

	public void setPayment(String payment) {
		this.payment = payment;
	}

	public String getPaymentRemarkCode() {
		return paymentRemarkCode;
	}

	public void setPaymentRemarkCode(String paymentRemarkCode) {
		this.paymentRemarkCode = paymentRemarkCode;
	}

	public String getPaymentRemarkName() {
		return paymentRemarkName;
	}

	public void setPaymentRemarkName(String paymentRemarkName) {
		this.paymentRemarkName = paymentRemarkName;
	}

	public String getSpecialRemark() {
		return specialRemark;
	}

	public void setSpecialRemark(String specialRemark) {
		this.specialRemark = specialRemark;
	}

	public List<TourOrderDTO> getTourOrderList() {
		return tourOrderList;
	}

	public void setTourOrderList(List<TourOrderDTO> tourOrderList) {
		this.tourOrderList = tourOrderList;
	}

	public List<TourPaymentDTO> getTourPaymentList() {
		return tourPaymentList;
	}

	public void setTourPaymentList(List<TourPaymentDTO> tourPaymentList) {
		this.tourPaymentList = tourPaymentList;
	}

}
