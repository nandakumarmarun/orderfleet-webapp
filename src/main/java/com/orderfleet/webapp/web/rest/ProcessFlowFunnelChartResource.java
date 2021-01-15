package com.orderfleet.webapp.web.rest;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.ChartType;
import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.chart.dto.FunnelChartDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardChartDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountProfileMapper;

@Controller
@RequestMapping("/web")
public class ProcessFlowFunnelChartResource {
	
	private final Logger log = LoggerFactory.getLogger(ProcessFlowFunnelChartResource.class);

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	@Inject
	private AccountProfileService accountProfileService;
	@Inject
	private UserRepository userRepository;
	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;
	@Inject
	private AccountProfileRepository accountProfileRepository;
	@Inject
	private AccountProfileMapper accountProfileMapper;
	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;



	@Inject
	private EmployeeProfileService employeeProfileService;

	@RequestMapping(value = "/process-flow-funnel-chart", method = RequestMethod.GET)
	public String customerJourney(Model model) {
		return "company/process-flow-funnel-chart";
	}
	
	@RequestMapping(value = "/process-flow-funnel-chart/load", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DashboardChartDTO> getFunnelChartData() {
		DashboardChartDTO dashboardChartDTO = new DashboardChartDTO();
		dashboardChartDTO.setChartType(ChartType.FUNNEL);
		dashboardChartDTO.setFunnelChartDtos(makeFunnelChart());
		dashboardChartDTO.setChartLabel("Sales Funnel");
		return new ResponseEntity<>(dashboardChartDTO, HttpStatus.OK);
	}
	
//	private List<FunnelChartDTO> makeFunnelChart() {
//		List<FunnelChartDTO> funnelChartDtos = new ArrayList<>();
//		// List<Object[]> fNameAndCounts = stageHeaderRepository.findFunnelDetails(fDate.atTime(0, 0), tDate.atTime(23, 59));
//		if(fNameAndCounts.isEmpty()) {
//			return funnelChartDtos;
//		}
//		for (Object[] stage : fNameAndCounts) {
//			String amount = stage[3] == null ? "0.00" : stage[3].toString();
//			funnelChartDtos.add(new FunnelChartDTO(stage[1].toString() + "\nCount: "+ stage[2].toString() +" ", amount));
//		}
//		return funnelChartDtos;
//	}
	
	private List<FunnelChartDTO> makeFunnelChart() {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			List<String> employeePids = employeeProfileService.findAllByCompany().stream().map(obj -> obj.getPid().toString()).collect(Collectors.toList());
			userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		} else {
			List<String> employeePids = employeeProfileService.findAllEmployeeByUserIdsIn(userIds).stream().map(obj -> obj.getPid().toString()).collect(Collectors.toList());
			userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
			Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
			userIds.add(currentUserId);
		}
		
		List<String> documentPids =  primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(VoucherType.PRIMARY_SALES_ORDER).parallelStream().map(obj -> obj.getPid().toString())
				.collect(Collectors.toList());
		
		List<ProcessFlowStatus> processStatus = Arrays.asList(ProcessFlowStatus.DEFAULT, ProcessFlowStatus.PO_PLACED,
				ProcessFlowStatus.IN_STOCK, ProcessFlowStatus.PO_ACCEPTED_AT_TSL,
				ProcessFlowStatus.UNDER_PRODUCTION, ProcessFlowStatus.READY_TO_DISPATCH_AT_TSL,
				ProcessFlowStatus.READY_TO_DISPATCH_AT_PS, ProcessFlowStatus.DELIVERED,
				ProcessFlowStatus.NOT_DELIVERED);
		

		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		Period days_90 = Period.ofDays(90);
		fDate = tDate.minus(days_90);
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		
		
		List<FunnelChartDTO> funnelChartDtos = new ArrayList<>();
		
		List<Object[]> inventoryVouchers = inventoryVoucherHeaderRepository
				.findByUserIdInAndDocumentPidInAndProcessFlowStatusStatusDateBetweenOrderByCreatedDateDesc(userIds,
						documentPids, processStatus, fromDate, toDate);
		
		List<Object[]> r = inventoryVouchers.stream().filter(u -> {
			if (u[29] != null) {
				LocalDate currentdate = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = u[29].toString();
				LocalDate deliveryDate = LocalDate.parse(date, formatter);
				long noOfDaysBetween = ChronoUnit.DAYS.between(currentdate, deliveryDate);
				if (noOfDaysBetween <= 0) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		List<Object[]> o = inventoryVouchers.stream().filter(u -> {
			if (u[29] != null) {
				LocalDate currentdate = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = u[29].toString();
				LocalDate deliveryDate = LocalDate.parse(date, formatter);
				long noOfDaysBetween = ChronoUnit.DAYS.between(currentdate, deliveryDate);
				if (noOfDaysBetween > 0
						&& noOfDaysBetween < 15) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		List<Object[]> y = inventoryVouchers.stream().filter(u -> {
			if (u[29] != null) {
				LocalDate currentdate = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = u[29].toString();
				LocalDate deliveryDate = LocalDate.parse(date, formatter);
				long noOfDaysBetween = ChronoUnit.DAYS.between(currentdate, deliveryDate);
				if (noOfDaysBetween >= 15
						&& noOfDaysBetween < 30) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		List<Object[]> g = inventoryVouchers.stream().filter(u -> {
			if (u[29] != null) {
				LocalDate currentdate = LocalDate.now();
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				String date = u[29].toString();
				LocalDate deliveryDate = LocalDate.parse(date, formatter);
				long noOfDaysBetween = ChronoUnit.DAYS.between(currentdate, deliveryDate);
				if (noOfDaysBetween >= 30
						&& noOfDaysBetween <= 45) {
					return true;
				}
			}
			return false;
		}).collect(Collectors.toList());
		
		funnelChartDtos.add(new FunnelChartDTO("before_today", "Before Today", r.size()+".00", "#FF3933"));
		funnelChartDtos.add(new FunnelChartDTO("1_to_14_days", "1 to 14 Days", o.size()+".00", "#FFB833"));
		funnelChartDtos.add(new FunnelChartDTO("15_to_30_days", "15 to 30 Days", y.size()+".00", "#FFFC33"));
		funnelChartDtos.add(new FunnelChartDTO("31_to_45_days", "31 to 45 Days", g.size()+".00", "#7DFF33"));
		return funnelChartDtos;
	}	
}
