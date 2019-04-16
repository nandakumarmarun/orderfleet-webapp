package com.orderfleet.webapp.web.rest.tallypartner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPriceLevel;
import com.orderfleet.webapp.domain.DocumentProductCategory;
import com.orderfleet.webapp.domain.DocumentProductGroup;
import com.orderfleet.webapp.domain.DocumentStockLocationDestination;
import com.orderfleet.webapp.domain.DocumentStockLocationSource;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.ProductCategory;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserPriceLevel;
import com.orderfleet.webapp.domain.UserProductCategory;
import com.orderfleet.webapp.domain.UserProductGroup;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentPriceLevelRepository;
import com.orderfleet.webapp.repository.DocumentProductCategoryRepository;
import com.orderfleet.webapp.repository.DocumentProductGroupRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationDestinationRepository;
import com.orderfleet.webapp.repository.DocumentStockLocationSourceRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductCategoryRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserPriceLevelRepository;
import com.orderfleet.webapp.repository.UserProductCategoryRepository;
import com.orderfleet.webapp.repository.UserProductGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeProfileService;

/**
 * save datas to documentuserwiseupdate.
 *
 * @author Sarath
 * @since Feb 24, 2018
 *
 */

@Component
@Transactional
public class DocumentUserWiseUpdateController {

	private final Logger log = LoggerFactory.getLogger(DocumentUserWiseUpdateController.class);

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private ProductCategoryRepository productCategoryRepository;

	@Inject
	private DocumentProductCategoryRepository documentProductCategoryRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private DocumentProductGroupRepository documentProductGroupRepository;

	@Inject
	private DocumentStockLocationSourceRepository documentStockLocationSourceRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private DocumentStockLocationDestinationRepository stockLocationDestinationRepository;

	@Inject
	private DocumentPriceLevelRepository documentPriceLevelRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private UserProductGroupRepository userProductGroupRepository;

	@Inject
	private UserProductCategoryRepository userProductCategoryRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private UserPriceLevelRepository userPriceLevelRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	public void assighnDocumentProductCategory() {
		log.debug("request to save Document Product Categories");

		List<Document> documents = documentRepository.findAllDocumentsByDocumentType(DocumentType.INVENTORY_VOUCHER);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// save Document Product Categories
		List<DocumentProductCategory> documentProductCategories = new ArrayList<>();
		List<ProductCategory> categorys = productCategoryRepository.findAllByCompanyIdAndDeactivated(true);
		documents.forEach(doc -> {
			categorys.forEach(cat -> {
				documentProductCategories.add(new DocumentProductCategory(doc, cat, company));
			});
			documentProductCategoryRepository.deleteByDocumentPid(doc.getPid());
			documentProductCategoryRepository.save(documentProductCategories);
		});
	}

	public void assighnDocumentProductGroup() {
		log.debug("request to save Document Product Group");
		List<Document> documents = documentRepository.findAllDocumentsByDocumentType(DocumentType.INVENTORY_VOUCHER);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// save Document Product Groups
		List<DocumentProductGroup> documentProductGroups = new ArrayList<>();
		List<ProductGroup> groups = productGroupRepository.findAllByCompanyIdAndDeactivatedProductGroup(true);
		documents.forEach(doc -> {
			groups.forEach(grp -> {
				documentProductGroups.add(new DocumentProductGroup(doc, grp, company, 0));
			});
			documentProductGroupRepository.deleteByDocumentPid(doc.getPid());
			documentProductGroupRepository.save(documentProductGroups);
		});
	}

	public void assighnStockLocationSource() {
		log.debug("request to save Document Stock Location Source");
		List<Document> documents = documentRepository.findAllDocumentsByDocumentType(DocumentType.INVENTORY_VOUCHER);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// save Document Stock Location Source
		List<DocumentStockLocationSource> documentStockLocationSources = new ArrayList<>();
		List<StockLocation> stockLocations = stockLocationRepository
				.findAllByCompanyIdAndDeactivatedStockLocation(true);
		documents.forEach(doc -> {
			stockLocations.forEach(stk -> {
				documentStockLocationSources.add(new DocumentStockLocationSource(doc, stk, company, false));
			});
			documentStockLocationSourceRepository.deleteByDocumentPid(doc.getPid());
			documentStockLocationSourceRepository.save(documentStockLocationSources);
		});
	}

	public void assighnStockLocationDestination() {
		log.debug("request to save Document Stock Location Destination");
		List<Document> documents = documentRepository.findAllDocumentsByDocumentType(DocumentType.INVENTORY_VOUCHER);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// save Document Stock Location Destination
		List<DocumentStockLocationDestination> documentStockLocationDestinations = new ArrayList<>();
		List<StockLocation> stockLocations = stockLocationRepository
				.findAllByCompanyIdAndDeactivatedStockLocation(true);
		documents.forEach(doc -> {
			stockLocations.forEach(stk -> {
				documentStockLocationDestinations.add(new DocumentStockLocationDestination(doc, stk, company, false));
			});
			stockLocationDestinationRepository.deleteByDocumentPid(doc.getPid());
			stockLocationDestinationRepository.save(documentStockLocationDestinations);

		});
	}

