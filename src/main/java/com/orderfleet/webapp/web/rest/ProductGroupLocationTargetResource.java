package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupLocationTarget;
import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroupUserTarget;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.ProductGroupLocationTargetRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ProductGroupSalesTargetGrouprepository;
import com.orderfleet.webapp.repository.SalesTargetGroupRepository;
import com.orderfleet.webapp.repository.SalesTargetGroupUserTargetRepository;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.ProductGroupLocationTargetService;
import com.orderfleet.webapp.service.ProductGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupService;
import com.orderfleet.webapp.service.SalesTargetGroupUserTargetService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupLocationTargetDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesMonthlyTargetDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetBlockDTO;
import com.orderfleet.webapp.web.rest.dto.SalesTargetGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing SalesTargetGroupUserTarget.
 * 
 * @author Muhammed Riyas T
 * @since June 16, 2016
 */
@Controller
@RequestMapping("/web")
public class ProductGroupLocationTargetResource {

	private final Logger log = LoggerFactory.getLogger(ProductGroupLocationTargetResource.class);

	@Inject
	private SalesTargetGroupUserTargetService salesTargetGroupUserTargetService;

	@Inject
	private SalesTargetGroupService salesTargetGroupService;

	@Inject
	private UserService userService;

	@Inject
	private DocumentService documentService;

	@Inject
	private SalesTargetGroupRepository salesTargetGroupRepository;

	@Inject
	private SalesTargetGroupUserTargetRepository salesTargetGroupUserTargetRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private ProductGroupService productGroupService;

	@Inject
	private ProductGroupSalesTargetGrouprepository productGroupSalesTargetGrouprepository;

	@Inject
	private LocationService locationService;

	@Inject
	private ProductGroupLocationTargetRepository productGroupLocationTargetRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private ProductGroupLocationTargetService productGroupLocationTargetService;

	@Timed
	@RequestMapping(value = "/location-monthly-product-group-targets", method = RequestMethod.GET)
	public String setMonthlySalesTargets(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of  set Product Group Targets");

		model.addAttribute("locations", locationService.findAllLocationsByCompanyAndActivatedLocations());

		model.addAttribute("productGroups", productGroupService.findAllByCompanyAndDeactivatedProductGroup(true));
		return "company/set-monthly-product-group-target";
	}

