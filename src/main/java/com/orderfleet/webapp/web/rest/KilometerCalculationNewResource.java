
package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DistanceFareService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.KilometerCalculationsDenormalisationService;
import com.orderfleet.webapp.service.exception.TaskSubmissionPostSaveException;
import com.orderfleet.webapp.service.util.RandomUtil;

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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.inject.Inject;
import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
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
//@RestController
//@RequestMapping("/api")
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
	private KilometerCalculationsDenormalisationService kilometerCalculationsDenormalisationService;

	@Inject
	private PunchOutRepository punchOutRepository;

	/***
	 *
	 * @param model
	 * @return
	 * @throws URISyntaxException
	 */
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

	/***
	 *
	 * @param userPid
	 * @param filterBy
	 * @param fromDate
	 * @param toDate
	 * @return
	 * @throws TaskSubmissionPostSaveException
	 */
	@RequestMapping(value = "/kilo-calc-new/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<KilometerCalculationDTO>> getAllKilometerCalculationByAccountProfile(
			@RequestParam("userPid") String userPid, /* @RequestParam String accountProfilePid, */
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate)
			throws TaskSubmissionPostSaveException {
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
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, fromDateTime, toFateTime);
		}
		return new ResponseEntity<>(kilometreCalculationDTO, HttpStatus.OK);
	}

	/**
	 * Retrieves filtered data of distance traveled for a specific user and account
	 * profile
	 * between the specified start and end dates.
	 *
	 * @param userPid           The unique identifier for the user.
	 * @param accountProfilePid The unique identifier for the account profile (not
	 *                          used in the method).
	 * @param fDate             The start date (LocalDate) for filtering data.
	 * @param tDate             The end date (LocalDate) for filtering data.
	 * @return A list of KilometerCalculationDTO objects representing the distance
	 *         traveled.
	 * @throws TaskSubmissionPostSaveException If an error occurs during the
	 *                                         distance calculation.
	 */
	private List<KilometerCalculationDTO> getFilterData(
			String userPid, String accountProfilePid,
			LocalDate fDate, LocalDate tDate)
			throws TaskSubmissionPostSaveException {
		log.debug("Request to get distance Traveled : Enter:getFilterData() - " +
				"[ userPid : " + userPid + " accountProfilePid : " + "accountProfilePid " +
				" fromDate : " + fDate + " toDate : " + tDate + "]");
		// Initialize variables
		String userlogin = null;
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<ExecutiveTaskExecution> lastExecutiveTaskExecution = new ArrayList<>();
		List<ExecutiveTaskExecution> lastExecutive = new ArrayList<>();
		List<ExecutiveTaskExecution> inValidData = new ArrayList<>();
		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();
		List<KilometerCalculationDTO> invalidKiloCalDTOs = new ArrayList<KilometerCalculationDTO>();
		List<KilometerCalculationDTO> ExistingKiloCalcDTO = new ArrayList<KilometerCalculationDTO>();

		// Retrieve company and employee information from the database
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<EmployeeProfile> employee = employeeProfileRepository.findByUserPid(userPid);
		userlogin = getUser(userlogin, employee);
		String Thread = userlogin + "-" + company.getId();

		// Retrieve Kilometer information from the database
		ExistingKiloCalcDTO = kilometerCalculationsDenormalisationService
				.findAllByDateBetweenAndCompanyId(
						company.getId(), fromDate, toDate, userPid);

		log.debug(" Existing Data Size : " + ExistingKiloCalcDTO.size());

		// Find attendance data for the user within the specified date range
		List<Attendance> attendances = attendanceRepository
				.findAllByCompanyPidAndUserPidAndPlannedDateBetweenOrderByCreatedDateDesc(
						company.getPid(), userPid, fromDate, toDate);

		log.debug(" attendances : " + attendances.size());

		// Find PunchOut data for the user within the specified date range
		List<PunchOut> punchOuts = punchOutRepository
				.findAllByCompanyIdUserPidAndCreatedDateBetween(userPid, fromDate, toDate);

		log.debug(" punchOuts : " + punchOuts.size());

		// Retrieve a list of executive task executions for the user within the daterange
		lastExecutive = executiveTaskExecutionRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderBySendDateAsc(
						userPid, fDate.atTime(0, 0), tDate.atTime(23, 59));

		log.debug(" Visits  : " + lastExecutive.size());
		// Assign attendance and punch-out data if available
		// Adding Attendance object to the list of ExecutiveTaskExecution objects
		for (Attendance attendance : attendances) {
			log.debug("Attendence Date : " + attendance.getPlannedDate());
			// Create a new ExecutiveTaskExecution object for the attendance
			ExecutiveTaskExecution attadenceExecutiveTaskExecution = getAttendenceExeTExe(attendance);
			// Add the ExecutiveTaskExecution object to the beginning of the
			// lastExecutiveTaskExecution list
			lastExecutive.add(0, attadenceExecutiveTaskExecution);

			Optional<PunchOut> optionalPunchOut = punchOuts
					.stream()
					.filter(data -> data.getAttendance().getPid()
							.equals(attendance.getPid()))
					.findAny();

			// Assigning Punchout origin
			if (optionalPunchOut.isPresent()) {
				log.debug("Punchout Date : " + optionalPunchOut.get().getPunchOutDate());
				log.debug(Thread + " : Assigning Punchout");

				// Create a new AccountProfile object for the punch Out
				ExecutiveTaskExecution punchoutExecutiveTaskExecution = getPucnhOutExeTExe(optionalPunchOut.get());
				// Add the ExecutiveTaskExecution object to the lastExecutiveTaskExecution list
				lastExecutive.add(punchoutExecutiveTaskExecution);
			}
		}

		log.debug(" All User Punchings  : " + lastExecutive.size());
		// Sort the list of ExecutiveTaskExecution objects by the sendDate
		lastExecutiveTaskExecution = lastExecutive.stream()
				.filter(data -> data.getLongitude() != null
						&& data.getLongitude().doubleValue() != 0
						&& data.getLatitude() != null
						&& data.getLatitude().doubleValue() != 0)
				.sorted(Comparator.comparing(ExecutiveTaskExecution::getSendDate))
				.collect(Collectors.toList());

		log.debug("==============================valid Accounts================================\n");
		lastExecutiveTaskExecution.forEach(data -> log.debug(data.getAccountProfile().getName() + "==" + data.getSendDate()));
		log.debug("============================================================================\n");

		log.debug(" Valid Data    : " + lastExecutiveTaskExecution.size());
		inValidData = lastExecutive.stream()
				.filter(data -> data.getLongitude() == null ||
						data.getLatitude() == null ||
						data.getLongitude().doubleValue() == 0.0 ||
						data.getLatitude().doubleValue() == 0.0)
				.sorted(Comparator.comparing(ExecutiveTaskExecution::getSendDate))
				.collect(Collectors.toList());

		log.debug("=====================================Invalid Accounts=========================\n");
		inValidData.forEach(data -> log.debug(data.getAccountProfile().getName() + "==" + data.getSendDate()));
		log.debug("==============================================================================\n");
		log.debug(System.lineSeparator());

		// Add the starting point kilometer (Attendence)
		 if (lastExecutiveTaskExecution.size() > 0) {
				log.debug(" Setting Strating Point : ");
				ExecutiveTaskExecution firstpoint = lastExecutiveTaskExecution.get(0);
				log.debug(" Strating Point  :  "  + firstpoint.getAccountProfile().getName());
				log.debug(" Attendece Pid   :  "   + firstpoint.getPid());
				log.debug(" Attendence Date : " + firstpoint.getSendDate());

				Optional<KilometerCalculationDTO> optionalKilometerCalculationDTO = ExistingKiloCalcDTO
						.stream()
						.filter(data -> data.getTaskExecutionPid()
								.equals(firstpoint.getPid())
								&& data.getCreatedDate()
										.equals(firstpoint.getSendDate()))
						.findAny();

				if (!optionalKilometerCalculationDTO.isPresent()) {
					log.debug(" Attendence Not Exist in History  : " + optionalKilometerCalculationDTO.isPresent());
					startingPointkilometerDTO(lastExecutiveTaskExecution, kiloCalDTOs, employee, Thread);
				}
		 }

		// Iterating through the list of ExecutiveTaskExecution
		// objects to calculate the distance between points.
		if (lastExecutiveTaskExecution.size() > 0) {
			log.debug("Traveling Starting " );
			for (int i = 0; i < lastExecutiveTaskExecution.size() - 1; i++) {
				ExecutiveTaskExecution originExecutiveTaskExecution = lastExecutiveTaskExecution.get(i);
				ExecutiveTaskExecution destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);

				String origin = originExecutiveTaskExecution.getLatitude() + ","
						+ originExecutiveTaskExecution.getLongitude();
				String destination = destinationExecutiveTaskExecution.getLatitude() + ","
						+ destinationExecutiveTaskExecution.getLongitude();

				logingg(originExecutiveTaskExecution, destinationExecutiveTaskExecution, origin, destination);

				try {
					Optional<KilometerCalculationDTO> optionalKilometerCalculationDTO = ExistingKiloCalcDTO
							.stream()
							.filter(data -> data.getTaskExecutionPid().equals(destinationExecutiveTaskExecution.getPid())
									&& data.getCreatedDate().equals(destinationExecutiveTaskExecution.getSendDate()))
							.findAny();
					if (optionalKilometerCalculationDTO.isPresent()) {
						logingExistingData(optionalKilometerCalculationDTO);
						continue;
					}
					KilometerCalculationDTO calcKm = getDistance(origin, destination,
							destinationExecutiveTaskExecution, i);

					// Calculating Distance from origin to Destination
					kiloCalDTOs.add(calcKm);
					log.debug( i +" : Size : "+ kiloCalDTOs.size());
				} catch (TaskSubmissionPostSaveException e) {
					throw new RuntimeException(e);
				}
			}

			log.debug("new data Size : "+ kiloCalDTOs.size());
			List<KilometerCalculationDTO> distinctList = kiloCalDTOs.stream()
					.distinct()
					.collect(Collectors.toList());
			log.debug("All Size : " + distinctList.size());
			if (distinctList.size() > 0) {
				convertToJson(distinctList, Thread + " Saving New Data : ");
				kilometerCalculationsDenormalisationService
						.CalculateDistance(distinctList, company.getId());
			}
			ExistingKiloCalcDTO.addAll(distinctList);
		}


		for (ExecutiveTaskExecution inValidDatu : inValidData) {
			int i = 0;
			log.debug("Send Date 	 : " + inValidDatu.getSendDate());
			log.debug("Account Name  : " + inValidDatu.getAccountProfile().getName());

			Optional<KilometerCalculationDTO> optionalKilometerCalculationDTO = ExistingKiloCalcDTO
					.stream()
					.filter(data -> data.getTaskExecutionPid()
							.equals(inValidDatu.getPid())
							&& data.getCreatedDate()
							.equals(inValidDatu.getPunchInDate()))
					.findAny();

			if (!optionalKilometerCalculationDTO.isPresent()) {
				log.debug(inValidDatu.getAccountProfile().getName() + " Not Present ");
				defaultkilometerDTO(inValidDatu,
						invalidKiloCalDTOs, employee, Thread, i);
				i++;
			}
		}

		log.debug("Adding Invalid Km : " + invalidKiloCalDTOs.size());
		ExistingKiloCalcDTO.addAll(invalidKiloCalDTOs);

		// Sort the list of KilometerCalculationDTO objects by the punchingTime in descending order
		ExistingKiloCalcDTO = ExistingKiloCalcDTO
				.stream()
				.sorted(Comparator.comparing(KilometerCalculationDTO::getExecreatedDate)
						.reversed())
				.collect(Collectors.toList());
		log.debug("Request to get distance Traveled : Exit : getFilterData(){} : " + ExistingKiloCalcDTO.size());
		return ExistingKiloCalcDTO;
	}

	private void LogggNestDay(ExecutiveTaskExecution destinationExecutiveTaskExecution, ExecutiveTaskExecution firstpoint) {
		log.debug(" Setting Starting Point ");
		log.debug(" Next Day   		  :  " + destinationExecutiveTaskExecution.getSendDate().toLocalDate());
		log.debug(" Strating Point    : " + firstpoint.getAccountProfile().getName());
		log.debug(" Strating Pid      : " + firstpoint.getPid());
		log.debug(" Strating Date     : " + firstpoint.getSendDate());
	}

	private void logingg(ExecutiveTaskExecution originExecutiveTaskExecution, ExecutiveTaskExecution destinationExecutiveTaskExecution, String origin, String destination) {
		log.debug("----------------------------------------------------------------------------------------------");
		log.debug(" Origin Account: "
				+ originExecutiveTaskExecution.getAccountProfile().getName()
				+ ": "
				+ origin);
		log.debug(" Destination Account : "
				+ destinationExecutiveTaskExecution.getAccountProfile().getName()
				+ " : " + destination);
		log.debug("----------------------------------------------------------------------------------------------");
	}

	private void logingExistingData(Optional<KilometerCalculationDTO> optionalKilometerCalculationDTO) {
		log.debug("Already Calculated : " + optionalKilometerCalculationDTO.get().getAccountProfileName());
	}

	/**
	 *
	 * This method is used to generate a KilometerCalculationDTO object based on the
	 * given data.
	 * The object is then added to the list of KilometerCalculationDTOs.
	 * 
	 * @param lastExecutiveTaskExecution
	 * @param kiloCalDTOs
	 * @param employee
	 * @param Thread
	 * @param i
	 */
	private void defaultkilometerDTO(ExecutiveTaskExecution lastExecutiveTaskExecution,
			List<KilometerCalculationDTO> kiloCalDTOs,
			Optional<EmployeeProfile> employee, String Thread, int i) {
		ExecutiveTaskExecution destinationExecutiveTaskExecution;

		// Get the next ExecutiveTaskExecution from the provided list
		destinationExecutiveTaskExecution = lastExecutiveTaskExecution;

		// Create a new KilometerCalculationDTO object and set its properties
		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
		kiloCalDTO.setKilometre(0);
		kiloCalDTO.setMetres(0);
		kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
		kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
		kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
		kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
		kiloCalDTO.setExecreatedDate(destinationExecutiveTaskExecution.getSendDate());
		if (employee.isPresent()) {
			kiloCalDTO.setEmployeeName(employee.get().getName());
		}
		kiloCalDTO.setLocation("No Location Found!");
		kiloCalDTO.setEndLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());

		// Add the KilometerCalculationDTO object to the kiloCalDTOs list
		kiloCalDTOs.add(kiloCalDTO);
	}

	/**
	 * Initializes a KilometerCalculationDTO object based on the provided
	 * parameters.
	 *
	 * @param lastExecutiveTaskExecution The list of last executed executive task
	 *                                   executions.
	 * @param kiloCalDTOs                The list of KilometerCalculationDTO objects
	 *                                   to store the data.
	 * @param employee                   An optional EmployeeProfile object
	 *                                   representing the employee information.
	 * @param Thread                     The thread identifier (possibly for logging
	 *                                   purposes).
	 */
	private void startingPointkilometerDTO(
			List<ExecutiveTaskExecution> lastExecutiveTaskExecution,
			List<KilometerCalculationDTO> kiloCalDTOs,
			Optional<EmployeeProfile> employee, String Thread) {
		ExecutiveTaskExecution destinationExecutiveTaskExecution;
		// Get the first ExecutiveTaskExecution from the list as the
		// destinationExecutiveTaskExecution.
		destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(0);
		// Create a new KilometerCalculationDTO object.
		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
		// Set initial values for the KilometerCalculationDTO object.
		kiloCalDTO.setKilometre(0);
		kiloCalDTO.setMetres(0);
		kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
		kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
		kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
		kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
		kiloCalDTO.setExecreatedDate(destinationExecutiveTaskExecution.getSendDate());
		if (employee.isPresent()) {kiloCalDTO.setEmployeeName(employee.get().getName());}
		kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setEndLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
		// Add the initialized KilometerCalculationDTO object to the list of DTOs.
		kiloCalDTOs.add(kiloCalDTO);
	}

	/**
	 * Calculates the distance between the given origin and destination using a
	 * distance API.
	 *
	 * @param origin                            The latitude and longitude of the
	 *                                          origin location in the format
	 *                                          "latitude,longitude".
	 * @param destination                       The latitude and longitude of the
	 *                                          destination location in the format
	 *                                          "latitude,longitude".
	 * @param destinationExecutiveTaskExecution The ExecutiveTaskExecution object
	 *                                          associated with the destination
	 *                                          location.
	 * @return A KilometerCalculationDTO object containing the calculated distance
	 *         details.
	 * @throws TaskSubmissionPostSaveException If an error occurs during the
	 *                                         -- distance calculation or API call.
	 */
	public KilometerCalculationDTO getDistance(
			String origin,
			String destination,
			ExecutiveTaskExecution destinationExecutiveTaskExecution, int nodecount)
			throws TaskSubmissionPostSaveException {

		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();
		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
		MapDistanceApiDTO distanceApiJson = null;
		MapDistanceDTO distance = null;

		EmployeeProfile employee = employeeProfileRepository
				.findEmployeeProfileByUser(
						destinationExecutiveTaskExecution.getUser());
		try {

			// Call the distance API to get the distance between the origin and destination
			distanceApiJson = geoLocationService.findDistance(origin, destination);

			if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {

				// Extract distance details from the API response
				distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();

				if (distance != null) {

					// Populate the KilometerCalculationDTO object with the calculated distance
					// details
					kiloCalDTO.setKilometre(distance.getValue() * 0.001);
					kiloCalDTO.setMetres(distance.getValue());
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					kiloCalDTO
							.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					kiloCalDTO
							.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
					kiloCalDTO.setExecreatedDate(destinationExecutiveTaskExecution.getSendDate());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("Location : " + destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				} else {

					// If distance is not available, set default values in the
					// KilometerCalculationDTO object
					log.debug("Attendance latitude and longitudes are not accurate ");
					kiloCalDTO.setKilometre(0.0);
					kiloCalDTO.setMetres(0.0);
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					kiloCalDTO.setExecreatedDate(destinationExecutiveTaskExecution.getSendDate());
					kiloCalDTO
							.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					kiloCalDTO
							.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				}
			}
			log.debug("kilometers Traveled  : " + kiloCalDTO.getKilometre());
			log.debug("meters Traveled  : " + kiloCalDTO.getMetres());
			return kiloCalDTO;
		} catch (Exception e) {
			// Handle exceptions and throw a custom exception with relevant information
			log.debug("Exception while processing saveKilometreDifference method {}", e);
			throw new TaskSubmissionPostSaveException("Exception while processing saveKilometreDifference method. "
					+ "Company : " + destinationExecutiveTaskExecution.getCompany().getLegalName() + " User:"
					+ destinationExecutiveTaskExecution.getUser().getLogin() + " Disatance API JSON :" + distanceApiJson
					+ " Exception : " + e);
		}
	}

	/**
	 * This method handles a GET request to update the location information of an
	 * executive task execution.
	 *
	 * @param pid The unique identifier for the executive task execution.
	 * @return ResponseEntity containing the updated InvoiceWiseReportView with the
	 *         location information.
	 */
	@RequestMapping(value = "/kilo-calc-new/updateLocationExeTask/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView> updateLocationExecutiveTaskExecutions(
			@PathVariable String pid) {
		log.debug("pid : " + pid);

		// Find the ExecutiveTaskExecution by its unique identifier (pid) using the
		// repository.
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		Optional<Attendance> optionalAtt = attendanceRepository.findOneByPid(pid);
		Optional<PunchOut> optionalPunchOut = punchOutRepository.findOneByPid(pid);

		KilometerCalculationDenormalised kcd = kilometerCalculationsDenormalisationService.findbyExePid(pid);

		// Create a new InvoiceWiseReportView to store the updated information.
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();

		// Check if the ExecutiveTaskExecution is present in the repository.
		if (opExecutiveeExecution.isPresent()) {
			// Get the ExecutiveTaskExecution object from the Optional.
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();
			if (!execution.getLocation().equals("No Location")) {
				log.debug("Already Defined" + execution.getLocation());
				kcd.setLocation(execution.getLocation());
				kilometerCalculationsDenormalisationService.saveLocation(kcd);
			} else if (execution.getLatitude() != BigDecimal.ZERO
					&& execution.getLatitude() != null && execution.getLongitude() != null
					&& execution.getLongitude() != BigDecimal.ZERO) {
				String location  = null;

				// Call the geoLocationService to find the address from the latitude and
				// longitude.
				System.out.println("-------lat != 0");
				try{
					location = geoLocationService
							.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				}catch (NullPointerException ex){
					log.debug("Catching Error ");
					execution.setLocation("Location Not Found !");
					kcd.setLocation("Location Not Found !");
				} catch(HttpClientErrorException exe){
					log.debug("Catching Error ");
					execution.setLocation("Location Not Found !");
					kcd.setLocation("Location Not Found !");
				}
				System.out.println("-------" + location);
				// Update the location of the ExecutiveTaskExecution.
				execution.setLocation(location != null && location !=""  ? location : "Location Not Found !");
				kcd.setLocation(location != null && location !=""  ? location : "Location Not Found !");
			} else {
				System.out.println("-------No Location");
				// Set the location as "No Location" if latitude is zero (invalid location).
				execution.setLocation("Location Not Found !");
				kcd.setLocation("Location Not Found !");
			}

			// Save the updated ExecutiveTaskExecution object in the repository.
			execution = executiveTaskExecutionRepository.save(execution);
			kilometerCalculationsDenormalisationService.saveLocation(kcd);

			// Create a new InvoiceWiseReportView with the updated ExecutiveTaskExecution
			// object.
			executionView = new InvoiceWiseReportView(execution);
			return new ResponseEntity<>(executionView, HttpStatus.OK);
		} else if (optionalAtt.isPresent()) {
			Attendance attendance = optionalAtt.get();
			if (!attendance.getLocation().equals("No Location")) {
				log.debug("Already Defined" + attendance.getLocation());
				kcd.setLocation(attendance.getLocation());
			} else if (attendance.getLatitude() != BigDecimal.ZERO
					&& attendance.getLatitude() != null && attendance.getLongitude() != null
					&& attendance.getLongitude() != BigDecimal.ZERO) {
				String location  = null;
				try{
					location = geoLocationService
							.findAddressFromLatLng(attendance.getLatitude() + "," + attendance.getLongitude());
				}catch(NullPointerException ex){
					log.debug("Catching Error ");
					attendance.setLocation("Location Not Found !");
					kcd.setLocation("Location Not Found !");
				}catch(HttpClientErrorException exe){
					log.debug("Catching Error ");
					attendance.setLocation("Location Not Found !");
					kcd.setLocation("Location Not Found !");
				}
				attendance.setLocation(location != null && location !=""  ? location : "Location Not Found !");
				kcd.setLocation(location != null && location !=""  ? location : "Location Not Found !");
			} else {
				attendance.setLocation("Location Not Found !");
				kcd.setLocation("Location Not Found !");
			}
			attendanceRepository.save(attendance);
			kilometerCalculationsDenormalisationService.saveLocation(kcd);
			return new ResponseEntity<>(executionView, HttpStatus.OK);
		} else if (optionalPunchOut.isPresent()) {
			PunchOut punchOut = optionalPunchOut.get();
			if (!punchOut.getLocation().equals("No Location")) {
				log.debug("Already Defined" + punchOut.getLocation());
				kcd.setLocation(punchOut.getLocation());
			} else if (punchOut.getLatitude() != BigDecimal.ZERO
					&& punchOut.getLatitude() != null && punchOut.getLongitude() != null
					&& punchOut.getLongitude() != BigDecimal.ZERO) {

				String location  = null;
				try{
					location = geoLocationService
							.findAddressFromLatLng(punchOut.getLatitude() + "," + punchOut.getLongitude());
				}catch(NullPointerException ex){
					log.debug("Catching Error ");
					punchOut.setLocation("Location Not Found !");
					kcd.setLocation("Location Not Found !");
				}catch(HttpClientErrorException exe){
					log.debug("Catching Error ");
					punchOut.setLocation("Location Not Found !");
					kcd.setLocation("Location Not Found !");
				}
				punchOut.setLocation(location != null && location !=""  ? location : "Location Not Found !");
				kcd.setLocation(location != null && location !=""  ? location : "Location Not Found !");
			} else {
				punchOut.setLocation("Location Not Found !");
				kcd.setLocation("Location Not Found !");
			}
			punchOutRepository.save(punchOut);
			kilometerCalculationsDenormalisationService.saveLocation(kcd);
			return new ResponseEntity<>(executionView, HttpStatus.OK);
		}
		// Return the ResponseEntity with the InvoiceWiseReportView containing the
		// updated location information.
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}


	private ExecutiveTaskExecution getPucnhOutExeTExe(PunchOut punchOut) {
		log.debug(" Adding PunchOut ");
		AccountProfile PunchoutaccountProfile = new AccountProfile();
		ExecutiveTaskExecution punchoutExecutiveTaskExecution = new ExecutiveTaskExecution();
		PunchoutaccountProfile.setName("PunchOut");
		punchoutExecutiveTaskExecution.setUser(punchOut.getUser());
		punchoutExecutiveTaskExecution.setLatitude(punchOut.getLatitude());
		punchoutExecutiveTaskExecution.setLongitude(punchOut.getLongitude());
		punchoutExecutiveTaskExecution.setDate(punchOut.getCreatedDate());
		punchoutExecutiveTaskExecution.setSendDate(punchOut.getPunchOutDate());
		punchoutExecutiveTaskExecution.setAccountProfile(PunchoutaccountProfile);
		punchoutExecutiveTaskExecution.setLocation(punchOut.getLocation());
		punchoutExecutiveTaskExecution.setPid(punchOut.getPid());
		return punchoutExecutiveTaskExecution;
	}

	private ExecutiveTaskExecution getAttendenceExeTExe(
			Attendance attendance) {
		log.debug("-------------------------------------------");
		ExecutiveTaskExecution attadenceExecutiveTaskExecution = new ExecutiveTaskExecution();
		// Set properties for the ExecutiveTaskExecution object based on attendance data
		AccountProfile attendenceaccountProfile = new AccountProfile();
		attendenceaccountProfile.setName("Attendence");
		attadenceExecutiveTaskExecution.setUser(attendance.getUser());
		log.debug("Attendence lattitude : " + attendance.getLatitude());
		log.debug("Attendence lattitude : " + attendance.getLongitude());
		attadenceExecutiveTaskExecution.setLatitude(attendance.getLatitude());
		attadenceExecutiveTaskExecution.setLongitude(attendance.getLongitude());
		attadenceExecutiveTaskExecution.setDate(attendance.getPlannedDate());
		attadenceExecutiveTaskExecution.setSendDate(attendance.getPlannedDate());
		attadenceExecutiveTaskExecution.setAccountProfile(attendenceaccountProfile);
		attadenceExecutiveTaskExecution.setLocation(attendance.getLocation());
		attadenceExecutiveTaskExecution.setPid(attendance.getPid());
		return attadenceExecutiveTaskExecution;
	}

	public <T> void convertToJson(Object collection, String message) {
		ObjectMapper objectMapper = getObjectMapper();
		try {
			log.info(System.lineSeparator());
			String jsonString = objectMapper.writeValueAsString(collection);
			log.debug(message + jsonString + System.lineSeparator());
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public ObjectMapper getObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
		return mapper;
	}

	private static String getUser(
			String userlogin,
			Optional<EmployeeProfile> employee) {
		if (employee.isPresent()) {
			userlogin = employee
					.get()
					.getUser()
					.getLogin();
		}
		return userlogin;
	}


	@RequestMapping(value = "/kilo-calc-test", method = RequestMethod.GET)
	@Timed
	public KilometerCalculationDenormalised QueryTest(@RequestParam  String userPid,@RequestParam String fromDate , @RequestParam String toDate , @RequestParam long companyId){
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDateTime fromDateTime = LocalDate.parse(fromDate, formatter).atTime(00,00);
		LocalDateTime toFateTime = LocalDate.parse(toDate, formatter).atTime(23,59);
		return kilometerCalculationsDenormalisationService.testQuery(userPid,fromDateTime,toFateTime,companyId);
	}

}

// Daybraking but not use beacuase we removed custom fillter
//				if (destinationExecutiveTaskExecution.getSendDate().toLocalDate()
//						.isAfter(originExecutiveTaskExecution.getSendDate().toLocalDate())) {
//					ExecutiveTaskExecution firstpoint = destinationExecutiveTaskExecution;
//					LogggNestDay(destinationExecutiveTaskExecution, firstpoint);
//					Optional<KilometerCalculationDTO> optionalKilometerCalculationDTO = ExistingKiloCalcDTO
//							.stream()
//							.filter(data -> data.getTaskExecutionPid()
//									.equals(firstpoint.getPid())
//									&& data.getCreatedDate()
//									.equals(firstpoint.getSendDate()))
//							.findAny();
//					if (!optionalKilometerCalculationDTO.isPresent()) {
//						List<ExecutiveTaskExecution> DaybrakeExecutiveTaskExecution = new ArrayList<>();
//						DaybrakeExecutiveTaskExecution.add(destinationExecutiveTaskExecution);
//						startingPointkilometerDTO(DaybrakeExecutiveTaskExecution, kiloCalDTOs, employee, Thread);
//					}
//					continue;
//				}