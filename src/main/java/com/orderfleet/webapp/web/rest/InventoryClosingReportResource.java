package com.orderfleet.webapp.web.rest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

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
import com.orderfleet.webapp.domain.InventoryClosingHeader;
import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.UserSourceLocation;
import com.orderfleet.webapp.domain.enums.Flow;
import com.orderfleet.webapp.repository.InventoryClosingHeaderRepository;
import com.orderfleet.webapp.repository.InventoryClosingReportSettingsRepository;
import com.orderfleet.webapp.repository.InventoryVoucherDetailRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductGroupProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserSourceLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.InventoryClosingHeaderService;
import com.orderfleet.webapp.service.InventoryClosingReportSettingGroupService;
import com.orderfleet.webapp.service.UserProductGroupService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingHolder;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryClosingReportSettingGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ProductGroupDTO;

@RequestMapping("/web")
@Controller
public class InventoryClosingReportResource {

	@Inject
	private UserRepository userRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private UserProductGroupService userProductGroupService;

	@Inject
	private ProductGroupProductRepository productGroupProductRepository;

	@Inject
	private InventoryClosingReportSettingGroupService inventoryClosingReportSettingGroupService;

	@Inject
	private InventoryClosingReportSettingsRepository inventoryClosingReportSettingsRepository;

	@Inject
	private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

	@Inject
	private OpeningStockRepository openingStockRepository;
	
	@Inject
	private UserSourceLocationRepository userSourceLocationRepository;
	
	@Inject
	private InventoryClosingHeaderService inventoryClosingHeaderService;
	
	@Inject
	private InventoryClosingHeaderRepository inventoryClosingHeaderRepository;

	@RequestMapping(value = "/inventory-closing-report", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getAllInventoryClosingReport(Model model) {
		// user under current user
		userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).ifPresent(u -> {
			if (u.getShowAllUsersData()) {
				model.addAttribute("employees", employeeProfileService.findAllByCompany());
			} else {
				List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
				model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
			}
		});
		return "company/inventoryClosingReport";
	}
	
	@RequestMapping(value = "/inventory-closing-report/loadClosingReport" , method = RequestMethod.GET)
	public @ResponseBody InventoryClosingReportDTO getDocumentsByUser(@RequestParam(value = "employeePid") String employeePid) {
		InventoryClosingReportDTO inventoryClosingReportDTO = new InventoryClosingReportDTO();
		Optional<EmployeeProfileDTO> optinalEmployeeProfileDTO = employeeProfileService.findOneByPid(employeePid);
		if(optinalEmployeeProfileDTO.isPresent()) {
			String userPid = optinalEmployeeProfileDTO.get().getUserPid();
			//user default stock location
			UserSourceLocation userSourceLocation = userSourceLocationRepository.findSourceLocationsByUserPid(userPid);
			if(userSourceLocation == null) {
				return inventoryClosingReportDTO;
			}
					
			//find product group
			List<ProductGroupDTO> productGroupDTOs = userProductGroupService.findProductGroupsByUserPid(userPid);
			List<String> groupPids = new ArrayList<>();
			for (ProductGroupDTO productGroupDTO : productGroupDTOs) {
				groupPids.add(productGroupDTO.getPid());
			}
			if(!groupPids.isEmpty()) {
				LocalDateTime from = LocalDate.now().minusYears(3).atTime(0, 0);
				Optional<InventoryClosingHeader> optionalICH = inventoryClosingHeaderRepository
						.findTop1ByUserPidOrderByIdDesc(userPid);
				if (optionalICH.isPresent()) {
					from = optionalICH.get().getClosedDate();
				}
				LocalDateTime to = LocalDateTime.now();
				List<InventoryClosingReportSettingGroupDTO> reportSettingGroupDTOHeaders = inventoryClosingReportSettingGroupService.findAllByCompany();
				Collections.sort(reportSettingGroupDTOHeaders);
				
				inventoryClosingReportDTO.setReportSettingGroupDTOHeaders(reportSettingGroupDTOHeaders);
				//find actual product
				List<ProductProfile> productProfileList = productGroupProductRepository.findProductByProductGroupPidIn(groupPids);
				Map<String, List<Double>> productQtyMap = new LinkedHashMap<>();
				for (ProductProfile productProfile : productProfileList) {
					List<Double> quantities = new ArrayList<>();
					Double closingQty = 0d;
					
					Optional<OpeningStock> optionalOpStock = openingStockRepository.findTop1ByProductProfilePidAndStockLocationPidOrderByCreatedDateDesc(productProfile.getPid(), userSourceLocation.getStockLocation().getPid());
					if (optionalOpStock.isPresent()) {
						quantities.add(optionalOpStock.get().getQuantity());
						closingQty = optionalOpStock.get().getQuantity();
					}
					for (InventoryClosingReportSettingGroupDTO icrsGroupDTO : reportSettingGroupDTOHeaders) {
						Set<Long> documentIds = inventoryClosingReportSettingsRepository.findDocumentIdsByReportSettingGroupPid(icrsGroupDTO.getPid());
						Double volume = 0d;
						if(!documentIds.isEmpty()) {
							Set<Long> productProfileIds = new HashSet<>();
							productProfileIds.add(productProfile.getId());
							volume = inventoryVoucherDetailRepository.sumOfVolumeByUserPidAndDocumentsAndProductsAndCreatedDateBetween(userPid, documentIds, productProfileIds,from, to);
						}
						quantities.add(volume);
						if(icrsGroupDTO.getFlow().equals(Flow.IN)) {
							closingQty = closingQty + volume;
						} else {
							closingQty = closingQty - volume;
						}
					}
					//closing
					quantities.add(closingQty);
					productQtyMap.put(productProfile.getName(), quantities);
				}
				
				inventoryClosingReportDTO.setProductQuantityMap(productQtyMap);
			}
		}
		return inventoryClosingReportDTO;
	}
	
	@RequestMapping(value = "/inventory-closing-report/close-inventory-report", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<String> closeInventory(@RequestBody InventoryClosingHolder inventoryClosingHolder) {
		inventoryClosingHeaderService.processInventoryClosing(inventoryClosingHolder);
		return ResponseEntity.ok().body("Success");
	}
}
