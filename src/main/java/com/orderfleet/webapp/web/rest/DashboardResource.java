package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.DashboardAttendance;
import com.orderfleet.webapp.domain.DashboardChartItem;
import com.orderfleet.webapp.domain.DashboardItem;
import com.orderfleet.webapp.domain.DashboardItemGroup;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.Funnel;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ChartType;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DashboardItemType;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TargetType;
import com.orderfleet.webapp.domain.enums.TaskPlanType;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.CustomerTimeSpentRepository;
import com.orderfleet.webapp.repository.DashboardAttendanceUserRepository;
import com.orderfleet.webapp.repository.DashboardChartItemRepository;
import com.orderfleet.webapp.repository.DashboardGroupDashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardItemGroupRepository;
import com.orderfleet.webapp.repository.DashboardItemGroupUserRepository;
import com.orderfleet.webapp.repository.DashboardItemRepository;
import com.orderfleet.webapp.repository.DashboardItemUserRepository;
import com.orderfleet.webapp.repository.DashboardNotificationRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.repository.FunnelRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupDocumentRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupProductRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.repository.SalesTargetReportSettingSalesTargetBlockRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountingVoucherHeaderService;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.DashboardUserService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.chart.dto.BarChartDTO;
import com.orderfleet.webapp.web.rest.chart.dto.FunnelChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardHeaderSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardUserDataDTO;
import com.orderfleet.webapp.web.websocket.dto.DashboardAttendanceDTO;

/**
 * Web controller for managing Dash board.
 * 
 * @author Muhammed Riyas T
 * @since August 24, 2016
 * 
 * @author shaheer
 * @since July 11, 2018
 */
@Controller
@RequestMapping(value = "/web")
public class DashboardResource {

	private final Logger log = LoggerFactory.getLogger(DashboardResource.class);

	@Inject
	private DashboardUserService dashboardUserService;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private AttendanceService attendanceService;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DashboardNotificationRepository dashboardNotificationRepository;

	@Inject
	private DashboardItemRepository dashboardItemRepository;

	@Inject
	private CompanySettingRepository companySettingRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private AccountingVoucherHeaderService accountingVoucherHeaderService;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private UserService userService;

	@Inject
	private LocationHierarchyService locationHierarchyService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private DashboardItemUserRepository dashboardItemUserRepository;

	@Inject
	private DashboardAttendanceUserRepository dashboardAttendanceUserRepository;

	@Inject
	private ServletContext servletContext;

	@Inject
	private CustomerTimeSpentRepository customerTimeSpentRepository;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private SalesTargetGroupDocumentRepository salesTargetGroupDocumentRepository;

	@Inject
	private SalesTargetGroupProductRepository salesTargetGroupProductRepository;

	@Inject
	private SalesTargetReportSettingSalesTargetBlockRepository salesTargetReportSettingSalesTargetBlockRepository;

	@Inject
	private FunnelRepository funnelRepository;

	@Inject
	private StageHeaderRepository stageHeaderRepository;

	@Inject
	private DashboardChartItemRepository dashboardChartItemRepository;
	
	@Inject
	private DashboardItemGroupRepository dashboardItemGroupRepository;
	
	@Inject
	private DashboardGroupDashboardItemRepository dashboardGroupDashboardItemRepository;
	
	@Inject
	private DashboardItemGroupUserRepository dashboardItemGroupUserRepository;
	
	@Inject
	private UserRepository userRepository;
	
	/**
	 * GET /web/dashboard : get dashboard page.
	 * @throws JsonProcessingException 
	 * @throws JSONException 
	 */
	@RequestMapping(value = { "/", "/dashboard" }, method = RequestMethod.GET)
	public String dashboard(Model model) throws JSONException {
		log.info("Web request to get dashboard common page");
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		model.addAttribute("companyId", companyId);
		User user = userService.getCurrentUser();
		if (user != null && user.getEnableWebsocket()) {
			model.addAttribute("enableWebsocket", true);
		} else {
			model.addAttribute("enableWebsocket", false);
		}
		Optional<CompanyConfiguration> companyConfiguration = companyConfigurationRepository
				.findByCompanyIdAndName(companyId, CompanyConfig.THEME);
		if (companyConfiguration.isPresent()) {
			servletContext.setAttribute("currentcss", companyConfiguration.get().getValue());
		} else {
			servletContext.setAttribute("currentcss", "white.css");
		}
		List<DashboardItemGroup> dbItemGroups = dashboardItemGroupRepository.findAllByCompanyId();
		if(!dbItemGroups.isEmpty()) {
			JSONArray jsonArr = new JSONArray();
			for (DashboardItemGroup dashboardItemGroup : dbItemGroups) {
				JSONObject jo = new JSONObject();
				jo.put("id", dashboardItemGroup.getId());
				jo.put("name", dashboardItemGroup.getName());
				jsonArr.put(jo);
			}
			model.addAttribute("dbItemGroups", jsonArr.toString());
		}
		
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority("ROLE_EXECUTIVE"));
		Long userCount = userRepository.countByCompanyPidAndAuthoritiesIn(companyRepository
				.findOne(companyId).getPid(), authorities);
		
