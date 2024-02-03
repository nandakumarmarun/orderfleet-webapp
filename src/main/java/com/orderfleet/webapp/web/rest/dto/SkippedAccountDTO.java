package com.orderfleet.webapp.web.rest.dto;

public class SkippedAccountDTO {

    private String accountType;

    private String location;

    private String accountProfileName;

    private String address;

    private String geotagLocation;
    private String district;
    private String phoneNo;
    private double closingBalance;
    private String email;

    public SkippedAccountDTO() {
    }

    public SkippedAccountDTO(String accountType, String location, String accountProfileName, String address, String geotagLocation, String district, String phoneNo, double closingBalance, String email) {
        this.accountType = accountType;
        this.location = location;
        this.accountProfileName = accountProfileName;
        this.address = address;
        this.geotagLocation = geotagLocation;
        this.district = district;
        this.phoneNo = phoneNo;
        this.closingBalance = closingBalance;
        this.email = email;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAccountProfileName() {
        return accountProfileName;
    }

    public void setAccountProfileName(String accountProfileName) {
        this.accountProfileName = accountProfileName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGeotagLocation() {
        return geotagLocation;
    }

    public void setGeotagLocation(String geotagLocation) {
        this.geotagLocation = geotagLocation;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
