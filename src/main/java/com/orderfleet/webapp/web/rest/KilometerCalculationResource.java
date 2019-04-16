
package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
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
import com.orderfleet.webapp.domain.KilometreCalculation;
import com.orderfleet.webapp.repository.KilometreCalculationRepository;
import com.orderfleet.webapp.service.DistanceFareService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;

/**
 * @author Anish
 *
 */
@Controller
@RequestMapping("/web")
public class KilometerCalculationResource {

	private final Logger log = LoggerFactory.getLogger(KilometerCalculationResource.class);

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private EmployeeProfileService employeeProfileService;
		
	@Inject
	private KilometreCalculationRepository kilometreCalculationRepository;
	
	@Inject
	private DistanceFareService distanceFareService;
	
	
	@RequestMapping(value = "/kilo-calc", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getKilometerCalculation(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of kilometer calculation report");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		model.addAttribute("distanceFare",distanceFareService.findAllByCompany());
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/kilometer-calculation";
	}
	
	
//	@RequestMapping(value = "/kilo-calc/get-account-profile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
//	@Timed
//	public ResponseEntity<List<AccountProfileDTO>> getAllAccountProfiles(@RequestParam("userPid") String userPid) {
//		log.debug("Web request to get list of Account Profiles using userPid ", userPid);
//
//		List<String> locationPids = employeeProfileLocationService.findLocationsByUserPid(userPid).stream()
//				.map(a -> a.getPid()).collect(Collectors.toList());
//		List<AccountProfileDTO> accountProfiles = locationAccountProfileService
//				.findAccountProfileByLocationPidIn(locationPids);
//
//		return new ResponseEntity<>(accountProfiles, HttpStatus.OK);
//	}
	
	@RequestMapping(value = "/kilo-calc/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<KilometerCalculationDTO>> getAllKilometerCalculationByAccountProfile(
			@RequestParam("userPid") String userPid, /*@RequestParam String accountProfilePid,*/
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to get kilometre calculation filtered report by userPid " + userPid);
		String accountProfilePid = "no";
		List<KilometerCalculationDTO> kilometreCalculationDTO = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, LocalDate.now(), LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, weekStartDate, LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, monthStartDate, LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			kilometreCalculationDTO = getFilterData(userPid, accountProfilePid, fromDateTime, toFateTime);
		}

		return new ResponseEntity<>(kilometreCalculationDTO, HttpStatus.OK);
	}
	
	private List<KilometerCalculationDTO> getFilterData(String userPid, String accountProfilePid, LocalDate fDate,
			LocalDate tDate) {
//		LocalDateTime fromDate = fDate.atTime(0, 0);
//		LocalDateTime toDate = tDate.atTime(23, 59);


		List<KilometreCalculation> kilometreCalculations = new ArrayList<>();
		if (!userPid.equals("no") && accountProfilePid.equals("no")) {

			kilometreCalculations = kilometreCalculationRepository.findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(userPid,fDate,tDate);
//			kilometreCalculations.removeIf(u -> u.getExecutiveTaskExecution() == null);
		}	

		List<KilometerCalculationDTO> result = kilometreCalculations.stream().map(KilometerCalculationDTO::new)
				.collect(Collectors.toList());
		
		result = result.stream().sorted(Comparator.comparing(KilometerCalculationDTO::getDate).reversed()).collect(Collectors.toList());
		
		return result;

	
	}
	
}
