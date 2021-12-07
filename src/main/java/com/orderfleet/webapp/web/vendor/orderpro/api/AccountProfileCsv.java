package com.orderfleet.webapp.web.vendor.orderpro.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.vendor.orderpro.service.AccountProfileCsvUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/v1")
public class AccountProfileCsv {

	private final Logger log = LoggerFactory.getLogger(AccountProfileCsv.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private AccountProfileCsvUploadService accountProfileCsvUploadService;;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;

	@RequestMapping(value = "/account-profiles.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "AP_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get all by compId";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					List<AccountProfile> existingAccountProfiles = accountProfileRepository
							.findAllByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
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

					accountProfileCsvUploadService.saveUpdateAccountProfiles(accountProfileDTOs, so);
					for (AccountProfile accountProfile : existingAccountProfiles) {
						accountProfileDTOs.removeIf(aProfile -> aProfile.getAlias().equals(accountProfile.getAlias()));
					}
					List<String> newAccountProfileAlias = accountProfileDTOs.stream()
							.map(aProfile -> aProfile.getAlias()).collect(Collectors.toList());
					System.out.println("-----------------"+newAccountProfileAlias.size());
					accountProfileCsvUploadService.locationAccountProfileAssociation(newAccountProfileAlias, so.getCompany());
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/locations.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
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
					accountProfileCsvUploadService.saveUpdateLocations(locationDTOs, so);

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

	@RequestMapping(value = "/location-hierarchy.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> createUpdateLocationHierarchy(
			@RequestBody List<LocationHierarchyDTO> locationHierarchyDTOs) {
		log.debug("REST request to save location hierarchy : {}", locationHierarchyDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_HIRARCHY).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					accountProfileCsvUploadService.saveUpdateLocationHierarchy(locationHierarchyDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Location-Hierarchy sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/location-account-profile.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveLocationAccountProfiles(
			@Valid @RequestBody List<LocationAccountProfileDTO> locationAccountProfileDTOs) {
		log.debug("REST request to save location-accountprofiles : {}", locationAccountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.LOCATION_ACCOUNT_PROFILE).map(so -> {

					Optional<SyncOperation> opSyncLO = syncOperationRepository.findOneByCompanyIdAndOperationType(
							SecurityUtils.getCurrentUsersCompanyId(), SyncOperationType.LOCATION_ACCOUNT_PROFILE);

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
					accountProfileCsvUploadService.saveUpdateLocationAccountProfiles(locationAccountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>("Location-Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/receivable-payable.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveReceivablePayables(
			@Valid @RequestBody List<ReceivablePayableDTO> receivablePayableDTOs) {
		log.debug("REST request to save Receivable Payable : {}", receivablePayableDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.RECEIVABLE_PAYABLE).map(so -> {

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
					accountProfileCsvUploadService.saveUpdateReceivablePayables(receivablePayableDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Receivable-Payable sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/account-profiles-closing-balance.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfileClosingBalances(
			@Valid @RequestBody List<AccountProfileDTO> accountProfileDTOs) {
		log.debug("REST request to save account profiles closing balance : {}", accountProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ACCOUNT_PROFILE_CLOSING_BALANCE).map(so -> {
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
					accountProfileCsvUploadService.saveUpdateAccountProfileClosingBalances(accountProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"account profile closing balance sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}

	@RequestMapping(value = "/price-level-account-product-group", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSavePriceLevelAccountProductGroup(
			@Valid @RequestBody List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroupDTOs) {
		log.debug("REST request to save priceLevel Account ProductGroupDTOs : {}",
				priceLevelAccountProductGroupDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.PRICELEVEL_ACCOUNT_PRODUCTGROUP).map(so -> {

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
					accountProfileCsvUploadService
							.saveUpdatePriceLevelAccountProductGroups(priceLevelAccountProductGroupDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"pricelevel_account_productgroup sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
}
