package com.orderfleet.webapp.web.vendor.excel.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EcomProductProfileProductDTO;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupEcomProductDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductProfileCustomDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.vendor.excel.service.ProductProfileUploadService;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class ProductProfileExcel {

	private final Logger log = LoggerFactory.getLogger(ProductProfileExcel.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	// @Inject
	// private ProductProfileUploadService productProfileUploadService;

	@Inject
	private ProductProfileUploadService productProfileUploadService;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;

	@RequestMapping(value = "/product-categories.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductCategories(
			@Valid @RequestBody List<ProductCategoryDTO> productCategoryDTOs) {
		log.debug("REST request to save ProductCategories : {}", productCategoryDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTCATEGORY).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductCategories(productCategoryDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserProductCategories();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}
					if (so.getDocument()) {
						documentUserWiseUpdateController.assighnDocumentProductCategory();
					}
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-Category sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-group.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroups(@Valid @RequestBody List<ProductGroupDTO> productGroupDTOs) {
		log.debug("REST request to save ProductGroups : {}", productGroupDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTGROUP).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductGroups(productGroupDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserProductGroups();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}
					if (so.getDocument()) {
						documentUserWiseUpdateController.assighnDocumentProductGroup();
					}

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-Group sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-profiles.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfiles(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) {
		
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTPROFILE).map(so -> {
					// update sync status
					
					log.debug("----------------------------------------", productProfileDTOs.size());
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductGroupProductExcel(productProfileDTOs, so);
					productProfileUploadService.saveUpdateProductProfiles(productProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
		
	}
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		
//		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
//		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
//				SyncOperationType.PRODUCTPROFILE).map(so -> {
//
//					Optional<SyncOperation> opSyncCG = syncOperationRepository.findOneByCompanyIdAndOperationType(
//							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTCATEGORY);
//
//					if (opSyncCG.isPresent() && !opSyncCG.get().getCompleted()) {
//						return new ResponseEntity<>("product-category save processing try after some time.",
//								HttpStatus.BAD_REQUEST);
//					}

//					Optional<SyncOperation> opSyncPG = syncOperationRepository.findOneByCompanyIdAndOperationType(
//							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTGROUP);
//
//					if (opSyncPG.isPresent() && !opSyncPG.get().getCompleted()) {
//						return new ResponseEntity<>("product-group save processing try after some time.",
//								HttpStatus.BAD_REQUEST);
//					}

					// update sync status
//					
//					so.setCompleted(false);
//					so.setLastSyncStartedDate(LocalDateTime.now());
//					syncOperationRepository.save(so);
//					// save/update
//					productProfileUploadService.saveUpdateProductProfiles(productProfileDTOs, so);
//					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
//				}).orElse(new ResponseEntity<>("Product-profile sync operation not registered for this company",
//						HttpStatus.BAD_REQUEST));
	// }

	@RequestMapping(value = "/custom/product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfileCustom(
			@RequestBody TPProductProfileCustomDTO productProfileCustomDTO) {
		log.debug("REST request to save ProductProfile custom");
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTPROFILE).map(so -> {

					Optional<SyncOperation> opSyncCG = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTCATEGORY);

					if (!opSyncCG.get().getCompleted()) {
						return new ResponseEntity<>("product-category save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncPG = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTGROUP);

					if (!opSyncPG.get().getCompleted()) {
						return new ResponseEntity<>("product-group save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductProfiles(productProfileCustomDTO, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-profile-custom sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-group_product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroupProductProfile(
			@Valid @RequestBody List<TPProductGroupProductDTO> productGroupProductDTOs) {
		log.debug("REST request to save product-group_Product-Profiles : {}", productGroupProductDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTGROUP_PRODUCTPROFILE).map(so -> {

					Optional<SyncOperation> opSyncPP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTPROFILE);

					if (!opSyncPP.get().getCompleted()) {
						return new ResponseEntity<>("product-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					// productProfileUploadService.saveUpdateProductGroupProduct(productGroupProductDTOs,
					// so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"Product-group-Product-profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	/*@RequestMapping(value = "/stock-locations.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSavestockLocations(@Valid @RequestBody List<StockLocationDTO> stockLocationDTOs) {
		log.debug("REST request to save Stock Locations : {}", stockLocationDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.STOCK_LOCATION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateStockLocations(stockLocationDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserStockLocations();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}
					if (so.getDocument()) {
						documentUserWiseUpdateController.assighnStockLocationDestination();
						documentUserWiseUpdateController.assighnStockLocationSource();
					}

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Stock-location sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/opening-stock.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveOpeningStock(@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
		log.debug("REST request to save opening stock : {}", openingStockDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.OPENING_STOCK).map(so -> {

					Optional<SyncOperation> opSyncSL = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.STOCK_LOCATION);

					if (!opSyncSL.get().getCompleted()) {
						return new ResponseEntity<>("stock-location save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncPP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTPROFILE);

					if (!opSyncPP.get().getCompleted()) {
						return new ResponseEntity<>("product-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateOpeningStock(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
*/
	@RequestMapping(value = "/price-levels.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSavePriceLevels(@Valid @RequestBody List<PriceLevelDTO> priceLevelDTOs) {
		log.debug("REST request to save price level : {}", priceLevelDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRICE_LEVEL).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdatePriceLevels(priceLevelDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserPriceLevels();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}
					if (so.getDocument()) {
						documentUserWiseUpdateController.assighnDocumentPriceLevels();
					}

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("price-levels sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/price-level-list.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSavePriceLevelList(
			@Valid @RequestBody List<PriceLevelListDTO> priceLevelListDTOs) {
		log.debug("REST request to save price level list : {}", priceLevelListDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRICE_LEVEL_LIST).map(so -> {

					Optional<SyncOperation> opSyncPL = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRICE_LEVEL);

					if (!opSyncPL.get().getCompleted()) {
						return new ResponseEntity<>("price-level save processing ! try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncPP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTPROFILE);

					if (!opSyncPP.get().getCompleted()) {
						return new ResponseEntity<>("product-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdatePriceLevelList(priceLevelListDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Price-Level-List sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-wise-default-ledger.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductWiseDefaultLedger(@Valid @RequestBody List<ProductProfileDTO> ppDTOs) {
		log.debug("REST request to save product-wise-default-ledger : {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCT_WISE_DEFAULT_LEDGER).map(so -> {
					Optional<SyncOperation> opSyncPP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTPROFILE);
					if (!opSyncPP.get().getCompleted()) {
						return new ResponseEntity<>("product-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductWiseDefaultLedger(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"product-wise-default-ledger sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/gst-product-group.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveGSTProductGroupWise(@Valid @RequestBody List<GSTProductGroupDTO> gstDTOs) {
		log.debug("REST request to save gst_product-group : {}", gstDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.GST_PRODUCT_GROUP).map(so -> {

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateGSTProductGroup(gstDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("gst_product_group sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/tax-master.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveTaxMasters(@Valid @RequestBody List<TaxMasterDTO> taxMasterDTOs) {
		log.debug("REST request to save taxMasters : {}", taxMasterDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.TAX_MASTER).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateTaxMasters(taxMasterDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("TaxMaster sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-profile-tax-master.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfileTaxMasters(@Valid @RequestBody List<ProductProfileDTO> ppDTOs) {
		log.debug("REST request to save product profiles tax masters : {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCT_PROFILE_TAX_MASTER).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductProfileTaxMasters(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"PRODUCT_PROFILE_TAX_MASTER sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/ecom-product-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveEcomProductProfile(@Valid @RequestBody List<EcomProductProfileDTO> ppDTOs) {
		log.debug("REST request to save ecom product profile : {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ECOM_PRODUCT_PROFILE).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateEcomProductProfiles(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("ECOM_PRODUCT_PROFILE sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/ecom-product-profile-product.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveEcomProductProfileProduct(
			@Valid @RequestBody List<EcomProductProfileProductDTO> ppDTOs) {
		log.debug("REST request to save ecom product profile product: {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ECOM_PRODUCT_PROFILE_PRODUCT).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateEcomProductProfileProducts(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"ECOM_PRODUCT_PROFILE_PRODUCT sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-group-ecom-product.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroupEcomProductProfile(
			@Valid @RequestBody List<ProductGroupEcomProductDTO> ppDTOs) {
		log.debug("REST request to save product group ecom product profile : {}", ppDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCT_GROUP_ECOM_PRODUCT).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					productProfileUploadService.saveUpdateProductGroupEcomProductProfiles(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"PRODUCT_GROUP_ECOM_PRODUCT sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

}
