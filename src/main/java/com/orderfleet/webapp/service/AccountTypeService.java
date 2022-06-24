package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;

/**
 * Service Interface for managing AccountType.
 * 
 * @author Muhammed Riyas T
 * @since May 14, 2016
 */
public interface AccountTypeService {

	String PID_PREFIX = "ACT-";

	/**
	 * Save a accountType.
	 * 
	 * @param accountTypeDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	AccountTypeDTO save(AccountTypeDTO accountTypeDTO);
	
	AccountTypeDTO saveOrUpdate(List<AccountTypeDTO> accountTypeDTOs);

	/**
	 * Update a accountType.
	 * 
	 * @param accountTypeDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	AccountTypeDTO update(AccountTypeDTO accountTypeDTO);

	/**
	 * Get all the accountTypes.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AccountType> findAll(Pageable pageable);

	/**
	 * Get all the accountTypes.
	 * 
	 * @return the list of entities
	 */
	List<AccountTypeDTO> findAllByCompany();

	/**
	 * Get all the accountTypes of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AccountTypeDTO> findAllByCompanyAndActivated(Pageable pageable, boolean activated);

	/**
	 * Get the "id" accountType.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	AccountTypeDTO findOne(Long id);

	/**
	 * Get the accountType by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<AccountTypeDTO> findOneByPid(String pid);

	/**
	 * Get the accountTypeDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<AccountTypeDTO> findByName(String name);

	/**
	 * Delete the "id" accountType.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 6, 2017
	 * 
	 *        Update the accountType by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	AccountTypeDTO updateAccountTypeStatus(String pid, Boolean active);

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all accountTypeDTOs from AccountType by status and company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @param active
	 *            the active of the entity
	 * @return the entities
	 */
	Page<AccountTypeDTO> findAllCompanyAndAccountTypeActivated(Pageable pageable, Boolean active);

	List<AccountTypeDTO> findAllCompanyAndAccountTypeActivated(boolean active);

	Optional<AccountTypeDTO> findByCompanyIdAndName(Long companyId, String name);

	List<AccountTypeDTO> findAllByCompanyId(Long companyId);

	AccountTypeDTO saveAccountType(Long companyId, AccountTypeDTO accountTypeDTO);

	AccountTypeDTO findFirstByCompanyId(Long companyId);
	
	Page<AccountTypeDTO> findByLastModifiedAndActivatedTrue(LocalDateTime lastSyncdate, Pageable pageable);
	
	List<AccountTypeDTO> findAllByAccountTypePidIn(List<String> accountTypePids);
}
