package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.AccountNameType;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LeadManagementDTO;

/**
 * Service Interface for managing AccountProfile.
 *
 * @author Muhammed Riyas T
 * @since June 02, 2016
 */
public interface AccountProfileService {

	String PID_PREFIX = "ACCP-";

	/**
	 * Save a accountProfile.
	 *
	 * @param accountProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	AccountProfileDTO save(AccountProfileDTO accountProfileDTO);

	/**
	 * Update a accountProfile.
	 *
	 * @param accountProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	AccountProfileDTO update(AccountProfileDTO accountProfileDTO);

	/**
	 * Get all the accountProfiles.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AccountProfile> findAll(Pageable pageable);

	/**
	 * Get all the accountProfiles.
	 *
	 * @return the list of entities
	 */
	List<AccountProfileDTO> findAllByCompany();

	/**
	 * Get all the accountProfiles.
	 *
	 * @param pid
	 * @return the list of entities
	 */
	List<AccountProfileDTO> findAllByAccountType(String pid);

	/**
	 * Get all the accountProfiles of a company.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<AccountProfileDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" accountProfile.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	AccountProfileDTO findOne(Long id);

	/**
	 * Get the accountProfile by "pid".
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<AccountProfileDTO> findOneByPid(String pid);

	/**
	 * Get the accountProfileDTO by "name".
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<AccountProfileDTO> findByName(String name);

	/**
	 * Delete the "id" accountProfile.
	 *
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	/**
	 * Get the accountProfileDTO by "alias".
	 *
	 * @param alias
	 *            the alias of the entity
	 * @return the entity
	 */
	Optional<AccountProfileDTO> findByAlias(String alias);

	List<AccountProfileDTO> findAllByAccountTypeName(String accountTypeName);

	List<AccountProfileDTO> findAllByCompanyAndAccountImportStatus(boolean importStatus);

	AccountProfileDTO updateAccountProfileImportStatus(AccountProfileDTO accountProfileDTO);

	List<AccountProfileDTO> findUsersAccountProfile(String userPid);

	AccountProfileDTO updateAccountProfileStatus(String pid, boolean active);
	
	AccountProfileDTO updateAccountProfileVerifiedStatus(String pid, AccountStatus verified);

	List<AccountProfileDTO> findAllByCompanyAndActivated(boolean active);

	Optional<AccountProfileDTO> findByCompanyIdAndName(Long companyId, String name);

	AccountProfileDTO saveAccountProfile(Long companyId, AccountProfileDTO accountProfileDTO, String userLogin);

	List<AccountProfileDTO> findByPidNotIn(List<String> accountPids, String AccountTypePid);

	List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivated(List<String> accountTypePids);

	List<AccountProfileDTO> findAccountProfileByAccountTypePidInAndActivatedAndImportStatus(List<String> accountTypePids,boolean importStatus);

	List<AccountProfileDTO> findAllByCompanyAndAccountImportStatusAndActivated(boolean importStatus);

	Optional<AccountProfile> findOneByCompanyIdAndName(Long companyId, String name);
	
	AccountProfileDTO updateImportedStatus(String pid, boolean active);
	
	List<AccountProfileDTO> findByCompanyIdAndUserIdInAndImportStatusAndCreatedDateBetweenOrderByCreatedDateDesc(Long companyId,
			List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate,boolean active);
	
	List<AccountProfileDTO> findByCompanyIdAndUserIdInAndDatasourceTypeMobileAndLastModifiedDateBetweenOrderByCreatedDateDesc(Long companyId,
			List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);
	
	List<AccountProfileDTO> findAllByCompanyAndDatasourceType(DataSourceType dataSourceType);
	
	List<LeadManagementDTO> findAllByCompanyAndAccountTypeAccountNameType(AccountNameType accountNameType);
	
	LeadManagementDTO saveLeadManagement(LeadManagementDTO leadManagementDTO);
	
	Optional<LeadManagementDTO> findLeadManagementByPid(String pid);
	
	LeadManagementDTO updateLeadManagement(LeadManagementDTO leadManagementDTO);
	
	List<AccountProfileDTO> findByStageTypeName(StageNameType stageTypeName);
	
	List<AccountProfile> findAllAccountProfileByCompanyId(Long companyId);

	List<AccountProfileDTO> findAllCustomByCompanyAndActivated(boolean active);

	List<AccountProfileDTO> findByCompanyIdAndUserIdInAndLastModifedDateBetweenOrderByLastModifedDateDesc(
			Long companyId, List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);

}
