
package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DistanceFareService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.exception.TaskSubmissionPostSaveException;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.MapDistanceApiDTO;
import com.orderfleet.webapp.web.rest.dto.MapDistanceDTO;
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

import javax.inject.Inject;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Anish
 *
 */
@Controller
@RequestMapping("/web")
public class KilometerCalculationNewResource {

	private final Logger log = LoggerFactory.getLogger(KilometerCalculationNewResource.class);
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
	private GeoLocationService geoLocationService;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@RequestMapping(value = "/kilo-calc-new", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getKilometerCalculation(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of kilometer calculation report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		model.addAttribute("distanceFare", distanceFareService.findAllByCompany());
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/kilometer-calculation-new";
	}

	@RequestMapping(value = "/kilo-calc-new/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<KilometerCalculationDTO>> getAllKilometerCalculationByAccountProfile(
			@RequestParam("userPid") String userPid, /* @RequestParam String accountProfilePid, */
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to get kilometre calculation filtered report by userPid " + userPid);
		String accountProfilePid = "no";
		List<KilometerCalculationDTO> kilometreCalculationDTO = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, yeasterday, yeasterday);
		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, fromDateTime, fromDateTime);
		}  else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, fromDateTime, toFateTime);
		}

		return new ResponseEntity<>(kilometreCalculationDTO, HttpStatus.OK);
	}

	private List<KilometerCalculationDTO> getFilterData(String userPid, String accountProfilePid, LocalDate fDate, LocalDate tDate) {
		log.debug("Request to get distance Traveled : Enter:getFilterData() - "+"[ userPid : "+userPid+" accountProfilePid : "+ accountProfilePid +" fromDate : "+fDate+" toDate : "+tDate+"]" );

		String originatt = null;

		String userlogin = null;

		List<ExecutiveTaskExecution> lastExecutiveTaskExecution = new ArrayList<>();

		List<KilometreCalculation> kilometreCalculations = new ArrayList<>();

		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		Optional<EmployeeProfile> employee = employeeProfileRepository.findByUserPid(userPid);

		Optional<Attendance> attendance = attendanceRepository.findTop1ByCompanyPidAndUserPidOrderByCreatedDateDesc(company.getPid(), userPid);

		kilometreCalculations = kilometreCalculationRepository.findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fDate, tDate);

		lastExecutiveTaskExecution = executiveTaskExecutionRepository.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(userPid, fDate.atTime(0, 0), tDate.atTime(23, 59));

		Optional<KilometreCalculation> optionalKilometreCalculation = kilometreCalculations.stream().filter(kilocalc -> kilocalc.getExecutiveTaskExecution() == null).findAny();

		log.debug(userlogin+" : " + "ExecutiveTaskExecutions Size  :  " + lastExecutiveTaskExecution.size());

		if(employee.isPresent())
		{
			userlogin = employee.get().getUser().getLogin();
		}

		if(optionalKilometreCalculation.isPresent()){
				log.debug(userlogin +" : Present" );
				KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO(optionalKilometreCalculation.get());
				kiloCalDTO.setLocation(attendance.get().getLocation());
				kiloCalDTOs.add(kiloCalDTO);
		}

		if (attendance.isPresent() && attendance.get().getCreatedDate().toLocalDate().isEqual(LocalDate.now())) {
			if (attendance.get().getLatitude() != null && attendance.get().getLongitude() != null && !attendance.get().getLatitude().equals(BigDecimal.ZERO) && !attendance.get().getLongitude().equals(BigDecimal.ZERO)) {
				originatt = attendance.get().getLatitude() + " , " + attendance.get().getLongitude();
				log.info(userlogin +" : Attendance gps location tracked : " + originatt);

			} else if (attendance.get().getTowerLatitude() != null && attendance.get().getTowerLongitude() != null && !attendance.get().getTowerLatitude().equals(BigDecimal.ZERO) && !attendance.get().getTowerLongitude().equals(BigDecimal.ZERO)) {
				originatt = attendance.get().getTowerLatitude() + " , " + attendance.get().getTowerLongitude();
				log.info(userlogin+" : "+"Attendance tower location tracked : " + originatt);
			}
		}

		if( originatt != null && !originatt.isEmpty()){
			if(lastExecutiveTaskExecution.size()>0){
				KilometerCalculationDTO kiloCalDTO = null;
				ExecutiveTaskExecution destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(0);
				String destination = destinationExecutiveTaskExecution.getLatitude() +","+destinationExecutiveTaskExecution.getLongitude();
				log.debug(userlogin+" : "+"From Attendance " + " TO " + destinationExecutiveTaskExecution.getAccountProfile().getName());
				log.debug(userlogin+" : "+"origin   :  " + originatt +" destination   :  " + destination);
				try {
					kiloCalDTO = getdistance(originatt,destination,destinationExecutiveTaskExecution);
				} catch (TaskSubmissionPostSaveException e) {
					throw new RuntimeException(e);
				}
				kiloCalDTOs.add(kiloCalDTO);
			}
		}

		for (int i = 0; i < lastExecutiveTaskExecution.size()-1; i++)
		{
			ExecutiveTaskExecution originExecutiveTaskExecution = new ExecutiveTaskExecution();
			ExecutiveTaskExecution destinationExecutiveTaskExecution = new ExecutiveTaskExecution();

			for(int j = i; j<lastExecutiveTaskExecution.size(); j--){
				if (lastExecutiveTaskExecution.get(j).getLongitude() != null && lastExecutiveTaskExecution.get(j).getLongitude().doubleValue() != 0 && lastExecutiveTaskExecution.get(j).getLatitude()  != null && lastExecutiveTaskExecution.get(j).getLatitude().doubleValue() != 0) {
					originExecutiveTaskExecution = lastExecutiveTaskExecution.get(j);
					break;
				}
				log.debug(userlogin+" : "+lastExecutiveTaskExecution.get(j).getAccountProfile().getName().toString() + " : " + "NoLocation");
			}

			if (lastExecutiveTaskExecution.get(i + 1).getLongitude() != null && lastExecutiveTaskExecution.get(i + 1).getLongitude().doubleValue() != 0 && lastExecutiveTaskExecution.get(i + 1).getLatitude() != null && lastExecutiveTaskExecution.get(i + 1).getLatitude().doubleValue() != 0)
			{
				destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);
			}
			else
			{
				log.debug(userlogin+" : "+"Destination is not Present");
				destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);
				KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
				kiloCalDTO.setKilometre(0);
				kiloCalDTO.setMetres(0);
				kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
				kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
				kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
				kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
				kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
				kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getPunchInDate().toLocalDate().toString());
				if(employee.isPresent()){kiloCalDTO.setEmployeeName(employee.get().getName());}
				kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
				kiloCalDTO.setEndLocation(destinationExecutiveTaskExecution.getLocation());
				kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
				kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				kiloCalDTOs.add(kiloCalDTO);
				continue;
			}

			String origin = originExecutiveTaskExecution.getLatitude() +","+originExecutiveTaskExecution.getLongitude();

			String destination = destinationExecutiveTaskExecution.getLatitude()+","+destinationExecutiveTaskExecution.getLongitude();

			log.debug(userlogin+" : "+"Distance traveled From " + originExecutiveTaskExecution.getAccountProfile().getName() + " TO "+ destinationExecutiveTaskExecution.getAccountProfile().getName());

			log.debug(userlogin+" : "+"origin   :  " + origin + "destination   :  " + destination);

			try {
				kiloCalDTOs.add(getdistance(origin,destination,destinationExecutiveTaskExecution));
			} catch (TaskSubmissionPostSaveException e) {
				throw new RuntimeException(e);
			}
		}

		kiloCalDTOs = kiloCalDTOs.stream().sorted(Comparator.comparing(KilometerCalculationDTO::getPunchingTime).reversed()).collect(Collectors.toList());

		log.debug(userlogin+" : "+"Request to get distance Traveled : Exit :getFilterData() - " + kiloCalDTOs.toString());

		return kiloCalDTOs;
	}

	public  KilometerCalculationDTO getdistance(String origin ,String destination,ExecutiveTaskExecution destinationExecutiveTaskExecution) throws TaskSubmissionPostSaveException {

		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();

		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();

		MapDistanceApiDTO distanceApiJson = null;

		MapDistanceDTO distance = null;

		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUser(destinationExecutiveTaskExecution.getUser());
		try{
			distanceApiJson = geoLocationService.findDistance(origin, destination);

			if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {

				distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();

				if (distance != null) {
					kiloCalDTO.setKilometre(distance.getValue() * 0.001);
					kiloCalDTO.setMetres(distance.getValue());
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getPunchInDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				}
			}
			return kiloCalDTO;
		}
		catch (Exception e) {
			log.debug("Exception while processing saveKilometreDifference method {}", e);
			throw new TaskSubmissionPostSaveException("Exception while processing saveKilometreDifference method. "
					+ "Company : " + destinationExecutiveTaskExecution.getCompany().getLegalName() + " User:"
					+ destinationExecutiveTaskExecution.getUser().getLogin() + " Disatance API JSON :" + distanceApiJson
					+ " Exception : " + e);
		}

	}

	@RequestMapping(value = "/kilo-calc-new/updateLocationExeTask/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();

			if (execution.getLatitude() != BigDecimal.ZERO) {
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);
				execution.setLocation(location);

			} else {
				System.out.println("-------No Location");
				execution.setLocation("No Location");
			}
			execution = executiveTaskExecutionRepository.save(execution);
			executionView = new InvoiceWiseReportView(execution);
		}
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}


}
