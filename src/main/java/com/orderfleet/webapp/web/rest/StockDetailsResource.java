package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
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
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserStockLocation;
import com.orderfleet.webapp.domain.enums.AccountTypeColumn;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.domain.enums.StockFlow;
import com.orderfleet.webapp.repository.DocumentStockCalculationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.DocumentAccountTypeService;
import com.orderfleet.webapp.service.DocumentPriceLevelService;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.StockDetailsService;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentStockCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Document.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Controller
@RequestMapping("/web")
public class StockDetailsResource {

	private final Logger log = LoggerFactory.getLogger(StockDetailsResource.class);

	@Inject
	private InventoryVoucherHeaderService inventoryVoucherHeaderService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserRepository userRepository;
	
	@Inject
	private StockDetailsService stockDetailsService;
	
	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Inject
	private UserStockLocationRepository userStockLocationRepository;
	
	

	@RequestMapping(value = "/stockDetails", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllDocuments(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of stockDetails");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

		if (userIds.isEmpty()) {
			String user = SecurityUtils.getCurrentUserLogin();
			System.out.println("if.." + userIds.size());
			long userId = userRepository.getIdByLogin(user);
			userIds = new ArrayList<>();
			userIds.add(userId);
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		} else {
			System.out.println("else .." + userIds.size());
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		String user = SecurityUtils.getCurrentUserLogin();
		Optional<User> userOp = userRepository.findOneByLogin(user);
		long userId = userOp.get().getId();
		
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(userOp.get().getPid());
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation()).collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository.findByStockLocationIn(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		if(openingStockUserBased.size()!=0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			//LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(userOp.get());
			for(StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
			}
			stockDetails.addAll(unSaled);
		}
		
		model.addAttribute("stockDetails",stockDetails);
		return "company/stockDetails";
	}

	@RequestMapping(value = "/stockDetails/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StockDetailsDTO>> filter(@RequestParam("employeePid") String employeePid) {
		log.debug("Web request to filter Stock Details");

		long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfileDTO> employeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		String userPid = employeeProfileDTO.get().getUserPid();
		Optional<User> user = userRepository.findOneByPid(userPid);
		long userId = user.get().getId();
		List<UserStockLocation> userStockLocations = userStockLocationRepository.findByUserPid(user.get().getPid());
		Set<StockLocation> usersStockLocations = userStockLocations.stream().map(usl -> usl.getStockLocation()).collect(Collectors.toSet());
		List<OpeningStock> openingStockUserBased = openingStockRepository.findByStockLocationIn(new ArrayList<>(usersStockLocations));
		List<StockDetailsDTO> stockDetails = new ArrayList<StockDetailsDTO>();
		if(openingStockUserBased.size()!=0) {
			LocalDateTime fromDate = openingStockUserBased.get(0).getCreatedDate();
			//LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
			LocalDateTime toDate = LocalDate.now().atTime(23, 59);
			stockDetails = inventoryVoucherHeaderService.findAllStockDetails(companyId, userId, fromDate, toDate);
			List<StockDetailsDTO> unSaled = stockDetailsService.findOtherStockItems(user.get());
			for(StockDetailsDTO dto : stockDetails) {
				unSaled.removeIf(unSale -> unSale.getProductName().equals(dto.getProductName()));
			}
			stockDetails.addAll(unSaled);
		}
		
		return new ResponseEntity<>(stockDetails, HttpStatus.OK);
		
		
	}
}
