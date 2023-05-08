package com.orderfleet.webapp.web.vendor.focus.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.focus.dto.OutStandingFocus;

@Service
public class OutStandingFocusUploadService {

	private final Logger log = LoggerFactory.getLogger(AccountProfileFocusUploadService.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	
	private final CompanyRepository companyRepository;

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final AccountProfileRepository accountProfileRepository;

	private final ReceivablePayableRepository receivablePayableRepository;
	
	private final SyncOperationRepository syncOperationRepository;

	public OutStandingFocusUploadService(CompanyRepository companyRepository,
			BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			AccountProfileRepository accountProfileRepository,
			ReceivablePayableRepository receivablePayableRepository,SyncOperationRepository syncOperationRepository) {
		super();
		this.companyRepository = companyRepository;
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.accountProfileRepository = accountProfileRepository;
		this.receivablePayableRepository = receivablePayableRepository;
		this.syncOperationRepository = syncOperationRepository;
	}
	final Long companyId = (long) 304975;
	@Transactional
	public void saveUpdateReceivablePayable(List<OutStandingFocus> OutStandingFocusDTos) {
		log.info("Saving Outstanding Invoice...");
		long start = System.nanoTime();
//		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		receivablePayableRepository.deleteByCompanyId(companyId);
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
		
		List<String> customerIds = OutStandingFocusDTos.stream().map(a -> a.getCustomerCode())
				.filter(c -> c != null && !c.equalsIgnoreCase("false")).collect(Collectors.toList());
		
		log.info("Customer Ids size {}", customerIds.size());
	
		log.info("CompanyID", companyId );
		
//		Map<String, Double> accountBalanceMap = new HashMap<>();
		
		List<AccountProfile> accProfiles = accountProfileRepository.findAccountProfileAndCustomerIds(companyId,customerIds);
	
	     for(OutStandingFocus OutStandingFocusDTO : OutStandingFocusDTos) {
	    	 
	    	 Optional<AccountProfile> opCustomers = accProfiles.stream()
						.filter(a -> a.getCustomerId().equalsIgnoreCase(OutStandingFocusDTO.getCustomerCode())).findAny();
	    	 
	    	 if (opCustomers.isPresent()) {
	    		 ReceivablePayable receivablePayable = new ReceivablePayable();
	    		 receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
	    		 receivablePayable.setAccountProfile(opCustomers.get());
	    		 receivablePayable.setCompany(company);
	    		 receivablePayable.setReferenceDocumentBalanceAmount(Double.parseDouble(OutStandingFocusDTO.getBalanceAmount()));
				 receivablePayable.setReferenceDocumentNumber(OutStandingFocusDTO.getVoucherNo());
				 receivablePayable.setReferenceDocumentDate(convertDate(OutStandingFocusDTO.getVoucherDate()));
				 receivablePayable.setBillOverDue(dueUpdate(convertDate(OutStandingFocusDTO.getDueDate())));
				 receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
				 saveReceivablePayable.add(receivablePayable);
				 
//				 double currBal = accountBalanceMap.containsKey(OutStandingFocusDTO.getCustomerCode())
//							? accountBalanceMap.get(OutStandingFocusDTO.getCustomerCode())
//							: 0.0;
//					accountBalanceMap.put(OutStandingFocusDTO.getCustomerCode(), currBal + Double.parseDouble(OutStandingFocusDTO.getBalanceAmount()));
	    	 }
	     }
	     
//	     log.info("Account balance map size {}", accountBalanceMap.size());
//			for (Map.Entry<String, Double> entry : accountBalanceMap.entrySet()) {
//				// entry.getKey(), entry.getValue());
//				accProfiles.stream().filter(a -> a.getCustomerId().equalsIgnoreCase(entry.getKey())).findAny()
//					.ifPresent(ap -> {
//						ap.setClosingBalance(entry.getValue());
//					});
//			}
			log.info("Save account profile size {}", accProfiles.size());
			accountProfileRepository.save(accProfiles);
			log.info("Save receivable size {}", saveReceivablePayable.size());
			bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
			long end = System.nanoTime();
			double elapsedTime = (end - start) / 1000000.0;
			// update sync table
			Optional<SyncOperation> oPsyncOperation = syncOperationRepository.findOneByCompanyIdAndOperationType(companyId, SyncOperationType.RECEIVABLE_PAYABLE);
			SyncOperation syncOperation;
			if(oPsyncOperation.isPresent()){
				syncOperation = oPsyncOperation.get();
				syncOperation.setOperationType(SyncOperationType.RECEIVABLE_PAYABLE);
				syncOperation.setCompleted(true);
				syncOperation.setLastSyncStartedDate(LocalDateTime.now());
				syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
				syncOperation.setLastSyncTime(elapsedTime);
				syncOperation.setCompany(company);
			}else{
				syncOperation = new SyncOperation();
				syncOperation.setOperationType(SyncOperationType.RECEIVABLE_PAYABLE);
				syncOperation.setCompleted(true);
				syncOperation.setLastSyncStartedDate(LocalDateTime.now());
				syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
				syncOperation.setLastSyncTime(elapsedTime);
				syncOperation.setCompany(company);
			}
			System.out.println( "syncCompleted Date : "+syncOperation.getLastSyncCompletedDate());
			syncOperationRepository.save(syncOperation);
			log.info("Sync completed in {} ms", elapsedTime);
	                
	}

	private LocalDate convertDate(String date) {
		if (date != null && date != "" && !date.equalsIgnoreCase("false")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate dateTime = LocalDate.parse(date, formatter);
			return dateTime;
		}
		return LocalDate.now();
	}
	
	public Long dueUpdate(LocalDate referenceDocDate) {
		if (referenceDocDate != null) {
			LocalDate currentDate = LocalDate.now();
			Long differenceInDays = Math
					.abs(ChronoUnit.DAYS.between(currentDate, referenceDocDate));
			return differenceInDays;
		} 
		return 0L;
	}
	
	

	
	
}
