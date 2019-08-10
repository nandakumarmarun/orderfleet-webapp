package com.orderfleet.webapp.web.vendor.excel.api;

import java.net.URISyntaxException;
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
import com.orderfleet.webapp.web.rest.dto.DynamicFormDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.DynamicExcelDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.ReceiptExcelDTO;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class DynamicFormDownloadController {

	private final Logger log = LoggerFactory.getLogger(DynamicFormDownloadController.class);

	@Autowired
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@RequestMapping(value = "/get-dynamic-documents.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<DynamicExcelDTO> downloadDynamicDocumentsJson() throws URISyntaxException {
		log.debug("REST request to download dynamic documents : {}");
		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findByCompanyAndStatusOrderByCreatedDateDesc();
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
			int updated = dynamicDocumentHeaderRepository
					.updateDynamicDocumentHeaderTallyDownloadStatusUsingPidAndCompany(TallyDownloadStatus.PROCESSING,
							dynamicExcelDtos.stream().map(ddh -> ddh.getDynamicDocumentPid())
									.collect(Collectors.toList()));
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

			dynamicDocumentHeaderRepository.updateDynamicDocumentHeaderTallyDownloadStatusUsingPidAndCompany(
					TallyDownloadStatus.COMPLETED, dynamicDocumentHeaderPids);

		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

}
