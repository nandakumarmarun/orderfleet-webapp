package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.InventoryClosingDetailProduct;
import com.orderfleet.webapp.domain.InventoryClosingHeader;
import com.orderfleet.webapp.domain.InventoryClosingProductDetailSettingGroup;
import com.orderfleet.webapp.domain.InventoryClosingReportSettingGroup;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserSourceLocation;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.InventoryClosingDetailProductRepository;
import com.orderfleet.webapp.repository.InventoryClosingHeaderRepository;
import com.orderfleet.webapp.repository.InventoryClosingProductDetailSettingGroupRepository;
import com.orderfleet.webapp.repository.InventoryClosingReportSettingGroupRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserSourceLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InventoryClosingHeaderService;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingDetailProductDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingHolder;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingProductDetailSettingGroupDTO;

@Service
@Transactional
public class InventoryClosingHeaderServiceImpl implements InventoryClosingHeaderService{

	@Inject
	private InventoryClosingReportSettingGroupRepository inventoryClosingReportSettingGroupRepository;
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private UserSourceLocationRepository userSourceLocationRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private InventoryClosingHeaderRepository inventoryClosingHeaderRepository;
	
	@Inject
	private InventoryClosingDetailProductRepository inventoryClosingDetailProductRepository;
	
	@Inject
	private InventoryClosingProductDetailSettingGroupRepository inventoryClosingProductDetailSettingGroupRepository;
	
	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Override
	public void processInventoryClosing(InventoryClosingHolder inventoryClosingHolder) {
		LocalDateTime currentDate = LocalDateTime.now();
		Optional<EmployeeProfile> optinalEmployeeProfileDTO = employeeProfileRepository.findOneByPid(inventoryClosingHolder.getSelectedUserPid());
		if(optinalEmployeeProfileDTO.isPresent()) {
			User user = optinalEmployeeProfileDTO.get().getUser();
			User loggedUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
			Company company = user.getCompany();
			// save inventoryClosingHeader
			InventoryClosingHeader inventoryClosingHeader = new InventoryClosingHeader();
			UserSourceLocation userSourceLocation = userSourceLocationRepository.findSourceLocationsByUserPid(user.getPid());
			inventoryClosingHeader.setClosedBy(loggedUser);
			inventoryClosingHeader.setCompany(company);
			inventoryClosingHeader.setStockLocation(userSourceLocation.getStockLocation());
			inventoryClosingHeader.setUser(user);
			inventoryClosingHeader.setClosedDate(currentDate);
			inventoryClosingHeader = inventoryClosingHeaderRepository.save(inventoryClosingHeader);
			
			// delete all opening stock
			openingStockRepository.deleteByCompanyId(company.getId());
			
			// save InventoryClosingDetailProduct
			for (InventoryClosingDetailProductDTO inventoryClosingDetailProductDTO : inventoryClosingHolder.getInventoryClosingDetailProducts()) {
				InventoryClosingDetailProduct inventoryClosingDetailProduct =new InventoryClosingDetailProduct(); 
				ProductProfile productProfile = productProfileRepository.findByCompanyIdAndNameIgnoreCase(company.getId(), inventoryClosingDetailProductDTO.getProductProfileName()).get();
				inventoryClosingDetailProduct.setClosing(inventoryClosingDetailProductDTO.getClosing());
				inventoryClosingDetailProduct.setInitial(inventoryClosingDetailProductDTO.getInitial());
				inventoryClosingDetailProduct.setCompany(company);
				inventoryClosingDetailProduct.setCreatedBy(loggedUser);
				inventoryClosingDetailProduct.setCreatedDate(currentDate);
				inventoryClosingDetailProduct.setInvntoryClosingHeader(inventoryClosingHeader);
				inventoryClosingDetailProduct.setProductProfile(productProfile);
				inventoryClosingDetailProduct = inventoryClosingDetailProductRepository.save(inventoryClosingDetailProduct);
				
				// update opening stock, delete and insert
				OpeningStock openingStock = new OpeningStock();
				openingStock.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
				openingStock.setOpeningStockDate(currentDate);
				openingStock.setCreatedDate(currentDate);
				openingStock.setCompany(company);
				openingStock.setProductProfile(productProfile);
				openingStock.setStockLocation(userSourceLocation.getStockLocation());
				openingStock.setBatchNumber("123");
				openingStock.setQuantity(inventoryClosingDetailProductDTO.getClosing());
				openingStockRepository.save(openingStock);
				
				//save InventoryClosingProductDetailSettingGroup
				List<InventoryClosingProductDetailSettingGroup>inventoryClosingProductDetailSettingGroups=new ArrayList<>();
				for(InventoryClosingProductDetailSettingGroupDTO inventoryClosingProductDetailSettingGroupDTO:inventoryClosingHolder.getInventoryClosingProductDetailSettingGroups()) {
					InventoryClosingProductDetailSettingGroup inventoryClosingProductDetailSettingGroup=new InventoryClosingProductDetailSettingGroup();
					inventoryClosingProductDetailSettingGroup.setCompany(company);
					inventoryClosingProductDetailSettingGroup.setCreatedBy(loggedUser);
					inventoryClosingProductDetailSettingGroup.setCreatedDate(currentDate);
					inventoryClosingProductDetailSettingGroup.setFlow(inventoryClosingProductDetailSettingGroupDTO.getFlow());
					inventoryClosingProductDetailSettingGroup.setInventoryClosingDetailProduct(inventoryClosingDetailProduct);
					InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup=inventoryClosingReportSettingGroupRepository.findOneByPid(inventoryClosingProductDetailSettingGroupDTO.getInventoryClosingReportSettingGroupPid()).get();
					inventoryClosingProductDetailSettingGroup.setInventoryClosingReportSettingGroup(inventoryClosingReportSettingGroup);
					inventoryClosingProductDetailSettingGroups.add(inventoryClosingProductDetailSettingGroup);
				}
				inventoryClosingProductDetailSettingGroupRepository.save(inventoryClosingProductDetailSettingGroups);
			}
			
		}
	}

}
