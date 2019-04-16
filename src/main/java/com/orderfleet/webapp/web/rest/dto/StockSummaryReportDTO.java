package com.orderfleet.webapp.web.rest.dto;

import java.util.List;

/**
 * A DTO for the StockSummary report.
 * 
 * @author Shaheer
 * @since February 22, 2016
 */
public class StockSummaryReportDTO {

	private String productPid;

	private String productName;

	private double quantity;

	private List<StockLocationSummary> stockLocationSummarys;

	public StockSummaryReportDTO() {
	}

	public StockSummaryReportDTO(String productPid, String productName, double quantity) {
		super();
		this.productPid = productPid;
		this.productName = productName;
		this.quantity = quantity;
	}

	public String getProductPid() {
		return productPid;
	}

	public void setProductPid(String productPid) {
		this.productPid = productPid;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public List<StockLocationSummary> getStockLocationSummarys() {
		return stockLocationSummarys;
	}

	public void setStockLocationSummarys(List<StockLocationSummary> stockLocationSummarys) {
		this.stockLocationSummarys = stockLocationSummarys;
	}

	public class StockLocationSummary {

		private String stockLocationPid;

		private String stockLocationName;

		private double quantity;

		public StockLocationSummary(String stockLocationPid, String stockLocationName, double quantity) {
			super();
			this.stockLocationPid = stockLocationPid;
			this.stockLocationName = stockLocationName;
			this.quantity = quantity;
		}

		public String getStockLocationPid() {
			return stockLocationPid;
		}

		public void setStockLocationPid(String stockLocationPid) {
			this.stockLocationPid = stockLocationPid;
		}

		public String getStockLocationName() {
			return stockLocationName;
		}

		public void setStockLocationName(String stockLocationName) {
			this.stockLocationName = stockLocationName;
		}

		public double getQuantity() {
			return quantity;
		}

		public void setQuantity(double quantity) {
			this.quantity = quantity;
		}

	}

}
