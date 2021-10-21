package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Units;
import com.orderfleet.webapp.repository.UnitsRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UnitsService;
import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class UnitsResource {

	private final Logger log = LoggerFactory.getLogger(UnitsResource.class);
	
	@Inject
	private UnitsService unitsService;
	
	@Inject
	 private UnitsRepository unitsRepository;
	
	@RequestMapping(value = "/units", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<UnitsDTO> createVehicle(@Valid @RequestBody UnitsDTO unitsDTO)
			throws URISyntaxException {
		log.debug("Web request to save units : {}", unitsDTO);
		if (unitsDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("units", "idexists", "A new units cannot already have an ID"))
					.body(null);
		}
		if (unitsRepository.findByName(unitsDTO.getName())
				.isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("units", "name exists", "units already in use"))
					.body(null);
		}
		 UnitsDTO result = unitsService.save(unitsDTO);
		return ResponseEntity.created(new URI("/web/units/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("units", result.getPid().toString())).body(result);
	}
	
	
	
	@RequestMapping(value = "/units", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UnitsDTO> updateBank(@Valid @RequestBody UnitsDTO unitsDTO) throws URISyntaxException {
		log.debug("REST request to update unit : {}", unitsDTO);
		if (unitsDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("unit", "idNotexists", "unit must have an ID")).body(null);
		}
//		 Optional<Units> existingBank = unitsRepository.findOneByName(unitsDTO.getName());
//		if (existingBank.isPresent() && (!existingBank.get().getPid().equals(unitsDTO.getPid()))) {
//			return ResponseEntity.badRequest()
//					.headers(HeaderUtil.createFailureAlert("bank", "nameexists", "Bank already in use")).body(null);
//		}
		UnitsDTO result = unitsService.update(unitsDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("Units", "idNotexists", "unit Bank ID")).body(null);
		}
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("unit", unitsDTO.getPid().toString()))
				.body(result);
	}
	
	
	
	@RequestMapping(value = "/units", method = RequestMethod.GET)
	@Timed
	public String getAllUnits(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of units");
		List<UnitsDTO> units = unitsService.findAll();
		model.addAttribute("units", units);
		return "company/units";
	}
	
	@RequestMapping(value = "/units/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<UnitsDTO> getunit(@PathVariable String pid) {
		log.debug("Web request to get Unit by pid : {}", pid);
		if (pid == null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("units", "notExixt", "A new units cannot have an ID"))
					.body(null);
		}
		 UnitsDTO result = unitsService.findOneByPid(pid);
		 return ResponseEntity.ok().body(result);
	}
	
	@RequestMapping(value = "/units/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteunit(@PathVariable String pid) {
		log.debug("REST request to delete Unit : {}", pid);
		unitsService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("unit", pid.toString())).build();
	}
}
