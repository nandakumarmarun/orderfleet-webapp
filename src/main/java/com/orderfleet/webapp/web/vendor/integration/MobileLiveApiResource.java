package com.orderfleet.webapp.web.vendor.integration;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
				 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get one by pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
				Optional<AccountProfile> optionalAccount = accountProfileRepository.findOneByPid(accountPid);

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
