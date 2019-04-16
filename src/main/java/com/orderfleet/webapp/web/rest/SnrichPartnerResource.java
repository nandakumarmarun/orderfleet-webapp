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
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Country;
import com.orderfleet.webapp.domain.District;
import com.orderfleet.webapp.domain.SnrichPartner;
import com.orderfleet.webapp.domain.State;
import com.orderfleet.webapp.repository.CountryRepository;
import com.orderfleet.webapp.repository.DistrictRepository;
import com.orderfleet.webapp.repository.SnrichPartnerRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.SnrichPartnerService;
import com.orderfleet.webapp.web.rest.dto.SnrichPartnerDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class SnrichPartnerResource {

	private final Logger log = LoggerFactory.getLogger(SnrichPartnerResource.class);
	
	@Inject
	private SnrichPartnerService snrichPartnerService;
	
	@Inject
	private SnrichPartnerRepository snrichPartnerRepository;
	
	@Inject
	private CountryRepository countryRepository;
	
	@Inject
	private StateRepository stateRepository;
	
	@Inject
	private DistrictRepository districtRepository;
	
	@RequestMapping(value = "/snrich-partners", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN})
	public String getAllSnrichParnters(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Partners");
		List<SnrichPartnerDTO> snrichPartnerDTOs = snrichPartnerService.GetAllPartners();
		model.addAttribute("partners", snrichPartnerDTOs);
		List<Country> countries = countryRepository.findAll();
		model.addAttribute("countries", countries);
		return "site_admin/snrich-partner";
	}
	
	
	@RequestMapping(value = "/snrich-partners", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SnrichPartnerDTO> createSnrichPartner(@Valid @RequestBody SnrichPartnerDTO snrichPartnerDTO)
			throws URISyntaxException {
		log.debug("Web request to save Partner : {}", snrichPartnerDTO);
		if (snrichPartnerDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("partner", "idexists", "A new partner cannot already have an ID"))
					.body(null);
		}
		if (snrichPartnerRepository.findOneByName(snrichPartnerDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "name exists", "Partner already in use"))
					.body(null);
		}
		if (snrichPartnerRepository.findOneByEmail(snrichPartnerDTO.getEmail()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "mail exists", "Partner already in use"))
					.body(null);
		}
		snrichPartnerDTO.setActivated(true);
		SnrichPartnerDTO result = snrichPartnerService.save(snrichPartnerDTO);
		return ResponseEntity.created(new URI("/web/partners/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("partner", result.getPid().toString())).body(result);
	}
	
	
	@RequestMapping(value = "/snrich-partners", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SnrichPartnerDTO> updateSnrichPartner(@Valid @RequestBody SnrichPartnerDTO snrichPartnerDTO)
			throws URISyntaxException {
		log.debug("REST request to update Partner : {}", snrichPartnerDTO);
		if (snrichPartnerDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "idNotexists", "Partner must have an ID"))
					.body(null);
		}

		Optional<SnrichPartner> existingPartner = snrichPartnerRepository.findOneByName(snrichPartnerDTO.getName());
		if (existingPartner.isPresent() && (!existingPartner.get().getPid().equals(snrichPartnerDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "name exists", "Partner already in use"))
					.body(null);
		}

		Optional<SnrichPartner> existingPartnerMail = snrichPartnerRepository.findOneByEmail(snrichPartnerDTO.getEmail());
		if (existingPartnerMail.isPresent() && (!existingPartnerMail.get().getPid().equals(snrichPartnerDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "mail exists", "Partner already in use"))
					.body(null);
		}
		snrichPartnerDTO.setActivated(true);
		SnrichPartnerDTO result = snrichPartnerService.update(snrichPartnerDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "idNotexists", "Invalid Partner ID")).body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("partner", snrichPartnerDTO.getPid().toString())).body(result);
	}
	
	
	@RequestMapping(value = "/snrich-partners/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SnrichPartnerDTO> getSnrichPartner(@PathVariable String pid) {
		log.debug("Web request to get Partner by pid : {}", pid);
		return snrichPartnerService.findOneByPid(pid).map(snrichPartnerDTO -> new ResponseEntity<>(snrichPartnerDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	
	@Timed
	@RequestMapping(value = "/snrich-partners/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SnrichPartnerDTO> updateSnrichPartnerStatus(@Valid @RequestBody SnrichPartnerDTO snrichPartnerDTO) {
		log.debug("request to update partner status", snrichPartnerDTO);
		SnrichPartnerDTO result = snrichPartnerService.updatePartnerStatus(snrichPartnerDTO.getPid(), snrichPartnerDTO.getActivated());
		return new ResponseEntity<SnrichPartnerDTO>(result, HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/snrich-partners/countryChange/{countryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<State>> snrichCountryChange(@PathVariable String countryCode) {
		log.debug("Web request to get Partner by countryCode : {}", countryCode);
		Country country = countryRepository.findByCode(countryCode);
		List<State> states = stateRepository.findAllByCountryId(country.getId());
		return new ResponseEntity<>(states, HttpStatus.OK);
	}
	

	@RequestMapping(value = "/snrich-partners/stateChange/{stateCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<District>> snrichStateChange(@PathVariable String stateCode) {
		log.debug("Web request to get Partner by stateCode : {}", stateCode);
		State state = stateRepository.findByCode(stateCode);
		List<District> districts = districtRepository.findAllByStateId(state.getId());
		return new ResponseEntity<>(districts, HttpStatus.OK);
	}
}
