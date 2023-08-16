package com.orderfleet.webapp.web.rest.integration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.integration.dto.GSTProductGroupDTO;
import com.orderfleet.webapp.web.rest.integration.dto.TPProductGroupProductDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
@RestController
@RequestMapping(value = "/api/tp/v1")
public class MasterDataProductResourceIdUpdateNew {

	private final Logger log = LoggerFactory.getLogger(MasterDataProductResourceIdUpdateNew.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private TPProductProfileManagementService tpProductProfileManagementService;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@RequestMapping(value = "/product-categoriesUpdateIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tpProductProfileManagementService.saveUpdateProductCategoriesUpdateIdNew(productCategoryDTOs, so);

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

	@RequestMapping(value = "/product-groupdataUpdateIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tpProductProfileManagementService.saveUpdateProductGroupsUpdateIdNew(productGroupDTOs, so);

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

	@RequestMapping(value = "/product-profileUpdateIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfiles(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs) {
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTPROFILE).map(so -> {

					Optional<SyncOperation> opSyncCG = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTCATEGORY);

					if (opSyncCG.isPresent() && !opSyncCG.get().getCompleted()) {
						return new ResponseEntity<>("product-category save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncPG = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.PRODUCTGROUP);

					if (opSyncPG.isPresent() && !opSyncPG.get().getCompleted()) {
						return new ResponseEntity<>("product-group save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status  
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					
					tpProductProfileManagementService.saveUpdateProductProfilesUpdateIdNew(productProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}


	/**
	 * @apiNote  Endpoint for bulk saving or updating product-group_Product-Profiles.
	 * This method receives a list of TPProductGroupProductDTO objects containing product-group_Product-Profile information
	 * and performs the necessary operations based on the provided data.
	 *
	 * @param productGroupProductDTOs A list of TPProductGroupProductDTO objects representing product-group_Product-Profile data.
	 *                                Each object contains information about a specific product-group_Product-Profile mapping.
	 *                                The data must be validated before processing.
	 * @return ResponseEntity<String> A response entity with a status code and message indicating the result of the operation.
	 *                                If successful, the response entity will have a status code of 200 (OK) and a message "Uploaded".
	 *                                If there is an error, the response entity will have a status code of 400 (Bad Request)
	 *                                and an appropriate error message.
	 * @since 1.109.5
	 */
	@RequestMapping(value = "/product-group_product-profileUpdateIdNew.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroupProductProfile(
			@Valid @RequestBody List<TPProductGroupProductDTO> productGroupProductDTOs) {
		log.debug("Received a request to bulk save/update product-group_Product-Profiles.",
				productGroupProductDTOs.size());

		// Get the current user's companyId
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		String Tread = String.valueOf(System.nanoTime() +"-"+companyId );

		// Check if the company configuration "REFRESH_PRODUCT_GROUP_PRODUCT" exists for the current companyId
		Optional<CompanyConfiguration> refreshProductGroupProductConfig =
					companyConfigurationRepository.findByCompanyIdAndName(
							companyId, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);

			if(refreshProductGroupProductConfig.isPresent()
					&& refreshProductGroupProductConfig.get().getValue().equalsIgnoreCase("true")) {
				log.info(Tread + " : refreshProductGroupProductConfig : {}", companyId);
				// If productGroupProductDTOs list is not empty,
				// delete all existing product-product group mappings for the companyId
				if(!productGroupProductDTOs.isEmpty()) {
					log.info(Tread + " : Deleting existing product-product group mappings for companyId: {}", companyId);
					tpProductProfileManagementService
							.deleteProductProductGroups(companyId);
				}
			}

		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
						SyncOperationType.PRODUCTGROUP_PRODUCTPROFILE)
				.map(so -> {

					// Check if a sync operation for PRODUCTPROFILE is already in progress
					Optional<SyncOperation> opSyncPP =
							syncOperationRepository.findOneByCompanyIdAndOperationType(
									SecurityUtils.getCurrentUsersCompanyId(),
									SyncOperationType.PRODUCTPROFILE);

					if (!opSyncPP.get().getCompleted()) {
						log.warn( Tread + " : Product-profile save processing is not allowed at this time due to an ongoing operation.");

						// If a sync operation for PRODUCTPROFILE is in progress, return an error response
						return new ResponseEntity<>("product-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// Update sync status for the current sync operation (PRODUCTGROUP_PRODUCTPROFILE)
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);

					// Save/Update product-group_Product-Profiles based on the configuration
					if(refreshProductGroupProductConfig.isPresent()
							&& refreshProductGroupProductConfig.get().getValue().equalsIgnoreCase("true")) {
						log.info(Tread + " : Deleting and Inserting product product groups for companyId: {}", companyId);
						tpProductProfileManagementService
								.saveDeleteProductGroupProductUpdateIdNew(
										productGroupProductDTOs, so,Tread);
					}else {
						log.info(Tread + " : Updating and inserting product product groups for companyId: {}", companyId);
						tpProductProfileManagementService
								.saveUpdateProductGroupProductUpdateIdNew(
										productGroupProductDTOs, so,Tread);
					}

					// Return a success response
					log.info("Product-group_Product-Profiles bulk save/update completed successfully.");
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"Product-group-Product-profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/opening-stockUpdatedIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
			        log.debug(String.valueOf(System.nanoTime() + so.getCompany().getId()));
					tpProductProfileManagementService.saveUpdateOpeningStockUpadetIdNew(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/temporary-opening-stockUpdatedIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveTemporaryOpeningStock(@Valid @RequestBody List<OpeningStockDTO> openingStockDTOs) {
		log.debug("REST request to save opening stock : {}", openingStockDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.TEMPORARY_OPENING_STOCK).map(so -> {

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
					tpProductProfileManagementService.saveUpdateTemporaryOpeningStockUpdatedIdNew(openingStockDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("opening-stock sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	
	@RequestMapping(value = "/price-level-listUpdatedIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tpProductProfileManagementService.saveUpdatePriceLevelListUpdatedIdNew(priceLevelListDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Price-Level-List sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/gst-product-groupUpdatedIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tpProductProfileManagementService.saveUpdateGSTProductGroupUpdatedIdNew(gstDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("gst_product_group sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/product-wise-default-ledgerIdNem.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tpProductProfileManagementService.saveUpdateProductWiseDefaultLedger(ppDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"product-wise-default-ledger sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
}
