package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.DocumentProductCategory;
import com.orderfleet.webapp.domain.DocumentProductGroup;
import com.orderfleet.webapp.domain.DocumentStockLocationDestination;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.FormFormElement;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReferenceDocument;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AccountGroupRepository;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.DocumentInventoryVoucherColumnRepository;
import com.orderfleet.webapp.repository.DocumentProductCategoryRepository;
import com.orderfleet.webapp.repository.DocumentProductGroupRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationDestinationRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReferenceDocumentRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountGroupService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DepartmentService;
import com.orderfleet.webapp.service.DesignationService;
import com.orderfleet.webapp.service.DivisionService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.SetupCompanyService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.util.RandomUtil;

/**
 * Service Implementation for set up company by site admin.
 * 
 * @author Shaheer
 * @since November 14, 2016
 */
@Service
@Transactional
public class SetupCompanyServiceImpl implements SetupCompanyService {

	private final Logger log = LoggerFactory.getLogger(SetupCompanyServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountGroupRepository accountGroupRepository;

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private DocumentFormsRepository documentFormsRepository;

	@Inject
	private ReferenceDocumentRepository referenceDocumentRepository;

	@Inject
	private DocumentInventoryVoucherColumnRepository documentInventoryVoucherColumnRepository;

	@Inject
	private DocumentProductCategoryRepository documentProductCategoryRepository;

	@Inject
	private DocumentProductGroupRepository documentProductGroupRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Inject
	private DocumentStockLocationDestinationRepository documentStockLocationDestinationRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private FormFormElementRepository formFormElementRepository;

	@Override
	public Company cloneCompany(String existingCompanyPid, String legalName, String email) {
		log.info("cloning company information.");
		return companyRepository.findOneByPid(existingCompanyPid).map(company -> {
			Company newCompany = new Company();
			newCompany.setPid(CompanyService.PID_PREFIX + RandomUtil.generatePid());
			newCompany.setLegalName(legalName);
			newCompany.setAlias(company.getAlias());
			newCompany.setCompanyType(company.getCompanyType());
			newCompany.setIndustry(company.getIndustry());
			newCompany.setAddress1(company.getAddress1());
			newCompany.setAddress2(company.getAddress2());
			newCompany.setCountry(company.getCountry());
			newCompany.setState(company.getState());
			newCompany.setDistrict(company.getDistrict());
			newCompany.setEmail(email);
			newCompany.setLocation(company.getLocation());
			newCompany.setLogo(company.getLogo());
			newCompany.setLogoContentType(company.getLogoContentType());
			newCompany.setWebsite(company.getWebsite());
			newCompany.setIsActivated(true);
			newCompany.setVersion(company.getVersion());
			return companyRepository.save(newCompany);
		}).orElse(null);
	}

	@Override
	public User cloneUser(String existingUserPid, String login, String email, Company company) {
		log.info("cloning user information.");
		return userRepository.findOneByPid(existingUserPid).map(user -> {
			User newUser = new User();
			newUser.setPid(UserService.PID_PREFIX + RandomUtil.generatePid());
			newUser.setCompany(company);
			newUser.setLogin(login);
			newUser.setPassword(RandomUtil.generatePassword());
			newUser.setFirstName(user.getFirstName());
			newUser.setLastName(user.getLastName());
			newUser.setEmail(email);
			newUser.setMobile(user.getMobile());
			newUser.setActivated(true);
			newUser.setLangKey(user.getLangKey());
			newUser.setDeviceKey(user.getDeviceKey());
			newUser.setDeviceType(user.getDeviceType());
			newUser.setActivationKey(RandomUtil.generateActivationKey());
			newUser.setResetKey(user.getResetKey());
			newUser.setResetDate(user.getResetDate());
			newUser.setAuthorities(user.getAuthorities());
			newUser.setVersion(user.getVersion());
			return userRepository.save(newUser);
		}).orElse(null);
	}

	@Override
	public void cloneDocuments(String existingCompanyPid, Company company) {
		// copy documents
		List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(existingCompanyPid);
		List<Document> newDocuments = new ArrayList<>();
		for (Document document : documents) {
			try {
				Document newDocument = (Document) document.clone();
				newDocument.setId(null);
				newDocument.setPid(DocumentService.PID_PREFIX + RandomUtil.generatePid());
				newDocument.setCompany(company);
				newDocuments.add(newDocument);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<Document> savedDocuments = documentRepository.save(newDocuments);

		// copy document_ref documents
		List<ReferenceDocument> referenceDocuments = referenceDocumentRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<ReferenceDocument> newReferenceDocuments = new ArrayList<>();
		referenceDocuments.parallelStream().forEach(refd -> {
			Optional<Document> existreRerenceDocument = savedDocuments.parallelStream()
					.filter(rf -> rf.getName().equals(refd.getReferenceDocument().getName())).findAny();
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(refd.getDocument().getName())).findAny();
			if (existreRerenceDocument.isPresent() && existDocument.isPresent()) {
				ReferenceDocument referenceDocument = new ReferenceDocument();
				referenceDocument.setReferenceDocument(existreRerenceDocument.get());
				referenceDocument.setDocument(existDocument.get());
				referenceDocument.setCompany(company);
				newReferenceDocuments.add(referenceDocument);
			}
		});
		referenceDocumentRepository.save(newReferenceDocuments);

		// copy Document Inventory Voucher Column
		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns = documentInventoryVoucherColumnRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<DocumentInventoryVoucherColumn> newDocumentInventoryVoucherColumns = new ArrayList<>();
		documentInventoryVoucherColumns.parallelStream().forEach(invc -> {
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(invc.getDocument().getName())).findAny();
			if (existDocument.isPresent()) {
				DocumentInventoryVoucherColumn documentInventoryVoucherColumn = new DocumentInventoryVoucherColumn();
				documentInventoryVoucherColumn.setDocument(existDocument.get());
				documentInventoryVoucherColumn.setCompany(company);
				documentInventoryVoucherColumn.setInventoryVoucherColumn(invc.getInventoryVoucherColumn());
				newDocumentInventoryVoucherColumns.add(documentInventoryVoucherColumn);
			}
		});
		documentInventoryVoucherColumnRepository.save(newDocumentInventoryVoucherColumns);

		// copy document_Product Categories
		List<DocumentProductCategory> documentProductCategories = documentProductCategoryRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyPid(company.getPid());
		List<DocumentProductCategory> newDocumentProductCategories = new ArrayList<>();
		documentProductCategories.parallelStream().forEach(dpc -> {
			Optional<ProductCategory> existProductCategory = productCategories.parallelStream()
					.filter(pg -> pg.getName().equals(dpc.getProductCategory().getName())).findAny();
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(dpc.getDocument().getName())).findAny();
			if (existProductCategory.isPresent() && existDocument.isPresent()) {
				DocumentProductCategory documentProductCategory = new DocumentProductCategory();
				documentProductCategory.setProductCategory(existProductCategory.get());
				documentProductCategory.setDocument(existDocument.get());
				documentProductCategory.setCompany(company);
				newDocumentProductCategories.add(documentProductCategory);
			}
		});
		documentProductCategoryRepository.save(newDocumentProductCategories);

		// copy document_Product Groups
		List<DocumentProductGroup> documentDocumentProductGroups = documentProductGroupRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPid(company.getPid());
		List<DocumentProductGroup> newDocumentProductGroups = new ArrayList<>();
		documentDocumentProductGroups.parallelStream().forEach(dpg -> {
			Optional<ProductGroup> existProductGroup = productGroups.parallelStream()
					.filter(pg -> pg.getName().equals(dpg.getProductGroup().getName())).findAny();
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(dpg.getDocument().getName())).findAny();
			if (existProductGroup.isPresent() && existDocument.isPresent()) {
				DocumentProductGroup documentProductGroup = new DocumentProductGroup();
				documentProductGroup.setProductGroup(existProductGroup.get());
				documentProductGroup.setDocument(existDocument.get());
				documentProductGroup.setCompany(company);
				newDocumentProductGroups.add(documentProductGroup);
			}
		});
		documentProductGroupRepository.save(newDocumentProductGroups);

		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyPid(company.getPid());
		// copy document_Stock Location Source
		List<DocumentStockLocationSource> documentStockLocationSources = documentStockLocationSourceRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<DocumentStockLocationSource> newDocumentStockLocationSources = new ArrayList<>();
		documentStockLocationSources.parallelStream().forEach(dsls -> {
			Optional<StockLocation> existStockLocation = stockLocations.parallelStream()
					.filter(sl -> sl.getName().equals(dsls.getStockLocation().getName())).findAny();
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(dsls.getDocument().getName())).findAny();
			if (existStockLocation.isPresent() && existDocument.isPresent()) {
				DocumentStockLocationSource documentStockLocationSource = new DocumentStockLocationSource();
				documentStockLocationSource.setStockLocation(existStockLocation.get());
				documentStockLocationSource.setDocument(existDocument.get());
				documentStockLocationSource.setIsDefault(dsls.getIsDefault());
				documentStockLocationSource.setCompany(company);
				newDocumentStockLocationSources.add(documentStockLocationSource);
			}
		});
		documentStockLocationSourceRepository.save(newDocumentStockLocationSources);

		// copy document_Stock Location Destination
		List<DocumentStockLocationDestination> documentStockLocationDestinations = documentStockLocationDestinationRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<DocumentStockLocationDestination> newDocumentStockLocationDestinations = new ArrayList<>();
		documentStockLocationDestinations.parallelStream().forEach(dsld -> {
			Optional<StockLocation> existStockLocation = stockLocations.parallelStream()
					.filter(sl -> sl.getName().equals(dsld.getStockLocation().getName())).findAny();
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(dsld.getDocument().getName())).findAny();
			if (existStockLocation.isPresent() && existDocument.isPresent()) {
				DocumentStockLocationDestination documentStockLocationDestination = new DocumentStockLocationDestination();
				documentStockLocationDestination.setStockLocation(existStockLocation.get());
				documentStockLocationDestination.setDocument(existDocument.get());
				documentStockLocationDestination.setIsDefault(dsld.getIsDefault());
				documentStockLocationDestination.setCompany(company);
				newDocumentStockLocationDestinations.add(documentStockLocationDestination);
			}
		});
		documentStockLocationDestinationRepository.save(newDocumentStockLocationDestinations);

		// copy forms
		List<Form> forms = formRepository.findAllByCompanyPid(existingCompanyPid);
		List<Form> newForms = new ArrayList<>();
		for (Form form : forms) {
			try {
				Form newForm = (Form) form.clone();
				newForm.setId(null);
				newForm.setPid(FormService.PID_PREFIX + RandomUtil.generatePid());
				newForm.setCompany(company);
				newForms.add(newForm);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<Form> savedForms = formRepository.save(newForms);

		// copy document_forms
		List<DocumentForms> documentForms = documentFormsRepository.findAllByDocumentCompanyPid(existingCompanyPid);
		List<DocumentForms> newDocumentForms = new ArrayList<>();
		documentForms.parallelStream().forEach(df -> {
			Optional<Form> existForm = savedForms.parallelStream()
					.filter(f -> f.getName().equals(df.getForm().getName())).findAny();
			Optional<Document> existDocument = savedDocuments.parallelStream()
					.filter(d -> d.getName().equals(df.getDocument().getName())).findAny();
			if (existForm.isPresent() && existDocument.isPresent()) {
				DocumentForms documentForm = new DocumentForms();
				documentForm.setForm(existForm.get());
				documentForm.setDocument(existDocument.get());
				newDocumentForms.add(documentForm);
			}
		});
		documentFormsRepository.save(newDocumentForms);

		// copy Form Elements
		List<FormElement> formElements = formElementRepository.findAllByCompanyPid(existingCompanyPid);
		List<FormElement> newFormElements = new ArrayList<>();
		for (FormElement formElement : formElements) {
			try {
				FormElement newFormElement = (FormElement) formElement.clone();
				newFormElement.setId(null);
				newFormElement.setPid(FormService.PID_PREFIX + RandomUtil.generatePid());
				newFormElement.setCompany(company);
				newFormElements.add(newFormElement);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<FormElement> savedFormElements = formElementRepository.save(newFormElements);

		// copy form_form elements
		List<FormFormElement> formFormElements = formFormElementRepository.findAllByCompanyPid(existingCompanyPid);
		List<FormFormElement> newFormFormElements = new ArrayList<>();
		formFormElements.parallelStream().forEach(ffe -> {
			Optional<FormElement> existFormElement = savedFormElements.parallelStream()
					.filter(fe -> fe.getName().equals(ffe.getFormElement().getName())).findAny();
			Optional<Form> existForm = savedForms.parallelStream().filter(f -> f.getName().equals(f.getName()))
					.findAny();
			if (existFormElement.isPresent() && existForm.isPresent()) {
				FormFormElement formFormElement = new FormFormElement();
				formFormElement.setFormElement(existFormElement.get());
				formFormElement.setForm(existForm.get());
				formFormElement.setEditable(ffe.getEditable());
				formFormElement.setReportOrder(ffe.getReportOrder());
				formFormElement.setSortOrder(ffe.getSortOrder());
				formFormElement.setValidationEnabled(ffe.getValidationEnabled());
				formFormElement.setCompany(company);
				newFormFormElements.add(formFormElement);
			}
		});
		formFormElementRepository.save(newFormFormElements);
	}

	@Override
	public void cloneEmployeeProfilesAndLocations(String existingCompanyPid, Company company) {
		log.info("cloning EmployeeProfiles and locations.");
		// copy department
		List<Department> departments = departmentRepository.findAllByCompanyPid(existingCompanyPid);
		List<Department> newDepartments = new ArrayList<>();
		for (Department department : departments) {
			try {
				Department newDepartment = (Department) department.clone();
				newDepartment.setId(null);
				newDepartment.setPid(DepartmentService.PID_PREFIX + RandomUtil.generatePid());
				newDepartment.setCompany(company);
				newDepartments.add(newDepartment);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		departmentRepository.save(newDepartments);

		// copy designation
		List<Designation> designations = designationRepository.findAllByCompanyPid(existingCompanyPid);
		List<Designation> newDesignations = new ArrayList<>();
		for (Designation designation : designations) {
			try {
				Designation newDesignation = (Designation) designation.clone();
				newDesignation.setId(null);
				newDesignation.setPid(DesignationService.PID_PREFIX + RandomUtil.generatePid());
				newDesignation.setCompany(company);
				newDesignations.add(newDesignation);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		designationRepository.save(newDesignations);

		// copy employee profile
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyPid(existingCompanyPid);
		List<EmployeeProfile> newEmployeeProfiles = new ArrayList<>();
		for (EmployeeProfile employeeProfile : employeeProfiles) {
			try {
				EmployeeProfile newEmployeeProfile = (EmployeeProfile) employeeProfile.clone();
				newEmployeeProfile.setId(null);
				// Employee user association
				if (employeeProfile.getUser() != null) {
					User user = userRepository.findByCompanyIdAndFirstNameAndLastName(company.getId(),
							employeeProfile.getUser().getFirstName(), employeeProfile.getUser().getLastName());
					newEmployeeProfile.setUser(user);
				}
				newEmployeeProfile.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
				newEmployeeProfile.setCompany(company);
				newEmployeeProfile.setDepartment(departmentRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), employeeProfile.getDepartment().getName())
						.get());
				newEmployeeProfile.setDesignation(designationRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), employeeProfile.getDesignation().getName())
						.get());
				newEmployeeProfiles.add(newEmployeeProfile);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<EmployeeProfile> savedEmployeeProfiles = employeeProfileRepository.save(newEmployeeProfiles);

		// copy Location
		List<Location> locations = locationRepository.findAllByCompanyPid(existingCompanyPid);
		List<Location> newLocations = new ArrayList<>();
		for (Location location : locations) {
			try {
				Location newLocation = (Location) location.clone();
				newLocation.setId(null);
				newLocation.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				newLocation.setCompany(company);
				newLocations.add(newLocation);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<Location> savedLocations = locationRepository.save(newLocations);

		// copy Location Hierarchy
		cloneLocationHierarchy(existingCompanyPid, company);

		// copy employeeProfile_locations
		List<EmployeeProfileLocation> employeeProfileLocations = employeeProfileLocationRepository
				.findByLocationCompanyPid(existingCompanyPid);
		List<EmployeeProfileLocation> newEmployeeProfileLocations = new ArrayList<>();
		employeeProfileLocations.parallelStream().forEach(empLoc -> {

			Optional<EmployeeProfile> existEmployeeProfile = savedEmployeeProfiles.parallelStream()
					.filter(e -> e.getName().equals(empLoc.getEmployeeProfile().getName())).findAny();

			Optional<Location> existLocation = savedLocations.parallelStream()
					.filter(l -> l.getName().equals(empLoc.getLocation().getName())).findAny();

			if (existEmployeeProfile.isPresent() && existLocation.isPresent()) {
				EmployeeProfileLocation employeeProfileLocation = new EmployeeProfileLocation();
				employeeProfileLocation.setEmployeeProfile(existEmployeeProfile.get());
				employeeProfileLocation.setLocation(existLocation.get());
				newEmployeeProfileLocations.add(employeeProfileLocation);
			}
		});
		employeeProfileLocationRepository.save(newEmployeeProfileLocations);
	}

	private void cloneLocationHierarchy(String existingCompanyPid, Company company) {
		// check it has already location hierarchy
		Optional<LocationHierarchy> hasHierarchy = locationHierarchyRepository
				.findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(company.getId());
		if (hasHierarchy.isPresent()) {
			return;
		}
		List<LocationHierarchy> locationHierarchies = locationHierarchyRepository
				.findByCompanyPidAndActivatedTrue(existingCompanyPid);
		List<LocationHierarchy> newLocationHierarchies = locationHierarchies.stream().map(lh -> {
			Optional<Location> loc = locationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
					lh.getLocation().getName());
			if (lh.getParent() != null) {
				Optional<Location> parent = locationRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
						lh.getParent().getName());
				if (loc.isPresent() && parent.isPresent()) {
					LocationHierarchy locationHierarchy = new LocationHierarchy();
					locationHierarchy.setCompany(company);
					locationHierarchy.setLocation(loc.get());
					locationHierarchy.setParent(parent.get());
					locationHierarchy.setVersion(1L);
					return locationHierarchy;
				}
			} else {
				LocationHierarchy locationHierarchy = new LocationHierarchy();
				locationHierarchy.setCompany(company);
				locationHierarchy.setLocation(loc.get());
				locationHierarchy.setParent(null);
				locationHierarchy.setVersion(1L);
				return locationHierarchy;
			}
			return null;
		}).collect(Collectors.toList());

		locationHierarchyRepository.save(newLocationHierarchies);
	}

	@Override
	public void cloneAccountProfiles(String existingCompanyPid, Company company) {
		log.info("cloning AccountProfiles.");
		// copy account type
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AT_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all by compPid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountType> accountTypes = accountTypeRepository.findAllByCompanyPid(existingCompanyPid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		List<AccountType> newAccountTypes = new ArrayList<>();
		for (AccountType accountType : accountTypes) {
			try {
				AccountType newAccountType = (AccountType) accountType.clone();
				newAccountType.setId(null);
				newAccountType.setPid(AccountTypeService.PID_PREFIX + RandomUtil.generatePid());
				newAccountType.setCompany(company);
				newAccountTypes.add(newAccountType);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		accountTypeRepository.save(newAccountTypes);

		// copy account profile
		  DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by companyPid";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> accountProfiles = accountProfileRepository.findAllByCompanyPid(existingCompanyPid);
		  String flag1 = "Normal";
			LocalDateTime endLCTime1 = LocalDateTime.now();
			String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
			String endDate1 = startLCTime1.format(DATE_FORMAT1);
			Duration duration1 = Duration.between(startLCTime1, endLCTime1);
			long minutes1 = duration1.toMinutes();
			if (minutes1 <= 1 && minutes1 >= 0) {
				flag1 = "Fast";
			}
			if (minutes1 > 1 && minutes1 <= 2) {
				flag1 = "Normal";
			}
			if (minutes1 > 2 && minutes1 <= 10) {
				flag1 = "Slow";
			}
			if (minutes1 > 10) {
				flag1 = "Dead Slow";
			}
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);
		List<AccountProfile> newAccountProfiles = new ArrayList<>();
		for (AccountProfile accountProfile : accountProfiles) {
			try {
				AccountProfile newAccountProfile = (AccountProfile) accountProfile.clone();
				newAccountProfile.setId(null);
				newAccountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				newAccountProfile.setCompany(company);
				 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id11 = "AT_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description11 ="get by compId and name ignore case";
					LocalDateTime startLCTime11 = LocalDateTime.now();
					String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
					String startDate11 = startLCTime11.format(DATE_FORMAT11);
					logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
				newAccountProfile.setAccountType(accountTypeRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), accountProfile.getAccountType().getName())
						.get());
				 String flag11 = "Normal";
					LocalDateTime endLCTime11 = LocalDateTime.now();
					String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
					String endDate11 = startLCTime11.format(DATE_FORMAT11);
					Duration duration11 = Duration.between(startLCTime11, endLCTime11);
					long minutes11 = duration11.toMinutes();
					if (minutes11 <= 1 && minutes11 >= 0) {
						flag11 = "Fast";
					}
					if (minutes11 > 1 && minutes11 <= 2) {
						flag11 = "Normal";
					}
					if (minutes11 > 2 && minutes11 <= 10) {
						flag11 = "Slow";
					}
					if (minutes11 > 10) {
						flag11 = "Dead Slow";
					}
			                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
							+ description11);

				newAccountProfiles.add(newAccountProfile);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		accountProfileRepository.save(newAccountProfiles);

		// copy account group
		 DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id11 = "AG_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 ="get all by companyPid";
			LocalDateTime startLCTime11 = LocalDateTime.now();
			String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
			String startDate11 = startLCTime11.format(DATE_FORMAT11);
			logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
		List<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyPid(existingCompanyPid);
		 String flag11 = "Normal";
			LocalDateTime endLCTime11 = LocalDateTime.now();
			String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
			String endDate11 = startLCTime11.format(DATE_FORMAT11);
			Duration duration11 = Duration.between(startLCTime11, endLCTime11);
			long minutes11 = duration11.toMinutes();
			if (minutes11 <= 1 && minutes11 >= 0) {
				flag11 = "Fast";
			}
			if (minutes11 > 1 && minutes11 <= 2) {
				flag11 = "Normal";
			}
			if (minutes11 > 2 && minutes11 <= 10) {
				flag11 = "Slow";
			}
			if (minutes11 > 10) {
				flag11 = "Dead Slow";
			}
	                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
					+ description11);

		List<AccountGroup> newAccountGroups = new ArrayList<>();
		for (AccountGroup accountGroup : accountGroups) {
			try {
				AccountGroup newAccountGroup = (AccountGroup) accountGroup.clone();
				newAccountGroup.setId(null);
				newAccountGroup.setPid(AccountGroupService.PID_PREFIX + RandomUtil.generatePid());
				newAccountGroup.setCompany(company);
				newAccountGroups.add(newAccountGroup);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		accountGroupRepository.save(newAccountGroups);

	}

	@Override
	public void cloneProductProfiles(String existingCompanyPid, Company company) {
		log.info("cloning ProductProfiles.");
		// copy product divisions
		List<Division> divisions = divisionRepository.findAllByCompanyPid(existingCompanyPid);
		List<Division> newDivisions = new ArrayList<>();
		for (Division division : divisions) {
			try {
				Division newDivision = (Division) division.clone();
				newDivision.setId(null);
				newDivision.setPid(DivisionService.PID_PREFIX + RandomUtil.generatePid());
				newDivision.setCompany(company);
				newDivisions.add(newDivision);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		divisionRepository.save(newDivisions);

		// copy product categories
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyPid(existingCompanyPid);
		List<ProductCategory> newProductCategories = new ArrayList<>();
		for (ProductCategory productCategory : productCategories) {
			try {
				ProductCategory newProductCategory = (ProductCategory) productCategory.clone();
				newProductCategory.setId(null);
				newProductCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
				newProductCategory.setCompany(company);
				newProductCategories.add(newProductCategory);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		productCategoryRepository.save(newProductCategories);

		// copy product profiles
		List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyPid(existingCompanyPid);
		List<ProductProfile> newProductProfiles = new ArrayList<>();
		for (ProductProfile productProfile : productProfiles) {
			try {
				ProductProfile newProductProfile = (ProductProfile) productProfile.clone();
				newProductProfile.setId(null);
				newProductProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				newProductProfile.setCompany(company);
				newProductProfile.setFiles(null);
				newProductProfile
						.setProductCategory(productCategoryRepository.findByCompanyIdAndNameIgnoreCase(company.getId(),
								productProfile.getProductCategory().getName()).get());
				newProductProfile.setDivision(divisionRepository
						.findByCompanyIdAndNameIgnoreCase(company.getId(), productProfile.getDivision().getName())
						.get());
				newProductProfiles.add(newProductProfile);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<ProductProfile> savedProductProfiles = productProfileRepository.save(newProductProfiles);

		// copy product group
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPid(existingCompanyPid);
		List<ProductGroup> newProductGroups = new ArrayList<>();
		for (ProductGroup productGroup : productGroups) {
			try {
				ProductGroup newProductGroup = (ProductGroup) productGroup.clone();
				newProductGroup.setId(null);
				newProductGroup.setPid(ProductGroupService.PID_PREFIX + RandomUtil.generatePid());
				newProductGroup.setCompany(company);
				newProductGroups.add(newProductGroup);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		List<ProductGroup> savedProductGroups = productGroupRepository.save(newProductGroups);

		// copy productgroup_products
		List<ProductGroupProduct> productGroupProducts = productGroupProductRepository
				.findAllByCompanyPid(existingCompanyPid);
		List<ProductGroupProduct> newProductGroupProducts = new ArrayList<>();
		productGroupProducts.parallelStream().forEach(pgp -> {
			Optional<ProductProfile> existProduct = savedProductProfiles.parallelStream()
					.filter(p -> p.getName().equals(pgp.getProduct().getName())).findAny();
			Optional<ProductGroup> existProductGroup = savedProductGroups.parallelStream()
					.filter(pg -> pg.getName().equals(pgp.getProductGroup().getName())).findAny();
			if (existProduct.isPresent() && existProductGroup.isPresent()) {
				ProductGroupProduct productGroupProduct = new ProductGroupProduct();
				productGroupProduct.setProduct(existProduct.get());
				productGroupProduct.setProductGroup(existProductGroup.get());
				newProductGroupProducts.add(productGroupProduct);
			}
		});
		productGroupProductRepository.save(newProductGroupProducts);

		// copy product stock location
		List<StockLocation> stockLocations = stockLocationRepository.findAllByCompanyPid(existingCompanyPid);
		List<StockLocation> newStockLocations = new ArrayList<>();
		for (StockLocation stockLocation : stockLocations) {
			try {
				StockLocation newStockLocation = (StockLocation) stockLocation.clone();
				newStockLocation.setId(null);
				newStockLocation.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid());
				newStockLocation.setCompany(company);
				newStockLocations.add(newStockLocation);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		}
		stockLocationRepository.save(newStockLocations);
	}

}
