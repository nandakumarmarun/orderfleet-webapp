package com.orderfleet.webapp.web.vendor.integre.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.vendor.integre.dto.AccountProfileVendorDTO;

@Service
@Transactional
public class MasterDataDownloadService {

private static final Logger log = LoggerFactory.getLogger(MasterDataDownloadService.class);
private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	
	public List<AccountProfileVendorDTO> getAllNewAccountProfiles(Long companyId,DataSourceType dataSourceType){
		log.info("New Ledgers of company:"+companyId+" and datasourceType :"+dataSourceType);
		List<AccountProfileVendorDTO> accountProfilesVendor = new ArrayList<>();
		List<AccountProfile> accountProfiles = new ArrayList<>();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_136" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get all by compId and data source type";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		accountProfiles = accountProfileRepository.findAllByCompanyIdAndDataSourceType(companyId ,dataSourceType);
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

		accountProfilesVendor = accountProfiles.stream()
								.map(ap -> new AccountProfileVendorDTO(ap))
								.collect(Collectors.toList());
		log.info("number of new ledgers"+accountProfilesVendor.size());
		return accountProfilesVendor;
	}
}
