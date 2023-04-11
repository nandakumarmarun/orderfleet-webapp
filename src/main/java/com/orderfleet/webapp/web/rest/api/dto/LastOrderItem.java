package com.orderfleet.webapp.web.rest.api.dto;

public class LastOrderItem {
    private String productPid;
    private String productName;
    private double quantity;

    private String orderDate;

    public LastOrderItem() {
    }

    public String getProductPid() {
        return productPid;
    }

    public void setProductPid(String getProductPid) {
        this.productPid = getProductPid;
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
}
