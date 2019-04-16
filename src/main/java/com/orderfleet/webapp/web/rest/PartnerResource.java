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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Country;
import com.orderfleet.webapp.domain.District;
import com.orderfleet.webapp.domain.State;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CountryRepository;
import com.orderfleet.webapp.repository.DistrictRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.PartnerService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.PartnerDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Partner.
 * 
 * @author Sarath
 * @since Feb 14, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class PartnerResource {

	private final Logger log = LoggerFactory.getLogger(PartnerResource.class);

	@Inject
	private CountryRepository countryRepository;

	@Inject
	private StateRepository stateRepository;

	@Inject
	private DistrictRepository districtRepository;

	@Inject
	private UserService userService;

	@Inject
	PartnerService partnerService;

	@Inject
	private UserRepository userRepository;

	/**
	 * POST /partners : Create a new partner.
	 *
	 * @param partnerDTO
	 *            the partnerDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the
	 *         new partnerDTO, or with status 400 (Bad Request) if the partner
	 *         has already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/partners", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<PartnerDTO> createPartner(@Valid @RequestBody PartnerDTO partnerDTO)
			throws URISyntaxException {
		log.debug("Web request to save Partner : {}", partnerDTO);
		if (partnerDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("partner", "idexists", "A new partner cannot already have an ID"))
					.body(null);
		}
		if (userService.findByName(partnerDTO.getLogin()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "name exists", "Partner already in use"))
					.body(null);
		}
		if (userRepository.findOneByEmail(partnerDTO.getEmail()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "mail exists", "Partner already in use"))
					.body(null);
		}
		partnerDTO.setActivated(true);
		PartnerDTO result = partnerService.save(partnerDTO);
		return ResponseEntity.created(new URI("/web/partners/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("partner", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /partners : Updates an existing partner.
	 *
	 * @param partnerDTO
	 *            the partnerDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         partnerDTO, or with status 400 (Bad Request) if the partnerDTO is
	 *         not valid, or with status 500 (Internal Server Error) if the
	 *         partnerDTO couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/partners", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PartnerDTO> updatePartner(@Valid @RequestBody PartnerDTO partnerDTO)
			throws URISyntaxException {
		log.debug("REST request to update Partner : {}", partnerDTO);
		if (partnerDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "idNotexists", "Partner must have an ID"))
					.body(null);
		}

		Optional<User> existingPartner = userService.findByName(partnerDTO.getLogin());
		if (existingPartner.isPresent() && (!existingPartner.get().getPid().equals(partnerDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "name exists", "Partner already in use"))
					.body(null);
		}

		Optional<User> existingPartnerMail = userRepository.findOneByEmail(partnerDTO.getEmail());
		if (existingPartnerMail.isPresent() && (!existingPartnerMail.get().getPid().equals(partnerDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("accountGroup", "mail exists", "Partner already in use"))
					.body(null);
		}
		partnerDTO.setActivated(true);
		PartnerDTO result = partnerService.update(partnerDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("partner", "idNotexists", "Invalid Partner ID")).body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("partner", partnerDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /partners : get all the partners.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of partners
	 *         in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/partners", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getAllPartners(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Partners");
		List<PartnerDTO> partners = partnerService.GetAllPartners();
		model.addAttribute("partners", partners);
		List<Country> countries = countryRepository.findAll();
		model.addAttribute("countries", countries);
		return "site_admin/partners";
	}

	/**
	 * GET /partners/:pid : get the "pid" partner.
	 *
	 * @param pid
	 *            the pid of the partnerDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         partnerDTO, or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/partners/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<PartnerDTO> getPartner(@PathVariable String pid) {
		log.debug("Web request to get Partner by pid : {}", pid);
		return partnerService.findOneByPid(pid).map(partnerDTO -> new ResponseEntity<>(partnerDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// /**
	// * DELETE /partners/:id : delete the "id" partner.
	// *
	// * @param id
	// * the id of the partnerDTO to delete
	// * @return the ResponseEntity with status 200 (OK)
	// */
	// @RequestMapping(value = "/partners/{pid}", method = RequestMethod.DELETE,
	// produces = MediaType.APPLICATION_JSON_VALUE)
	// @Timed
	// public ResponseEntity<Void> deletePartner(@PathVariable String pid) {
	// log.debug("REST request to delete Partner : {}", pid);
	// partnerService.delete(pid);
	// return
	// ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("partner",
	// pid.toString())).build();
	// }

	@Timed
	@RequestMapping(value = "/partners/changeStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PartnerDTO> updateActivityGroupStatus(@Valid @RequestBody PartnerDTO partnerDTO) {
		log.debug("request to update partner status", partnerDTO);
		PartnerDTO res = partnerService.updatePartnerStatus(partnerDTO.getPid(), partnerDTO.getActivated());
		return new ResponseEntity<PartnerDTO>(res, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/partners/activatePartner", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PartnerDTO> activatePartner(@Valid @RequestParam String partners) {
		log.debug("request to activate partner ");
		// String[] partnerarray = partners.split(",");
		// for (String partner : partnerarray) {
		// partnerService.updatePartnerStatus(partner, true);
		// }
		return new ResponseEntity<PartnerDTO>(HttpStatus.OK);
	}

	@RequestMapping(value = "/partners/countryChange/{countryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<State>> countryChange(@PathVariable String countryCode) {
		log.debug("Web request to get Partner by countryCode : {}", countryCode);
		Country country = countryRepository.findByCode(countryCode);
		List<State> states = stateRepository.findAllByCountryId(country.getId());
		return new ResponseEntity<>(states, HttpStatus.OK);
	}

	@RequestMapping(value = "/partners/stateChange/{stateCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)

	@Timed
	public ResponseEntity<List<District>> stateChange(@PathVariable String stateCode) {
		log.debug("Web request to get Partner by stateCode : {}", stateCode);
		State state = stateRepository.findByCode(stateCode);
		List<District> districts = districtRepository.findAllByStateId(state.getId());
		return new ResponseEntity<>(districts, HttpStatus.OK);
	}
}
