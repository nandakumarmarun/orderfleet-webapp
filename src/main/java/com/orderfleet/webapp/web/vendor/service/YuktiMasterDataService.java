package com.orderfleet.webapp.web.vendor.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.ProductCategoryService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.snr.yukti.model.master.Branch;
import com.snr.yukti.model.master.Category;
import com.snr.yukti.model.master.Product;
import com.snr.yukti.model.master.Receivables;
import com.snr.yukti.model.master.SalesArea;
import com.snr.yukti.model.master.UserAccount;
import com.snr.yukti.service.YuktiMasterService;

@Service
public class YuktiMasterDataService {

	private static final Logger log = LoggerFactory.getLogger(YuktiMasterDataService.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private DesignationRepository designationRepository;
	
	@Inject
	private DepartmentRepository departmentRepository;
	
	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private ReceivablePayableService receivablePayableService;

	@Inject
	private ReceivablePayableRepository receivablePayableRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private YuktiMasterService yuktiMasterService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private ProductGroupRepository productGroupRepository;
	
	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	@Transactional
	public void downloadSaveUserAccounts(final Company company) {
		List<UserAccount> userAccounts = yuktiMasterService.getUserAccounts(company.getId());
		if (!userAccounts.isEmpty()) {
			saveUpdateEmployeeProfile(userAccounts, company);
		}
	}
	
	private void saveUpdateEmployeeProfile(final List<UserAccount> userAccounts, final Company company) {
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyId(Boolean.TRUE);
		List<EmployeeProfile> newEmployeeProfiles = new ArrayList<>();
		final Designation defaultDesignation = designationRepository.findFirstByCompanyId(company.getId());
		final Department defaultDepartment = departmentRepository.findFirstByCompanyId(company.getId());
		if(defaultDesignation == null || defaultDepartment == null) {
			return;
		}
		Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
		for (UserAccount userAccount : userAccounts) {
			// check already exist
			EmployeeProfile toSaveEmployee;
			Optional<EmployeeProfile> opEmployee = employeeProfiles.stream()
					.filter(e -> {
						if(e.getAlias() != null && !e.getAlias().isEmpty()) {
							return e.getAlias().equals(userAccount.getUserId());
						}
						return false;
					}).findAny();
			if (opEmployee.isPresent()) {
				toSaveEmployee = opEmployee.get();
			} else {
				toSaveEmployee = new EmployeeProfile();
				toSaveEmployee.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveEmployee.setCompany(company);
				//default values
				toSaveEmployee.setAddress("No Address");
				toSaveEmployee.setDepartment(defaultDepartment);
				toSaveEmployee.setDesignation(defaultDesignation);
			}
			if(userAccount.getRealName() == null || userAccount.getRealName().isEmpty()) {
				toSaveEmployee.setName(userAccount.getUserId());
			} else {
				toSaveEmployee.setName(userAccount.getRealName());	
			}
			toSaveEmployee.setAlias(userAccount.getUserId());
			toSaveEmployee.setReferenceId(userAccount.getId()+"");
			toSaveEmployee.setPhone(userAccount.getPhone());
			Set<ConstraintViolation<EmployeeProfile>> constraintViolations = validator.validateValue(EmployeeProfile.class, "email", userAccount.getEmail());
			if(constraintViolations.isEmpty()) {
				toSaveEmployee.setEmail(userAccount.getEmail());
			}
			newEmployeeProfiles.add(toSaveEmployee);
		}
		// save to db
		employeeProfileRepository.save(newEmployeeProfiles);
	}
	
	@Transactional
	public void downloadSaveProductCategory(final Company company) {
		List<Category> categories = yuktiMasterService.getProductCategories(company.getId());
		if (!categories.isEmpty()) {
			saveUpdateProductCategory(categories, company);
		}
	}

	private void saveUpdateProductCategory(final List<Category> categories, final Company company) {
		List<ProductCategory> productCategories = productCategoryRepository.findAllByCompanyId(company.getId());
		List<ProductCategory> newProductCategories = new ArrayList<>();
		for (Category category : categories) {
			// check already exist
			ProductCategory toSaveCategory;
			Optional<ProductCategory> opCategory = productCategories.stream()
					.filter(p -> p.getAlias().equals(category.getCategoryId())).findAny();
			if (opCategory.isPresent()) {
				toSaveCategory = opCategory.get();
				toSaveCategory.setLastModifiedDate(LocalDateTime.now());
			} else {
				toSaveCategory = new ProductCategory();
				toSaveCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
				toSaveCategory.setCompany(company);
			}
			toSaveCategory.setName(category.getDescription());
			toSaveCategory.setAlias(category.getCategoryId());
			toSaveCategory.setDescription(category.getDescription());
			newProductCategories.add(toSaveCategory);
		}
		// save to db
		productCategoryRepository.save(newProductCategories);
	}

	private ProductCategory downloadSaveProductCategoryById(final String id, final Company company) {
		Optional<Category> optionalCategory = yuktiMasterService.getProductCategoryById(id, company.getId());
		if (optionalCategory.isPresent()) {
			return saveUpdateProductCategory(optionalCategory.get(), company);
		}
		return null;
	}

	private ProductCategory saveUpdateProductCategory(final Category category, final Company company) {
		ProductCategory toSaveCategory = new ProductCategory();
		toSaveCategory.setPid(ProductCategoryService.PID_PREFIX + RandomUtil.generatePid());
		toSaveCategory.setCompany(company);
		toSaveCategory.setName(category.getDescription());
		toSaveCategory.setAlias(category.getCategoryId());
		toSaveCategory.setDescription(category.getDescription());
		return productCategoryRepository.saveAndFlush(toSaveCategory);
	}

	@Transactional
	public void downloadSaveProductProfile(final Company company) {
		List<Product> products = yuktiMasterService.getProductProfiles(company.getId());
		log.info(" {} Product Profile downloaded from external API for company {}", products.size(), company.getLegalName());
		if (!products.isEmpty()) {
			deactivateProductProfiles(company);//deactivating products
			saveUpdateProductProfile(products, company);
		}
	}

	private void saveUpdateProductProfile(final List<Product> products, final Company company) {
		final Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		final ProductGroup defaultProductGroup = productGroupRepository.findFirstByCompanyId(company.getId());
		final StockLocation defaultStockLocation = stockLocationRepository.findFirstByCompanyId(company.getId());
		
		List<ProductProfile> dbProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());
		List<ProductCategory> dbProductCategories = productCategoryRepository.findAllByCompanyId(company.getId());
		
		List<ProductProfile> newProductProfiles = new ArrayList<>();
		Set<ProductGroupProduct> newProductGroupProducts = new HashSet<>();
		Set<OpeningStock> newOpeningStocks = new HashSet<>();
		for (Product product : products) {
			// check already exist
			ProductProfile toSaveProductProfile;
			
			Optional<ProductProfile> opProductProfile = dbProductProfiles.stream()
					.filter(p -> p.getAlias().equals(product.getStockId())).findAny();
			if (opProductProfile.isPresent()) {
				toSaveProductProfile = opProductProfile.get();
			} else {
				toSaveProductProfile = new ProductProfile();
				toSaveProductProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveProductProfile.setCompany(company);
				toSaveProductProfile.setAlias(product.getStockId());
				
				// defaults
				toSaveProductProfile.setDivision(defaultDivision);
				toSaveProductProfile.setPrice(new BigDecimal(0));
				newProductGroupProducts.add(new ProductGroupProduct(toSaveProductProfile, defaultProductGroup, company));
			}
			Optional<ProductCategory> optionalCategory = dbProductCategories.stream()
					.filter(c -> c.getAlias().equals(product.getCategoryId())).findAny();
			if (optionalCategory.isPresent()) {
				toSaveProductProfile.setProductCategory(optionalCategory.get());
			} else {
				// find and set
				toSaveProductProfile
						.setProductCategory(downloadSaveProductCategoryById(product.getCategoryId(), company));
			}
			toSaveProductProfile.setName(product.getDescription());
			toSaveProductProfile.setDescription(product.getLongDescription());
			toSaveProductProfile.setPrice(new BigDecimal(product.getPrice()));
			toSaveProductProfile.setMrp(new Double(product.getMrp()));
			
			toSaveProductProfile.setSku(product.getUnits());
			toSaveProductProfile.setLastModifiedDate(LocalDateTime.now());
			toSaveProductProfile.setActivated(product.getInactive() == 0 ? Boolean.TRUE : Boolean.FALSE);
			newProductProfiles.add(toSaveProductProfile);
			
			OpeningStock toSaveOpeningStock = new OpeningStock();
			
			toSaveOpeningStock.setCompany(toSaveProductProfile.getCompany());
			toSaveOpeningStock.setOpeningStockDate(LocalDateTime.now());
			toSaveOpeningStock.setCreatedDate(LocalDateTime.now());
			toSaveOpeningStock.setActivated(true);
			toSaveOpeningStock.setBatchNumber("123");
			toSaveOpeningStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid());
			toSaveOpeningStock.setProductProfile(toSaveProductProfile);
			toSaveOpeningStock.setQuantity(Double.parseDouble(product.getStock()));
			toSaveOpeningStock.setStockLocation(defaultStockLocation);
			newOpeningStocks.add(toSaveOpeningStock);
		}
		//deactivate all account profiles before save
		deactivateAccountProfile(company);
		// save to db
		productProfileRepository.save(newProductProfiles);
		if(!newProductGroupProducts.isEmpty()) {
			//productGroupProductRepository.save(newProductGroupProducts);
			bulkOperationRepositoryCustom.bulkSaveProductGroupProductProfile(newProductGroupProducts);
		}
		if(!newOpeningStocks.isEmpty()) {
			openingStockRepository.deleteByCompanyId(company.getId());
			bulkOperationRepositoryCustom.bulkSaveOpeningStocks(newOpeningStocks);
		}
	}

	
	public void deactivateProductProfiles(final Company company) {
		log.info("Deactivating the product profiles");
		productProfileRepository.deactivateAllProductProfile(company.getId());
	}
	
	public void deactivateAccountProfile(final Company company) {
		log.info("Deactivation the account profile");
		
	}
	
	
	@Transactional
	public void downloadSaveTerritories(final Company company) {
		List<SalesArea> salesAreas = yuktiMasterService.getSalesAreas(company.getId());
		if (!salesAreas.isEmpty()) {
			saveUpdateTerritories(salesAreas, company);
		}
	}

	private void saveUpdateTerritories(final List<SalesArea> salesAreas, final Company company) {
		List<Location> locations = locationRepository.findAllByCompanyIdAndLocationActivatedOrDeactivated(true);
		List<Location> newLocations = new ArrayList<>();
		for (SalesArea salesArea : salesAreas) {
			// check already exist
			Location toSaveLocation;
			Optional<Location> opLocation = locations.stream().filter(l -> {
				if (l.getAlias() != null) {
					return l.getAlias().equals(salesArea.getAreaCode());
				} else {
					return false;
				}
			}).findAny();
			if (opLocation.isPresent()) {
				toSaveLocation = opLocation.get();
			} else {
				toSaveLocation = new Location();
				toSaveLocation.setPid(LocationService.PID_PREFIX + RandomUtil.generatePid());
				toSaveLocation.setCompany(company);
			}
			toSaveLocation.setName(salesArea.getAreaName());
			toSaveLocation.setAlias(salesArea.getAreaCode());
			toSaveLocation.setDescription(salesArea.getDocPrefix());
			newLocations.add(toSaveLocation);
		}
		// save to db
		locationRepository.save(newLocations);
	}

	@Transactional
	public void downloadSaveAccountProfile(final Company company) {
		List<Branch> branches = yuktiMasterService.getBranches(company.getId());
		List<Location> locations = locationRepository.findAllByCompanyIdAndLocationActivatedOrDeactivated(company.getId(), true);
		if (!branches.isEmpty() && !locations.isEmpty()) {
			// their branches is our customer
			saveUpdateAccountProfile(branches, locations, company);
		}
	}

	private void saveUpdateAccountProfile(final List<Branch> branches, List<Location> locations, final Company company) {
		User user = null;
		Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if(optionalUser.isPresent()) {
			user = optionalUser.get();
		} 
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AT_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get first by compId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		AccountType accountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
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

		 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "AP_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get all by compId";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfile> dbAccountProfiles = accountProfileRepository.findAllByCompanyId(company.getId());
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
		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		for (Branch branch : branches) {
			// check already exist
			AccountProfile toSaveAccountProfile;
			Optional<AccountProfile> opAccountProfile = dbAccountProfiles.stream()
					.filter(ap -> branch.getBranchCode().equals(ap.getAlias())).findAny();
			if (opAccountProfile.isPresent()) {
				toSaveAccountProfile = opAccountProfile.get();
			} else {
				toSaveAccountProfile = new AccountProfile();
				toSaveAccountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveAccountProfile.setCompany(company);

				// defaults
				toSaveAccountProfile.setCity("No City");
				toSaveAccountProfile.setDataSourceType(DataSourceType.VENDOR);
				toSaveAccountProfile.setAccountStatus(AccountStatus.Verified);
			}
			toSaveAccountProfile.setUser(user);
			toSaveAccountProfile.setName(branch.getBranchName());
			toSaveAccountProfile.setAlias(branch.getBranchCode());
			toSaveAccountProfile.setAccountType(accountType);
			if(branch.getBranchAddress() != null && !branch.getBranchAddress().trim().isEmpty()) {
				toSaveAccountProfile.setAddress(branch.getBranchAddress());	
			} else {
				toSaveAccountProfile.setAddress("No Address");
			}
			toSaveAccountProfile.setDescription(branch.getDescription().trim());
			toSaveAccountProfile.setActivated(branch.getInactive() == 0 ? Boolean.TRUE : Boolean.FALSE);
			Optional<Location> opLocation = locations.stream()
					.filter(loc -> branch.getArea().equals(loc.getAlias())).findAny();
			if (opLocation.isPresent()) {
				toSaveAccountProfile.setLocation(opLocation.get().getName());
				//save account
				accountProfileRepository.save(toSaveAccountProfile);
				LocationAccountProfile locationAccounts = new LocationAccountProfile(opLocation.get(), toSaveAccountProfile, company);
				locationAccountProfiles.add(locationAccounts);
			}
		}
		//save association
		locationAccountProfileRepository.save(locationAccountProfiles);
	}

	@Transactional
	public void downloadSaveReceivables(final Company company) {
		List<Receivables> receivables = yuktiMasterService.getReceivables(company.getId());
		if (!receivables.isEmpty()) {
			deleteAndSaveReceivables(receivables, company);
		}
	}

	private void deleteAndSaveReceivables(final List<Receivables> receivables, final Company company) {
		List<ReceivablePayable> newReceivablePayables = new ArrayList<>();
		List<ReceivablePayableDTO> receivablePayable = receivablePayableService.findAllByCompanyId(company.getId());
		if (!receivablePayable.isEmpty()) {
			receivablePayableRepository.deleteByCompanyId(company.getId());
		}
		for (Receivables receivable : receivables) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "AP_QUERY_113" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get by compId and alias ignorecase";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Optional<AccountProfile> optionalAccount = accountProfileRepository
					.findByCompanyIdAndAliasIgnoreCase(company.getId(), receivable.getDebtorNo());
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
			if (optionalAccount.isPresent()) {
				ReceivablePayable toSaveReceivablePayable = new ReceivablePayable();
				toSaveReceivablePayable.setAccountProfile(optionalAccount.get());
				toSaveReceivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
				toSaveReceivablePayable.setCompany(company);
				toSaveReceivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
				toSaveReceivablePayable.setReferenceDocumentNumber(receivable.getReference());
				toSaveReceivablePayable.setReferenceDocumentAmount(receivable.getAmount());
				toSaveReceivablePayable.setReferenceDocumentBalanceAmount(receivable.getBalance());
				toSaveReceivablePayable.setReferenceDocumentDate(LocalDate.parse(receivable.getTransDate()));
				toSaveReceivablePayable.setBillOverDue(
						ChronoUnit.DAYS.between(LocalDate.parse(receivable.getDueDate()), LocalDate.now()));
				newReceivablePayables.add(toSaveReceivablePayable);
			}
		}
		// save to db
		receivablePayableRepository.save(newReceivablePayables);
		updateAccountProfileClosingbalance(newReceivablePayables);
	}

	private void updateAccountProfileClosingbalance(List<ReceivablePayable> receivablePayables) {
		Map<Long, AccountProfile> accountProfiles = new HashMap<>();
		for (ReceivablePayable receivablePayable : receivablePayables) {
			if (accountProfiles.containsKey(receivablePayable.getAccountProfile().getId())) {
				AccountProfile accProfile = accountProfiles.get(receivablePayable.getAccountProfile().getId());
				accProfile.setClosingBalance(
						accProfile.getClosingBalance() + receivablePayable.getReferenceDocumentBalanceAmount());
				accountProfiles.put(accProfile.getId(), accProfile);
			} else {
				AccountProfile accProfile = receivablePayable.getAccountProfile();
				accProfile.setClosingBalance(receivablePayable.getReferenceDocumentBalanceAmount());
				accountProfiles.put(accProfile.getId(), accProfile);
			}
		}
		accountProfileRepository.save(accountProfiles.values());
	}

}
