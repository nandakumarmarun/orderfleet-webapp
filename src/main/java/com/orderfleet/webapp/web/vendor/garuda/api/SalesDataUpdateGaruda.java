package com.orderfleet.webapp.web.vendor.garuda.api;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.security.SecurityUtils;

@RestController
@RequestMapping(value = "/api/garuda")
public class SalesDataUpdateGaruda {
	
	private final Logger log = LoggerFactory.getLogger(SalesDataUpdateGaruda.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	
	@PostMapping("/update-order-status.json")
	@Timed
	@Transactional
	public ResponseEntity<String> updateOrderStatus(@RequestBody List<String> invoiceNumbers) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		log.debug("REST request to update Inventory Voucher Header Status (" + company.getLegalName() + ") : {}",
				invoiceNumbers.size());

		if (!invoiceNumbers.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "INV_QUERY_182" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="update Iv Header TallyDownload Status Using Doc No server AndCompanyId";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderDownloadStatusUsingDocumentNumberServerAndCompanyId(
					TallyDownloadStatus.COMPLETED, company.getId(), invoiceNumbers);
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
			log.debug("updated " + updated + " to Completed");
		}
		return new ResponseEntity<String>("Success", HttpStatus.OK);
	}
}
