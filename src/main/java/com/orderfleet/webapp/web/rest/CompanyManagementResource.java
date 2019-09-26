package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
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
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Country;
import com.orderfleet.webapp.domain.District;
import com.orderfleet.webapp.domain.PartnerCompany;
import com.orderfleet.webapp.domain.State;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.Industry;
import com.orderfleet.webapp.repository.CountryRepository;
import com.orderfleet.webapp.repository.DistrictRepository;
import com.orderfleet.webapp.repository.PartnerCompanyRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.SyncOperationService;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * CompanyViewDTO Web controller for managing Company.
 * 
 * @author Sarath
 * @since Aug 17, 2016
 */
@Controller
@RequestMapping("/web")
public class CompanyManagementResource {

	private final Logger log = LoggerFactory.getLogger(CompanyManagementResource.class);

	private DistrictRepository districtRepository;

	private StateRepository stateRepository;

	private CountryRepository countryRepository;

	private CompanyService companyService;

	private UserRepository userRepository;

	private PartnerCompanyRepository partnerCompanyRepository;

	private SyncOperationService syncOperationService;
	

	@Inject
	public CompanyManagementResource(DistrictRepository districtRepository, StateRepository stateRepository,
			CountryRepository countryRepository, CompanyService companyService, UserRepository userRepository,
			PartnerCompanyRepository partnerCompanyRepository, SyncOperationService syncOperationService) {
		super();
		this.districtRepository = districtRepository;
		this.stateRepository = stateRepository;
		this.countryRepository = countryRepository;
		this.companyService = companyService;
		this.userRepository = userRepository;
		this.partnerCompanyRepository = partnerCompanyRepository;
		this.syncOperationService = syncOperationService;
	}

