package com.orderfleet.webapp.domain.model;

import java.util.Date;

public class DocumentLineGraphModel {
	
	private Date documentDate;
	
	private double salesOrder;
	
	private double sales;
	
	private double receipts;
	
	public Date getDocumentDate() {
		return documentDate;
	}

	public void setDocumentDate(Date documentDate) {
		this.documentDate = documentDate;
	}

	public double getSalesOrder() {
		return salesOrder;
	}

	public void setSalesOrder(double salesOrder) {
		this.salesOrder = salesOrder;
	}

	public double getSales() {
		return sales;
	}

	public void setSales(double sales) {
		this.sales = sales;
	}

	public double getReceipts() {
		return receipts;
	}

	public void setReceipts(double receipts) {
		this.receipts = receipts;
	}

}
