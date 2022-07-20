package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;

/**
 * A DTO for the Document entity.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public class StockDetailsDTO implements Cloneable {

	private double openingStock;
	private String productName;
	private String productPid;
	private double saleStock;
	private double freeQnty;
	private double saledQuantity;
	private double damageQty;
	private double closingStock;
	private LocalDateTime reportingTime;
	private String employeeName;
	private List<String> productList;
	private String customerName;

	private List<StockDetailsTotalDTO> stocktotal;

	public StockDetailsDTO() {
		super();

	}

	
	public StockDetailsDTO(double openingStock, String productName, String productPid, double saleStock,
			double freeQnty, double saledQuantity, double damageQty, double closingStock, LocalDateTime reportingTime,
			String employeeName, List<StockDetailsTotalDTO> stocktotal,List<String> productList) {
		super();
		this.openingStock = openingStock;
		this.productName = productName;
		this.productPid = productPid;
		this.saleStock = saleStock;
		this.freeQnty = freeQnty;
		this.saledQuantity = saledQuantity;
		this.damageQty = damageQty;
		this.closingStock = closingStock;
		this.reportingTime = reportingTime;
		this.employeeName = employeeName;
		this.stocktotal = stocktotal;
		this.productList = productList;
	}


	public StockDetailsDTO(double openingStock, String productName, double saledQuantity, double closingStock,
			String productPid) {
		super();
		this.openingStock = openingStock;
		this.productName = productName;
		this.saledQuantity = saledQuantity;
		this.closingStock = closingStock;
		this.productPid = productPid;

	}

	public StockDetailsDTO(double openingStock, String productName, double saleStock, double freeQnty,
			double saledQuantity, double closingStock, String productPid) {
		super();
		this.openingStock = openingStock;
		this.productName = productName;
		this.saledQuantity = saledQuantity;
		this.closingStock = closingStock;
		this.productPid = productPid;
		this.saleStock = saleStock;
		this.freeQnty = freeQnty;
	}

	public double getOpeningStock() {
		return openingStock;
	}

	public void setOpeningStock(double openingStock) {
		this.openingStock = openingStock;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getSaledQuantity() {
		return saledQuantity;
	}

	public void setSaledQuantity(double saledQuantity) {
		this.saledQuantity = saledQuantity;
	}

	public double getClosingStock() {
		return closingStock;
	}

	public void setClosingStock(double closingStock) {
		this.closingStock = closingStock;
	}

	public String getProductPid() {
		return productPid;
	}

	public void setProductPid(String productPid) {
		this.productPid = productPid;
	}

	public double getSaleStock() {
		return saleStock;
	}

	public void setSaleStock(double saleStock) {
		this.saleStock = saleStock;
	}

	public double getFreeQnty() {
		return freeQnty;
	}

	public void setFreeQnty(double freeQnty) {
		this.freeQnty = freeQnty;
	}

	public double getDamageQty() {
		return damageQty;
	}

	public void setDamageQty(double damageQty) {
		this.damageQty = damageQty;
	}

	public LocalDateTime getReportingTime() {
		return reportingTime;
	}

	public void setReportingTime(LocalDateTime reportingTime) {
		this.reportingTime = reportingTime;
	}

	public String getEmployeeName() {
		return employeeName;
	}

	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}

	public List<StockDetailsTotalDTO> getStocktotal() {
		return stocktotal;
	}

	public void setStocktotal(List<StockDetailsTotalDTO> stocktotal) {
		this.stocktotal = stocktotal;
	}

	public List<String> getProductList() {
		return productList;
	}


	public void setProductList(List<String> productList) {
		this.productList = productList;
	}


	public String getCustomerName() {
		return customerName;
	}


	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}


	@Override
	public String toString() {
		return "StockDetailsDTO [productName=" + productName + ", saledQuantity=" + saledQuantity + ", damageQty="
				+ damageQty + ", employeeName=" + employeeName + ", customerName=" + customerName + "]";
	}


	


	


	

}