	/**
	 * GET /companies : get all the companies.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of companies in
	 *         body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/company", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.PARTNER })
	public String getAllCompanies(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Companies");

		List<Company> companies = new ArrayList<>();
		Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		boolean opAuthUser = getByAutherisation(user);
		if (opAuthUser) {
			companies = partnerCompanyRepository.findAllCompaniesByPartnerPid(user.get().getPid());
		} else {
			companies = companyService.findAllCompany();
		}

		List<Industry> industries = Arrays.asList(Industry.values());
		List<Country> countries = countryRepository.findAll();
		model.addAttribute("companies", companies);
		model.addAttribute("countries", countries);
		model.addAttribute("industries", industries);
		return "site_admin/company";
	}

	/**
	 * POST /companies : Create a new company.
	 *
	 * @param companyDTO
	 *            the companyDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         companyDTO, or with status 400 (Bad Request) if the company has
	 *         already an ID
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/company", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<CompanyViewDTO> createCompany(@Valid @RequestBody CompanyViewDTO companyDTO)
			throws URISyntaxException {
		log.debug("Web request to save Company : {}", companyDTO.getPhoneNo());
		if (companyDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("company", "idexists", "A new company cannot already have an ID"))
					.body(null);
		}
		if (companyService.findByName(companyDTO.getLegalName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "nameexists", "Company already in use"))
					.body(null);
		}
		Company company = companyService.save(companyDTO);
		companyService.saveDefaultSettings(company);

		Optional<User> user = userRepository.findOneWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin());
		boolean val = getByAutherisation(user);
		if (val) {
			partnerCompanyRepository.save(new PartnerCompany(company, user.get()));
			syncOperationService.saveSyncOperationForPartners(company.getPid(),
					"PRODUCTCATEGORY,PRODUCTGROUP,PRODUCTPROFILE,PRODUCTGROUP_PRODUCTPROFILE,PRICE_LEVEL,PRICE_LEVEL_LIST,STOCK_LOCATION,OPENING_STOCK,ACCOUNT_PROFILE,LOCATION,LOCATION_HIRARCHY,LOCATION_ACCOUNT_PROFILE,RECEIVABLE_PAYABLE,ACCOUNT_PROFILE_CLOSING_BALANCE,TAX_MASTER,PRODUCT_PROFILE_TAX_MASTER,SALES_ORDER,RECEIPT");
		}

		CompanyViewDTO result = new CompanyViewDTO(company);
		return ResponseEntity.created(new URI("/web/company/" + result.getPid()))
				.headers(HeaderUtil.createEntityCreationAlert("company", result.getPid().toString())).body(result);
	}

	/**
	 * PUT /companies : Updates an existing company.
	 *
	 * @param companyDTO
	 *            the companyDTO to update
	 * @return the ResponseEntity with status 200 (OK) and with body the updated
	 *         companyDTO, or with status 400 (Bad Request) if the companyDTO is not
	 *         valid, or with status 500 (Internal Server Error) if the companyDTO
	 *         couldnt be updated
	 * @throws URISyntaxException
	 *             if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/company", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyViewDTO> updateCompany(@Valid @RequestBody CompanyViewDTO companyDTO)
			throws URISyntaxException {
		log.debug("REST request to update Company : {}", companyDTO);
		if (companyDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "Company must have an ID"))
					.body(null);
		}
		Optional<CompanyViewDTO> existingCompany = companyService.findByName(companyDTO.getLegalName());
		if (existingCompany.isPresent() && (!existingCompany.get().getPid().equals(companyDTO.getPid()))) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "nameexists", "Company already in use"))
					.body(null);
		}
		CompanyViewDTO result = companyService.update(companyDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "Invalid Company ID")).body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("company", companyDTO.getPid().toString())).body(result);
	}

	/**
	 * GET /companies/:pid : get the "pid" company.
	 *
	 * @param pid
	 *            the pid of the companyDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the companyDTO,
	 *         or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/company/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyViewDTO> getCompany(@PathVariable String pid) {
		log.debug("Web request to get Company by pid : {}", pid);
		return companyService.findOneByPid(pid).map(companyDTO -> new ResponseEntity<>(companyDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE /companies/:id : delete the "id" company.
	 *
	 * @param id
	 *            the id of the companyDTO to delete
	 * @return the ResponseEntity with status 200 (OK)
	 */
	@RequestMapping(value = "/company/{pid}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> deleteCompany(@PathVariable String pid) {
		log.debug("REST request to delete Company : {}", pid);
		companyService.delete(pid);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("company", pid)).build();
	}

	/**
	 * GET /companies/:pid : get the "pid" company.
	 *
	 * @param pid
	 *            the pid of the companyDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the companyDTO,
	 *         or with status 404 (Not Found)
	 */
	@RequestMapping(value = "/company/changeStatus", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyViewDTO> changeStatusCompany(@Valid @RequestBody CompanyViewDTO companyDTO)
			throws URISyntaxException {
		log.debug("Web request to get Company by pid : {}", companyDTO);
		CompanyViewDTO companyViewDTO = companyService.changeStatusCompany(companyDTO);
		return new ResponseEntity<>(companyViewDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/company/countryChange/{countryCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<State>> countryChange(@PathVariable String countryCode) {
		log.debug("Web request to get Company by countryCode : {}", countryCode);
		Country country = countryRepository.findByCode(countryCode);
		List<State> states = stateRepository.findAllByCountryId(country.getId());
		return new ResponseEntity<>(states, HttpStatus.OK);
	}

	@RequestMapping(value = "/company/stateChange/{stateCode}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<District>> stateChange(@PathVariable String stateCode) {
		log.debug("Web request to get Company by stateCode : {}", stateCode);
		State state = stateRepository.findByCode(stateCode);
		List<District> districts = districtRepository.findAllByStateId(state.getId());
		return new ResponseEntity<>(districts, HttpStatus.OK);
	}

	@RequestMapping(value = "/company-logo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyViewDTO> getCurrentUserEmployeeProfileImage() {
		log.debug("Web request to get Current company logo");
		return new ResponseEntity<>(companyService.findtCurrentCompanyLogo(), HttpStatus.OK);
	}

	private boolean getByAutherisation(Optional<User> user) {
		Authority authority = new Authority("ROLE_PARTNER");
		if (user.isPresent()) {
			Optional<Authority> opAuthUser = user.get().getAuthorities().stream().filter(pc -> pc.equals(authority))
					.findAny();
			return opAuthUser.isPresent();
		}
		return false;
	}

	@RequestMapping(value = "/company/industryChange/{industry}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<CompanyViewDTO>> industryChange(@PathVariable Industry industry) {
		log.debug("Web request to get Companies by industry : {}", industry);
		List<CompanyViewDTO> companyViewDTOs = companyService.findAllCompanyByIndustrySortedByName(industry);
		return new ResponseEntity<>(companyViewDTOs, HttpStatus.OK);
	}
}
