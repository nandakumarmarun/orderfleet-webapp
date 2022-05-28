package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFCreationHelper;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
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
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.SalesOrderStatus;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.domain.enums.VoucherType;
import com.orderfleet.webapp.geolocation.api.GeoLocationService;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskExecutionService;
import com.orderfleet.webapp.service.InventoryVoucherHeaderService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.PrimarySecondaryDocumentService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherDetailDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.SalesOrderStatusReportDTO;
import com.orderfleet.webapp.web.rest.dto.SalesPerformanceDTO;

@Controller
@RequestMapping("/web")
public class SalesOrderStatusViseReportResource {

	private final Logger log = LoggerFactory.getLogger(SalesOrderStatusViseReportResource.class);
	
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String CUSTOM = "CUSTOM";
	
	@Inject
	private PrimarySecondaryDocumentService primarySecondaryDocumentService;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
	
	@Inject
	private EmployeeProfileService employeeProfileService;
	
	@Inject
	private AccountProfileService accountProfileService;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private GeoLocationService geoLocationService;
	
	@Inject
	private InventoryVoucherHeaderService inventoryVoucherService;
	
	@Inject
	private LocationAccountProfileService locationAccountProfileService;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private ExecutiveTaskExecutionService executiveTaskExecutionService;
	
	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
	
	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;
	
	@Inject
	private LocationRepository locationRepository;
	
	
	@RequestMapping(value = "/sales-order-statusvise-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllItemWiseSales(Model model) throws URISyntaxException {
		log.debug("Web request to get All ItemWiseSales");
		List<DocumentDTO> documentDTOs = primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(VoucherType.PRIMARY_SALES_ORDER);
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		model.addAttribute("accounts", accountProfileService.findAllByCompany());
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		model.addAttribute("documents", documentDTOs);
		model.addAttribute("voucherTypes", primarySecondaryDocumentService.findAllVoucherTypesByCompanyId());
		model.addAttribute("territories",locationRepository.findAllByCompanyId());
		return "company/salesOrderStatusReport";
	}

	@RequestMapping(value = "/sales-order-statusvise-report/load-document", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public List<DocumentDTO> getDocuments(@Valid @RequestParam VoucherType voucherType) {
		return primarySecondaryDocumentService.findAllDocumentsByCompanyIdAndVoucherType(voucherType);
	}
	
	@RequestMapping(value = "/sales-order-statusvise-report/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<List<SalesOrderStatusReportDTO>> filterInventoryVouchers(
			@RequestParam("employeePids") List<String> employeePids,
			@RequestParam("tallyDownloadStatus") String tallyDownloadStatus,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam("documentPids") List<String> documentPids, @RequestParam String fromDate,
			@RequestParam String toDate,
			@RequestParam String salesOrderStatus,
			@RequestParam String terittoryPids) {
		log.debug("Web request to filter accounting vouchers");
		LocalDate fDate = LocalDate.now();
		LocalDate tDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		if (documentPids.isEmpty()) {
			return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		}
	
		
		if (filterBy.equals(SalesOrderStatusViseReportResource.CUSTOM)) {
			fDate = LocalDate.parse(fromDate, formatter);
			tDate = LocalDate.parse(toDate, formatter);
		} else if (filterBy.equals(SalesOrderStatusViseReportResource.YESTERDAY)) {
			fDate = LocalDate.now().minusDays(1);
			tDate = fDate;
		} else if (filterBy.equals(SalesOrderStatusViseReportResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fDate = LocalDate.now().with(fieldISO, 1);
		} else if (filterBy.equals(SalesOrderStatusViseReportResource.MTD)) {
			fDate = LocalDate.now().withDayOfMonth(1);
		}
		
		
		List<SalesOrderStatusReportDTO> salesOrderStatusReportDTOs = getFilterData(employeePids, documentPids, tallyDownloadStatus,
				salesOrderStatus,accountPid, fDate, tDate,terittoryPids);
	
		return new ResponseEntity<>(salesOrderStatusReportDTOs, HttpStatus.OK);
	}

	private List<SalesOrderStatusReportDTO> getFilterData(List<String> employeePids, List<String> documentPids,
			String tallyDownloadStatus,String salesOrderStatus, String accountPid, LocalDate fDate, LocalDate tDate,String terittoryPids) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Long> userIds = employeeProfileRepository.findUserIdByEmployeePidIn(employeePids);
		Long currentUserId = userRepository.getIdByLogin(SecurityUtils.getCurrentUserLogin());
		userIds.add(currentUserId);
		 System.out.println("CompanyID"+SecurityUtils.getCurrentUsersCompanyId());
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		List<SalesOrderStatus> salesStatus = null;

		switch (salesOrderStatus) {
		case "CREATED":
			 salesStatus = Arrays.asList(SalesOrderStatus.CREATED);
			break;
		case "CANCELED":
			 salesStatus = Arrays.asList(SalesOrderStatus.CANCELED);
			break;
		case "DELIVERD":
			 salesStatus = Arrays.asList(SalesOrderStatus.DELIVERD);
			break;
		case "CONFIRM":
			 salesStatus = Arrays.asList(SalesOrderStatus.CONFIRM);
			break;
		case "ALL":
			 salesStatus = Arrays.asList(SalesOrderStatus.CANCELED, SalesOrderStatus.DELIVERD,
					 SalesOrderStatus.CREATED,SalesOrderStatus.CONFIRM);
			break;
		}
		
		List<TallyDownloadStatus> tallyStatus = null;

		switch (tallyDownloadStatus) {
		case "PENDING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PENDING);
			break;
		case "PROCESSING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PROCESSING);
			break;
		case "COMPLETED":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED);
			break;
		case "ALL":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED, TallyDownloadStatus.PROCESSING,
					TallyDownloadStatus.PENDING);
			break;
		}
		
