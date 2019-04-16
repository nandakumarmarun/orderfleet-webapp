package com.orderfleet.webapp.web.tally.service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPriceLevel;
import com.orderfleet.webapp.domain.DocumentProductCategory;
import com.orderfleet.webapp.domain.DocumentProductGroup;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserPriceLevel;
import com.orderfleet.webapp.domain.UserProductCategory;
import com.orderfleet.webapp.domain.UserProductGroup;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.DocumentPriceLevelRepository;
import com.orderfleet.webapp.repository.DocumentProductCategoryRepository;
import com.orderfleet.webapp.repository.DocumentProductGroupRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserPriceLevelRepository;
import com.orderfleet.webapp.repository.UserProductCategoryRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.web.tally.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.tally.dto.LocationDTO;
import com.orderfleet.webapp.web.tally.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.tally.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.tally.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.tally.dto.StockLocationDTO;

@Service
@Transactional
public class AssignTallyDataService {
	
	private static final Logger log = LoggerFactory.getLogger(AssignTallyDataService.class);
	
	@Inject
	private UserStockLocationRepository userStockLocationRepository;
	
	@Inject
	private StockLocationRepository stockLocationRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	
	@Inject
	private ProductGroupRepository productGroupRepository;
	
	@Inject
	private ProductGroupProductRepository productGroupProductRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private LocationRepository locationRepository;
	
	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
	
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private PriceLevelRepository priceLevelRepository;
	
	@Inject
	private UserPriceLevelRepository userPriceLevelRepository;
	
	@Inject
	private DocumentPriceLevelRepository documentPriceLevelRepository;
	
	@Inject
	private LocationHierarchyRepository locationHierarchyRepository;
	
	@Inject
	private ProductCategoryRepository productCategoryRepository;
	
	@Inject
	private DocumentProductGroupRepository documentProductGroupRepository;
	
	@Inject
	private DocumentProductCategoryRepository documentProductCategoryRepository;
	
	@Inject
	private UserProductGroupRepository userProductGroupRepository;
	
