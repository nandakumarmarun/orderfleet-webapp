package com.orderfleet.webapp.web.vendor.integre.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StockLocationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.DivisionRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.integre.dto.AccountProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.EmployeeProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.OpeningStockVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.PriceLevelVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.ProductProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.StockLocationVendorDTO;

@Service
@Transactional
public class MasterDataUploadService {

	private static final Logger log = LoggerFactory.getLogger(MasterDataUploadService.class);
	
	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private DivisionRepository divisionRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Inject
	private PriceLevelRepository priceLevelRepository;
	

	public void saveOrUpdateStockLocation(List<StockLocationVendorDTO> stockLocationDtos, Company company) {
		for (StockLocationVendorDTO stockLocationDto : stockLocationDtos) {
			StockLocation location = new StockLocation();
			List<StockLocation> dbStockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
			Optional<StockLocation> stockLocation = dbStockLocations.stream()
					.filter(stock -> stock.getAlias()  !=null ? stock.getAlias().equals(stockLocationDto.getCode()):false).findAny();
			if (stockLocation.isPresent()) {
				location = stockLocation.get();
			} else {
				location.setAlias(stockLocationDto.getCode());
				location.setCompany(company);
				location.setStockLocationType(StockLocationType.ACTUAL);
				location.setPid(StockLocationService.PID_PREFIX  + RandomUtil.generatePid());
			}
			location.setName(stockLocationDto.getName());
			stockLocationRepository.save(location);
		}
	}

