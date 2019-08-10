package com.orderfleet.webapp.web.vendor.excel.api;

import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.web.vendor.excel.dto.ReceiptExcelDTO;


@RestController
@RequestMapping(value = "/api/excel/v1")
public class ReceiptDownloadController {

	private final Logger log = LoggerFactory.getLogger(ReceiptDownloadController.class);
	
	@Autowired
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;
	
	@RequestMapping(value = "/get-receipts.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ReceiptExcelDTO> downloadReceiptsJson() throws URISyntaxException {
		log.debug("REST request to download receipts : {}");
		List<ReceiptExcelDTO> receiptDTOs = new ArrayList<>();
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findByCompanyAndStatusOrderByCreatedDateDesc();
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
			for (AccountingVoucherDetail accountingVoucherDetail : accountingVoucherHeader
					.getAccountingVoucherDetails()) {
				if (accountingVoucherDetail.getAccountingVoucherAllocations().isEmpty()) {
					ReceiptExcelDTO receiptDTO = new ReceiptExcelDTO(accountingVoucherDetail);
					receiptDTOs.add(receiptDTO);
				} else {
					for (AccountingVoucherAllocation accountingVoucherAllocation : accountingVoucherDetail
							.getAccountingVoucherAllocations()) {
						ReceiptExcelDTO receiptDTO = new ReceiptExcelDTO(accountingVoucherAllocation);
						receiptDTO.setHeaderAmount(accountingVoucherHeader.getTotalAmount());
						receiptDTO.setNarrationMessage(accountingVoucherAllocation.getAccountingVoucherDetail().getRemarks());
						receiptDTOs.add(receiptDTO);
					}
				}
			}
		}
		
		if(!receiptDTOs.isEmpty()) {
			int updated = accountingVoucherHeaderRepository.
					updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING, receiptDTOs.stream().map(avh -> avh.getAccountingVoucherHeaderPid()).collect(Collectors.toList()));
			log.debug("updated "+updated+" to PROCESSING");
		}
		return receiptDTOs;
	}
	
	
	@RequestMapping(value = "/update-receipt-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> UpdateReceiptStatus(@Valid @RequestBody List<String> accountingVoucherHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Accounting Voucher Header Status : {}", accountingVoucherHeaderPids.size());
			if (!accountingVoucherHeaderPids.isEmpty()) {
				accountingVoucherHeaderRepository.updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.COMPLETED,  accountingVoucherHeaderPids);
			}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
}