		List<String> accountProfilePids;
		List<String> productTerritoryPids = new ArrayList<>();
		accountProfilePids = Arrays.asList(accountPid);
		productTerritoryPids = terittoryPids != "" ? Arrays.asList(terittoryPids.split(",")) : productTerritoryPids;
		

		
		List<Object[]> inventoryVouchers;
		if ("-1".equals(accountPid)) {
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndDocumentPidInAndTallyDownloadStatusandsaleOrderstausDateBetweenOrderByCreatedDateDesc(userIds,
							documentPids, tallyStatus, fromDate, toDate,salesStatus);
		} else {
			inventoryVouchers = inventoryVoucherHeaderRepository
					.findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusaStatusdDateBetweenOrderByCreatedDateDesc(
							userIds, accountPid, documentPids, tallyStatus, fromDate, toDate,salesStatus);		 
		}
		
		if (inventoryVouchers.isEmpty()) {
			System.out.println("List is empty");
			return Collections.emptyList();
		} else {
			List<SalesOrderStatusReportDTO> salesOrderStatusReportDTOs  = new ArrayList<>(); 
			List<SalesOrderStatusReportDTO> fillterdsalesOrderStatusReportDTOs = createSalesOrderStatusReportDTO(inventoryVouchers);
			if(!productTerritoryPids.isEmpty()) {
				for(String productTerritoryPid : productTerritoryPids) {
					List<SalesOrderStatusReportDTO> optSalesOrderStatusReportDTO = fillterdsalesOrderStatusReportDTOs.stream().filter(p -> p.getTerritoryPid().equals(productTerritoryPid)).collect(Collectors.toList());
					if(!optSalesOrderStatusReportDTO.isEmpty()) {
						salesOrderStatusReportDTOs.addAll(optSalesOrderStatusReportDTO);
					}
				}
				return salesOrderStatusReportDTOs;
			}
			else {
				return createSalesOrderStatusReportDTO(inventoryVouchers);
			}
		
		}
	}


	
	private List<SalesOrderStatusReportDTO> createSalesOrderStatusReportDTO(List<Object[]> inventoryVouchers) {
		Set<String> ivHeaderPids = inventoryVouchers.parallelStream().map(obj -> obj[0].toString())
				.collect(Collectors.toSet());
		boolean compconfig= getCompanyCofig();
		int size = inventoryVouchers.size();
		List<SalesOrderStatusReportDTO> salesOrderStatusReportDTOs = new ArrayList<>(size);
		
		List<AccountProfileDTO> accountProfils =accountProfileService.findAllByCompany();
		List<LocationAccountProfile> LocationAccountProfile = locationAccountProfileRepository.findAllByCompanyId();
		
		for (int i = 0; i < size; i++) {
			SalesOrderStatusReportDTO salesOrderStatusReportDTO = new SalesOrderStatusReportDTO();
			Object[] ivData = inventoryVouchers.get(i);
			salesOrderStatusReportDTO.setPid(ivData[0].toString());
			salesOrderStatusReportDTO.setDocumentNumberLocal(ivData[1].toString());
			salesOrderStatusReportDTO.setDocumentNumberServer(ivData[2].toString());
			salesOrderStatusReportDTO.setServerDate((LocalDateTime) ivData[5]);
			salesOrderStatusReportDTO.setReceiverAccountPid(ivData[7].toString());
			salesOrderStatusReportDTO.setExicutiveTaskExecutionPid(ivData[30].toString());
			salesOrderStatusReportDTO.setTallyDownloadStatus(TallyDownloadStatus.valueOf(ivData[16].toString()));
			Optional<AccountProfileDTO> matchingObject = accountProfils.stream().
				    filter(p -> p.getPid().equals(ivData[7].toString())).findAny();
			
			
			Optional<LocationAccountProfile> LocationAccountProfileOp = LocationAccountProfile.stream().
				    filter(a->a.getAccountProfile().getPid().equals(ivData[7].toString())).findAny();
			
			if(LocationAccountProfileOp.isPresent()) {
				
				LocationAccountProfile locationAccountProfile = LocationAccountProfileOp.get();
				salesOrderStatusReportDTO.setTerritory(locationAccountProfile.getLocation().getName());
				salesOrderStatusReportDTO.setTerritoryPid(locationAccountProfile.getLocation().getPid());
			}
			
			if(matchingObject.isPresent()) {
				AccountProfileDTO accountProfile = matchingObject.get();
				
				salesOrderStatusReportDTO.setDistrict(accountProfile.getDistrictName() != null ? accountProfile.getDistrictName() : "" );
				salesOrderStatusReportDTO.setReceiverAccountLocation(accountProfile.getLocation() != null ? accountProfile.getLocation() : "" );
			
			}
			
			if(compconfig)
			{
				salesOrderStatusReportDTO.setReceiverAccountName(ivData[27].toString());
			}
			else
			{
				salesOrderStatusReportDTO.setReceiverAccountName(ivData[8].toString());
			}
			salesOrderStatusReportDTO.setEmployeePid(ivData[11].toString());
			salesOrderStatusReportDTO.setEmployeeName(ivData[12].toString());
			salesOrderStatusReportDTO.setReceiverAccountLocation(ivData[26] != null ? ivData[26].toString() : "");
			salesOrderStatusReportDTO.setSalesOrderStatus(SalesOrderStatus.valueOf(ivData[28].toString()));
			salesOrderStatusReportDTO.setDocumentTotal((double) ivData[14]);
			String exicutivetaskexicutionid = ivData[29].toString();
			salesOrderStatusReportDTO.setLocation(ivData[29].toString());
			BigDecimal bigDecimal = new BigDecimal(ivData[31].toString());
			salesOrderStatusReportDTO.setAccountProfileLatitude(bigDecimal);
			if(ivData[32] != null){
				LocalDate deliveryDateLocal = (LocalDate) ivData[32];
			
				LocalDateTime deliveryDate = deliveryDateLocal.atTime(0, 0);
				salesOrderStatusReportDTO.setDeliveryDate(deliveryDate);
			}
					
			salesOrderStatusReportDTOs.add(salesOrderStatusReportDTO);
		}
		return salesOrderStatusReportDTOs;
	}
	
	@RequestMapping(value = "/sales-order-statusvise-report/changeSalesOrderStatus", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> changeSalesOrderStatus(@RequestParam String pid,
			@RequestParam SalesOrderStatus salesOrderStatus) {
		log.info("Sales Sales Management Status " + salesOrderStatus);
		InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = inventoryVoucherService.findOneByPid(pid).get();
		if (inventoryVoucherHeaderDTO.getTallyDownloadStatus() != TallyDownloadStatus.COMPLETED) {
			inventoryVoucherHeaderDTO.setSalesOrderStatus(salesOrderStatus);
			inventoryVoucherService.updateInventoryVoucherHeaderSalesOrderStatus(inventoryVoucherHeaderDTO);
			return new ResponseEntity<>("success", HttpStatus.OK);
		}

		return new ResponseEntity<>("failed", HttpStatus.OK);

	}
	
	
	
	@RequestMapping(value = "/sales-order-statusvise-report/updateLocation/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<SalesOrderStatusReportDTO> updateLocationExecutiveTaskExecutions(@PathVariable String pid) {
		Optional<ExecutiveTaskExecution> opExecutiveeExecution = executiveTaskExecutionRepository.findOneByPid(pid);
		SalesOrderStatusReportDTO salesOrderStatusReportDTO = new SalesOrderStatusReportDTO();
		if (opExecutiveeExecution.isPresent()) {
			ExecutiveTaskExecution execution = opExecutiveeExecution.get();

			if (execution.getLatitude() != BigDecimal.ZERO) {
				System.out.println("-------lat != 0");
				String location = geoLocationService
						.findAddressFromLatLng(execution.getLatitude() + "," + execution.getLongitude());
				System.out.println("-------" + location);
				salesOrderStatusReportDTO.setLocation(location);

			} else {
				System.out.println("-------No Location");
				execution.setLocation("No Location");
			}
			/*
			 * if (execution.getLocationType() == LocationType.GpsLocation) { String
			 * location = geoLocationService .findAddressFromLatLng(execution.getLatitude()
			 * + "," + execution.getLongitude()); execution.setLocation(location); } else if
			 * (execution.getLocationType() == LocationType.TowerLocation) { TowerLocation
			 * location = geoLocationService.findAddressFromCellTower(execution.getMcc(),
			 * execution.getMnc(), execution.getCellId(), execution.getLac());
			 * execution.setLatitude(location.getLat());
			 * execution.setLongitude(location.getLan());
			 * execution.setLocation(location.getLocation()); }
			 */
		}
		return new ResponseEntity<>(salesOrderStatusReportDTO, HttpStatus.OK);
	}
	
	
	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}

}