	public void saveOrUpdateAccountProfile(List<AccountProfileVendorDTO> accountProfileDtos, Company company) {
		log.info("Account Profile list size : {}"+accountProfileDtos.size());
		AccountProfile toSaveAccountProfile;
		AccountType accountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
		List<AccountProfile> dbAccountProfiles = accountProfileRepository
				.findAllByCompanyId(company.getId());
		for (AccountProfileVendorDTO apUpload : accountProfileDtos) {
			Optional<AccountProfile> opAccountProfile = dbAccountProfiles.stream()
					.filter(ap -> ap.getAlias() !=null ? ap.getAlias().equals(apUpload.getCode()):false).findAny();
			if (opAccountProfile.isPresent()) {
				toSaveAccountProfile = opAccountProfile.get();
			} else {
				toSaveAccountProfile = new AccountProfile();
				toSaveAccountProfile.setPid(AccountProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveAccountProfile.setAlias(apUpload.getCode());
				toSaveAccountProfile.setCompany(company);
				// defaults
				toSaveAccountProfile.setCity("No City");
				toSaveAccountProfile.setDataSourceType(DataSourceType.VENDOR);
				toSaveAccountProfile.setAccountStatus(AccountStatus.Verified);
				toSaveAccountProfile.setAccountType(accountType);
			}
			toSaveAccountProfile.setName(apUpload.getName());
			toSaveAccountProfile.setAddress(apUpload.getAddress() == null || apUpload.getAddress() == "" ? "No Address" : apUpload.getAddress());
			toSaveAccountProfile.setLocation(apUpload.getLocation());
			toSaveAccountProfile.setPhone1(apUpload.getPhone1());
			toSaveAccountProfile.setPhone2(apUpload.getPhone2());
			toSaveAccountProfile.setEmail1(apUpload.getEmail1() == "" ? null : apUpload.getEmail1());
			toSaveAccountProfile.setClosingBalance(apUpload.getClosingBalance());
			accountProfileRepository.save(toSaveAccountProfile);
		}
	}
	
	public void saveOrUpdateAccountProfileByPid(List<AccountProfileVendorDTO> accountProfileDtos, Company company) {
		log.info("Account Profile list size : {}"+accountProfileDtos.size());
		AccountProfile toSaveAccountProfile=new AccountProfile();
		AccountType accountType = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(company.getId());
		List<AccountProfile> dbAccountProfiles = accountProfileRepository
				.findAllByCompanyId(company.getId());
		for (AccountProfileVendorDTO apUpload : accountProfileDtos) {
			Optional<AccountProfile> opAccountProfile = dbAccountProfiles.stream()
					.filter(ap -> ap.getPid() !=null ? ap.getPid().equals(apUpload.getPid()):null).findAny();
			if (opAccountProfile.isPresent()) {
				toSaveAccountProfile = opAccountProfile.get();
				toSaveAccountProfile.setAlias(apUpload.getCode());
				toSaveAccountProfile.setCompany(company);
				// defaults
				toSaveAccountProfile.setCity("No City");
				toSaveAccountProfile.setDataSourceType(DataSourceType.VENDOR);
				toSaveAccountProfile.setAccountStatus(AccountStatus.Verified);
				toSaveAccountProfile.setAccountType(accountType);
				toSaveAccountProfile.setName(apUpload.getName());
				toSaveAccountProfile.setAddress(apUpload.getAddress() == null || apUpload.getAddress() == "" ? "No Address" : apUpload.getAddress());
				toSaveAccountProfile.setLocation(apUpload.getLocation());
				toSaveAccountProfile.setPhone1(apUpload.getPhone1());
				toSaveAccountProfile.setPhone2(apUpload.getPhone2());
				toSaveAccountProfile.setEmail1(apUpload.getEmail1() == "" ? null : apUpload.getEmail1());
				toSaveAccountProfile.setClosingBalance(apUpload.getClosingBalance());
			}
			accountProfileRepository.save(toSaveAccountProfile);
		}
	}

	public void saveOrUpdateProductProfiles(List<ProductProfileVendorDTO> productProfileDTOs, Company company) {
		log.info("ProductProfile list size : {}"+productProfileDTOs.size());
		final Division defaultDivision = divisionRepository.findFirstByCompanyId(company.getId());
		final ProductCategory defaultProductCategory = productCategoryRepository.findFirstByCompanyId(company.getId());
		List<ProductProfile> dbProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());
		List<ProductProfile> newProductProfiles = new ArrayList<>();
		for (ProductProfileVendorDTO product : productProfileDTOs) {
			// check already exist
			ProductProfile toSaveProductProfile;
			Optional<ProductProfile> opProductProfile = dbProductProfiles.stream()
					.filter(p -> p.getAlias() !=null ?p.getAlias().equals(product.getCode()):false).findAny();
			if (opProductProfile.isPresent()) {
				toSaveProductProfile = opProductProfile.get();
			} else {
				toSaveProductProfile = new ProductProfile();
				toSaveProductProfile.setPid(ProductProfileService.PID_PREFIX + RandomUtil.generatePid());
				toSaveProductProfile.setCompany(company);
				toSaveProductProfile.setAlias(product.getCode());
				// defaults
				toSaveProductProfile.setDivision(defaultDivision);
				toSaveProductProfile.setProductCategory(defaultProductCategory);
			}
			toSaveProductProfile.setName(product.getName());
			toSaveProductProfile.setSku(product.getSku());
			toSaveProductProfile.setTaxRate(product.getTaxRate());
			toSaveProductProfile.setMrp(product.getMrp());
			toSaveProductProfile.setPrice(product.getPrice());
			Optional<ProductProfile> opPProfile = newProductProfiles.stream().filter(pp -> pp.getName().equals(product.getName())).findAny();
			if(!opPProfile.isPresent()) {
				newProductProfiles.add(toSaveProductProfile);
			}
			
		}
		productProfileRepository.save(newProductProfiles);
	}

