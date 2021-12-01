package com.orderfleet.webapp.web.vendor.excel.api;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections4.map.HashedMap;
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
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicFormDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.DynamicExcelDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.ReceiptExcelDTO;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class DynamicFormDownloadController {

	private final Logger log = LoggerFactory.getLogger(DynamicFormDownloadController.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Autowired
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@RequestMapping(value = "/get-dynamic-documents.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<DynamicExcelDTO> downloadDynamicDocumentsJson() throws URISyntaxException {
		log.debug("REST request to download dynamic documents : {}");
		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_135" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get  document by company and status order by create date";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> documentHeaders = dynamicDocumentHeaderRepository
				.findByCompanyAndStatusOrderByCreatedDateDesc();
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
		List<DynamicDocumentHeader> dynamicDocumentHeaders = documentHeaders.stream().distinct()
				.collect(Collectors.toList());

		List<DynamicExcelDTO> dynamicExcelDtos = new ArrayList<>();

	
		for (DynamicDocumentHeader ddh : dynamicDocumentHeaders) {
			DynamicExcelDTO dynamicExcelDto = new DynamicExcelDTO();
			Map<String, String> formDetail = new HashedMap<>();
			dynamicExcelDto.setCustomerAlias(ddh.getExecutiveTaskExecution().getAccountProfile().getAlias());
			dynamicExcelDto.setCustomerName(ddh.getExecutiveTaskExecution().getAccountProfile().getName());
			for (FilledForm filledForms : ddh.getFilledForms()) {
				for (FilledFormDetail ffd : filledForms.getFilledFormDetails()) {
					formDetail.put(ffd.getFormElement().getName(), ffd.getValue());
				}
			}
			dynamicExcelDto.setDynamicDocumentPid(ddh.getPid());
			dynamicExcelDto.setFormDetails(formDetail);
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formatDateTime = ddh.getDocumentDate().format(formatter);
			dynamicExcelDto.setDateTime(formatDateTime);
			dynamicExcelDtos.add(dynamicExcelDto);
		}

		if (!dynamicExcelDtos.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "DYN_QUERY_136" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="Update documentHeader tally download status using Pid and company";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			int updated = dynamicDocumentHeaderRepository
					.updateDynamicDocumentHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							dynamicExcelDtos.stream().map(ddh -> ddh.getDynamicDocumentPid())
									.collect(Collectors.toList()));
			 String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
		                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
						+ description1);
			log.debug("updated " + updated + " to PROCESSING");
		}

		return dynamicExcelDtos;

	}

	@RequestMapping(value = "/update-dynamic-document-status.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)

	@Timed
	public ResponseEntity<Void> UpdateReceiptStatus(@Valid @RequestBody List<String> dynamicDocumentHeaderPids)
			throws URISyntaxException {
		log.debug("REST request to update Dynamic Document Header Status : {}", dynamicDocumentHeaderPids.size());
		if (!dynamicDocumentHeaderPids.isEmpty()) {
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "DYN_QUERY_136" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="Update documentHeader tally download status using Pid and company";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			dynamicDocumentHeaderRepository.updateDynamicDocumentHeaderTallyDownloadStatusUsingPidAndCompany(
					TallyDownloadStatus.COMPLETED, dynamicDocumentHeaderPids);
			 String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
		                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
						+ description1);
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
