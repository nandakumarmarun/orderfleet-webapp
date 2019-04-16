package com.orderfleet.webapp.web.rest.dto;

/**
 * A DTO for the StockComsumtion report.
 * 
 * @author Shaheer
 * @since September 24, 2016
 */
public class StockConsumptionDTO {
	
	private Long productId;
	
	private String productPid;
	
    private String productName;

    private Long stockLocationId;
    
    private String stockLocationName;

    private double openingStock;
    
    private double in;

    private double out;
    
    private double closingStock;
    
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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

	public Long getStockLocationId() {
		return stockLocationId;
	}

	public void setStockLocationId(Long stockLocationId) {
		this.stockLocationId = stockLocationId;
	}

	public String getStockLocationName() {
		return stockLocationName;
	}

	public void setStockLocationName(String stockLocationName) {
		this.stockLocationName = stockLocationName;
	}

	public double getOpeningStock() {
		return openingStock;
	}

	public void setOpeningStock(double openingStock) {
		this.openingStock = openingStock;
	}

	
	public double getIn() {
		return in;
	}

	public void setIn(double in) {
		this.in = in;
	}

	public double getOut() {
		return out;
	}

	public void setOut(double out) {
		this.out = out;
	}

	public double getClosingStock() {
		return closingStock;
	}

	public void setClosingStock(double closingStock) {
		this.closingStock = closingStock;
	}

	@Override
	public String toString() {
		return "StockConsumptionDTO [productId=" + productId + ", productPid=" + productPid + ", productName="
				+ productName + ", stockLocationId=" + stockLocationId + ", stockLocationName=" + stockLocationName
				+ ", openingStock=" + openingStock + ", in=" + in + ", out=" + out + ", closingStock=" + closingStock
				+ "]";
	}

}
