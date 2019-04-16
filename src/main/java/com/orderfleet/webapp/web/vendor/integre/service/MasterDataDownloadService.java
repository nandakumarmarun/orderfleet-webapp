package com.orderfleet.webapp.web.vendor.integre.service;

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
import com.orderfleet.webapp.web.vendor.integre.dto.AccountProfileVendorDTO;

@Service
@Transactional
public class MasterDataDownloadService {

private static final Logger log = LoggerFactory.getLogger(MasterDataDownloadService.class);
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	
	public List<AccountProfileVendorDTO> getAllNewAccountProfiles(Long companyId,DataSourceType dataSourceType){
		log.info("New Ledgers of company:"+companyId+" and datasourceType :"+dataSourceType);
		List<AccountProfileVendorDTO> accountProfilesVendor = new ArrayList<>();
		List<AccountProfile> accountProfiles = new ArrayList<>();
		accountProfiles = accountProfileRepository.findAllByCompanyIdAndDataSourceType(companyId ,dataSourceType);
		accountProfilesVendor = accountProfiles.stream()
								.map(ap -> new AccountProfileVendorDTO(ap))
								.collect(Collectors.toList());
		log.info("number of new ledgers"+accountProfilesVendor.size());
		return accountProfilesVendor;
	}
}
