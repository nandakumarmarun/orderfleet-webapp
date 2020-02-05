package com.orderfleet.webapp.web.rest.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.orderfleet.webapp.domain.AccountProfileGeoLocationTagging;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;


/**
 *A DTO For AccountProfileGeoLocation Entity.
 *
 * @author fahad
 * @since Jul 6, 2017
 */
public class AccountProfileGeoLocationTaggingDTO {

	private String pid;
	
	private String accountProfilePid;
	
	private String userFirstName;
	
	private BigDecimal longitude;
	
	private BigDecimal latitude;
	
	private String location;
	
	private LocalDateTime sendDate;
	
	private GeoTaggingType geoTaggingType;

	
	public AccountProfileGeoLocationTaggingDTO() {
		super();
	}


	public AccountProfileGeoLocationTaggingDTO(AccountProfileGeoLocationTagging accountProfileGeoLocationTagging) {
		super();
		this.pid=accountProfileGeoLocationTagging.getPid();
		this.accountProfilePid = accountProfileGeoLocationTagging.getAccountProfile().getPid();
		this.userFirstName = accountProfileGeoLocationTagging.getUser().getFirstName();
		this.longitude = accountProfileGeoLocationTagging.getLongitude();
		this.latitude = accountProfileGeoLocationTagging.getLatitude();
		this.location = accountProfileGeoLocationTagging.getLocation();
		this.sendDate = accountProfileGeoLocationTagging.getSendDate();
	}
	
	public AccountProfileGeoLocationTaggingDTO(String pid, String accountProfilePid, BigDecimal longitude, BigDecimal latitude,
			String location, LocalDateTime sendDate) {
		super();
		this.pid=pid;
		this.accountProfilePid = accountProfilePid;
		this.longitude = longitude;
		this.latitude = latitude;
		this.location = location;
		this.sendDate = sendDate;
	}

	
	
	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}


	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getUserFirstName() {
		return userFirstName;
	}


	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}


	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDateTime getSendDate() {
		return sendDate;
	}

	public void setSendDate(LocalDateTime sendDate) {
		this.sendDate = sendDate;
	}

	public GeoTaggingType getGeoTaggingType() {
		return geoTaggingType;
	}

	public void setGeoTaggingType(GeoTaggingType geoTaggingType) {
		this.geoTaggingType = geoTaggingType;
	}


	@Override
	public String toString() {
		return "AccountProfileGeoLocationTaggingDTO [pid=" + pid + ", accountProfilePid=" + accountProfilePid
				+ ", longitude=" + longitude + ", latitude=" + latitude + ", location=" + location + ", sendDate="
				+ sendDate + "]";
	}
	
	
}
