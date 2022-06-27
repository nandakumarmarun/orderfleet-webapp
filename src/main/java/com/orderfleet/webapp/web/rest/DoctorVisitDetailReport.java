
package com.orderfleet.webapp.web.rest;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.DistanceFare;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.KilometreCalculation;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.DistanceFareRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormDetailRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.KilometreCalculationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DistanceFareService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DoctorVisitDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.VisitReportView;

@Controller
@RequestMapping("/web")
public class DoctorVisitDetailReport {

	private final Logger log = LoggerFactory.getLogger(DoctorVisitDetailReport.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private KilometreCalculationRepository kilometreCalculationRepository;

	@Inject
	private DistanceFareService distanceFareService;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private UserService userService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private FilledFormDetailRepository filledFormDetailRepository;

	@Inject
	private DistanceFareRepository distanceFareRepository;
	
	@Inject
	private FormElementRepository formElementRepository;

	@RequestMapping(value = "/doctor-visit-details", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getDoctorVisit(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Doctor visit detail report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/visit-report/doctor-visit-detail";
	}

	@RequestMapping(value = "/doctor-visit-details/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DoctorVisitDTO>> getAllDoctorVisitDetails(
			@RequestParam("employeePid") String employeePid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate, @RequestParam boolean inclSubordinate) {

		log.debug("Web request to get Doctor Visit details" + "employee Pid:" + employeePid);

		List<DoctorVisitDTO> doctorVisitDto = new ArrayList<>();

		String activityPid = "ACTV-UchGFubTal1627904876754";

		if (filterBy.equals("TODAY")) {
			doctorVisitDto = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), activityPid, inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			doctorVisitDto = getFilterData(employeePid, yesterday, yesterday, activityPid, inclSubordinate);

		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			doctorVisitDto = getFilterData(employeePid, fromDateTime, fromDateTime, activityPid, inclSubordinate);
		}
		return new ResponseEntity<>(doctorVisitDto, HttpStatus.OK);
	}

	private List<DoctorVisitDTO> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate, String activityPid,
			boolean inclSubordinate) {
		// TODO Auto-generated method stub

		List<DoctorVisitDTO> doctorVisitDto = new ArrayList<>();
		log.info("Get Filter Data");

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);
		log.info("User Ids :" + userIds);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}

		log.info("Finding executive Task execution");

		List<Object[]> etExtecutions = executiveTaskExecutionRepository.findByUserIdInDateBetweenAndActivityPid(userIds,
				fromDate, toDate, activityPid);

		if (etExtecutions.isEmpty()) {
			return null;
		}

		Map<String, List<Long>> employeeWiseGrouped = etExtecutions.stream().collect(Collectors.groupingBy(
				obj -> (String) obj[1], TreeMap::new, Collectors.mapping(ete -> (Long) ete[0], Collectors.toList())));

		List<Long> eteIds = employeeWiseGrouped.values().stream().flatMap(List::stream).collect(Collectors.toList());

		log.info("Finding Visit Details");
		List<Object[]> visitDetails = new ArrayList<>();
		if (eteIds.size() > 0) {

			visitDetails = executiveTaskExecutionRepository.findByExeTaskIdIn(eteIds);
		}

		log.info("Finding Attendance details.......");
		List<Object[]> attendance = new ArrayList<>();
		attendance = attendanceRepository.findByUserIdInAndDateBetween(userIds, fromDate, toDate);

		log.info("Finding Kilometers..........");
		List<Object[]> kilometers = new ArrayList<>();

		kilometers = kilometreCalculationRepository.findByUserIdsAndDateBetwewn(userIds, fromDate, toDate);

		log.info("Finding fare..........");
		List<DistanceFare> fare = new ArrayList();

		fare = distanceFareRepository.findAllByCompanyId();

		List<UserDTO> userDtoList = userService.findByUserIdIn(userIds);
		List<String> userPids = userDtoList.stream().map(UserDTO::getPid).collect(Collectors.toList());

		String documentPid = "DOC-YiKsWTaibY1627905412612";

		String formPid = "FORM-USYqDGCxNr1627906203295";

		log.info("Finding filled form  ......");
		List<Object[]> filledForm = new ArrayList<>();
		if (userPids.isEmpty()) {

			filledForm = filledFormRepository.findFilledFormsIdsByDocumentAndFormPidAndCreatedDateBetween(documentPid,
					formPid, fromDate, toDate);
		} else if (!userPids.isEmpty()) {

			filledForm = filledFormRepository
					.findFilledFormsIdsByDocumentAndFormPidAndUserPidCreatedByAndCreatedDateBetween(documentPid,
							formPid, userPids, fromDate, toDate);

		}
		Set<Long> filledFormIds = new HashSet<>();

		for (Object[] ffObj : filledForm) {

			filledFormIds.add(ffObj != null ? Long.parseLong(ffObj[0].toString()) : 0);
		}
		System.out.println("Ids:"+filledFormIds);
		log.info("Finding filled form details  ......");
		List<Object[]> filledFormKilometer = new ArrayList<>();
		List<Object[]> filledFormtravel = new ArrayList<>();
		List<Object[]> filledFormfood = new ArrayList<>();
		String kilometerPid = "FRME-okRw3bCwv51627904876526";
		String travelPid = "FRME-HECQhZ1Ydt1627906105742";
		String foodPid ="FRME-NwVw8pY7VN1627906129492";
		Optional<FormElement> kilometer= formElementRepository.findOneByPid(kilometerPid);
		Long kid=kilometer.get().getId();
		Optional<FormElement> travel= formElementRepository.findOneByPid(travelPid);
		Long tid=travel.get().getId();
		Optional<FormElement> food= formElementRepository.findOneByPid(foodPid);
		Long fid=food.get().getId();

		if (filledFormIds.size() > 0) {
           if(kid!=null)
           {
			filledFormKilometer = filledFormDetailRepository.findAllByFilledFormIdInAndFormElementId(filledFormIds,
					kid);
           }
		
           if(tid!=null)
           {
        	   filledFormtravel = filledFormDetailRepository.findAllByFilledFormIdInAndFormElementId(filledFormIds,
					tid);
           }
           if(fid!=null)
           {
        	   filledFormfood = filledFormDetailRepository.findAllByFilledFormIdInAndFormElementId(filledFormIds,
					fid);
           }
		
		}


		for (Map.Entry<String, List<Long>> entry : employeeWiseGrouped.entrySet()) {
			DoctorVisitDTO doctorVisit = new DoctorVisitDTO();
			String employeeName = entry.getKey();
			doctorVisit.setEmpName(employeeName);
			System.out.println("ENterd in to loop :" + employeeName);

			for (Object[] obj : attendance) {
				if (obj[2].toString().equals(employeeName)) {

					LocalDateTime attTime = LocalDateTime.parse(obj[1].toString());
					 DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss a");  
				        String formatDateTime = attTime.format(format);  
					doctorVisit.setRoute(obj[0].toString());
					doctorVisit.setAttendance(formatDateTime);
				}
			}
			for (Object[] obj : visitDetails) {
				if (obj[3].toString().equals(employeeName)) {
					LocalDateTime firstVisittime = LocalDateTime.parse(obj[1].toString());
					 DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss a");  
				        String formatDate = firstVisittime.format(format); 
				        
					LocalDateTime lastVisitTime = LocalDateTime.parse(obj[2].toString());
					 DateTimeFormatter formats = DateTimeFormatter.ofPattern("HH:mm:ss a");  
				        String formatTime = lastVisitTime.format(formats); 

					doctorVisit.setDoctorVisitNo(obj[0].toString());

					doctorVisit.setStartTime(formatDate);
					doctorVisit.setEndTime(formatTime);

				}
			}
			for (Object[] obj : kilometers) {
				if (obj[1].toString().equals(employeeName)) {
					doctorVisit.setTotalDistance(Double.parseDouble(obj[0].toString()));
				}
			}
			for (DistanceFare df : fare) {
				doctorVisit.setFare(df.getFare());
			}

			for (Object[] km : filledFormKilometer) {
				
				doctorVisit.setTotalTravelled(km[1].toString());
			}
			 
			Double ta = 0.0;
			Double da = 0.0;
			for (Object[] tr : filledFormtravel) {
				
				doctorVisit.setTravelExpense(tr[1].toString());
				ta =Double.parseDouble(tr[1].toString());
			 }
			for(Object[] fd:filledFormfood)
			{
				doctorVisit.setFoodExpense(fd[1].toString());
				da = Double.parseDouble(fd[1].toString());
				}
			
			Double totalExpnse = ta+da;
			doctorVisit.setTotalExpense(String.valueOf(totalExpnse));
			doctorVisitDto.add(doctorVisit);
		}

		return doctorVisitDto;
	}

	private List<Long> getUserIdsUnderCurrentUser(String employeePid, boolean inclSubordinate) {

		List<Long> userIds = Collections.emptyList();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
//				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
//				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
				Set<Long> dashboardUserIds = dashboardUserRepository.findUserIdsByCompanyId();
				Set<Long> uniqueIds = new HashSet<>();
				log.info("dashboard user ids empty: " + dashboardUserIds.isEmpty());
				if (!dashboardUserIds.isEmpty()) {
					log.info(" user ids empty: " + userIds.isEmpty());
					log.info("userids :" + userIds.toString());
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid != null && uid.equals(sid)) {
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
					// List<User> users = userRepository.findAllByCompanyId();
					// userIds = users.stream().map(User::getId).collect(Collectors.toList());
					userIds = userRepository.findAllUserIdsByCompanyId();
				}
			}
		} else {
			if (inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
				System.out.println("Testing start for Activity Transaction");
				System.out.println("employeePid:" + employeePid);
				System.out.println("userIds:" + userIds.toString());
				System.out.println("Testing end for Activity Transaction");
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds = Arrays.asList(opEmployee.get().getUser().getId());
				}
				System.out.println("Testing start for Activity Transaction");
				System.out.println("--------------------------------------");
				System.out.println("employeePid:" + employeePid);
				System.out.println("UserIds:" + userIds.toString());
				System.out.println("Testing end for Activity Transaction");
			}
		}

		return userIds;

	}

}
