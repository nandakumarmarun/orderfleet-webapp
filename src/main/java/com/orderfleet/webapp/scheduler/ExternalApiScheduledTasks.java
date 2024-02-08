package com.orderfleet.webapp.scheduler;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.web.rest.InvoiceDetailsToDenormalizedTable;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.config.Constants;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
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
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private InvoiceDetailsToDenormalizedTable invoiceDetailsToDenormalizedTable;
	
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
    public void scheduleFocusWithCronExpression() throws URISyntaxException, IOException, JSONException, ParseException {
        log.info("External API Master Data Update Cron Task in focus :: Execution Time - {}", LocalDateTime.now());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("Fixed Rate scheduler:: " + strDate);
        final Long companyId = (long) 304975;
        Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(companyId, CompanyConfig.SEND_TO_FOCUS);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
			uploadFocusResourceAutoShedule.uploadToFocusAutomatically();
		
		}
		}
    }
	//run on 11:59 pm every day
	@Scheduled(cron = "0 59 23 * * * ")
	public void scheduleInvoiceTransferToDenormalized() throws URISyntaxException, IOException, JSONException, ParseException {
		log.info("Transferring failed Data  to denormalized table");
		LocalDate fDate = LocalDate.now();
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = fDate.atTime(23, 59);
		List<ExecutiveTaskExecution> executiveTaskExecutions  = executiveTaskExecutionRepository.findAllExecutiveTaskExecutionByInvoiceStatusFalseAndCreatedDateBetween(fromDate,toDate);
      if(executiveTaskExecutions.size()>0) {
		  invoiceDetailsToDenormalizedTable.filterExecutiveTaskExecutionsDenormalized(executiveTaskExecutions);
	  }
         log.info("Data successfully Transferred:"+executiveTaskExecutions.size());
		}

}
