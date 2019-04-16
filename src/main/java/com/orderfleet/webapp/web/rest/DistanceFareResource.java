package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;

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
import com.orderfleet.webapp.service.DistanceFareService;
import com.orderfleet.webapp.web.rest.dto.DistanceFareDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class DistanceFareResource {

	private final Logger log = LoggerFactory.getLogger(DistanceFareResource.class);
	
	@Inject
	private DistanceFareService distanceFareService;
	
	@RequestMapping(value = "/distanceFare", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<DistanceFareDTO> createDistanceFare(
			@Valid @RequestBody DistanceFareDTO distanceFareDTO) throws URISyntaxException{
		log.debug("Web request to save DistanceFare : {}", distanceFareDTO);
		DistanceFareDTO result = distanceFareService.save(distanceFareDTO);
		return ResponseEntity.created(new URI("/web/distanceFare" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("distanceFare", result.getPid().toString()))
				.body(result);
		
	}
	
	@RequestMapping(value = "/distanceFare", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DistanceFareDTO> updateDistanceFare(
			@Valid @RequestBody DistanceFareDTO distanceFareDTO) {
		log.debug("Web request to update DistanceFare : {}", distanceFareDTO);
		DistanceFareDTO result = distanceFareService.update(distanceFareDTO);
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("distanceFare", distanceFareDTO.getPid().toString()))
				.body(result);
	}
	
	@RequestMapping(value = "/distanceFare/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteDistanceFare(@PathVariable String pid) {
		log.debug("REST request to delete DistanceFare : {}", pid);
		distanceFareService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("distanceFare", pid.toString()))
				.build();
	}
	
	@RequestMapping(value = "/distanceFare", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDistanceFare(Model model) {
		model.addAttribute("distaceFare",distanceFareService.findAllByCompany());
		return "company/distanceFare";
		
	}
	
	@RequestMapping(value = "/distanceFare/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DistanceFareDTO> getDistanceFare(@PathVariable String pid) {
		log.debug("Web request to get DistanceFare by pid : {}", pid);
		return distanceFareService.findOneByPid(pid)
				.map(distanceFareDTO -> new ResponseEntity<>(distanceFareDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
}
