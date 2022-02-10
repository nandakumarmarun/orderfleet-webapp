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
	
	@RequestMapping(value = "/product-group_product-profileUpdateIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroupProductProfile(
			@Valid @RequestBody List<TPProductGroupProductDTO> productGroupProductDTOs) {
		log.debug("REST request to save product-group_Product-Profiles : {}", productGroupProductDTOs.size());

		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "COMP_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<CompanyConfiguration> optionaldistanceTarvel = companyConfigurationRepository
		.findByCompanyIdAndName(companyId, CompanyConfig.REFRESH_PRODUCT_GROUP_PRODUCT);
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

			if(optionaldistanceTarvel.isPresent()) {
				if(!productGroupProductDTOs.isEmpty()) {
					
					tpProductProfileManagementService.deleteProductProductGroups(companyId);
					
				}
			}
		
		
		
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
					log.info("Insert product product groups.......");
					if(optionaldistanceTarvel.isPresent()) {
						tpProductProfileManagementService.saveDeleteProductGroupProductUpdateIdNew(productGroupProductDTOs, so);
					}else {
						tpProductProfileManagementService.saveUpdateProductGroupProductUpdateIdNew(productGroupProductDTOs, so);
					}
					
					
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
}
