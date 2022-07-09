package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.VisitReportView;

@Controller
@RequestMapping("/web")
public class CustomerWiseItemSalesReportResource {

	private final Logger log = LoggerFactory.getLogger(CustomerWiseItemSalesReportResource.class);

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@RequestMapping(value = "/customer-wise-itemsale", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of stockDetails");
//		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
//
//		if (userIds.isEmpty()) {
//			String user = SecurityUtils.getCurrentUserLogin();
//			System.out.println("if.." + userIds.size());
//			long userId = userRepository.getIdByLogin(user);
//			userIds = new ArrayList<>();
//			userIds.add(userId);
//			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
//		} else {
//			System.out.println("else .." + userIds.size());
//			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
//		}

		
		
		model.addAttribute("products", productProfileRepository.findAllByCompanyIdActivatedTrue());
		return "company/customerWiseItemSale";

	}

	@RequestMapping(value = "/customer-wise-itemsale/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Map<String, List<StockDetailsDTO>>> filter(@RequestParam("employeePid") String employeePid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam boolean inclSubordinate) {
		log.debug("Web request to filter Stock Details");

		Map<String, List<StockDetailsDTO>> salesDetails = new HashMap<>();
		if (filterBy.equals("TODAY")) {
			salesDetails = getFilterData(employeePid, LocalDate.now(), LocalDate.now(), inclSubordinate);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yesterday = LocalDate.now().minusDays(1);
			salesDetails = getFilterData(employeePid, yesterday, yesterday, inclSubordinate);

		} else if (filterBy.equals("SINGLE")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			salesDetails = getFilterData(employeePid, fromDateTime, fromDateTime, inclSubordinate);
		}
		

		return new ResponseEntity<>(salesDetails, HttpStatus.OK);

	}

	private Map<String, List<StockDetailsDTO>> getFilterData(String employeePid, LocalDate fDate, LocalDate tDate,
			boolean inclSubordinate) {
		// TODO Auto-generated method stub
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<Long> userIds = getUserIdsUnderCurrentUser(employeePid, inclSubordinate);
		log.info("User Ids :" + userIds);

		Map<String, List<StockDetailsDTO>> stockDetails = new HashMap<>();
		stockDetails = inventoryVoucherHeaderService.findAllSalesDetails(companyId, userIds, fromDate, toDate);

//		List<String> pnames = productProfileRepository.findProductNamesByCompanyId();
//
//		StockDetailsDTO stockDTO = new StockDetailsDTO();
//		stockDTO.setProductList(pnames);
//		stockDetails.add(stockDTO);
		
		
		
      

		return stockDetails;

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
