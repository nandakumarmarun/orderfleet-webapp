package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DealerUJ {
	  @JsonProperty("main") 
	    public ArrayList<Dealer> getDealer() { 
			 return this.main; } 
	    public void setDealer(ArrayList<Dealer> main) { 
			 this.main = main; } 
	    ArrayList<Dealer> main;
	    @JsonProperty("ordkey") 
	    public ArrayList<String> getOrdkey() { 
			 return this.ordkey; } 
	    public void setOrdkey(ArrayList<String> ordkey) { 
			 this.ordkey = ordkey; } 
	    ArrayList<String> ordkey;

}
