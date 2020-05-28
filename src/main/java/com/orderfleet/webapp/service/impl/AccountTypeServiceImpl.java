package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountTypeMapper;

/**
 * Service Implementation for managing AccountType.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
@Service
@Transactional
public class AccountTypeServiceImpl implements AccountTypeService {

	private final Logger log = LoggerFactory.getLogger(AccountTypeServiceImpl.class);

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AccountTypeMapper accountTypeMapper;

	/**
	 * Save a accountType.
	 * 
	 * @param accountTypeDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountTypeDTO save(AccountTypeDTO accountTypeDTO) {
		log.debug("Request to save AccountType : {}", accountTypeDTO);

		// set pid
		accountTypeDTO.setPid(AccountTypeService.PID_PREFIX + RandomUtil.generatePid());
		AccountType accountType = accountTypeMapper.accountTypeDTOToAccountType(accountTypeDTO);
		// set company
		accountType.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		accountType = accountTypeRepository.save(accountType);
		AccountTypeDTO result = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
		return result;
	}

	/**
	 * Update a accountType.
	 * 
	 * @param accountTypeDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public AccountTypeDTO update(AccountTypeDTO accountTypeDTO) {
		log.debug("Request to Update AccountType : {}", accountTypeDTO);
		return accountTypeRepository.findOneByPid(accountTypeDTO.getPid()).map(accountType -> {
			accountType.setName(accountTypeDTO.getName());
			accountType.setAlias(accountTypeDTO.getAlias());
			accountType.setAccountNameType(accountTypeDTO.getAccountNameType());
			accountType.setDescription(accountTypeDTO.getDescription());
			accountType.setReceiverSupplierType(accountTypeDTO.getReceiverSupplierType());
			accountType = accountTypeRepository.save(accountType);
			AccountTypeDTO result = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the accountTypes.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountType> findAll(Pageable pageable) {
		log.debug("Request to get all AccountTypes");
		Page<AccountType> result = accountTypeRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the accountTypes.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountTypeDTO> findAllByCompany() {
		log.debug("Request to get all AccountTypes");
		List<AccountType> accountTypeList = accountTypeRepository.findAllByCompanyId();
		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypeList);
		return result;
	}

	/**
	 * Get all the accountTypes.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<AccountTypeDTO> findAllByCompanyAndActivated(Pageable pageable, boolean activated) {
		log.debug("Request to get all AccountTypes");
		Page<AccountType> accountTypes = accountTypeRepository.findAllByCompanyAndActivated(activated, pageable);
		Page<AccountTypeDTO> result = new PageImpl<AccountTypeDTO>(
				accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypes.getContent()), pageable,
				accountTypes.getTotalElements());
		return result;
	}

	/**
	 * Get one accountType by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public AccountTypeDTO findOne(Long id) {
		log.debug("Request to get AccountType : {}", id);
		AccountType accountType = accountTypeRepository.findOne(id);
		AccountTypeDTO accountTypeDTO = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
		return accountTypeDTO;
	}

	/**
	 * Get one accountType by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountTypeDTO> findOneByPid(String pid) {
		log.debug("Request to get AccountType by pid : {}", pid);
		return accountTypeRepository.findOneByPid(pid).map(accountType -> {
			AccountTypeDTO accountTypeDTO = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
			return accountTypeDTO;
		});
	}

	/**
	 * Get one accountType by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountTypeDTO> findByName(String name) {
		log.debug("Request to get AccountType by name : {}", name);
		return accountTypeRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(accountType -> {
					AccountTypeDTO accountTypeDTO = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
					return accountTypeDTO;
				});
	}

	/**
	 * Delete the accountType by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete AccountType : {}", pid);
		accountTypeRepository.findOneByPid(pid).ifPresent(accountType -> {
			accountTypeRepository.delete(accountType.getId());
		});
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 6, 2017
	 * 
	 *        Update the accountType by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public AccountTypeDTO updateAccountTypeStatus(String pid, Boolean active) {
		log.debug("Request to update AccountType status: {}");
		return accountTypeRepository.findOneByPid(pid).map(accountType -> {
			accountType.setActivated(active);
			accountType = accountTypeRepository.save(accountType);
			AccountTypeDTO result = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all accountTypeDTOs from AccountType by status and company.
	 * 
	 * @param pageable the pagination information
	 * @param active   the active of the entity
	 * @return the list of entities
	 */

	@Override
	@Transactional(readOnly = true)
	public Page<AccountTypeDTO> findAllCompanyAndAccountTypeActivated(Pageable pageable, Boolean active) {
		Page<AccountType> accountTypes = accountTypeRepository.findAllByCompanyIdAndAccountTypeActivated(pageable,
				active);
		Page<AccountTypeDTO> accountTypeDTOs = new PageImpl<AccountTypeDTO>(
				accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypes.getContent()), pageable,
				accountTypes.getTotalElements());
		return accountTypeDTOs;
	}

	/**
	 * @author Sarath
	 * @since Feb 10, 2017
	 * 
	 *        find all deactivated accountTypeDTOs.
	 */

	@Override
	@Transactional(readOnly = true)
	public List<AccountTypeDTO> findAllCompanyAndAccountTypeActivated(boolean active) {
		log.debug("Request to get all active or deactivated AccountTypes");
		List<AccountType> accountTypeList = accountTypeRepository.findAllByCompanyIdAndActivated(active);
		log.info("Account type list size :" + accountTypeList.size());
		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypeList);
		return result;
	}

	/**
	 * Get one accountType by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<AccountTypeDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get AccountType by name : {}", name);
		return accountTypeRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(accountType -> {
			AccountTypeDTO accountTypeDTO = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
			return accountTypeDTO;
		});
	}

	/**
	 * Get all the accountTypes.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<AccountTypeDTO> findAllByCompanyId(Long companyId) {
		log.debug("Request to get all AccountTypes");
		List<AccountType> accountTypeList = accountTypeRepository.findAllByCompanyId(companyId);
		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypeList);
		return result;
	}

	/**
	 * Save a accountType.
	 * 
	 * @param accountTypeDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public AccountTypeDTO saveAccountType(Long companyId, AccountTypeDTO accountTypeDTO) {
		log.debug("Request to save AccountType : {}", accountTypeDTO);

		// set pid
		accountTypeDTO.setPid(AccountTypeService.PID_PREFIX + RandomUtil.generatePid());
		AccountType accountType = accountTypeMapper.accountTypeDTOToAccountType(accountTypeDTO);
		// set company
		accountType.setCompany(companyRepository.findOne(companyId));
		accountType = accountTypeRepository.save(accountType);
		AccountTypeDTO result = accountTypeMapper.accountTypeToAccountTypeDTO(accountType);
		return result;
	}

	/**
	 * Get all the accountTypes.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public AccountTypeDTO findFirstByCompanyId(Long companyId) {
		log.debug("Request to get top One AccountType");
		AccountType accountTypeList = accountTypeRepository.findFirstByCompanyIdOrderByIdAsc(companyId);
		AccountTypeDTO result = accountTypeMapper.accountTypeToAccountTypeDTO(accountTypeList);
		return result;
	}

	@Override
	public Page<AccountTypeDTO> findByLastModifiedAndActivatedTrue(LocalDateTime lastSyncdate, Pageable pageable) {
		Page<AccountType> accountTypes = accountTypeRepository
				.findAllByCompanyIdAndAccountTypeActivatedTrueAndLastModifiedDateGreater(lastSyncdate, pageable);
		Page<AccountTypeDTO> accountTypeDTOs = new PageImpl<AccountTypeDTO>(
				accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypes.getContent()), pageable,
				accountTypes.getTotalElements());
		return accountTypeDTOs;
	}

	@Override
	public List<AccountTypeDTO> findAllByAccountTypePidIn(List<String> accountTypePids) {
		List<AccountType> accountTypeList = accountTypeRepository.findAllByAccountTypePidIn(accountTypePids);
		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypeList);
		return result;
	}
}
