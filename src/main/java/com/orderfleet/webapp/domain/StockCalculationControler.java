package com.orderfleet.webapp.domain;

import java.time.LocalDateTime;

public class StockCalculationControler {

    private long id;

    private String pid;
    private String ProductPid;

    private String productid;

    private String ProductName;

    private String sourceStockLoation;

    private String destinationStockLoation;

    private LocalDateTime createdate;

    private LocalDateTime lastModifiedDate;

    private LocalDateTime Orderdate;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getProductPid() {
        return ProductPid;
    }

    public void setProductPid(String productPid) {
        ProductPid = productPid;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getSourceStockLoation() {
        return sourceStockLoation;
    }

    public void setSourceStockLoation(String sourceStockLoation) {
        this.sourceStockLoation = sourceStockLoation;
    }

    public String getDestinationStockLoation() {
        return destinationStockLoation;
    }

    public void setDestinationStockLoation(String destinationStockLoation) {
        this.destinationStockLoation = destinationStockLoation;
    }

    public LocalDateTime getCreatedate() {
        return createdate;
    }

    public void setCreatedate(LocalDateTime createdate) {
        this.createdate = createdate;
    }

    public LocalDateTime getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public LocalDateTime getOrderdate() {
        return Orderdate;
    }

    public void setOrderdate(LocalDateTime orderdate) {
        Orderdate = orderdate;
    }
}
