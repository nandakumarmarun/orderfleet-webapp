package com.orderfleet.webapp.web.vendor.uncleJhon.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DealerResponseUJ {

	 @JsonProperty("data") 
	    public DealerUJ getDealerUJ() { 
			 return this.data; } 
	    public void setData(DealerUJ data) { 
			 this.data = data; } 
	    DealerUJ data;
}
