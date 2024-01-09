package com.orderfleet.webapp.web.rest.dto;

public class InventoryData {
    private String productName;
    private double quantity;
    private double freeQuantity;
    private double sellingRate;
    private double taxPercentage;
    private double discountPercentage;
    private double rowTotal;
    private double discountAmount;
    private String remarks;

    public InventoryData() {
    }

    public InventoryData(String productName, double quantity, double freeQuantity, double sellingRate, double taxPercentage, double discountPercentage, double rowTotal, double discountAmount, String remarks) {
        this.productName = productName;
        this.quantity = quantity;
        this.freeQuantity = freeQuantity;
        this.sellingRate = sellingRate;
        this.taxPercentage = taxPercentage;
        this.discountPercentage = discountPercentage;
        this.rowTotal = rowTotal;
        this.discountAmount = discountAmount;
        this.remarks = remarks;
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

    public double getFreeQuantity() {
        return freeQuantity;
    }

    public void setFreeQuantity(double freeQuantity) {
        this.freeQuantity = freeQuantity;
    }

    public double getSellingRate() {
        return sellingRate;
    }

    public void setSellingRate(double sellingRate) {
        this.sellingRate = sellingRate;
    }

    public double getTaxPercentage() {
        return taxPercentage;
    }

    public void setTaxPercentage(double taxPercentage) {
        this.taxPercentage = taxPercentage;
    }

    public double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public double getRowTotal() {
        return rowTotal;
    }

    public void setRowTotal(double rowTotal) {
        this.rowTotal = rowTotal;
    }

    public double getDiscountAmount(double discountAmount) {
        return this.discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
