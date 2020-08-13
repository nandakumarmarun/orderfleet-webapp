package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ActivityStage;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentAccountType;
import com.orderfleet.webapp.domain.DocumentAccountingVoucherColumn;
import com.orderfleet.webapp.domain.DocumentForms;
import com.orderfleet.webapp.domain.DocumentInventoryVoucherColumn;
import com.orderfleet.webapp.domain.FormElementType;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ReferenceDocument;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.VoucherNumberGenerator;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.ActivityStageRepository;
import com.orderfleet.webapp.repository.FormElementTypeRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ReferenceDocumentRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserDocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.repository.VoucherNumberGeneratorRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityGroupService;
import com.orderfleet.webapp.service.AttendanceStatusSubgroupService;
import com.orderfleet.webapp.service.CompetitorProfileService;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.service.DocumentAccountingVoucherColumnService;
import com.orderfleet.webapp.service.DocumentEcomProductGroupService;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.DocumentInventoryVoucherColumnService;
import com.orderfleet.webapp.service.DocumentPriceLevelService;
import com.orderfleet.webapp.service.DocumentPrintService;
import com.orderfleet.webapp.service.DocumentProductCategoryService;
import com.orderfleet.webapp.service.DocumentProductGroupService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DocumentStockLocationDestinationService;
import com.orderfleet.webapp.service.DocumentStockLocationSourceService;
import com.orderfleet.webapp.service.EcomProductGroupProductService;
import com.orderfleet.webapp.service.EcomProductProfileService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.FormElementService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.IncomeExpenseHeadService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.KnowledgebaseService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.service.PriceLevelAccountProductGroupService;
import com.orderfleet.webapp.service.PriceTrendConfigurationService;
import com.orderfleet.webapp.service.PriceTrendProductCompetitorService;
import com.orderfleet.webapp.service.PriceTrendProductGroupService;
import com.orderfleet.webapp.service.PriceTrendProductService;
import com.orderfleet.webapp.service.ProductGroupProductService;
import com.orderfleet.webapp.service.ProductProfileService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.RootPlanHeaderService;
import com.orderfleet.webapp.service.RootPlanSubgroupApproveService;
import com.orderfleet.webapp.service.SalesTargetGroupUserTargetService;
import com.orderfleet.webapp.service.StockDetailsService;
import com.orderfleet.webapp.service.SubFormElementService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.service.UserEcomProductGroupService;
import com.orderfleet.webapp.service.UserFavouriteDocumentService;
import com.orderfleet.webapp.service.UserKnowledgebaseFileService;
import com.orderfleet.webapp.service.UserMobileMenuItemGroupService;
import com.orderfleet.webapp.service.UserPriceLevelService;
import com.orderfleet.webapp.service.UserProductCategoryService;
import com.orderfleet.webapp.service.UserProductGroupService;
import com.orderfleet.webapp.service.UserReceiptTargetService;
import com.orderfleet.webapp.service.UserRestrictedAttendanceSubgroupService;
import com.orderfleet.webapp.service.UserStockLocationService;
import com.orderfleet.webapp.service.VehicleService;
import com.orderfleet.webapp.service.VoucherNumberGeneratorService;
import com.orderfleet.webapp.web.rest.StockLocationResource;
import com.orderfleet.webapp.web.rest.api.dto.DocumentAccountingVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.api.dto.DocumentInventoryVoucherColumnDTO;
import com.orderfleet.webapp.web.rest.api.dto.FormFormElementDTO;
import com.orderfleet.webapp.web.rest.api.dto.KnowledgeBaseFilesDTO;
import com.orderfleet.webapp.web.rest.api.dto.MBLocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.rest.api.dto.RootPlanHeaderUserTaskListDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityStageDTO;
import com.orderfleet.webapp.web.rest.dto.AttendanceStatusSubgroupDTO;
import com.orderfleet.webapp.web.rest.dto.CompetitorProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentAccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentEcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentFormDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementDTO;
import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMenuItemDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductCompetitorDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.dto.ReferenceDocumentDto;
import com.orderfleet.webapp.web.rest.dto.RootPlanSubgroupApproveDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.SubFormElementDTO;
import com.orderfleet.webapp.web.rest.dto.UserDocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserFavouriteDocumentDTO;
import com.orderfleet.webapp.web.rest.dto.UserReceiptTargetDTO;
import com.orderfleet.webapp.web.rest.dto.UserRestrictedAttendanceSubgroupDTO;
import com.orderfleet.webapp.web.rest.dto.VehicleDTO;
import com.orderfleet.webapp.web.rest.dto.VoucherNumberGeneratorDTO;
import com.orderfleet.webapp.web.rest.util.PaginationUtil;

import net.bytebuddy.description.annotation.AnnotationSource.Empty;

/**
 * REST controller for master data given to android device.
 *
 * @author Shaheer
 * @since June 29, 2016
 */
@RestController
@RequestMapping("/api")
public class MasterDataController {

	private final Logger log = LoggerFactory.getLogger(MasterDataController.class);

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private UserDocumentRepository userDocumentRepository;

	@Inject
	private FormElementTypeRepository formElementTypeRepository;

	@Inject
	private FormElementService formElementService;

	@Inject
	private DocumentFormsService documentFormsService;

	@Inject
	private UserProductGroupService userProductGroupService;

	@Inject
	private UserEcomProductGroupService userEcomProductGroupService;

	@Inject
	private UserProductCategoryService userProductCategoryService;

	@Inject
	private ProductProfileService productProfileService;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private FormService formsService;

	@Inject
	private DocumentInventoryVoucherColumnService documentInventoryVoucherColumnService;

	@Inject
	private DocumentAccountingVoucherColumnService documentAccountingVoucherColumnService;

	@Inject
	private DocumentAccountTypeService documentAccountTypeService;

	@Inject
	private DocumentProductGroupService documentProductGroupService;

	@Inject
	private DocumentEcomProductGroupService documentEcomProductGroupService;

	@Inject
	private DocumentProductCategoryService documentProductCategoryService;

	@Inject
	private DocumentPriceLevelService documentPriceLevelService;

	@Inject
	private KnowledgebaseService knowledgebaseService;

	@Inject
	private UserKnowledgebaseFileService userKnowledgebaseFileService;

	@Inject
	private ReceivablePayableService receivablePayableService;

	@Inject
	private ProductGroupProductService productGroupProductService;

	@Inject
	private EcomProductGroupProductService ecomProductGroupProductService;

	@Inject
	private UserPriceLevelService userPriceLevelService;

	@Inject
	private CompetitorProfileService competitorProfileService;