	@RequestMapping(value = "/location-monthly-product-group-targets/monthly-product-group-targets", method = RequestMethod.GET)
	public @ResponseBody List<ProductGroupMonthlyTargetDTO> monthlySalesTargets(@RequestParam String locationsPid,
			@RequestParam String monthAndYear) {
		log.debug("Web request to get monthly Product Group Targets");
		LocationDTO locationDTO = new LocationDTO();
		if (!locationsPid.equals("no")) {
			locationDTO = locationService.findOneByPid(locationsPid).get();
		}

		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyId(true);

		String locationPid = "no";
		if (locationDTO.getPid() != null) {
			locationPid = locationDTO.getPid();
		}

		if (productGroups.size() > 0) {

			String[] monthAndYearArray = monthAndYear.split("/");
			int month = Integer.valueOf(monthAndYearArray[0]);
			int year = Integer.valueOf(monthAndYearArray[1]);
			YearMonth yearMonth = YearMonth.of(year, month);
			LocalDate firstDateMonth = yearMonth.atDay(1);
			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
			List<ProductGroupMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();

			for (ProductGroup productGroup : productGroups) {
				ProductGroupMonthlyTargetDTO monthlyTargetDTO = new ProductGroupMonthlyTargetDTO();
				monthlyTargetDTO.setProductGroupPid(productGroup.getPid());
				monthlyTargetDTO.setProductGroupName(productGroup.getName());
				monthlyTargetDTO.setLocationPid(locationPid);

				List<ProductGroupLocationTarget> productGroupLocationTargets = productGroupLocationTargetRepository
						.findByLocationPidAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
								locationPid, productGroup.getPid(), firstDateMonth, lastDateMonth);
				if (productGroupLocationTargets.size() > 0) {
					// monthlyTargetDTO.setTarget(salesTargetGroupUserTargets.get(0).getTargetNumber());
					// monthlyTargetDTO.setUserActivityTragetPid(activityUserTargets.get(0).getPid());
					double totalAmount = 0;
					double totalVolume = 0;
					for (ProductGroupLocationTarget productGroupLocationTarget : productGroupLocationTargets) {
						totalAmount = totalAmount + productGroupLocationTarget.getAmount();
						totalVolume = totalVolume + productGroupLocationTarget.getVolume();
					}
					monthlyTargetDTO.setAmount(totalAmount);
					monthlyTargetDTO.setVolume(totalVolume);
					monthlyTargetDTO.setProductGroupLocationTragetPid(productGroupLocationTargets.get(0).getPid());
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

	@RequestMapping(value = "/location-monthly-product-group-targets/monthly-product-group-targets", method = RequestMethod.POST)
	public @ResponseBody ProductGroupMonthlyTargetDTO saveMonthlyActivityTargets(
			@RequestBody ProductGroupMonthlyTargetDTO monthlyTargetDTO) {
		log.debug("Web request to save monthly Sales Targets {}" + monthlyTargetDTO);

		Optional<LocationDTO> locationDTO = locationService.findOneByPid(monthlyTargetDTO.getLocationPid());
		if (locationDTO.isPresent()) {
			monthlyTargetDTO.setLocationPid(locationDTO.get().getPid());
			if (monthlyTargetDTO.getProductGroupLocationTragetPid().equals("null")) {
				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
				int month = Integer.valueOf(monthAndYearArray[0]);
				int year = Integer.valueOf(monthAndYearArray[1]);
				YearMonth yearMonth = YearMonth.of(year, month);

				LocalDate firstDateMonth = yearMonth.atDay(1);
				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
				ProductGroupLocationTargetDTO result = productGroupLocationTargetService
						.saveMonthlyTarget(monthlyTargetDTO, firstDateMonth, lastDateMonth);
				monthlyTargetDTO.setProductGroupLocationTragetPid(result.getPid());
			} else {
				productGroupLocationTargetRepository.findOneByPid(monthlyTargetDTO.getProductGroupLocationTragetPid())
						.ifPresent(activityUserTarget -> {
							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
							activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
							productGroupLocationTargetRepository.save(activityUserTarget);
						});
			}
		}
		return monthlyTargetDTO;
	}

//	@RequestMapping(value = "/user-monthly-sales-targets/monthly-sales-targets", method = RequestMethod.POST)
//	public @ResponseBody SalesMonthlyTargetDTO saveMonthlyActivityTargets(
//			@RequestBody SalesMonthlyTargetDTO monthlyTargetDTO) {
//		log.debug("Web request to save monthly Sales Targets");
//
//		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService
//				.findOneByPid(monthlyTargetDTO.getUserPid());
//		if (employeeProfileDTO.isPresent()) {
//			monthlyTargetDTO.setUserPid(employeeProfileDTO.get().getUserPid());
//			if (monthlyTargetDTO.getSalesTargetGroupUserTragetPid().equals("null")) {
//				String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
//				int month = Integer.valueOf(monthAndYearArray[0]);
//				int year = Integer.valueOf(monthAndYearArray[1]);
//				YearMonth yearMonth = YearMonth.of(year, month);
//
//				LocalDate firstDateMonth = yearMonth.atDay(1);
//				LocalDate lastDateMonth = yearMonth.atEndOfMonth();
//				SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService
//						.saveMonthlyTarget(monthlyTargetDTO, firstDateMonth, lastDateMonth);
//				monthlyTargetDTO.setSalesTargetGroupUserTragetPid(result.getPid());
//			} else {
//				salesTargetGroupUserTargetRepository.findOneByPid(monthlyTargetDTO.getSalesTargetGroupUserTragetPid())
//						.ifPresent(activityUserTarget -> {
//							activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
//							activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
//							salesTargetGroupUserTargetRepository.save(activityUserTarget);
//						});
//			}
//		}
//		return monthlyTargetDTO;
//	}
//
//	/** account wise monthly sales targets */
//	@Timed
//	@RequestMapping(value = "/account-wise-monthly-sales-targets", method = RequestMethod.GET)
//	public String setAccountMonthlySalesTargets(Model model) {
//		log.debug("Web request to get a page of  set account wise Sales Target");
//		model.addAttribute("users", userService.findAllByCompany());
//		List<Object[]> sTargetBlockObjects = salesTargetGroupRepository.findSalesTargetGroupPropertyByCompanyId();
//		List<SalesTargetBlockDTO> sTargetBlockDtos = new ArrayList<>();
//		for (Object[] sTargetBlockObj : sTargetBlockObjects) {
//			SalesTargetBlockDTO salesTargetBlockDTO = new SalesTargetBlockDTO();
//			salesTargetBlockDTO.setPid(sTargetBlockObj[0].toString());
//			salesTargetBlockDTO.setName(sTargetBlockObj[1].toString());
//			sTargetBlockDtos.add(salesTargetBlockDTO);
//		}
//		model.addAttribute("salesTargetGroups", sTargetBlockDtos);
//		return "company/set-account-wise-monthly-sales-target";
//	}
//
//	@RequestMapping(value = "/account-wise-monthly-sales-targets/load", method = RequestMethod.GET)
//	public @ResponseBody List<SalesMonthlyTargetDTO> loadAccountWiseMonthlySalesTargets(
//			@RequestParam(required = false) String page, @RequestParam String userPid,
//			@RequestParam String salesTargetGroupPid, @RequestParam String monthAndYear) {
//		log.debug("Web request to get monthly Sales Targets");
//		List<Object[]> accountProfileObjects;
//		if ("NON-ALPHABETICAL".equals(page)) {
//			accountProfileObjects = accountProfileRepository.findByCompanyAndAccountPidAndNameStartWithNonAlphabetic();
//		} else {
//			accountProfileObjects = accountProfileRepository.findByCompanyAndAccountPidAndNameStartWith(page);
//		}
//		if (!accountProfileObjects.isEmpty()) {
//			int[] mnthYr = Arrays.stream(monthAndYear.split("/")).mapToInt(Integer::valueOf).toArray();
//			YearMonth yearMonth = YearMonth.of(mnthYr[1], mnthYr[0]);
//			LocalDate firstDateMonth = yearMonth.atDay(1);
//			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
//			List<SalesMonthlyTargetDTO> monthlyTargetDTOs = new ArrayList<>();
//			for (Object[] apObj : accountProfileObjects) {
//				SalesMonthlyTargetDTO monthlyTargetDTO = new SalesMonthlyTargetDTO();
//				monthlyTargetDTO.setAccountProfilePid(apObj[0].toString());
//				monthlyTargetDTO.setAccountProfileName(apObj[1].toString());
//				monthlyTargetDTO.setUserPid(userPid);
//
//				List<Object[]> salesTargetGroupUserTargets = salesTargetGroupUserTargetRepository
//						.findByUserPidAndSalesTargetGroupPidAndAccountProfilePidAndDateBetween(userPid,
//								salesTargetGroupPid, apObj[0].toString(), firstDateMonth, lastDateMonth);
//				if (salesTargetGroupUserTargets.isEmpty()) {
//					monthlyTargetDTO.setAmount(0);
//					monthlyTargetDTO.setVolume(0);
//				} else {
//					Object[] salesTargetGroupUserTarget = salesTargetGroupUserTargets.get(0);
//					monthlyTargetDTO.setSalesTargetGroupUserTragetPid(salesTargetGroupUserTarget[0].toString());
//					monthlyTargetDTO.setAmount((Double) salesTargetGroupUserTarget[1]);
//					monthlyTargetDTO.setVolume((Double) salesTargetGroupUserTarget[2]);
//				}
//				monthlyTargetDTOs.add(monthlyTargetDTO);
//			}
//			return monthlyTargetDTOs;
//		}
//		return null;
//	}
//
//	@RequestMapping(value = "/account-wise-monthly-sales-targets", method = RequestMethod.POST)
//	public @ResponseBody SalesMonthlyTargetDTO saveAccountWiseMonthlyActivityTargets(
//			@RequestBody SalesMonthlyTargetDTO monthlyTargetDTO) {
//		log.debug("Web request to save Account Wise monthly Sales Targets");
//		if (monthlyTargetDTO.getSalesTargetGroupUserTragetPid().equals("null")) {
//			String[] monthAndYearArray = monthlyTargetDTO.getMonthAndYear().split("/");
//			int month = Integer.valueOf(monthAndYearArray[0]);
//			int year = Integer.valueOf(monthAndYearArray[1]);
//			YearMonth yearMonth = YearMonth.of(year, month);
//
//			LocalDate firstDateMonth = yearMonth.atDay(1);
//			LocalDate lastDateMonth = yearMonth.atEndOfMonth();
//			SalesTargetGroupUserTargetDTO result = salesTargetGroupUserTargetService.saveMonthlyTarget(monthlyTargetDTO,
//					firstDateMonth, lastDateMonth);
//			monthlyTargetDTO.setSalesTargetGroupUserTragetPid(result.getPid());
//		} else {
//			salesTargetGroupUserTargetRepository.findOneByPid(monthlyTargetDTO.getSalesTargetGroupUserTragetPid())
//					.ifPresent(activityUserTarget -> {
//						activityUserTarget.setAmount(monthlyTargetDTO.getAmount());
//						activityUserTarget.setVolume(monthlyTargetDTO.getVolume());
//						salesTargetGroupUserTargetRepository.save(activityUserTarget);
//					});
//		}
//		return monthlyTargetDTO;
//	}
}