package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Location.
 * 
 * @author Shaheer
 * @since May 07, 2016
 */
@Controller
@RequestMapping("/web")
public class LocationResource {

	private final Logger log = LoggerFactory.getLogger(LocationResource.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private LocationService locationService;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private EmployeeProfileLocationService employeeProfileLocationService;
	
	@Inject
	private AccountProfileMapper accountProfileMapper;

	/**
	 * POST /locations : Create a new location.
	 *
	 * @param locationDTO
	 *            the locationDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new locationDTO, or with status 400 (Bad Request) if the location
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/locations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<LocationDTO> createLocation(@Valid @RequestBody LocationDTO locationDTO)
			throws URISyntaxException {
		log.debug("Web request to save Location : {}", locationDTO);
		if (locationDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("location", "idexists", "A new location cannot already have an ID"))
					.body(null);
		}
		if (locationService.findByName(locationDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("location", "nameexists", "Location already in use"))
					.body(null);
		}
		locationDTO.setActivated(true);
		LocationDTO result = locationService.save(locationDTO);
		return ResponseEntity.created(new URI("/web/locations/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("location", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /locations : Updates an existing location.
	 *
	 * @param locationDTO
	 *            the locationDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         locationDTO, or with status 400 (Bad Request) if the locationDTO
	 *         is not valid, or with status 500 (Internal Server Error) if the
	 *         locationDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/locations", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<LocationDTO> updateLocation(@Valid @RequestBody LocationDTO locationDTO)
			throws URISyntaxException {
		log.debug("Web request to update Location : {}", locationDTO);
		if (locationDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("location", "idNotexists", "Location must have an ID"))
					.body(null);
		}
		Optional<LocationDTO> existingLocation = locationService.findByName(locationDTO.getName());
		if (existingLocation.isPresent() && (!existingLocation.get().getPid().equals(locationDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("location", "nameexists", "Location already in use"))
					.body(null);
		}
		LocationDTO result = locationService.update(locationDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("location", "idNotexists", "Invalid Location ID"))
					.body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("location", locationDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /locations : get all the locations.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of locations
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/locations", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllLocations(Pageable pageable, Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Locations");
		model.addAttribute("deactivatedLocations", locationService.findAllByCompanyAndLocationActivated(false));
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			List<LocationDTO> locations = locationService.findAllByCompanyAndLocationActivated(true);
			model.addAttribute("locations", locations);
		} else {
			if(userIds.size()>1){
				List<UserDTO> users=userService.findByUserIdIn(userIds);
				List<String> userPids=new ArrayList<>();
				for(UserDTO userDTO:users){
					userPids.add(userDTO.getPid());
				}
				List<LocationDTO>locationDTOs=employeeProfileLocationService.findLocationsByUserPidIn(userPids);
				model.addAttribute("locations", locationDTOs);
			}else{
				User userLogin=userService.getCurrentUser();
				List<LocationDTO>locationDTOs=employeeProfileLocationService.findLocationsByUserPid(userLogin.getPid());
				model.addAttribute("locations", locationDTOs);
			}
		}
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_133" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get accProfile by activated";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
//		List<Object[]> accountProfiles = accountProfileRepository.findAccountProfileAndCreatedByAndActivated(true);
		List<AccountProfile> accountProfileList = accountProfileRepository
				.findAllByCompanyAndActivateOrDeactivateAccountProfileOrderByName(true);
		String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		if(!accountProfileList.isEmpty()) {
			List<AccountProfileDTO> accountProfileDtos =accountProfileMapper.accountProfilesToAccountProfileDTOs(accountProfileList);
//			= accountProfiles.parallelStream().map(obj -> {
//				AccountProfileDTO accDto = new AccountProfileDTO();
//				accDto.setPid(obj[0].toString());
//				accDto.setAlias(obj[1] == null ? "" : obj[1].toString());
//				accDto.setName(obj[2].toString());
//				accDto.setDescription(obj[3] == null ? "" : obj[3].toString());
//				accDto.setAddress(obj[4] == null ? "" : obj[4].toString());
//				accDto.setUserName(obj[5] == null ? "" : obj[5].toString());
//				return accDto;
//			}).collect(Collectors.toList());
	               
	                
			model.addAttribute("accountProfiles", accountProfileDtos);
		}
		return "company/locations";
	}

	/**
	 * GET /locations-json : get all the locations.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of locations
	 *         in body
	 */
	@RequestMapping(value = "/locations-json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@ResponseBody
	public List<LocationDTO> getAllLocationsByCompany() {
		log.debug("REST request to get a Locations");
		return locationService.findAllByCompany();
	}

	/**
	 * GET /locations/:pid : get the "pid" location.
	 *
	 * @param pid
	 *            the pid of the locationDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         locationDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/locations/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<LocationDTO> getLocation(@PathVariable String pid) {
		log.debug("Web request to get Location by pid : {}", pid);
		return locationService.findOneByPid(pid).map(locationDTO -> new ResponseEntity<>(locationDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /locations/:id : delete the "id" location.
	 *
	 * @param id
	 *            the id of the locationDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/locations/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteLocation(@PathVariable String pid) {
		log.debug("REST request to delete Location : {}", pid);
		locationService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("location", pid.toString())).build();
	}

	@RequestMapping(value = "/locations/accounts", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> locationAccountProfiles(@RequestParam String locationPid) {
		log.debug("REST request to location Account Profiles : {}", locationPid);
		List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
				.findAccountProfileByLocationPid(locationPid);
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/locations/assign-accounts", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<Void> saveAssignedAccountProfiles(@RequestParam String locationPid,
			@RequestParam String assignedAccountProfiles) {
		log.debug("REST request to save assigned Account Profiles : {}", locationPid);
		locationAccountProfileService.save(locationPid, assignedAccountProfiles);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 * 
	 *        UPDATE STATUS /activities/changeStatus : update status
	 *        (Activated/Deactivated) of location.
	 * 
	 * @param locationDTO
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         location.
	 */
	@Timed
	@RequestMapping(value = "/locations/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocationDTO> updateLocationStatus(@Valid @RequestBody LocationDTO locationDTO) {
		log.debug("change status of  Location", locationDTO);
		LocationDTO res = locationService.updateLocationStatus(locationDTO.getPid(), locationDTO.getActivated());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        Activate STATUS /locations/activateLocation : activate status of
	 *        Location.
	 * 
	 * @param locations
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@Timed
	@RequestMapping(value = "/locations/activateLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<LocationDTO> activateLocation(@Valid @RequestParam String locations) {
		log.debug("Request to activate Location");
		String[] location = locations.split(",");
		for (String locationpid : location) {
			locationService.updateLocationStatus(locationpid, true);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