	@Inject
	private PriceTrendProductService priceTrendProductService;

	@Inject
	private PriceTrendProductGroupService priceTrendProductGroupService;

	@Inject
	private PriceTrendProductCompetitorService priceTrendProductCompetitorService;

	@Inject
	private EmployeeProfileLocationService employeeProfileLocationService;

	@Inject
	private UserReceiptTargetService userReceiptTargetService;

	@Inject
	private PriceTrendConfigurationService priceTrendConfigurationService;

	@Inject
	private SalesTargetGroupUserTargetService salesTargetGroupUserTargetService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private UserStockLocationService userStockLocationService;

	@Inject
	private DocumentStockLocationSourceService documentStockLocationSourceService;

	@Inject
	private DocumentStockLocationDestinationService documentStockLocationDestinationService;

	@Inject
	private UserFavouriteDocumentService userFavouriteDocumentService;

	@Inject
	private LocationService locationService;

	@Inject
	private EcomProductProfileService ecomProductProfileService;

	@Inject
	private UserMobileMenuItemGroupService userMobileMenuItemGroupService;

	@Inject
	private ActivityGroupService activityGroupService;

	@Inject
	private OpeningStockService openingStockService;

	@Inject
	private IncomeExpenseHeadService incomeExpenseHeadService;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private AttendanceStatusSubgroupService attendanceStatusSubgroupService;

	@Inject
	private DocumentPrintService documentPrintService;

	@Inject
	private RootPlanSubgroupApproveService rootPlanSubgroupApproveService;

	@Inject
	private RootPlanHeaderService rootPlanHeaderService;

	@Inject
	private UserRestrictedAttendanceSubgroupService userRestrictedAttendanceSubgroupService;

	@Inject
	private PriceLevelAccountProductGroupService priceLevelAccountProductGroupService;

	@Inject
	private UserDocumentService userDocumentService;

	@Inject
	private ActivityStageRepository activityStageRepository;

	@Inject
	private ReferenceDocumentRepository referenceDocumentRepository;

	@Inject
	private VehicleService vehicleService;

	@Inject
	private VoucherNumberGeneratorRepository voucherNumberGeneratorRepository;

	@Inject
	private VoucherNumberGeneratorService voucherNumberGeneratorSerice;

	@Inject
	private PostDatedVoucherService postDatedVoucherService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private StockDetailsService stockDetailsService;

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private SubFormElementService subFormElementService;

	@Inject
	private StockLocationRepository stockLocationRepository;