	@Inject
	private UserProductCategoryRepository userProductCategoryRepository;
	
	
		public void assignDocumentProductCategory(List<ProductCategoryDTO> productCategoryDtos, Company company) {
			List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
			List<ProductCategory> productCatagories = productCategoryRepository.findAllByCompanyPid(company.getPid());
			List<DocumentProductCategory> documentProductCategories = new ArrayList<>();
			for(Document document : documents){
				if(document.getDocumentType() == DocumentType.INVENTORY_VOUCHER){
					for(ProductCategory productCategory : productCatagories){
						documentProductCategories.add(new DocumentProductCategory(document, productCategory, company));
					}
				}
			}
			documentProductCategoryRepository.deleteByCompanyId(company.getId());
			documentProductCategoryRepository.save(documentProductCategories);
		}
		
		
		public void assignDocumentProductGroup(List<ProductGroupDTO> productGroupDtos, Company company) {
			List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(company.getPid());
			List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPid(company.getPid());
			List<DocumentProductGroup> documentProductGroups = new ArrayList<>();
			int sortOrder = 1;
			for(Document document : documents){
				if(document.getDocumentType() == DocumentType.INVENTORY_VOUCHER){
					for(ProductGroup productGroup : productGroups){
						documentProductGroups.add(new DocumentProductGroup(document, productGroup, company,sortOrder));
						sortOrder++;
					}
				}
			}
			documentProductGroupRepository.deleteByCompanyId(company.getId());
			documentProductGroupRepository.save(documentProductGroups);
		}
		
		
		public void assignUserProductGroup(List<ProductGroupDTO> productGroupDtos, List<ProductGroup> existingProductGroups, Company company) {
			List<User> users = userRepository.findAllByCompanyPid(company.getPid());
			List<ProductGroup> newProductGroups = productGroupRepository.findAllByCompanyPid(company.getPid());
			List<UserProductGroup> userProductGroups = new ArrayList<>();
			
			for(ProductGroup productGroup : newProductGroups){
				
				Optional<ProductGroup> productGroupName = existingProductGroups.stream()
						.filter(upg ->upg.getName().equals(productGroup.getName())).findAny();
				if(!productGroupName.isPresent()){
					for(User user : users){
						userProductGroups.add(new UserProductGroup(user,productGroup,company));
					}
				}
			}
			userProductGroupRepository.save(userProductGroups);
		}
		
		
		public void assignUserProductCategory(List<ProductCategoryDTO> productCategoriesDtos, List<ProductCategory> existingProductCategories, Company company) {
			List<User> users = userRepository.findAllByCompanyPid(company.getPid());
			List<ProductCategory> newProductCategories = productCategoryRepository.findAllByCompanyPid(company.getPid());
			List<UserProductCategory> userProductCategories = new ArrayList<>();
			
			for(ProductCategory productCategory : newProductCategories){
				
				Optional<ProductCategory> productCategoryName = existingProductCategories.stream()
						.filter(upc ->upc.getName().equals(productCategory.getName())).findAny();
				if(!productCategoryName.isPresent()){
					for(User user : users){
						userProductCategories.add(new UserProductCategory(user,productCategory,company));
					}
				}
			}
			userProductCategoryRepository.save(userProductCategories);
		}
		
 
	public void userStockLocationAssociation(List<StockLocation> existingStockLocations, List<StockLocationDTO> stockLocationDtos, Company company) {
		log.info("Assigning users and stocklocations");
		List<UserStockLocation> userStockLocations = new ArrayList<>();
		List<StockLocation> newExistingStockLocations = stockLocationRepository.findAllByCompanyId(company.getId());
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		
		for(StockLocation stockLocation : newExistingStockLocations){
			//checking company tally contain "Main Location"
			Optional<StockLocationDTO> mainLocationExist = stockLocationDtos.stream()
					.filter(sl -> sl.getName().equals("Main Location")).findAny();
			
			//assigning users to new stock locations
			Optional<StockLocation> stockLocationName = existingStockLocations.stream()
					.filter(sln ->sln.getName().equals(stockLocation.getName())).findAny();
			if(!stockLocationName.isPresent()){
				for(User user : users) {
					userStockLocations.add(new UserStockLocation(user, stockLocation,company));
				}
			}
			//removing association of users with "Main Location" if company tally does not contain "Main Location"
			if(!mainLocationExist.isPresent() && stockLocation.getName().equals("Main Location")){
				userStockLocationRepository.deleteByCompanyIdAndStockLocationPid(company.getId(), stockLocation.getPid());
			}
		}
		userStockLocationRepository.save(userStockLocations);
	}
	

	public void documentStockLocationAssociation(List<StockLocation> existingStockLocations, List<StockLocationDTO> stockLocationDtos, Company company) {
		log.info("Assigning documents and stocklocations");
		List<DocumentStockLocationSource> documentStockLocationSource = new ArrayList<>();
		List<StockLocation> newExistingStockLocations = stockLocationRepository
				 .findAllByCompanyId(company.getId());
		List<Document> documents = documentRepository
				.findAllDocumentsByCompanyPidAndDocumentType(DocumentType.INVENTORY_VOUCHER,company.getPid());
		List<String> documentStockLocation = documentStockLocationSourceRepository.findPidByCompanyId(company.getId());
		
		for(StockLocation stockLocation : newExistingStockLocations){
			Optional<StockLocationDTO> mainLocationExist = stockLocationDtos.stream()
					.filter(sl -> sl.getName().equals("Main Location")).findAny();
			
			Optional<StockLocation> stockLocationName = existingStockLocations.stream()
					.filter(sln ->sln.getName().equals(stockLocation.getName())).findAny();
			if(!stockLocationName.isPresent()){
				for(Document document : documents){
					documentStockLocationSource.add(new DocumentStockLocationSource(document,stockLocation,company,false));
				}
			}
			//assigning stocklocation if company tally contains "Main Location"
			else if(mainLocationExist.isPresent() && documentStockLocation.size() == 0){
				for(Document document : documents){
					documentStockLocationSource.add(new DocumentStockLocationSource(document,stockLocation,company,false));
				}
			}
		}
		documentStockLocationSourceRepository.save(documentStockLocationSource);
	}
	
