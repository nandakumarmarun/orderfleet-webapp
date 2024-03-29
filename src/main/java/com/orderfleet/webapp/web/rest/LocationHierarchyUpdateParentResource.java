package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.catalina.security.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class LocationHierarchyUpdateParentResource {

	private final Logger log = LoggerFactory.getLogger(LocationHierarchyUpdateParentResource.class);

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private LocationService locationService;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepo;

	@Inject
	private LocationRepository locationRepo;

	/**
	 * POST /location-hierarchies : Create a new locationHierarchy.
	 *
	 * @param locationHierarchyDTOs the locationHierarchyDTOs to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         locationHierarchyDTO, or with status 400 (Bad Request) if the
	 *         locationHierarchy has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/location-hierarchies-parent-update", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<Void> createLocationHierarchy(@RequestParam String selectedNode,
			@RequestParam String parentId) throws URISyntaxException {
		log.debug("REST request to update parent: {}");

		Location selectedLocation = locationRepo
				.findByName(selectedNode);
		if (selectedLocation!=null) {
			locationHierarchyRepo.updateParentIdByLocation(selectedLocation.getId(), Long.valueOf(parentId));
		}
		// locationHierarchyService.save(locationHierarchyDTOs);
		return ResponseEntity.ok().headers(
				HeaderUtil.createEntityCreationAlert("locationHierarchy", "locationHierarchy Created successfully"))
				.build();
	}

	/**
	 * POST /root-location : Create a Root location in Hierarchy.
	 *
	 * @param locationId the location to create
	 * @return the ResponseEntity with status 201 (Created), or with status 400 (Bad
	 *         Request) if the location has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/root-location-update-parent", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> createRootLocation(@RequestBody Long locationId) throws URISyntaxException {
		log.debug("REST request to save Root Location with id : {}", locationId);
		locationHierarchyService.saveRootLocation(locationId);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityCreationAlert("rootLocation", "rootLocation Created successfully"))
				.build();
	}

	@RequestMapping(value = "/location-hierarchy-update-parent", method = RequestMethod.GET)
	public String getLocationHierarchyPage(Model model) {
		log.debug("Web request to get LocationHierarchies");
		model.addAttribute("locations", locationService.findAllByCompanyAndIdInLocationHierarchy());
		return "company/locations-hierarchy-update-parent";
	}

	/**
	 * GET /location-hierarchies : get all the locationHierarchies.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         locationHierarchies in body
	 */
	@RequestMapping(value = "/location-hierarchies-update-parent", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<LocationHierarchyDTO>> getAllLocationHierarchies() throws URISyntaxException {
		log.debug("REST request to get LocationHierarchies");
		List<LocationHierarchyDTO> locationHierarchyDTOs = locationHierarchyService.findAllByCompanyAndActivatedTrue();
		return new ResponseEntity<>(locationHierarchyDTOs, HttpStatus.OK);
	}

}
