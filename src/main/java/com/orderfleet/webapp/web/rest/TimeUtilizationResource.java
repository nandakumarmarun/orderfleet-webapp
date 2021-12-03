package com.orderfleet.webapp.web.rest;


import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.repository.AccountingVoucherHeaderRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDetailView;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionView;
import com.orderfleet.webapp.web.rest.dto.TimeUtilizationDTO;
import com.orderfleet.webapp.web.rest.dto.TimeUtilizationView;

@Controller
@RequestMapping("/web")
public class TimeUtilizationResource {
	private final Logger log = LoggerFactory.getLogger(TimeUtilizationResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private UserService userService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;
	
	@Inject
	private AttendanceService attendanceService;

	@RequestMapping(value = "/time-utilization", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllTimeUtilization(Model model) throws URISyntaxException {
			model.addAttribute("users", userService.findAllByCompany());	
		return "company/timeUtilization";
	}

	@RequestMapping(value = "/time-utilization/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<TimeUtilizationView, List<TimeUtilizationDTO>>> filterExecutiveTaskExecutions(@RequestParam("userPid") String userPid,  @RequestParam("filterBy") String filterBy,
		@RequestParam String fromDate, @RequestParam String toDate) {
		List<TimeUtilizationDTO>timeUtilizationDTOs=new ArrayList<>();
		List<TimeUtilizationDTO>timeUtilizationDTOs2=new ArrayList<>();
		Map<LocalDate, List<TimeUtilizationDTO>> timeUtilizationGroupedByDate=null;
		if (filterBy.equals("TODAY")) {
			List<ExecutiveTaskExecutionView> executiveTaskExecutions = getFilterData(userPid, LocalDate.now(), LocalDate.now());
			List<AttendanceDTO> attendanceList = getFilterAttendanceData(userPid, LocalDate.now(), LocalDate.now());
			
			timeUtilizationDTOs=addListToTimeUtilizationList(attendanceList, executiveTaskExecutions);
			if(timeUtilizationDTOs.size()!=0){
				timeUtilizationDTOs.stream().sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())).forEach(e->timeUtilizationDTOs2.add(e));
			}
			timeUtilizationGroupedByDate=mappingTimeWithTimeUtilization(filterBy, timeUtilizationDTOs2);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			List<ExecutiveTaskExecutionView> executiveTaskExecutions = getFilterData(userPid,  yeasterday, yeasterday);
			List<AttendanceDTO> attendanceList = getFilterAttendanceData(userPid,  yeasterday, yeasterday);
			
			timeUtilizationDTOs=addListToTimeUtilizationList(attendanceList, executiveTaskExecutions);
			if(timeUtilizationDTOs.size()!=0){
				timeUtilizationDTOs.stream().sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())).forEach(e->timeUtilizationDTOs2.add(e));
			}
			timeUtilizationGroupedByDate=mappingTimeWithTimeUtilization(filterBy, timeUtilizationDTOs2);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			List<ExecutiveTaskExecutionView> executiveTaskExecutions = getFilterData(userPid,  weekStartDate, LocalDate.now());
			List<AttendanceDTO> attendanceList = getFilterAttendanceData(userPid,  weekStartDate, LocalDate.now());
			
