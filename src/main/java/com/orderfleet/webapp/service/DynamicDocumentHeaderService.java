package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;

/**
 * Service Interface for managing DynamicDocumentHeader.
 * 
 * @author Muhammed Riyas T
 * @since August 09, 2016
 */
public interface DynamicDocumentHeaderService {

	String PID_PREFIX = "DYDH-";

	/**
	 * Save a dynamicDocumentHeader.
	 * 
	 * @param dynamicDocumentHeaderDTO the entity to save
	 * @return the persisted entity
	 */
	DynamicDocumentHeaderDTO save(DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO);

	void update(DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO);

	/**
	 * Get all the dynamicDocumentHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<DynamicDocumentHeader> findAll(Pageable pageable);

	/**
	 * Get all the dynamicDocumentHeaders.
	 * 
	 * @return the list of entities
	 */
	List<DynamicDocumentHeaderDTO> findAllByCompany();

	/**
	 * Get all the dynamicDocumentHeaders of a company.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<DynamicDocumentHeaderDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" dynamicDocumentHeader.
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */
	DynamicDocumentHeaderDTO findOne(Long id);

	/**
	 * Get the dynamicDocumentHeader by "pid".
	 * 
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	Optional<DynamicDocumentHeaderDTO> findOneByPid(String pid);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetween(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdDocumentPidAndDateBetween(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdAndDocumentNameStatusFalseOrderByCreatedDateDesc(
			String documentName);

	void updateDynamicDocumentStatus(DynamicDocumentHeaderDTO executiveTaskExecutionDTO);

	DynamicDocumentHeaderDTO findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
			String executiveTaskExecutionPid, String documentName);

	Set<Document> findDocumentsByUserIdIn(List<Long> userIds);

	List<DynamicDocumentHeaderDTO> findByFilledFormsIn(List<FilledForm> filledForms);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetweenSetFilledForm(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllDynamicDocumentByExecutiveTaskExecutionPid(String pid);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndTallyDownloadStatusAndDateBetween(
			String userPid, String documentPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetween(String userPid,
			List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate);

	List<DynamicDocumentHeaderDTO> findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetween(String documentPid,
			List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate);

	void updateDynamicDocumentHeaderStatus(DynamicDocumentHeaderDTO dynamicDocumentDTO);
}
