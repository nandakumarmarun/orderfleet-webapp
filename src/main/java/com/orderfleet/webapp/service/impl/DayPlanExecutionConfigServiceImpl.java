package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DayPlanExecutionConfig;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DayPlanPages;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DayPlanExecutionConfigRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DayPlanExecutionConfigService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DayPlanExecutionConfigDTO;

/**
 * Service Implementation for managing DayPlanExecutionConfig.
 * 
 * @author Muhammed Riyas T
 * @since Jan 03, 2017
 */
@Service
@Transactional
public class DayPlanExecutionConfigServiceImpl implements DayPlanExecutionConfigService {

	private final Logger log = LoggerFactory.getLogger(DayPlanExecutionConfigServiceImpl.class);

	@Inject
	private DayPlanExecutionConfigRepository dayPlanExecutionConfigRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a dayPlanExecutionConfigs.
	 * 
	 * @param dayPlanExecutionConfigDTOs
	 *            the entity to save
	 */
	@Override
	public void save(List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigDTOs) {
		log.debug("Request to save DayPlanExecutionConfigs");
		List<DayPlanExecutionConfig> dayPlanExecutionConfigs = new ArrayList<>();
		for (DayPlanExecutionConfigDTO dayPlanExecutionConfigDTO : dayPlanExecutionConfigDTOs) {
			Optional<DayPlanExecutionConfig> opDayPlanExecutionConfig = dayPlanExecutionConfigRepository
					.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(),
							dayPlanExecutionConfigDTO.getName());
			DayPlanExecutionConfig dayPlanExecutionConfig = null;
			if (opDayPlanExecutionConfig.isPresent()) {
				dayPlanExecutionConfig = opDayPlanExecutionConfig.get();
				dayPlanExecutionConfig.setEnabled(dayPlanExecutionConfigDTO.getEnabled());
				dayPlanExecutionConfig.setSortOrder(dayPlanExecutionConfigDTO.getSortOrder());
			} else {
				dayPlanExecutionConfig = new DayPlanExecutionConfig();
				dayPlanExecutionConfig.setName(dayPlanExecutionConfigDTO.getName());
				dayPlanExecutionConfig.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
				dayPlanExecutionConfig.setEnabled(dayPlanExecutionConfigDTO.getEnabled());
				dayPlanExecutionConfig.setSortOrder(dayPlanExecutionConfigDTO.getSortOrder());
			}
			dayPlanExecutionConfigs.add(dayPlanExecutionConfig);
		}
		dayPlanExecutionConfigRepository.save(dayPlanExecutionConfigs);
	}

	@Override
	public void saveAssignedUsers(Long id, String assignedUsers) {
		DayPlanExecutionConfig dayPlanExecutionConfig = dayPlanExecutionConfigRepository.findOne(id);
		String[] users = assignedUsers.split(",");
		Set<User> listUsers = new HashSet<User>();
		for (String userId : users) {
			User user = userRepository.findOneByPid(userId).get();
			listUsers.add(user);
		}
		dayPlanExecutionConfig.setUsers(listUsers);
		dayPlanExecutionConfigRepository.save(dayPlanExecutionConfig);
	}

	/**
	 * Get all the dayPlanExecutionConfigs.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DayPlanExecutionConfigDTO> findAllByCompany() {
		log.debug("Request to get all DayPlanExecutionConfigs");
		List<DayPlanExecutionConfig> dayPlanExecutionConfigList = dayPlanExecutionConfigRepository.findAllByCompanyId();
		List<DayPlanExecutionConfigDTO> result = dayPlanExecutionConfigList.stream().map(DayPlanExecutionConfigDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all enabled dayPlanExecutionConfigs.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DayPlanExecutionConfigDTO> findAllByCompanyIdAndEnabledTrueCurrentUser() {
		log.debug("Request to get all DayPlanExecutionConfigs");
		List<DayPlanExecutionConfig> dayPlanExecutionConfigList = dayPlanExecutionConfigRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
		String login = SecurityUtils.getCurrentUserLogin();
		List<DayPlanExecutionConfigDTO> result = new ArrayList<>();
		for (DayPlanExecutionConfig dayPlanExecutionConfig : dayPlanExecutionConfigList) {
			for (User user : dayPlanExecutionConfig.getUsers()) {
				if (login.equals(user.getLogin())) {
					result.add(new DayPlanExecutionConfigDTO(dayPlanExecutionConfig));
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Get all enabled dayPlanExecutionConfigs.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DayPlanExecutionConfigDTO> findAllByCompanyIdAndEnabledTrue() {
		log.debug("Request to get all DayPlanExecutionConfigs");
		List<DayPlanExecutionConfig> dayPlanExecutionConfigList = dayPlanExecutionConfigRepository
				.findAllByCompanyIdAndEnabledTrue(SecurityUtils.getCurrentUsersCompanyId());
		List<DayPlanExecutionConfigDTO> result = dayPlanExecutionConfigList.stream().map(DayPlanExecutionConfigDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one dayPlanExecutionConfig by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DayPlanExecutionConfigDTO> findByName(DayPlanPages name) {
		log.debug("Request to get DayPlanExecutionConfig by name : {}", name);
		return dayPlanExecutionConfigRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(dayPlanExecutionConfig -> {
					DayPlanExecutionConfigDTO dayPlanExecutionConfigDTO = new DayPlanExecutionConfigDTO(
							dayPlanExecutionConfig);
					return dayPlanExecutionConfigDTO;
				});
	}

	@Override
	public DayPlanExecutionConfigDTO findById(Long id) {
		DayPlanExecutionConfig dayPlanExecutionConfig = dayPlanExecutionConfigRepository.findOne(id);
		DayPlanExecutionConfigDTO dayPlanExecutionConfigDTO = new DayPlanExecutionConfigDTO(dayPlanExecutionConfig);
		dayPlanExecutionConfigDTO
				.setUsers(dayPlanExecutionConfig.getUsers().stream().map(UserDTO::new).collect(Collectors.toSet()));
		return dayPlanExecutionConfigDTO;
	}

}
