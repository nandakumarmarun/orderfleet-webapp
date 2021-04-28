package com.orderfleet.webapp.web.vendor.odoo.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
			BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			AccountProfileRepository accountProfileRepository,
			ReceivablePayableRepository receivablePayableRepository) {
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

		// create list of customer id value not false
		List<String> customerIds = list.stream().map(a -> a.getCustomer_id())
				.filter(c -> c != null && !c.equalsIgnoreCase("false")).collect(Collectors.toList());

		// create list account profile
		Map<String, Double> accountBalanceMap = new HashMap<>();
		List<AccountProfile> accProfiles = accountProfileRepository.findAccountProfileAndCustomerIds(customerIds);
		List<AccountProfile> accProfilesNotInList = accountProfileRepository
				.findAccountProfileAndCustomerIdsNotIn(customerIds);
		for (OdooOutstandingInvoice ppDto : list) {
			accProfiles.stream().filter(a -> a.getCustomerId().equalsIgnoreCase(ppDto.getCustomer_id())).findAny()
					.ifPresent(ap -> {
						ReceivablePayable receivablePayable = new ReceivablePayable();

						receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
						receivablePayable.setAccountProfile(ap);
						receivablePayable.setCompany(company);
						receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
						receivablePayable.setReferenceDocumentAmount(ppDto.getAmount_original());
						receivablePayable.setReferenceDocumentBalanceAmount(ppDto.getAmount_unreconciled());
						receivablePayable.setReceivablePayableId(String.valueOf(ppDto.getId()));
						receivablePayable.setReferenceDocumentNumber(ppDto.getInvoice_ref());
						receivablePayable.setReferenceDocumentDate(convertDate(ppDto.getDate_original()));
						String dueDate = ppDto.getDate_due();
						if (dueDate != null && dueDate != "" && !dueDate.equalsIgnoreCase("false")) {
							receivablePayable.setBillOverDue(dueUpdate(convertDate(dueDate)));
						} else {
							receivablePayable.setBillOverDue(dueUpdate(convertDate(ppDto.getDate_original())));
						}
						double currBal = accountBalanceMap.containsKey(ppDto.getCustomer_id())
								? accountBalanceMap.get(ppDto.getCustomer_id())
								: 0.0;
						accountBalanceMap.put(ppDto.getCustomer_id(), currBal + ppDto.getAmount_unreconciled());

						saveReceivablePayable.add(receivablePayable);
					});
		}
		log.info("Account balance map size {}", accountBalanceMap.size());
		for (Map.Entry<String, Double> entry : accountBalanceMap.entrySet()) {
			// entry.getKey(), entry.getValue());
			accProfiles.stream().filter(a -> a.getCustomerId().equalsIgnoreCase(entry.getKey())).findAny()
					.ifPresent(ap -> {
						ap.setClosingBalance(entry.getValue());
					});
		}
		log.info("Update Not In CustomerIds Account Profile Closing Balance zero " + accProfilesNotInList.size());
		accProfilesNotInList.forEach(f -> f.setClosingBalance(0));

		accProfiles.addAll(accProfilesNotInList);

		log.info("Save account profile size {}", saveReceivablePayable.size());
		accountProfileRepository.save(accProfiles);
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

	public Long dueUpdate(LocalDate referenceDocDate) {
		if (referenceDocDate != null) {
			LocalDate currentDate = LocalDate.now();
			Long differenceInDays = Math.abs(ChronoUnit.DAYS.between(currentDate, referenceDocDate));
			return differenceInDays;
		}
		return 0L;
	}

	private LocalDate convertDate(String date) {
		if (date != null && date != "" && !date.equalsIgnoreCase("false")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate dateTime = LocalDate.parse(date, formatter);
			return dateTime;
		}
		return null;
	}

}
