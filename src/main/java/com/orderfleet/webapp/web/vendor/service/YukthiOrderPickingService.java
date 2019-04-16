package com.orderfleet.webapp.web.vendor.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

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
		List<Object[]> accountProfiles = accountProfileRepository.findAccountPidsByAliasInAndActivatedAndCompany(branchIds);
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
