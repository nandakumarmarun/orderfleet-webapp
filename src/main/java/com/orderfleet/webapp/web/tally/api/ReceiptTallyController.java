package com.orderfleet.webapp.web.tally.api;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.tally.dto.ReceiptDTO;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;

@RestController
@RequestMapping(value = "/api/tally")
public class ReceiptTallyController {

	private final Logger log = LoggerFactory.getLogger(ReceiptTallyController.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private TallyConfigurationRepository tallyConfigRepository;
	
	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;
	
	TallyConfiguration tallyConfig = null;
							 
	@RequestMapping(value = "/get-receipts-tally", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptDTO> getReceiptJSON(@RequestParam("key")String productKey, 
			@RequestParam("company")String companyName) throws URISyntaxException {
		log.debug("REST request to download receipts : {}");
		
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(productKey,companyName);
		List<ReceiptDTO> receiptList = new ArrayList<>();
		
		if(!tallyConfiguration.isPresent()) {
			log.info("Configuration Not set");
			return receiptList;
		}
		tallyConfig = tallyConfiguration.get();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ACC_QUERY_144" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get all account voucher header receipt for tally";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.getAllReceiptsForTally(tallyConfig.getCompany().getId(), TallyDownloadStatus.PENDING);
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
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader.getAccountingVoucherDetails()) {
				ReceiptDTO receiptDTO = new ReceiptDTO(accountingVoucherDetail);
				receiptDTO.setTallyConfig(new TallyConfigurationDTO(tallyConfig));
				receiptList.add(receiptDTO);
			}
		}
		log.info("Receipts size : " + receiptList.size());
		return receiptList;
		
	}
	
	
	//method used for update receipt status based on tally response : updating variable TallyDownloadStatus enum
	@RequestMapping(value = "/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional
	public ResponseEntity<Void> UpdateReceiptStatusWithTallyResponse(@Valid @RequestHeader("key") String tallyKey,
																	@RequestHeader("company") String companyName,
																	@RequestBody List<String> accountingVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Accounting Voucher Header Download Status (tally) : {}", accountingVoucherHeaderPids.size());
		Optional<TallyConfiguration> tallyConfiguration = tallyConfigRepository.findByProductKeyAndCompanyName(tallyKey,companyName);
		
		if(tallyConfiguration.isPresent()){
			if (!accountingVoucherHeaderPids.isEmpty()) {
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "ACC_QUERY_147" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="updating the accVoucher tally download status using pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				accountingVoucherHeaderRepository
				.updateAccountingVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus.COMPLETED, tallyConfiguration.get().getCompany().getId(), accountingVoucherHeaderPids);
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
			}
			return new ResponseEntity<>(HttpStatus.CREATED);
		}else{
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
