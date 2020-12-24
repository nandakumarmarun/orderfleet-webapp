package com.orderfleet.webapp.web.vendor.sap.pravesh.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.sap.pravesh.dto.ResponseBodySapPraveshOutstanding;

@Service
public class OutstandingSapPraveshUploadService {
	private final Logger log = LoggerFactory.getLogger(OutstandingSapPraveshUploadService.class);

	private final CompanyRepository companyRepository;

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final AccountProfileRepository accountProfileRepository;

	private final ReceivablePayableRepository receivablePayableRepository;

	public OutstandingSapPraveshUploadService(CompanyRepository companyRepository,
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
	public void saveOutstandingInvoice(final List<ResponseBodySapPraveshOutstanding> list) {

		log.info("Saving Outstanding Invoice...");
		long start = System.nanoTime();

		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		receivablePayableRepository.deleteByCompanyId(company.getId());
		Set<ReceivablePayable> saveReceivablePayable = new HashSet<>();

		// create list of customer id value not false
		List<String> customerIds = list.stream().map(a -> a.getCustomerId())
				.filter(c -> c != null && !c.equalsIgnoreCase("false")).collect(Collectors.toList());

		List<String> dealerIds = list.stream().map(a -> a.getDealerCode())
				.filter(c -> c != null && !c.equalsIgnoreCase("false")).collect(Collectors.toList());

		log.info("Customer Ids size {}", customerIds.size());

		// create list account profile
		Map<String, Double> accountBalanceMap = new HashMap<>();
		List<AccountProfile> accProfiles = accountProfileRepository.findAccountProfileAndCustomerIds(customerIds);
		List<AccountProfile> dealerProfiles = accountProfileRepository.findAccountProfileAndCustomerIds(dealerIds);

		for (ResponseBodySapPraveshOutstanding ppDto : list) {
			Optional<AccountProfile> opCustomers = accProfiles.stream()
					.filter(a -> a.getCustomerId().equalsIgnoreCase(ppDto.getCustomerId())).findAny();

			Optional<AccountProfile> opDealers = dealerProfiles.stream()
					.filter(a -> a.getCustomerId().equalsIgnoreCase(ppDto.getDealerCode())).findAny();

			if (opCustomers.isPresent() && opDealers.isPresent()) {

				ReceivablePayable receivablePayable = new ReceivablePayable();
				receivablePayable.setPid(ReceivablePayableService.PID_PREFIX + RandomUtil.generatePid());
				receivablePayable.setAccountProfile(opCustomers.get());
				receivablePayable.setSupplierAccountProfile(opDealers.get());
				receivablePayable.setCompany(company);
				receivablePayable.setReceivablePayableType(ReceivablePayableType.Receivable);
				receivablePayable.setReferenceDocumentAmount(ppDto.getOrderTotal());
				receivablePayable.setReferenceDocumentBalanceAmount(ppDto.getBalance());
				receivablePayable.setReferenceDocumentNumber(ppDto.getOrderNum());
				receivablePayable.setReferenceDocumentDate(convertDate(ppDto.getDocDate()));
				saveReceivablePayable.add(receivablePayable);

	             double currBal = accountBalanceMap.containsKey(ppDto.getCustomerId()) ? accountBalanceMap.get(ppDto.getCustomerId()) : 0.0;
	             accountBalanceMap.put(ppDto.getCustomerId(), currBal + ppDto.getBalance());
			}

		}
		log.info("Account balance map size {}", accountBalanceMap.size());
		for (Map.Entry<String, Double> entry : accountBalanceMap.entrySet()) {
			// entry.getKey(), entry.getValue());
			accProfiles.stream().filter(a -> a.getCustomerId().equalsIgnoreCase(entry.getKey())).findAny()
				.ifPresent(ap -> {
					ap.setClosingBalance(entry.getValue());
				});
		}
		log.info("Save account profile size {}", saveReceivablePayable.size());
		accountProfileRepository.save(accProfiles);
		log.info("Save receivable size {}", saveReceivablePayable.size());
		bulkOperationRepositoryCustom.bulkSaveReceivablePayables(saveReceivablePayable);
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
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