	public void saveOrUpdateOpeningStock(List<OpeningStockVendorDTO> openingStockDtos, Company company) {
		log.info("OpeningStock list size : {}"+openingStockDtos.size());
		List<StockLocation> stockLocationList = stockLocationRepository.findAllByCompanyId(company.getId());
		List<ProductProfile> productProfileList = productProfileRepository.findAllByCompanyId(company.getId());
		List<OpeningStock> openingStockList = openingStockRepository.findAllByCompanyId(company.getId());
		List<OpeningStock> saveOpeningStock = new ArrayList<OpeningStock>();
		
		for (OpeningStockVendorDTO opvendor : openingStockDtos) {
			OpeningStock openingStock = new OpeningStock();
			  if(opvendor.getProductProfileId() != null && opvendor.getStockLocationId() != null) {
					Optional<OpeningStock> opOpeningStock = openingStockList.stream().filter(os -> {
						if (os.getStockLocation().getAlias() != null && os.getStockLocation().getAlias().equals(opvendor.getStockLocationId())
								&& os.getProductProfile().getAlias() != null && os.getProductProfile().getAlias().equals(opvendor.getProductProfileId())) {
							return true;
						} else {
							return false;
						}
					}).findAny();
					if (opOpeningStock.isPresent()) {
						openingStock = opOpeningStock.get();
					} else {
							Optional<StockLocation> stockLocation = stockLocationList.stream()
									.filter(st -> st.getAlias() != null ? st.getAlias().equals(opvendor.getStockLocationId()):false).findAny();
								if (stockLocation.isPresent()) {
									openingStock.setStockLocation(stockLocation.get());
								}
							Optional<ProductProfile> productProfile = productProfileList.stream()
									.filter(pp -> pp.getAlias() != null ? pp.getAlias().equals(opvendor.getProductProfileId()):false).findAny();
								if (productProfile.isPresent()) {
									openingStock.setProductProfile(productProfile.get());
								}
					}
			  }	
			
			openingStock.setCreatedDate(LocalDateTime.now());
			openingStock.setQuantity(opvendor.getStock());
			openingStock.setCompany(company);
			openingStock.setOpeningStockDate(LocalDateTime.now());
			openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid());
			if (openingStock.getStockLocation() != null && openingStock.getProductProfile() != null) {
				saveOpeningStock.add(openingStock);
			}
		}
		if(saveOpeningStock.size() != 0) {
			openingStockRepository.save(saveOpeningStock);
		}
	}

	public void saveOrUpdateEmployeeProfile(List<EmployeeProfileVendorDTO> employeeProfileDTOs, Company company) {
		log.info("EmployeeProfile list size : {}"+employeeProfileDTOs.size());
		Department deafultDepartment = departmentRepository.findFirstByCompanyId(company.getId());
		Designation deafultDesignation = designationRepository.findFirstByCompanyId(company.getId());
		List<EmployeeProfile> employeeProfileList = employeeProfileRepository.findAllByCompanyPid(company.getPid());
		for (EmployeeProfileVendorDTO employeeDTO : employeeProfileDTOs) {
			EmployeeProfile employeeProfile = new EmployeeProfile();
			Optional<EmployeeProfile> employee = employeeProfileList.stream()
					.filter(emp -> emp.getAlias() !=null ?emp.getAlias().equals(employeeDTO.getCode()):false).findAny();
			if (employee.isPresent()) {
				employeeProfile = employee.get();
			} else {
				employeeProfile.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
				employeeProfile.setActivated(true);
				employeeProfile.setCompany(company);
				employeeProfile.setDepartment(deafultDepartment);
				employeeProfile.setDesignation(deafultDesignation);
				employeeProfile.setUser(null);
			}
			employeeProfile.setCreatedDate(ZonedDateTime.now());
			employeeProfile.setName(employeeDTO.getName());
			employeeProfile.setAlias(employeeDTO.getCode());
			employeeProfile.setAddress(employeeDTO.getAddress() == null || employeeDTO.getAddress() == "" ? "No Address" :employeeDTO.getAddress());
			employeeProfileList.add(employeeProfile);
		}
		employeeProfileRepository.save(employeeProfileList);
	}
	
	public void saveOrUpdatePriceLevel(List<PriceLevelVendorDTO> priceLevelVendorDTOs ,Company company) {
		log.info("PriceLevel list size : {}" + priceLevelVendorDTOs.size());
		List<PriceLevel> priceLevelList = priceLevelRepository.findByCompanyId(company.getId());
		for(PriceLevelVendorDTO priceLevelVendorDTO : priceLevelVendorDTOs){
			PriceLevel priceLevel = new PriceLevel();
			Optional<PriceLevel> priceLevelExist = priceLevelList.stream()
					.filter(pLevel -> pLevel.getAlias() !=null ?pLevel.getAlias().equals(priceLevelVendorDTO.getCode()):false)
					.findAny();
			if (priceLevelExist.isPresent()) {
				priceLevel = priceLevelExist.get();
			}else{
				priceLevel.setPid(PriceLevelService.PID_PREFIX + RandomUtil.generatePid());
				priceLevel.setAlias(priceLevelVendorDTO.getCode());
				priceLevel.setCompany(company);
				priceLevel.setDescription(priceLevelVendorDTO.getDescription());
			}
			priceLevel.setName(priceLevelVendorDTO.getName());
			priceLevel.setSortOrder(priceLevelVendorDTO.getSortOrder());
			priceLevelList.add(priceLevel);
		}
		priceLevelRepository.save(priceLevelList);
	}

}
