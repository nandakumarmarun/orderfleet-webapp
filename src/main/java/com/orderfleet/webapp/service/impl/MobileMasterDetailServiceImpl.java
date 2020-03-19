package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.MobileMasterDetail;
import com.orderfleet.webapp.domain.MobileMasterUpdate;
import com.orderfleet.webapp.repository.BankRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.MobileMasterDetailRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BankService;
import com.orderfleet.webapp.service.MobileMasterDetailService;
import com.orderfleet.webapp.service.MobileMasterUpdateService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.dto.MobileMasterDetailDTO;
import com.orderfleet.webapp.web.rest.mapper.BankMapper;


@Service
@Transactional
public class MobileMasterDetailServiceImpl implements MobileMasterDetailService {

	private final Logger log = LoggerFactory.getLogger(MobileMasterDetailServiceImpl.class);

	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	MobileMasterDetailRepository mobileMasterDetailRepository;
	
	@Override
	public List<MobileMasterDetail> convertMobileMasterDetails(List<MobileMasterDetailDTO> mobileMasterDetailDtos,
														MobileMasterUpdate mobileMasterUpdate) {
		List<MobileMasterDetail> mmdList = new ArrayList<>();
		List<MobileMasterDetail> mmdExistingList = new ArrayList<>();
		mmdExistingList =  mobileMasterDetailRepository.findByMobileMasterUpdateId(mobileMasterUpdate.getId());
		
		for(MobileMasterDetailDTO dto : mobileMasterDetailDtos) {
			MobileMasterDetail mmd = new MobileMasterDetail();
			Optional<MobileMasterDetail> opMobileExisting = 
					mmdExistingList.stream().filter(mmel ->
										mmel.getMobileMasterItem()==dto.getMobileMasterItem()).findAny();
			if(!opMobileExisting.isPresent()) {
				Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
				mmd.setCompany(company);
				mmd.setMobileMasterItem(dto.getMobileMasterItem());
				mmd.setMobileMasterUpdate(mobileMasterUpdate);
			}else {
				mmd = opMobileExisting.get();
			}
			mmd.setCreatedDate(LocalDateTime.now());
			mmd.setCount(dto.getCount());
			mmd.setOperationTime(dto.getOperationTime());
			
			mmdList.add(mmd);
		}
		
		for(MobileMasterDetail emmd : mmdExistingList) {
			Optional<MobileMasterDetail> opmmd = 
					mmdList.stream().filter(mmd -> 
						mmd.getMobileMasterItem() == emmd.getMobileMasterItem()).findAny();
			
			if(!opmmd.isPresent()) {
				mmdList.add(emmd);
			}
		}
		
		return mmdList;
	}

	@Override
	@Transactional
	public List<MobileMasterDetail> saveMobileMasterDetails(List<MobileMasterDetail> mobileMasterDetails) {
		// TODO Auto-generated method stub
		return mobileMasterDetailRepository.save(mobileMasterDetails);
	}
	
	
	
	
}
