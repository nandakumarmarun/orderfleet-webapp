package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.TargetFrequency;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupUserTargetService;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;

/**
 * Web controller for managing DayWiseTargetSetting.
 *
 * @author Sarath
 * @since Dec 8, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class DayWiseTargetSettingResource {

	private final Logger log = LoggerFactory.getLogger(DayWiseTargetSettingResource.class);

	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	private SalesTargetGroupUserTargetService salesTargetGroupUserTargetService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	public DayWiseTargetSettingResource(SalesTargetGroupService salesTargetGroupService,
			SalesTargetGroupUserTargetService salesTargetGroupUserTargetService) {
		super();
		this.salesTargetGroupService = salesTargetGroupService;
		this.salesTargetGroupUserTargetService = salesTargetGroupUserTargetService;
	}

	/**
	 * GET /day-wise-user-target-group-setting : get dayWiseTargetSetting list.
	 *
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/day-wise-user-target-group-setting", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAttendanceReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of day-wise-user-target-group-setting");
		model.addAttribute("salesTargetGroups", salesTargetGroupService.findAllByCompany());
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("salesTargetGroupUserTargets",
				salesTargetGroupUserTargetService.findAllByCompanyIdAndTargetFrequencyAndDateBetween(
						TargetFrequency.DAY, LocalDate.now(), LocalDate.now()));
		return "company/dayWiseUserTargetGroupSetting";
	}

	@RequestMapping(value = "/day-wise-user-target-group-setting/filter", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<SalesTargetGroupUserTargetDTO>> filterSalesTargetGroupUserTargets(
			@RequestParam String employeePid, @RequestParam String salesTargetGroupPid, @RequestParam String fromDate,
			@RequestParam String toDate) {
		log.debug("Web request to filter SalesTargetGroupUserTargets : {}");
		String userPid = null;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate startDate = LocalDate.parse(fromDate, formatter);
		LocalDate endDate = LocalDate.parse(toDate, formatter);

		Optional<EmployeeProfileDTO> employee = employeeProfileService.findOneByPid(employeePid);
		if (employee.isPresent()) {
			userPid = employee.get().getUserPid();

			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetService
					.findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(userPid, salesTargetGroupPid,
							startDate, endDate, TargetFrequency.DAY);
			return new ResponseEntity<>(salesTargetGroupUserTargetDTOs, HttpStatus.OK);
		}
		return null;
	}

	@RequestMapping(value = "/day-wise-user-target-group-setting/load", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<List<SalesTargetGroupUserTargetDTO>> getSalesTargetGroupUserTarget(
			@RequestParam String employeePid, @RequestParam String salesTargetGroupPid, @RequestParam String fromDate,
			@RequestParam String toDate) {
		log.debug("Web request to get SalesTargetGroupUserTarget : {}", employeePid);
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate startDate = LocalDate.parse(fromDate, formatter);
		LocalDate endDate = LocalDate.parse(toDate, formatter);

		String userPid = null;
		Optional<EmployeeProfileDTO> employee = employeeProfileService.findOneByPid(employeePid);
		if (employee.isPresent()) {
			userPid = employee.get().getUserPid();

			List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs = salesTargetGroupUserTargetService
					.findUserAndSalesTargetGroupPidAndTargetFrequencyAndDateWise(userPid, salesTargetGroupPid,
							startDate, endDate, TargetFrequency.DAY);

			List<SalesTargetGroupUserTargetDTO> result = new ArrayList<>();

			for (LocalDate date = startDate; date.isBefore(endDate.plusDays(1)); date = date.plusDays(1)) {
				SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO = new SalesTargetGroupUserTargetDTO();
				salesTargetGroupUserTargetDTO.setUserPid(userPid);
				salesTargetGroupUserTargetDTO.setSalesTargetGroupPid(salesTargetGroupPid);
				salesTargetGroupUserTargetDTO.setFromDate(date);
				salesTargetGroupUserTargetDTO.setToDate(date);
				salesTargetGroupUserTargetDTO.setDay(date.getDayOfWeek().toString());
				salesTargetGroupUserTargetDTO.setVolume(0);
				result.add(salesTargetGroupUserTargetDTO);
			}

			for (SalesTargetGroupUserTargetDTO groupUserTargetDTO : salesTargetGroupUserTargetDTOs) {
				for (SalesTargetGroupUserTargetDTO salesTargetGroupUserTargetDTO : result) {
					if (salesTargetGroupUserTargetDTO.getFromDate().isEqual(groupUserTargetDTO.getFromDate())) {
						salesTargetGroupUserTargetDTO.setVolume(groupUserTargetDTO.getVolume());
						salesTargetGroupUserTargetDTO.setPid(groupUserTargetDTO.getPid());
					}
				}
			}
			return new ResponseEntity<>(result, HttpStatus.OK);
		}
		return null;
	}

	@RequestMapping(value = "/day-wise-user-target-group-setting", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<AccountGroupDTO> createAccountGroup(
			@Valid @RequestBody List<SalesTargetGroupUserTargetDTO> salesTargetGroupUserTargetDTOs)
			throws URISyntaxException {
		log.debug("Web request to save AccountGroup : {}", salesTargetGroupUserTargetDTOs);
		salesTargetGroupUserTargetService.saveUpdateDaylyTarget(salesTargetGroupUserTargetDTOs);
		return null;
	}

}
