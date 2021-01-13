package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.SalesProductGroupUserTarget;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesProductGroupUserTargetRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.SalesMonthlyProductGroupTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesProductGroupUserTargetDTO;

/**
 * Web controller for managing SalesProductGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class SalesProductGroupUserTargetResource {

	private final Logger log = LoggerFactory.getLogger(SalesProductGroupUserTargetResource.class);

	@Inject
	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private UserService userService;

	@Inject
	private DocumentService documentService;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private SalesProductGroupUserTargetRepository salesProductGroupUserTargetRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * GET /user-monthly-sales-targets : get all the salesUserTargets.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         salesUserTargets in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/user-monthly-sales-product-group-targets", method = RequestMethod.GET)
	public String setMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set Sales Product Group Target");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("productGroups", productGroupService.findAllByCompanyAndDeactivatedProductGroup(true));
		return "company/set-monthly-sales-product-group-target";
	}

	@RequestMapping(value = "/user-monthly-sales-product-group-targets/monthly-sales-targets", method = RequestMethod.GET)
	public @ResponseBody List<SalesMonthlyProductGroupTargetDTO> monthlySalesTargets(@RequestParam String employeePid,
			@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly Sales Targets");
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}

		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId(true);

		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		if (productGroups.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<SalesMonthlyProductGroupTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (ProductGroup productGroup : productGroups) {
				SalesMonthlyProductGroupTargetDTO monthlyTargetDTO = new SalesMonthlyProductGroupTargetDTO();
				monthlyTargetDTO.setProductGroupPid(productGroup.getPid());
				monthlyTargetDTO.setProductGroupName(productGroup.getName());
				monthlyTargetDTO.setUserPid(userPid);

				List<SalesProductGroupUserTarget> salesProductGroupUserTargets = salesProductGroupUserTargetRepository
						.findByUserPidAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(userPid,
								productGroup.getPid(), firstDateMonth, lastDateMonth);
				if (salesProductGroupUserTargets.size() > 0) {
					// monthlyTargetDTO.setTarget(salesProductGroupUserTargets.get(0).getTargetNumber());
					// monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
					double totalAmount = 0;
					double totalVolume = 0;
					for (SalesProductGroupUserTarget salesProductGroupUserTarget : salesProductGroupUserTargets) {
						totalAmount = totalAmount + salesProductGroupUserTarget.getAmount();
						totalVolume = totalVolume + salesProductGroupUserTarget.getVolume();
					}
					monthlyTargetDTO.setAmount(totalAmount);
					monthlyTargetDTO.setVolume(totalVolume);
					monthlyTargetDTO.setSalesProductGroupUserTragetPid(salesProductGroupUserTargets.get(0).getPid());
				} else {
					monthlyTargetDTO.setAmount(0);
					monthlyTargetDTO.setVolume(0);
				}
				monthlyTargetDTOs.add(monthlyTargetDTO);
			}
			return monthlyTargetDTOs;
		}
		return null;
	}

	@RequestMapping(value = "/user-monthly-sales-product-group-targets/monthly-sales-targets", method = RequestMethod.POST)
	public @ResponseBody SalesMonthlyProductGroupTargetDTO saveMonthlyActivityTargets(
			@RequestBody SalesMonthlyProductGroupTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Sales Targets"+monthlyTargetDTO);

		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService
				.findOneByPid(monthlyTargetDTO.getUserPid());
		if (employeeProfileDTO.isPresent()) {
			monthlyTargetDTO.setUserPid(employeeProfileDTO.get().getUserPid());
			if (monthlyTargetDTO.getSalesProductGroupUserTragetPid().equals("null")) {
				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
				int month = Integer.valueOf(monthAndYearArray[0]);
				int year = Integer.valueOf(monthAndYearArray[1]);
				YearMonth yearMonth = YearMonth.of(year, month);

				LocalDate firstDateMonth = yearMonth.atDay(1);
				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
				SalesProductGroupUserTargetDTO result = saveMonthlyTarget(monthlyTargetDTO, firstDateMonth,
						lastDateMonth);
				monthlyTargetDTO.setSalesProductGroupUserTragetPid(result.getPid());
			} else {
				salesProductGroupUserTargetRepository.findOneByPid(monthlyTargetDTO.getSalesProductGroupUserTragetPid())
						.ifPresent(activityUserTarget -> {
							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
							activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
							salesProductGroupUserTargetRepository.save(activityUserTarget);
						});
			}
		}
		return monthlyTargetDTO;
	}

	private SalesProductGroupUserTargetDTO saveMonthlyTarget(SalesMonthlyProductGroupTargetDTO monthlyTargetDTO,
			LocalDate firstDateMonth, LocalDate lastDateMonth) {
		log.info("Save monthly target location wise");
		SalesProductGroupUserTarget salesProductGroupUserTarget = new SalesProductGroupUserTarget();
		// set pid
		salesProductGroupUserTarget.setPid("SPGUT-" + RandomUtil.generatePid());
		salesProductGroupUserTarget
				.setProductGroup(productGroupRepository.findOneByPid(monthlyTargetDTO.getProductGroupPid()).get());
		// set company
		salesProductGroupUserTarget.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		salesProductGroupUserTarget.setFromDate(firstDateMonth);
		salesProductGroupUserTarget.setToDate(lastDateMonth);
		salesProductGroupUserTarget.setAmount(0);
		salesProductGroupUserTarget.setVolume(monthlyTargetDTO.getVolume());
		if (monthlyTargetDTO.getUserPid() != null) {
			salesProductGroupUserTarget.setUser(userRepository.findOneByPid(monthlyTargetDTO.getUserPid()).get());
		}

		salesProductGroupUserTarget = salesProductGroupUserTargetRepository.save(salesProductGroupUserTarget);
		SalesProductGroupUserTargetDTO result = new SalesProductGroupUserTargetDTO(salesProductGroupUserTarget);
		return result;
	}

}