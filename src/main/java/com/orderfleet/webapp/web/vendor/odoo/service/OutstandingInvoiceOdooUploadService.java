package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooOutstandingInvoice;

@Service
public class OutstandingInvoiceOdooUploadService {
	private final Logger log = LoggerFactory.getLogger(OutstandingInvoiceOdooUploadService.class);
	
	private final CompanyRepository companyRepository;
	
	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;
	
	private final AccountProfileRepository accountProfileRepository;
	
	private final ReceivablePayableRepository receivablePayableRepository;
	
	

	public OutstandingInvoiceOdooUploadService(CompanyRepository companyRepository,
			BulkOperationRepositoryCustom bulkOperationRepositoryCustom, AccountProfileRepository accountProfileRepository, ReceivablePayableRepository receivablePayableRepository) {
		super();
		this.companyRepository = companyRepository;
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.accountProfileRepository = accountProfileRepository;
		this.receivablePayableRepository = receivablePayableRepository;
	}



	@Transactional
	public void saveOutstandingInvoice(final List<OdooOutstandingInvoice> list) {

		log.info("Saving Outstanding Invoice...");
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		receivablePayableRepository.deleteByCompanyId(company.getId());
		
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();
		
		for (OdooOutstandingInvoice ppDto : list) {
			
			String customer_id = ppDto.getCustomer_id();
			if (customer_id != null && !customer_id.equalsIgnoreCase("false")) {
				Optional<AccountProfile> accountProfile = accountProfileRepository.findOneByCustomerId(customer_id);
				if(accountProfile.isPresent()) {
					ReceivablePayable receivablePayable = new ReceivablePayable();
					receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
					receivablePayable.setAccountProfile(accountProfile.get());
					receivablePayable.setCompany(company);
					receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
					receivablePayable.setReferenceDocumentAmount(ppDto.getAmount_original());
					receivablePayable.setReferenceDocumentBalanceAmount(ppDto.getAmount_unreconciled());
					receivablePayable.setReferenceDocumentNumber(ppDto.getInvoice_ref());
					receivablePayable.setReferenceDocumentDate(convertDate(ppDto.getDate_original()));
					String dueDate = ppDto.getDate_due();
					if (dueDate!= null && dueDate != "" && !dueDate.equalsIgnoreCase("false")) {
						receivablePayable.setBillOverDue(ChronoUnit.DAYS.between(LocalDate.parse(dueDate), LocalDate.now()));
					}
					saveReceivablePayable.add(receivablePayable);	
				}
			}	
		}
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
		

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}
	
	private LocalDate convertDate(String date) {
		if (date!= null && date != "" && !date.equalsIgnoreCase("false")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dateTime = LocalDate.parse(date, formatter);
			return dateTime;	
		}
		return null;
	}
	
}
