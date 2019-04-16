package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Service Interface for managing ExecutiveTaskExecution.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
public interface ExecutiveTaskExecutionService {

	String PID_PREFIX = "ETASKE-";

	/**
	 * Save a executiveTaskExecution.
	 * 
	 * @param executiveTaskExecutionDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ExecutiveTaskExecutionDTO save(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO);

	void changeActivityStatus(String pid, ActivityStatus activityStatus, String reason);

	/**
	 * Get all the executiveTaskExecutions.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskExecution> findAll(Pageable pageable);

	/**
	 * Get all the executiveTaskExecutions of a company.
	 * 
	 * @return the list of entities
	 */
	List<ExecutiveTaskExecutionDTO> findAllByCompany();

	/**
	 * Get all the executiveTaskExecutions of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ExecutiveTaskExecutionDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" executiveTaskExecution.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ExecutiveTaskExecutionDTO findOne(Long id);

	/**
	 * Get the executiveTaskExecution by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ExecutiveTaskExecutionDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" executiveTaskExecution.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	Long countByUserIdAndActivityIdAndDateBetweenAndActivityStatusNot(Long userId, Long activityId,
			ActivityStatus activityStatus, LocalDateTime startDate, LocalDateTime endDate);

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdUserPidActivityPidAndDateBetween(String userPid,
			String activityPid, LocalDateTime fromDate, LocalDateTime toDate);

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdUserPidAndDateBetweenOrderByDateAsc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdActivityPidAndDateBetween(String activityPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	void updateExecutiveTaskExecutionStatus(ExecutiveTaskExecutionDTO executiveTaskExecutionDTO);

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdAndStatusFalse();

	List<ExecutiveTaskExecutionDTO> findAllByCompanyIdActivityPidAndAccountProfilePid(String activityPid,
			String accountPid);

	Optional<ExecutiveTaskExecutionDTO> findAllByCompanyIdAndClientTransactionKey(String clientTransactionKey);

	Map<String, String> findAccountFromForm(String formPid, String dynamicDocumentPid, String exePid);

	List<UserDTO> findAllUniqueUsersFromExecutiveTaskExecution(String companyPid);

	List<UserDTO> getCountUniqueUsersFromExecutiveTaskExecutionAndCreateDateBetween(String companyPid,
			LocalDateTime monthStartDate, LocalDateTime monthEndDate);

	Long countAndUserNameByCompanyPidAndLocationInAndDateBetweenAndLocationType(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate,LocationType locationType);

	List<ExecutiveTaskExecutionDTO> getByCompanyPidAndLocationInAndDateBetween(List<String> locations, String companyPid,
			LocalDateTime fromDate, LocalDateTime toDate);
	

	List<ExecutiveTaskExecutionDTO> getByCompanyPidAndLocationInAndDateBetweenAndLocationType(List<String> locations,
			String companyPid, LocalDateTime fromDate, LocalDateTime toDate, LocationType locationType);

}