			timeUtilizationDTOs=addListToTimeUtilizationList(attendanceList, executiveTaskExecutions);
			if(timeUtilizationDTOs.size()!=0){
				timeUtilizationDTOs.stream().sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())).forEach(e->timeUtilizationDTOs2.add(e));
			}
			timeUtilizationGroupedByDate=mappingTimeWithTimeUtilization(filterBy, timeUtilizationDTOs2);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			List<ExecutiveTaskExecutionView> executiveTaskExecutions = getFilterData(userPid,  monthStartDate, LocalDate.now());
			List<AttendanceDTO> attendanceList = getFilterAttendanceData(userPid,  monthStartDate, LocalDate.now());
			
			timeUtilizationDTOs=addListToTimeUtilizationList(attendanceList, executiveTaskExecutions);
			if(timeUtilizationDTOs.size()!=0){
				timeUtilizationDTOs.stream().sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())).forEach(e->timeUtilizationDTOs2.add(e));
			}
			timeUtilizationGroupedByDate=mappingTimeWithTimeUtilization(filterBy, timeUtilizationDTOs2);
		} 
		else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			List<ExecutiveTaskExecutionView> executiveTaskExecutions = getFilterData(userPid,  fromDateTime, toFateTime);
			List<AttendanceDTO> attendanceList = getFilterAttendanceData(userPid,  fromDateTime, toFateTime);
			
			timeUtilizationDTOs=addListToTimeUtilizationList(attendanceList, executiveTaskExecutions);
			if(timeUtilizationDTOs.size()!=0){
			timeUtilizationDTOs.stream().sorted((e1, e2) -> e1.getDate().compareTo(e2.getDate())).forEach(e->timeUtilizationDTOs2.add(e));
			}
			timeUtilizationGroupedByDate=mappingTimeWithTimeUtilization(filterBy, timeUtilizationDTOs2);
		}
		
		Map<LocalDate, List<TimeUtilizationDTO>> result = timeUtilizationGroupedByDate.entrySet().stream()
				.sorted(Map.Entry.comparingByKey())
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
				(oldValue, newValue) -> oldValue, LinkedHashMap::new));
		//get map in reverse order
		Map<LocalDate, List<TimeUtilizationDTO>> finalResult=getMapInReverseOrder(result);
		finalResult=addUnspecifedTime(finalResult);
		Map<TimeUtilizationView, List<TimeUtilizationDTO>> map=getTimeActiveAndUnactive(finalResult);
		return new ResponseEntity<>(map, HttpStatus.OK);
	}

	public Map<TimeUtilizationView, List<TimeUtilizationDTO>> getTimeActiveAndUnactive(Map<LocalDate, List<TimeUtilizationDTO>> result) {
		ArrayList<LocalDate> keyList = new ArrayList<>(result.keySet());
		Map<TimeUtilizationView, List<TimeUtilizationDTO>> map=new LinkedHashMap<>();
		for (int i = 0 ; i < keyList.size(); i++) {
			
			//get key
			LocalDate key = keyList.get(i);
			String unspecifiedTimeSpend=null;
			String activityTimeSpend=null;
			Long timeUnspecified =0L;
			Long timeActivity=0L;
			//get value corresponding to key
			List<TimeUtilizationDTO> value = result.get(key);
			for (TimeUtilizationDTO timeUtilizationDTO : value) {
				if(timeUtilizationDTO.getDescription().equals("Unspecified")){
					if(timeUtilizationDTO.getToDateTime()!=null && timeUtilizationDTO.getFromDateTime()!=null){
					timeUnspecified+=timeDifference(timeUtilizationDTO.getToDateTime(),timeUtilizationDTO.getFromDateTime());
					}
				}else if(timeUtilizationDTO.getDescription().equals("Attendance")){
					
				}
				else{
					if(timeUtilizationDTO.getToDateTime()!=null && timeUtilizationDTO.getFromDateTime()!=null){
					timeActivity+=timeDifference(timeUtilizationDTO.getToDateTime(),timeUtilizationDTO.getFromDateTime());
					}
				}
			}
			unspecifiedTimeSpend=milliSecondToDate(timeUnspecified);
			activityTimeSpend=milliSecondToDate(timeActivity);
			TimeUtilizationView timeUtilizationView=new TimeUtilizationView(key.toString(),unspecifiedTimeSpend,activityTimeSpend);
		map.put(timeUtilizationView, value);
		}
		return map;
	}
	
	public Long timeDifference(LocalDateTime d1,LocalDateTime d2) {
		long diffInMilli = java.time.Duration.between(d2, d1).toMillis();
		return diffInMilli;
		
	}
	
	public String milliSecondToDate(Long dateInMillisecond){
		long diffSeconds = dateInMillisecond / 1000 % 60;
		long diffMinutes = dateInMillisecond / (60 * 1000) % 60;
		long diffHours = dateInMillisecond / (60 * 60 * 1000) % 24;
		return diffHours+"h:"+diffMinutes+"m:"+diffSeconds+"s";
	}
	
	//Get data in reverse order and Avoid attendance duplicate
	public Map<LocalDate, List<TimeUtilizationDTO>> getMapInReverseOrder(Map<LocalDate, List<TimeUtilizationDTO>> result) {
		Map<LocalDate, List<TimeUtilizationDTO>> finalResult=new LinkedHashMap<>();
		ArrayList<LocalDate> keyList = new ArrayList<>(result.keySet());
		for (int i = keyList.size() - 1; i >= 0; i--) {
			//get key
			LocalDate key = keyList.get(i);
			//get value corresponding to key
			List<TimeUtilizationDTO> value = result.get(key);
			int j=2;
			for (TimeUtilizationDTO timeUtilizationDTO : value) {
				if(timeUtilizationDTO.getDescription().equals("Attendance")){
					timeUtilizationDTO.setSortValue(1);
				}else{
					timeUtilizationDTO.setSortValue(j);
					j++;
				}
			}
			List<TimeUtilizationDTO> noDuplicate=new ArrayList<>();
			int k=0;
			for (TimeUtilizationDTO timeUtilizationDTO : value) {
				if(k==0){
				if(timeUtilizationDTO.getSortValue()==1){
					noDuplicate.add(timeUtilizationDTO);
					k++;
				}
				}else{
					if(timeUtilizationDTO.getSortValue()!=1){
					noDuplicate.add(timeUtilizationDTO);
					}
				}
			}
			finalResult.put(key, noDuplicate);
		}
		return finalResult;
	}
	
	//add unspecified data
	public Map<LocalDate, List<TimeUtilizationDTO>> addUnspecifedTime(Map<LocalDate, List<TimeUtilizationDTO>> result) {
		Map<LocalDate, List<TimeUtilizationDTO>> finalResult=new LinkedHashMap<>();
		ArrayList<LocalDate> keyList = new ArrayList<>(result.keySet());
		for (int i = 0 ; i < keyList.size(); i++) {
			List<TimeUtilizationDTO> listWithUnspecified=new ArrayList<>();
			//get key
			LocalDate key = keyList.get(i);
			//get value corresponding to key
			List<TimeUtilizationDTO> value = result.get(key);
			for(int j=1;j<=value.size();j++){
			for (TimeUtilizationDTO timeUtilizationDTO : value) {
				for (TimeUtilizationDTO timeUtilizationDTO1 : value) {
				if(timeUtilizationDTO.getSortValue()==j && timeUtilizationDTO1.getSortValue()==j+1){
					listWithUnspecified.add(timeUtilizationDTO);
					TimeUtilizationDTO unspecifiedDTO=new TimeUtilizationDTO();
					unspecifiedDTO.setDescription("Unspecified");
					unspecifiedDTO.setDate(timeUtilizationDTO1.getDate());
					unspecifiedDTO.setFromDateTime(timeUtilizationDTO.getToDateTime());
					unspecifiedDTO.setToDateTime(timeUtilizationDTO1.getFromDateTime());
					unspecifiedDTO.setTimeSpent(findTimeSpend(timeUtilizationDTO.getToDateTime(),timeUtilizationDTO1.getFromDateTime()));
					listWithUnspecified.add(unspecifiedDTO);
					break;
				}
				}
				if(j==value.size()){
					if(timeUtilizationDTO.getSortValue()==j){
						listWithUnspecified.add(timeUtilizationDTO);
					}
				}
			}
			
			}
			if(value.size()!=0){
			finalResult.put(key, listWithUnspecified);
			}
		}
		return finalResult;
	}
	
	//get attendance list
	private List<AttendanceDTO> getFilterAttendanceData(String userPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<AttendanceDTO> attendanceList = new ArrayList<AttendanceDTO>();
			attendanceList = attendanceService.findAllByCompanyIdUserPidAndDateBetween(userPid, fromDate, toDate);
		return attendanceList;
	}
	
	//get activity list
	private List<ExecutiveTaskExecutionView> getFilterData(String userPid, 
			LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<ExecutiveTaskExecution>();
			executiveTaskExecutions = executiveTaskExecutionRepository
					.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(userPid, fromDate, toDate);
		List<ExecutiveTaskExecutionView> executiveTaskExecutionViews = new ArrayList<>();
		for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
			ExecutiveTaskExecutionView executiveTaskExecutionView = new ExecutiveTaskExecutionView(
					executiveTaskExecution);
			String timeSpend=findTimeSpend(executiveTaskExecution.getStartTime(), executiveTaskExecution.getEndTime());
			executiveTaskExecutionView.setTimeSpend(timeSpend);
			List<ExecutiveTaskExecutionDetailView> executiveTaskExecutionDetailViews = new ArrayList<ExecutiveTaskExecutionDetailView>();
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
				ExecutiveTaskExecutionDetailView executiveTaskExecutionDetailView = new ExecutiveTaskExecutionDetailView(obj[0].toString(),
						obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString());
				executiveTaskExecutionDetailView.setDocumentVolume(Double.valueOf(obj[4].toString()));
				executiveTaskExecutionDetailViews.add(executiveTaskExecutionDetailView);
			}
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "ACC_QUERY_115" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get AccVoucher By ExecutiveTaskExecutionId";
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
	                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
					+ description1);

			for (Object[] obj : accountingVouchers) {
				executiveTaskExecutionDetailViews.add(new ExecutiveTaskExecutionDetailView(obj[0].toString(),
						obj[1].toString(), Double.valueOf(obj[2].toString()), obj[3].toString()));
			}
			DateTimeFormatter DATE_TIME_FORMAT11 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT11 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id11 = "DYN_QUERY_110" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description11 ="get all documents by Executive Task executionId";
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
	                logger.info(id11 + "," + endDate11 + "," + startTime11 + "," + endTime11 + "," + minutes11 + ",END," + flag11 + ","
					+ description11);
			for (Object[] obj : dynamicDocuments) {
				boolean imageFound = false;
				
				executiveTaskExecutionDetailViews.add(
						new ExecutiveTaskExecutionDetailView(obj[0].toString(), obj[1].toString(), obj[2].toString(), imageFound));
			}
			executiveTaskExecutionView.setExecutiveTaskExecutionDetailViews(executiveTaskExecutionDetailViews);
			executiveTaskExecutionViews.add(executiveTaskExecutionView);
		}
		return executiveTaskExecutionViews;
	}
	
	//Find Time different between startTime and endTime
	public String findTimeSpend(LocalDateTime startTime,LocalDateTime endTime){
		long hours=0;
		long minutes=0;
		long seconds=0;
		if(startTime!=null && endTime!=null){
		long years = startTime.until( endTime, ChronoUnit.YEARS);
		startTime = startTime.plusYears( years );

		long months = startTime.until( endTime, ChronoUnit.MONTHS);
		startTime = startTime.plusMonths( months );

		long days = startTime.until( endTime, ChronoUnit.DAYS);
		startTime = startTime.plusDays( days );
		 hours = startTime.until( endTime, ChronoUnit.HOURS);
		startTime = startTime.plusHours( hours );

		 minutes = startTime.until( endTime, ChronoUnit.MINUTES);
		startTime = startTime.plusMinutes( minutes );

		 seconds = startTime.until( endTime, ChronoUnit.SECONDS);
		}
		return  hours + " : " +minutes + " : " +seconds ;
		
	}
	
	//time Utilization Grouped By Date
	public Map<LocalDate, List<TimeUtilizationDTO>> mappingTimeWithTimeUtilization(String filter, List<TimeUtilizationDTO> timeUtilizationDTOs) {
		Map<LocalDate, List<TimeUtilizationDTO>> timeUtilizationGroupedByDate=new TreeMap<>();
			timeUtilizationGroupedByDate=timeUtilizationDTOs.stream().collect(Collectors.groupingBy(etp -> etp.getDate().toLocalDate()));
		return timeUtilizationGroupedByDate;
		
	}
	
	// Add Attendance and ExecutiveTaskExecution together
	public List<TimeUtilizationDTO> addListToTimeUtilizationList(List<AttendanceDTO> attendanceDTOs,List<ExecutiveTaskExecutionView> executiveTaskExecutionViews) {
		List<TimeUtilizationDTO>timeUtilizationDTOs=new ArrayList<>();
		if(executiveTaskExecutionViews.size()!=0){
			for(ExecutiveTaskExecutionView executiveTaskExecutionView:executiveTaskExecutionViews){
				TimeUtilizationDTO timeUtilizationDTO=new TimeUtilizationDTO(executiveTaskExecutionView.getActivityName()+"-"+executiveTaskExecutionView.getAccountProfileName(), executiveTaskExecutionView.getStartTime(), executiveTaskExecutionView.getEndTime(), executiveTaskExecutionView.getPlannedDate(), executiveTaskExecutionView.getTimeSpend(),0);
			timeUtilizationDTOs.add(timeUtilizationDTO);
			}
		}
		if(attendanceDTOs.size()!=0){
			for(AttendanceDTO attendanceDTO:attendanceDTOs){
				TimeUtilizationDTO timeUtilizationDTO=new TimeUtilizationDTO("Attendance", null, attendanceDTO.getPlannedDate(), attendanceDTO.getPlannedDate(), findTimeSpend(attendanceDTO.getPlannedDate(), attendanceDTO.getPlannedDate()),0);
			timeUtilizationDTOs.add(timeUtilizationDTO);
			}
		}

		return timeUtilizationDTOs;
	}
}
