
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
//@RestController
//@RequestMapping("/api")
public class KilometerCalculationNewResourceMonthWise {

	private final Logger log = LoggerFactory.getLogger(KilometerCalculationNewResourceMonthWise.class);
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
	@RequestMapping(value = "/kilo-calc-new-month", method = RequestMethod.GET)
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
		return "company/kilometer-calculation-month-wise";
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
	@RequestMapping(value = "/kilo-calc-new-month/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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

		ExistingKiloCalcDTO = ExistingKiloCalcDTO
				.stream()
				.sorted(Comparator.comparing(KilometerCalculationDTO::getExecreatedDate)
						.reversed())
				.collect(Collectors.toList());
		return ExistingKiloCalcDTO;
	}


	/**
	 * This method handles a GET request to update the location information of an
	 * executive task execution.
	 *
	 * @param pid The unique identifier for the executive task execution.
	 * @return ResponseEntity containing the updated InvoiceWiseReportView with the
	 *         location information.
	 */
	@RequestMapping(value = "/kilo-calc-new-month/updateLocationExeTask/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
				}catch (Exception e){
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
				}catch (Exception e){
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
				}catch(Exception ex){
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
}
