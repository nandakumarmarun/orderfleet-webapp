package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountUJ {

	 @JsonProperty("address") 
	    public String getAddress() { 
			 return this.address; } 
	    public void setAddress(String address) { 
			 this.address = address; } 
	    String address;
	    @JsonProperty("city") 
	    public String getCity() { 
			 return this.city; } 
	    public void setCity(String city) { 
			 this.city = city; } 
	    String city;
	    @JsonProperty("closingBalance") 
	    public double getClosingBalance() { 
			 return this.closingBalance; } 
	    public void setClosingBalance(double closingBalance) { 
			 this.closingBalance = closingBalance; } 
	    double closingBalance;
	    @JsonProperty("code") 
	    public String getCode() { 
			 return this.code; } 
	    public void setCode(String code) { 
			 this.code = code; } 
	    String code;
	    @JsonProperty("email1") 
	    public String  getEmail1() { 
			 return this.email1; } 
	    public void setEmail1(String email1) { 
			 this.email1 = email1; } 
	    String email1;
	    @JsonProperty("gstNo") 
	    public String getGstNo() { 
			 return this.gstNo; } 
	    public void setGstNo(String gstNo) { 
			 this.gstNo = gstNo; } 
	    String gstNo;
	    @JsonProperty("location") 
	    public String getLocation() { 
			 return this.location; } 
	    public void setLocation(String location) { 
			 this.location = location; } 
	    String location;
	    @JsonProperty("name") 
	    public String getName() { 
			 return this.name; } 
	    public void setName(String name) { 
			 this.name = name; } 
	    String name;
	    @JsonProperty("phone1") 
	    public String getPhone1() { 
			 return this.phone1; } 
	    public void setPhone1(String phone1) { 
			 this.phone1 = phone1; } 
	    String phone1;
	    @JsonProperty("phone2") 
	    public String getPhone2() { 
			 return this.phone2; } 
	    public void setPhone2(String phone2) { 
			 this.phone2 = phone2; } 
	    String phone2;
	
	    @Override
		public String toString() {
			return "AccountUJ [address=" + address + ", city=" + city + ", closingBalance=" + closingBalance + ", code="
					+ code + ", email1=" + email1 + ", gstNo=" + gstNo + ", location=" + location + ", name=" + name
					+ ", phone1=" + phone1 + ", phone2=" + phone2 + "]";
		}
	    
	    
}
