package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;

public class TransferAccountDTO {
    private String accountProfilePid;
    private String accountProfileName;
    private String locationPid;
    private String locationName;
    private String accountProfileAddress;
    private String userPid;
    private String username;
    public TransferAccountDTO(){

    }
    public TransferAccountDTO(LocationAccountProfile locationAccountProfile) {
        super();
        this.accountProfilePid = accountProfilePid;
        this.accountProfileName = accountProfileName;
        this.locationPid = locationPid;
        this.locationName = locationName;
        this.accountProfileAddress = accountProfileAddress;
        this.userPid = userPid;
        this.username = username;
    }

    public String getAccountProfilePid() {
        return accountProfilePid;
    }

    public void setAccountProfilePid(String accountProfilePid) {
        this.accountProfilePid = accountProfilePid;
    }

    public String getAccountProfileName() {
        return accountProfileName;
    }

    public void setAccountProfileName(String accontProfileName) {
        this.accountProfileName = accontProfileName;
    }

    public String getLocationPid() {
        return locationPid;
    }

    public void setLocationPid(String locationPid) {
        this.locationPid = locationPid;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getAccountProfileAddress() {
        return accountProfileAddress;
    }

    public void setAccountProfileAddress(String accountProfileAddress) {
        this.accountProfileAddress = accountProfileAddress;
    }

    public String getUserPid() {
        return userPid;
    }

    public void setUserPid(String userPid) {
        this.userPid = userPid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "TransferAccountDTO{" +
                "accountProfilePid='" + accountProfilePid + '\'' +
                ", accontProfileName='" + accountProfileName + '\'' +
                ", locationPid='" + locationPid + '\'' +
                ", locationName='" + locationName + '\'' +
                ", accountProfileAddress='" + accountProfileAddress + '\'' +
                ", userPid='" + userPid + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