	/**
	 * GET /account-types : get all accountTypes.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all
	 *         accountTypes
	 * @throws URISyntaxException if the pagination headers couldn't be generated
	 */
	@GetMapping("/account-types")
	@Timed
	public ResponseEntity<List<AccountTypeDTO>> getAllAccountTypes(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate, Pageable pageable)
			throws URISyntaxException {
		Page<AccountTypeDTO> pageDTO;
		if (lastSyncdate == null) {
			pageDTO = accountTypeService.findAllCompanyAndAccountTypeActivated(pageable, true);
		} else {
			pageDTO = accountTypeService.findByLastModifiedAndActivatedTrue(lastSyncdate, pageable);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.headers(PaginationUtil.generatePaginationHttpHeaders(pageDTO, "/api/account-types"))
				.body(pageDTO.getContent());
	}

	/**
	 * GET /territories :get all the territories.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all
	 *         accountProfiles
	 * @throws URISyntaxException if the pagination headers couldn't be generated
	 */
	@Timed
	@GetMapping("/territories")
	public ResponseEntity<List<LocationDTO>> getAllTerritoriesByCompany(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate)
			throws URISyntaxException {
		log.debug("REST request to get all territories");
		List<LocationDTO> locationDTOs;
		if (lastSyncdate == null) {
			locationDTOs = locationService.findAllByUserAndLocationActivated(true);
		} else {
			locationDTOs = locationService.findAllByUserAndLocationActivatedLastModified(true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(locationDTOs);
	}

	/**
	 * GET /territories :get all the territories.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all
	 *         accountProfiles
	 * @throws URISyntaxException if the pagination headers couldn't be generated
	 */
	@Timed
	@GetMapping("/employee-location-hierarchies")
	public ResponseEntity<List<MBLocationHierarchyDTO>> getuserLocationHierarchies() throws URISyntaxException {
		log.debug("REST request to get all location-hierarchies");
		List<MBLocationHierarchyDTO> locationHierarchyDTOs = locationHierarchyService.findByUserAndActivatedTrue();
		return ResponseEntity.ok().body(locationHierarchyDTOs);
	}

	/**
	 * GET / : get all the EmployeeProfileLocations.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of LocationDTO
	 *         in body
	 */
	@GetMapping(value = "/employee-locations", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<LocationDTO>> getEmployeeLocations(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all EmployeeProfileLocations");
		List<LocationDTO> locationDTOs;
		if (lastSyncdate == null) {
			locationDTOs = employeeProfileLocationService.findLocationsByEmployeeProfileIsCurrentUser();
		} else {
			locationDTOs = employeeProfileLocationService
					.findLocationsByEmployeeProfileIsCurrentUserAndlastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(locationDTOs);
	}

	/**
	 * GET /location-account-profiles :get all the accountProfiles. This will give
	 * all account profile under employees locations.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all
	 *         accountProfiles
	 * @throws URISyntaxException if the pagination headers couldn't be generated
	 */
	@GetMapping("/location-account-profiles")
	@Timed
	public ResponseEntity<List<AccountProfileDTO>> getAllAccountProfilesByLocations(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate,
			@RequestParam int page, @RequestParam int size) throws URISyntaxException {
		Page<AccountProfileDTO> pageAccounts;
		if (lastSyncdate == null) {
			pageAccounts = locationAccountProfileService
					.findAccountProfilesByUserLocationsAndAccountProfileActivated(page, size);
		} else {
			pageAccounts = locationAccountProfileService
					.findAllByAccountProfileActivatedTrueAndLocationInAndLastModifiedDate(page, size, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.headers(PaginationUtil.generatePaginationHttpHeaders(pageAccounts, "/api/location-account-profiles"))
				.body(pageAccounts.getContent());
	}

	/**
	 * GET /location-account-profile-association :get location wise accountProfiles.
	 * This will give all location account profile under employees locations.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body all
	 *         locationAccountProfiles
	 * @throws URISyntaxException if the pagination headers couldn't be generated
	 */
	@GetMapping("/location-account-profile-association")
	@Timed
	public ResponseEntity<List<LocationAccountProfileDTO>> getAllLocationAccountProfilesByLocations(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate,
			@RequestParam int page, @RequestParam int size) throws URISyntaxException {
		int limit = size;
		int offset = page * size;
		List<LocationAccountProfileDTO> locationAccountProfiles;
		if (lastSyncdate == null) {
			locationAccountProfiles = locationAccountProfileService.findByUserLocationsAndAccountProfileActivated(limit,
					offset);
		} else {
			locationAccountProfiles = locationAccountProfileService
					.findByUserLocationsAndAccountProfileActivatedAndLastModified(limit, offset, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(locationAccountProfiles);
	}

	/**
	 * GET /activities : get all the activities.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of activities in
	 *         body
	 */
	@GetMapping(value = "/activities")
	@Timed
	public ResponseEntity<List<ActivityDTO>> getAllActivities(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all activities");
		List<ActivityDTO> activityDTOs;
		if (lastSyncdate == null) {
			activityDTOs = userActivityService.findByUserIsCurrentUserAndActivityActivated(true);
		} else {
			activityDTOs = userActivityService.findByUserIsCurrentUserAndActivityActivatedAndlastModifiedDate(true,
					lastSyncdate);
		}
		// set default stock location for documents
		for (ActivityDTO activityDTO : activityDTOs) {
			for (DocumentDTO documentDTO : activityDTO.getDocuments()) {
				List<UserDocumentDTO> userDocumentDTOs = userDocumentService
						.findByUserIsCurrentUserAndDocumentPid(documentDTO.getPid());
				if (!userDocumentDTOs.isEmpty()) {
					documentDTO.setImageOption(userDocumentDTOs.get(0).getImageOption());
				}
				StockLocationDTO source = documentStockLocationSourceService
						.findDefaultStockLocationByDocumentPid(documentDTO.getPid());
				if (source != null) {
					documentDTO.setSourceStockLocationPid(source.getPid());
					documentDTO.setSourceStockLocationName(source.getName());
				}
				StockLocationDTO destination = documentStockLocationDestinationService
						.findDefaultStockLocationByDocumentPid(documentDTO.getPid());
				if (destination != null) {
					documentDTO.setDestinationStockLocationPid(destination.getPid());
					documentDTO.setDestinationStockLocationName(destination.getName());
				}
			}
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(activityDTOs);
	}

	/**
	 * GET /activity-groups : get all the activityGroups.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityGroups in body
	 */
	@GetMapping(value = "/activity-groups", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityGroupDTO>> getAllActivityGroups(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all activityGroups");
		List<ActivityGroupDTO> activityGroupDTOs;
		if (lastSyncdate == null) {
			activityGroupDTOs = activityGroupService
					.findActivityGroupsByUserIsCurrentUserAndActivityGroupActivated(true);
		} else {
			activityGroupDTOs = activityGroupService
					.findActivityGroupsByUserIsCurrentUserAndActivityGroupActivatedAndLastModifiedDate(true,
							lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(activityGroupDTOs);
	}

	/**
	 * GET /product-group-products : get all the productGroupProducts.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of productGroups
	 *         in body
	 */
	@RequestMapping(value = "/ecom-product-group-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<EcomProductGroupProductDTO>> getAllProductGroupProducts(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all ProductGroupProducts");

		List<EcomProductGroupProductDTO> productGroupProductDTOs;
		if (lastSyncdate == null) {
			productGroupProductDTOs = ecomProductGroupProductService
					.findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(true);
		} else {
			productGroupProductDTOs = ecomProductGroupProductService
					.findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivatedAndModifiedDate(
							true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(productGroupProductDTOs);
	}

	/**
	 * GET /product-group-products : get all the productGroupProducts.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of productGroups
	 *         in body
	 */
	@RequestMapping(value = "/product-group-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductGroupProductDTO>> getAllEcomProductGroupProducts(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all ProductGroupProducts");

		List<ProductGroupProductDTO> productGroupProductDTOs;
		if (lastSyncdate == null) {
			productGroupProductDTOs = productGroupProductService
					.findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivated(true);
		} else {
			productGroupProductDTOs = productGroupProductService
					.findProductGroupProductsByUserProductGroupIsCurrentUserAndProductGroupActivatedAndModifiedDate(
							true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(productGroupProductDTOs);
	}

	/**
	 * GET /product-groups : get all the productGroups.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of productGroups
	 *         in body
	 */
	@GetMapping(value = "/product-groups", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductGroupDTO>> getAllProductGroups(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all productGroups");

		List<ProductGroupDTO> productGroupDTOs;
		if (lastSyncdate == null) {
			productGroupDTOs = userProductGroupService
					.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(true);
		} else {
			productGroupDTOs = userProductGroupService
					.findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(true,
							lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(productGroupDTOs);
	}

	/**
	 * GET /product-groups : get all the productGroups.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of productGroups
	 *         in body
	 */
	@GetMapping(value = "/ecom-product-groups", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<EcomProductGroupDTO>> getAllEcomProductGroups(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all EcomProductGroups");

		List<EcomProductGroupDTO> productGroupDTOs;
		if (lastSyncdate == null) {
			productGroupDTOs = userEcomProductGroupService
					.findProductGroupsByUserIsCurrentUserAndProductGroupsActivated(true);
		} else {
			productGroupDTOs = userEcomProductGroupService
					.findProductGroupsByUserIsCurrentUserAndProductGroupActivatedAndProductGroupLastModifiedDate(true,
							lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(productGroupDTOs);
	}

	/**
	 * GET /product-categories : get all the productCategories.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productCategories in body
	 */
	@GetMapping(value = "/product-categories", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductCategoryDTO>> getAllProductCategories(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all productCategories");
		List<ProductCategoryDTO> productCategoryDTOs;
		if (lastSyncdate == null) {
			productCategoryDTOs = userProductCategoryService
					.findProductCategoriesByUserIsCurrentUserAndProductCategoryActivated(true);
		} else {
			productCategoryDTOs = userProductCategoryService
					.findByUserIsCurrentUserAndProductCategoryActivatedAndLastModifiedDate(true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(productCategoryDTOs);
	}

	/**
	 * GET /product-profiles : get all the productProfiles.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productProfiles in body
	 */
	@RequestMapping(value = "/product-profiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProductProfileDTO>> getAllProductProfiles(@RequestParam int page, @RequestParam int size,
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all productProfiles");
		List<ProductProfileDTO> productProfileDTOs;
		if (lastSyncdate == null) {
			productProfileDTOs = productProfileService
					.findProductsByUserProductCategoriesIsCurrentUserAndActivated(page, size);
		} else {
			productProfileDTOs = productProfileService.findByProductCategoryInAndActivatedTrueAndLastModifiedDate(page,
					size, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(productProfileDTOs);
	}

	/**
	 * GET /price-levels: get all the price levels.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of price level
	 *         tags in body
	 */
	@RequestMapping(value = "/price-levels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceLevelDTO>> getAllPriceLevels(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all PriceLevels ");
		List<PriceLevelDTO> priceLevelDTOs;
		if (lastSyncdate == null) {
			priceLevelDTOs = userPriceLevelService.findPriceLevelsByUserIsCurrentUser();
		} else {
			priceLevelDTOs = userPriceLevelService.findPriceLevelsByUserIsCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(priceLevelDTOs);
	}

	/**
	 * GET /stock-locations : get all the stockLocations of the current user.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         stockLocations in body
	 */
	@GetMapping(value = "/stock-locations")
	@Timed
	public ResponseEntity<List<StockLocationDTO>> getAllStockLocations(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all StockLocations assigned to user");
		List<StockLocationDTO> stockLocationDTOs;
		if (lastSyncdate == null) {
			stockLocationDTOs = userStockLocationService
					.findStockLocationsByUserIsCurrentUserAndStockLocationActivated(true);
		} else {
			stockLocationDTOs = userStockLocationService
					.findStockLocationsByUserIsCurrentUserAndStockLocationActivatedAndLastModifiedDate(true,
							lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(stockLocationDTOs);
	}

	/**
	 * GET /document-source-stock-locations : get all the stockLocations of the
	 * document source.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         stockLocations in body
	 */
	@GetMapping(value = "/document-source-stock-locations")
	@Timed
	public ResponseEntity<List<DocumentStockLocationDTO>> getDocumentSourceStockLocations(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Document source StockLocations");
		List<DocumentStockLocationDTO> documentStockLocationDTOs;
		if (lastSyncdate == null) {
			documentStockLocationDTOs = documentStockLocationSourceService.findAllByCompany();
		} else {
			documentStockLocationDTOs = documentStockLocationSourceService
					.findAllByCompanyIdAndlastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(documentStockLocationDTOs);
	}

	/**
	 * GET /document-destination-stock-locations : get all the stockLocations of the
	 * document destination.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         stockLocations in body
	 */
	@GetMapping(value = "/document-destination-stock-locations")
	@Timed
	public ResponseEntity<List<DocumentStockLocationDTO>> getDocumentDestinationStockLocations(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Document destination StockLocations");
		List<DocumentStockLocationDTO> documentStockLocationDTOs;
		if (lastSyncdate == null) {
			documentStockLocationDTOs = documentStockLocationDestinationService.findAllByCompany();
		} else {
			documentStockLocationDTOs = documentStockLocationDestinationService
					.findAllByCompanyAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(documentStockLocationDTOs);
	}

	/**
	 * GET /documents : get all the documents.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of documents in
	 *         body
	 */
	@GetMapping(value = "/documents", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentDTO>> getAllDocuments(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all documents");
		List<Document> documents;
		if (lastSyncdate == null) {
			documents = userDocumentRepository.findDocumentsByUserIsCurrentUser();
		} else {
			documents = userDocumentRepository.findDocumentsByUserIsCurrentUserAndLlastModifiedDate(lastSyncdate);
		}
		List<DocumentDTO> documentDTOs = documents.stream().map(DocumentDTO::new).collect(Collectors.toList());
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(documentDTOs);
	}

	/**
	 * GET /favourite-documents : get user favourite documents.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of favourite
	 *         documents in body
	 */
	@RequestMapping(value = "/favourite-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UserFavouriteDocumentDTO>> getAllFavouriteDocuments(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all favourite documents");
		List<UserFavouriteDocumentDTO> userFavouriteDocumentDTOs;
		if (lastSyncdate == null) {
			userFavouriteDocumentDTOs = userFavouriteDocumentService.findFavouriteDocumentsByUserIsCurrentUser();
		} else {
			userFavouriteDocumentDTOs = userFavouriteDocumentService
					.findFavouriteDocumentsByUserIsCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(userFavouriteDocumentDTOs);
	}

	/**
	 * GET /document-product-groups : get all the document product groups.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of document
	 *         product groups in body
	 */
	@GetMapping(value = "/document-product-groups", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentProductGroupDTO>> getAllDocumentProductGroups(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all document product groups");
		List<DocumentProductGroupDTO> documentProductGroupDTOs;
		if (lastSyncdate == null) {
			documentProductGroupDTOs = documentProductGroupService.findByUserDocumentIsCurrentUser();
		} else {
			documentProductGroupDTOs = documentProductGroupService
					.findByUserDocumentIsCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(documentProductGroupDTOs);
	}

	@GetMapping(value = "/document-ecom-product-groups", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentEcomProductGroupDTO>> getAllDocumentEcomProductGroups(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all document ecom product groups");
		List<DocumentEcomProductGroupDTO> documentProductGroupDTOs;
		if (lastSyncdate == null) {
			documentProductGroupDTOs = documentEcomProductGroupService.findByUserDocumentIsCurrentUser();
		} else {
			documentProductGroupDTOs = documentEcomProductGroupService
					.findByUserDocumentIsCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(documentProductGroupDTOs);
	}

	/**
	 * GET /document-product-categories : get all the document product categories.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of document
	 *         product categories in body
	 */
	@GetMapping(value = "/document-product-categories", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentProductCategoryDTO>> getAllDocumentProductCategories(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all document product categories");
		List<DocumentProductCategoryDTO> documentProductCategoryDTOs;
		if (lastSyncdate == null) {
			documentProductCategoryDTOs = documentProductCategoryService.findByUserDocumentIsCurrentUser();
		} else {
			documentProductCategoryDTOs = documentProductCategoryService
					.findByUserDocumentIsCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(documentProductCategoryDTOs);

	}

	/**
	 * GET /form-elements-type : get all the formElementsType.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         formElementsType in body
	 */
	@GetMapping(value = "/form-elements-type", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementType>> getAllFormElementsType(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Form Element Types");
		List<FormElementType> formElementTypes;
		if (lastSyncdate == null) {
			formElementTypes = formElementTypeRepository.findAll();
		} else {
			formElementTypes = formElementTypeRepository.findAll();
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(formElementTypes);
	}

	/**
	 * GET /form-elements : get all the formElements.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of formElements
	 *         in body
	 */
	@GetMapping(value = "/form-elements", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormElementDTO>> getAllFormElements(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Form Elements");
		List<FormElementDTO> formElementDTOs;
		if (lastSyncdate == null) {
			formElementDTOs = formElementService.findUsersFormElement();
		} else {
			formElementDTOs = formElementService.findUsersFormElement();
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(formElementDTOs);
	}

	/**
	 * GET /form-elements : get all the formElements.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of formElements
	 *         in body
	 */
	@GetMapping(value = "/sub-form-elements", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<SubFormElementDTO>> getAllSubFormElements(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Form Elements");
		List<SubFormElementDTO> subFormElementDTOs;
		if (lastSyncdate == null) {
			subFormElementDTOs = subFormElementService.findAllSubFormElementByCompany();
		} else {
			subFormElementDTOs = subFormElementService.findAllSubFormElementByCompany();
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(subFormElementDTOs);
	}

	/**
	 * GET /forms : get all the forms.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of forms in body
	 */
	@GetMapping(value = "/forms", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormDTO>> getAllForms(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Form");
		List<FormDTO> formDTOs;
		if (lastSyncdate == null) {
			formDTOs = formsService.findAllByCompany();
		} else {
			formDTOs = formsService.findAllByCompanyIdAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(formDTOs);
	}

	/**
	 * GET /forms-form-elements : get all the FormFormElements.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         FormFormElements in body
	 */
	@GetMapping(value = "/forms-form-elements", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<FormFormElementDTO>> getAllFormFormElements(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Form Form Elements");
		List<FormFormElementDTO> formElementDTOs;
		if (lastSyncdate == null) {
			formElementDTOs = formFormElementService.findAllByCompany();
		} else {
			formElementDTOs = formFormElementService.findAllByCompanyIdAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(formElementDTOs);
	}

	/**
	 * GET /document-forms : get all the documentForm.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of DocumentForm
	 *         in body
	 */

	@GetMapping(value = "/document-forms", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentFormDTO>> getAllDocumentForms(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all DocumentForm");
		List<DocumentForms> documentForms;
		if (lastSyncdate == null) {
			documentForms = documentFormsService.findAllByCompany();
		} else {
			documentForms = documentFormsService.findAllByCompanyIdAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(documentForms.stream().map(DocumentFormDTO::new).collect(Collectors.toList()));
	}

	/**
	 * GET /document-inventory-voucher-columns : get all the Document Inventory
	 * Voucher Column.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         DocumentInventoryVoucherColumns in body
	 */
	@GetMapping(value = "/document-inventory-voucher-columns", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentInventoryVoucherColumnDTO>> getDocumentInventoryVoucherColumns(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all DocumentInventoryVoucherColumns");
		List<DocumentInventoryVoucherColumn> documentInventoryVoucherColumns;
		if (lastSyncdate == null) {
			documentInventoryVoucherColumns = documentInventoryVoucherColumnService.findByCompanyId();
		} else {
			documentInventoryVoucherColumns = documentInventoryVoucherColumnService
					.findAllByCompanyIdAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(documentInventoryVoucherColumns.stream().map(DocumentInventoryVoucherColumnDTO::new)
						.collect(Collectors.toList()));

	}

	/**
	 * GET /document-accounting-voucher-columns : get all the Document accounting
	 * Voucher Column.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of Document
	 *         accounting VoucherColumns in body
	 */
	@GetMapping(value = "/document-accounting-voucher-columns", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentAccountingVoucherColumnDTO>> getDocumentAccountingVoucherColumns(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all DocumentAccountingVoucherColumns");
		List<DocumentAccountingVoucherColumn> documentAccountingVoucherColumns;
		if (lastSyncdate == null) {
			documentAccountingVoucherColumns = documentAccountingVoucherColumnService.findByCompanyId();
		} else {
			documentAccountingVoucherColumns = documentAccountingVoucherColumnService
					.findByCompanyIdAndLastModifiedDate(lastSyncdate);
		}
		if (documentAccountingVoucherColumns == null) {
			ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(Collections.emptyList());
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(documentAccountingVoucherColumns.stream().map(DocumentAccountingVoucherColumnDTO::new)
						.collect(Collectors.toList()));
	}

	/**
	 * GET /document-account-types : get all the Document Account Types.
	 *
	 * @return list of DocumentAccountType in body
	 */
	@GetMapping(value = "/document-account-types", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentAccountTypeDTO>> getDocumentAccountTypes(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Document Account Types");
		List<DocumentAccountType> documentAccountTypes;
		if (lastSyncdate == null) {
			documentAccountTypes = documentAccountTypeService.findAllByCompany();
		} else {
			documentAccountTypes = documentAccountTypeService.findAllByCompanyLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(documentAccountTypes.stream().map(DocumentAccountTypeDTO::new).collect(Collectors.toList()));
	}

	/**
	 * GET /document-price-levels : get all the DocumentPriceLevel.
	 *
	 * @return list of DocumentPriceLevel
	 */
	@GetMapping(value = "/document-price-levels", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DocumentPriceLevelDTO>> getDocumentPriceLevels(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all Document Price levels");
		List<DocumentPriceLevelDTO> documentPriceLevelDTOs;
		if (lastSyncdate == null) {
			documentPriceLevelDTOs = documentPriceLevelService.findByUserDocumentsIsCurrentUser();
		} else {
			documentPriceLevelDTOs = documentPriceLevelService
					.findByUserDocumentsIsCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(documentPriceLevelDTOs);

	}

	/**
	 * GET /knowledgebase : get all the knowledgebase tags.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of knowledgebase
	 *         tags in body
	 */
	@GetMapping(value = "/knowledgebase", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<KnowledgebaseDTO>> getAllKnowledgebase(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all knowledgebase ");
		List<KnowledgebaseDTO> knowledgebaseDTOs;
		if (lastSyncdate == null) {
			knowledgebaseDTOs = knowledgebaseService.findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivated(true);
		} else {
			knowledgebaseDTOs = knowledgebaseService
					.findAllByCompanyIdAndKnowledgebaseActivatedOrDeactivatedAndLastModifiedDate(true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(knowledgebaseDTOs);
	}

	/**
	 * GET /knowledgebase-files: get all the knowledgebaseFiles tags.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         knowledgebaseFiles tags in body
	 */
	@RequestMapping(value = "/knowledgebase-files", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<KnowledgeBaseFilesDTO> getAllKnowledgebaseFiles() {
		log.debug("REST request to get all knowledgebaseFiles ");
		return userKnowledgebaseFileService.findAllByUserIsCurrentUser();
	}

	/**
	 * GET /receivable-payable: get all the ReceivablePayable tags.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         ReceivablePayables tags in body
	 */
	@RequestMapping(value = "/receivable-payable", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ReceivablePayableDTO>> getAllReceivablePayables(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all ReceivablePayables ");

		List<ReceivablePayableDTO> receivablePayableDTOs;
		if (lastSyncdate == null) {
			// receivablePayables by current userLocation
			receivablePayableDTOs = receivablePayableService.findAllByCompanyAndAccountProfileIn();
		} else {
			receivablePayableDTOs = receivablePayableService
					.findAllByCompanyAndlastModifiedDateAndAccountProfileIn(lastSyncdate);
		}

		List<PostDatedVoucherDTO> postDatedVoucherDTOs = new ArrayList<>();
		postDatedVoucherDTOs = postDatedVoucherService.findAllPostDatedVouchers();
		List<ReceivablePayableDTO> result = new ArrayList<>();
		for (ReceivablePayableDTO rpDTO : receivablePayableDTOs) {
			if (rpDTO.getReceivablePayableType().equals(ReceivablePayableType.Payable)) {

				double refDocAmt = Double.valueOf(rpDTO.getReferenceDocumentAmount()) * -1;
				double refDocBalAmt = Double.valueOf(rpDTO.getReferenceDocumentBalanceAmount()) * -1;

				rpDTO.setReferenceDocumentAmount(refDocAmt);
				rpDTO.setReferenceDocumentBalanceAmount(refDocBalAmt);

				result.add(rpDTO);
			} else {
				if (postDatedVoucherDTOs != null && postDatedVoucherDTOs.size() != 0) {
					List<PostDatedVoucherDTO> postDatedVouchers = postDatedVoucherDTOs.stream()
							.filter(pdc -> pdc.getReferenceVoucher().equals(rpDTO.getReferenceDocumentNumber()))
							.collect(Collectors.toList());
					if (postDatedVouchers != null && postDatedVouchers.size() != 0) {
						double finalAmount = rpDTO.getReferenceDocumentBalanceAmount();
						for (PostDatedVoucherDTO dto : postDatedVouchers) {
							finalAmount = finalAmount - dto.getReferenceDocumentAmount();
						}
						rpDTO.setReferenceDocumentFinalBalanceAmount(finalAmount);
					} else {
						rpDTO.setReferenceDocumentFinalBalanceAmount(rpDTO.getReferenceDocumentBalanceAmount());
					}
				} else {
					rpDTO.setReferenceDocumentFinalBalanceAmount(rpDTO.getReferenceDocumentBalanceAmount());
				}
				result.add(rpDTO);
			}
		}

		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(result);

	}

	/**
	 * GET /competitor-profiles : get all the CompetitorProfile.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         CompetitorProfile in body
	 */
	@GetMapping(value = "/competitor-profiles", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<CompetitorProfileDTO>> getAllCompetitorProfiles(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all CompetitorProfiles ");
		List<CompetitorProfileDTO> competitorProfileDTOs;
		if (lastSyncdate == null) {
			competitorProfileDTOs = competitorProfileService
					.findAllByCompanyIdAndCompetitorProfileActivatedOrDeactivated(true);
		} else {
			competitorProfileDTOs = competitorProfileService
					.findAllByCompanyIdAndCompetitorProfileActivatedAndLastModifiedDate(true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(competitorProfileDTOs);
	}

	/**
	 * GET /price-trend-products : get all the PriceTrendProduct.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         PriceTrendProduct in body
	 */
	@RequestMapping(value = "/price-trend-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceTrendProductDTO>> getAllPriceTrendProducts(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all PriceTrendProducts ");
		List<PriceTrendProductDTO> priceTrendProductDTOs;
		if (lastSyncdate == null) {
			priceTrendProductDTOs = priceTrendProductService.findAllByCompanyIdAndPriceTrendProductActivated(true);
		} else {
			priceTrendProductDTOs = priceTrendProductService
					.findAllByCompanyIdAndPriceTrendProductActivatedAndLastModifiedDate(true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(priceTrendProductDTOs);
	}

	/**
	 * GET /price-trend-product-groups : get all the PriceTrendProductGroup.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         PriceTrendProductGroup in body
	 */
	@RequestMapping(value = "/price-trend-product-groups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceTrendProductGroupDTO>> getAllPriceTrendProductGroups(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all PriceTrendProductGroups ");
		List<PriceTrendProductGroupDTO> priceTrendProductGroupDTOs;
		if (lastSyncdate == null) {
			priceTrendProductGroupDTOs = priceTrendProductGroupService
					.findAllByCompanyIdAndPriceTrendProductGroupActivated(true);
		} else {
			priceTrendProductGroupDTOs = priceTrendProductGroupService
					.findAllByCompanyIdAndPriceTrendProductGroupActivatedAndlastModifiedDate(true, lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(priceTrendProductGroupDTOs);
	}

	/**
	 * GET /price-trend-configuration : get all the PriceTrendConfiguration.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         PriceTrendConfiguration in body
	 */
	@GetMapping(value = "/price-trend-configuration", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceTrendConfigurationDTO>> getPriceTrendConfiguration(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all PriceTrendConfiguration ");
		List<PriceTrendConfigurationDTO> priceTrendConfigurationDTOs;
		if (lastSyncdate == null) {
			priceTrendConfigurationDTOs = priceTrendConfigurationService.findAllByCompany();
		} else {
			priceTrendConfigurationDTOs = priceTrendConfigurationService
					.findAllByCompanyAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(priceTrendConfigurationDTOs);
	}

	/**
	 * GET /price-trend-product-competitors : get all the
	 * PriceTrendProductCompetitor.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         PriceTrendProductCompetitor in body
	 */
	@GetMapping(value = "/price-trend-product-competitors", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<PriceTrendProductCompetitorDTO>> getAllPriceTrendProductCompetitors(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all PriceTrendProductCompetitors ");

		List<PriceTrendProductCompetitorDTO> priceTrendProductCompetitorDTOs;
		if (lastSyncdate == null) {
			priceTrendProductCompetitorDTOs = priceTrendProductCompetitorService.findAllByCompany();
		} else {
			priceTrendProductCompetitorDTOs = priceTrendProductCompetitorService
					.findAllByCompanyAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified())
				.body(priceTrendProductCompetitorDTOs);
	}

	/**
	 * GET /user-receipt-target : get all the UserReceiptTarget.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         UserReceiptTarget in body
	 */
	@RequestMapping(value = "/user-receipt-target", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public UserReceiptTargetDTO getUserReceiptTarget() {
		log.debug("REST request to get User Receipt Target");
		return userReceiptTargetService.findOneByCurrentUserAndDate(LocalDate.now());
	}

	/**
	 * GET /sales-target-group-user-target : get all the SalesTargetGroupUserTarget.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         SalesTargetGroupUserTarget in body
	 */
	@RequestMapping(value = "/sales-target-group-user-target", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<SalesTargetGroupUserTargetDTO> getSalesTargetGroupUserTarget() {
		log.debug("REST request to get User Receipt Target");
		return salesTargetGroupUserTargetService.findByCurrentUserAndCurrentMonth();
	}

	/**
	 * GET /opening-stock : get all the openingStock by product.
	 *
	 * @param productPid the productPid of the product profile of openingStocks to
	 *                   get
	 * @return the ResponseEntity with status 200 (OK) and the list of openingStocks
	 *         in body
	 */
	@GetMapping(value = "/opening-stock", params = { "productPid" })
	@Timed
	public ResponseEntity<List<OpeningStockDTO>> getOpeningStocksByProduct(
			@RequestParam(value = "productPid") String productPid,
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get openingStock by product");
		List<OpeningStockDTO> openingStockDTOs;
		if (lastSyncdate == null) {
			openingStockDTOs = openingStockService
					.findByCompanyIdAndProductProfilePidAndOpeningStockActivated(productPid, true);
		} else {
			openingStockDTOs = openingStockService
					.findByCompanyIdAndProductProfilePidAndOpeningStockActivatedAndLastModifiedDate(productPid, true,
							lastSyncdate);
		}
		// List<OpeningStockDTO> result = new ArrayList<>();
		// for (OpeningStockDTO openingStockDTO : openingStockDTOs) {
		// if (openingStockDTO.getBatchNumber() != null &&
		// !openingStockDTO.getBatchNumber().equalsIgnoreCase("")) {
		// result.add(openingStockDTO);
		// }
		// }
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(openingStockDTOs);
	}

	/**
	 * GET /ecom-product-profile : get all the ecomProductProfile. This will give
	 * ecom-product with list of product profile
	 *
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroupInfoSections in body
	 */
	@GetMapping("/ecom-product-profile")
	@Timed
	public ResponseEntity<List<EcomProductProfileDTO>> getEcomProducs(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get user EcomProductProfiles");
		List<EcomProductProfileDTO> ecomProductProfileDTOs;
		if (lastSyncdate == null) {
			ecomProductProfileDTOs = ecomProductProfileService.findByCurrentUser();
		} else {
			ecomProductProfileDTOs = ecomProductProfileService.findByCurrentUserAndLastModifiedDate(lastSyncdate);
		}
		return ResponseEntity.ok().header("Last-Sync-Date", getResourceLastModified()).body(ecomProductProfileDTOs);

	}

	/**
	 * GET /ecom-product-profile : get all the ecomProductProfile. This will give
	 * ecom-product with list of product profile
	 *
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         productGroupInfoSections in body
	 */

	@Timed
	@Transactional(readOnly = true)
	@GetMapping("/mobile-menu-items")
	public ResponseEntity<List<MobileMenuItemDTO>> getMobileMenuItems() {
		log.debug("REST request to get user mobile menu items");
		return new ResponseEntity<>(userMobileMenuItemGroupService.findCurrentUserMenuItems(), HttpStatus.OK);
	}

	@Timed
	@GetMapping("/income-expense-head")
	public ResponseEntity<List<IncomeExpenseHeadDTO>> getIncomeExpenseHeads() {
		log.debug("REST request to get user Income Expense Heads");
		return new ResponseEntity<>(incomeExpenseHeadService.findAllByCompany(), HttpStatus.OK);
	}

	private String getResourceLastModified() {
		return LocalDateTime.now().toString();
	}

	@Timed
	@GetMapping("/attendance-status-subgroup")
	public ResponseEntity<List<AttendanceStatusSubgroupDTO>> getAttendanceStatusSubgroups() {
		log.debug("REST request to get user Attendance Status Subgroup");
		return new ResponseEntity<>(attendanceStatusSubgroupService.findAllByCompany(), HttpStatus.OK);
	}

	@Timed
	@GetMapping("/document-print")
	public ResponseEntity<List<DocumentPrintDTO>> getDocumentPrint() {
		log.debug("REST request to get user document print");
		return new ResponseEntity<>(documentPrintService.findAllByUserLogin(), HttpStatus.OK);
	}

	@Timed
	@GetMapping("/rootplan-attendance-subgroup-approvals")
	public ResponseEntity<List<RootPlanSubgroupApproveDTO>> getRootPlanAttendanceSubgroupApprovals() {
		log.debug("REST request to get root plan attendance Subgroup");
		List<RootPlanSubgroupApproveDTO> rootPlanSubgroupApproveDTOs = rootPlanSubgroupApproveService
				.findAllByUserLogin();
		List<UserRestrictedAttendanceSubgroupDTO> userRestrictedAttendanceSubgroupDTOs = userRestrictedAttendanceSubgroupService
				.findAllByUserLogin();
		List<RootPlanSubgroupApproveDTO> result = new ArrayList<>();
		for (RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO : rootPlanSubgroupApproveDTOs) {
			int i = 0;
			for (UserRestrictedAttendanceSubgroupDTO userRestrictedAttendanceSubgroupDTO : userRestrictedAttendanceSubgroupDTOs) {
				if (rootPlanSubgroupApproveDTO.getAttendanceStatusSubgroupId() == userRestrictedAttendanceSubgroupDTO
						.getAttendanceSubgroupId()) {
					i = 1;
					break;
				}
			}
			if (i == 0) {
				result.add(rootPlanSubgroupApproveDTO);
			}
		}
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Timed
	@GetMapping("/root-plan-header")
	public ResponseEntity<List<RootPlanHeaderUserTaskListDTO>> getRootPlanHeader() {
		log.debug("REST request to get root plan attendance Subgroup");
		return new ResponseEntity<>(rootPlanHeaderService.findAllDetailsByUserLogin(), HttpStatus.OK);
	}

	@Timed
	@GetMapping("/price-levels/account-productgroups")
	public ResponseEntity<List<PriceLevelDTO>> getPriceLevelAccountProductGroup() {
		log.debug("REST request to get price Level Account ProductGroup");
		return new ResponseEntity<>(priceLevelAccountProductGroupService.findAllByUserLogin(), HttpStatus.OK);
	}

	@RequestMapping(value = "/activity-stage", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ActivityStageDTO>> getActivityStageByCompany() {
		log.debug("REST request to get all activity stage ");
		List<ActivityStage> activityStageList = activityStageRepository.findAllByCompanyId();

		List<ActivityStageDTO> activityStageDtoList = activityStageList.stream()
				.map(activity -> new ActivityStageDTO(activity)).collect(Collectors.toList());

		return new ResponseEntity<>(activityStageDtoList, HttpStatus.OK);
	}

	@RequestMapping(value = "/document-reference-documents", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ReferenceDocumentDto>> getDocumentAndReferenceDocument() {
		log.debug("REST request to get all reference document-reference document mapping");
		List<ReferenceDocument> docRefDocuments = referenceDocumentRepository
				.findAllByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		List<ReferenceDocumentDto> referenceDocumentDtos = docRefDocuments.stream()
				.map(refdoc -> new ReferenceDocumentDto(refdoc)).collect(Collectors.toList());
		return new ResponseEntity<>(referenceDocumentDtos, HttpStatus.OK);
	}

	@GetMapping(value = "/vehicles")
	@Timed
	public ResponseEntity<List<VehicleDTO>> getVehicles(
			@RequestHeader(required = false, value = "Last-Sync-Date") LocalDateTime lastSyncdate) {
		log.debug("REST request to get all vehichle and its StockLocations");
		List<VehicleDTO> vehicleStockLocationDTOs = vehicleService.findAllByCompany();
		return new ResponseEntity<>(vehicleStockLocationDTOs, HttpStatus.OK);
	}

	@GetMapping(value = "/document-voucher-numbers")
	public ResponseEntity<List<VoucherNumberGeneratorDTO>> getDocumentVoucherNumbers(@RequestParam String userPid,
			@RequestParam String companyPid) {
		log.debug("REST request to get all document voucher numbers");
		List<VoucherNumberGenerator> voucherNumberGeneratorList = voucherNumberGeneratorRepository
				.findAllByUserAndCompany(userPid, companyPid);
		if (voucherNumberGeneratorList == null || voucherNumberGeneratorList.size() == 0) {
			return new ResponseEntity<>(new ArrayList<VoucherNumberGeneratorDTO>(), HttpStatus.OK);
		}
		List<String> documentPids = voucherNumberGeneratorList.stream().map(vng -> vng.getDocument().getPid())
				.collect(Collectors.toList());
		log.info("Call to get the last document number...................");
		List<Object[]> objectArray = inventoryVoucherHeaderRepository.getLastNumberForEachDocument(companyPid, userPid,
				documentPids);
		log.info("Company Pid" + companyPid + "\n User Pid : " + userPid + "\n Document Pids : " + documentPids);
		List<VoucherNumberGeneratorDTO> voucherNumberGeneratorDTOs = new ArrayList<>();
		// document entry exist in inventory voucher entries
		boolean documentExit = false;
		for (VoucherNumberGenerator vng : voucherNumberGeneratorList) {
			documentExit = false;
			for (Object[] obj : objectArray) {
				log.info(obj[1].toString() + "---" + vng.getDocument().getPid());
				if (vng.getDocument().getPid().equals(obj[1])) {

					voucherNumberGeneratorDTOs.add(new VoucherNumberGeneratorDTO(vng.getId(), vng.getUser().getPid(),
							vng.getUser().getFirstName(), vng.getDocument().getPid(), vng.getDocument().getName(),
							vng.getPrefix(), vng.getStartwith(), obj[0].toString()));
					documentExit = true;
				}
			}
			if (!documentExit) {
				voucherNumberGeneratorDTOs.add(new VoucherNumberGeneratorDTO(vng.getId(), vng.getUser().getPid(),
						vng.getUser().getFirstName(), vng.getDocument().getPid(), vng.getDocument().getName(),
						vng.getPrefix(), vng.getStartwith(), vng.getPrefix() + "0"));
			}

		}

		return new ResponseEntity<>(voucherNumberGeneratorDTOs, HttpStatus.OK);
//		 String str = "abcdDCBA123";
//		 String strNew = str.replace("a", "");
	}

	@RequestMapping(value = "/stockDetails", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StockDetailsDTO>> filter(@RequestParam("userPid") String userPid,
			@RequestParam(name="stockLocationPid",required = false) String stockLocationPid) {
		log.debug("API request to fetch users Stock Details (Van sales)");

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<User> user = userRepository.findOneByPid(userPid);
		long userId = user.get().getId();
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		List<UserStockLocation> userStockLocations = new ArrayList<>();
		boolean stockLocationSelected = false;
		if(stockLocationPid != null && !stockLocationPid.isEmpty()){
			userStockLocations = userStockLocationRepository.findByUserPidAndStockLocationPid(user.get().getPid(), stockLocationPid);
			log.info("stock details based on stocklocation and user"+userStockLocations.size());
			stockLocationSelected = true;
		}else{
			userStockLocations = userStockLocationRepository.findByUserPid(user.get().getPid());
			log.info("stock details based on  user");
		}
		
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation())
				.collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository
				.findByStockLocationInOrderByCreatedDateAsc(new ArrayList<>(usersStockLocations));

		if (openingStockUserBased.size() != 0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			// LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate ,usersStockLocations);
			log.info("stockdetails check 1 :-"+stockDetails.size());
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get(),usersStockLocations, stockLocationSelected);
			log.info("unsaled stockdetails check 2 :-"+unSaled.size());

			for (StockDetailsDTO dto : stockDetails) {
				
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
			}
			stockDetails.addAll(unSaled);
		}
		log.info(stockDetails.size()+" size of stockdetails");
		stockDetails
				.sort((StockDetailsDTO s1, StockDetailsDTO s2) -> s1.getProductName().compareTo(s2.getProductName()));
		return new ResponseEntity<>(stockDetails, HttpStatus.OK);
	}

	@RequestMapping(value = "/stock-location-products", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getProductPidsByStockLocation(
			@RequestParam(value = "stockLocationPid") String stockLocationPid) {

		log.debug("REST request to get products by stockLocations");
		List<String> producProfilePids = new ArrayList<>();
		Optional<StockLocation> opStockLocation = stockLocationRepository.findOneByPid(stockLocationPid);
		if (opStockLocation.isPresent()) {
			List<OpeningStock> openingStocks = openingStockRepository.findByStockLocation(opStockLocation.get());
			if (openingStocks != null && openingStocks.size() > 0) {
				for (OpeningStock os : openingStocks) {
					producProfilePids.add(os.getProductProfile().getPid());
				}
			}
		}
		if (producProfilePids.size() > 0) {
			producProfilePids = producProfilePids.stream().distinct().collect(Collectors.toList());
		}
		return new ResponseEntity<>(producProfilePids, HttpStatus.OK);
	}

}