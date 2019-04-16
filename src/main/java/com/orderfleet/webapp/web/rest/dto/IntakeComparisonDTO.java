package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DTO for the SalesInTake.
 * 
 * @author Muhammed Riyas T
 * @since Mar 03, 2017
 */
public class IntakeComparisonDTO {

	private String productName;

	private List<IntakeComparisonMonth> months;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<IntakeComparisonMonth> getMonths() {
		return months;
	}

	public void setMonths(List<IntakeComparisonMonth> months) {
		this.months = months;
	}

	public class IntakeComparisonMonth {

		private String month;

		private double quantity;

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public double getQuantity() {
			return quantity;
		}

		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}

	}

}