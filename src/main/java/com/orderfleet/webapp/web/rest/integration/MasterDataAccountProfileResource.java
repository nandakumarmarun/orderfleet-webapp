package com.orderfleet.webapp.web.rest.integration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.itextpdf.text.pdf.PdfChunk;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.PostDatedVoucherAllocationRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PostDatedVoucherAllocationService;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherAllocationDTO;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@RestController
@RequestMapping(value = "/api/tp/v1")
public class MasterDataAccountProfileResource {

	private final Logger log = LoggerFactory.getLogger(MasterDataAccountProfileResource.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private TPAccountProfileManagementService tpAccountProfileManagementService;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;
	
	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private TallyDataUploadService tallyDataUploadService;
	
	@Inject
	PostDatedVoucherService postDatedVoucherService;
	
	@Inject
	PostDatedVoucherRepository postDatedVoucherRepository;
	
	@Inject
	PostDatedVoucherAllocationService postDatedVoucherAllocationService;

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
					tpAccountProfileManagementService.saveUpdateAccountProfiles(accountProfileDTOs, so);
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

					tpAccountProfileManagementService.saveUpdateLocations(locationDTOs, so);

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
					tpAccountProfileManagementService.saveUpdateLocationHierarchy(locationHierarchyDTOs, so);
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
					tpAccountProfileManagementService.saveUpdateLocationAccountProfiles(locationAccountProfileDTOs, so);
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
					tpAccountProfileManagementService.saveUpdateReceivablePayables(receivablePayableDTOs, so);
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
					tpAccountProfileManagementService.saveUpdateAccountProfileClosingBalances(accountProfileDTOs, so);
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
					tpAccountProfileManagementService
							.saveUpdatePriceLevelAccountProductGroups(priceLevelAccountProductGroupDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				})
				.orElse(new ResponseEntity<>(
						"gst ledger sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	@PostMapping("/gst-ledger-tally")
	public ResponseEntity<String> getGstLedgers(@RequestBody List<GstLedgerDTO> gstLedgers) {
		log.debug("Rest API to insert gst ledgers "+gstLedgers.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.GST_LEDGER).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					tpAccountProfileManagementService.saveOrUpdateGstLedgers(gstLedgers, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
	@PostMapping(path = "/post-dated-vouchers", produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> saveAllPostDatedVouchers(@Valid @RequestBody List<PostDatedVoucherDTO> postDatedVoucherDtos) throws IOException
	{
		log.info("API request for post dated voucher -- List size -- {}",postDatedVoucherDtos.size());
		
		//postDatedVoucherRepository.deleteByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		postDatedVoucherService.deleteAllPostDatedVoucherEntries(SecurityUtils.getCurrentUsersCompanyId());
		
		List<PostDatedVoucherDTO> result = new ArrayList<PostDatedVoucherDTO>();
		
		Comparator<PostDatedVoucherDTO> accountWiseComparator = 
				Comparator.comparing(PostDatedVoucherDTO::getReferenceDocumentNumber);
		postDatedVoucherDtos.sort(accountWiseComparator);
		log.info("Saving postdated entries......");
		result.addAll(postDatedVoucherService.saveAll(postDatedVoucherDtos));
		log.info("Saved all postDatedVouchers");
		List<PostDatedVoucherAllocationDTO> postDatedVoucherAllocationDtos = new ArrayList<>();
		for(PostDatedVoucherDTO pdc : postDatedVoucherDtos){
			if(pdc != null && pdc.getPostDatedVoucherAllocationList() != null)
				postDatedVoucherAllocationDtos.addAll(pdc.getPostDatedVoucherAllocationList());
		}
		log.info("Fetched all postdatedVoucherAllocations.................");
		postDatedVoucherAllocationService.saveAllPDCAllocation(postDatedVoucherAllocationDtos);
		String pdc = null;
//		File file = new File("pdc.txt");
//		FileWriter fw = new FileWriter(file,true);
//		BufferedWriter bw = new BufferedWriter(fw);
//		for(PostDatedVoucherDTO dto : postDatedVoucherDtos)
//		{
//////			result.add(postDatedVoucherService.save(dto));	
//			 pdc = 	"\n-------------------------------------------------------------"
//							+"\n"+dto.getAccountProfileName() +"  Doc Date:  "+dto.getReferenceDocumentDate()+"    Doc No: "+dto.getReferenceDocumentNumber()+"  Doc amt: "+dto.getReferenceDocumentAmount();
//							
//							if(dto.getPostDatedVoucherAllocation() == null )
//								continue;
//							
//							for(PostDatedVoucherAllocationDTO allocDto : dto.getPostDatedVoucherAllocation()){
//								if(allocDto == null)
//									continue;
//								pdc += "\n ALLOCATION:  "+allocDto.getAllocReferenceVoucher()+"      AMT : "+allocDto.getAllocReferenceVoucherAmount();
//							}
//			bw.write(pdc);
//		}
//		bw.close();
//		fw.close();
		if(result.size() == postDatedVoucherDtos.size())
		{
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else {
			List<String> pdcSuccessList = result.stream().map(pdcSucces -> pdcSucces.getAccountProfileName()).collect(Collectors.toList());
			List<String> pdcAllList = postDatedVoucherDtos.stream().map(pdcAll -> pdcAll.getAccountProfileName()).collect(Collectors.toList());
			pdcAllList.removeAll(pdcSuccessList);
			return new ResponseEntity<>(pdcAllList.size()+" Entrie(s) not inserted.\n"
			+pdcAllList.toString(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
