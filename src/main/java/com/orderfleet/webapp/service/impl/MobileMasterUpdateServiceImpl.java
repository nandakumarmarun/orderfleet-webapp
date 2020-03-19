package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.MobileMasterUpdate;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.MobileMasterUpdateRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.MobileMasterUpdateService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.MobileMasterUpdateDTO;

/**
 * Service Implementation for managing Bank.
 * 
 * @author sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class MobileMasterUpdateServiceImpl implements MobileMasterUpdateService {

	private final Logger log = LoggerFactory.getLogger(MobileMasterUpdateServiceImpl.class);

	@Autowired
	CompanyRepository companyRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	MobileMasterUpdateRepository mobileMasterUpdateRepository;

	@Override
	public MobileMasterUpdate convertMobileMasterUpdate(MobileMasterUpdateDTO mobileMasterUpdateDto) {
		MobileMasterUpdate mobileMasterUpdate = new MobileMasterUpdate();
		Optional<MobileMasterUpdate> opMobileMasterUpdate = mobileMasterUpdateRepository
				.findByUserPid(mobileMasterUpdateDto.getUserPid());
		if (!opMobileMasterUpdate.isPresent()) {
			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			mobileMasterUpdate.setCompany(company);
			mobileMasterUpdate.setPid(MobileMasterUpdateService.PID_PREFIX + RandomUtil.generatePid());
			Optional<User> opUser = userRepository.findOneByPid(mobileMasterUpdateDto.getUserPid());
			if (opUser.isPresent()) {
				mobileMasterUpdate.setUser(opUser.get());
			}
		} else {
			mobileMasterUpdate = opMobileMasterUpdate.get();
		}
		mobileMasterUpdate.setUpdateTime(mobileMasterUpdateDto.getUpdateTime());
		mobileMasterUpdate.setCreatedDate(LocalDateTime.now());
		mobileMasterUpdate.setUserBuildVersion(
				mobileMasterUpdateDto.getUserBuildVersion() != null ? mobileMasterUpdateDto.getUserBuildVersion()
						: "1.0.0.1");

		return mobileMasterUpdate;
	}

	@Override
	@Transactional
	public MobileMasterUpdate saveMobileMasterUpdate(MobileMasterUpdate mobileMasterUpdate) {
		// TODO Auto-generated method stub
		return mobileMasterUpdateRepository.save(mobileMasterUpdate);
	}

	
}
