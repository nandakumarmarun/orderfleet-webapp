package com.orderfleet.webapp.web.rest.api.dto;

public class StockDTO {
    private String productPid;
    private String productName;
    private Double openingStock;
    private Double saledQuantity;
    private Double closingStock;
    private double damageQty;

    public StockDTO() {
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

    public Double getOpeningStock() {
        return openingStock;
    }

    public void setOpeningStock(Double openingStock) {
        this.openingStock = openingStock;
    }

    public Double getSaledQuantity() {
        return saledQuantity;
    }

    public void setSaledQuantity(Double saledQuantity) {
        this.saledQuantity = saledQuantity;
    }

    public Double getClosingStock() {
        return closingStock;
    }

    public void setClosingStock(Double closingStock) {
        this.closingStock = closingStock;
    }

    public double getDamageQty() {
        return damageQty;
    }

    public void setDamageQty(double damageQty) {
        this.damageQty = damageQty;
    }

    @Override
    public String toString() {
        return "StockDTO{" +
                "productPid='" + productPid + '\'' +
                ", productName='" + productName + '\'' +
                ", openingStock=" + openingStock +
                ", saledQuantity=" + saledQuantity +
                ", closingStock=" + closingStock +
                ", damageQty=" + damageQty +
                '}';
    }
}
