package com.orderfleet.webapp.web.vendor.integre.service;

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
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupProduct;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserPriceLevel;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentPriceLevelRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserPriceLevelRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;

@Service
@Transactional
public class AssignUploadDataService {
	
	private static final Logger log = LoggerFactory.getLogger(AssignUploadDataService.class);
	
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
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private PriceLevelRepository priceLevelRepository;
	
	@Inject
	private UserPriceLevelRepository userPriceLevelRepository;
	
	@Inject
	private DocumentPriceLevelRepository documentPriceLevelRepository;
	

	public void userStockLocationAssociation(List<String> newStockLocationAliasList,Company company) {
		log.info("new socklocation size:"+newStockLocationAliasList.size());
		List<UserStockLocation> userStockLocations = new ArrayList<>();
		List<StockLocation> newStockLocations = new ArrayList<>();
		List<StockLocation> existingStockLocations = stockLocationRepository
				 .findAllByCompanyId(company.getId());
		
		for(String newStockLocationAlias: newStockLocationAliasList) {
			Optional<StockLocation> stockLocationExist = existingStockLocations.stream()
					.filter(stockLocation ->stockLocation.getAlias() != null ? stockLocation.getAlias()
							.equals(newStockLocationAlias):false).findAny();
			if(stockLocationExist.isPresent()){
				newStockLocations.add(stockLocationExist.get());
			}
		}
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for(User user : users) {
			for(StockLocation stockLoc : newStockLocations) {
				userStockLocations.add(new UserStockLocation(user, stockLoc,company));
			}
		}
		userStockLocationRepository.save(userStockLocations);
	}
	

	public void documentStockLocationAssociation(List<String> newStockLocationAliasList,Company company) {
		List<DocumentStockLocationSource> documentStockLocationSource = new ArrayList<>();
		List<StockLocation> newStockLocations = new ArrayList<>();
		List<StockLocation> existingStockLocations = stockLocationRepository
				 .findAllByCompanyId(company.getId());
		
		for(String newStockLocationAlias: newStockLocationAliasList) {
			Optional<StockLocation> stockLocationExist = existingStockLocations.stream()
					.filter(stockLocation ->stockLocation.getAlias() != null ? stockLocation.getAlias()
							.equals(newStockLocationAlias):false).findAny();
			if(stockLocationExist.isPresent()){
				newStockLocations.add(stockLocationExist.get());
			}
		}
		
		List<Document> documents = documentRepository
				.findAllDocumentsByCompanyPidAndDocumentType(DocumentType.INVENTORY_VOUCHER,company.getPid());
		for(Document document : documents){
			for(StockLocation stockLocation : newStockLocations) {
				documentStockLocationSource.add(new DocumentStockLocationSource(document,stockLocation,company,false));
			}
		}
		documentStockLocationSourceRepository.save(documentStockLocationSource);
	}
	
	public void productProductGroupAssociation(List<String> newProductProfileAliasList,Company company) {
		List<ProductGroupProduct> productGroupProducts = new ArrayList<>();
		List<ProductProfile> newProductProfiles = new ArrayList<>();
		List<ProductProfile> existingProductProfiles = productProfileRepository
				 .findAllByCompanyId(company.getId());
		
		for(String newProductProfileAlias: newProductProfileAliasList) {
			Optional<ProductProfile> productProfileExist = existingProductProfiles.stream()
					.filter(productProfile ->productProfile.getAlias() != null ? productProfile.getAlias()
							.equals(newProductProfileAlias):false).findAny();
			if(productProfileExist.isPresent()){
				newProductProfiles.add(productProfileExist.get());
			}
		}
		
		final ProductGroup defaultProductGroup = productGroupRepository.findFirstByCompanyId(company.getId());
		for(ProductProfile productProfile : newProductProfiles) {
			productGroupProducts.add(new ProductGroupProduct(productProfile,defaultProductGroup,company));
		}
		productGroupProductRepository.save(productGroupProducts);
	}
	
	public void employeeProfileLocationAssociation(List<String> newEmployeeProfileAliasList, Company company) {
		List<EmployeeProfileLocation> employeeProfileLocations = new ArrayList<>();
		List<EmployeeProfile> newEmployeeProfiles = new ArrayList<>();
		List<EmployeeProfile> existingEmployeeProfiles = employeeProfileRepository
				 .findAllByCompanyId(company.getId());
		
		for(String newEmployeeProfileAlias: newEmployeeProfileAliasList) {
			Optional<EmployeeProfile> employeeProfileExist = existingEmployeeProfiles.stream()
					.filter(eProfile ->eProfile.getAlias() != null ? eProfile.getAlias().equals(newEmployeeProfileAlias):false)
					.findAny();
			if(employeeProfileExist.isPresent()){
				newEmployeeProfiles.add(employeeProfileExist.get());
			}
		}
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for(Location location : locations){
			for(EmployeeProfile employeeProfile : newEmployeeProfiles) {
				employeeProfileLocations.add(new EmployeeProfileLocation(employeeProfile,location));
			}
		}
		employeeProfileLocationRepository.save(employeeProfileLocations);
	}
	
	public void locationAccountProfileAssociation(List<String> newAccountProfileAliasList,Company company) {
		List<LocationAccountProfile> locationAccountProfiles = new ArrayList<>();
		List<AccountProfile> newAccountProfiles = new ArrayList<>();
		List<AccountProfile> existingAccountProfiles = accountProfileRepository
				 .findAllByCompanyId(company.getId());
		
		for(String newAccountProfileAlias: newAccountProfileAliasList) {
			Optional<AccountProfile> accountProfileExist = existingAccountProfiles.stream()
					.filter(aProfile ->aProfile.getAlias() != null ? aProfile.getAlias().equals(newAccountProfileAlias):false)
					.findAny();
			if(accountProfileExist.isPresent()){
				newAccountProfiles.add(accountProfileExist.get());
			}
		}
		List<Location> locations = locationRepository.findAllByCompanyId(company.getId());
		for(Location location : locations){
			for(AccountProfile accountProfile : newAccountProfiles) {
				locationAccountProfiles.add(new LocationAccountProfile(location,accountProfile,company));
			}
		}
		locationAccountProfileRepository.save(locationAccountProfiles);
	}
	
	public void userPriceLevelAssociation(List<String> newPriceLevelCodeList, Company company) {
		List<UserPriceLevel> userPriceLevels = new ArrayList<>();
		List<PriceLevel> newPriceLevels = new ArrayList<>();
		List<PriceLevel> existingPriceLevels = priceLevelRepository.findByCompanyId(company.getId());
		
		for(String newPriceLevelName: newPriceLevelCodeList) {
			Optional<PriceLevel> priceLevelExist = existingPriceLevels.stream()
					.filter(pLevel ->pLevel.getAlias() != null ? pLevel.getAlias().equals(newPriceLevelName):false).findAny();
			if(priceLevelExist.isPresent()){
				newPriceLevels.add(priceLevelExist.get());
			}
		}
		
		List<User> users = userRepository.findAllByCompanyPid(company.getPid());
		for(User user : users) {
			for(PriceLevel priceLevel : existingPriceLevels) {
				if(!priceLevel.getName().equals("General")) {
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
