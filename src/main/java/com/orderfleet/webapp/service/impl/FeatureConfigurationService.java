package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.enums.Feature;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;

@Service
public class FeatureConfigurationService {
	
	@Autowired
	private CompanyTrialSetUpService companyTrialSetUpService;

	public void addFeature(Feature feature) {
		//later can replaced with subscription based features
		switch (feature) {
		case BASIC_ORDERPRO:
			configureBasicOrderProFeatures();
			break;
		default:
			break;
		}
	}

	public void addFeature(Set<Feature> features) {

	}

	private void configureBasicOrderProFeatures() {
		
	}

	
}
