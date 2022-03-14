package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.AccountProfileGeoLocationTaggingRepository;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileGeoLocationTaggingService;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.impl.InventoryVoucherHeaderServiceImpl;
import com.orderfleet.webapp.web.rest.api.dto.AccountProfileGeoLocationTaggingDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AttachGeoLocationView;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDetailView;
import com.orderfleet.webapp.web.rest.dto.MobileGeoLocationView;
import com.orderfleet.webapp.web.rest.dto.VisitGeoLocationView;

@Controller
@RequestMapping("/web")
public class AttachGeoLocationResource {

	private final Logger log = LoggerFactory.getLogger(AttachGeoLocationResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private UserService userService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private AccountProfileGeoLocationTaggingRepository accountProfileGeoLocationTaggingRepository;

	@Inject
	private AccountProfileGeoLocationTaggingService accountProfileGeoLocationTaggingService;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@RequestMapping(value = "/attach-geo-location", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllAttachGeoLocations(Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			// model.addAttribute("users", userService.findAllByCompany());
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			// model.addAttribute("users", userService.findByUserIdIn(userIds));
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		return "company/attachGeoLocation";
	}

	@RequestMapping(value = "/attach-geo-location/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AttachGeoLocationView> filterExecutiveTaskExecutions(
			@RequestParam("employeePid") String employeePid, @RequestParam("activityPid") String activityPid,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate, @RequestParam String rName) {
		AttachGeoLocationView attachGeoLocationView = new AttachGeoLocationView();
		String userPid = "";
		if ("no".equals(employeePid)) {
			userPid = "no";
		} else {
			Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
			if (opEmployee.isPresent()) {
				userPid = opEmployee.get().getUser().getPid();
			} else {
				userPid = "no";
			}
		}

		if (filterBy.equals("TODAY")) {
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, LocalDate.now(), LocalDate.now(),
					rName);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, yeasterday, yeasterday, rName);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, weekStartDate, LocalDate.now(),
					rName);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, monthStartDate, LocalDate.now(),
					rName);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, fromDateTime, toFateTime, rName);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, fromDateTime, fromDateTime, rName);
		}
		return new ResponseEntity<>(attachGeoLocationView, HttpStatus.OK);
	}

	private AttachGeoLocationView getFilterData(String userPid, String activityPid, String accountPid, LocalDate fDate,
			LocalDate tDate, String rName) {
		AttachGeoLocationView attachGeoLocationView = new AttachGeoLocationView();
		attachGeoLocationView.setrName(rName);
		if ("MOBILE".equals(rName)) {
			attachGeoLocationView
					.setMobileGeoLocationViews(getMobileGeoLocationFilteredData(userPid, accountPid, fDate, tDate));
		} else if ("VISIT".equals(rName)) {
			attachGeoLocationView.setVisitGeoLocationViews(
					getVisitGeoLocationFilteredData(userPid, activityPid, accountPid, fDate, tDate));
		}
		return attachGeoLocationView;
	}

	private List<MobileGeoLocationView> getMobileGeoLocationFilteredData(String userPid, String accountPid,
			LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "APGT_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get by company with accountProfile";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> apGeoLocationTag = accountProfileGeoLocationTaggingRepository
				.findByActivatedAndCompanyWithAccountProfileAndSentDateBetween(fromDate, toDate);
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
		if (apGeoLocationTag.isEmpty()) {
			return Collections.emptyList();
		}
		// filter by user
		List<Long> userIds = new ArrayList<>();
		if (userPid.equals("no")) {
			userIds.addAll(employeeHierarchyService.getCurrentUsersSubordinateIds());
		} else {
			Optional<User> opUser = userRepository.findOneByPid(userPid);
			if (opUser.isPresent()) {
				userIds.addAll(Arrays.asList(opUser.get().getId()));
			}
		}
		if (!userIds.isEmpty()) {
			apGeoLocationTag = apGeoLocationTag.stream().filter(apglt -> userIds.contains((Long) apglt[2]))
					.collect(Collectors.toList());
		}

		// filter by account profile
		if (!accountPid.equals("no")) {
			apGeoLocationTag = apGeoLocationTag.stream().filter(apglt -> accountPid.equals(apglt[4].toString()))
					.collect(Collectors.toList());
		}
		boolean compCon = getCompanyCofig();
		List<MobileGeoLocationView> mobileGeoLocationViews = new ArrayList<>();
		for (Object[] object : apGeoLocationTag) {
			MobileGeoLocationView mobileGeoLocationView = new MobileGeoLocationView();
			mobileGeoLocationView.setPid(object[0].toString());
			mobileGeoLocationView.setSendDate((LocalDateTime) object[1]);
			mobileGeoLocationView.setUserName(object[3].toString());
			mobileGeoLocationView.setAccountPid(object[4].toString());
			if (compCon) {
				mobileGeoLocationView.setAccountProfileName(object[9].toString());
			} else {
				mobileGeoLocationView.setAccountProfileName(object[5].toString());
			}
			mobileGeoLocationView.setLatitude((BigDecimal) object[6]);
			mobileGeoLocationView.setLongitude((BigDecimal) object[7]);
			mobileGeoLocationView.setLocation(object[8] == null ? "" : object[8].toString());
			mobileGeoLocationViews.add(mobileGeoLocationView);
		}
		return mobileGeoLocationViews;
	}

	public boolean getCompanyCofig() {
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if (optconfig.isPresent()) {
			if (Boolean.valueOf(optconfig.get().getValue())) {
				return true;
			}
		}
		return false;
	}

	private List<VisitGeoLocationView> getVisitGeoLocationFilteredData(String userPid, String activityPid,
			String accountPid, LocalDate fDate, LocalDate tDate) {
		List<Long> userIds;
		if (userPid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		} else {
			userIds = Collections.emptyList();
		}
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
		if (userPid.equals("no") && activityPid.equals("no") && accountPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdAndDateBetweenOrderByDateDesc(fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInAndDateBetweenOrderByDateDesc(userIds, fromDate, toDate);
			}
		} else if (!userPid.equals("no") && !activityPid.equals("no") && !accountPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidActivityPidAccountPidAndDateBetweenOrderByDateDesc(userPid, activityPid,
							accountPid, fromDate, toDate);
		} else if (!userPid.equals("no") && activityPid.equals("no") && accountPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidAndDateBetweenOrderByDateDesc(userPid, fromDate, toDate);
		} else if (!activityPid.equals("no") && userPid.equals("no") && accountPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdActivityPidAndDateBetweenOrderByDateDesc(activityPid, fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInActivityPidAndDateBetweenOrderByDateDesc(userIds, activityPid, fromDate,
								toDate);
			}
		} else if (!accountPid.equals("no") && userPid.equals("no") && activityPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdAccountPidAndDateBetweenOrderByDateDesc(accountPid, fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInAccountPidAndDateBetweenOrderByDateDesc(userIds, accountPid, fromDate,
								toDate);
			}
		} else if (!userPid.equals("no") && !activityPid.equals("no") && accountPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidActivityPidAndDateBetweenOrderByDateDesc(userPid, activityPid, fromDate,
							toDate);
		} else if (!userPid.equals("no") && !accountPid.equals("no") && activityPid.equals("no")) {
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByDateDesc(userPid, accountPid, fromDate,
							toDate);
		} else if (!accountPid.equals("no") && !activityPid.equals("no") && userPid.equals("no")) {
			// user under current user
			if (userIds.isEmpty()) {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByCompanyIdActivityPidAccountPidAndDateBetweenOrderByDateDesc(activityPid, accountPid,
								fromDate, toDate);
			} else {
				executiveTaskExecutions = executiveTaskExecutionRepository
						.findAllByUserIdInActivityPidAccountPidAndDateBetweenOrderByDateDesc(userIds, activityPid,
								accountPid, fromDate, toDate);
			}
		}
		return createGeoLocationView(executiveTaskExecutions);
	}

	private List<VisitGeoLocationView> createGeoLocationView(List<ExecutiveTaskExecution> executiveTaskExecutions) {
		List<VisitGeoLocationView> visitGeoLocationViews = new ArrayList<>();
		List<EmployeeProfileDTO> employeeList = employeeProfileService.findAllByCompany();
		boolean compCon = getCompanyCofig();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			VisitGeoLocationView visitGeoLocationView = new VisitGeoLocationView(executiveTaskExecution);
			if (compCon) {
				visitGeoLocationView.setAccountProfileName(visitGeoLocationView.getDescription());
			} else {
				visitGeoLocationView.setAccountProfileName(visitGeoLocationView.getAccountProfileName());
			}
			List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews = new ArrayList<>();
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

			String id = "INV_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description = "get by executive task execution Id";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<Object[]> inventoryVouchers = inventoryVoucherHeaderRepository
					.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
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
			for (Object[] obj : inventoryVouchers) {
				ExecutiveTaskExecutionDetailView executiveTaskExecutionDetailView = new ExecutiveTaskExecutionDetailView(
						obj[0].toString(), obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString());
				executiveTaskExecutionDetailView.setDocumentVolume(Double.valueOf(obj[4].toString()));
				executiveTaskExecutionDetailViews.add(executiveTaskExecutionDetailView);
			}
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "ACC_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 = "get AccVoucher By ExecutiveTaskExecutionId";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<Object[]> accountingVouchers = accountingVoucherHeaderRepository
					.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
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
			logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1
					+ "," + description1);

			for (Object[] obj : accountingVouchers) {
				executiveTaskExecutionDetailViews.add(new ExecutiveTaskExecutionDetailView(obj[0].toString(),
						obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString()));
			}
			DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id11 = "DYN_QUERY_110" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 = "get all documents by Executive Task executionId";
			LocalDateTime startLCTime11 = LocalDateTime.now();
			String startTime11 = startLCTime11.format(DATE_TIME_FORMAT11);
			String startDate11 = startLCTime11.format(DATE_FORMAT11);
			logger.info(id11 + "," + startDate11 + "," + startTime11 + ",_ ,0 ,START,_," + description11);
			List<Object[]> dynamicDocuments = dynamicDocumentHeaderRepository
					.findByExecutiveTaskExecutionId(executiveTaskExecution.getId());
			String flag11 = "Normal";
			LocalDateTime endLCTime11 = LocalDateTime.now();
			String endTime11 = endLCTime11.format(DATE_TIME_FORMAT11);
			String endDate11 = startLCTime11.format(DATE_FORMAT11);
			Duration duration11 = Duration.between(startLCTime11, endLCTime11);
			long minutes11 = duration11.toMinutes();
			if (minutes11 <= 1 && minutes11 >= 0) {
				flag11 = "Fast";
			}
			if (minutes11 > 1 && minutes11 <= 2) {
				flag11 = "Normal";
			}
			if (minutes11 > 2 && minutes11 <= 10) {
				flag11 = "Slow";
			}
			if (minutes11 > 10) {
				flag11 = "Dead Slow";
			}
			logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END,"
					+ flag11 + "," + description11);
			for (Object[] obj : dynamicDocuments) {
				boolean imageFound = false;
				executiveTaskExecutionDetailViews.add(new ExecutiveTaskExecutionDetailView(obj[0].toString(),
						obj[1].toString(), obj[2].toString(), imageFound));
			}
			visitGeoLocationView.setExecutiveTaskExecutionDetailViews(executiveTaskExecutionDetailViews);

			Optional<EmployeeProfileDTO> opEmployee = employeeList.stream()
					.filter(emp -> emp.getUserPid().equals(executiveTaskExecution.getUser().getPid())).findAny();
			if (opEmployee.isPresent()) {
				visitGeoLocationView.setEmployeeName(opEmployee.get().getName());
			} else {
				visitGeoLocationView.setEmployeeName("");
			}
			visitGeoLocationViews.add(visitGeoLocationView);
		}
		return visitGeoLocationViews;
	}

	@RequestMapping(value = "/attach-geo-location/getAccountProfileGeoLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<AccountProfileGeoLocationTaggingDTO>> getAllGeoLocationTaggingByAccountProfile(
			@RequestParam String accountProfilePid) {
		List<AccountProfileGeoLocationTaggingDTO> accountProfileGeoLocationTaggingDTOs = accountProfileGeoLocationTaggingService
				.getAllAccountProfileGeoLocationTaggingByAccountProfile(accountProfilePid);
		return new ResponseEntity<>(accountProfileGeoLocationTaggingDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/attach-geo-location/getOldAccountProfile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> getAccountProfile(
			@RequestParam("accountProfilePid") String accountProfilePid) {
		AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountProfilePid).get();
		return new ResponseEntity<AccountProfileDTO>(accountProfileDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/attach-geo-location/attachAccountProfile", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> attachAccountProfile(
			@RequestParam("accountProfilePid") String accountProfilePid, @RequestParam("latitude") BigDecimal latitude,
			@RequestParam("longitude") BigDecimal longitude, @RequestParam("location") String location) {
		AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountProfilePid).get();
		accountProfileDTO.setLatitude(latitude);
		accountProfileDTO.setLongitude(longitude);
		accountProfileDTO.setLocation(location);
		accountProfileDTO.setGeoTaggingType(GeoTaggingType.WEB_TAGGED_VISIT);
		accountProfileDTO.setGeoTaggedTime(LocalDateTime.now());
		accountProfileDTO.setGeoTaggedUserLogin(SecurityUtils.getCurrentUserLogin());
		accountProfileDTO = accountProfileService.update(accountProfileDTO);
		return new ResponseEntity<>(accountProfileDTO, HttpStatus.OK);

	}

	@RequestMapping(value = "/attach-geo-location/saveGeoLocation", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AccountProfileDTO> attachAccountProfile(@RequestParam("geoLocationPid") String geoLocationPid,
			@RequestParam("accountProfilePid") String accountProfilePid) {
		AccountProfileDTO accountProfileDTO = accountProfileService.findOneByPid(accountProfilePid).get();
		AccountProfileGeoLocationTaggingDTO accountProfileGeoLocationTaggingDTO = accountProfileGeoLocationTaggingService
				.findOneByPid(geoLocationPid).get();
		accountProfileDTO.setLatitude(accountProfileGeoLocationTaggingDTO.getLatitude());
		accountProfileDTO.setLongitude(accountProfileGeoLocationTaggingDTO.getLongitude());
		accountProfileDTO.setLocation(accountProfileGeoLocationTaggingDTO.getLocation());
		accountProfileDTO.setGeoTaggingType(GeoTaggingType.WEB_TAGGED_MOBILE);
		accountProfileDTO.setGeoTaggedTime(LocalDateTime.now());
		accountProfileDTO.setGeoTaggedUserLogin(SecurityUtils.getCurrentUserLogin());
		accountProfileDTO = accountProfileService.update(accountProfileDTO);
		return new ResponseEntity<AccountProfileDTO>(accountProfileDTO, HttpStatus.OK);
	}

	@RequestMapping(value = "/attach-geo-location/download-attach-geo-Location-xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void DownloadExel(@RequestParam("employeePid") String employeePid,
			@RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam String rName, HttpServletResponse response) {
		AttachGeoLocationView attachGeoLocationView = new AttachGeoLocationView();
		String userPid = "";
		if ("no".equals(employeePid)) {
			userPid = "no";
		} else {
			Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
			if (opEmployee.isPresent()) {
				userPid = opEmployee.get().getUser().getPid();
			} else {
				userPid = "no";
			}
		}

		if (filterBy.equals("TODAY")) {
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, LocalDate.now(), LocalDate.now(),
					rName);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, yeasterday, yeasterday, rName);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, weekStartDate, LocalDate.now(),
					rName);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, monthStartDate, LocalDate.now(),
					rName);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, fromDateTime, toFateTime, rName);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			attachGeoLocationView = getFilterData(userPid, activityPid, accountPid, fromDateTime, fromDateTime, rName);
		}
		buildExcelDocument(attachGeoLocationView, response);
	}

	private void buildExcelDocument(AttachGeoLocationView attachGeoLocationView, HttpServletResponse response) {
		log.debug("Downloading Excel report");
		String excelFileName = "attachGeoLocation" + ".xls";
		String sheetName = "Sheet1";
		String[] headerColumns = { "Date", "Employee", "AccountProfile", "Latitude","Location","Longitude" };
		try (HSSFWorkbook workbook = new HSSFWorkbook()) {
			HSSFSheet worksheet = workbook.createSheet(sheetName);
			createHeaderRow(worksheet, headerColumns);
			createReportRows(worksheet, attachGeoLocationView);
// Resize all columns to fit the content size
			for (int i = 0; i < headerColumns.length; i++) {
				worksheet.autoSizeColumn(i);
			}
			response.setHeader("Content-Disposition", "inline; filename=" + excelFileName);
			response.setContentType("application/vnd.ms-excel");
//Writes the report to the output stream
			ServletOutputStream outputStream = response.getOutputStream();
			worksheet.getWorkbook().write(outputStream);
			outputStream.flush();
		} catch (IOException ex) {
			log.error("IOException on downloading Product profiles {}", ex.getMessage());
		}
	}

	private void createReportRows(HSSFSheet worksheet, AttachGeoLocationView attachGeoLocationView) {

		List<MobileGeoLocationView> mobileGeoLocationViews = attachGeoLocationView.getMobileGeoLocationViews();
		List<VisitGeoLocationView> visitGeoLocationViews = attachGeoLocationView.getVisitGeoLocationViews();
		int rowNum = 1;

		/*
		 * CreationHelper helps us create instances of various things like DataFormat,
		 * Hyperlink, RichTextString etc, in a format (HSSF, XSSF) independent way
		 */
		HSSFCreationHelper createHelper = worksheet.getWorkbook().getCreationHelper();
		// Create Cell Style for formatting Date
		HSSFCellStyle dateCellStyle = worksheet.getWorkbook().createCellStyle();
		dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy hh:mm:ss"));
		// Create Other rows and cells with Sales data

		if (!mobileGeoLocationViews.isEmpty()) {
			for (MobileGeoLocationView pp : mobileGeoLocationViews) {

				LocalDateTime localDateTime = pp.getSendDate();
				Instant i = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
				Date date = Date.from(i);

				HSSFRow row = worksheet.createRow(rowNum++);
				Cell sendDateCell = row.createCell(0);
				sendDateCell.setCellValue(date);
				sendDateCell.setCellStyle(dateCellStyle);
				row.createCell(1).setCellValue(pp.getUserName());
				row.createCell(2).setCellValue(pp.getAccountProfileName().toString());
				row.createCell(3).setCellValue(pp.getLatitude().toString());
				row.createCell(4).setCellValue(pp.getLocation());
				row.createCell(5).setCellValue(pp.getLongitude().toString());
				System.out.println("username" + pp.getUserName());
				System.out.println("latitude" + pp.getLatitude().toString());
				System.out.println("acccountprofile" + pp.getAccountProfileName().toString());
				System.out.println("Location" + pp.getLocation());
				System.out.println("date"+date);
			}
		}

		if (!visitGeoLocationViews.isEmpty()) {
			for (VisitGeoLocationView pp : visitGeoLocationViews) {
				HSSFRow row = worksheet.createRow(rowNum++);
				LocalDateTime localDateTime = pp.getCreatedDate();		
				Instant i = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
				Date date = Date.from(i);
				row.createCell(0).setCellValue(pp.getEmployeeName());
				row.createCell(1).setCellValue(pp.getLatitude().toString());
				row.createCell(2).setCellValue(pp.getAccountProfileName().toString());
				row.createCell(3).setCellValue(pp.getLocation());
				row.createCell(4).setCellValue(pp.getLongitude().toString());
			}
		}

	}

	private void createHeaderRow(HSSFSheet worksheet, String[] headerColumns) {
		// Create a Font for styling header cells
		Font headerFont = worksheet.getWorkbook().createFont();
		headerFont.setFontName("Arial");
		headerFont.setBold(true);
		headerFont.setFontHeightInPoints((short) 14);
		headerFont.setColor(IndexedColors.RED.getIndex());
		// Create a CellStyle with the font
		HSSFCellStyle headerCellStyle = worksheet.getWorkbook().createCellStyle();
		headerCellStyle.setFont(headerFont);
		// Create a Row
		HSSFRow headerRow = worksheet.createRow(0);
		// Create cells
		for (int i = 0; i < headerColumns.length; i++) {
			HSSFCell cell = headerRow.createCell(i);
			cell.setCellValue(headerColumns[i]);
			cell.setCellStyle(headerCellStyle);
		}
	}

}
