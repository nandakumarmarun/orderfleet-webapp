package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.domain.enums.CustomerTimeSpentType;
import com.orderfleet.webapp.domain.enums.LocationType;

/**
 * A DTO for the DashboardUserData.
 * 
 * @author Muhammed Riyas T
 * @since October 19, 2016
 */
public class DashboardWebSocketDataDTO {

	private String userPid;
	
	private String accountProfilePid;

	private String lastLocation;

	private String lastAccountLocation;

	private LocalDateTime lastTime;

	private List<DashboardSummaryDTO> dashboardItems;

	private LocationType locationType;
	private boolean isGpsOff;
	private boolean isMobileDataOff;
	
	private boolean decrementSkipCount;
	
	private CustomerTimeSpentType customerTimeSpentType;
	
	//reset dash board document circle
	private boolean reloadNumberCircle;

	public String getUserPid() {
		return userPid;
	}

	public void setUserPid(String userPid) {
		this.userPid = userPid;
	}

	public String getLastLocation() {
		return lastLocation;
	}

	public void setLastLocation(String lastLocation) {
		this.lastLocation = lastLocation;
	}

	public String getLastAccountLocation() {
		return lastAccountLocation;
	}

	public void setLastAccountLocation(String lastAccountLocation) {
		this.lastAccountLocation = lastAccountLocation;
	}

	public LocalDateTime getLastTime() {
		return lastTime;
	}

	public void setLastTime(LocalDateTime lastTime) {
		this.lastTime = lastTime;
	}

	public List<DashboardSummaryDTO> getDashboardItems() {
		return dashboardItems;
	}

	public void setDashboardItems(List<DashboardSummaryDTO> dashboardItems) {
		this.dashboardItems = dashboardItems;
	}

	public LocationType getLocationType() {
		return locationType;
	}

	public void setLocationType(LocationType locationType) {
		this.locationType = locationType;
	}

	public boolean getIsGpsOff() {
		return isGpsOff;
	}

	public void setIsGpsOff(boolean isGpsOff) {
		this.isGpsOff = isGpsOff;
	}

	public boolean getIsMobileDataOff() {
		return isMobileDataOff;
	}

	public void setIsMobileDataOff(boolean isMobileDataOff) {
		this.isMobileDataOff = isMobileDataOff;
	}

	public boolean getDecrementSkipCount() {
		return decrementSkipCount;
	}

	public void setDecrementSkipCount(boolean decrementSkipCount) {
		this.decrementSkipCount = decrementSkipCount;
	}

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public CustomerTimeSpentType getCustomerTimeSpentType() {
		return customerTimeSpentType;
	}

	public void setCustomerTimeSpentType(CustomerTimeSpentType customerTimeSpentType) {
		this.customerTimeSpentType = customerTimeSpentType;
	}

	public boolean getReloadNumberCircle() {
		return reloadNumberCircle;
	}

	public void setReloadNumberCircle(boolean reloadNumberCircle) {
		this.reloadNumberCircle = reloadNumberCircle;
	}
	
	

}
