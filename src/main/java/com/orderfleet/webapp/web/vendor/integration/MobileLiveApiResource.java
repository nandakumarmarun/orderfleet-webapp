package com.orderfleet.webapp.web.vendor.integration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.CompanyIntegrationModule;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyIntegrationModuleRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.model.PickingHeader;
import com.orderfleet.webapp.web.vendor.service.YukthiOrderPickingService;
import com.snr.yukti.service.YuktiProductService;
import com.snr.yukti.util.YuktiApiUtil;

@RestController
@RequestMapping("/api")
public class MobileLiveApiResource {
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private CompanyIntegrationModuleRepository companyIntegrationModuleNameRepository;

	@Inject
	private YuktiProductService yuktiProductService;
	
	@Inject
	private YukthiOrderPickingService yukthiOrderPickingService;
	
	@GetMapping("/vendor/product/get-price")
	public String getPrice(@RequestParam String productPid, @RequestParam String accountPid) {
		Optional<CompanyIntegrationModule> integrationModuleName = companyIntegrationModuleNameRepository.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if(integrationModuleName.isPresent() && Constants.YUKTI_ERP.equals(integrationModuleName.get().getIntegrationModule().getName())) {
			String apiBaseUrl = integrationModuleName.get().getBaseUrl();
			if(apiBaseUrl != null) {
				Long companyId = SecurityUtils.getCurrentUsersCompanyId();
				YuktiApiUtil.setApiEndpoint(companyId, apiBaseUrl);
				Optional<ProductProfile> optionalProduct = productProfileRepository.findOneByPid(productPid);
				Optional<AccountProfile> optionalAccount = accountProfileRepository.findOneByPid(accountPid);
				if(optionalProduct.isPresent() && optionalAccount.isPresent()) {
					return yuktiProductService.getProductSellingPriceRetailPriceAndTax(optionalProduct.get().getAlias(), optionalAccount.get().getAlias(), companyId);
				}
			}
			
		}
		return null;
	}
	
	@GetMapping("/vendor/orders")
	public List<PickingHeader> getOrdersForPicking(@RequestParam String fromDate, @RequestParam String toDate) {
		Optional<CompanyIntegrationModule> integrationModuleName = companyIntegrationModuleNameRepository.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if(integrationModuleName.isPresent() && Constants.YUKTI_ERP.equals(integrationModuleName.get().getIntegrationModule().getName())) {
			String apiBaseUrl = integrationModuleName.get().getBaseUrl();
			if(apiBaseUrl != null) {
				Long companyId = SecurityUtils.getCurrentUsersCompanyId();
				YuktiApiUtil.setApiEndpoint(companyId, apiBaseUrl);
				return yukthiOrderPickingService.getOrdersForPicking(fromDate, toDate, companyId);
			}
		}
		return Collections.emptyList();
	}
	
	@PostMapping("/vendor/orders")
	public ResponseEntity<String> savePickedOrders(@RequestBody PickingHeader pickingHeader) {
		Optional<CompanyIntegrationModule> integrationModuleName = companyIntegrationModuleNameRepository.findByCompanyId(SecurityUtils.getCurrentUsersCompanyId());
		if(integrationModuleName.isPresent() && Constants.YUKTI_ERP.equals(integrationModuleName.get().getIntegrationModule().getName())) {
			String apiBaseUrl = integrationModuleName.get().getBaseUrl();
			if(apiBaseUrl != null) {
				Long companyId = SecurityUtils.getCurrentUsersCompanyId();
				YuktiApiUtil.setApiEndpoint(companyId, apiBaseUrl);
				yukthiOrderPickingService.savePickedOrders(pickingHeader, companyId);
				return ResponseEntity.ok("Success");
			}
		}
		return ResponseEntity.ok("No vendor module configured for this company");
	}
	
}
