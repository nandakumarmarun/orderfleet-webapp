package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Dealer {

	  @JsonProperty("contact") 
	    public String getContact() { 
			 return this.contact; } 
	    public void setContact(String contact) { 
			 this.contact = contact; } 
	    String contact;
	    @JsonProperty("distcode") 
	    public String getDistcode() { 
			 return this.distcode; } 
	    public void setDistcode(String distcode) { 
			 this.distcode = distcode; } 
	    String distcode;
	    @JsonProperty("dlrcode") 
	    public String getDlrcode() { 
			 return this.dlrcode; } 
	    public void setDlrcode(String dlrcode) { 
			 this.dlrcode = dlrcode; } 
	    String dlrcode;
	    @JsonProperty("dlrname") 
	    public String getDlrname() { 
			 return this.dlrname; } 
	    public void setDlrname(String dlrname) { 
			 this.dlrname = dlrname; } 
	    String dlrname;
	    @JsonProperty("door") 
	    public String getDoor() { 
			 return this.door; } 
	    public void setDoor(String door) { 
			 this.door = door; } 
	    String door;
	    @JsonProperty("landmark") 
	    public String getLandmark() { 
			 return this.landmark; } 
	    public void setLandmark(String landmark) { 
			 this.landmark = landmark; } 
	    String landmark;
	    @JsonProperty("pin") 
	    public String getPin() { 
			 return this.pin; } 
	    public void setPin(String pin) { 
			 this.pin = pin; } 
	    String pin;
	    @JsonProperty("place") 
	    public String getPlace() { 
			 return this.place; } 
	    public void setPlace(String place) { 
			 this.place = place; } 
	    String place;
	    @JsonProperty("state") 
	    public String getState() { 
			 return this.state; } 
	    public void setState(String state) { 
			 this.state = state; } 
	    String state;
	    @JsonProperty("street") 
	    public String getStreet() { 
			 return this.street; } 
	    public void setStreet(String street) { 
			 this.street = street; } 
	    String street;
	    @JsonProperty("telno") 
	    public String getTelno() { 
			 return this.telno; } 
	    public void setTelno(String telno) { 
			 this.telno = telno; } 
	    String telno;
	    @JsonProperty("town") 
	    public String getTown() { 
			 return this.town; } 
	    public void setTown(String town) { 
			 this.town = town; } 
	    String town;
}
