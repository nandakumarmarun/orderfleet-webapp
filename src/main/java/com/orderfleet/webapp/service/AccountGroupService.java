package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountGroup;
import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;

/**
 * Service Interface for managing AccountGroup.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
/**
 *
 * @author Android
 * @since Feb 7, 2017
 */
public interface AccountGroupService {

	String PID_PREFIX = "AGP-";

	/**
	 * Save a accountGroup.
	 * 
	 * @param accountGroupDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	AccountGroupDTO save(AccountGroupDTO accountGroupDTO);

	/**
	 * Update a accountGroup.
	 * 
	 * @param accountGroupDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	AccountGroupDTO update(AccountGroupDTO accountGroupDTO);

	/**
	 * Get all the accountGroups.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AccountGroup> findAll(Pageable pageable);

	/**
	 * Get all the accountGroups of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AccountGroupDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" accountGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	AccountGroupDTO findOne(Long id);

	/**
	 * Get the accountGroup by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<AccountGroupDTO> findOneByPid(String pid);

	/**
	 * Get the accountGroupDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<AccountGroupDTO> findByName(String name);

	/**
	 * Delete the "id" accountGroup.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	
	/**
	 *  @author Fahad
	 * @since Feb 6, 2017
	 * 
	 * Update  the AccountGroup status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 *@param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	AccountGroupDTO updateAccountGroupStatus(String pid,boolean activate);
	
	/**
	 *  @author Fahad
	 * @since Feb 10, 2017
	 * 
	 * find all active company 
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<AccountGroupDTO> findAllByCompanyAndActivated(Pageable pageable, boolean active);
	
	/**
	 *  @author Fahad
	 * @since Feb 10, 2017
	 * 
	 * find all deactive company 
	 * 
	 *@param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	List<AccountGroupDTO> findAllByCompanyAndDeactivated(boolean deactive);
}
