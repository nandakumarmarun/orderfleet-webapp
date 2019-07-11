package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupLocationRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.SalesTargetGroupLocationService;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.mapper.LocationMapper;

/**
 * Service Implementation for managing SalesTargetGroupLocation.
 *
 * @author Sarath
 * @since Oct 14, 2016
 */

@Service
@Transactional
public class SalesTargetGroupLocationServiceImpl implements SalesTargetGroupLocationService {

	private final Logger log = LoggerFactory.getLogger(SalesTargetGroupLocationServiceImpl.class);

	@Inject
	private SalesTargetGroupLocationRepository salesTargetGroupLocationRepository;

	@Inject
	private LocationMapper locationMapper;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private LocationRepository locationRepository;

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findSalesTargetGroupLocationsBySalesTargetGroupPid(String salesTargetGroupPid) {
		log.debug("Request to get all Locations");
		List<Location> locationList = salesTargetGroupLocationRepository
				.findLocationsBySalesTargetGroupPid(salesTargetGroupPid);

		List<LocationDTO> result = new ArrayList<>();

		for (Location location : locationList) {
			result.add(new LocationDTO(location));
		}
		return result;
	}

	@Override
	public void save(String salesTargetGroupPid, String assignedLocations) {

		log.debug("Request to save User Sales Target Group Location");
		SalesTargetGroup salesTargetGroup = salesTargetGroupRepository.findOneByPid(salesTargetGroupPid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] locations = assignedLocations.split(",");
		List<SalesTargetGroupLocation> userLocations = new ArrayList<>();
		for (String activityPid : locations) {
			Location location = locationRepository.findOneByPid(activityPid).get();
			userLocations.add(new SalesTargetGroupLocation(location, salesTargetGroup, company));
		}
		salesTargetGroupLocationRepository.deleteBySalesTargetGroupPid(salesTargetGroupPid);
		salesTargetGroupLocationRepository.save(userLocations);
	}
}
