package com.orderfleet.webapp.web.vendor.excel.api;

import java.net.URISyntaxException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.web.rest.dto.DynamicFormDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.ReceiptExcelDTO;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class DynamicFormDownloadController {

	private final Logger log = LoggerFactory.getLogger(DynamicFormDownloadController.class);

	@Autowired
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@RequestMapping(value = "/get-dynamic-documents.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public DynamicFormDTO downloadDynamicDocumentsJson() throws URISyntaxException {
		log.debug("REST request to download dynamic documents : {}");
		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		DynamicFormDTO dynamicFormDTO = new DynamicFormDTO();
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findByCompanyAndStatusOrderByCreatedDateDesc();

		List<String> elementNameToShow = new ArrayList<>();
		// Add four new field if include header is true
		elementNameToShow.add("Account Profile");
		elementNameToShow.add("Address");
		elementNameToShow.add("City");
		elementNameToShow.add("Location");
		elementNameToShow.add("Phone Number");
		elementNameToShow.add("User");
		elementNameToShow.add("Employee");
		elementNameToShow.add("Timestamp");
		elementNameToShow.add("GPSLocation");

		List<Map<Integer, String>> elementValues = new ArrayList<>();
		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
			for (FilledForm filledForm : dynamicDocumentHeader.getFilledForms()) {

				Map<Integer, String> elements = new TreeMap<>();

				String accName = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile() != null
						? dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getName()
						: " ";
				String accPhone1 = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile() != null
						? dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getPhone1()
						: " ";
				String accAddress = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile() != null
						? dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getAddress()
						: " ";
				String accCity = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile() != null
						? dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getCity()
						: " ";
				String accLocation = dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile() != null
						? dynamicDocumentHeader.getExecutiveTaskExecution().getAccountProfile().getLocation()
						: " ";
				elements.put(elementNameToShow.indexOf("Account Profile"), accName);
				elements.put(elementNameToShow.indexOf("Address"), accAddress);
				elements.put(elementNameToShow.indexOf("City"), accCity);
				elements.put(elementNameToShow.indexOf("Location"), accLocation);
				elements.put(elementNameToShow.indexOf("Phone Number"), accPhone1);
				elements.put(elementNameToShow.indexOf("User"), dynamicDocumentHeader.getCreatedBy().getFirstName());
				elements.put(elementNameToShow.indexOf("Employee"), dynamicDocumentHeader.getEmployee().getName());
				elements.put(elementNameToShow.indexOf("Timestamp"),
						fmt.format(dynamicDocumentHeader.getDocumentDate()).toString());
				elements.put(elementNameToShow.indexOf("GPSLocation"),
						dynamicDocumentHeader.getExecutiveTaskExecution().getLocation());

				double abp = 0;
				double indent = 0;
				double compliance = 0;
				for (FilledFormDetail ffd : filledForm.getFilledFormDetails()) {
					if (elementNameToShow.contains(ffd.getFormElement().getName())) {
						// Temporary Code for Changing (only for Indent and
						// Shipment status)

						if (ffd.getFormElement().getName().equals("ABP")) {
							if (ffd.getValue().getClass().equals(Integer.class)) {
								abp = Integer.parseInt(ffd.getValue());
							}
						}
						if (ffd.getFormElement().getName().equals("INDENT")) {
							if (ffd.getValue().getClass().equals(Integer.class)) {
								indent = Integer.parseInt(ffd.getValue());
							}
						}
						if (ffd.getFormElement().getName().equals("%Compliance")) {
							if (abp == 0 || indent == 0) {
								compliance = (indent / abp) * 100;
								double newCompliance = Math.round(compliance * 100.0) / 100.0;
								ffd.setValue(newCompliance + "%");
							}
						}

						// End
						String c = elements.get(elementNameToShow.indexOf(ffd.getFormElement().getName()));
						if (c != null) {
							elements.put(elementNameToShow.indexOf(ffd.getFormElement().getName()),
									c + "," + ffd.getValue());
						} else {
							elements.put(elementNameToShow.indexOf(ffd.getFormElement().getName()), ffd.getValue());
						}

					}
				}

				elementValues.add(elements);
			}
		}

		dynamicFormDTO.setElementNameToShow(elementNameToShow);
		dynamicFormDTO.setElementValues(elementValues);

		/*
		 * if(!receiptDTOs.isEmpty()) { int updated = accountingVoucherHeaderRepository.
		 * updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(
		 * TallyDownloadStatus.PROCESSING, receiptDTOs.stream().map(avh ->
		 * avh.getAccountingVoucherHeaderPid()).collect(Collectors.toList()));
		 * log.debug("updated "+updated+" to PROCESSING"); }
		 */

		return dynamicFormDTO;

	}

	/*
	 * @RequestMapping(value = "/update-receipt-status", method =
	 * RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	 * 
	 * @Timed public ResponseEntity<Void> UpdateReceiptStatus(@Valid @RequestBody
	 * List<String> accountingVoucherHeaderPids) throws URISyntaxException {
	 * log.debug("REST request to update Accounting Voucher Header Status : {}",
	 * accountingVoucherHeaderPids.size()); if
	 * (!accountingVoucherHeaderPids.isEmpty()) { accountingVoucherHeaderRepository.
	 * updateAccountingVoucherHeaderTallyDownloadStatusUsingPidAndCompany(
	 * TallyDownloadStatus.COMPLETED, accountingVoucherHeaderPids); } return new
	 * ResponseEntity<>(HttpStatus.CREATED); }
	 */

}
