package com.orderfleet.webapp.service.impl;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.api.dto.MBLocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.mapper.LocationHierarchyMapper;
import com.orderfleet.webapp.web.rest.mapper.LocationMapper;

/**
 * Service Implementation for managing LocationHierarchy.
 * 
 * @author Shaheer
 * @since May 27, 2016
 */
@Service
@Transactional
public class LocationHierarchyServiceImpl implements LocationHierarchyService {

	private final Logger log = LoggerFactory.getLogger(LocationHierarchyServiceImpl.class);

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private LocationHierarchyMapper locationHierarchyMapper;

	@Inject
	private LocationMapper locationMapper;

	@Inject
	private LocationService locationService;
	/**
	 * Save a locationHierarchy.
	 * 
	 * @param locationHierarchyDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	public LocationHierarchyDTO save(LocationHierarchyDTO locationHierarchyDTO) {
		log.debug("Request to save LocationHierarchy : {}", locationHierarchyDTO);
		LocationHierarchy locationHierarchy = locationHierarchyMapper
				.locationHierarchyDTOToLocationHierarchy(locationHierarchyDTO);
		locationHierarchy = locationHierarchyRepository.save(locationHierarchy);
		LocationHierarchyDTO result = locationHierarchyMapper
				.locationHierarchyToLocationHierarchyDTO(locationHierarchy);
		return result;
	}

	/**
	 * Save a root location in Hierarchy.
	 * 
	 * @param locationId
	 *            the locationId to save
	 */
	public void saveRootLocation(Long locationId) {
		log.debug("Request to save Root Location with id : {}", locationId);
		Long currentVersion = locationHierarchyRepository.findMaxVersionByCompanyId();
		locationHierarchyRepository.insertLocationHierarchyWithNoParent(currentVersion + 1, locationId);
	}

	/**
	 * Save locationHierarchies.
	 * 
	 * @param locationHierarchyDTOs
	 *            the entities to save
	 */
	public void save(List<LocationHierarchyDTO> locationHierarchyDTOs) {
		log.debug("Request to save LocationHierarchies : {}", locationHierarchyDTOs);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Long version;
		// Only one version of a company hierarchy is active at a time
		Optional<LocationHierarchy> locationHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(companyId);
		if (locationHierarchy.isPresent()) {
			locationHierarchyRepository.updateLocationHierarchyInactivatedFor(ZonedDateTime.now(),
					locationHierarchy.get().getVersion());
			version = locationHierarchy.get().getVersion() + 1;
		} else {
			version = 1L;
		}
		// TODO:improve this code
		for (LocationHierarchyDTO locationDTO : locationHierarchyDTOs) {
			if (locationDTO.getParentId() != null) {
				locationHierarchyRepository.insertLocationHierarchyWithParent(version, locationDTO.getLocationId(),
						locationDTO.getParentId());
			} else {
				locationHierarchyRepository.insertLocationHierarchyWithNoParent(version, locationDTO.getLocationId());
			}
		}
	}

	/**
	 * Get all the locationHierarchies.
	 * 
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public List<LocationHierarchyDTO> findAllByCompanyAndActivatedTrue() {
		log.debug("Request to get all LocationHierarchies");
		List<LocationHierarchy> locationsHierarchies = locationHierarchyRepository
				.findByCompanyIdAndActivatedTrue(SecurityUtils.getCurrentUsersCompanyId());
		List<LocationHierarchyDTO> result = locationHierarchyMapper
				.locationHierarchiesToLocationHierarchyDTOs(locationsHierarchies);
		return result;
	}

	/**
	 * Get one locationHierarchy by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public LocationHierarchyDTO findOne(Long id) {
		log.debug("Request to get LocationHierarchy : {}", id);
		LocationHierarchy locationHierarchy = locationHierarchyRepository.findOne(id);
		LocationHierarchyDTO locationHierarchyDTO = locationHierarchyMapper
				.locationHierarchyToLocationHierarchyDTO(locationHierarchy);
		return locationHierarchyDTO;
	}

	/**
	 * Delete the locationHierarchy by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete LocationHierarchy : {}", id);
		locationHierarchyRepository.delete(id);
	}

	/**
	 * Save a root location in Hierarchy.
	 * 
	 * @param locationId
	 *            the locationId to save
	 */
	public void saveRootLocationWithCompanyId(Long locationId, Long companyId) {
		log.debug("Request to save Root Location with id : {}", locationId);
		Long currentVersion = locationHierarchyRepository.findMaxVersionByCompanyId(companyId);
		locationHierarchyRepository.insertLocationHierarchyWithNoParent(currentVersion + 1, locationId, companyId);
	}

