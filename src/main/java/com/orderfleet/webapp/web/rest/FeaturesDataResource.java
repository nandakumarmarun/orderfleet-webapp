package com.orderfleet.webapp.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.FeaturesData;
import com.orderfleet.webapp.domain.RegistrationData;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.Feature;
import com.orderfleet.webapp.repository.FeaturesDataRepository;
import com.orderfleet.webapp.repository.PartnerCompanyRepository;
import com.orderfleet.webapp.repository.RegistrationDataRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.CompanyTrialSetUpService;
import com.orderfleet.webapp.service.UserService;

@RequestMapping("/web")
@Controller
@Secured({ AuthoritiesConstants.SITE_ADMIN, AuthoritiesConstants.PARTNER })
public class FeaturesDataResource {

	@Inject
	private FeaturesDataRepository featuresDataRepository;

	@Inject
	private RegistrationDataRepository registrationDataRepository;

	@Inject
	private CompanyTrialSetUpService companyTrialSetUpService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyService companyService;

	@Inject
	private PartnerCompanyRepository partnerCompanyRepository;

	@Inject
	private UserService userService;

	@RequestMapping(value = "/get-features-data", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<Boolean> getFeaturesData() {
		List<User> users = userRepository.findAllByCompanyId();
		boolean value = true;
		if (users.size() > 1) {
			value = false;
		} else if (users.get(0).getLogin().equals("siteadmin")) {
			value = false;
		}
		return new ResponseEntity<Boolean>(value, HttpStatus.OK);
	}

	@RequestMapping(value = "/load-features-data", method = RequestMethod.GET)
	@Timed
	public String loadFeaturesPage(Model model) {
		// model.addAttribute("companyPid", companyPid);
		// model.addAttribute("userCount", userCount);
		return "/company/loadFeaturesPage";
	}

	@RequestMapping(value = "/features-configuration-page", method = RequestMethod.GET)
	@Timed
	public String featuresConfigurationPage(Model model) {
		List<Company> companies = new ArrayList<>();
		Optional<User> user = userService.getUserWithAuthoritiesByLogin(SecurityUtils.getCurrentUserLogin());
		boolean opAuthUser = getByAutherisation(user);
		if (opAuthUser) {
			companies = partnerCompanyRepository.findAllCompaniesByPartnerPid(user.get().getPid());
		} else {
			companies = companyService.findAllCompany();
		}
		model.addAttribute("companies", companies);
		// model.addAttribute("companies", companyService.findAll());
		return "/company/featureConfigurationPage";
	}

	@RequestMapping(value = "/load-customised-configuration", method = RequestMethod.GET)
	@Timed
	public String loadCustomisedConfiguration(Model model) {
		model.addAttribute("registerData", registrationDataRepository.findOneByCompanyId());
		return "/company/customisedConfiguration";
	}

	@RequestMapping(value = "/create-users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> createUsers(@RequestParam String companyPid, @RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		boolean condition = false;
		try {

			companyTrialSetUpService.createUsers(registrationData.getShortName(), registrationData.getCompany(),
					userCount, registrationData.getUserCount() + 1);
			condition = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true) {
			Optional<FeaturesData> featuresData = featuresDataRepository
					.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Create_Users);
			if (!featuresData.isPresent()) {
				FeaturesData featuresData2 = new FeaturesData();
				featuresData2.setCompany(registrationData.getCompany());
				featuresData2.setFeature(Feature.Create_Users);
				featuresDataRepository.save(featuresData2);
			}

		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/default-set-up", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> setUpDefaultCompanyData(@RequestParam String companyPid,
			@RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Default_Company_Data_SetUp);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.setUpDefaultCompanyData(registrationData.getCompany());
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Default_Company_Data_SetUp);
			featuresDataRepository.save(featuresData);
		} else if (optionalfeaturesData.isPresent()) {
			condition = true;
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/create-employees", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> createEmployees(@RequestParam String companyPid, @RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Employee_Created);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.createEmployees(registrationData.getFullName(), registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.createAdditionalEmployees(registrationData.getFullName(),
						registrationData.getCompany(), userCount);
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Employee_Created);
			featuresDataRepository.save(featuresData);
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/employee-hierarchy-setup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> employeeHierarchy(@RequestParam String companyPid, @RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Employee_Hierarchy_Created);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.createEmployeeHierarchies(registrationData.getFullName(),
						registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.createAdditionalEmployeeHierarchies(registrationData.getFullName(),
						registrationData.getCompany(), userCount);
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Employee_Hierarchy_Created);
			featuresDataRepository.save(featuresData);
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/setup-document-company-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> setUpDocumentCompanyData(@RequestParam String companyPid,
			@RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Document_Company_Data_SetUp);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.setUpDocumentCompanyData(registrationData.getCompany());
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Document_Company_Data_SetUp);
			featuresDataRepository.save(featuresData);
		} else if (optionalfeaturesData.isPresent()) {
			condition = true;
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/setup-activity-company-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> setUpActivityCompanyData(@RequestParam String companyPid,
			@RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Activity_Company_Data_SetUp);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.setUpActivityCompanyData(registrationData.getCompany());
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Activity_Company_Data_SetUp);
			featuresDataRepository.save(featuresData);
		} else if (optionalfeaturesData.isPresent()) {
			condition = true;
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/assign-employee-to-locations", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> assignEmployeeToLocations(@RequestParam String companyPid,
			@RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository.findByCompanyIdAndFeature(
				registrationData.getCompany().getId(), Feature.Employee_To_Location_Assigned);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.assignEmployeeToLocations(registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.assignAdditionalEmployeeToLocations(registrationData.getCompany(), userCount);
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Employee_To_Location_Assigned);
			featuresDataRepository.save(featuresData);
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/assign-to-users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> assignToUsers(@RequestParam String companyPid, @RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository.findByCompanyIdAndFeature(
				registrationData.getCompany().getId(), Feature.Default_Data_To_Users_Assigned);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.assignToUsers(registrationData.getFullName(), registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.assignToAdditionalUsers(registrationData.getCompany(), userCount);
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Default_Data_To_Users_Assigned);
			featuresDataRepository.save(featuresData);
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/dashboard-features", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> dashboardUsers(@RequestParam String companyPid, @RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.Dashboard_SetUp);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.dashboardUsers(registrationData.getFullName(), registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.dashboardAdditionalUsers(registrationData.getFullName(),
						registrationData.getCompany(), userCount, registrationData.getUserCount());
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Dashboard_SetUp);
			featuresDataRepository.save(featuresData);
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/assign-document-company-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> assignDocumentCompanyData(@RequestParam String companyPid,
			@RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository.findByCompanyIdAndFeature(
				registrationData.getCompany().getId(), Feature.Document_Company_Data_Assigned);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.assignDocumentCompanyData(registrationData.getCompany());
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Document_Company_Data_Assigned);
			featuresDataRepository.save(featuresData);
		} else if (optionalfeaturesData.isPresent()) {
			condition = true;
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/assign-menuItem-to-users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> assignMenuItemToUsers(@RequestParam String companyPid, @RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository
				.findByCompanyIdAndFeature(registrationData.getCompany().getId(), Feature.MenuItem_To_Users_Assigned);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.assignMenuItemToUsers(registrationData.getFullName(),
						registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.assignMenuItemToAdditionalUsers(registrationData.getCompany(), userCount);
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.MenuItem_To_Users_Assigned);
			featuresDataRepository.save(featuresData);
		}
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/assign-mobilemenuItem-to-users", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Boolean> assignMobileMenuItemToUsers(@RequestParam String companyPid,
			@RequestParam int userCount) {
		RegistrationData registrationData = registrationDataRepository.findAllByCompanyPid(companyPid);
		Optional<FeaturesData> optionalfeaturesData = featuresDataRepository.findByCompanyIdAndFeature(
				registrationData.getCompany().getId(), Feature.Mobile_MenuItem_To_Users_Assigned);
		boolean condition = false;
		try {
			if (!optionalfeaturesData.isPresent()) {
				companyTrialSetUpService.assignMobileMenuItemToUsers(registrationData.getFullName(),
						registrationData.getCompany());
				condition = true;
			} else {
				companyTrialSetUpService.assignMobileMenuItemToAdditionalUsers(registrationData.getCompany(),
						userCount);
				condition = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (condition == true && !optionalfeaturesData.isPresent()) {
			FeaturesData featuresData = new FeaturesData();
			featuresData.setCompany(registrationData.getCompany());
			featuresData.setFeature(Feature.Mobile_MenuItem_To_Users_Assigned);
			featuresDataRepository.save(featuresData);
		}
		registrationData.setUserCount(registrationData.getUserCount() + userCount);
		registrationDataRepository.save(registrationData);
		return new ResponseEntity<Boolean>(condition, HttpStatus.OK);

	}

	@RequestMapping(value = "/getUserCountByCompany", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<Integer> getUserCountByCompany(@RequestParam String companyPid) {
		Integer count = registrationDataRepository.findUserCountByCompany(companyPid);
		return new ResponseEntity<Integer>(count, HttpStatus.OK);

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
