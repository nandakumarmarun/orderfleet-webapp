package com.orderfleet.webapp.scheduler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyIntegrationModule;
import com.orderfleet.webapp.repository.CompanyIntegrationModuleRepository;
import com.orderfleet.webapp.web.rest.UploadFocusResourceAutoShedule;
import com.orderfleet.webapp.web.vendor.service.YuktiMasterDataService;
import com.snr.yukti.util.YuktiApiUtil;

import net.minidev.json.parser.ParseException;

@Component
public class ExternalApiScheduledTasks {

	private static final Logger log = LoggerFactory.getLogger(ExternalApiScheduledTasks.class);

	@Inject
	private CompanyIntegrationModuleRepository companyIntegrationModuleNameRepository;
	
	@Inject
	private YuktiMasterDataService yuktiMasterDataService;
	
	@Inject
	private UploadFocusResourceAutoShedule uploadFocusResourceAutoShedule;
	
	//run on 11pm every day
	@Scheduled(cron = "0 0 23 * * ?")
    public void scheduleTaskWithCronExpression() {
        log.info("External API Master Data Update Cron Task :: Execution Time - {}", LocalDateTime.now());
        List<CompanyIntegrationModule> integrationModuleName = companyIntegrationModuleNameRepository.findByAutoUpdateTrue();
        for (CompanyIntegrationModule companyIntegrationModule : integrationModuleName) {
        	if(Constants.YUKTI_ERP.equals(companyIntegrationModule.getIntegrationModule().getName())) {
        		String apiBaseUrl = companyIntegrationModule.getBaseUrl();
    			if(apiBaseUrl != null) {
    				log.info("Schedular is ready to auto update Master data on YUKI ERP with api - {}", apiBaseUrl);
    				YuktiApiUtil.setApiEndpoint(companyIntegrationModule.getCompany().getId(), apiBaseUrl);
    				updateYuktiErpMasterData(companyIntegrationModule.getCompany());
    			}
    		}
		}
    }
	
	private void updateYuktiErpMasterData(Company company) {
		List<String> masters = Arrays.asList("PRODUCTCATEGORY", "PRODUCTPROFILE" , "ACCOUNT_PROFILE");
		for (String master : masters) {
			downloadAndSaveMasterFronYuktiERP(master, company);
		}
	}
	
	private void downloadAndSaveMasterFronYuktiERP(String master, Company company) {
		log.info("Scheduled Auto update With Master {} and company id {} Started", master, company.getId());
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
	
	@Async
	@Scheduled(fixedRate = 3600000)
    public void scheduleKoglandWithCronExpression() throws URISyntaxException, IOException, JSONException, ParseException {
        log.info("External API Master Data Update Cron Task in focus :: Execution Time - {}", LocalDateTime.now());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Fixed Rate scheduler:: " + strDate);
        uploadFocusResourceAutoShedule.uploadToFocusAutomatically();
    }
}