	@Override
	public List<LocationHierarchyDTO> findByLocationsInAndActivatedTrue(List<Location> locations) {
		List<LocationHierarchy> locationsHierarchies = locationHierarchyRepository
				.findByLocationInAndActivatedTrue(locations);
		return locationHierarchyMapper.locationHierarchiesToLocationHierarchyDTOs(locationsHierarchies);
	}

	@Override
	public List<Long> getAllChildrenIdsByParentId(Long parentLocId) {
		// get child locations from location hierarchy
		List<Object> result = locationHierarchyRepository.findChildrenByParentLocationIdAndActivatedTrue(parentLocId);
		List<Long> locationIds = new ArrayList<>();
		for (Object object : result) {
			locationIds.add(((BigInteger) object).longValue());
		}
		return locationIds;
	}

	@Override
	public List<LocationDTO> findChildLocationsByParentId(Long parentLocId) {
		// get child locations from location hierarchy
		List<Location> locations = locationHierarchyRepository.findLocationByParentIdAndActivatedTrue(parentLocId);
		return locationMapper.locationsToLocationDTOs(locations);
	}

	@Override
	public LocationDTO findParentLocation(Long locationId) {
		Location location = locationHierarchyRepository.findParentLocationByLocationIdAndActivatedTrue(locationId);
		return locationMapper.locationToLocationDTO(location);
	}

	@Override
	public List<LocationHierarchyDTO> findByLocationPidInAndActivatedTrue(List<String> locationPids) {
		List<LocationHierarchy> locationsHierarchies = locationHierarchyRepository
				.findByLocationPidInAndActivatedTrue(locationPids);
		return locationHierarchyMapper.locationHierarchiesToLocationHierarchyDTOs(locationsHierarchies);
	}

	@Override
	public List<MBLocationHierarchyDTO> findByCompanyAndActivatedTrue() {
		log.debug("Request to get all LocationHierarchies");
		List<LocationHierarchy> locationsHierarchies = locationHierarchyRepository
				.findByCompanyIdAndActivatedTrue(SecurityUtils.getCurrentUsersCompanyId());
		return locationsHierarchies.stream().map(MBLocationHierarchyDTO::new).collect(Collectors.toList());
	}

	@Override
	public List<MBLocationHierarchyDTO> findByUserAndActivatedTrue() {
		log.debug("Request to get all User based LocationHierarchies");
		List<LocationHierarchy> userLocationsHierarchies  = new ArrayList<>();
		List<LocationHierarchy> locationsHierarchies = locationHierarchyRepository
				.findByCompanyIdAndActivatedTrue(SecurityUtils.getCurrentUsersCompanyId());
		List<LocationDTO> locationDTOs = locationService.findAllByUserAndLocationActivated(true);
//		for(LocationDTO loc: locationDTOs) {
//			locationsHierarchies.stream()
//					.filter(locHry -> (locHry.getLocation().getPid().equals(loc.getPid()) || 
//							locHry.getParent().getPid().equals(loc.getPid()) ));
//		}
		for(LocationHierarchy locHierychy: locationsHierarchies) {
			
			Optional<LocationDTO> parentExist = 
					locationDTOs.stream().filter(loc -> loc.getPid().equals(locHierychy.getParent()==null?null:locHierychy.getParent().getPid()))
					.findAny();
			Optional<LocationDTO> childExist = 
					locationDTOs.stream().filter(loc ->	loc.getPid().equals(locHierychy.getLocation()==null?null:locHierychy.getLocation().getPid()))
					.findAny();
			if(parentExist.isPresent() && childExist.isPresent()) {
				userLocationsHierarchies.add(locHierychy);
			}
		}
		return userLocationsHierarchies.stream().map(MBLocationHierarchyDTO::new).collect(Collectors.toList());
	}
}
