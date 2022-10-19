package com.orderfleet.webapp.web.rest.api.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReleaseInfo {
	
	  
	    public String releaseNumber;
	   
	    public String releaseDate;
	    public List<String> features;
	    
	    
		public String getReleaseNumber() {
			return releaseNumber;
		}
		public void setReleaseNumber(String releaseNumber) {
			this.releaseNumber = releaseNumber;
		}
		public String getReleaseDate() {
			return releaseDate;
		}
		public void setReleaseDate(String releaseDate) {
			this.releaseDate = releaseDate;
		}
		public List<String> getFeatures() {
			return features;
		}
		public void setFeatures(List<String> features) {
			this.features = features;
		}
		    
}