	public void assighnDocumentPriceLevels() {
		log.debug("request to save price levels");
		List<Document> documents = documentRepository.findAllDocumentsByDocumentType(DocumentType.INVENTORY_VOUCHER);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// save Document Stock Location Destination
		List<DocumentPriceLevel> documentPriceLevels = new ArrayList<>();
		List<PriceLevel> levels = priceLevelRepository.findAllByCompanyIdAndDeactivatedPriceLevel(true);
		documents.forEach(doc -> {
			levels.forEach(lvl -> {
				documentPriceLevels.add(new DocumentPriceLevel(doc, lvl, company));
			});
			documentPriceLevelRepository.deleteByDocumentPid(doc.getPid());
			documentPriceLevelRepository.save(documentPriceLevels);

		});

	}

	public void assignSaveUserProductGroups() {
		log.debug("request to save user productgroups");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Set<Authority> authorities = new HashSet<>(Arrays.asList(new Authority("ROLE_EXECUTIVE")));
		List<User> users = userRepository.findAllByCompanyIdAndAuthoritiesInAndActivated(company.getId(), authorities,
				true);
		// save User Product Groups
		List<UserProductGroup> userProductGroups = new ArrayList<>();
		List<ProductGroup> groups = productGroupRepository.findAllByCompanyIdAndDeactivatedProductGroup(true);
		users.forEach(usr -> {
			groups.forEach(grp -> {
				userProductGroups.add(new UserProductGroup(usr, grp, company));
			});
			userProductGroupRepository.deleteByUserPid(usr.getPid());
			userProductGroupRepository.save(userProductGroups);
		});
	}

	public void assignSaveUserProductCategories() {
		log.debug("request to save user Categories");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Set<Authority> authorities = new HashSet<>(Arrays.asList(new Authority("ROLE_EXECUTIVE")));
		List<User> users = userRepository.findAllByCompanyIdAndAuthoritiesInAndActivated(company.getId(), authorities,
				true);
		// save User Product Categories
		List<UserProductCategory> userProductCategories = new ArrayList<>();
		List<ProductCategory> categorys = productCategoryRepository.findAllByCompanyIdAndDeactivated(true);
		users.forEach(usr -> {
			categorys.forEach(cat -> {
				userProductCategories.add(new UserProductCategory(usr, cat, company));
			});
			userProductCategoryRepository.deleteByUserPid(usr.getPid());
			userProductCategoryRepository.save(userProductCategories);
		});
	}

	public void assignSaveUserStockLocations() {
		log.debug("request to save user StockLocation");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Set<Authority> authorities = new HashSet<>(Arrays.asList(new Authority("ROLE_EXECUTIVE")));
		List<User> users = userRepository.findAllByCompanyIdAndAuthoritiesInAndActivated(company.getId(), authorities,
				true);
		// save User Stock Location Destination
		List<UserStockLocation> userStockLocations = new ArrayList<>();
		List<StockLocation> stockLocations = stockLocationRepository
				.findAllByCompanyIdAndDeactivatedStockLocation(true);
		users.forEach(usr -> {
			stockLocations.forEach(stk -> {
				userStockLocations.add(new UserStockLocation(usr, stk, company));
			});
			userStockLocationRepository.deleteByUserPid(usr.getPid());
			userStockLocationRepository.save(userStockLocations);
		});
	}

	public void assignSaveUserPriceLevels() {
		log.debug("request to save user PriceLevel");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Set<Authority> authorities = new HashSet<>(Arrays.asList(new Authority("ROLE_EXECUTIVE")));
		List<User> users = userRepository.findAllByCompanyIdAndAuthoritiesInAndActivated(company.getId(), authorities,
				true);
		// save User Stock Location Destination
		List<UserPriceLevel> userPriceLevels = new ArrayList<>();
		List<PriceLevel> levels = priceLevelRepository.findAllByCompanyIdAndDeactivatedPriceLevel(true);
		users.forEach(usr -> {
			levels.forEach(lvl -> {
				userPriceLevels.add(new UserPriceLevel(usr, lvl, company));
			});
			userPriceLevelRepository.deleteByUserPid(usr.getPid());
			userPriceLevelRepository.save(userPriceLevels);
		});

	}

	public void assignSaveUserLocations() {
		log.debug("request to save user Locations");
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Set<Authority> authorities = new HashSet<>(Arrays.asList(new Authority("ROLE_EXECUTIVE")));
		List<User> users = userRepository.findAllByCompanyIdAndAuthoritiesInAndActivated(company.getId(), authorities,
				true);

		List<Long> toUserIds = users.stream().map(user -> user.getId()).collect(Collectors.toList());
		// save User Stock Location Destination
		List<EmployeeProfileLocation> employeeProfileLocations = new ArrayList<>();
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyIdNotInUserIds(true,
				toUserIds);

		List<Location> locations = locationRepository.findAllByCompanyIdAndLocationActivatedOrDeactivated(true);

		employeeProfiles.forEach(empl -> {
			locations.forEach(loc -> {
				employeeProfileLocations.add(new EmployeeProfileLocation(empl, loc));
			});
			employeeProfileLocationRepository.deleteByEmployeeProfilePid(empl.getPid());
			employeeProfileLocationRepository.save(employeeProfileLocations);
		});

	}

}
