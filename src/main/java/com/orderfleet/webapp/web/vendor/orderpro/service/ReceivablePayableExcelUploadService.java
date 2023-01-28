package com.orderfleet.webapp.web.vendor.orderpro.service;


import java.time.LocalDateTime;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;

import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;

import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;

import com.orderfleet.webapp.service.AccountProfileService;

import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;

@Service
public class ReceivablePayableExcelUploadService {
	
	private final Logger log = LoggerFactory.getLogger(ReceivablePayableExcelUploadService.class);
	
	@Inject
	private  BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
    
	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private  AccountProfileRepository accountProfileRepository;


	@Inject
	private  ReceivablePayableRepository receivablePayableRepository;

	
	

	@Transactional
	@Async
	public void saveUpdateReceivablePayables(final List<ReceivablePayableDTO> receivablePayableDTOs,final SyncOperation syncOperation) {
		// TODO Auto-generated method stub
		
		
		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
		// find all exist account profiles
		List<String> apCustomerCode = receivablePayableDTOs.stream().map(rp -> rp.getCustomerCode())
				.collect(Collectors.toList());
		System.out.println(apCustomerCode.size() + company.getId()+ "---------------Alias Size");
		
		
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByCompanyIdAndCustomerIdIn(company.getId(), apCustomerCode);
		
		System.out.println(accountProfiles.size() + "---------------Acc Alias Sizes");
		// delete all receivable payable
		receivablePayableRepository.deleteByCompanyId(company.getId());
		for (ReceivablePayableDTO rpDto : receivablePayableDTOs) {
			// only save if account profile exist
			accountProfiles.stream().filter(a -> a.getCustomerId().equals(rpDto.getCustomerCode())).findAny()
					.ifPresent(ap -> {
						System.out.println("Enter here:"+rpDto.getReferenceDocumentDate());
						ap.setCreditDays(rpDto.getCreditDays()!=null ?Long.valueOf (rpDto.getCreditDays()) : 0);
						ReceivablePayable receivablePayable = new ReceivablePayable();
						receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
						receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
						receivablePayable.setAccountProfile(ap);
						receivablePayable.setCompany(company);
						receivablePayable.setBillOverDue(rpDto.getBillOverDue()!=null ? Long.valueOf(rpDto.getBillOverDue()) : 0);
						receivablePayable.setReferenceDocumentAmount(rpDto.getReferenceDocumentAmount()!= 0 ? rpDto.getReferenceDocumentAmount():0.0);
						receivablePayable.setReferenceDocumentBalanceAmount(rpDto.getReferenceDocumentBalanceAmount()!=0 ? rpDto.getReferenceDocumentBalanceAmount():0.0);
						receivablePayable.setReferenceDocumentDate(rpDto.getReferenceDocumentDate()!= null ?rpDto.getReferenceDocumentDate():null);
						receivablePayable.setReferenceDocumentNumber(rpDto.getReferenceDocumentNumber()!=null ? rpDto.getReferenceDocumentNumber() : "");
						receivablePayable.setReferenceDocumentType(rpDto.getReferenceDocumentType()!= null ? rpDto.getReferenceDocumentType() : "");
						receivablePayable.setRemarks(rpDto.getRemarks()!=null ? rpDto.getRemarks():"");
						saveReceivablePayable.add(receivablePayable);
					});
		}
		System.out.println(saveReceivablePayable.size() + "----Before Saving");
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);

		Set<AccountProfile> saveUpdateAccountProfiles = new HashSet<>();

		for (AccountProfile ap : accountProfiles) {
			double closingBalance = saveReceivablePayable.stream()
					.filter(rp -> rp.getAccountProfile().getCustomerId().equals(ap.getCustomerId()))
					.mapToDouble(ReceivablePayable::getReferenceDocumentBalanceAmount).sum();

			ap.setClosingBalance(closingBalance);

			saveUpdateAccountProfiles.add(ap);

		}

		bulkOperationRepositoryCustom.bulkSaveAccountProfile(saveUpdateAccountProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}

}
