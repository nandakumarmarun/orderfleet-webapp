
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
import java.time.LocalDateTime;
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

	@Inject
	private PunchOutRepository punchOutRepository;

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
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) throws TaskSubmissionPostSaveException {
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

	private List<KilometerCalculationDTO> getFilterData(String userPid, String accountProfilePid, LocalDate fDate, LocalDate tDate) throws TaskSubmissionPostSaveException {
		log.debug("Request to get distance Traveled : Enter:getFilterData() - "+"[ userPid : "+userPid+" accountProfilePid : "+ accountProfilePid +" fromDate : "+fDate+" toDate : "+tDate+"]" );
		System.out.println();
		String originatt = null;
		String userlogin = null;
		boolean ispunchouted = false;

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

        Optional<PunchOut> optionalPunchOut = null;

        List<ExecutiveTaskExecution> lastExecutiveTaskExecution = new ArrayList<>();
		List<KilometreCalculation> kilometreCalculations = new ArrayList<>();
		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();

		Company company = companyRepository.
				findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<EmployeeProfile> employee = employeeProfileRepository
				.findByUserPid(userPid);
		Optional<Attendance> attendance = attendanceRepository
				.findTop1ByCompanyPidAndUserPidAndCreatedDateBetweenOrderByCreatedDateDesc(
						company.getPid(), userPid,fromDate,toDate);
		kilometreCalculations = kilometreCalculationRepository
				.findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(
						userPid, fDate, tDate);
		lastExecutiveTaskExecution = executiveTaskExecutionRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(
						userPid, fDate.atTime(0, 0), tDate.atTime(23, 59));
		Optional<KilometreCalculation> optionalKilometreCalculation =
				kilometreCalculations
						.stream()
						.filter(kilocalc -> kilocalc.getExecutiveTaskExecution() == null)
						.findAny();

		if(employee.isPresent())
		{
			userlogin = employee.get().getUser().getLogin();
		}

		log.debug(userlogin+" : " + "Order Details :  " + lastExecutiveTaskExecution.size());
		System.out.println();

		if (attendance.isPresent() && attendance.get().getCreatedDate().toLocalDate().isEqual(fDate)) {
			log.debug(userlogin +" : "+ "Attendence :  Present" + " On " + attendance.get().getCreatedDate().toLocalDate());
			if(optionalKilometreCalculation.isPresent()){
				log.debug(userlogin +" : Attandence" );
				KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO(optionalKilometreCalculation.get());
				kiloCalDTO.setLocation(attendance.get().getLocation());
				kiloCalDTO.setAttendence(true);
				kiloCalDTOs.add(kiloCalDTO);
			}
			if (attendance.get().getLatitude() != null
					&& attendance.get().getLongitude() != null
					&& !attendance.get().getLatitude().equals(BigDecimal.ZERO)
					&& !attendance.get().getLongitude().equals(BigDecimal.ZERO)) {

				log.debug(userlogin +" : "+ " Tracking Attendance gps location ");
				originatt = attendance.get().getLatitude() + " , " + attendance.get().getLongitude();
				log.info(userlogin +" : "+" : Attendance gps location tracked : " + originatt);

			} else if (attendance.get().getTowerLatitude() != null
					&& attendance.get().getTowerLongitude() != null
					&& !attendance.get().getTowerLatitude().equals(BigDecimal.ZERO)
					&& !attendance.get().getTowerLongitude().equals(BigDecimal.ZERO)) {

				log.debug(userlogin +" : "+ " Tracking Attendance tower location");
				originatt = attendance.get().getTowerLatitude() + " , " + attendance.get().getTowerLongitude();
				log.info(userlogin+" : "+"Attendance tower location tracked : " + originatt);
				System.out.println();

			}
            optionalPunchOut = punchOutRepository
                    .findIsAttendancePresent(
							attendance.get().getPid());
			log.debug(" is Punchouted : " + optionalPunchOut.isPresent());
			System.out.println();
		}

		if( originatt != null && !originatt.isEmpty()){
			log.debug(userlogin +" : "+ " : Processing Distance From Attendence");
			if(lastExecutiveTaskExecution.size()>0){
				log.debug(userlogin+" : " + " ExecutiveTaskExecutions Size  :  " + lastExecutiveTaskExecution.size());
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
				if (lastExecutiveTaskExecution.get(j).getLongitude() != null
                        && lastExecutiveTaskExecution.get(j).getLongitude().doubleValue() != 0
                        && lastExecutiveTaskExecution.get(j).getLatitude()  != null
                        && lastExecutiveTaskExecution.get(j).getLatitude().doubleValue() != 0) {

                    originExecutiveTaskExecution = lastExecutiveTaskExecution.get(j);
					break;
				}
				log.debug(userlogin+" : "+lastExecutiveTaskExecution.get(j).getAccountProfile().getName().toString() + " : " + "NoLocation");
			}

			if (lastExecutiveTaskExecution.get(i + 1).getLongitude() != null
                    && lastExecutiveTaskExecution.get(i + 1).getLongitude().doubleValue() != 0
                    && lastExecutiveTaskExecution.get(i + 1).getLatitude() != null
                    && lastExecutiveTaskExecution.get(i + 1).getLatitude().doubleValue() != 0) {

                destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);
			}
			else
			{
				destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);
				log.debug(userlogin+" : "+ destinationExecutiveTaskExecution.getAccountProfile() +" : Location Not Found");
				KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
				kiloCalDTO.setKilometre(0);
				kiloCalDTO.setMetres(0);
				kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
				kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
				kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
				kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
				log.debug("Date : " + destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
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

			if(optionalPunchOut.isPresent()
					&& originExecutiveTaskExecution.getPunchInDate().isBefore(optionalPunchOut.get().getPunchOutDate())
					&& destinationExecutiveTaskExecution.getPunchInDate().isAfter(optionalPunchOut.get().getPunchOutDate())){

				if(ispunchouted == false) {
						log.debug("Enter Punchout");
						log.debug("punch out Time : " + optionalPunchOut.get().getPunchOutDate());
						log.debug("Origin Punch in Date : " + originExecutiveTaskExecution.getPunchInDate());
						log.debug("Origin Punch out Date : " + destinationExecutiveTaskExecution.getPunchInDate());
						if (optionalPunchOut.isPresent()) {
							log.debug("Punch Out Present");
							KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
							PunchOut punchout = optionalPunchOut.get();

							log.debug("Account Profiles : " + originExecutiveTaskExecution.getAccountProfile().getName());
							System.lineSeparator();

							String originToPunchout = originExecutiveTaskExecution.getLatitude() + "," + originExecutiveTaskExecution.getLongitude();
							String destination = destinationExecutiveTaskExecution.getLatitude() + "," + destinationExecutiveTaskExecution.getLongitude();
							String punchoutlocation = punchout.getLatitude() + "," + punchout.getLongitude();

							log.debug(userlogin + " : " + "Distance traveled From " + originExecutiveTaskExecution.getAccountProfile().getName() + " TO " + punchout.getLocation() + "(Punchout)");
							log.debug(userlogin + " : " + " punch out origin   :  " + originToPunchout + "destination   :  " + punchoutlocation);
							kiloCalDTO = getpunchoutdistance(originToPunchout, punchoutlocation, punchout);
							kiloCalDTO.setPunchOut(true);
							kiloCalDTOs.add(kiloCalDTO);
							log.debug(userlogin + " : " + " punch out destination   :  " + originToPunchout + " destination   :  " + destination);
							kiloCalDTOs.add(getdistance(punchoutlocation, destination, destinationExecutiveTaskExecution));
						}
						ispunchouted=true;
					}
				}
			else{
				String origin = originExecutiveTaskExecution.getLatitude() +","+originExecutiveTaskExecution.getLongitude();

				String destination = destinationExecutiveTaskExecution.getLatitude()+","+destinationExecutiveTaskExecution.getLongitude();

				log.debug(userlogin+" : "+"Distance traveled From " + originExecutiveTaskExecution.getAccountProfile().getName() + " TO "+ destinationExecutiveTaskExecution.getAccountProfile().getName());

				log.debug(userlogin+" : "+"origin   :  " + origin + "destination   :  " + destination);
				System.out.println();

				try {
					kiloCalDTOs.add(getdistance(origin,destination,destinationExecutiveTaskExecution));
				} catch (TaskSubmissionPostSaveException e) {
					throw new RuntimeException(e);
				}
			}
		}

		log.debug("kilo meter Calculation : " + kiloCalDTOs);

		if(ispunchouted == false){
			if (optionalPunchOut != null  && optionalPunchOut.isPresent()) {
				PunchOut punchout = optionalPunchOut.get();
				System.out.println();
				log.debug("Enter Punch-out no oders After");
				log.debug("punch out Time : " + optionalPunchOut.get().getPunchOutDate());
				KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
				ExecutiveTaskExecution originExecutiveTaskExecution = lastExecutiveTaskExecution.get(lastExecutiveTaskExecution.size()-1);
				log.debug("Origin Punch in Date : " + originExecutiveTaskExecution.getPunchInDate());
				log.debug("Origin Punch out Date : " + punchout.getPunchOutDate());
				String origin = originExecutiveTaskExecution.getLatitude() +","+originExecutiveTaskExecution.getLongitude();
				String destination = punchout.getLatitude()+","+punchout.getLongitude();
				kiloCalDTO = getpunchoutdistance(origin, destination, punchout);
				kiloCalDTO.setPunchOut(true);
				kiloCalDTOs.add(kiloCalDTO);
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
					log.debug("Punching Time : "+destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					log.debug("Punching Date : "+destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getPunchInDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("Location : "+destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				}
				else{
					log.debug("attandence latitude and longitudes are not aqurate ");
					kiloCalDTO.setKilometre(0.0);
					kiloCalDTO.setMetres(0.0);
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					log.debug("Punching Time : "+destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					log.debug("Punching Date : "+destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getPunchInDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("Location : "+destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				}
			}
			System.out.println();
			log.debug(kiloCalDTO.toString());
			System.out.println();
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


	public  KilometerCalculationDTO getDistanceFromAttandance(String origin ,String destination,ExecutiveTaskExecution destinationExecutiveTaskExecution) throws TaskSubmissionPostSaveException {

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
					log.debug("attadence format currect ");
					kiloCalDTO.setKilometre(distance.getValue() * 0.001);
					kiloCalDTO.setMetres(distance.getValue());
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					log.debug("Punching Time : "+destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					log.debug("Punching Date : "+destinationExecutiveTaskExecution.getPunchInDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getPunchInDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("Location : "+destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setLocation("Location Not Found");
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				}

			}
			System.out.println();
			log.debug(kiloCalDTO.toString());
			System.out.println();
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

	public  KilometerCalculationDTO getpunchoutdistance(String origin ,String destination,PunchOut punchOut) throws TaskSubmissionPostSaveException {
		log.debug("punch Out Fetching : " + punchOut.toString());
		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();

		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();

		MapDistanceApiDTO distanceApiJson = null;

		MapDistanceDTO distance = null;

		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUser(punchOut.getUser());
		try{
			distanceApiJson = geoLocationService.findDistance(origin, destination);
			log.debug("distanceApiJson : " + distanceApiJson.toString());
			if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {

				distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();
				log.debug("distance : " + distance.toString());
				if (distance != null) {
					log.debug("distance not null to punchout");
					kiloCalDTO.setKilometre(distance.getValue() * 0.001);
					kiloCalDTO.setMetres(distance.getValue());
					kiloCalDTO.setUserPid(punchOut.getUser().getPid());
					kiloCalDTO.setUserName(punchOut.getUser().getFirstName());
					kiloCalDTO.setDate(punchOut.getPunchOutDate().toLocalDate());
					kiloCalDTO.setPunchingTime(punchOut.getPunchOutDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(punchOut.getPunchOutDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("PunchoutLocation : " + punchOut.getLocation());
					kiloCalDTO.setLocation(punchOut.getLocation());
				}
			}
			log.debug(kiloCalDTO.toString());
			return kiloCalDTO;
		}
		catch (Exception e) {
			log.debug("Exception while processing saveKilometreDifference method {}", e);
			throw new TaskSubmissionPostSaveException("Exception while processing saveKilometreDifference method. "
					+ "Company : " + punchOut.getCompany().getLegalName() + " User:"
					+ punchOut.getUser().getLogin() + " Disatance API JSON :" + distanceApiJson
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




//		if(attendance.isPresent()){
//			log.debug("attendance present");
//			Optional<PunchOut> optionalPunchOut = punchOutRepository
//					.findIsAttendancePresent(attendance.get().getPid());
//			if(optionalPunchOut.isPresent()){
//				log.debug("punch out present");
//				KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
//				PunchOut punchout = optionalPunchOut.get();
//				lastExecutiveTaskExecution = lastExecutiveTaskExecution.stream().sorted(Comparator.comparing(ExecutiveTaskExecution::getPunchInDate).reversed()).collect(Collectors.toList());
//					ExecutiveTaskExecution lastexecutiveTaskExecutionDTO = lastExecutiveTaskExecution.get(0);
//					log.debug("Account Profiles : "+lastexecutiveTaskExecutionDTO.getAccountProfile().getName());
//
//					String origin = lastexecutiveTaskExecutionDTO.getLatitude() +","+lastexecutiveTaskExecutionDTO.getLongitude();
//					String destination = punchout.getLatitude()+","+punchout.getLongitude();
//
//					log.debug(userlogin+" : "+"Distance traveled From " + lastexecutiveTaskExecutionDTO.getAccountProfile().getName() + " TO "+ punchout.getLocation()+"(Punchout)");
//					log.debug(userlogin+" : "+" punch out origin   :  " + origin + "destination   :  " + destination);
//					kiloCalDTO = getpunchoutdistance(origin,destination,punchout);
//					kiloCalDTO.setPunchOut(true);
//					kiloCalDTOs.add(kiloCalDTO);
//			}
//		}