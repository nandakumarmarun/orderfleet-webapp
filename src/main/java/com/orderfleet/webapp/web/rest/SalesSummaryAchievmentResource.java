package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileLocationService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.SalesSummaryAchievmentService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.SalesSummaryAchievmentDTO;

/**
 * Web controller for managing SalesSummaryAchievment.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesSummaryAchievmentResource {

	private final Logger log = LoggerFactory.getLogger(SalesSummaryAchievmentResource.class);

	@Inject
	private SalesSummaryAchievmentService salesSummaryAchievmentService;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeProfileLocationService employeeProfileLocationService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGroupRepository;

	/**
	 * GET /sales-summary-achievments : get salesSummaryAchievments.
	 *
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@RequestMapping(value = "/sales-summary-achievments", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSalesSummaryAchievments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of SalesSummaryAchievments");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}

		model.addAttribute("productGroups", productGroupService.findAllByCompanyAndDeactivatedProductGroup(true));

		return "company/salesSummaryAchievments";
	}

	/**
	 * POST /sales-summary-achievments : Create a new salesSummaryAchievment.
	 *
	 * @param salesSummaryAchievmentDTO the salesSummaryAchievmentDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body the new
	 *         salesSummaryAchievmentDTO, or with status 400 (Bad Request) if the
	 *         salesSummaryAchievment has already an ID
	 * @throws URISyntaxException if the Location URI syntax is incorrect
	 */
	@RequestMapping(value = "/sales-summary-achievments", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<?> createSalesSummaryAchievment(
			@RequestBody List<SalesSummaryAchievmentDTO> salesSummaryAchievments) throws URISyntaxException {
		log.debug("Web request to save SalesSummaryAchievments ");
		salesSummaryAchievmentService.saveSalesSummaryAchievments(salesSummaryAchievments);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/sales-summary-achievments/monthly-data", method = RequestMethod.GET)
	public @ResponseBody List<SalesSummaryAchievmentDTO> monthlySalesSummaryAchievment(@RequestParam String employeePid,
			@RequestParam String locationPid, @RequestParam String productGroupPid,
			@RequestParam(value = "date", required = true) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
		log.debug("Web request to get Sales Summary Achievments");
		List<SalesTargetGroup> userSalesTargetGroups = new ArrayList<>();

		if (productGroupPid.equalsIgnoreCase("no")) {
			userSalesTargetGroups = salesTargetGroupUserTargetRepository.findAllSalesTargetGroupByCompanyId();
		} else {

			userSalesTargetGroups = productGroupSalesTargetGroupRepository
					.findSalesTargetGroupByProductGroupPid(productGroupPid);

		}

		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("-1")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = null;
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		if (!userPid.equals(null)) {
			if (userSalesTargetGroups.size() > 0) {

				LocalDate start = date.withDayOfMonth(1);
				LocalDate end = date.withDayOfMonth(date.lengthOfMonth());

				List<SalesSummaryAchievmentDTO> salesSummaryAchievmentDTOs = new ArrayList<>();

				for (SalesTargetGroup userSalesTargetGroup : userSalesTargetGroups) {
					SalesSummaryAchievmentDTO salesSummaryAchievmentDTO = new SalesSummaryAchievmentDTO();
					salesSummaryAchievmentDTO.setPid("no");
					salesSummaryAchievmentDTO.setSalesTargetGroupPid(userSalesTargetGroup.getPid());
					salesSummaryAchievmentDTO.setSalesTargetGroupName(userSalesTargetGroup.getName());

					SalesSummaryAchievment salesSummaryAchievment = salesSummaryAchievmentService
							.findByUserPidAndSalesTargetGroupPidAndDateBetweenAndLocationPid(userPid,
									userSalesTargetGroup.getPid(), start, end, locationPid);
					if (salesSummaryAchievment != null) {
						salesSummaryAchievmentDTO.setPid(salesSummaryAchievment.getPid());
						salesSummaryAchievmentDTO.setAmount(salesSummaryAchievment.getAmount());
					}
					salesSummaryAchievmentDTOs.add(salesSummaryAchievmentDTO);
				}

				salesSummaryAchievmentDTOs.sort((SalesSummaryAchievmentDTO s1, SalesSummaryAchievmentDTO s2) -> s1
						.getSalesTargetGroupName().toLowerCase().compareTo(s2.getSalesTargetGroupName().toLowerCase()));
				return salesSummaryAchievmentDTOs;
			}
		}
		return null;
	}

	@RequestMapping(value = "/sales-summary-achievments/loadLocation", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<LocationDTO>> getLocationByUser(@RequestParam String employeePid) {
		List<LocationDTO> locationDTOs = employeeProfileLocationService.findLocationsByEmployeeProfilePid(employeePid);
		return new ResponseEntity<List<LocationDTO>>(locationDTOs, HttpStatus.OK);

	}

}
