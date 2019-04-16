package com.orderfleet.webapp.web.rest;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PartnerCompany;
import com.orderfleet.webapp.domain.RegistrationData;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.PartnerCompanyRepository;
import com.orderfleet.webapp.repository.RegistrationDataRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;
import com.orderfleet.webapp.service.SyncOperationService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.CreateAccountDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web/trial")
@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.PARTNER })
public class CompanyTrialSetUpResource {

	private final Logger log = LoggerFactory.getLogger(CompanyTrialSetUpResource.class);

	@Inject
	private CompanyTrialSetUpService companyTrialSetUpService;

	@Inject
	private CompanyService companyService;

	@Inject
	private RegistrationDataRepository registrationDataRepository;

	@Inject
	private SyncOperationService syncOperationService;

	@Inject
	private UserService userService;

	@Inject
	private PartnerCompanyRepository partnerCompanyRepository;

	@RequestMapping(value = "/confirmPlan", method = RequestMethod.GET)
	@Timed
	public String createAccount(Model model) {
		return "/company/confirmPlan";
	}

	@RequestMapping(value = "/setup", method = RequestMethod.GET)
	@Timed
	public String confirmation(Model model) {
		return "/company/setUpTrial1";
	}

	@RequestMapping(value = "/taskCompletionNotificationPage", method = RequestMethod.GET)
	@Timed
	public String notificationPage(Model model) {
		return "/company/taskCompletionNotificationPage";
	}

	@RequestMapping(value = "/setup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Map<String, Boolean>> stepCreateAccount(
			@Valid @RequestBody CreateAccountDTO createAccountDTO) {
		log.debug("Web request to set up new company automatically using data : {}", createAccountDTO);
		// validate login already exist
		// validate shotname alreadyExist
		Pattern p = Pattern.compile("([0-9])");
		Matcher m = p.matcher(createAccountDTO.getCompanyName());
		if (m.find()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("Company Name",
					"company name not contain number", "company name not contain number")).body(null);
		}
		if (companyService.findByAlias(createAccountDTO.getShortName()).isPresent()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("Short Name", "shortnameexists", "Short Name already in use"))
					.body(null);
		}
		Map<String, Boolean> map = trialSetUp(createAccountDTO);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	private Map<String, Boolean> trialSetUp(CreateAccountDTO createAccountDTO) {
		Map<String, Boolean> map = new HashMap<>();
		try {
			// company
			Company company = companyTrialSetUpService.createCompany(createAccountDTO.getCompanyName(),
					createAccountDTO.getShortName(), createAccountDTO.getEmail(), createAccountDTO.getCountry(),
					createAccountDTO.getGstNo());
			map.put("1", true);

			companyTrialSetUpService.createAdmin(createAccountDTO, company);
			map.put("2", true);

			RegistrationData registrationData = new RegistrationData();
			registrationData.setCompany(company);
			registrationData.setCompanyName(createAccountDTO.getCompanyName());
			registrationData.setCountry(company.getCountry());
			registrationData.setFullName(createAccountDTO.getName());
			registrationData.setShortName(createAccountDTO.getShortName());
			registrationDataRepository.save(registrationData);

			Optional<User> user = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin());
			boolean val = getByAutherisation(user);
			if (val) {
				partnerCompanyRepository.save(new PartnerCompany(company, user.get()));
				syncOperationService.saveSyncOperationForPartners(company.getPid(),
						"PRODUCTCATEGORY,PRODUCTGROUP,PRODUCTPROFILE,PRODUCTGROUP_PRODUCTPROFILE,PRICE_LEVEL,PRICE_LEVEL_LIST,STOCK_LOCATION,OPENING_STOCK,ACCOUNT_PROFILE,LOCATION,LOCATION_HIRARCHY,LOCATION_ACCOUNT_PROFILE,RECEIVABLE_PAYABLE,ACCOUNT_PROFILE_CLOSING_BALANCE,TAX_MASTER,PRODUCT_PROFILE_TAX_MASTER,SALES_ORDER,RECEIPT");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
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
}
