package com.orderfleet.webapp.web.vendor.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.model.PickingDetail;
import com.orderfleet.webapp.web.vendor.model.PickingHeader;
import com.snr.yukti.model.picking.PickOrder;
import com.snr.yukti.model.picking.PickOrderLineItem;
import com.snr.yukti.model.picking.PickOrderLineItemSave;
import com.snr.yukti.model.picking.PickOrderSave;
import com.snr.yukti.service.YuktiPickingService;

@Service
public class YukthiOrderPickingService {
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private YuktiPickingService yuktiPickingService;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;


	public List<PickingHeader> getOrdersForPicking(String fromDate, String toDate, Long companyId) {
		List<PickOrder> pickOrders = yuktiPickingService.getOrdersForPickingWithOrderDueDate(fromDate, companyId);
		if(pickOrders.isEmpty()) {
			return Collections.emptyList();
		}
		List<PickingHeader> pickHeaders = new ArrayList<>();
		//branch ids
		Set<String> branchIds = new HashSet<>();
		for (PickOrder pickOrder : pickOrders) {
			PickingHeader pickingHeader = new PickingHeader();
			pickingHeader.setTransactionNo(pickOrder.getTransNo());
			pickingHeader.setReferenceNo(pickOrder.getRefNo());
			pickingHeader.setOrderDate(pickOrder.getOrderDate());
			pickingHeader.setLocation(pickOrder.getLocation());
			pickingHeader.setBranchId(pickOrder.getBranchId());
			branchIds.add(pickOrder.getBranchId());
			
			List<PickingDetail> pickDetails = new ArrayList<>();
			for (PickOrderLineItem lineItem : pickOrder.getLineItems()) {
				PickingDetail pickDetail = new PickingDetail();
				pickDetail.setId(lineItem.getId());
				pickDetail.setStockId(lineItem.getStockId());
				pickDetail.setProductName(lineItem.getDescription());
				pickDetail.setOrderQty(lineItem.getQty());
				pickDetail.setPickedQty(lineItem.getPickQty());
				pickDetail.setCurrentQty(0d);
				
				pickDetails.add(pickDetail);
			}
			pickingHeader.setPickDetails(pickDetails);
			pickHeaders.add(pickingHeader);
		}
		//update actual account id
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_112" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get accPids by aliasIn activated and company";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<Object[]> accountProfiles = accountProfileRepository.findAccountPidsByAliasInAndActivatedAndCompany(branchIds);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		for (PickingHeader pickingHeader : pickHeaders) {
			Optional<Object[]> opAccount = accountProfiles.stream().filter(obj -> pickingHeader.getBranchId().equals(""+obj[1])).findAny();
			if(opAccount.isPresent()) {
				pickingHeader.setAccountProfilePid(opAccount.get()[0].toString());
				pickingHeader.setAccountProfileName(opAccount.get()[2].toString());
			}
		}
		return pickHeaders;
	}
	
	public void savePickedOrders(PickingHeader pickingHeader, Long companyId) {
		PickOrderSave pickOrderSave = new PickOrderSave();
		pickOrderSave.setTransNo(pickingHeader.getTransactionNo());
		pickOrderSave.setPickDate(pickingHeader.getOrderDate());
		pickOrderSave.setLocation(pickingHeader.getLocation());
		
		EmployeeProfile employeePorifle = 
				employeeProfileRepository.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUser().getUsername());
		pickOrderSave.setPickUser(employeePorifle.getReferenceId());
		pickOrderSave.setRemarks(pickingHeader.getRemarks());
		
		List<PickOrderLineItemSave> lineItems = new ArrayList<>();
		for (PickingDetail pickDetail : pickingHeader.getPickDetails()) {
			PickOrderLineItemSave lineItem = new PickOrderLineItemSave();
			lineItem.setId(pickDetail.getId());
			lineItem.setStockId(pickDetail.getStockId());
			lineItem.setQty(pickDetail.getOrderQty());
			lineItem.setPickQty(pickDetail.getPickedQty());
			lineItem.setThisPick(pickDetail.getCurrentQty());
			lineItem.setPick(pickDetail.getPick());
			
			lineItems.add(lineItem);
		}
		pickOrderSave.setPickLines(lineItems);
		yuktiPickingService.addOrModifyPickOrder(pickOrderSave, companyId);
	}


}
