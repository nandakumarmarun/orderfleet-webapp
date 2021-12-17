package com.orderfleet.webapp.web.rest;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.PrimarySecondaryDocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.web.rest.report.dto.VisitReportDTO;

@Controller
@RequestMapping("/web")
public class VisitReportResource {

	private final Logger log = LoggerFactory.getLogger( VisitReportResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private PrimarySecondaryDocumentRepository primarySecondaryDocumentRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@RequestMapping(value = "/visits-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String visitReport(Model model) {
		return "company/visit-report/visit-report-common";
	}

	@RequestMapping(value = "/visits-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<VisitReportDTO> filterInventoryVouchers(@RequestParam("employeePid") String employeePid,
			@RequestParam("filterBy") String filterBy, @RequestParam LocalDate fromDate, @RequestParam LocalDate toDate,
			@RequestParam boolean inclSubordinate) {
		List<Document> documents = primarySecondaryDocumentRepository.findAllDocumentsByCompanyId();
		if (documents.isEmpty()) {
			return null;
		}
		if (filterBy.equals(VisitReportResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;
		} else if (filterBy.equals(VisitReportResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(VisitReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(VisitReportResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(VisitReportResource.CUSTOM)) {
		}
		return new ResponseEntity<>(
				createVisitReportByEmployeeDocumentAndDate(documents, employeePid, fromDate, toDate, inclSubordinate),
				HttpStatus.OK);
	}

	private VisitReportDTO createVisitReportByEmployeeDocumentAndDate(List<Document> documents, String employeePid,
			LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = getUserIdsUnderEmployee(employeePid, inclSubordinate);
		if (userIds.isEmpty()) {
			return null;
		}
		List<Object[]> etExtecutions = executiveTaskExecutionRepository.findByUserIdInAndDateBetween(userIds, fromDate,
				toDate);
		if (etExtecutions.isEmpty()) {
			return null;
		}

		// group employee wise
		Map<String, List<Long>> employeeWiseGrouped = etExtecutions.stream().collect(Collectors.groupingBy(
				obj -> (String) obj[1], TreeMap::new, Collectors.mapping(ete -> (Long) ete[0], Collectors.toList())));
		// get all inventory vouchers under executions
		List<Long> eteIds = employeeWiseGrouped.values().stream().flatMap(List::stream).collect(Collectors.toList());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "INV_QUERY_128" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by doc and executive task executionIdIn";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> ivhDtos = inventoryVoucherHeaderRepository
				.findByDocumentsAndExecutiveTaskExecutionIdIn(documents, eteIds);
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
	
		// Object ivhDtosvolume
		// =inventoryVoucherHeaderRepository.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdIn(documents,fromDate,toDate,userIds);
		Set<String> ivhPids = new HashSet<>();

		for (Object[] ivh : ivhDtos) {
			ivhPids.add(ivh[4].toString());
		}
		List<InventoryVoucherDetail> ivDetails = inventoryVoucherDetailRepository
				.findAllByInventoryVoucherHeaderPidIn(ivhPids);

		List<List<String>> reportValues = new ArrayList<>();
		for (Map.Entry<String, List<Long>> entry : employeeWiseGrouped.entrySet()) {
			List<String> values = new ArrayList<>();
			String employeeName = entry.getKey();
			long totalVisit = entry.getValue().size();
			long saleVisit = ivhDtos.stream().filter(obj -> ((String) obj[0]).equals(employeeName)).count();
			double totalVolume = ivDetails.stream()
					.filter(ivd -> ivd.getInventoryVoucherHeader().getEmployee().getName().equals(employeeName))
					.mapToDouble(InventoryVoucherDetail::getVolume).sum();
			long unProductiveCount = totalVisit - saleVisit;

			values.add(employeeName);
			values.add(totalVisit + "");
			values.add(saleVisit + "");
			values.add(unProductiveCount + "");
			values.add(totalVolume + "");

			// values.add(ivhDtosvolume +"");
			// values.add(tvolume+"");
			reportValues.add(values);
		}

		return new VisitReportDTO(Arrays.asList("Visits", "Sales Order"), reportValues);
	}

	private List<Long> getUserIdsUnderEmployee(String employeePid, boolean inclSubordinate) {
		List<Long> userIds = Collections.emptyList();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
				Set<Long> uniqueIds = new HashSet<>();
				if (!dashboardUserIds.isEmpty()) {
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid.equals(sid)) {
									uniqueIds.add(sid);
								}
							}
						}
					} else {
						userIds = new ArrayList<>(dashboardUserIds);
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}
			} else {
				if (userIds.isEmpty()) {
					List<User> users = userRepository.findAllByCompanyId();
					userIds = users.stream().map(User::getId).collect(Collectors.toList());
				}
			}
		} else {
			if (inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds = Arrays.asList(opEmployee.get().getUser().getId());
				}
			}
		}
		return userIds;
	}

}
