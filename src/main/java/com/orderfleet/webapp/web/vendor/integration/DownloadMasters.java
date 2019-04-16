package com.orderfleet.webapp.web.vendor.integration;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyIntegrationModule;
import com.orderfleet.webapp.repository.CompanyIntegrationModuleRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.service.YuktiMasterDataService;
import com.snr.yukti.util.YuktiApiUtil;

@Controller
@RequestMapping("/web")
public class DownloadMasters {
	
	@Inject
	private YuktiMasterDataService yuktiMasterDataService;
	
	@Inject
	private CompanyIntegrationModuleRepository companyIntegrationModuleNameRepository;
	
	@RequestMapping(value = "/masters", method = RequestMethod.GET)
	public String downloadMasterPage(Model model) {
		return "company/vendor/download-master";
	}

	@PostMapping("/masters/download")
	@ResponseBody
	public ResponseEntity<String> downloadMasters(@RequestBody VendorIntegrationRequest tpIntegrationRequest) {
		Optional<CompanyIntegrationModule> integrationModuleName = companyIntegrationModuleNameRepository.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if(integrationModuleName.isPresent()) {
			final CompanyIntegrationModule moduleName = integrationModuleName.get();
			for (String master : tpIntegrationRequest.getMasters()) {
				downloadAndSaveMaster(master, moduleName);
			}
			return ResponseEntity.ok().body("SUCCESS");
		} else {
			return ResponseEntity.ok().body("No vendor module configured for this company");
		}
	}
	
	private void downloadAndSaveMaster(String master, CompanyIntegrationModule integrationModule) {
		if(Constants.YUKTI_ERP.equals(integrationModule.getIntegrationModule().getName())) {
			String apiBaseUrl = integrationModule.getBaseUrl();
			if(apiBaseUrl != null) {
				YuktiApiUtil.setApiEndpoint(integrationModule.getCompany().getId(), apiBaseUrl);
				downloadAndSaveMasterFronYuktiERP(master, integrationModule.getCompany());
			}
		}
	}
	
	private void downloadAndSaveMasterFronYuktiERP(String master, Company company) {
		switch (master) {
		case "USERACCOUNT" :
			yuktiMasterDataService.downloadSaveUserAccounts(company);
			break;
		case "PRODUCTCATEGORY" :
			yuktiMasterDataService.downloadSaveProductCategory(company);
			break;
		case "PRODUCTPROFILE":
			yuktiMasterDataService.downloadSaveProductProfile(company);
			break;
		case "TERRITORY":
			yuktiMasterDataService.downloadSaveTerritories(company);
			break;
		case "ACCOUNT_PROFILE":
			yuktiMasterDataService.downloadSaveAccountProfile(company);
			break;
		case "RECEIVABLE_PAYABLE":
			yuktiMasterDataService.downloadSaveReceivables(company);
			break;
		default:
			break;
		}
	}

}
