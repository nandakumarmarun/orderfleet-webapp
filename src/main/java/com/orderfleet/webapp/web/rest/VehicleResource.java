package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Vehicle;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.VehicleRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.VehicleService;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class VehicleResource {

	private final Logger log = LoggerFactory.getLogger(VehicleResource.class);
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private VehicleRepository vehicleRepository;
	
	@Inject
	private VehicleService vehicleService;
	
	@RequestMapping(value = "/vehicle", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllVehicles(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Vehicles");
		model.addAttribute("stockLocations", stockLocationRepository.findAllByCompanyId());
		model.addAttribute("vehicles",vehicleRepository.findAllByCompany());
		return "company/vehicle";
	}
	
	
	@RequestMapping(value = "/vehicle", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<VehicleDTO> createVehicle(@Valid @RequestBody VehicleDTO vehicleDTO)
			throws URISyntaxException {
		log.debug("Web request to save Vehicle : {}", vehicleDTO);
		if (vehicleDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("vehicle", "idexists", "A new vehicle cannot already have an ID"))
					.body(null);
		}
		if (vehicleRepository.findOneByNameAndCompanyId(vehicleDTO.getName(),SecurityUtils.getCurrentUsersCompanyId())
				.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("vehicle", "name exists", "Vehicle already in use"))
					.body(null);
		}
		
		VehicleDTO result = vehicleService.save(vehicleDTO);
		return ResponseEntity.created(new URI("/web/vehicle/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("vehicle", result.getPid().toString())).body(result);
	}
	
	
	@RequestMapping(value = "/vehicle", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<VehicleDTO> updateVehicle(@Valid @RequestBody VehicleDTO vehicleDTO)
			throws URISyntaxException {
		log.debug("REST request to update Vehicle : {}", vehicleDTO);
		if (vehicleDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("vehicle", "idNotexists", "Vehicle must have an ID"))
					.body(null);
		}

		Optional<Vehicle> existingVehicle = vehicleRepository
				.findOneByNameAndCompanyId(vehicleDTO.getName(),SecurityUtils.getCurrentUsersCompanyId());
		if (existingVehicle.isPresent() && (!existingVehicle.get().getPid().equals(vehicleDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("vehicle", "name exists", "Vehicle already in use"))
					.body(null);
		}

		VehicleDTO result = vehicleService.update(vehicleDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("vehicle", "idNotexists", "Invalid Vehicle ID")).body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("vehicle", vehicleDTO.getPid().toString())).body(result);
	}
	
	
	@RequestMapping(value = "/vehicle/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<VehicleDTO> getVehicle(@PathVariable String pid) {
		log.debug("Web request to get Vehicle by pid : {}", pid);
		return vehicleService.findOneByPid(pid).map(vehicleDTO -> new ResponseEntity<>(vehicleDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	
	@RequestMapping(value = "/vehicle/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteVehicle(@PathVariable String pid) {
		log.debug("REST request to delete Vehicle : {}", pid);
		vehicleService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("vehicle", pid.toString())).build();
	}
}
