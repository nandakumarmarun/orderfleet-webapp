package com.orderfleet.webapp.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.AccountingVoucherColumn;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.DashboardUser;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentAccountType;
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.DocumentPriceLevel;
import com.orderfleet.webapp.domain.DocumentProductCategory;
import com.orderfleet.webapp.domain.DocumentProductGroup;
import com.orderfleet.webapp.domain.DocumentStockCalculation;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.EmployeeHierarchy;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormElementValue;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.InventoryVoucherColumn;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.MenuItem;
import com.orderfleet.webapp.domain.MobileConfiguration;
import com.orderfleet.webapp.domain.MobileMenuItemGroup;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PrimarySecondaryDocument;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ReceiptDocument;
import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserActivity;
import com.orderfleet.webapp.domain.UserDocument;
import com.orderfleet.webapp.domain.UserFavouriteDocument;
import com.orderfleet.webapp.domain.UserMenuItem;
import com.orderfleet.webapp.domain.UserMobileMenuItemGroup;
import com.orderfleet.webapp.domain.UserPriceLevel;
import com.orderfleet.webapp.domain.UserProductCategory;
import com.orderfleet.webapp.domain.UserProductGroup;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.CartType;
import com.orderfleet.webapp.domain.enums.CompanyType;
import com.orderfleet.webapp.domain.enums.ContactManagement;
import com.orderfleet.webapp.domain.enums.DashboardItemConfigType;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.DashboardUIType;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.Industry;
import com.orderfleet.webapp.domain.enums.InventoryVoucherUIType;
import com.orderfleet.webapp.domain.enums.PartnerIntegrationSystem;
import com.orderfleet.webapp.domain.enums.ReceiverSupplierType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;
import com.orderfleet.webapp.domain.enums.VoucherNumberGenerationType;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.AuthorityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CountryRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.DistrictRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.DocumentAccountTypeRepository;
import com.orderfleet.webapp.repository.DocumentAccountingVoucherColumnRepository;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.DocumentInventoryVoucherColumnRepository;
import com.orderfleet.webapp.repository.DocumentPriceLevelRepository;
import com.orderfleet.webapp.repository.DocumentProductCategoryRepository;
import com.orderfleet.webapp.repository.DocumentProductGroupRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EmployeeHierarchyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.MenuItemRepository;
import com.orderfleet.webapp.repository.MobileConfigurationRepository;
import com.orderfleet.webapp.repository.MobileMenuItemGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ReceiptDocumentRepository;
import com.orderfleet.webapp.repository.SnrichProductCompanyRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.repository.StaticFormJSCodeRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.repository.UserFavouriteDocumentRepository;
import com.orderfleet.webapp.repository.UserMenuItemRepository;
import com.orderfleet.webapp.repository.UserMobileMenuItemGroupRepository;
import com.orderfleet.webapp.repository.UserPriceLevelRepository;
import com.orderfleet.webapp.repository.UserProductCategoryRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.CreateAccountDTO;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;
import com.orderfleet.webapp.web.rest.util.DocumentColumnsUtil;
import com.orderfleet.webapp.web.rest.util.MenuItemUtil;

@Service
@Transactional
public class CompanyTrialSetUpService {

	private final Logger log = LoggerFactory.getLogger(CompanyTrialSetUpService.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CountryRepository countryRepository;

	@Inject
	private StateRepository stateRepository;

	@Inject
	private DistrictRepository districtRepository;

	@Inject
	private AuthorityRepository authorityRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserMenuItemRepository userMenuItemRepository;

	@Inject
	private MenuItemRepository menuItemRepository;

	@Inject
	private MobileMenuItemGroupRepository mobileMenuItemGroupRepository;

	@Inject
	private UserMobileMenuItemGroupRepository userMobileMenuItemGroupRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private EmployeeHierarchyRepository employeeHierarchyRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private FormElementTypeRepository formElementTypeRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Inject
	private DocumentFormsRepository documentFormsRepository;

	@Inject
	private DocumentAccountTypeRepository documentAccountTypeRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private DocumentInventoryVoucherColumnRepository documentInventoryVoucherColumnRepository;

	@Inject
	private DocumentAccountingVoucherColumnRepository documentAccountingVoucherColumnRepository;

	@Inject
	private DocumentPriceLevelRepository documentPriceLevelRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;

	@Inject
	private UserActivityRepository userActivityRepository;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private UserPriceLevelRepository userPriceLevelRepository;

	@Inject
	private UserFavouriteDocumentRepository userFavouriteDocumentRepository;

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private ReceiptDocumentRepository receiptDocumentRepository;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private UserProductCategoryRepository userProductCategoryRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private DocumentStockCalculationRepository documentStockCalculationRepository;

	@Inject
	private StaticFormJSCodeRepository staticFormJSCodeRepository;

	@Inject
	private DocumentProductCategoryRepository documentProductCategoryRepository;

	@Inject
	private DocumentProductGroupRepository documentProductGroupRepository;

	@Inject
	private SnrichProductCompanyRepository snrichProductCompanyRepository;

	@Inject
	private MobileConfigurationRepository mobileConfigurationRepository;

	public void setUpDefaultCompanyData(Company company) {
		createMobileConfiguration(company);
		createTerritory(company);
		createAccountTypes(company);
		createDepartments(company);
		createDesignations(company);
		createPriceLevels(company);
		createStockLocation(company);
		createDivision(company);
		createProductGroup(company);
		createProductCategory(company);
		AccountProfile accountProfile = createAccountProfile(company);
		assignAccountToTerritory(company, accountProfile);
	}

	public void setUpActivityCompanyData(Company company) {
		Activity activity = createActivity(company);
		assignDocumentToActivity(company, activity);
	}

	public void setUpOrderActivityCompanyData(Company company) {
		Activity activity = createActivity(company);
		assignOrderProDocumentToActivity(company, activity);
	}

	public void setUpDocumentCompanyData(Company company) {
		List<Document> documents = createDocumentProfile(company);
		List<FormElement> formElements = createFormElement(company);
		Form form = createForm(company);

		assignStockCalculation(company, documents);
		assignSupplierReceiverAccountType(company, documents);
		assignByToAccountType(company, documents);
		assignFormtoFormElement(form, company, formElements);
		assignDocumenttoForm(form, documents);
		assignDocumentInventoryVoucherColumn(company, documents);
		assignDocumentAccountingVoucherColumn(company, documents);
		assignPriceLevelToDocument(company, documents);
		assignStockLocationSourceToDocument(company, documents);

	}

	public void assignDocumentCompanyData(Company company) {
		receiptDocument(company);
		primarySecondaryDocument(company);
	}

	public Company createCompany(String companyName, String shortName, String email, String country, String gstNo) {

		// save company
		Company company = new Company();
		company.setPid(CompanyService.PID_PREFIX + RandomUtil.generatePid());
		company.setLegalName(companyName);
		company.setAlias(shortName);
		company.setCompanyType(CompanyType.GENERAL);
		company.setCountry(countryRepository.findByCode(country));
		company.setDistrict(districtRepository.findByCode("ER"));
		company.setState(stateRepository.findByCode("KL"));
		company.setIndustry(Industry.GENERAL);
		company.setEmail(email);
		company.setGstNo(gstNo);
		// Change Later
		company.setLocation("No Location");
		company.setIsActivated(true);
		company = companyRepository.save(company);
		log.debug("Automatic company created successfully with company name : {}", company.getLegalName());
		return company;
	}

	public void createAdmin(CreateAccountDTO createAccountDTO, Company company) {
		createAdminUser(createAccountDTO, company);
	}

	public void createUsers(String shortName, Company company, int userCount, int userStartNumber) {
		if (userStartNumber == 1) {
			createManagerUser(shortName, company);
		}
		createExecutiveUser(shortName, company, userCount, userStartNumber);
	}

	public void createEmployees(String name, Company company) {
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
//			if (user.getFirstName().equals(name)) {
//				createEmployee(user);
//			} else if (user.getFirstName().equals("aitrich")) {
//				createEmployee(user);
//			} else {
			createEmployee(user);
//			}
		}
	}

