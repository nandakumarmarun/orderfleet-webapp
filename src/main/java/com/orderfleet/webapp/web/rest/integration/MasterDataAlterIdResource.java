package com.orderfleet.webapp.web.rest.integration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AlterIdMasterService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.async.TPProductProfileManagementService;
import com.orderfleet.webapp.service.async.TPalterIdservicesManagementService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductCategoryDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;

@RestController
@RequestMapping(value = "/api/tp/v1")
public class MasterDataAlterIdResource {
	
	@Inject
	private SyncOperationRepository syncOperationRepository;
	
	@Inject
	private TPalterIdservicesManagementService tPalterIdservicesManagementService;
	
	@Inject
	private TPProductProfileManagementService tpProductProfileManagementService;
	
	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;
	
	@Autowired
	private AlterIdMasterService alterIdMasterService;
	
	private final Logger log = LoggerFactory.getLogger(MasterDataAccountProfileResource.class);
	
	@RequestMapping(value = "/account-profiles-alterid.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfiles(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) {
		log.debug("REST request to save AccountProfiles : {}", accountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ACCOUNT_PROFILE).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					tPalterIdservicesManagementService.saveUpdateAccountProfiles(accountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/account-profilesUpdateId-alterId.json/{fullUpdate}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfilesWithId(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs,@PathVariable boolean fullUpdate) {
		log.debug("REST request to save AccountProfiles : {}", accountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ACCOUNT_PROFILE).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					
					// save/update
					tPalterIdservicesManagementService.saveUpdateAccountProfilesUpdateIdNew(accountProfileDTOs,so,fullUpdate);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/locations-alterid.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocations(@Valid @RequestBody List<LocationDTO> locationDTOs) {
		log.debug("REST request to save locations : {}", locationDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update

					tPalterIdservicesManagementService.saveUpdateLocations(locationDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserLocations();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Locations sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/locationsUpdateId-alterID.json/{fullUpdate}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationsWithId(@Valid @RequestBody List<LocationDTO> locationDTOs, @PathVariable("fullUpdate") boolean fullUpdate ) {
		log.debug("REST request to save locations : {}", locationDTOs.size(),fullUpdate);
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update

					tPalterIdservicesManagementService.saveUpdateLocationsUpdationIdNew(locationDTOs, so,fullUpdate);

					
					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserLocations();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Locations sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/location-account-profile-alterid.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationAccountProfiles(
			@Valid @RequestBody List<LocationAccountProfileDTO> locationAccountProfileDTOs) {
		log.debug("REST request to save location-accountprofiles : {}", locationAccountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_ACCOUNT_PROFILE).map(so -> {

					Optional<SyncOperation> opSyncLO = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.LOCATION);

					if (!opSyncLO.get().getCompleted()) {
						return new ResponseEntity<>("location save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncAP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.ACCOUNT_PROFILE);

					if (!opSyncAP.get().getCompleted()) {
						return new ResponseEntity<>("account-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					tPalterIdservicesManagementService.saveUpdateLocationAccountProfiles(locationAccountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>("Location-Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/location-account-alterid-profileUpdateId.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationAccountProfilesWithId(
			@Valid @RequestBody List<LocationAccountProfileDTO> locationAccountProfileDTOs) {
		log.debug("REST request to save location-accountprofiles : {}", locationAccountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_ACCOUNT_PROFILE).map(so -> {

					Optional<SyncOperation> opSyncLO = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.LOCATION);

					if (!opSyncLO.get().getCompleted()) {
						return new ResponseEntity<>("location save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					
					Optional<SyncOperation> opSyncAP = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.ACCOUNT_PROFILE);

					if (!opSyncAP.get().getCompleted()) {
						return new ResponseEntity<>("account-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					tPalterIdservicesManagementService.saveUpdateLocationAccountProfilesUpdateIdNew(locationAccountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>("Location-Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/product-categories-alterid.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tPalterIdservicesManagementService.saveUpdateProductCategories(productCategoryDTOs, so);

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
	
	@RequestMapping(value = "/product-categoriesalterIDUpdateIdNew.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductCategoriesWithId(
			@Valid @RequestBody List<ProductCategoryDTO> productCategoryDTOs) {
		log.debug("REST request to save ProductCategories : {}", productCategoryDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTCATEGORY).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					tPalterIdservicesManagementService.saveUpdateProductCategoriesUpdateIdNew(productCategoryDTOs, so);

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
	
	@RequestMapping(value = "/product-group-alterid.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tPalterIdservicesManagementService.saveUpdateProductGroups(productGroupDTOs, so);

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

	@RequestMapping(value = "/product-groupdataUpdateIdNew-alterID.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductGroupsWithId(@Valid @RequestBody List<ProductGroupDTO> productGroupDTOs) {
		log.debug("REST request to save ProductGroups : {}", productGroupDTOs.size());
		
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRODUCTGROUP).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					tPalterIdservicesManagementService.saveUpdateProductGroupsUpdateIdNew(productGroupDTOs, so);

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
	
	@RequestMapping(value = "/product-profile-alterid.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					tPalterIdservicesManagementService.saveUpdateProductProfiles(productProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@RequestMapping(value = "/product-profileUpdateIdNew-alterId.json/{fullUpdate}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveProductProfilesWithId(
			@Valid @RequestBody List<ProductProfileDTO> productProfileDTOs , @PathVariable("fullUpdate") boolean fullUpdate ) {
		log.debug("REST request to save ProductProfiles : {}", productProfileDTOs.size()+":method"+fullUpdate);
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
					
					tPalterIdservicesManagementService.saveUpdateProductProfilesUpdateIdNew(productProfileDTOs, so,fullUpdate);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Product-profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/product-wise-default-ledgerIdNew-alterId.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
	
	@RequestMapping(value = "/alterid-master.json/{masterName}" ,method=RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<Long> getAlterIdFromAlterIdMaster(@PathVariable("masterName") String masterName){
		log.debug("REST request to save AlteIdMaster ecom product profile : {}");
		AlterIdMasterDTO alterIdMasterDTO = alterIdMasterService.findByMasterName(masterName, SecurityUtils.getCurrentUsersCompanyId());
		log.info("AlterIDREsponse"+alterIdMasterDTO.getAlterId());
		if(alterIdMasterDTO.getAlterId() == null) {
			System.out.println("present");
			alterIdMasterDTO.setAlterId(0L);
		}
		log.info("AlterIDREsponse"+alterIdMasterDTO.getAlterId());
		return ResponseEntity.ok().body(alterIdMasterDTO.getAlterId());
	}
	
	
}
