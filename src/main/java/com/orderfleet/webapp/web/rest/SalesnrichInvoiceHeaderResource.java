package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyInvoiceConfigurationService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.SalesnrichInvoiceHeaderService;
import com.orderfleet.webapp.web.rest.dto.BillDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.BillingUserDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyInvoiceConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.SalesnrichInvoiceHeaderDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesnrichInvoice.
 *
 * @author Sarath
 * @since Mar 19, 2018
 *
 */
@Controller
@RequestMapping("/web")
public class SalesnrichInvoiceHeaderResource {

	private final Logger log = LoggerFactory.getLogger(SalesnrichInvoiceHeaderResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private CompanyService companyService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private SalesnrichInvoiceHeaderService salesnrichInvoiceService;

	@Inject
	private CompanyInvoiceConfigurationService companyInvoiceConfigurationService;

	@RequestMapping(value = "/salesnrich-billing", method = RequestMethod.GET)
	@Timed
	@Secured(AuthoritiesConstants.SITE_ADMIN)
	public String getBillingPage(Model model) {
		String invoiceNumber = String.valueOf(salesnrichInvoiceService.getCountOfSalesnrichInvoiceHeader() + 1);
		model.addAttribute("invoiceNumber", invoiceNumber);
		model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		model.addAttribute("billDetailsDTO", new BillDetailsDTO()); // form submission
		return "site_admin/salesnrich-billing";
	}

	@RequestMapping(value = "/salesnrich-billing/{companypid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<BillingUserDTO> getTransactionDetailsByCompany(@PathVariable("companypid") String companyPid,
			@RequestParam("fromDate") LocalDate fromDate, @RequestParam("toDate") LocalDate toDate,
			@RequestParam("filterBy") String filterBy) {
		log.debug("Web request to Get Users by companyPid: {}", companyPid);
		log.debug("Date format : {}", fromDate);

		if (filterBy.equals("CURRENT MONTH")) {
			LocalDate initial = LocalDate.now();
			fromDate = initial.withDayOfMonth(1);
			toDate = initial.withDayOfMonth(initial.lengthOfMonth());

		} else if (filterBy.equals("PREVIOUS MONTH")) {
			LocalDate lastDayPreviousMonth = YearMonth.now().minusMonths(1).atEndOfMonth();
			fromDate = lastDayPreviousMonth.withDayOfMonth(1);
			toDate = lastDayPreviousMonth;
		}

		LocalDateTime startDate = fromDate.atTime(0, 0);
		LocalDateTime endDate = toDate.atTime(23, 59);
		List<User> users = userRepository.findAllByCompanyPidSortedByName(companyPid);
		if (users.isEmpty()) {
			return null;
		}
		List<Long> userIds = users.stream().map(User::getId).collect(Collectors.toList());
		List<String> executiveTaskExecutions = executiveTaskExecutionRepository
				.findUserByCompanyPidAndUserIdInAndDateBetweenOrderByDateDesc(userIds, startDate, endDate);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_113" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get  attendane by companyId userPidIn and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate1 = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate1 + "," + startTime + ",_ ,0 ,START,_," + description);
		List<String> attentances = attendanceRepository.findByCompanyPidAndUserPidInAndDateBetween(userIds, startDate,
				endDate);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate1 = startLCTime.format(DATE_FORMAT);
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
                logger.info(id + "," + endDate1 + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		Map<String, Long> executionMap = null;
		Map<String, Long> attendenceMap = null;
		if (!executiveTaskExecutions.isEmpty()) {
			executionMap = executiveTaskExecutions.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		}
		if (!attentances.isEmpty()) {
			attendenceMap = attentances.stream()
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		}
		List<BillingUserDTO> billingUserDtos = new ArrayList<>();
		for (User u : users) {
			BillingUserDTO billDTO = new BillingUserDTO();
			billDTO.setLogin(u.getLogin());
			if (executionMap != null) {
				billDTO.setExecutionCount(executionMap.getOrDefault(u.getLogin(), 0L));
			}
			if (attendenceMap != null) {
				billDTO.setAttendanceCount(attendenceMap.getOrDefault(u.getLogin(), 0L));
			}
			billingUserDtos.add(billDTO);
		}
		return billingUserDtos;
	}

	@RequestMapping(value = "/salesnrich-billing/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<SalesnrichInvoiceHeaderDTO> saveSalesnrichInvoiceHeader(
			@Valid @RequestBody SalesnrichInvoiceHeaderDTO salesnrichInvoiceHeaderDTO) throws URISyntaxException {

		if (salesnrichInvoiceService.findByInvoiceNumber(salesnrichInvoiceHeaderDTO.getInvoiceNumber()).isPresent()) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("salesnrichInvoice",
					"invoice Number exists", "SalesNrich Billing already in use")).body(null);
		}
		SalesnrichInvoiceHeaderDTO result = salesnrichInvoiceService.save(salesnrichInvoiceHeaderDTO);
		return ResponseEntity.created(new URI("/web/salesnrich-billing/" + result.getInvoiceNumber()))
				.headers(HeaderUtil.createEntityCreationAlert("productProfile", result.getInvoiceNumber().toString()))
				.body(result);
	}

	@RequestMapping(value = "/salesnrich-billing", method = RequestMethod.POST)
	@Timed
	public String showBillPreview(@Valid @ModelAttribute("billDetailsDTO") BillDetailsDTO billDetailsDTO, Model model) {
		/*
		 * if (salesnrichInvoiceService.findByInvoiceNumber(salesnrichInvoiceDTO.
		 * getInvoiceNumber()).isPresent()) { return
		 * ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert(
		 * "salesnrichInvoice", "invoice Number exists",
		 * "SalesNrich Billing already in use")).body(null); } SalesnrichInvoiceDTO
		 * result = salesnrichInvoiceService.save(salesnrichInvoiceDTO); return
		 * ResponseEntity.created(new URI("/web/productProfiles/" +
		 * result.getInvoiceNumber()))
		 * .headers(HeaderUtil.createEntityCreationAlert("productProfile",
		 * result.getInvoiceNumber().toString())) .body(result);
		 */
		Optional<CompanyViewDTO> optionalCompany = companyService.findOneByPid(billDetailsDTO.getCompanyPid());
		if (optionalCompany.isPresent()) {
			model.addAttribute("company", optionalCompany.get());
		}
		model.addAttribute("billDetailsDTO", billDetailsDTO);
		return "print/salesnrich-invoice";
	}

	@RequestMapping(value = "/salesnrich-billing/get-company-invoice-config/{companyPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CompanyInvoiceConfigurationDTO> getcompanyInvoiceConfig(@PathVariable String companyPid) {
		log.debug("Web request to get CompanyInvoiceConfiguration by companyPid : {}", companyPid);
		return companyInvoiceConfigurationService.findOneByCompanyPid(companyPid)
				.map(productProfileDTO -> new ResponseEntity<>(productProfileDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

}
