package com.orderfleet.webapp.web.rest.dto;

import java.util.ArrayList;
import java.util.List;

public class AttachGeoLocationView {
	
	private String rName;
	
	List<VisitGeoLocationView> visitGeoLocationViews = new ArrayList<>();
	
	List<MobileGeoLocationView> mobileGeoLocationViews = new ArrayList<>();
	
	public String getrName() {
		return rName;
	}

	public void setrName(String rName) {
		this.rName = rName;
	}

	public List<VisitGeoLocationView> getVisitGeoLocationViews() {
		return visitGeoLocationViews;
	}

	public void setVisitGeoLocationViews(List<VisitGeoLocationView> visitGeoLocationViews) {
		this.visitGeoLocationViews = visitGeoLocationViews;
	}

	public List<MobileGeoLocationView> getMobileGeoLocationViews() {
		return mobileGeoLocationViews;
	}

	public void setMobileGeoLocationViews(List<MobileGeoLocationView> mobileGeoLocationViews) {
		this.mobileGeoLocationViews = mobileGeoLocationViews;
	}
	

}
