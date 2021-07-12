package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.mapper.LocationMapper;

/**
 * Service Implementation for managing Location.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
@Service
@Transactional
public class LocationServiceImpl implements LocationService {

	private final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private LocationMapper locationMapper;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	/**
	 * Save a location.
	 * 
	 * @param locationDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public LocationDTO save(LocationDTO locationDTO) {
		log.debug("Request to save Location : {}", locationDTO);
		locationDTO.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid()); // set
		Location location = locationMapper.locationDTOToLocation(locationDTO);
		location.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		location = locationRepository.save(location);
		LocationDTO result = locationMapper.locationToLocationDTO(location);
		return result;
	}

	/**
	 * Update a location.
	 * 
	 * @param locationDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public LocationDTO update(LocationDTO locationDTO) {
		log.debug("Request to Update Location : {}", locationDTO);
		return locationRepository.findOneByPid(locationDTO.getPid()).map(location -> {
			location.setName(locationDTO.getName());
			location.setAlias(locationDTO.getAlias());
			location.setDescription(locationDTO.getDescription());
			location = locationRepository.save(location);
			LocationDTO result = locationMapper.locationToLocationDTO(location);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the locations.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findAllByCompany() {
		log.debug("Request to get all Locations");
		List<Location> locationList = locationRepository.findAllByCompanyIdOrderByName();
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locationList);
		return result;
	}

	/**
	 * Get all the locations.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<LocationDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Locations");
		Page<Location> locations = locationRepository.findAllByCompanyId(pageable);
		Page<LocationDTO> result = new PageImpl<LocationDTO>(
				locationMapper.locationsToLocationDTOs(locations.getContent()), pageable, locations.getTotalElements());
		return result;
	}

	/**
	 * Get one location by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public LocationDTO findOne(Long id) {
		log.debug("Request to get Location : {}", id);
		Location location = locationRepository.findOne(id);
		LocationDTO locationDTO = locationMapper.locationToLocationDTO(location);
		return locationDTO;
	}

	/**
	 * Get one location by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<LocationDTO> findOneByPid(String pid) {
		log.debug("Request to get Location by pid : {}", pid);
		return locationRepository.findOneByPid(pid).map(location -> {
			LocationDTO locationDTO = locationMapper.locationToLocationDTO(location);
			return locationDTO;
		});
	}

	/**
	 * Get one location by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<LocationDTO> findByName(String name) {
		log.debug("Request to get Location by name : {}", name);
		return locationRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(location -> {
					LocationDTO locationDTO = locationMapper.locationToLocationDTO(location);
					return locationDTO;
				});
	}

	/**
	 * Delete the location by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Location : {}", pid);
		locationRepository.findOneByPid(pid).ifPresent(location -> {
			locationRepository.delete(location.getId());
		});
	}

	/**
	 * Get all locations by not in location hierarchy.
	 * 
	 * @return the list of entities
	 */
	public List<LocationDTO> findAllByCompanyAndIdNotInLocationHierarchy() {
		log.debug("Request to get all Locations by locations not in the list of ids");
		List<Location> locations = locationRepository.findByCompanyIdAndIdNotIn();
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locations);
		return result;
	}
	
	public List<LocationDTO> findAllByCompanyAndIdInLocationHierarchy() {
		log.debug("Request to get all Locations by locations  in the list of ids");
		List<Location> locations = locationRepository.findByCompanyIdAndIdIn();
		List<LocationDTO> result = locationMapper.locationsToLocationDTOs(locations);
		return result;
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 7, 2017
	 * 
	 *        Update the Location status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public LocationDTO updateLocationStatus(String pid, boolean active) {
		log.debug("request to change status of location", pid);
		return locationRepository.findOneByPid(pid).map(location -> {
			if (!active) {
				locationAccountProfileRepository.deleteByLocationPid(pid);
			}
			location.setActivated(active);
			location = locationRepository.save(location);
			LocationDTO result = locationMapper.locationToLocationDTO(location);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all locationDTOs from Location by status and company.
	 * 
	 * @param active the active of the entity
	 * @return the entity
	 */

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findAllByCompanyAndLocationActivated(boolean active) {
		log.debug("request to get all activated locations ");
		List<Location> locations = locationRepository.findAllByCompanyIdAndLocationActivatedOrDeactivated(active);
		List<LocationDTO> locationDTOs = locationMapper.locationsToLocationDTOs(locations);
		return locationDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active   the active of the entity
	 * 
	 * @param pageable the pageable of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<LocationDTO> findAllByCompanyAndActivatedLocationOrderByName(Pageable pageable, boolean active) {
		log.debug("request to get all activated locations ");
		Page<Location> pageLocation = locationRepository.findAllByCompanyIdAndActivatedLocationOrderByName(pageable,
				active);
		Page<LocationDTO> pageLocationDTO = new PageImpl<LocationDTO>(
				locationMapper.locationsToLocationDTOs(pageLocation.getContent()), pageable,
				pageLocation.getTotalElements());
		return pageLocationDTO;
	}

	/**
	 * Get one location by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<LocationDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get Location by name : {}", name);
		return locationRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(location -> {
			LocationDTO locationDTO = locationMapper.locationToLocationDTO(location);
			return locationDTO;
		});
	}

	/**
	 * Save a location.
	 * 
	 * @param locationDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public LocationDTO saveLocation(Long companyId, LocationDTO locationDTO) {
		log.debug("Request to save Location : {}", locationDTO);
		locationDTO.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid()); // set
		Location location = locationMapper.locationDTOToLocation(locationDTO);
		location.setCompany(companyRepository.findOne(companyId));
		location = locationRepository.save(location);
		LocationDTO result = locationMapper.locationToLocationDTO(location);
		return result;
	}

	/**
	 *
	 */

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findAllByCompanyIdAndLocationActivatedLastModified(boolean active,
			LocalDateTime lastModifiedDate) {
		log.debug("request to get all activated locations ");
		List<Location> locations = locationRepository.findAllByCompanyIdAndLocationActivatedLastModified(active,
				lastModifiedDate);
		List<LocationDTO> locationDTOs = locationMapper.locationsToLocationDTOs(locations);
		return locationDTOs;
	}

	@Override
	public List<LocationDTO> findAllByCompanyAndLocationIdIn(List<Long> locationIds) {
		List<Location> locations = locationRepository.findAllByCompanyIdAndActivatedLocationIn(locationIds);
		return locationMapper.locationsToLocationDTOs(locations);
	}

	@Override
	public List<Location> findAllLocationByCompanyId(Long companyId) {
		List<Location> locations = new ArrayList<>();
		for (Object[] object : locationRepository.findAllLocationByCompanyId(companyId)) {
			Location location = new Location();
			location.setId((Long) object[0]);
			location.setName(object[1].toString());
			if (object[2] != null) {
				location.setAlias(object[2].toString());
			}
			locations.add(location);
		}
		return locations;
	}

	@Override
	public List<LocationDTO> findAllLocationsByCompanyAndActivatedLocations() {

		log.debug("Request to get all Locations Activated");

		List<Location> locationList = locationRepository.findAllLocationsByCompanyAndActivatedLocations(true);

		List<LocationDTO> result = new ArrayList<>();

		for (Location location : locationList) {
			result.add(new LocationDTO(location));
		}
		return result;

	}

	@Override
	public void saveActivatedLocations(String assignedLocations) {

		List<Location> allLocations = locationRepository.findAllByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		List<Location> deactivatedLocationList = new ArrayList<>();
		for (Location location1 : allLocations) {
			location1.setActivatedLocations(false);
			deactivatedLocationList.add(location1);
		}
		log.debug("Request to Change activated locations to false : " + deactivatedLocationList.size());
		locationRepository.save(deactivatedLocationList);

		log.debug("Request to Save  Locations Activated with Pids : " + assignedLocations);

		List<Location> activatedlocationsList = new ArrayList<>();
		String[] locations = assignedLocations.split(",");
		for (String locationPid : locations) {
			Location location = locationRepository.findOneByPid(locationPid).get();
			location.setActivatedLocations(true);
			activatedlocationsList.add(location);
		}
		log.debug("Size : " + activatedlocationsList.size());
		locationRepository.save(activatedlocationsList);

	}

	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findAllByUserAndLocationActivated(boolean active) {
		log.debug("request to get all activated locations ");
		List<Location> locations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		List<LocationDTO> locationDTOs = locationMapper.locationsToLocationDTOs(locations);
		return locationDTOs;
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<LocationDTO> findAllByUserAndLocationActivatedLastModified(boolean active,
			LocalDateTime lastModifiedDate) {
		log.debug("request to get all activated locations ");
		List<Location> userAssignedLocations = new ArrayList<>();
		List<Location> locations = locationRepository.findAllByCompanyIdAndLocationActivatedLastModified(active,
				lastModifiedDate);
		List<Location> userLocations = employeeProfileLocationRepository.findLocationsByEmployeeProfileIsCurrentUser();
		
		for(Location userLoc : userLocations) {
			Optional<Location> opLocation = locations.stream().filter(loc -> loc.getId()==userLoc.getId()).findAny();
			if(opLocation.isPresent()) {
				userAssignedLocations.add(opLocation.get());
			}
		}
		
		List<LocationDTO> locationDTOs = locationMapper.locationsToLocationDTOs(userAssignedLocations);
		return locationDTOs;
	}
}
