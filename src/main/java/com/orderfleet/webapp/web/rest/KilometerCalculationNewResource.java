
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

	/**
	 * Retrieves filtered data of distance traveled for a specific user and account profile
	 * between the specified start and end dates.
	 *
	 * @param userPid The unique identifier for the user.
	 * @param accountProfilePid The unique identifier for the account profile (not used in the method).
	 * @param fDate The start date (LocalDate) for filtering data.
	 * @param tDate The end date (LocalDate) for filtering data.
	 * @return A list of KilometerCalculationDTO objects representing the distance traveled.
	 * @throws TaskSubmissionPostSaveException If an error occurs during the distance calculation.
	 */
	private List<KilometerCalculationDTO> getFilterData(
			String userPid, String accountProfilePid,
			LocalDate fDate, LocalDate tDate)
			throws TaskSubmissionPostSaveException {
		log.debug("Request to get distance Traveled : Enter:getFilterData() - " +
				"[ userPid : "+userPid+" accountProfilePid : "+"accountProfilePid " +
				" fromDate : "+fDate+" toDate : "+tDate+"]" );
		// Initialize variables
		String userlogin = null;
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
    Optional<PunchOut> optionalPunchOut = null;
		List<ExecutiveTaskExecution> lastExecutiveTaskExecution = new ArrayList<>();
		List<KilometreCalculation> kilometreCalculations = new ArrayList<>();
		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();

		// Retrieve company and employee information from the database
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		Optional<EmployeeProfile> employee = employeeProfileRepository.findByUserPid(userPid);
		userlogin = getUser(userlogin, employee);
		String Thread = userlogin +"-"+ company.getId();

		// Find attendance data for the user within the specified date range
		Optional<Attendance> attendance =
				attendanceRepository.findTop1ByCompanyPidAndUserPidAndCreatedDateBetweenOrderByCreatedDateDesc(
								company.getPid(), userPid,fromDate,toDate);

		// Retrieve a list of executive task executions for the user within the date range
		lastExecutiveTaskExecution =
				executiveTaskExecutionRepository.findAllByCompanyIdUserPidAndDateBetweenOrderBySendDateAsc(
						userPid, fDate.atTime(0, 0), tDate.atTime(23, 59));
		log.debug(Thread+" : " + "Order Details :  " + lastExecutiveTaskExecution.size());

		// Assign attendance and punch-out data if available
		// Adding Attendance object to the list of ExecutiveTaskExecution objects
		if (attendance.isPresent() && attendance.get().getCreatedDate().toLocalDate().isEqual(fDate)) {
			log.debug(Thread +" : " + "Attendence :  Present" + " On " + attendance.get().getCreatedDate().toLocalDate());

			// Retrieve punchout information from the database
			optionalPunchOut = punchOutRepository.findIsAttendancePresent(attendance.get().getPid());
			log.debug("Punchouted : " + optionalPunchOut.isPresent());
			log.debug(Thread +" : Assigning sAttandence" );

			// Create a new AccountProfile object for the attendance
			AccountProfile attendenceaccountProfile = new AccountProfile();
			attendenceaccountProfile.setName("Attendence");

			// Create a new ExecutiveTaskExecution object for the attendance
			ExecutiveTaskExecution attadenceExecutiveTaskExecution = new ExecutiveTaskExecution();

			// Set properties for the ExecutiveTaskExecution object based on attendance data
			attadenceExecutiveTaskExecution.setUser(attendance.get().getUser());
			attadenceExecutiveTaskExecution.setLatitude(attendance.get().getLatitude());
			attadenceExecutiveTaskExecution.setLongitude(attendance.get().getLongitude());
			attadenceExecutiveTaskExecution.setDate(attendance.get().getPlannedDate());
			attadenceExecutiveTaskExecution.setSendDate(attendance.get().getPlannedDate());
			attadenceExecutiveTaskExecution.setAccountProfile(attendenceaccountProfile);
			attadenceExecutiveTaskExecution.setLocation(attendance.get().getLocation());
			attadenceExecutiveTaskExecution.setPid(attendance.get().getPid());

			// Add the ExecutiveTaskExecution object to the beginning of the lastExecutiveTaskExecution list
			lastExecutiveTaskExecution.add(0,attadenceExecutiveTaskExecution);

		 	//Assigning Punchout origin
			if(optionalPunchOut.isPresent()){
				log.debug(Thread +" : Assigning Punchout" );
				PunchOut punchOut = optionalPunchOut.get();

				// Create a new AccountProfile object for the punch Out
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

				// Add the ExecutiveTaskExecution object to the lastExecutiveTaskExecution list
				lastExecutiveTaskExecution.add(punchoutExecutiveTaskExecution);
			}
		}

		// Sort the list of ExecutiveTaskExecution objects by the sendDate
		lastExecutiveTaskExecution = lastExecutiveTaskExecution.stream()
				.sorted(Comparator.comparing(ExecutiveTaskExecution::getSendDate))
				.collect(Collectors.toList());
		log.debug(Thread+" : " + "Order Details :  " + lastExecutiveTaskExecution.size());

		// Calculate the starting point kilometer
		if(lastExecutiveTaskExecution.size() > 0){
			startingPointkilometerDTO(lastExecutiveTaskExecution,kiloCalDTOs, employee, Thread);
		}

		//Iterating through the list of ExecutiveTaskExecution objects to calculate the distance between points.
		for (int i = 0; i < lastExecutiveTaskExecution.size()-1; i++)
		{
			ExecutiveTaskExecution originExecutiveTaskExecution = null;
			ExecutiveTaskExecution destinationExecutiveTaskExecution = new ExecutiveTaskExecution();

			// Iterating to find the origin location for the current destination
			for(int j = i; j<lastExecutiveTaskExecution.size() && j>=0; j--){
				originExecutiveTaskExecution = new ExecutiveTaskExecution();

				// Iterating Origin if null its go-to backwards
				if (lastExecutiveTaskExecution.get(j).getLongitude() != null
                        && lastExecutiveTaskExecution.get(j).getLongitude().doubleValue() != 0
                        && lastExecutiveTaskExecution.get(j).getLatitude()  != null
                        && lastExecutiveTaskExecution.get(j).getLatitude().doubleValue() != 0) {
                    originExecutiveTaskExecution = lastExecutiveTaskExecution.get(j);
					break;
				}
				log.debug(Thread+" : " +lastExecutiveTaskExecution.get(j).getAccountProfile().getName().toString() + " : " + "NoLocation");
			}

			// Iterating Destination if null, add default
			if (lastExecutiveTaskExecution.get(i + 1).getLongitude() != null
                    && lastExecutiveTaskExecution.get(i + 1).getLongitude().doubleValue() != 0
                    && lastExecutiveTaskExecution.get(i + 1).getLatitude() != null
                    && lastExecutiveTaskExecution.get(i + 1).getLatitude().doubleValue() != 0) {
                destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);
			} else {

				//Adding Default kilometre
				defaultkilometerDTO(lastExecutiveTaskExecution, kiloCalDTOs, employee, Thread, i);
				continue;
			}

			// Adding Default kilometer in case the origin is null
			if(originExecutiveTaskExecution.getLatitude() == null
					&& originExecutiveTaskExecution.getLongitude() == null){
			defaultkilometerDTO(lastExecutiveTaskExecution, kiloCalDTOs, employee, Thread, i);
		  } else{
				String origin = originExecutiveTaskExecution.getLatitude()+","+originExecutiveTaskExecution.getLongitude();
				String destination = destinationExecutiveTaskExecution.getLatitude() +","+destinationExecutiveTaskExecution.getLongitude();
				log.debug(Thread+" : "+"Distance traveled From " + originExecutiveTaskExecution.getAccountProfile().getName()
						+ " TO "+ destinationExecutiveTaskExecution.getAccountProfile().getName());
				log.debug(Thread+" : "+"origin   :  " + origin + "destination   :  " + destination);
				try {

					// Calculating Distance from origin to Destination
					kiloCalDTOs.add(getDistance(origin, destination, destinationExecutiveTaskExecution));
				} catch (TaskSubmissionPostSaveException e) {
					throw new RuntimeException(e);
				}
			}
		}
		log.debug("kilo meter Calculation : " + kiloCalDTOs);

		// Sort the list of KilometerCalculationDTO objects by the punchingTime in descending order
		kiloCalDTOs = kiloCalDTOs
				.stream()
				.sorted(Comparator.comparing(KilometerCalculationDTO::getPunchingTime)
						.reversed())
				.collect(Collectors.toList());
		log.debug(Thread +" : " +"Request to get distance Traveled : Exit :getFilterData() - " + kiloCalDTOs.toString());
		return kiloCalDTOs;
	}
	/**
	 *
	 * This method is used to generate a KilometerCalculationDTO object based on the given data.
	 * The object is then added to the list of KilometerCalculationDTOs.
	 * @param lastExecutiveTaskExecution
	 * @param kiloCalDTOs
	 * @param employee
	 * @param Thread
	 * @param i
	 */
	private void defaultkilometerDTO(
			List<ExecutiveTaskExecution> lastExecutiveTaskExecution,
			List<KilometerCalculationDTO> kiloCalDTOs,
			Optional<EmployeeProfile> employee, String Thread, int i) {
		ExecutiveTaskExecution destinationExecutiveTaskExecution;

		// Get the next ExecutiveTaskExecution from the provided list
		destinationExecutiveTaskExecution = lastExecutiveTaskExecution.get(i + 1);
		log.debug(Thread +" : "+ destinationExecutiveTaskExecution.getAccountProfile() +" : Location Not Found");

		// Create a new KilometerCalculationDTO object and set its properties
		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
		kiloCalDTO.setKilometre(0);
		kiloCalDTO.setMetres(0);
		kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
		kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
		kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		log.debug("Date : " + destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
		kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
		kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
		if(employee.isPresent()){kiloCalDTO.setEmployeeName(employee.get().getName());}
		kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setEndLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());

		// Add the KilometerCalculationDTO object to the kiloCalDTOs list
		kiloCalDTOs.add(kiloCalDTO);
	}
	/**
	 * Initializes a KilometerCalculationDTO object based on the provided parameters.
	 *
	 * @param lastExecutiveTaskExecution The list of last executed executive task executions.
	 * @param kiloCalDTOs                The list of KilometerCalculationDTO objects to store the data.
	 * @param employee                   An optional EmployeeProfile object representing the employee information.
	 * @param Thread                     The thread identifier (possibly for logging purposes).
	 */
	private void startingPointkilometerDTO(
			List<ExecutiveTaskExecution> lastExecutiveTaskExecution,
			List<KilometerCalculationDTO> kiloCalDTOs,
			Optional<EmployeeProfile> employee, String Thread) {
		ExecutiveTaskExecution destinationExecutiveTaskExecution;

		// Get the first ExecutiveTaskExecution from the list as the destinationExecutiveTaskExecution.
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
		log.debug("Date : " + destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
		kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
		kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
		if(employee.isPresent()){kiloCalDTO.setEmployeeName(employee.get().getName());}
		kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setEndLocation(destinationExecutiveTaskExecution.getLocation());
		kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
		kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());

		// Add the initialized KilometerCalculationDTO object to the list of DTOs.
		kiloCalDTOs.add(kiloCalDTO);
	}
	/**
	 * Calculates the distance between the given origin and destination using a distance API.
	 *
	 * @param origin The latitude and longitude of the origin location in the format "latitude,longitude".
	 * @param destination The latitude and longitude of the destination location in the format "latitude,longitude".
	 * @param destinationExecutiveTaskExecution The ExecutiveTaskExecution object associated with the destination location.
	 * @return A KilometerCalculationDTO object containing the calculated distance details.
	 * @throws TaskSubmissionPostSaveException If an error occurs during the distance calculation or API call.
	 */
	public  KilometerCalculationDTO getDistance(
			String origin ,
			String destination,
			ExecutiveTaskExecution destinationExecutiveTaskExecution)
			throws TaskSubmissionPostSaveException {
		List<KilometerCalculationDTO> kiloCalDTOs = new ArrayList<KilometerCalculationDTO>();
		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
		MapDistanceApiDTO distanceApiJson = null;
		MapDistanceDTO distance = null;
		EmployeeProfile employee =
				employeeProfileRepository
						.findEmployeeProfileByUser(
								destinationExecutiveTaskExecution.getUser());
		try{

			// Call the distance API to get the distance between the origin and destination
			distanceApiJson = geoLocationService.findDistance(origin, destination);
			if (distanceApiJson != null && !distanceApiJson.getRows().isEmpty()) {

				// Extract distance details from the API response
				distance = distanceApiJson.getRows().get(0).getElements().get(0).getDistance();
				if (distance != null) {

					// Populate the KilometerCalculationDTO object with the calculated distance details
					kiloCalDTO.setKilometre(distance.getValue() * 0.001);
					kiloCalDTO.setMetres(distance.getValue());
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					log.debug("Punching Time : "+destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					log.debug("Punching Date : "+destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("Location : "+destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				} else{

					// If distance is not available, set default values in the KilometerCalculationDTO object
					log.debug("Attendance latitude and longitudes are not accurate.");
					kiloCalDTO.setKilometre(0.0);
					kiloCalDTO.setMetres(0.0);
					kiloCalDTO.setUserPid(destinationExecutiveTaskExecution.getUser().getPid());
					kiloCalDTO.setUserName(destinationExecutiveTaskExecution.getUser().getFirstName());
					kiloCalDTO.setAccountProfileName(destinationExecutiveTaskExecution.getAccountProfile().getName());
					kiloCalDTO.setDate(destinationExecutiveTaskExecution.getDate().toLocalDate());
					log.debug("Punching Time : "+destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					kiloCalDTO.setPunchingTime(destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					log.debug("Punching Date : "+destinationExecutiveTaskExecution.getSendDate().toLocalTime().toString());
					kiloCalDTO.setPunchingDate(destinationExecutiveTaskExecution.getSendDate().toLocalDate().toString());
					kiloCalDTO.setEmployeeName(employee.getName());
					log.debug("Location : "+destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setLocation(destinationExecutiveTaskExecution.getLocation());
					kiloCalDTO.setTaskExecutionPid(destinationExecutiveTaskExecution.getPid());
				}
			}
			log.debug(kiloCalDTO.toString());
			return kiloCalDTO;
		}
		catch (Exception e) {

			// Handle exceptions and throw a custom exception with relevant information
			log.debug("Exception while processing saveKilometreDifference method {}", e);
			throw new TaskSubmissionPostSaveException("Exception while processing saveKilometreDifference method. "
					+ "Company : " + destinationExecutiveTaskExecution.getCompany().getLegalName() + " User:"
					+ destinationExecutiveTaskExecution.getUser().getLogin() + " Disatance API JSON :" + distanceApiJson
					+ " Exception : " + e);
		}
	}
	/**
	 * This method handles a GET request to update the location information of an executive task execution.
	 *
	 * @param pid The unique identifier for the executive task execution.
	 * @return ResponseEntity containing the updated InvoiceWiseReportView with the location information.
	 */
	@RequestMapping(value = "/kilo-calc-new/updateLocationExeTask/{pid}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<InvoiceWiseReportView>
	updateLocationExecutiveTaskExecutions(
			@PathVariable String pid) {

		// Find the ExecutiveTaskExecution by its unique identifier (pid) using the repository.
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);

		// Create a new InvoiceWiseReportView to store the updated information.
		InvoiceWiseReportView executionView = new InvoiceWiseReportView();

		// Check if the ExecutiveTaskExecution is present in the repository.
		if (opExecutiveeExecution.isPresent()) {

			// Get the ExecutiveTaskExecution object from the Optional.
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();
			if (execution.getLatitude() != BigDecimal.ZERO) {

				// Call the geoLocationService to find the address from the latitude and longitude.
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);

				// Update the location of the ExecutiveTaskExecution.
				execution.setLocation(location);
			} else {
				System.out.println("-------No Location");

				// Set the location as "No Location" if latitude is zero (invalid location).
				execution.setLocation("No Location");
			}

			// Save the updated ExecutiveTaskExecution object in the repository.
			execution = executiveTaskExecutionRepository.save(execution);

			// Create a new InvoiceWiseReportView with the updated ExecutiveTaskExecution object.
			executionView = new InvoiceWiseReportView(execution);
		}

		// Return the ResponseEntity with the InvoiceWiseReportView containing the updated location information.
		return new ResponseEntity<>(executionView, HttpStatus.OK);
	}


	private static String getUser(
			String userlogin,
			Optional<EmployeeProfile> employee) {
		if(employee.isPresent())
		{
			userlogin =
					employee
							.get()
							.getUser()
							.getLogin();
		}
		return userlogin;
	}

}