	public void productProductGroupAssociation(List<ProductProfileDTO> productProfileDtos,Company company) {
		List<ProductGroupProduct> productGroupProducts = new ArrayList<>();
		List<ProductProfile> existingProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());
		List<ProductGroup> existingProductGroups = productGroupRepository.findByCompanyId(company.getId());
		ProductGroup defaultProductGroup = productGroupRepository.findFirstByCompanyIdOrderById(company.getId());
		
		for(ProductProfileDTO productProfile: productProfileDtos) {
			Optional<ProductProfile> productProfileExist = existingProductProfiles.stream()
					.filter(pp ->pp.getName().equals(productProfile.getName())).findAny();
			Optional<ProductGroup> productGroupExist = existingProductGroups.stream()
					.filter(pg ->pg.getName().equals(productProfile.getDescription())).findAny();
			if(productProfileExist.isPresent()){
				if(productGroupExist.isPresent()){
					productGroupProducts.add(new ProductGroupProduct(productProfileExist.get(),productGroupExist.get(),company));
				}else{
					productGroupProducts.add(new ProductGroupProduct(productProfileExist.get(),defaultProductGroup,company));
				}
			}
		}
		productGroupProductRepository.deleteByCompanyId(company.getId());
		productGroupProductRepository.save(productGroupProducts);
	}
	
	public void employeeProfileLocationAssociation(List<Location> exisitngLocations, 
			List<LocationDTO> locationDtos, Company company) {
		
		List<EmployeeProfileLocation> employeeProfileLocations = new ArrayList<>();
		List<EmployeeProfile> employees = employeeProfileRepository.findAllByCompanyId(company.getId());
		List<Location> newLocations = locationRepository.findAllByCompanyId(company.getId());
		
		for(Location location : newLocations){
			Optional<Location> locationExist = exisitngLocations.stream()
					.filter(loc -> loc.getName().equals(location.getName())).findAny();
			
			if(!locationExist.isPresent()){
				for(EmployeeProfile employeeProfile : employees){
					employeeProfileLocations.add(new EmployeeProfileLocation(employeeProfile, location));
				}
			}
		}
		employeeProfileLocationRepository.save(employeeProfileLocations);
	}
	
	public void locationAccountProfileAssociation(List<AccountProfile> accountProfileList,
										List<AccountProfileDTO> accountProfileDtos, Company company) {
		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		
		for(AccountProfileDTO accountProfileDto: accountProfileDtos) {
			Optional<AccountProfile> accountProfile = accountProfileList.stream().filter(ap -> ap.getName().equals(accountProfileDto.getName())).findAny();
			Optional<Location> location = locations.stream().filter(loc -> loc.getName().equals(accountProfileDto.getDescription())).findAny();
			if(accountProfile.isPresent() && location.isPresent()) {
				locationAccountProfiles.add(new LocationAccountProfile(location.get(),accountProfile.get(),company));
			}
		}
		
		locationAccountProfileRepository.save(locationAccountProfiles);
	}
	
	public void locationHierarchyAssociation(List<LocationDTO> locationDto ,Company company) {
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		List<LocationHierarchy> locationHierarchyList = new ArrayList<>();
		log.debug("location dto size in location hierarchy association "+locationDto.size());
		
		for(LocationDTO locDto : locationDto) {
			System.out.println(locDto.getName());
			if(locDto.getDescription().equals("Current Assets")) {
				locDto.setDescription("Territory");
			}
			Optional<Location> opLocation = locations.stream().filter(loc -> loc.getName().equals(locDto.getName())).findAny();
			Optional<Location> opParent = locations.stream().filter(loc -> loc.getName().equals(locDto.getDescription())).findAny();
			
			if(opLocation.isPresent() && opParent.isPresent()) {
				System.out.println(opLocation.get().getName());
				LocationHierarchy locationHierarchy = new LocationHierarchy();
				locationHierarchy.setActivatedDate(ZonedDateTime.now());
				locationHierarchy.setActivated(true);
				locationHierarchy.setCompany(company);
				locationHierarchy.setInactivatedDate(ZonedDateTime.now());
				locationHierarchy.setLocation(opLocation.get());
				locationHierarchy.setParent(opParent.get());
				locationHierarchy.setVersion(1L);
				locationHierarchyList.add(locationHierarchy);
			}
		}
		if(locationHierarchyList.size() != 0) {
			locationHierarchyRepository.deleteByCompanyIdAndParentNotNull(company.getId());
			locationHierarchyRepository.save(locationHierarchyList);
		}
		
	}
	
	public void userPriceLevelAssociation(List<PriceLevel> existingPriceLevels, Company company) {
		List<UserPriceLevel> userPriceLevels = new ArrayList<>();
		List<PriceLevel> newPriceLevels = priceLevelRepository.findByCompanyId(company.getId());
		
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
			for(PriceLevel priceLevel : newPriceLevels) {
				Optional<PriceLevel> opPriceLevel = existingPriceLevels.stream()
						.filter(pp -> pp.getName().equals(priceLevel.getName())).findAny();
				
				if(!opPriceLevel.isPresent() && !priceLevel.getName().equals("General")){
					for(User user : users) {
						userPriceLevels.add(new UserPriceLevel(user,priceLevel,company));
					}
				}
			}
		userPriceLevelRepository.save(userPriceLevels);
	}
	
	public void documentPriceLevelAssociation(Company company) {
		List<DocumentPriceLevel> documentPriceLevels = new ArrayList<>();
		List<PriceLevel> existingPriceLevels = priceLevelRepository.findByCompanyId(company.getId());
		List<Document> documents = documentRepository
				.findAllDocumentsByCompanyPidAndDocumentType(DocumentType.INVENTORY_VOUCHER,company.getPid());
		for(Document document : documents) {
			for(PriceLevel priceLevel : existingPriceLevels){
				if(!priceLevel.getName().equals("General")) {
					documentPriceLevels.add(new DocumentPriceLevel(document,priceLevel,company));
				}
			}
		}
		documentPriceLevelRepository.deleteByCompanyId(company.getId());
		documentPriceLevelRepository.save(documentPriceLevels);
	}
	
	public void employeeUserAssociation(List<String> newEmployeeProfileCodes, Company company) {
		
		List<EmployeeProfile> existingEmployeeProfiles = employeeProfileRepository
				 .findAllByCompanyId(company.getId());
		
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority("ROLE_EXECUTIVE"));
		List<User> existingUsers = userRepository
				.findAllByCompanyIdAndAuthoritiesInAndActivated(company.getId(),authorities,true);
		
		List<EmployeeProfile> newEmployeeProfiles = new ArrayList<>();
		
		//finding new employees
		for(EmployeeProfile employeeProfile : existingEmployeeProfiles){
			if(employeeProfile.getUser() == null){
				newEmployeeProfiles.add(employeeProfile);
			}
		}
		
		//finding not-assigned users
		for(EmployeeProfile employee : existingEmployeeProfiles){
			if(employee.getUser() != null){
				existingUsers.removeIf(u ->u.getPid().equals(employee.getUser().getPid()));
			}
		}
		
		int index;
		if(existingUsers.size()>=newEmployeeProfiles.size()){
			index = newEmployeeProfiles.size();
		}else{
			index = existingUsers.size();
		}
		
		//assigning new employees with non-assigned users
		for(int i=0 ; i<index; i++){
			newEmployeeProfiles.get(i).setUser(existingUsers.get(i));
			employeeProfileRepository.save(newEmployeeProfiles.get(i));
		}
	}
}
