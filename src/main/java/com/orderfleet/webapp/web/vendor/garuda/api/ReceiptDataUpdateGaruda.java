package com.orderfleet.webapp.web.vendor.garuda.api;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;

@RestController
@RequestMapping(value = "/api/garuda")
public class ReceiptDataUpdateGaruda {

	private final Logger log = LoggerFactory.getLogger(ReceiptDataUpdateGaruda.class);
	
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;
	
	@PostMapping("/update-receipt-status.json")
	@Timed
	@Transactional
	public ResponseEntity<String> updateOrderStatus(@RequestBody List<String> docNumberServer) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		log.debug("REST request to update Inventory Voucher Header Status (" + company.getLegalName() + ") : {}",
				docNumberServer.size());

		if (!docNumberServer.isEmpty()) {
			int updated = accountingVoucherHeaderRepository.updateAccountingVoucherHeaderDownloadStatusUsingDocumentNumberServer(
					TallyDownloadStatus.COMPLETED, company.getId(), docNumberServer);
			log.debug("updated " + updated + " to Completed");
		}
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
}
