package com.orderfleet.webapp.web.vendor.excel.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
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
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.vendor.excel.service.AccountProfileGsifUploadService;
import com.orderfleet.webapp.web.vendor.excel.service.AccountProfileUploadService;
/**
 * Controller class for handling REST endpoints related to bulk synchronization operations.
 * This class is used by the GSIF, DevSoft Classic, and Electra client applications for managing
 * synchronization operations for companies.
 */
@RestController
@RequestMapping(value = "/api/excel/v1")
public class AccountProfileExcel {

	private final Logger log = LoggerFactory.getLogger(AccountProfileExcel.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private TPAccountProfileManagementService tpAccountProfileManagementService;
	
	@Inject
	private AccountProfileUploadService accountProfileUploadService;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;
	
	@Inject
	private AccountProfileGsifUploadService accountProfileGsifUploadService;
	/**
	 * REST endpoint for bulk saving account profiles in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of AccountProfileDTOs.
	 * It attempts to find an existing sync operation for account profiles based on
	 * company ID and operation type.
	 * If a sync operation is found, it updates its status and start time,
	 * then saves or updates the account profiles
	 * using the provided service.
	 *
	 * @param accountProfileDTOs List of AccountProfileDTOs to be saved or updated.
	 * @param request            HttpServletRequest representing the incoming HTTP request.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 * @URL :  /api/excel/v1/account-profiles.json
	 */
	@RequestMapping(value = "/account-profiles.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfiles(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs,
			HttpServletRequest request) {
		log.info("Received request to bulk save account profiles : " + request.getRequestURI());
		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
						SyncOperationType.ACCOUNT_PROFILE)
				.map(so -> {
					log.debug("Sync operation found. Updating sync status...");
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);

					log.debug("Saving/Updating account profiles...");
					accountProfileUploadService.saveUpdateAccountProfiles(accountProfileDTOs, so);
					log.info("Account profiles uploaded successfully...");

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	/**
	 * REST endpoint for bulk saving locations in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of LocationDTOs.
	 * It attempts to find an existing sync operation for locations based on company ID and operation type.
	 * If a sync operation is found, it updates its status and start time, then saves or updates the locations
	 * using the provided service. If the sync operation is flagged for user-related updates,
	 * it triggers additional actions related to user locations and updates the sync operation status accordingly.
	 *
	 * @param locationDTOs List of LocationDTOs to be saved or updated.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 * @Url :/api/excel/v1/locations.json
	 */
	@RequestMapping(value = "/locations.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocations(
			@Valid @RequestBody List<LocationDTO> locationDTOs) {
		log.debug("REST request to save locations : {}", locationDTOs.size());

		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
						SyncOperationType.LOCATION)
				.map(so -> {
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					accountProfileUploadService.saveUpdateLocations(locationDTOs, so);

					if (so.getUser()) {
						documentUserWiseUpdateController.assignSaveUserLocations();
						so.setUser(false);
						so.setCompleted(true);
						syncOperationRepository.save(so);
					}

					return new ResponseEntity<>(
							"Uploaded",
							HttpStatus.OK);
				}).orElse(new ResponseEntity<>(
						"Locations sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	/**
	 * REST endpoint for creating or updating location hierarchies in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of LocationHierarchyDTOs.
	 * It attempts to find an existing sync operation for location hierarchies
	 * based on company ID and operation type.
	 * If a sync operation is found, it updates its status and start time,
	 * then saves or updates the location hierarchies
	 * using the provided service.
	 *
	 * @param locationHierarchyDTOs List of LocationHierarchyDTOs to be created or updated.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 * @URL :/api/excel/v1/location-hierarchy.json
	 */
	@RequestMapping(value = "/location-hierarchy.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createUpdateLocationHierarchy(
			@RequestBody List<LocationHierarchyDTO> locationHierarchyDTOs) {
		log.debug("REST request to save location hierarchy : {}", locationHierarchyDTOs.size());

		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_HIRARCHY).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					accountProfileUploadService
							.saveUpdateLocationHierarchy(
							locationHierarchyDTOs, so);
					return new ResponseEntity<>(
							"Uploaded",
							HttpStatus.OK);
				}).orElse(new ResponseEntity<>(
						"Location-Hierarchy sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	/**
	 * REST endpoint for bulk saving location account profiles in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of LocationAccountProfileDTOs.
	 * It attempts to find an existing sync operation for location account profiles
	 * based on company ID and operation type.
	 * If sync operations for location and account profiles are completed,
	 * it updates the status and start time of the
	 * location-account-profile sync operation, then saves or updates the
	 * location account profiles using the provided service.
	 *
	 * @param locationAccountProfileDTOs List of LocationAccountProfileDTOs to be saved or updated.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 * @URL :/api/excel/v1/location-account-profile.json
	 */
	@RequestMapping(value = "/location-account-profile.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationAccountProfiles(
			@Valid @RequestBody List<LocationAccountProfileDTO> locationAccountProfileDTOs) {

		log.debug("REST request to save location-accountprofiles : {}",
				locationAccountProfileDTOs.size());
		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_ACCOUNT_PROFILE)
				.map(so -> {

					Optional<SyncOperation> opSyncLO =
							syncOperationRepository.
									findOneByCompanyIdAndOperationType(
											SecurityUtils.getCurrentUsersCompanyId(),
											SyncOperationType.LOCATION);

					if (!opSyncLO.get().getCompleted()) {
						return new ResponseEntity<>("location save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					Optional<SyncOperation> opSyncAP =
							syncOperationRepository
									.findOneByCompanyIdAndOperationType(
											SecurityUtils.getCurrentUsersCompanyId(),
											SyncOperationType.ACCOUNT_PROFILE);

					if (!opSyncAP.get().getCompleted()) {
						return new ResponseEntity<>(
								"account-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);

					// save/update
					accountProfileUploadService
							.saveUpdateLocationAccountProfiles(
									locationAccountProfileDTOs,
									so);

					return new ResponseEntity<>(
							"Uploaded",
							HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"Location-Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	/**
	 * REST endpoint for bulk saving Receivable Payables in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of ReceivablePayableDTOs.
	 * It attempts to find an existing sync operation for Receivable Payables based on company ID and operation type.
	 * If the sync operation for account profiles is completed, it updates the status and start time of the
	 * Receivable-Payable sync operation, then saves or updates the Receivable Payables using the provided service.
	 *
	 * @param receivablePayableDTOs List of ReceivablePayableDTOs to be saved or updated.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 */
	@RequestMapping(value = "/receivable-payable.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveReceivablePayables(
			@Valid @RequestBody List<ReceivablePayableDTO> receivablePayableDTOs) {
		log.debug("REST request to save Receivable Payable : {}", receivablePayableDTOs.size());

		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
						SyncOperationType.RECEIVABLE_PAYABLE)
				.map(so -> {
					Optional<SyncOperation> opSyncAP = syncOperationRepository
							.findOneByCompanyIdAndOperationType(
									SecurityUtils.getCurrentUsersCompanyId(),
									SyncOperationType.ACCOUNT_PROFILE);

					if (!opSyncAP.get().getCompleted()) {
						return new ResponseEntity<>(
								"account-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);

					// save/update
					accountProfileUploadService
							.saveUpdateReceivablePayables(
									receivablePayableDTOs, so);

					return new ResponseEntity<>(
							"Uploaded",
							HttpStatus.OK);

				}).orElse(new ResponseEntity<>(
						"Receivable-Payable sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	/**
	 * REST endpoint for bulk saving Account Profiles' closing balances in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of AccountProfileDTOs.
	 * It attempts to find an existing sync operation for Account Profiles' closing balances based on company ID and operation type.
	 * If the sync operation for account profiles is completed, it updates the status and start time of the
	 * Account Profile Closing Balance sync operation, then saves or updates the Account Profiles' closing balances
	 * using the provided service.
	 *
	 * @param accountProfileDTOs List of AccountProfileDTOs representing closing balances to be saved or updated.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 */
	@RequestMapping(value = "/account-profiles-closing-balance.json",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfileClosingBalances(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) {
		log.debug("REST request to save account profiles closing balance : {}",
				accountProfileDTOs.size());

		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),

						SyncOperationType.ACCOUNT_PROFILE_CLOSING_BALANCE)
				.map(so -> {
					Optional<SyncOperation> opSyncAP =
							syncOperationRepository
									.findOneByCompanyIdAndOperationType(
											SecurityUtils.getCurrentUsersCompanyId(),
											SyncOperationType.ACCOUNT_PROFILE);

					if (!opSyncAP.get().getCompleted()) {
						return new ResponseEntity<>(
								"account-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);

					// save/update
					accountProfileUploadService
							.saveUpdateAccountProfileClosingBalances(
									accountProfileDTOs, so);

					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"account profile closing balance sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	/**
	 * REST endpoint for bulk saving Price Level Account Product Groups in JSON format.
	 *
	 * This endpoint receives a POST request containing a list of PriceLevelAccountProductGroupDTOs.
	 * It attempts to find an existing sync operation for Price Level Account Product Groups based on company ID and operation type.
	 * If the sync operation for account profiles is completed, it updates the status and start time of the
	 * Price Level Account Product Group sync operation, then saves or updates the Price Level Account Product Groups
	 * using the provided service.
	 *
	 * @param priceLevelAccountProductGroupDTOs List of PriceLevelAccountProductGroupDTOs to be saved or updated.
	 * @return ResponseEntity<String> indicating the success or failure of the operation.
	 */
	@RequestMapping(value = "/price-level-account-product-group",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSavePriceLevelAccountProductGroup(
			@Valid @RequestBody List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroupDTOs) {

		log.debug("REST request to save priceLevel Account ProductGroupDTOs : {}",
				priceLevelAccountProductGroupDTOs.size());

		return syncOperationRepository
				.findOneByCompanyIdAndOperationType(
						SecurityUtils.getCurrentUsersCompanyId(),
						SyncOperationType.PRICELEVEL_ACCOUNT_PRODUCTGROUP)

				.map(so -> {
					Optional<SyncOperation> opSyncAP =
							syncOperationRepository
									.findOneByCompanyIdAndOperationType(
											SecurityUtils.getCurrentUsersCompanyId(),
											SyncOperationType.ACCOUNT_PROFILE);

					if (!opSyncAP.get().getCompleted()) {
						return new ResponseEntity<>(
								"account-profile save processing try after some time.",
								HttpStatus.BAD_REQUEST);
					}

					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);

					// save/update
					accountProfileUploadService
							.saveUpdatePriceLevelAccountProductGroups(
									priceLevelAccountProductGroupDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>(
						"pricelevel_account_productgroup sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}












	@RequestMapping(value = "/account-profiles-gsif.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfilesGsif(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) {
		log.debug("REST request to save AccountProfiles : {}", accountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ACCOUNT_PROFILE).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					accountProfileGsifUploadService.saveUpdateAccountProfiles(accountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/locations-gsif.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationsGsif(@Valid @RequestBody List<LocationDTO> locationDTOs) {
		log.debug("REST request to save locations : {}", locationDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					accountProfileGsifUploadService.saveUpdateLocations(locationDTOs, so);

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

	@RequestMapping(value = "/location-hierarchy-gsif.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createUpdateLocationHierarchyGsif(
			@RequestBody List<LocationHierarchyDTO> locationHierarchyDTOs) {
		log.debug("REST request to save location hierarchy : {}", locationHierarchyDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_HIRARCHY).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					accountProfileGsifUploadService.saveUpdateLocationHierarchy(locationHierarchyDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Location-Hierarchy sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/location-account-profile-gsif.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationAccountProfilesGsif(
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
					accountProfileGsifUploadService.saveUpdateLocationAccountProfiles(locationAccountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>("Location-Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	
}
