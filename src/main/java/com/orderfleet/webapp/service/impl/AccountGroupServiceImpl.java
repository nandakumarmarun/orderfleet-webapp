package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountGroup;

import com.orderfleet.webapp.repository.AccountGroupRepository;

import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;

import com.orderfleet.webapp.web.rest.mapper.AccountGroupMapper;

/**
 * Service Implementation for managing AccountGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Service
@Transactional
public class AccountGroupServiceImpl implements AccountGroupService {

	private final Logger log = LoggerFactory.getLogger(AccountGroupServiceImpl.class);

	@Inject
	private AccountGroupRepository accountGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountGroupMapper accountGroupMapper;

	/**
	 * Save a accountGroup.
	 * 
	 * @param accountGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountGroupDTO save(AccountGroupDTO accountGroupDTO) {
		log.debug("Request to save AccountGroup : {}", accountGroupDTO);

		// set pid
		accountGroupDTO.setPid(AccountGroupService.PID_PREFIX + RandomUtil.generatePid());
		AccountGroup accountGroup = accountGroupMapper.accountGroupDTOToAccountGroup(accountGroupDTO);
		// set company
		accountGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		accountGroup = accountGroupRepository.save(accountGroup);
		AccountGroupDTO result = accountGroupMapper.accountGroupToAccountGroupDTO(accountGroup);
		return result;
	}

	/**
	 * Update a accountGroup.
	 * 
	 * @param accountGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public AccountGroupDTO update(AccountGroupDTO accountGroupDTO) {
		log.debug("Request to Update AccountGroup : {}", accountGroupDTO);
		return accountGroupRepository.findOneByPid(accountGroupDTO.getPid()).map(accountGroup -> {
			accountGroup.setName(accountGroupDTO.getName());
			accountGroup.setAlias(accountGroupDTO.getAlias());
			accountGroup.setDescription(accountGroupDTO.getDescription());
			accountGroup = accountGroupRepository.save(accountGroup);
			AccountGroupDTO result = accountGroupMapper.accountGroupToAccountGroupDTO(accountGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the accountGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountGroup> findAll(Pageable pageable) {
		log.debug("Request to get all AccountGroups");
		Page<AccountGroup> result = accountGroupRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the accountGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountGroupDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all AccountGroups");
		Page<AccountGroup> accountGroups = accountGroupRepository.findAllByCompanyIdOrderByAccountGroupName(pageable);
		Page<AccountGroupDTO> result = new PageImpl<AccountGroupDTO>(
				accountGroupMapper.accountGroupsToAccountGroupDTOs(accountGroups.getContent()), pageable,
				accountGroups.getTotalElements());
		return result;
	}

	/**
	 * Get one accountGroup by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public AccountGroupDTO findOne(Long id) {
		log.debug("Request to get AccountGroup : {}", id);
		AccountGroup accountGroup = accountGroupRepository.findOne(id);
		AccountGroupDTO accountGroupDTO = accountGroupMapper.accountGroupToAccountGroupDTO(accountGroup);
		return accountGroupDTO;
	}

	/**
	 * Get one accountGroup by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get AccountGroup by pid : {}", pid);
		return accountGroupRepository.findOneByPid(pid).map(accountGroup -> {
			AccountGroupDTO accountGroupDTO = accountGroupMapper.accountGroupToAccountGroupDTO(accountGroup);
			return accountGroupDTO;
		});
	}

	/**
	 * Get one accountGroup by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountGroupDTO> findByName(String name) {
		log.debug("Request to get AccountGroup by name : {}", name);
		return accountGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountGroup -> {
					AccountGroupDTO accountGroupDTO = accountGroupMapper.accountGroupToAccountGroupDTO(accountGroup);
					return accountGroupDTO;
				});
	}

	/**
	 * Delete the accountGroup by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete AccountGroup : {}", pid);
		accountGroupRepository.findOneByPid(pid).ifPresent(accountGroup -> {
			accountGroupRepository.delete(accountGroup.getId());
		});
	}

	/**
	 * @author Fahad
	 * @since Feb 6, 2017
	 * 
	 *        Update the AccountGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public AccountGroupDTO updateAccountGroupStatus(String pid, boolean activate) {
		log.debug("Request to update AccountGroup status");
		return accountGroupRepository.findOneByPid(pid).map(accountGroup -> {
			accountGroup.setActivated(activate);
			accountGroup = accountGroupRepository.save(accountGroup);
			AccountGroupDTO result = accountGroupMapper.accountGroupToAccountGroupDTO(accountGroup);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 10, 2017
	 * 
	 *        find all active company
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public Page<AccountGroupDTO> findAllByCompanyAndActivated(Pageable pageable, boolean active) {
		log.debug("Request to get all active accountGroup");
		Page<AccountGroup> accountGroupList = accountGroupRepository.findAllByCompanyAndActive(pageable, active);
		Page<AccountGroupDTO> result = new PageImpl<AccountGroupDTO>(
				accountGroupMapper.accountGroupsToAccountGroupDTOs(accountGroupList.getContent()), pageable,
				accountGroupList.getTotalElements());
		return result;
	}

	/**
	 * @author Fahad
	 * @since Feb 10, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<AccountGroupDTO> findAllByCompanyAndDeactivated(boolean deactive) {
		log.debug("Request to get all Deactive accountGroup");
		List<AccountGroup> accountGroupList = accountGroupRepository.findAllByCompanyAndDeactive(deactive);
		List<AccountGroupDTO> result = accountGroupMapper.accountGroupsToAccountGroupDTOs(accountGroupList);
		return result;
	}

}
