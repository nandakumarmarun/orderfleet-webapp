package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyAddedDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyCustomDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class LocationHierarchyResource {

	private final Logger log = LoggerFactory.getLogger(LocationHierarchyResource.class);

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private LocationService locationService;
	
	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	/**
	 * POST /location-hierarchies : Create a new locationHierarchy.
	 *
	 * @param locationHierarchyDTOs
	 *            the locationHierarchyDTOs to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new locationHierarchyDTO, or with status 400 (Bad Request) if the
	 *         locationHierarchy has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/location-hierarchies", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createLocationHierarchy(@RequestBody List<LocationHierarchyDTO> locationHierarchyDTOs)
			throws URISyntaxException {
		log.debug("REST request to save LocationHierarchy : {}", locationHierarchyDTOs);
		locationHierarchyService.save(locationHierarchyDTOs);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityCreationAlert("locationHierarchy", "locationHierarchy Created successfully"))
				.build();
	}

	/**
	 * POST /root-location : Create a Root location in Hierarchy.
	 *
	 * @param locationId
	 *            the location to create
	 * @return the ResponseEntity with status 201 (Created), or with status 400
	 *         (Bad Request) if the location has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/root-location", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createRootLocation(@RequestBody Long locationId) throws URISyntaxException {
		log.debug("REST request to save Root Location with id : {}", locationId);
		locationHierarchyService.saveRootLocation(locationId);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityCreationAlert("rootLocation", "rootLocation Created successfully"))
				.build();
	}

	@RequestMapping(value = "/location-hierarchy", method = RequestMethod.GET)
	public String getLocationHierarchyPage(Model model) {
		log.debug("Web request to get LocationHierarchies");
		model.addAttribute("locations", locationService.findAllByCompanyAndIdNotInLocationHierarchy());
		return "company/locations-hierarchy";
	}

	/**
	 * GET /location-hierarchies : get all the locationHierarchies.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         locationHierarchies in body
	 */
	@RequestMapping(value = "/location-hierarchies", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<LocationHierarchyDTO>> getAllLocationHierarchies() throws URISyntaxException {
		log.debug("REST request to get LocationHierarchies");
		List<LocationHierarchyDTO> locationHierarchyDTOs = locationHierarchyService.findAllByCompanyAndActivatedTrue();
		return new ResponseEntity<>(locationHierarchyDTOs, HttpStatus.OK);
	}

	
	@RequestMapping(value = "/location-hierarchies-view", method = RequestMethod.GET)
	public String getLocationHierarchyviewPage(Model model) {
		log.debug("Web request to get LocationHierarchies");
		model.addAttribute("locations", locationService.findAllByCompanyAndIdNotInLocationHierarchy());
		model.addAttribute("locationsall", locationService.findAllByCompanyAndIdInLocationHierarchy());
		return "company/location-hierarchies-view";
	}
	
	@RequestMapping(value = "/location-hierarchies-view", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createLocationHierarchyView(@RequestBody LocationHierarchyCustomDTO locationHierarchyCustomDTO)
			throws URISyntaxException {
//		log.debug("REST request to save LocationHierarchy : {}", locationHierarchyCustomDTO);
		List<LocationHierarchyDTO> locationHierarchyDTOs = locationHierarchyCustomDTO.getSelectednode();
		List<LocationHierarchyAddedDTO> locationHierarchyAddedDTOs = locationHierarchyCustomDTO.getAddednode();
		List<LocationHierarchyDTO> locationHierarchyisCustomTrue = locationHierarchyService.findLocationHierarchyIscustomIstrue();
		if(!locationHierarchyisCustomTrue.isEmpty()) {
			locationHierarchyisCustomTrue.forEach(data -> {
				LocationHierarchyAddedDTO addedlocations = new LocationHierarchyAddedDTO();
				addedlocations.setId(data.getLocationId());
				addedlocations.setName(data.getLocationName());
				locationHierarchyAddedDTOs.add(addedlocations);
			});
		}
		locationHierarchyAddedDTOs.forEach(data -> locationHierarchyDTOs.forEach(data1 -> {
			if(data.getId().equals(data1.getLocationId())) {
				data1.setCustom(true);
			}
		}));
		locationHierarchyService.saveCustom(locationHierarchyDTOs);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityCreationAlert("locationHierarchy", "locationHierarchy Created successfully"))
				.build();
	}
	
	@RequestMapping(value = "/location-hierarchies-view/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
		log.debug("REST request to delete location-hierarchies-view : {}", id);
		
		locationHierarchyRepository.updateLocationHierarchyInactivatedForOnlyOne(ZonedDateTime.now(),id);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("location-hierarchies-view", id.toString())).build();
	}
	
	@RequestMapping(value = "/location-hierarchies-view", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<LocationHierarchyDTO>> getAllLocationHierarchiesfilterNoParent() throws URISyntaxException {
		log.debug("REST request to get LocationHierarchies");
		List<LocationHierarchyDTO> locationHierarchyDTOs = locationHierarchyService.findAllByCompanyAndActivatedTrue();
		locationHierarchyDTOs.forEach(System.out::println);
		return new ResponseEntity<>(locationHierarchyDTOs, HttpStatus.OK);
	}
}
