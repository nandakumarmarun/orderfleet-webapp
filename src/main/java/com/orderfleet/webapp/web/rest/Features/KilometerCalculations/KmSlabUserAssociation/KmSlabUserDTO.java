package com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlabUserAssociation;

import com.orderfleet.webapp.web.rest.Features.KilometerCalculations.KmSlab.KmSlab;

import javax.persistence.Column;

public class KmSlabUserDTO {

    private String slabPid;

    private String slabName;

    private double minKm;

    private double maxKm;

    private double Amount;

    private String userPid;

    private String userlogin;

    private String username;

    private long userId;

    private long companyid;

    private String companyPid;

    private String companyname;

    public KmSlabUserDTO() {
    }
    public KmSlabUserDTO(KmSlabUser kmSlabUser) {
        this.slabPid = kmSlabUser.getKmSlab().getPid();
        this.slabName = kmSlabUser.getKmSlab().getSlabName();
        this.minKm = kmSlabUser.getKmSlab().getMinKm();
        this.maxKm = kmSlabUser.getKmSlab().getMaxKm();
        this.Amount = kmSlabUser.getKmSlab().getAmount();
        this.userPid = kmSlabUser.getUser().getPid();
        this.userlogin = kmSlabUser.getUser().getLogin();
        this.username = kmSlabUser.getUser().getFirstName();
        this.userId = kmSlabUser.getUser().getId();
        this.companyid = kmSlabUser.getCompany().getId();
        this.companyPid = kmSlabUser.getCompany().getPid();
        this.companyname = kmSlabUser.getCompany().getLegalName();
    }


    public String getSlabPid() {
        return slabPid;
    }

    public void setSlabPid(String slabPid) {
        this.slabPid = slabPid;
    }

    public String getSlabName() {
        return slabName;
    }

    public void setSlabName(String slabName) {
        this.slabName = slabName;
    }

    public double getMinKm() {
        return minKm;
    }

    public void setMinKm(double minKm) {
        this.minKm = minKm;
    }

    public double getMaxKm() {
        return maxKm;
    }

    public void setMaxKm(double maxKm) {
        this.maxKm = maxKm;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getUserPid() {
        return userPid;
    }

    public void setUserPid(String userPid) {
        this.userPid = userPid;
    }

    public String getUserlogin() {
        return userlogin;
    }

    public void setUserlogin(String userlogin) {
        this.userlogin = userlogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getCompanyid() {
        return companyid;
    }

    public void setCompanyid(long companyid) {
        this.companyid = companyid;
    }

    public String getCompanyPid() {
        return companyPid;
    }

    public void setCompanyPid(String companyPid) {
        this.companyPid = companyPid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }
}
