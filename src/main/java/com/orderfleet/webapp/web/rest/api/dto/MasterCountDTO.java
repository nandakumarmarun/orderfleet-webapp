package com.orderfleet.webapp.web.rest.api.dto;

public class MasterCountDTO {

    private Integer accountSize;

    private Integer productSize;

    private Integer activitySize;

    public Integer getAccountSize() {
        return accountSize;
    }

    public MasterCountDTO() {
    }

    public MasterCountDTO(Integer accountSize, Integer productSize, Integer activitySize) {
        this.accountSize = accountSize;
        this.productSize = productSize;
        this.activitySize = activitySize;
    }

    public void setAccountSize(Integer accountSize) {
        this.accountSize = accountSize;
    }

    public Integer getProductSize() {
        return productSize;
    }

    public void setProductSize(Integer productSize) {
        this.productSize = productSize;
    }

    public Integer getActivitySize() {
        return activitySize;
    }

    public void setActivitySize(Integer activitySize) {
        this.activitySize = activitySize;
    }

    @Override
    public String toString() {
        return "MasterCountDTO{" +
                "accountSize=" + accountSize +
                ", productSize=" + productSize +
                ", activitySize=" + activitySize +
                '}';
    }
}