	public void createAdditionalEmployees(String name, Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			createEmployee(user);
		}
	}

	public void assignEmployeeToLocations(Company company) {
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			assignEmployeeToLocation(employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin()));
		}
	}

	public void assignAdditionalEmployeeToLocations(Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			assignEmployeeToLocation(employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin()));
		}
	}

	public void createEmployeeHierarchies(String name, Company company) {
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		EmployeeProfile adminEmployeeProfile = new EmployeeProfile();
		for (User user : users) {
			if (user.getFirstName().equals(name)) {
				adminEmployeeProfile = employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin());
				createEmployeeHierarchy(adminEmployeeProfile, null, company);
			} else if (user.getFirstName().equals("aitrich")) {

			} else {
				createEmployeeHierarchy(employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin()),
						adminEmployeeProfile, company);
			}
		}

	}

	public void createAdditionalEmployeeHierarchies(String name, Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		EmployeeProfile adminEmployeeProfile = new EmployeeProfile();
		for (User user : users) {
			if (user.getFirstName().equals(name)) {
				adminEmployeeProfile = employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin());
			}
		}
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			createEmployeeHierarchy(employeeProfileRepository.findEmployeeProfileByUserLogin(user.getLogin()),
					adminEmployeeProfile, company);
		}
	}

	public void assignMenuItemToUsers(String name, Company company) {

		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			if (user.getFirstName().equals(name)) {
				assignMenuItemToUser(user, MenuItemUtil.adminMenuItemIds());
			} else if (user.getFirstName().equals("aitrich")) {
				assignMenuItemToUser(user, MenuItemUtil.managerMenuItemIds());
			} else {
				assignMenuItemToUser(user, MenuItemUtil.employeeUserMenuItemIds());
			}
		}
	}

	// for orderpro users
	public void assignCustomMenuItemToUsers(String name, Company company) {

		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			if (user.getFirstName().equals("Admin")) {
				if (userMenuItemRepository.findByUserPid(user.getPid()).size() == 0) {
					// assignMenuItemToUser(user, MenuItemUtil.employeeUserMenuItemIds());
					assignMenuItemToUser(user, MenuItemUtil.customMenuItemIds());
				}
			} else {
				assignMenuItemToUser(user, MenuItemUtil.employeeUserMenuItemIds());
			}
		}
	}

	// for orderpro users
	public void assignCustomMenuItemToAdditionalUsers(Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			assignMenuItemToUser(user, MenuItemUtil.employeeUserMenuItemIds());
		}
	}

	public void assignMenuItemToAdditionalUsers(Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			assignMenuItemToUser(user, MenuItemUtil.employeeUserMenuItemIds());
		}
	}

	public void assignMobileMenuItemToUsers(String name, Company company) {
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			if (!user.getFirstName().equals(name) && !user.getFirstName().equals("aitrich")) {
				assignMobileMenuItemToUser(user);
			}
		}
	}

	public void assignMobileMenuItemToAdditionalUsers(Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			assignMobileMenuItemToUser(user);
		}
	}

	// for orderpro users
	public void assignCustomMobileMenuItemToUsers(String name, Company company) {
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			assignCustomMobileMenuItemToUser(user);
		}
	}

	// for orderpro users
	public void assignCustomMobileMenuItemToAdditionalUsers(Company company, int userCount) {
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			assignCustomMobileMenuItemToUser(user);
		}
	}

	public void assignToUsers(String name, Company company) {

		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			if (user.getFirstName().equals(name)) {
				assignUserToDocument(company, user);
				assignUserToActivity(company, user);
				assignUserToPriceLevel(company, user);
				assignUserToStockLocation(company, user);
				assignUserToFavouriteDocument(user);
			} else if (user.getFirstName().equals("aitrich")) {
				assignUserToActivity(company, user);
				assignUserToPriceLevel(company, user);
				assignUserToStockLocation(company, user);
				assignUserToFavouriteDocument(user);
				assignUserToDocument(company, user);
			} else {
				assignUserToActivity(company, user);
				assignUserToPriceLevel(company, user);
				assignUserToStockLocation(company, user);
				assignUserToFavouriteDocument(user);
				assignUserToDocument(company, user);
			}
		}
	}

	// for orderpro users
	public void assignToOrderProUsers(String name, Company company, int userCount) {
		PartnerIntegrationSystem partnerIntergrationSystem = snrichProductCompanyRepository
				.findPartnerIntegrationSystemByCompanyId(company.getId());
		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);

			assignUserToActivity(company, user);
			// custom user-stocklocation and user-pricelevel for tally partners
			if (partnerIntergrationSystem.equals(PartnerIntegrationSystem.TALLY)) {
				assignNewUserToPriceLevel(company, user);
				assignNewUserToStockLocation(company, user);
			} else {
				assignUserToPriceLevel(company, user);
				assignUserToStockLocation(company, user);
			}
			assignOrderProUserToFavouriteDocument(user);
			assignUserToDocument(company, user);
		}
	}

	public void assignToAdditionalUsers(Company company, int userCount) {

		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			assignUserToActivity(company, user);
			assignUserToPriceLevel(company, user);
			assignUserToStockLocation(company, user);
			assignUserToFavouriteDocument(user);
			assignUserToDocument(company, user);
		}

	}

	public void dashboardUsers(String name, Company company) {
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for (User user : users) {
			if (!user.getFirstName().equals(name) && !user.getFirstName().equals("Aitrich")) {
				createDashboardUser(user, company);
			}
		}
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
		createDashboardItems(documents, company);
	}

	public void dashboardAdditionalUsers(String name, Company company, int userCount, int userStartNumber) {

		if (userStartNumber == 1) {
			List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
			createDashboardItems(documents, company);
		}

		List<User> users = userRepository.findAllByCompanyPidSortedById(company.getPid());
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			createDashboardUser(user, company);
		}

	}

	private User createAdminUser(CreateAccountDTO createAccountDTO, Company company) {
		User admin = new User();
		admin.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
		admin.setCompany(company); // set company
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.ADMIN);
		Set<Authority> authorities = new HashSet<>();
		admin.setLogin(createAccountDTO.getEmail());
		// new user gets initially a generated password
		admin.setPassword(createAccountDTO.getPassword());
		admin.setFirstName(createAccountDTO.getName());
		admin.setLastName(createAccountDTO.getShortName());
		admin.setEmail(createAccountDTO.getEmail());
		// Change Later
		admin.setMobile("99999999999");
		admin.setLangKey("en");
		admin.setActivated(true);
		admin.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		admin.setAuthorities(authorities);
		admin.setDashboardUIType(DashboardUIType.TW);
		admin = userRepository.save(admin);
		log.debug("Automatic admin user created with login : {}", admin.getLogin());
		return admin;

	}

	// for orderpro users
	public User createOrderProAdminUser(Company company, RegistrationDto registrationDto) {
		User admin = new User();
		admin.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
		admin.setCompany(company); // set company
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.ORDERPRO_ADMIN);
		Set<Authority> authorities = new HashSet<>();
		// admin.setLogin("admin" + "@" + company.getAlias().toLowerCase());
		admin.setLogin(registrationDto.getUserName());
		// new user gets initially a generated password
		// admin.setPassword("admin" + "@" + company.getAlias().toLowerCase());
		admin.setPassword(registrationDto.getPassword());
		admin.setFirstName("Admin");
		admin.setLastName(company.getAlias());
		admin.setEmail("admin@" + company.getAlias().toLowerCase());
		// admin.setEmail(registrationDto.getEmail());
		// Change Later
		admin.setMobile("99999999999");
		admin.setLangKey("en");
		admin.setActivated(true);
		admin.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		admin.setAuthorities(authorities);
		admin.setDashboardUIType(DashboardUIType.TW);
		admin = userRepository.save(admin);
		log.debug("Automatic admin user created with login : {}", admin.getLogin());
		return admin;

	}

	private User createManagerUser(String companyShortName, Company company) {
		User manager = new User();
		manager.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
		manager.setCompany(company); // set company
		Authority authority = authorityRepository.findOne(AuthoritiesConstants.MANAGER);
		Set<Authority> authorities = new HashSet<>();
		manager.setLogin("aitrich" + "@" + companyShortName.toLowerCase());
		// new user gets initially a generated password
		manager.setPassword("aitrich@" + companyShortName.toLowerCase());
		manager.setFirstName("Aitrich");
		manager.setLastName(companyShortName);
		manager.setEmail("aitrich@" + companyShortName.toLowerCase());
		// Change Later
		manager.setMobile("99999999999");
		manager.setLangKey("en");
		manager.setActivated(true);
		manager.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		manager.setAuthorities(authorities);
		manager.setDashboardUIType(DashboardUIType.TW);
		manager = userRepository.save(manager);
		log.debug("Automatic manager user created with login : {}", manager.getLogin());
		return manager;
	}

	private List<User> createExecutiveUser(String companyShortName, Company company, int userCount,
			int userStartNumber) {
		List<User> executiveUsers = new ArrayList<>();
		for (int i = 1; i <= userCount; i++) {
			String prefix = "so" + userStartNumber;
			User executive = new User();
			executive.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
			executive.setCompany(company); // set company
			Authority authority = authorityRepository.findOne(AuthoritiesConstants.ROLE_EXECUTIVE);
			Set<Authority> authorities = new HashSet<>();
			executive.setLogin(prefix.toLowerCase() + "@" + companyShortName.toLowerCase());
			// new user gets initially a generated password
			executive.setPassword(prefix + "@" + companyShortName.toLowerCase());
			executive.setFirstName(prefix.toUpperCase());
			executive.setLastName(companyShortName);
			executive.setEmail(prefix + "@" + companyShortName.toLowerCase());
			// Change Later
			executive.setMobile("99999999999");
			executive.setLangKey("en");
			executive.setActivated(true);
			executive.setActivationKey(RandomUtil.generateActivationKey());
			authorities.add(authority);
			executive.setAuthorities(authorities);
			executive.setShowAllUsersData(false);
			executive.setDashboardUIType(DashboardUIType.TW);
			executive = userRepository.save(executive);
			log.debug("Automatic executive user created with login : {}", executive.getLogin());
			executiveUsers.add(executive);
			userStartNumber = userStartNumber + 1;
		}
		return executiveUsers;
	}

	// for orderpro users
	public List<User> createExecutiveUsers(String companyShortName, Company company, int userCount,
			Long userStartNumber) {
		List<User> executiveUsers = new ArrayList<>();
		for (int i = 1; i <= userCount; i++) {
			String prefix = "so" + userStartNumber;
			User executive = new User();
			executive.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
			executive.setCompany(company); // set company
			Authority authority = authorityRepository.findOne(AuthoritiesConstants.ROLE_EXECUTIVE);
			Set<Authority> authorities = new HashSet<>();
			executive.setLogin(prefix.toLowerCase() + "@" + companyShortName.toLowerCase());
			// new user gets initially a generated password
			executive.setPassword(prefix.toLowerCase() + "@" + companyShortName.toLowerCase());
			executive.setFirstName(prefix.toUpperCase());
			executive.setLastName(companyShortName);
			executive.setEmail(prefix + "@" + companyShortName.toLowerCase());
			// Change Later
			executive.setMobile("99999999999");
			executive.setLangKey("en");
			executive.setActivated(true);
			executive.setActivationKey(RandomUtil.generateActivationKey());
			authorities.add(authority);
			executive.setAuthorities(authorities);
			executive.setShowAllUsersData(false);
			executive.setDashboardUIType(DashboardUIType.TW);
			executive = userRepository.save(executive);
			log.debug("Automatic executive user created with login : {}", executive.getLogin());
			executiveUsers.add(executive);
			userStartNumber = userStartNumber + 1;
		}
		return executiveUsers;
	}

	private EmployeeProfile createEmployee(User user) {
		EmployeeProfile employeeProfile = new EmployeeProfile();
		employeeProfile.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
		employeeProfile.setName(user.getFirstName());
		employeeProfile.setDesignation(designationRepository
				.findByCompanyIdAndNameIgnoreCase(user.getCompany().getId(), "Sales Officer").get());
		employeeProfile.setDepartment(
				departmentRepository.findByCompanyIdAndNameIgnoreCase(user.getCompany().getId(), "Sales").get());
		employeeProfile.setEmail(user.getEmail());
		employeeProfile.setPhone(user.getMobile());
		employeeProfile.setUser(user);
		// Change Later
		employeeProfile.setAddress("");
		employeeProfile.setCompany(user.getCompany());
		employeeProfile = employeeProfileRepository.save(employeeProfile);
		return employeeProfile;

	}

	private void assignMenuItemToUser(User user, Set<Long> menuItemIds) {
		List<MenuItem> menuItems = menuItemRepository.findByMenuItemIdsIn(new ArrayList<>(menuItemIds));
		List<UserMenuItem> userMenuItems = new ArrayList<>();
		for (MenuItem menuItem : menuItems) {
			userMenuItems.add(new UserMenuItem(user, menuItem));
		}
		userMenuItemRepository.save(userMenuItems);
	}

	private void assignMobileMenuItemToUser(User user) {
		MobileMenuItemGroup mobileMenuItemGroup = mobileMenuItemGroupRepository.findByAlias("PMM").get();
		UserMobileMenuItemGroup userMobileMenuItemGroup = new UserMobileMenuItemGroup();
		userMobileMenuItemGroup.setMobileMenuItemGroup(mobileMenuItemGroup);
		userMobileMenuItemGroup.setUser(user);
		userMobileMenuItemGroupRepository.save(userMobileMenuItemGroup);
	}

	// for orderpro users
	private void assignCustomMobileMenuItemToUser(User user) {
		MobileMenuItemGroup mobileMenuItemGroup = mobileMenuItemGroupRepository.findByAlias("OMM").get();
		UserMobileMenuItemGroup userMobileMenuItemGroup = new UserMobileMenuItemGroup();
		userMobileMenuItemGroup.setMobileMenuItemGroup(mobileMenuItemGroup);
		userMobileMenuItemGroup.setUser(user);
		userMobileMenuItemGroupRepository.save(userMobileMenuItemGroup);
	}

	private void createEmployeeHierarchy(EmployeeProfile employee, EmployeeProfile parent, Company company) {
		EmployeeHierarchy employeeHierarchy = new EmployeeHierarchy();
		employeeHierarchy.setCompany(company);
		employeeHierarchy.setEmployee(employee);
		if (parent != null) {
			employeeHierarchy.setParent(parent);
		} else {
			employeeHierarchy.setParent(null);
		}
		employeeHierarchyRepository.save(employeeHierarchy);

	}

	private void createTerritory(Company company) {
		Location location = new Location();
		location.setName("Territory");
		location.setAlias("Territory");
		location.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
		location.setCompany(company);
		location = locationRepository.save(location);
		locationHierarchyRepository.insertTrialLocationHierarchyWithNoParent(1L, location.getCompany().getId(),
				location.getId());
	}

	private void createMobileConfiguration(Company company) {
		MobileConfiguration mobileConfiguration = new MobileConfiguration();
		mobileConfiguration.setAddNewCustomer(true);
		mobileConfiguration.setAttendanceMarkingRequired(false);
		mobileConfiguration.setBuildDueDetails(false);
		mobileConfiguration.setCompany(company);
		mobileConfiguration.setCreateTerritory(false);
		mobileConfiguration.setDayPlanDownloadRequired(false);
		mobileConfiguration.setHasGeoTag(false);
		mobileConfiguration.setHasPostDatedVoucherEnabled(false);
		mobileConfiguration.setIncludeAddressInAccountlist(true);
		mobileConfiguration.setMasterDataUpdateRequired(false);
		mobileConfiguration.setPid(MobileConfigurationService.PID_PREFIX + RandomUtil.generatePid());
		mobileConfiguration.setPromptAttendanceMarking(false);
		mobileConfiguration.setPromptDayPlanUpdate(false);
		mobileConfiguration.setPromptMasterdataUpdate(false);
		mobileConfiguration.setPromptVehicleMaster(false);
		mobileConfiguration.setRealTimeProductPriceEnabled(false);
		mobileConfiguration.setShowAllActivityCount(false);
		mobileConfiguration.setTaskExecutionOfflineSave(true);
		mobileConfiguration.setVoucherNumberGenerationType(VoucherNumberGenerationType.TYPE_1);
		mobileConfiguration.setInventoryVoucherUIType(InventoryVoucherUIType.TYPE_1);
		mobileConfiguration.setCartType(CartType.NORMAL);
		mobileConfiguration.setKfcEnabled(false);
		mobileConfiguration.setGpsMandatory(false);
		mobileConfiguration.setEnableSecondarySales(false);
		mobileConfiguration.setEnableAttendanceImage(false);
		mobileConfiguration.setSmartSearch(false);
		mobileConfiguration.setSalesOrderDownloadPdf(false);
		mobileConfiguration.setFindLocation(true);
		mobileConfiguration.setEnableDynamicUnit(false);
		mobileConfiguration.setEnableDiscountRoundOffColumn(false);
		mobileConfiguration.setStockLocationProducts(false);
		mobileConfiguration.setSalesOrderAllocation(false);
		mobileConfiguration.setRateWithoutCalculation(false);
		mobileConfiguration.setShowBestPerformerUpload(false);
		mobileConfiguration.setBelowPriceLevel(false);
		mobileConfiguration.setAmountToThreeDecimal(false);
		mobileConfiguration.setEnableGeoFencing(false);
        mobileConfigurationRepository.save(mobileConfiguration);
	}

	private List<AccountType> createAccountTypes(Company company) {
		List<AccountType> accountTypes = new ArrayList<>();
		AccountType accountType = new AccountType();
		accountType.setName("Sundry Debtors");
		accountType.setPid(AccountTypeService.PID_PREFIX + RandomUtil.generatePid());
		accountType.setCompany(company);
		accountType.setActivated(true);
		accountType.setReceiverSupplierType(ReceiverSupplierType.Receiver);
		accountType.setAccountNameType(AccountNameType.GENERAL);
		accountTypes.add(accountType);
		AccountType accountType1 = new AccountType();
		accountType1.setName("Company");
		accountType1.setActivated(true);
		accountType1.setPid(AccountTypeService.PID_PREFIX + RandomUtil.generatePid());
		accountType1.setCompany(company);
		accountType1.setReceiverSupplierType(ReceiverSupplierType.Supplier);
		accountType1.setAccountNameType(AccountNameType.GENERAL);
		accountTypes.add(accountType1);
		return accountTypeRepository.save(accountTypes);
	}

	private void createDepartments(Company company) {
		Department department = new Department();
		department.setName("Sales");
		department.setPid(DepartmentService.PID_PREFIX + RandomUtil.generatePid());
		department.setCompany(company);
		departmentRepository.save(department);
	}

	private void createDesignations(Company company) {
		Designation designation = new Designation();
		designation.setName("Sales Officer");
		designation.setPid(DepartmentService.PID_PREFIX + RandomUtil.generatePid());
		designation.setCompany(company);
		designationRepository.save(designation);
	}

	private PriceLevel createPriceLevels(Company company) {
		PriceLevel priceLevel = new PriceLevel();
		priceLevel.setCompany(company);
		priceLevel.setActivated(true);
		priceLevel.setName("General");
		priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
		priceLevel = priceLevelRepository.save(priceLevel);
		return priceLevel;
	}

	private void createDashboardUser(User user, Company company) {
		DashboardUser dashboardUser = new DashboardUser();
		dashboardUser.setUser(user);
		dashboardUser.setCompany(company);
		dashboardUserRepository.save(dashboardUser);
	}

	private List<Document> createDocumentProfile(Company company) {
		List<Document> documents = new ArrayList<>();
		Document document = new Document();
		document.setName("Sales Order");
		document.setActivityAccount(AccountTypeColumn.Receiver);
		document.setCompany(company);
		document.setDocumentType(DocumentType.INVENTORY_VOUCHER);
		document.setDocumentPrefix("SO");
		document.setPid(DocumentService.PID_PREFIX + RandomUtil.generatePid());
		document.setSave(true);
		document.setVoucherNumberGenerationType(VoucherNumberGenerationType.TYPE_1);
		documents.add(document);

		Document document1 = new Document();
		document1.setName("Receipt");
		document1.setActivityAccount(AccountTypeColumn.By);
		document1.setCompany(company);
		document1.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		document1.setDocumentPrefix("RE");
		document1.setPid(DocumentService.PID_PREFIX + RandomUtil.generatePid());
		document1.setSave(true);
		document1.setVoucherNumberGenerationType(VoucherNumberGenerationType.TYPE_1);
		documents.add(document1);

		Document document2 = new Document();
		document2.setName("No Order");
		document2.setCompany(company);
		document2.setDocumentType(DocumentType.DYNAMIC_DOCUMENT);
		document2.setDocumentPrefix("NO");
		document2.setPid(DocumentService.PID_PREFIX + RandomUtil.generatePid());
		document2.setSave(true);
		document2.setVoucherNumberGenerationType(VoucherNumberGenerationType.TYPE_1);
		documents.add(document2);
		return documentRepository.save(documents);

	}

	private List<FormElement> createFormElement(Company company) {
		List<FormElement> formElements = new ArrayList<>();
		FormElement formElement = new FormElement();
		formElement.setActivated(true);
		formElement.setCompany(company);
		formElement.setName("Reason");
		formElement.setFormElementType(formElementTypeRepository.findOne(3L));
		formElement.setPid(FormElementService.PID_PREFIX + RandomUtil.generatePid());
		// Form Element Value
		Set<FormElementValue> formElementValues = new HashSet<>();
		FormElementValue formElementValue1 = new FormElementValue();
		formElementValue1.setName("Stock Available");
		formElementValue1.setFormElement(formElement);
		formElementValue1.setSortOrder(0);
		formElementValues.add(formElementValue1);

		FormElementValue formElementValue2 = new FormElementValue();
		formElementValue2.setName("Over Outstanding");
		formElementValue2.setFormElement(formElement);
		formElementValue2.setSortOrder(0);
		formElementValues.add(formElementValue2);

		FormElementValue formElementValue3 = new FormElementValue();
		formElementValue3.setName("High Rate");
		formElementValue3.setFormElement(formElement);
		formElementValue3.setSortOrder(0);
		formElementValues.add(formElementValue3);

		formElement.setFormElementValues(formElementValues);
		formElements.add(formElement);

		FormElement formElement1 = new FormElement();
		formElement1.setActivated(true);
		formElement1.setCompany(company);
		formElement1.setName("Any other reason");
		formElement1.setFormElementType(formElementTypeRepository.findOne(1L));
		formElement1.setPid(FormElementService.PID_PREFIX + RandomUtil.generatePid());
		formElements.add(formElement1);
		return formElementRepository.save(formElements);

	}

	private Form createForm(Company company) {
		Form form = new Form();
		form.setActivated(true);
		form.setCompany(company);
		form.setName("No Order");
		form.setMultipleRecord(false);
		form.setPid(FormService.PID_PREFIX + RandomUtil.generatePid());
		form = formRepository.save(form);
		return form;
	}

	private void assignFormtoFormElement(Form form, Company company, List<FormElement> formElements) {
		Set<FormFormElement> formFormElements = new HashSet<FormFormElement>();
		for (FormElement formElement : formElements) {
			if (formElement.getName().equals("Reason")) {
				FormFormElement formFormElement = new FormFormElement();
				formFormElement.setForm(form);
				formFormElement.setFormElement(formElement);
				formFormElement.setSortOrder(1);
				formFormElement.setReportOrder(1);
				formFormElement.setCompany(company);
				formFormElement.setEditable(true);
				formFormElements.add(formFormElement);
			} else {
				FormFormElement formFormElement = new FormFormElement();
				formFormElement.setForm(form);
				formFormElement.setFormElement(formElement);
				formFormElement.setSortOrder(2);
				formFormElement.setReportOrder(2);
				formFormElement.setCompany(company);
				formFormElement.setEditable(true);
				formFormElements.add(formFormElement);
			}
		}
		formFormElementRepository.save(formFormElements);
	}

	private void assignDocumenttoForm(Form form, List<Document> documents) {
		for (Document document : documents) {
			if (document.getName().equals("No Order")) {
				DocumentForms documentForms = new DocumentForms();
				documentForms.setDocument(document);
				documentForms.setForm(form);
				documentForms.setSortOrder(1);
				documentFormsRepository.save(documentForms);
			}
		}

	}

	private void assignStockCalculation(Company company, List<Document> documents) {
		DocumentStockCalculation documentStockCalculation = new DocumentStockCalculation();
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				documentStockCalculation.setDocument(document);
				documentStockCalculation.setOpening(true);
				documentStockCalculationRepository.save(documentStockCalculation);
			}
		}

	}

	private void assignSupplierReceiverAccountType(Company company, List<Document> documents) {
		List<DocumentAccountType> documentAccountTypes = new ArrayList<>();
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				DocumentAccountType documentAccountType = new DocumentAccountType();
				documentAccountType.setAccountTypeColumn(AccountTypeColumn.Receiver);
				AccountType accountType = accountTypeRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), "Sundry Debtors").get();
				documentAccountType.setAccountType(accountType);
				documentAccountType.setDocument(document);
				documentAccountType.setCompany(company);
				documentAccountTypes.add(documentAccountType);
				DocumentAccountType documentAccountType1 = new DocumentAccountType();
				documentAccountType1.setAccountTypeColumn(AccountTypeColumn.Supplier);
				AccountType accountType1 = accountTypeRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), "Company").get();
				documentAccountType1.setAccountType(accountType1);
				documentAccountType1.setDocument(document);
				documentAccountType1.setCompany(company);
				documentAccountTypes.add(documentAccountType1);
			}
		}
		documentAccountTypeRepository.save(documentAccountTypes);

	}

	private void assignByToAccountType(Company company, List<Document> documents) {
		List<DocumentAccountType> documentAccountTypes = new ArrayList<>();
		for (Document document : documents) {
			if (document.getName().equals("Receipt")) {
				DocumentAccountType documentAccountType = new DocumentAccountType();
				documentAccountType.setAccountTypeColumn(AccountTypeColumn.By);
				AccountType accountType = accountTypeRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), "Sundry Debtors").get();
				documentAccountType.setAccountType(accountType);
				documentAccountType.setDocument(document);
				documentAccountType.setCompany(company);
				documentAccountTypes.add(documentAccountType);
				DocumentAccountType documentAccountType1 = new DocumentAccountType();
				documentAccountType1.setAccountTypeColumn(AccountTypeColumn.To);
				AccountType accountType1 = accountTypeRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), "Company").get();
				documentAccountType1.setAccountType(accountType1);
				documentAccountType1.setDocument(document);
				documentAccountType1.setCompany(company);
				documentAccountTypes.add(documentAccountType1);
			}
		}
		documentAccountTypeRepository.save(documentAccountTypes);
	}

	private void assignDocumentInventoryVoucherColumn(Company company, List<Document> documents) {
		for (Document document : documents) {
			if (document.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
				List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = new ArrayList<>();
				for (InventoryVoucherColumn inventoryVoucherColumn : DocumentColumnsUtil.inventory_voucher_column) {
					DocumentInventoryVoucherColumn documentInventoryVoucherColumn = new DocumentInventoryVoucherColumn();
					documentInventoryVoucherColumn.setCompany(company);
					documentInventoryVoucherColumn.setDocument(document);
					if (inventoryVoucherColumn.getName().equals("MRP")) {
						documentInventoryVoucherColumn.setEnabled(false);
					} else {
						documentInventoryVoucherColumn.setEnabled(true);
					}
					documentInventoryVoucherColumn.setInventoryVoucherColumn(inventoryVoucherColumn);
					documentInventoryVoucherColumns.add(documentInventoryVoucherColumn);
				}
				documentInventoryVoucherColumnRepository.save(documentInventoryVoucherColumns);
				saveCompanyStaticJsCode(company, document);
			}

		}
	}

	// save static js code
	private void saveCompanyStaticJsCode(Company company, Document document) {
		String jsCode = "function calculateTotal(inElements){var totalRate,discount,amount,tax,total,outElements,discountAmount;outElements={};totalRate=inElements.selling_rate*inElements.quantity;if(inElements.discount_amount===undefined||inElements.discount_amount===null){discountAmount=0;}else{discountAmount=inElements.discount_amount*inElements.quantity;}amount=totalRate-discountAmount;if(inElements.discount_percentage===undefined||inElements.discount_percentage===null){discount=0;}else{discount=(amount*inElements.discount_percentage)/100;}amount=amount-discount;if(inElements.tax_percentage===undefined||inElements.tax_percentage===null){tax=0;}else{tax=(amount*inElements.tax_percentage)/100;}total=amount+tax;outElements.total=total;return outElements;}";
		StaticFormJSCode staticFormJSCode = new StaticFormJSCode(jsCode, "Calculate Total", document, company);
		staticFormJSCodeRepository.save(staticFormJSCode);
	}

	// error
	private void assignDocumentAccountingVoucherColumn(Company company, List<Document> documents) {
		for (Document document : documents) {
			if (document.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
				List<DocumentAccountingVoucherColumn> documentAccountingVoucherColumns = new ArrayList<>();
				for (AccountingVoucherColumn accountingVoucherColumn : DocumentColumnsUtil.accounting_voucher_column) {
					DocumentAccountingVoucherColumn documentAccountingVoucherColumn = new DocumentAccountingVoucherColumn();
					documentAccountingVoucherColumn.setCompany(company);
					documentAccountingVoucherColumn.setDocument(document);
					documentAccountingVoucherColumn.setEnabled(true);
					documentAccountingVoucherColumn.setAccountingVoucherColumn(accountingVoucherColumn);
					documentAccountingVoucherColumns.add(documentAccountingVoucherColumn);
				}
				documentAccountingVoucherColumnRepository.save(documentAccountingVoucherColumns);
			}
		}
	}

	private void assignPriceLevelToDocument(Company company, List<Document> documents) {
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				DocumentPriceLevel documentPriceLevel = new DocumentPriceLevel(document,
						priceLevelRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "General").get(),
						company);
				documentPriceLevelRepository.save(documentPriceLevel);
			}
		}
	}

	private void createStockLocation(Company company) {
		StockLocation stockLocation = new StockLocation();
		stockLocation.setActivated(true);
		stockLocation.setCompany(company);
		stockLocation.setName("Main Location");
		stockLocation.setAlias("Main Location");
		stockLocation.setStockLocationType(StockLocationType.ACTUAL);
		stockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
		stockLocationRepository.save(stockLocation);

	}

	private void assignStockLocationSourceToDocument(Company company, List<Document> documents) {
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				DocumentStockLocationSource documentStockLocationSource = new DocumentStockLocationSource(
						document, stockLocationRepository
								.findByCompanyIdAndNameIgnoreCase(company.getId(), "Main Location").get(),
						company, false);
				documentStockLocationSourceRepository.save(documentStockLocationSource);
			}
		}
	}

	private Activity createActivity(Company company) {
		Activity activity = new Activity();
		activity.setActivated(true);
		activity.setCompany(company);
		activity.setPid(ActivityService.PID_PREFIX + RandomUtil.generatePid());
		activity.setName("Dealer Visit");
		activity.setAlias("DV");
		activity.setContactManagement(ContactManagement.ENABLED);
		Set<AccountType> activityAccountTypes = new HashSet<AccountType>();
		activityAccountTypes
				.add(accountTypeRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Sundry Debtors").get());
		activity.setActivityAccountTypes(activityAccountTypes);
		activity = activityRepository.save(activity);
		return activity;
	}

	private void assignDocumentToActivity(Company company, Activity activity) {
		List<ActivityDocument> listDocuments = new ArrayList<>();
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				ActivityDocument activityDocument = new ActivityDocument();
				activityDocument.setActivity(activity);
				activityDocument.setCompany(company);
				activityDocument.setDocument(document);
				activityDocument.setSortOrder(1);
				listDocuments.add(activityDocument);
			} else if (document.getName().equals("Receipt")) {
				ActivityDocument activityDocument = new ActivityDocument();
				activityDocument.setActivity(activity);
				activityDocument.setCompany(company);
				activityDocument.setDocument(document);
				activityDocument.setSortOrder(2);
				listDocuments.add(activityDocument);
			} else {
				ActivityDocument activityDocument = new ActivityDocument();
				activityDocument.setActivity(activity);
				activityDocument.setCompany(company);
				activityDocument.setDocument(document);
				activityDocument.setSortOrder(3);
				listDocuments.add(activityDocument);
			}
		}
		activityDocumentRepository.save(listDocuments);
	}

	// for orderpro users
	private void assignOrderProDocumentToActivity(Company company, Activity activity) {
		List<ActivityDocument> listDocuments = new ArrayList<>();
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				ActivityDocument activityDocument = new ActivityDocument();
				activityDocument.setActivity(activity);
				activityDocument.setCompany(company);
				activityDocument.setDocument(document);
				activityDocument.setSortOrder(1);
				listDocuments.add(activityDocument);
			} else if (document.getName().equals("Receipt")) {
				ActivityDocument activityDocument = new ActivityDocument();
				activityDocument.setActivity(activity);
				activityDocument.setCompany(company);
				activityDocument.setDocument(document);
				activityDocument.setSortOrder(2);
				listDocuments.add(activityDocument);
			} else {

			}
		}
		activityDocumentRepository.save(listDocuments);
	}

	private void assignUserToActivity(Company company, User user) {
		Activity activity = activityRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Dealer Visit").get();
		UserActivity userActivity = new UserActivity();
		userActivity.setActivity(activity);
		userActivity.setCompany(company);
		userActivity.setUser(user);
		userActivityRepository.save(userActivity);
	}

	private void assignUserToDocument(Company company, User user) {
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
		List<UserDocument> userDocuments = new ArrayList<>();
		for (Document document : documents) {
			UserDocument userDocument = new UserDocument();
			userDocument.setCompany(company);
			userDocument.setDocument(document);
			userDocument.setUser(user);
			userDocuments.add(userDocument);
		}
		userDocumentRepository.save(userDocuments);
	}

	private void assignUserToStockLocation(Company company, User user) {
		UserStockLocation userStockLocation = new UserStockLocation();
		userStockLocation.setCompany(company);
		userStockLocation.setStockLocation(
				stockLocationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Main Location").get());
		userStockLocation.setUser(user);
		userStockLocationRepository.save(userStockLocation);
	}

	private void assignUserToPriceLevel(Company company, User user) {
		UserPriceLevel userPriceLevel = new UserPriceLevel();
		userPriceLevel.setCompany(company);
		userPriceLevel
				.setPriceLevel(priceLevelRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "General").get());
		userPriceLevel.setUser(user);
		userPriceLevelRepository.save(userPriceLevel);
	}

	private void assignUserToFavouriteDocument(User user) {
		List<UserFavouriteDocument> userFavouriteDocuments = new ArrayList<>();
		List<ActivityDocument> activityDocuments = activityDocumentRepository
				.findAllByCompanyPid(user.getCompany().getPid());
		for (ActivityDocument activityDocument : activityDocuments) {
			if (activityDocument.getDocument().getName().equals("Sales Order")) {
				UserFavouriteDocument userFavouriteDocument = new UserFavouriteDocument();
				userFavouriteDocument.setActivity(activityDocument.getActivity());
				userFavouriteDocument.setCompany(user.getCompany());
				userFavouriteDocument.setDocument(activityDocument.getDocument());
				userFavouriteDocument.setSortOrder(1);
				userFavouriteDocument.setUser(user);
				userFavouriteDocuments.add(userFavouriteDocument);
			} else if (activityDocument.getDocument().getName().equals("Receipt")) {
				UserFavouriteDocument userFavouriteDocument = new UserFavouriteDocument();
				userFavouriteDocument.setActivity(activityDocument.getActivity());
				userFavouriteDocument.setCompany(user.getCompany());
				userFavouriteDocument.setDocument(activityDocument.getDocument());
				userFavouriteDocument.setSortOrder(2);
				userFavouriteDocument.setUser(user);
				userFavouriteDocuments.add(userFavouriteDocument);
			} else {
				UserFavouriteDocument userFavouriteDocument = new UserFavouriteDocument();
				userFavouriteDocument.setActivity(activityDocument.getActivity());
				userFavouriteDocument.setCompany(user.getCompany());
				userFavouriteDocument.setDocument(activityDocument.getDocument());
				userFavouriteDocument.setSortOrder(3);
				userFavouriteDocument.setUser(user);
				userFavouriteDocuments.add(userFavouriteDocument);
			}
		}
		userFavouriteDocumentRepository.save(userFavouriteDocuments);
	}

	// for orderpro users
	private void assignOrderProUserToFavouriteDocument(User user) {
		List<UserFavouriteDocument> userFavouriteDocuments = new ArrayList<>();
		List<ActivityDocument> activityDocuments = activityDocumentRepository
				.findAllByCompanyPid(user.getCompany().getPid());
		for (ActivityDocument activityDocument : activityDocuments) {
			if (activityDocument.getDocument().getName().equals("Sales Order")) {
				UserFavouriteDocument userFavouriteDocument = new UserFavouriteDocument();
				userFavouriteDocument.setActivity(activityDocument.getActivity());
				userFavouriteDocument.setCompany(user.getCompany());
				userFavouriteDocument.setDocument(activityDocument.getDocument());
				userFavouriteDocument.setSortOrder(1);
				userFavouriteDocument.setUser(user);
				userFavouriteDocuments.add(userFavouriteDocument);
			} else if (activityDocument.getDocument().getName().equals("Receipt")) {
				UserFavouriteDocument userFavouriteDocument = new UserFavouriteDocument();
				userFavouriteDocument.setActivity(activityDocument.getActivity());
				userFavouriteDocument.setCompany(user.getCompany());
				userFavouriteDocument.setDocument(activityDocument.getDocument());
				userFavouriteDocument.setSortOrder(2);
				userFavouriteDocument.setUser(user);
				userFavouriteDocuments.add(userFavouriteDocument);
			} else {

			}
		}
		userFavouriteDocumentRepository.save(userFavouriteDocuments);
	}

	private void assignEmployeeToLocation(EmployeeProfile employeeProfile) {
		EmployeeProfileLocation employeeProfileLocation = new EmployeeProfileLocation();
		employeeProfileLocation.setEmployeeProfile(employeeProfile);
		employeeProfileLocation.setLocation(locationRepository
				.findByCompanyIdAndNameIgnoreCase(employeeProfile.getCompany().getId(), "Territory").get());
		employeeProfileLocationRepository.save(employeeProfileLocation);
	}

	private void createDashboardItems(List<Document> documents, Company company) {
		List<DashboardItem> dashboardItems = new ArrayList<>();
		DashboardItem dashboardItem = new DashboardItem();
		// set pid
		dashboardItem.setPid(DashboardItemService.PID_PREFIX + RandomUtil.generatePid());
		dashboardItem.setName("Sales Order");
		dashboardItem.setDashboardItemType(DashboardItemType.DOCUMENT);
		dashboardItem.setDocumentType(DocumentType.INVENTORY_VOUCHER);
		dashboardItem.setTaskPlanType(TaskPlanType.BOTH);
		dashboardItem.setDashboardItemConfigType(DashboardItemConfigType.DASHBOARD);
		for (Document document : documents) {
			if (document.getName().equals("Sales Order")) {
				dashboardItem.getDocuments().add(documentRepository.findOneByPid(document.getPid()).get());
			}
		}
		// set company
		dashboardItem.setCompany(company);
		dashboardItems.add(dashboardItem);

		DashboardItem dashboardItem1 = new DashboardItem();
		// set pid
		dashboardItem1.setPid(DashboardItemService.PID_PREFIX + RandomUtil.generatePid());
		dashboardItem1.setName("Receipt");
		dashboardItem1.setDashboardItemType(DashboardItemType.DOCUMENT);
		dashboardItem1.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
		dashboardItem1.setTaskPlanType(TaskPlanType.BOTH);
		dashboardItem1.setDashboardItemConfigType(DashboardItemConfigType.DASHBOARD);
		for (Document document : documents) {
			if (document.getName().equals("Receipt")) {
				dashboardItem1.getDocuments().add(documentRepository.findOneByPid(document.getPid()).get());
			}
		}
		// set company
		dashboardItem1.setCompany(company);
		dashboardItems.add(dashboardItem1);

		DashboardItem dashboardItem2 = new DashboardItem();
		// set pid
		dashboardItem2.setPid(DashboardItemService.PID_PREFIX + RandomUtil.generatePid());
		dashboardItem2.setName("No Order");
		dashboardItem2.setDashboardItemType(DashboardItemType.DOCUMENT);
		dashboardItem2.setDocumentType(DocumentType.DYNAMIC_DOCUMENT);
		dashboardItem2.setTaskPlanType(TaskPlanType.BOTH);
		dashboardItem2.setDashboardItemConfigType(DashboardItemConfigType.DASHBOARD);
		for (Document document : documents) {
			if (document.getName().equals("No Order")) {
				dashboardItem2.getDocuments().add(documentRepository.findOneByPid(document.getPid()).get());
			}
		}
		// set company
		dashboardItem2.setCompany(company);
		dashboardItems.add(dashboardItem2);

		DashboardItem dashboardItem3 = new DashboardItem();
		// set pid
		dashboardItem3.setPid(DashboardItemService.PID_PREFIX + RandomUtil.generatePid());
		dashboardItem3.setName("Visit");
		dashboardItem3.setDashboardItemType(DashboardItemType.ACTIVITY);
		dashboardItem3.setTaskPlanType(TaskPlanType.BOTH);
		dashboardItem3.setDashboardItemConfigType(DashboardItemConfigType.DASHBOARD);
		Activity activity = activityRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Dealer Visit").get();
		dashboardItem3.getActivities().add(activity);
		// set company
		dashboardItem3.setCompany(company);
		dashboardItems.add(dashboardItem3);

		dashboardItemRepository.save(dashboardItems);
	}

	private AccountProfile createAccountProfile(Company company) {
		AccountProfile accountProfile = new AccountProfile();
		// set company
		accountProfile.setCompany(company);
		accountProfile.setAccountStatus(AccountStatus.Verified);
		accountProfile.setAccountType(
				accountTypeRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Company").get());
		accountProfile.setActivated(true);
		accountProfile.setCity("No City");
		accountProfile.setAddress("No Address");
		accountProfile.setName(company.getLegalName());
		accountProfile.setAlias(company.getLegalName());
		accountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
		accountProfile.setUser(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
		return accountProfileRepository.save(accountProfile);

	}

	private void assignAccountToTerritory(Company company, AccountProfile accountProfile) {
		LocationAccountProfile locationAccountProfile = new LocationAccountProfile(
				locationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Territory").get(), accountProfile,
				company);
		locationAccountProfileRepository.save(locationAccountProfile);
	}

	// error
	private void receiptDocument(Company company) {
		ReceiptDocument receiptDocument = new ReceiptDocument(
				documentRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Receipt").get(), company, true);
		receiptDocumentRepository.save(receiptDocument);
	}

	private void primarySecondaryDocument(Company company) {
		PrimarySecondaryDocument primarySecondaryDocument = new PrimarySecondaryDocument(
				documentRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Sales Order").get(), company,
				VoucherType.PRIMARY_SALES_ORDER, true);
		primarySecondaryDocumentRepository.save(primarySecondaryDocument);
	}

	public void assignProductGroups(String companyPid, String userPidss, String groupPids) {
		String[] userPids = userPidss.split(",");
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPidAndGroupPidIn(companyPid,
				Arrays.asList(groupPids.split(",")));
		List<UserProductGroup> userProductGroups = new ArrayList<>();
		for (String userPid : userPids) {
			userProductGroupRepository.deleteByUserPid(userPid);
			for (ProductGroup productGroup : productGroups) {
				UserProductGroup userProductGroup = new UserProductGroup(userRepository.findOneByPid(userPid).get(),
						productGroup, productGroup.getCompany());
				userProductGroups.add(userProductGroup);
			}
		}
		userProductGroupRepository.save(userProductGroups);
	}

	// for orderpro users
	public void assignDefaultProductGroups(String companyPid, int userCount) {
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPid(companyPid);
		List<UserProductGroup> userProductGroups = new ArrayList<>();
		List<User> users = userRepository.findAllByCompanyPidSortedById(companyPid);
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			for (ProductGroup productGroup : productGroups) {
				UserProductGroup userProductGroup = new UserProductGroup(
						userRepository.findOneByPid(user.getPid()).get(), productGroup, productGroup.getCompany());
				userProductGroups.add(userProductGroup);
			}
		}
		userProductGroupRepository.save(userProductGroups);
	}

	public void assignProductCategories(String companyPid, String userPidss, String categoryPids) {
		String[] userPids = userPidss.split(",");
		List<ProductCategory> productCategories = productCategoryRepository
				.findAllByCompanyPidOrderByProductId(companyPid, Arrays.asList(categoryPids.split(",")));
		List<UserProductCategory> userProductCategories = new ArrayList<>();
		for (String userPid : userPids) {
			userProductCategoryRepository.deleteByUserPid(userPid);
			for (ProductCategory productCategory : productCategories) {
				UserProductCategory userProductCategory = new UserProductCategory(
						userRepository.findOneByPid(userPid).get(), productCategory, productCategory.getCompany());
				userProductCategories.add(userProductCategory);
			}
		}
		userProductCategoryRepository.save(userProductCategories);
	}

	// for orderpro users
	public void assignDefaultProductCategories(String companyPid, int userCount) {
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyPid(companyPid);
		List<UserProductCategory> userProductCategories = new ArrayList<>();
		List<User> users = userRepository.findAllByCompanyPidSortedById(companyPid);
		for (int i = 0; i < userCount; i++) {
			User user = users.get(i);
			for (ProductCategory productCategory : productCategories) {
				UserProductCategory userProductCategory = new UserProductCategory(
						userRepository.findOneByPid(user.getPid()).get(), productCategory,
						productCategory.getCompany());
				userProductCategories.add(userProductCategory);
			}
		}
		userProductCategoryRepository.save(userProductCategories);
	}

	public void createDefaultProductCategory(Company company) {
		ProductCategory productCategory = new ProductCategory();
		productCategory.setActivated(true);
		productCategory.setAlias("GENERAL");
		productCategory.setCompany(company);
		productCategory.setDataSourceType(DataSourceType.WEB);
		productCategory.setDescription("GENERAL");
		productCategory.setName("GENERAL");
		productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
		productCategory.setThirdpartyUpdate(false);
		productCategoryRepository.save(productCategory);
	}

	public void createDefaultProductGroup(Company company) {
		ProductGroup productGroup = new ProductGroup();
		productGroup.setActivated(true);
		productGroup.setAlias("GENERAL");
		productGroup.setCompany(company);
		productGroup.setDataSourceType(DataSourceType.WEB);
		productGroup.setDescription("GENERAL");
		productGroup.setName("GENERAL");
		productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
//		productGroup.setTaxMastersList(taxMastersList);
		productGroupRepository.save(productGroup);
	}

	private void createDivision(Company company) {
		Division division = new Division();
		division.setActivated(true);
		division.setCompany(company);
		division.setName("Sales");
		division.setPid(DivisionService.PID_PREFIX + RandomUtil.generatePid());
		divisionRepository.save(division);
	}

	private void createProductGroup(Company company) {
		ProductGroup productGroup = new ProductGroup();
		productGroup.setActivated(true);
		productGroup.setCompany(company);
		productGroup.setName("General");
		productGroup.setAlias("General");
		productGroup.setThirdpartyUpdate(false);
		productGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		productGroupRepository.save(productGroup);
	}

	private void createProductCategory(Company company) {
		ProductCategory productCategory = new ProductCategory();
		productCategory.setActivated(true);
		productCategory.setCompany(company);
		productCategory.setName("Not Applicable");
		productCategory.setAlias("Not Applicable");
		productCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
		productCategory.setThirdpartyUpdate(false);
		productCategoryRepository.save(productCategory);
	}

	// for orderpro users
	public void assignDocumentProductCategory(Company company) {
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
		List<ProductCategory> productCatagories = productCategoryRepository.findAllByCompanyPid(company.getPid());
		List<DocumentProductCategory> documentProductCategories = new ArrayList<>();
		for (Document document : documents) {
			if (document.getDocumentType() == DocumentType.INVENTORY_VOUCHER) {
				for (ProductCategory productCategory : productCatagories) {
					documentProductCategories.add(new DocumentProductCategory(document, productCategory, company));
				}
			}
		}
		documentProductCategoryRepository.save(documentProductCategories);
	}

	// for orderpro users
	public void assignDocumentProductGroup(Company company) {
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPid(company.getPid());
		List<DocumentProductGroup> documentProductGroups = new ArrayList<>();
		int sortOrder = 1;
		for (Document document : documents) {
			if (document.getDocumentType() == DocumentType.INVENTORY_VOUCHER) {
				for (ProductGroup productGroup : productGroups) {
					documentProductGroups.add(new DocumentProductGroup(document, productGroup, company, sortOrder));
					sortOrder++;
				}
			}
		}
		documentProductGroupRepository.save(documentProductGroups);
	}

	public void assignEmployeeToLocationsForTally(Company company) {
		List<EmployeeProfileLocation> employeeProfileLocations = new ArrayList<>();
		// List<String> userPids =
		// userRepository.findUserPidByCompanyId(company.getId());
		List<EmployeeProfile> employees = employeeProfileRepository.findAllByCompanyId(company.getId());
		List<String> existingEmpLocPids = employeeProfileLocationRepository
				.findEmployeeProfilePidByEmployeeProfileIn(employees);
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());

		for (EmployeeProfile employee : employees) {
			Optional<String> opEmpLoc = existingEmpLocPids.stream().filter(emp -> emp.equals(employee.getPid()))
					.findAny();
			if (!opEmpLoc.isPresent()) {
				for (Location location : locations) {
					EmployeeProfileLocation employeeProfileLocation = new EmployeeProfileLocation();
					employeeProfileLocation.setEmployeeProfile(employee);
					employeeProfileLocation.setLocation(location);
					employeeProfileLocations.add(employeeProfileLocation);
				}
			}
		}
		employeeProfileLocationRepository.save(employeeProfileLocations);
	}

	// for orderpro Tally
	private void assignNewUserToStockLocation(Company company, User user) {

		List<UserStockLocation> userStockLocations = new ArrayList<>();
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
		if (stockLocations.size() > 1) {
			for (StockLocation stockLocation : stockLocations) {
				if (!stockLocation.getName().equals("Main Location")) {
					userStockLocations.add(new UserStockLocation(user, stockLocation, company));
				}
			}
		} else {
			stockLocationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "Main Location")
					.ifPresent(mainLocation -> {
						userStockLocations.add(new UserStockLocation(user, mainLocation, company));
					});
		}
		userStockLocationRepository.save(userStockLocations);
	}

	// for orderpro tally
	private void assignNewUserToPriceLevel(Company company, User user) {

		List<UserPriceLevel> userPriceLevels = new ArrayList<>();
		List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());
		if (priceLevels.size() > 1) {
			for (PriceLevel priceLevel : priceLevels) {
				if (!priceLevel.getName().equals("General")) {
					userPriceLevels.add(new UserPriceLevel(user, priceLevel, company));
				}
			}
		} else {
			priceLevelRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), "General").ifPresent(general -> {
				userPriceLevels.add(new UserPriceLevel(user, general, company));
			});
		}
		userPriceLevelRepository.save(userPriceLevels);
	}

}