		if(SecurityUtils.isCurrentUserInRole("ROLE_OP_ADMIN")){
			if(userCount > 0){
				return "redirect:/web/orderpro/dashboard";
			}else{
				return "redirect:/web/orderpro/add-mobile-users";
			}
		}else{
			return "company/dashboard";
		}
	}

	@RequestMapping(value = "/dashboard/attendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardAttendanceDTO> getAttendanceData(
			@RequestParam(value = "date", required = false) LocalDate date,
			@RequestParam(value = "territoryId", required = false) Long territoryId) {
		log.info("Web request to get dashboard attendance");
		long totalUsers;
		long attendedUsers;

		User user = userService.getCurrentUser();
		if (territoryId != null && territoryId > 0) {
			List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(territoryId);
			Set<Long> uniqueUserIds = employeeProfileLocationRepository.findEmployeeUserIdsByLocationIdIn(locationIds);
			List<Long> userIds = new ArrayList<>(uniqueUserIds);
			totalUsers = dashboardUserRepository.countByUserIdIn(userIds);
			attendedUsers = attendanceRepository.countByUserIdInAndDateBetween(userIds, date.atTime(0, 0),
					date.atTime(23, 59));
		} else {
			if (user.getShowAllUsersData()) {
				Set<Long> dashboardUserIds = dashboardUserRepository.findUserIdsByCompanyId();
				totalUsers = dashboardUserIds.size();
				attendedUsers = attendanceRepository.countByUserIdInAndDateBetween(dashboardUserIds, date.atTime(0, 0),
						date.atTime(23, 59));
			} else {
				List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
				Set<Long> dbUserIds = dashboardUserRepository.findUserIdsByUserIdIn(userIds);
				totalUsers = dbUserIds.size(); 
				attendedUsers = attendanceRepository.countByUserIdInAndDateBetween(dbUserIds, date.atTime(0, 0),
						date.atTime(23, 59));
			}
		}
		return new ResponseEntity<>(new DashboardAttendanceDTO(totalUsers, attendedUsers), HttpStatus.OK);
	}

	@Transactional(readOnly = true)
	@RequestMapping(value = "/dashboard/summary/attendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardHeaderSummaryDTO> getDashboardAttendanceSummaryData(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		log.info("Web request to get dashboard  summary");
		return new ResponseEntity<>(loadDashboardAttendanceHeader(date.atTime(0, 0), date.atTime(23, 59)),
				HttpStatus.OK);
	}

	@Transactional(readOnly = true)
	@RequestMapping(value = "/dashboard/summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardHeaderSummaryDTO> getDashboardSummaryData(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return new ResponseEntity<>(loadDashboardHeaderSummary(date.atTime(0, 0), date.atTime(23, 59)), HttpStatus.OK);
	}

	@Transactional(readOnly = true)
	@RequestMapping(value = "/dashboard/summary/chart", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardChartDTO> getDashboardSummaryFunnelChartData(
			@RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		return new ResponseEntity<>(loadDashboardSummaryHeaderChart(date.atTime(0, 0), date.atTime(23, 59)),
				HttpStatus.OK);
	}

	@Transactional(readOnly = true)
	@RequestMapping(value = "/dashboard/users-summary", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DashboardUserDataDTO<DashboardSummaryDTO>>> getDashboardUsersData(
			@RequestParam(value = "date", required = false) LocalDate date,
			@RequestParam(value = "territoryId", required = false) Long territoryId) {
		log.info("Web request to get dashboard user wise data");
		return new ResponseEntity<>(loadDashboardUserWiseData(date, territoryId), HttpStatus.OK);
	}
	
	@Transactional(readOnly = true)
	@RequestMapping(value = "/dashboard/users-summary/dashboarditem-group/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DashboardUserDataDTO<DashboardSummaryDTO>>> getDashboardUsersDataByDashboardItemGroup(@PathVariable Long id,
			@RequestParam(value = "date", required = false) LocalDate date) {
		return new ResponseEntity<>(loadDashboardUserWiseDataByDashboardItemGroup(date, id), HttpStatus.OK);
	}

	@RequestMapping(value = "/dashboard/delay-time", method = RequestMethod.GET)
	public @ResponseBody int getDelayTime() {
		CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
		if (companySetting != null && companySetting.getDashboardConfiguration() != null) {
			return companySetting.getDashboardConfiguration().getDelayTime();
		}
		return 0;
	}

	@RequestMapping(value = "/dashboard/update-delay-time", method = RequestMethod.POST)
	public @ResponseBody int updateDelayTime(@RequestParam int delayTime) {
		CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
		if (companySetting == null) {
			companySetting = new CompanySetting();
			companySetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		}
		companySetting.getDashboardConfiguration().setDelayTime(delayTime);
		companySettingRepository.save(companySetting);
		return 0;
	}
	
	private DashboardHeaderSummaryDTO loadDashboardHeaderSummary(LocalDateTime from, LocalDateTime to) {
		// dash board item configured to user
		List<DashboardItem> dashboardItems = dashboardItemUserRepository
				.findDashboardItemByUserLogin(SecurityUtils.getCurrentUserLogin());
		if (dashboardItems.isEmpty()) {
			dashboardItems = dashboardItemRepository.findAllByCompanyId();
		}
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDateTime weekStartDate = from.with(fieldISO, 1);
		LocalDateTime monthStartDate = from.withDayOfMonth(1);

		List<DashboardSummaryDTO> daySummaryDatas = new ArrayList<>();
		List<DashboardSummaryDTO> weekSummaryDatas = new ArrayList<>();
		List<DashboardSummaryDTO> monthSummaryDatas = new ArrayList<>();
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		for (DashboardItem dashboardItem : dashboardItems) {
			//for removing target row from Dash board UI summary, because it takes long time when a full month data loading
			if("Target".equals(dashboardItem.getName())) {
				continue;
			}
			daySummaryDatas.add(makeDashboardSummaryTile(dashboardItem, from, to, userIds, null));
			weekSummaryDatas.add(makeDashboardSummaryTile(dashboardItem, weekStartDate, to, userIds, null));
			monthSummaryDatas.add(makeDashboardSummaryTile(dashboardItem, monthStartDate, to, userIds, null));
		}
		DashboardHeaderSummaryDTO dashboardDTO = new DashboardHeaderSummaryDTO();
		dashboardDTO.setDaySummaryDatas(daySummaryDatas);
		dashboardDTO.setWeekSummaryDatas(weekSummaryDatas);
		dashboardDTO.setMonthSummaryDatas(monthSummaryDatas);
		return dashboardDTO;
	}

	private DashboardSummaryDTO makeDashboardSummaryTile(DashboardItem dashboardItem, LocalDateTime from, LocalDateTime to,List<Long> userIds, String userPid) {
		DashboardSummaryDTO dashboardSummaryDto = new DashboardSummaryDTO();
		dashboardSummaryDto.setDashboardItemPid(dashboardItem.getPid());
		dashboardSummaryDto.setLabel(dashboardItem.getName());
		dashboardSummaryDto.setDashboardItemType(dashboardItem.getDashboardItemType());
		dashboardSummaryDto.setTaskPlanType(dashboardItem.getTaskPlanType());
		dashboardSummaryDto.setNumberCircle(false);
		if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
			assignActivityAchievedAndScheduled(dashboardItem, dashboardSummaryDto, from, to, userIds, userPid);
		} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
			if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
				assignInventoryCountAndAmount(dashboardItem, dashboardSummaryDto, from, to, userIds, userPid);
			} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
				assignAccountingVoucherCountAndAmount(dashboardItem, dashboardSummaryDto, from, to,userIds, userPid);
			} else if (dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
				assignDynamicVoucherCountAndAmount(dashboardItem, dashboardSummaryDto, from, to,userIds, userPid);
			}
		} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.PRODUCT)) {
			assignProductAmountAndVolume(dashboardItem, dashboardSummaryDto, from, to, userIds, userPid);
		} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.TARGET)) {
			assignTargetBlockAchievedAndPlanned(dashboardItem, dashboardSummaryDto, from, to, userIds, userPid);
		}
		return dashboardSummaryDto;
	}

	private DashboardChartDTO loadDashboardSummaryHeaderChart(LocalDateTime from, LocalDateTime to) {
		List<Funnel> funnels = funnelRepository.findAllByCompanyId();
		DashboardChartDTO dashboardChartDTO = new DashboardChartDTO();
		dashboardChartDTO.setChartType(ChartType.FUNNEL);
		if (funnels.isEmpty()) {
			dashboardChartDTO.setChartType(ChartType.BAR);
			Optional<DashboardChartItem> opDashboardChartItem = dashboardChartItemRepository
					.findOneByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
			DashboardItem dashboardItem;
			if (opDashboardChartItem.isPresent()) {
				dashboardChartDTO.setChartLabel(opDashboardChartItem.get().getName());
				dashboardItem = opDashboardChartItem.get().getDashboardItem();
			} else {
				dashboardItem = dashboardItemRepository
						.findTopByCompanyIdOrderBySortOrderAsc(SecurityUtils.getCurrentUsersCompanyId());
				dashboardChartDTO.setChartLabel(dashboardItem.getName());
			}
			dashboardChartDTO.setBarChartDtos(loadDashboardSummaryBarChart(from, to, dashboardItem));
		} else {
//			dashboardChartDTO.setFunnelChartDtos(makeDashboardSummaryFunnelChart(from.minusMonths(3), to, funnels));
			from =  LocalDate.of(2018, Month.JANUARY, 1).atTime(0,0);
			dashboardChartDTO.setFunnelChartDtos(makeDashboardSummaryFunnelChart(from, to, funnels));
			dashboardChartDTO.setChartLabel("Sales Funnel");
		}
		return dashboardChartDTO;
	}

	private List<BarChartDTO> loadDashboardSummaryBarChart(LocalDateTime from, LocalDateTime to,
			DashboardItem dashboardItem) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		List<LocalDate> monthDates = getMonthsBetween(from.minusMonths(5).toLocalDate(), to.toLocalDate());
		if (dashboardItem.getDashboardItemType().equals(DashboardItemType.ACTIVITY)) {
			return makeActivityBasedBarChart(dashboardItem, monthDates, userIds);
		} else if (dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT)) {
			return makeDocumentBasedBarChart(dashboardItem, monthDates, userIds);
		}
		return Collections.emptyList();
	}

	private List<BarChartDTO> makeActivityBasedBarChart(DashboardItem dashboardItem, List<LocalDate> monthDates,
			List<Long> userIds) {
		List<BarChartDTO> barChartDtos = new ArrayList<>();
		if (!userIds.isEmpty()) {
			// filter activities by user activities
			Set<Activity> userActivities = userActivityService.findActivitiesByActivatedTrueAndUserIdIn(userIds);
			dashboardItem.getActivities().retainAll(userActivities);
		}
		for (LocalDate localDate : monthDates) {
			LocalDate start = localDate.withDayOfMonth(1);
			LocalDate end = localDate.withDayOfMonth(localDate.lengthOfMonth());
			long[] achvdSchd = getActivityAchievedAndScheduled(dashboardItem, start.atTime(0, 0), end.atTime(23, 59),
					userIds, null);
			barChartDtos.add(new BarChartDTO(localDate.getMonth().name().substring(0, 3), achvdSchd[0] + ""));
		}
		return barChartDtos;
	}

	private List<BarChartDTO> makeDocumentBasedBarChart(DashboardItem dashboardItem, List<LocalDate> monthDates,
			List<Long> userIds) {
		List<BarChartDTO> barChartDtos = new ArrayList<>();
		for (LocalDate localDate : monthDates) {
			LocalDate start = localDate.withDayOfMonth(1);
			LocalDate end = localDate.withDayOfMonth(localDate.lengthOfMonth());
			if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
				if (!userIds.isEmpty()) {
					// filter documents by user documents
					Set<Document> userDocuments = inventoryVoucherHeaderService.findDocumentsByUserIdIn(userIds);
					dashboardItem.getDocuments().retainAll(userDocuments);
				}
				Object[] countAndAmount = (Object[]) getInventoryCountAndAmount(dashboardItem, start.atTime(0, 0),
						end.atTime(23, 59), userIds, null);
				if (countAndAmount != null) {
					barChartDtos.add(new BarChartDTO(localDate.getMonth().name().substring(0, 3), String.valueOf(countAndAmount[1] == null ? 0 : (double) countAndAmount[1])));
				} else {
					barChartDtos.add(new BarChartDTO(localDate.getMonth().name().substring(0, 3), ""));
				}
			} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
				if (!userIds.isEmpty()) {
					// filter documents by user documents
					Set<Document> userDocuments = accountingVoucherHeaderService.findDocumentsByUserIdIn(userIds);
					dashboardItem.getDocuments().retainAll(userDocuments);
				}
				Object[] countAndAmount = (Object[]) getAccountingVoucherCountAndAmount(dashboardItem, start.atTime(0, 0),
						end.atTime(23, 59), userIds, null);
				if (countAndAmount != null) {
					barChartDtos
					.add(new BarChartDTO(localDate.getMonth().name().substring(0, 3), String.valueOf(countAndAmount[1] == null ? 0 : (double) countAndAmount[1])));	
				} else {
					barChartDtos.add(new BarChartDTO(localDate.getMonth().name().substring(0, 3), ""));
				}
			} else if (dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
				if (!userIds.isEmpty()) {
					// filter documents by user documents
					Set<Document> userDocuments = dynamicDocumentHeaderService.findDocumentsByUserIdIn(userIds);
					dashboardItem.getDocuments().retainAll(userDocuments);
				}
				long count = getDynamicDocumentCount(dashboardItem, start.atTime(0, 0), end.atTime(23, 59), userIds, null);
				barChartDtos.add(new BarChartDTO(localDate.getMonth().name().substring(0, 3), String.valueOf(count)));
			}
		}
		return barChartDtos;
	}

	private List<FunnelChartDTO> makeDashboardSummaryFunnelChart(LocalDateTime from, LocalDateTime to,
			List<Funnel> funnels) {
		List<FunnelChartDTO> funnelChartDtos = new ArrayList<>();
		List<Object[]> fNameAndCounts = stageHeaderRepository.findFunnelByDateBetween(from, to);
		for (Funnel funnel : funnels) {
			Optional<Object[]> opFNameAndCount = fNameAndCounts.stream().filter(fc -> funnel.getName().equals(fc[0].toString())).findFirst();
			if(opFNameAndCount.isPresent()) {
				String amount = opFNameAndCount.get()[2] == null ? "0" : ((BigDecimal)opFNameAndCount.get()[2]).toBigInteger().toString();
				funnelChartDtos.add(new FunnelChartDTO(funnel.getName() + "("+ opFNameAndCount.get()[1].toString() +")", amount));
			} else {
				funnelChartDtos.add(new FunnelChartDTO(funnel.getName() + "(0)", "0"));
			}
		}
		return funnelChartDtos;
	}
	
	private List<DashboardUserDataDTO<DashboardSummaryDTO>> loadDashboardUserWiseDataByDashboardItemGroup(LocalDate date,
			Long dbItemGroupId) {
		List<DashboardItem> dashboardItems = dashboardGroupDashboardItemRepository
				.findDashboardItemByDashboardGroupId(dbItemGroupId);
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		List<Object[]> dbItemGroupUsers;
		if(userIds.isEmpty()) {
			dbItemGroupUsers = dashboardItemGroupUserRepository.findUserByDashboardItemGroupIdAndUserIdIn(dbItemGroupId);
		} else {
			dbItemGroupUsers = dashboardItemGroupUserRepository.findUserByDashboardItemGroupIdAndUserIdIn(dbItemGroupId, userIds);	
		}
		List<DashboardUserDataDTO<DashboardSummaryDTO>> dashboardUserDatas = new ArrayList<>();
		for (Object[] userObj : dbItemGroupUsers) {
			UserDTO user = new UserDTO();
			user.setPid(userObj[0].toString());
			user.setLogin(userObj[1].toString());
			dashboardUserDatas.add(makeDashboardUserDataDTO(date, user, dashboardItems));
		}
		return dashboardUserDatas;
	}

	private List<DashboardUserDataDTO<DashboardSummaryDTO>> loadDashboardUserWiseData(LocalDate date,
			Long parentLocationId) {
		// dash board item configured to user
		List<DashboardItem> dashboardItems = dashboardItemUserRepository
				.findDashboardItemByUserLogin(SecurityUtils.getCurrentUserLogin());
		if (dashboardItems.isEmpty()) {
			dashboardItems = dashboardItemRepository.findAllByCompanyId();
		}
		List<DashboardUserDataDTO<DashboardSummaryDTO>> dashboardUserDatas = new ArrayList<>();
		List<UserDTO> dashboardUsers = getDashboardUsers(parentLocationId);
		for (UserDTO user : dashboardUsers) {
			dashboardUserDatas.add(makeDashboardUserDataDTO(date, user, dashboardItems));
		}
		return dashboardUserDatas;
	}
	
	private DashboardUserDataDTO<DashboardSummaryDTO> makeDashboardUserDataDTO(LocalDate date, UserDTO user, List<DashboardItem> dashboardItems) {
		DashboardUserDataDTO<DashboardSummaryDTO> dashboardUserData = new DashboardUserDataDTO<>(attendanceService, employeeProfileRepository, customerTimeSpentRepository);
		dashboardUserData.setUserPid(user.getPid());
		dashboardUserData.setEmployeeData(dashboardUserData, user.getPid());
		dashboardUserData.setAttendanceStatus(dashboardUserData, user.getPid(), date);
		dashboardUserData.setCustomerTimeSpent(dashboardUserData, user.getPid());
		ExecutiveTaskExecution executiveTaskExecution = executiveTaskExecutionRepository
						.findTop1ByUserPidAndDateBetweenOrderByDateDesc(user.getPid(), date.atTime(0, 0), date.atTime(23, 59));
		dashboardUserData.setLastExecutionDetails(dashboardUserData, executiveTaskExecution);
		// set user dash board item wise summary
		dashboardUserData.setUserSummaryData(createSingleUserSummaryData(dashboardItems, date.atTime(0, 0), date.atTime(23, 59),
				user.getPid(), executiveTaskExecution));
		Long count = dashboardNotificationRepository.countByCreatedByAndReadFalseAndCreatedDateBetween(user.getLogin(),
				date.atTime(0, 0), date.atTime(23, 59));
		dashboardUserData.setNotificationCount(count == null ? 0 : count.intValue());
		
		return dashboardUserData;
	}

	private List<UserDTO> getDashboardUsers(Long parentLocationId) {
		List<UserDTO> dashboardUsers = new ArrayList<>();
		if (parentLocationId != null && parentLocationId > 0) {
			List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(parentLocationId);
			Set<Long> uniqueUserIds = employeeProfileLocationRepository.findEmployeeUserIdsByLocationIdIn(locationIds);
			// find in both
			if (!uniqueUserIds.isEmpty()) {
				dashboardUsers = dashboardUserService.findUsersByUserIdIn(new ArrayList<>(uniqueUserIds));
			}
		} else {
			User currentUser = userService.getCurrentUser();
			if (currentUser.getShowAllUsersData()) {
				dashboardUsers = dashboardUserService.findUsersByCompanyId();
			} else {
				// get employee hierarchy users
				List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
				dashboardUsers = dashboardUserService.findUsersByUserIdIn(userIds);
			}
		}
		return dashboardUsers;
	}
	
	/**
	 * User wise Activities,Sales Order and Receipts summary in a date
	 * 
	 * @param from
	 * @param to
	 * @param userPid
	 * @return
	 */
	private List<DashboardSummaryDTO> createSingleUserSummaryData(List<DashboardItem> dashboardItems, LocalDateTime from,
			LocalDateTime to, String userPid, ExecutiveTaskExecution executiveTaskExecution) {
		List<DashboardSummaryDTO> dashboardSummaryDatas = new ArrayList<>();
		List<Long> userIds = Collections.emptyList();
		for (DashboardItem dashboardItem : dashboardItems) {
			DashboardSummaryDTO dashboardItemSummary = makeDashboardSummaryTile(dashboardItem, from, to,userIds, userPid);
			// check if it is in last transaction (for number circle)
			putNumberCircle(dashboardItem, dashboardItemSummary, executiveTaskExecution);
			dashboardSummaryDatas.add(dashboardItemSummary);
		}
		return dashboardSummaryDatas;
	}
	
	private void putNumberCircle(DashboardItem dashboardItem, DashboardSummaryDTO dashboardItemSummary,
			ExecutiveTaskExecution executiveTaskExecution) {
		if (dashboardItem.getDashboardItemType().equals(DashboardItemType.DOCUMENT) && executiveTaskExecution != null) {
			if (dashboardItem.getDocumentType().equals(DocumentType.INVENTORY_VOUCHER)) {
				putNumberCircleOnInventoryDocItem(dashboardItem.getDocuments(), dashboardItemSummary, executiveTaskExecution.getPid());
			} else if (dashboardItem.getDocumentType().equals(DocumentType.ACCOUNTING_VOUCHER)) {
				putNumberCircleOnAccountingDocItem(dashboardItem.getDocuments(), dashboardItemSummary, executiveTaskExecution.getPid());
			} else if (dashboardItem.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
				putNumberCircleOnDynamicDocItem(dashboardItem.getDocuments(), dashboardItemSummary, executiveTaskExecution.getPid());
			}
		}
	}
	
	private void putNumberCircleOnInventoryDocItem(Set<Document> documents, DashboardSummaryDTO dashboardItemSummary, String taskExecutionPid) {
		List<InventoryVoucherHeader> inventoryVoucherHeaders = inventoryVoucherHeaderRepository
				.findAllByExecutiveTaskExecutionPid(taskExecutionPid);
		if (!inventoryVoucherHeaders.isEmpty()) {
			for (Document document : documents) {
				if (inventoryVoucherHeaders.stream()
						.anyMatch(ih -> ih.getDocument().getPid().equals(document.getPid()))) {
					dashboardItemSummary.setNumberCircle(true);
				}
			}
		}
	}
	
	private void putNumberCircleOnAccountingDocItem(Set<Document> documents, DashboardSummaryDTO dashboardItemSummary, String taskExecutionPid) {
		List<AccountingVoucherHeader> accountingVoucherHeaders = accountingVoucherHeaderRepository
				.findAllByExecutiveTaskExecutionPid(taskExecutionPid);
		if (!accountingVoucherHeaders.isEmpty()) {
			for (Document document : documents) {
				if (accountingVoucherHeaders.stream()
						.anyMatch(ah -> ah.getDocument().getPid().equals(document.getPid()))) {
					dashboardItemSummary.setNumberCircle(true);
				}
			}
		}
	}
	
	private void putNumberCircleOnDynamicDocItem(Set<Document> documents, DashboardSummaryDTO dashboardItemSummary, String taskExecutionPid) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByExecutiveTaskExecutionPid(taskExecutionPid);
		if (!dynamicDocumentHeaders.isEmpty()) {
			for (Document document : documents) {
				if (dynamicDocumentHeaders.stream()
						.anyMatch(dh -> dh.getDocument().getPid().equals(document.getPid()))) {
					dashboardItemSummary.setNumberCircle(true);
				}
			}
		}
	}
	
	private void assignActivityAchievedAndScheduled(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDto, LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		if (!userIds.isEmpty()) {
			// filter activities by user activities
			Set<Activity> userActivities = userActivityService.findActivitiesByActivatedTrueAndUserIdIn(userIds);
			dashboardItem.getActivities().retainAll(userActivities);
		}
		long[] achvdSchd = getActivityAchievedAndScheduled(dashboardItem, from, to, userIds, userPid);
		dashboardSummaryDto.setAchieved(achvdSchd[0]);
		dashboardSummaryDto.setScheduled(achvdSchd[1]);
	}

	private long[] getActivityAchievedAndScheduled(DashboardItem dashboardItem, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		List<Activity> activities = new ArrayList<>(dashboardItem.getActivities());
		Collections.sort(activities, (Activity s1, Activity s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
		if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
			return getPlannedActivityAchievedAndScheduled(activities, from, to, userIds, userPid);
		} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
			return getUnPlannedActivityAchievedAndScheduled(activities, from, to, userIds, userPid);
		} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
			long achieved = 0;
			long scheduled = 0;
			// get both activities
			if (userPid == null) {
				if (!userIds.isEmpty()) {
					scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityInAndUserIdIn(from, to,
							activities, userIds);
					achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivitiesAndUserIdIn(from, to,
							activities, userIds);
				} else {
					scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityIn(from, to,
							activities);
					achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivities(from, to, activities);
				}
			} else {
				scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityInAndUserPid(from, to,
						activities, userPid);
				achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivities(from, to, activities,
						userPid);
			}
			return new long[] { achieved, scheduled };
		}
		return new long[] { 0, 0 };
	}

	private long[] getPlannedActivityAchievedAndScheduled(List<Activity> activities, LocalDateTime from,
			LocalDateTime to, List<Long> userIds, String userPid) {
		long achieved = 0;
		long scheduled = 0;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityInAndUserIdIn(from, to,
						activities, userIds);
				achieved = executiveTaskExecutionRepository
						.countByDateBetweenAndActivitiesAndUserIdInAndTaskPlanIsNotNull(from, to, activities, userIds);
			} else {
				scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityIn(from, to, activities);
				achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivitiesAndTaskPlanIsNotNull(from,
						to, activities);
			}
		} else {
			scheduled = executiveTaskPlanRepository.countByPlannedDateBetweenAndActivityInAndUserPid(from, to,
					activities, userPid);
			achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivitiesAndUserAndTaskPlanIsNotNull(from,
					to, activities, userPid);
		}
		return new long[] { achieved, scheduled };
	}

	private long[] getUnPlannedActivityAchievedAndScheduled(List<Activity> activities, LocalDateTime from,
			LocalDateTime to, List<Long> userIds, String userPid) {
		long achieved = 0;
		long scheduled = 0;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				achieved = executiveTaskExecutionRepository
						.countByDateBetweenAndActivitiesAndUserIdInAndTaskPlanIsNull(from, to, activities, userIds);
			} else {
				achieved = executiveTaskExecutionRepository.countByDateBetweenAndActivitiesAndTaskPlanIsNull(from,
						to, activities);
			}
		} else {
			achieved = executiveTaskExecutionRepository
					.countByDateBetweenAndActivitiesAndUserAndTaskPlanIsNull(from, to, activities, userPid);
		}
		return new long[] { achieved, scheduled };
	}
	
	private void assignInventoryCountAndAmount(DashboardItem dashboardItem, DashboardSummaryDTO dashboardSummaryDto,
			LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		if (!userIds.isEmpty()) {
			// filter documents by user documents
			Set<Document> userDocuments = inventoryVoucherHeaderService.findDocumentsByUserIdIn(userIds);
			dashboardItem.getDocuments().retainAll(userDocuments);
		}
		Object[] countAndAmount = (Object[]) getInventoryCountAndAmount(dashboardItem, from, to, userIds, userPid);
		if (countAndAmount != null) {
			dashboardSummaryDto.setCount(countAndAmount[0] == null ? 0L : (long)countAndAmount[0]);
			dashboardSummaryDto.setAmount(countAndAmount[1] == null ? 0d : (double)countAndAmount[1]);
		}
	}

	private Object getInventoryCountAndAmount(DashboardItem dashboardItem, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		Collections.sort(documents, (Document s1, Document s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
		Object obj = null;
		if (!documents.isEmpty()) {
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				if (userPid == null) {
					if (!userIds.isEmpty()) {
						obj = inventoryVoucherHeaderRepository
								.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdIn(documents, from, to,
										userIds);
					} else {
						obj = inventoryVoucherHeaderRepository
								.getCountAmountAndVolumeByDocumentsAndDateBetween(documents, from, to);
					}
				} else {
					obj = inventoryVoucherHeaderRepository
							.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUser(documents, from, to, userPid);
				}
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				obj = getPlannedInventoryCountAndAmount(documents, from, to, userIds, userPid);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				obj = getUnPlannedInventoryCountAndAmount(documents, from, to, userIds, userPid);
			}
		}
		return obj;
	}
	
	private Object getPlannedInventoryCountAndAmount(List<Document> documents, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		Object obj = null;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
								documents, from, to, userIds);
			} else {
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndTaskPlanIsNotNull(documents, from,
								to);
			}
		} else {
			obj = inventoryVoucherHeaderRepository
					.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserAndTaskPlanIsNotNull(documents,
							from, to, userPid);
		}
		return obj;
	}

	private Object getUnPlannedInventoryCountAndAmount(List<Document> documents, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		Object obj = null;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNull(documents,
								from, to, userIds);
			} else {
				obj = inventoryVoucherHeaderRepository
						.getCountAmountAndVolumeByDocumentsAndDateBetweenAndTaskPlanIsNull(documents, from, to);
			}
		} else {
			obj = inventoryVoucherHeaderRepository
					.getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserAndTaskPlanIsNull(documents, from,
							to, userPid);
		}
		return obj;
	}
	
	private void assignAccountingVoucherCountAndAmount(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDto, LocalDateTime from, LocalDateTime to,List<Long> userIds, String userPid) {
		if (!userIds.isEmpty()) {
			// filter documents by user documents
			Set<Document> userDocuments = accountingVoucherHeaderService.findDocumentsByUserIdIn(userIds);
			dashboardItem.getDocuments().retainAll(userDocuments);
		}
		Object[] countAndAmount = (Object[]) getAccountingVoucherCountAndAmount(dashboardItem, from, to, userIds, userPid);
		if (countAndAmount != null) {
			dashboardSummaryDto.setCount(countAndAmount[0] == null ? 0L : (long)countAndAmount[0]);
			dashboardSummaryDto.setAmount(countAndAmount[1] == null ? 0d : (double)countAndAmount[1]);
		}
	}

	private Object getAccountingVoucherCountAndAmount(DashboardItem dashboardItem, LocalDateTime from,
			LocalDateTime to, List<Long> userIds, String userPid) {
		Object obj = null;
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		if (!documents.isEmpty()) {
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				if (userPid == null) {
					if (!userIds.isEmpty()) {
						obj = accountingVoucherHeaderRepository
								.getCountAndAmountByDocumentsAndDateBetweenAndUserIdIn(documents, from, to, userIds);
					} else {
						obj = accountingVoucherHeaderRepository.getCountAndAmountByDocumentsAndDateBetween(documents,
								from, to);
					}
				} else {
					obj = accountingVoucherHeaderRepository.getCountAndAmountByDocumentsAndDateBetweenAndUser(documents,
							from, to, userPid);
				}
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				obj = getPlannedAccountingCountAndAmount(documents, from, to, userIds, userPid);				
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				obj = getUnPlannedAccountingCountAndAmount(documents, from, to, userIds, userPid);
			}
		}
		return obj;
	}
	
	private Object getPlannedAccountingCountAndAmount(List<Document> documents, LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		Object obj = null;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				obj = accountingVoucherHeaderRepository
						.getCountAndAmountByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(documents,
								from, to, userIds);
			} else {
				obj = accountingVoucherHeaderRepository
						.getCountAndAmountByDocumentsAndDateBetweenAndTaskPlanIsNotNull(documents, from, to);
			}
		} else {
			obj = accountingVoucherHeaderRepository
					.getCountAndAmountByDocumentsAndDateBetweenAndUserAndTaskPlanIsNotNull(documents, from, to,
							userPid);
		}
		return obj;
	}
	
	private Object getUnPlannedAccountingCountAndAmount(List<Document> documents, LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		Object obj = null;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				obj = accountingVoucherHeaderRepository
						.getCountAndAmountByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNull(documents, from,
								to, userIds);
			} else {
				obj = accountingVoucherHeaderRepository
						.getCountAndAmountByDocumentsAndDateBetweenAndTaskPlanIsNull(documents, from, to);
			}
		} else {
			obj = accountingVoucherHeaderRepository
					.getCountAndAmountByDocumentsAndDateBetweenAndUserAndTaskPlanIsNull(documents, from, to,
							userPid);
		}
		return obj;
	}
	
	private void assignDynamicVoucherCountAndAmount(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDto, LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		if (!userIds.isEmpty()) {
			// filter documents by user documents
			Set<Document> userDocuments = dynamicDocumentHeaderService.findDocumentsByUserIdIn(userIds);
			dashboardItem.getDocuments().retainAll(userDocuments);
		}
		long count = getDynamicDocumentCount(dashboardItem, from, to, userIds, userPid);
		dashboardSummaryDto.setCount(count);
	}
	
	private long getDynamicDocumentCount(DashboardItem dashboardItem, LocalDateTime from,
			LocalDateTime to, List<Long> userIds, String userPid) {
		Long count = 0L;
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		if (!documents.isEmpty()) {
			if (dashboardItem.getTaskPlanType().equals(TaskPlanType.BOTH)) {
				// get both document
				if (userPid == null) {
					if (!userIds.isEmpty()) {
						count = dynamicDocumentHeaderRepository
								.countByCreatedDateBetweenAndDocumentInAndCreatedByIdIn(from, to, documents, userIds);
					} else {
						count = dynamicDocumentHeaderRepository.countByCreatedDateBetweenAndDocumentIn(from, to,
								documents);
					}
				} else {
					count = dynamicDocumentHeaderRepository.countByCreatedDateBetweenAndDocumentInAndCreatedByPid(from,
							to, documents, userPid);
				}
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.PLANNED)) {
				count = getPlannedDynamicDocumentCount(documents, from, to, userIds, userPid);
			} else if (dashboardItem.getTaskPlanType().equals(TaskPlanType.UN_PLANNED)) {
				count = getUnPlannedDynamicDocumentCount(documents, from, to, userIds, userPid);
			}
		}
		return count;
	}
	
	private long getPlannedDynamicDocumentCount(List<Document> documents, LocalDateTime from,
			LocalDateTime to, List<Long> userIds, String userPid) {
		long count;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndCreatedByIdInAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
								from, to, documents, userIds);
			} else {
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
								from, to, documents);
			}
		} else {
			count = dynamicDocumentHeaderRepository
					.countByCreatedDateBetweenAndDocumentInAndCreatedByPidAndExecutiveTaskExecutionExecutiveTaskPlanIsNotNull(
							from, to, documents, userPid);
		}
		return count;
	}
	
	private long getUnPlannedDynamicDocumentCount(List<Document> documents, LocalDateTime from,
			LocalDateTime to, List<Long> userIds, String userPid) {
		long count;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndCreatedByIdInAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
								from, to, documents, userIds);
			} else {
				count = dynamicDocumentHeaderRepository
						.countByCreatedDateBetweenAndDocumentInAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
								from, to, documents);
			}
		} else {
			count = dynamicDocumentHeaderRepository
					.countByCreatedDateBetweenAndDocumentInAndCreatedByPidAndExecutiveTaskExecutionExecutiveTaskPlanIsNull(
							from, to, documents, userPid);
		}
		return count;
	}
	
	private void assignProductAmountAndVolume(DashboardItem dashboardItem, DashboardSummaryDTO dashboardSummaryDto,
			LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		if (!userIds.isEmpty()) {
			// filter documents by user documents
			Set<Document> userDocuments = inventoryVoucherHeaderService.findDocumentsByUserIdIn(userIds);
			dashboardItem.getDocuments().retainAll(userDocuments);
		}
		List<Document> documents = new ArrayList<>(dashboardItem.getDocuments());
		Collections.sort(documents, (Document s1, Document s2) -> s1.getName().compareToIgnoreCase(s2.getName()));
		if (!documents.isEmpty() && !dashboardItem.getProductGroups().isEmpty()) {
			Set<Long> productIds = productGroupProductRepository
					.findProductIdByProductGroupIn(dashboardItem.getProductGroups());
			Object[] amountAndVolume = (Object[]) getProductAmountAndVolume(dashboardItem.getTaskPlanType(), documents, productIds, from, to, userIds, userPid);
			if (amountAndVolume != null) {
				dashboardSummaryDto.setAmount(amountAndVolume[0] == null ? 0d : (double) amountAndVolume[0]);
				dashboardSummaryDto.setVolume(amountAndVolume[1] == null ? 0d : (double) amountAndVolume[1]);
			}
		}
	}
	
	private Object getProductAmountAndVolume(TaskPlanType taskPlanType, List<Document> documents, Set<Long> productIds, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		Object obj = null;
		if (taskPlanType.equals(TaskPlanType.BOTH)) {
			if (userPid == null) {
				if (!userIds.isEmpty()) {
					obj = inventoryVoucherDetailRepository
							.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdIn(documents,
									productIds, from, to, userIds);
				} else {
					obj = inventoryVoucherDetailRepository.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetween(
							documents, productIds, from, to);
				}
			} else {
				obj = inventoryVoucherDetailRepository
						.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUser(documents, productIds, from,
								to, userPid);
			}
		} else if (taskPlanType.equals(TaskPlanType.PLANNED)) {
			obj = getPlannedProductAmountVolume(documents, productIds, from, to, userIds, userPid);
		} else if (taskPlanType.equals(TaskPlanType.UN_PLANNED)) {
			obj = getUnPlannedProductAmountVolume(documents, productIds, from, to, userIds, userPid);
		}
		return obj;
	}
	
	private Object getPlannedProductAmountVolume(List<Document> documents, Set<Long> productIds, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		Object obj = null;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				obj = inventoryVoucherDetailRepository
						.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
								documents, productIds, from, to, userIds);
			} else {
				obj = inventoryVoucherDetailRepository
						.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndTaskPlanIsNotNull(documents,
								productIds, from, to);
			}
		} else {
			obj = inventoryVoucherDetailRepository
					.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
							documents, productIds, from, to, userPid);
		}
		return obj;
	}
	
	private Object getUnPlannedProductAmountVolume(List<Document> documents, Set<Long> productIds, LocalDateTime from, LocalDateTime to,
			List<Long> userIds, String userPid) {
		Object obj = null;
		if (userPid == null) {
			if (!userIds.isEmpty()) {
				obj = inventoryVoucherDetailRepository
						.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNull(
								documents, productIds, from, to, userIds);
			} else {
				obj = inventoryVoucherDetailRepository
						.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndTaskPlanIsNull(documents,
								productIds, from, to);
			}
		} else {
			obj = inventoryVoucherDetailRepository
					.getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNull(
							documents, productIds, from, to, userPid);
		}
		return obj;
	}
	
	private void assignTargetBlockAchievedAndPlanned(DashboardItem dashboardItem,
			DashboardSummaryDTO dashboardSummaryDto, LocalDateTime from, LocalDateTime to, List<Long> userIds, String userPid) {
		// get target type
		List<TargetType> targetTypes = salesTargetReportSettingSalesTargetBlockRepository
						.findTargetTypeBySalesTargetBlockId(dashboardItem.getSalesTargetBlock().getId());
		List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList = Collections.emptyList();
		if (!targetTypes.isEmpty()) {
			if (userPid == null) {
				if (!userIds.isEmpty()) {
					salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
							.findBySalesTargetGroupPidAndUserIdInAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
									dashboardItem.getSalesTargetGroup().getPid(), userIds, from.toLocalDate(),
									to.toLocalDate());
				} else {
					salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
							.findBySalesTargetGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
									dashboardItem.getSalesTargetGroup().getPid(), from.toLocalDate(), to.toLocalDate());
				}
			} else {
				salesTargetGroupUserTargetList = salesTargetGroupUserTargetRepository
						.findBySalesTargetGroupPidAndUserPidAndFromDateGreaterThanEqualAndToDateLessThanEqualAndAccountWiseTargetFalse(
								dashboardItem.getSalesTargetGroup().getPid(), userPid, from.toLocalDate(),
								to.toLocalDate());
			}
		}
		if (!salesTargetGroupUserTargetList.isEmpty()) {
			TargetType targetType = targetTypes.get(0);
			dashboardSummaryDto.setTargetType(targetTypes.get(0));
			String[] targetAchieved = getTargetBlockAchievedAndPlanned(dashboardItem, from, to,salesTargetGroupUserTargetList, targetType, userPid);
			if (targetType.equals(TargetType.AMOUNT)) {
				double roundedTrgtAmt = new BigDecimal(targetAchieved[0]).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				double roundedAchdAmt = new BigDecimal(targetAchieved[2]).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				dashboardSummaryDto.setTargetAmount(roundedTrgtAmt);
				dashboardSummaryDto.setTargetAchievedAmount(roundedAchdAmt);
			} 
			if (targetType.equals(TargetType.VOLUME)) {
				double roundedTrgtVlm = new BigDecimal(targetAchieved[1]).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				double roundedAchdVlm = new BigDecimal(targetAchieved[3]).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				double roundedAverage = new BigDecimal(targetAchieved[4]).setScale(2, RoundingMode.HALF_UP)
						.doubleValue();
				dashboardSummaryDto.setTargetVolume(roundedTrgtVlm);
				dashboardSummaryDto.setTargetAchievedVolume(roundedAchdVlm);
				dashboardSummaryDto.setTargetAverageVolume(roundedAverage);
			}
		}
	}
	
	private String[] getTargetBlockAchievedAndPlanned(DashboardItem dashboardItem, LocalDateTime from, LocalDateTime to,
			List<SalesTargetGroupUserTarget> salesTargetGroupUserTargetList, TargetType targetType, String userPid) {
		Double targetAmount = 0d;
		Double targetVolume = 0d;
		Double achievedAmount = 0d;
		Double achievedVolume = 0d;
		Double targetAvg = 0d;
		// get sales Target Group documents
		Set<Long> documentIds = salesTargetGroupDocumentRepository
				.findDocumentIdsBySalesTargetGroupPid(dashboardItem.getSalesTargetGroup().getPid());
		Set<Long> productProfileIds = salesTargetGroupProductRepository
				.findProductIdBySalesTargetGroupPid(dashboardItem.getSalesTargetGroup().getPid());
		for (SalesTargetGroupUserTarget salesTargetGroupUserTarget : salesTargetGroupUserTargetList) {
			targetAmount += salesTargetGroupUserTarget.getAmount();
			targetVolume += salesTargetGroupUserTarget.getVolume();
			if (!documentIds.isEmpty() && !productProfileIds.isEmpty()) {
				// get achieved amount
				if (targetType.equals(TargetType.AMOUNT)) {
					 Double amount = getAchievedAmount(salesTargetGroupUserTarget.getUser().getId(),documentIds, productProfileIds, from, to, userPid);
					 achievedAmount += amount;
				}
				// get achieved volume
				if (targetType.equals(TargetType.VOLUME)) {
					 Double volume = getAchievedVolume(salesTargetGroupUserTarget.getUser().getId(),documentIds, productProfileIds, from, to, userPid);
					if (volume != null) {
						achievedVolume += volume;
						targetAvg += (volume / salesTargetGroupUserTarget.getVolume());
					}
				}
			}
		}
		return new String[] { targetAmount.toString(), targetVolume.toString(), achievedAmount.toString(),
				achievedVolume.toString(), targetAvg.toString() };
	}
	
	private Double getAchievedAmount(Long targetUserId, Set<Long> documentIds, Set<Long> productProfileIds,
			LocalDateTime from, LocalDateTime to, String userPid) {
		Double amount;
		if (userPid == null) {
			amount = inventoryVoucherDetailRepository
					.sumOfAmountByUserIdAndDocumentsAndProductsAndCreatedDateBetween(
							targetUserId, documentIds, productProfileIds, from,
							to);
		} else {
			amount = inventoryVoucherDetailRepository
					.sumOfAmountByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds,
							productProfileIds, from, to);
		}
		return amount;
	}
	
	private Double getAchievedVolume(Long targetUserId, Set<Long> documentIds, Set<Long> productProfileIds,
			LocalDateTime from, LocalDateTime to, String userPid) {
		Double volume;
		if (userPid == null) {
			volume = inventoryVoucherDetailRepository
					.sumOfVolumeByUserIdAndDocumentsAndProductsAndCreatedDateBetween(
							targetUserId, documentIds, productProfileIds, from, to);
		} else {
			volume = inventoryVoucherDetailRepository
					.sumOfVolumeByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds,
							productProfileIds, from, to);
		}
		return volume;
	}

	private DashboardHeaderSummaryDTO loadDashboardAttendanceHeader(LocalDateTime from, LocalDateTime to) {
		// dash board item configured to user
		List<DashboardAttendance> dashboardAttendances = dashboardAttendanceUserRepository
				.findDashboardAttendanceByUserLogin(SecurityUtils.getCurrentUserLogin());
		List<DashboardSummaryDTO> daySummaryDatas = new ArrayList<>();
		List<DashboardSummaryDTO> weekSummaryDatas = new ArrayList<>();
		List<DashboardSummaryDTO> monthSummaryDatas = new ArrayList<>();
		TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
		LocalDateTime weekStartDate = from.with(fieldISO, 1);
		LocalDateTime monthStartDate = from.withDayOfMonth(1);
		for (DashboardAttendance dashboardAttendance : dashboardAttendances) {
			DashboardSummaryDTO daySummary = new DashboardSummaryDTO();
			daySummary.setDashboardItemPid(dashboardAttendance.getId() + "");
			daySummary.setLabel(dashboardAttendance.getName());
			if (dashboardAttendance.getAttendanceStatus() != null) {
				daySummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceStatus(from, to,
						dashboardAttendance.getAttendanceStatus()));
			} else if (dashboardAttendance.getAttendanceStatusSubgroup() != null) {
				daySummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceSubGroup(from, to,
						dashboardAttendance.getAttendanceStatusSubgroup()));
			}
			DashboardSummaryDTO weekSummary = new DashboardSummaryDTO();
			weekSummary.setDashboardItemPid(dashboardAttendance.getId() + "");
			weekSummary.setLabel(dashboardAttendance.getName());
			if (dashboardAttendance.getAttendanceStatus() != null) {
				weekSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceStatus(
						weekStartDate, to, dashboardAttendance.getAttendanceStatus()));
			} else if (dashboardAttendance.getAttendanceStatusSubgroup() != null) {
				weekSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceSubGroup(
						weekStartDate, to, dashboardAttendance.getAttendanceStatusSubgroup()));
			}
			DashboardSummaryDTO monthSummary = new DashboardSummaryDTO();
			monthSummary.setDashboardItemPid(dashboardAttendance.getId() + "");
			monthSummary.setLabel(dashboardAttendance.getName());
			if (dashboardAttendance.getAttendanceStatus() != null) {
				monthSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceStatus(
						monthStartDate, to, dashboardAttendance.getAttendanceStatus()));
			} else if (dashboardAttendance.getAttendanceStatusSubgroup() != null) {
				monthSummary.setCount(attendanceRepository.countByCompanyIdAndDateBetweenAndAttendanceSubGroup(
						monthStartDate, to, dashboardAttendance.getAttendanceStatusSubgroup()));
			}
			daySummaryDatas.add(daySummary);
			weekSummaryDatas.add(weekSummary);
			monthSummaryDatas.add(monthSummary);
		}
		DashboardHeaderSummaryDTO dashboardDTO = new DashboardHeaderSummaryDTO();
		dashboardDTO.setDaySummaryDatas(daySummaryDatas);
		dashboardDTO.setWeekSummaryDatas(weekSummaryDatas);
		dashboardDTO.setMonthSummaryDatas(monthSummaryDatas);
		return dashboardDTO;
	}

	public static List<LocalDate> getMonthsBetween(LocalDate startDate, LocalDate endDate) {
		long numOfMonthsBetween = ChronoUnit.MONTHS.between(startDate, endDate.plusMonths(1));
		return IntStream.iterate(0, i -> i + 1).limit(numOfMonthsBetween).mapToObj(i -> startDate.plusMonths(i))
				.collect(Collectors.toList());
	}
}
