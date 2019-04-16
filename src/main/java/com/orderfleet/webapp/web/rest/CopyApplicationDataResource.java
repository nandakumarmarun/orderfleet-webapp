package com.orderfleet.webapp.web.rest;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.service.UserFavouriteDocumentService;
import com.orderfleet.webapp.service.UserMenuItemService;
import com.orderfleet.webapp.service.UserMobileMenuItemGroupService;
import com.orderfleet.webapp.service.UserPriceLevelService;
import com.orderfleet.webapp.service.UserProductCategoryService;
import com.orderfleet.webapp.service.UserProductGroupService;
import com.orderfleet.webapp.service.UserStockLocationService;
import com.orderfleet.webapp.web.rest.dto.CopyApplicationDataDTO;

/**
 * REST controller for copying application data.
 */
@Controller
@RequestMapping("/web/admin/copy-data")
@Secured(AuthoritiesConstants.SITE_ADMIN)
public class CopyApplicationDataResource {
	
	@Inject
	private CompanyService companyService;
	
	@Inject
	private UserMenuItemService userMenuItemService;
	
	@Inject
	private UserMobileMenuItemGroupService userMobileMenuItemGroupService;
	
	@Inject
	private UserActivityService userActivityService;
	
	@Inject
	private UserDocumentService userDocumentService;
	
	@Inject
	private UserFavouriteDocumentService userFavouriteDocumentService;
	
	@Inject
	private UserProductGroupService userProductGroupService;

	@Inject
	private UserProductCategoryService userProductCategoryService;
	
	@Inject
	private UserStockLocationService userStockLocationService;

	@Inject
	private UserPriceLevelService userPriceLevelService;
	
	@Inject
	private EmployeeProfileService employeeProfileService;
	
    @GetMapping
    public String getCopyPage(Model model) {
    	model.addAttribute("companies", companyService.findAllCompaniesByActivatedTrue());
		return "site_admin/copy-app-data";
    }
    
    @PostMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public ResponseEntity<Void> copyUserWiseData(@Valid @RequestBody CopyApplicationDataDTO copyAppDataDTO) {
    	for(String data : copyAppDataDTO.getDataToCopy()) {
    		switch (data) {
			case "webMenu":
				userMenuItemService.copyMenuItems(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "mobileMenu":
				userMobileMenuItemGroupService.copyMobileMenuItemGroup(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "activity":
				userActivityService.copyActivities(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "document":
				userDocumentService.copyDocuments(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "favouriteDocument":
				userFavouriteDocumentService.copyUserFavouriteDocuments(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "productGroup":
				userProductGroupService.copyProductGroups(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "productCategory":
				userProductCategoryService.copyProductCategories(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "stockLocation":
				userStockLocationService.copyStockLocations(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "priceLevel":
				userPriceLevelService.copyPriceLevels(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			case "employeeUser":
				employeeProfileService.copyEmployeeUser(copyAppDataDTO.getFromUserPid(), copyAppDataDTO.getToUsersPid());
				break;
			default:
				break;
			}
    	}
    	return new ResponseEntity<>(HttpStatus.OK);
    }
    
   
}