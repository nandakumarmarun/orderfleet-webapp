package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;
import com.orderfleet.webapp.repository.AccountingVoucherDetailRepository;
import com.orderfleet.webapp.repository.BestPerformanceConfigurationRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.BestPerformerDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;

/**
 * WEB controller for managing the best performer.
 * 
 * @author Sarath
 * @since Mar 26, 2018
 *
 */

@Controller
@RequestMapping("/web")
public class BestPerformerResource {

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private AccountingVoucherDetailRepository accountingVoucherDetailRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private BestPerformanceConfigurationRepository bestPerformanceConfigurationRepository;

	@RequestMapping(value = "/best-performer", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getBestPerformers(Model model) {
		return "company/best-performer";
	}

	@RequestMapping(value = "/best-performer/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public BestPerformerDTO filterBestPerformerReport(@RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		if (filterBy.equals("TODAY")) {
			return getFilterData(LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			return getFilterData(yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			return getFilterData(weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			return getFilterData(monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			return getFilterData(fromDateTime, toDateTime);
		}
		return null;
	}

	private BestPerformerDTO getFilterData(LocalDate fDate, LocalDate tDate) {
		List<EmployeeProfileDTO> employeeProfileDTOs = employeeProfileService.findAllByCompany();
		List<Document> salesDocuments = bestPerformanceConfigurationRepository
				.findAllDocumentsByBestPerformanceType(BestPerformanceType.SALES);
		List<Document> receiptDocuments = bestPerformanceConfigurationRepository
				.findAllDocumentsByBestPerformanceType(BestPerformanceType.RECEIPT);

		BestPerformerDTO bestPerformerDTO = new BestPerformerDTO();
		if (!salesDocuments.isEmpty()) {
			Set<Long> docIds = salesDocuments.stream().map(Document::getId).collect(Collectors.toSet());
			List<Object[]> ivDetails = inventoryVoucherDetailRepository.findByDocumentIdInAndDateBetween(docIds,
					fDate.atTime(0, 0), tDate.atTime(23, 59));
			if (!ivDetails.isEmpty()) {
				for (EmployeeProfileDTO emp : employeeProfileDTOs) {
					bestPerformerDTO.getSalesPerformer().put(emp.getName(),
							getSumOfAmountOrQuantity(ivDetails, emp.getPid(), fDate, tDate));
				}
			}
		}
		if (!receiptDocuments.isEmpty()) {
			Set<Long> docIds = receiptDocuments.stream().map(Document::getId).collect(Collectors.toSet());
			List<Object[]> avDetails = accountingVoucherDetailRepository
					.findByDocumentIdInAndDateBetweenOrderByCreatedDateDesc(docIds, fDate.atTime(0, 0),
							tDate.atTime(23, 59));
			if (!avDetails.isEmpty()) {
				for (EmployeeProfileDTO emp : employeeProfileDTOs) {
					bestPerformerDTO.getReceiptPerformer().put(emp.getName(),
							getSumOfAmountOrQuantity(avDetails, emp.getPid(), fDate, tDate));
				}
			}
		}
		// sort
		bestPerformerDTO.setSalesPerformer(bestPerformerDTO.getSalesPerformer().entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		bestPerformerDTO.setReceiptPerformer(bestPerformerDTO.getReceiptPerformer().entrySet().stream()
				.sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new)));
		return bestPerformerDTO;
	}

	private Double getSumOfAmountOrQuantity(List<Object[]> avDetails, String employeePid, LocalDate startDate,
			LocalDate endDate) {
		return avDetails.parallelStream().filter(avd -> employeePid.equals(avd[0].toString()))
				.filter(avd -> (((LocalDateTime) avd[2]).toLocalDate().compareTo(startDate) >= 0)
						&& (((LocalDateTime) avd[2]).toLocalDate().compareTo(endDate) <= 0))
				.collect(Collectors.summingDouble(avd -> (Double) avd[3]));
	}
}
