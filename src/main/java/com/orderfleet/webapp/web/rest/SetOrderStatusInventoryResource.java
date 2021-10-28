package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.OrderStatusService;
import com.orderfleet.webapp.service.SetOrderStatusInventoryService;
import com.orderfleet.webapp.service.impl.InventoryVoucherHeaderServiceImpl;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.OrderStatusDTO;

@Controller
@RequestMapping("/web")
public class SetOrderStatusInventoryResource {

	private final Logger log = LoggerFactory.getLogger(SetOrderStatusInventoryResource.class);
	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private OrderStatusService orderStatusService;

	@Inject
	private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

	@Inject
	private SetOrderStatusInventoryService setOrderStatusInventoryService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@RequestMapping(value = "/set-order-status-inventory", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllSetOrderStatusInventory(Model model) {
		// user under current user
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		model.addAttribute("statuses", orderStatusService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER));
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
			model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
		}
		return "company/setOrderStatusInventory";
	}

	@RequestMapping(value = "/set-order-status-inventory/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<InventoryVoucherHeaderDTO>> filterSetOrderStatusInventory(
			@RequestParam("employeePid") String employeePid, @RequestParam("statusId") Long statusId,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();
		if (filterBy.equals("TODAY")) {
			inventoryVoucherHeaderDTOs = getFilterData(employeePid, statusId, accountPid, LocalDate.now(),
					LocalDate.now());
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			inventoryVoucherHeaderDTOs = getFilterData(employeePid, statusId, accountPid, yeasterday, yeasterday);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			inventoryVoucherHeaderDTOs = getFilterData(employeePid, statusId, accountPid, weekStartDate,
					LocalDate.now());
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			inventoryVoucherHeaderDTOs = getFilterData(employeePid, statusId, accountPid, monthStartDate,
					LocalDate.now());
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			inventoryVoucherHeaderDTOs = getFilterData(employeePid, statusId, accountPid, fromDateTime, toFateTime);
		}
		return new ResponseEntity<>(inventoryVoucherHeaderDTOs, HttpStatus.OK);
	}

	private List<InventoryVoucherHeaderDTO> getFilterData(String employeePid, Long statusId, String accountPid,
			LocalDate fDate, LocalDate tDate) {

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<Object[]> inventoryVoucherHeaders = new ArrayList<>();
		if(employeePid.equals("no")) {
			if (statusId == -1 && accountPid.equals("no")) {
				String id="INV_QUERY_145";
				String description="Selecting inv_vou DocDate,employeename,Receiveraccountname,docTotal,OrderStatusname from invVouHeader  by validating companyId and DocdateBetween and orderby docDate in desc order";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdDateBetweenOrderByDocumentDateDesc(fromDate, toDate);
			} else if (statusId != -1 && accountPid.equals("no")) {
				String id="INV_QUERY_147";
				String description="Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId ,orderStatus id and docDtaeBetween and Order by DocDate in desc order";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdOrderStatusAndDateBetweenOrderByDocumentDateDesc(statusId, fromDate, toDate);
			} else if (statusId == -1 && !accountPid.equals("no")) {
				String id="INV_QUERY_148";
				String description="Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId ,receiver AccountPid and docDtaeBetween and Order by DocDate in desc order";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdAccountPidAndDateBetweenOrderByDocumentDateDesc(accountPid, fromDate, toDate);
			} else if (statusId != -1 && !accountPid.equals("no")) {
				String id="INV_QUERY_149";
				String description="Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId ,orderStatus id,receiver AccountPid and docDtaeBetween and Order by DocDate in desc order";
				log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
				inventoryVoucherHeaders = inventoryVoucherHeaderRepository
						.findAllByCompanyIdOrderStatusIdAccountPidAndDateBetweenOrderByDocumentDateDesc( accountPid,statusId,
								fromDate, toDate);
			}
			}else {
				if (statusId == -1 && accountPid.equals("no")) {
					String id="INV_QUERY_146";
					String description="Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId and docDtaeBetween and Order by DocDate in desc order";
					log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
					inventoryVoucherHeaders = inventoryVoucherHeaderRepository
							.findAllByCompanyIdEmployeePidAndDateBetweenOrderByDocumentDateDesc(employeePid,fromDate, toDate);
				} else if (statusId != -1 && accountPid.equals("no")) {
					String id="INV_QUERY_150";
					String description="Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId ,employee pid,orderStatus id and docDtaeBetween and Order by DocDate in desc order";
					log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
					inventoryVoucherHeaders = inventoryVoucherHeaderRepository
							.findAllByCompanyIdEmployeePidOrderStatusIdAndDateBetweenOrderByDocumentDateDesc(employeePid,statusId, fromDate, toDate);
				} else if (statusId == -1 && !accountPid.equals("no")) {
					String id="INV_QUERY_152";
					String description=" Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId ,employee pid,receiver AccountPid and docDtaeBetween and Order by DocDate in desc order";
					log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
					inventoryVoucherHeaders = inventoryVoucherHeaderRepository
							.findAllByCompanyIdEmployeePidAccountPidAndDateBetweenOrderByDocumentDateDesc(employeePid,accountPid, fromDate, toDate);
				} else if (statusId != -1 && !accountPid.equals("no")) {
					String id="INV_QUERY_151";
					String description="Selecting inv voucher DocDate,employeename,receiverAccountName,DocTotal,orderStatusname from invVouHeader by validating companyId ,employee pid,orderStatus id,receiver AccountPid and docDtaeBetween and Order by DocDate in desc order";
					log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
					inventoryVoucherHeaders = inventoryVoucherHeaderRepository
							.findAllByCompanyIdEmployeePidOrderStatusIdAccountPidAndDateBetweenOrderByDocumentDateDesc(employeePid,statusId, accountPid,
									fromDate, toDate);
				}
			}
		List<InventoryVoucherHeaderDTO> inventoryVoucherHeaderDTOs = new ArrayList<>();
		if (!inventoryVoucherHeaders.isEmpty()) {
			for (Object[] object : inventoryVoucherHeaders) {
				InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO = new InventoryVoucherHeaderDTO();
				inventoryVoucherHeaderDTO.setDocumentDate((LocalDateTime) object[0]);
				inventoryVoucherHeaderDTO.setEmployeeName(object[1].toString());
				inventoryVoucherHeaderDTO.setReceiverAccountName(object[2].toString());
				inventoryVoucherHeaderDTO.setDocumentTotal((double) object[3]);
				inventoryVoucherHeaderDTO.setOrderStatusName(object[4].toString());
				inventoryVoucherHeaderDTOs.add(inventoryVoucherHeaderDTO);
			}
		}
		return inventoryVoucherHeaderDTOs;
	}

	@Timed
	@RequestMapping(value = "/set-order-status-inventory/changeOrderStatus", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> changeOrderStatus(@RequestParam("inventoryPid") String inventoryPid,
			@RequestParam("status") String status) {
		setOrderStatusInventoryService.saveSetOrderStatusInventoryHistory(inventoryPid, status);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/set-order-status-inventory/getOrderStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<OrderStatusDTO>> getOrderStatus() {
		List<OrderStatusDTO> orderStatusDTOs = orderStatusService.findAllByDocumentType(DocumentType.INVENTORY_VOUCHER);
		return new ResponseEntity<>(orderStatusDTOs, HttpStatus.OK);
	}

}
