package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.MobileConfiguration;
import com.orderfleet.webapp.domain.UserWiseMobileConfiguration;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserWiseMobileConfigurationRepository;
import com.orderfleet.webapp.service.UserWiseMobileConfigurationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.UserWiseMobileConfigurationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service Implementation for managing MobileConfiguration.
 *
 * @author Resmi T R
 * @since Nov 27, 2023
 *
 */
@Service
@Transactional
public class UserWIseMobileConfigurationServiceImpl implements UserWiseMobileConfigurationService{

	private final Logger log = LoggerFactory.getLogger(UserWIseMobileConfigurationServiceImpl.class);

	@Inject
	private UserWiseMobileConfigurationRepository mobileConfigurationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Override
	public UserWiseMobileConfigurationDTO saveUserMobileConfiguration(UserWiseMobileConfigurationDTO mobileConfigurationDTO) {
		log.debug("Request to save user MobileConfiguration : {}", mobileConfigurationDTO);
		UserWiseMobileConfiguration configuration = new UserWiseMobileConfiguration();

		configuration.setPid(UserWiseMobileConfigurationService.PID_PREFIX + RandomUtil.generatePid()); // set
		configuration.setCompany(companyRepository.findOneByPid(mobileConfigurationDTO.getCompanyPid()).get());
		configuration.setUser(userRepository.findOneByPid(mobileConfigurationDTO.getUserPid()).get());
		configuration.setLiveRouting(mobileConfigurationDTO.isLiveRouting());
		configuration = mobileConfigurationRepository.save(configuration);
		UserWiseMobileConfigurationDTO result = new UserWiseMobileConfigurationDTO(configuration);
		return result;
	}

	@Override
	public UserWiseMobileConfigurationDTO updateMobileConfiguration(UserWiseMobileConfigurationDTO mobileConfigurationDTO) {
		log.debug("Request to update user MobileConfiguration : {}", mobileConfigurationDTO);
		return mobileConfigurationRepository.findOneByPid(mobileConfigurationDTO.getPid()).map(configuration -> {

			configuration.setLiveRouting(mobileConfigurationDTO.isLiveRouting());

			configuration = mobileConfigurationRepository.save(configuration);
			UserWiseMobileConfigurationDTO result = new UserWiseMobileConfigurationDTO(configuration);
			return result;
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<UserWiseMobileConfigurationDTO> findOneByPid(String pid) {
		log.debug("Request to get all user MobileConfigurations");
		return mobileConfigurationRepository.findOneByPid(pid).map(config -> {
			UserWiseMobileConfigurationDTO mobileConfigurationDTO = new UserWiseMobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public UserWiseMobileConfigurationDTO findByCompanyId(Long companyId) {
		log.debug("Request to get user MobileConfigurations by company");
		UserWiseMobileConfiguration configuration = mobileConfigurationRepository.findByCompanyId(companyId);
		UserWiseMobileConfigurationDTO mobileConfigurationDTO = null;
		if (configuration != null) {
			mobileConfigurationDTO = new UserWiseMobileConfigurationDTO(configuration);
		}
		return mobileConfigurationDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserWiseMobileConfigurationDTO> findAll() {
		log.debug("Request to get all user MobileConfigurations");
		return mobileConfigurationRepository.findAll().stream().map(UserWiseMobileConfigurationDTO::new)
				.collect(Collectors.toList());
	}


	@Override
	public void deleteByPid(String pid) {
		log.debug("Request to delete User MobileConfigurations");
		mobileConfigurationRepository.deleteByPid(pid);
	}

	@Override
	public Optional<UserWiseMobileConfigurationDTO> findOneByCompanyId(Long companyId) {
		log.debug("Request to get all User MobileConfigurations");
		return mobileConfigurationRepository.findOneByCompanyId(companyId).map(config -> {
			UserWiseMobileConfigurationDTO mobileConfigurationDTO = new UserWiseMobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

	@Override
	public Optional<UserWiseMobileConfigurationDTO> findOneByCompanyId() {
		log.debug("Request to get all Uesr MobileConfigurations");
		return mobileConfigurationRepository.findOneByCompanyId().map(config -> {
			UserWiseMobileConfigurationDTO mobileConfigurationDTO = new UserWiseMobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

	@Override
	public Optional<UserWiseMobileConfigurationDTO> findOneByUserId(Long id) {
		log.debug("Request to get all Uesr MobileConfigurations by userId");
		return mobileConfigurationRepository.findOneByUserId(id).map(config -> {
			UserWiseMobileConfigurationDTO mobileConfigurationDTO = new UserWiseMobileConfigurationDTO(config);
			return mobileConfigurationDTO;
		});
	}

}
