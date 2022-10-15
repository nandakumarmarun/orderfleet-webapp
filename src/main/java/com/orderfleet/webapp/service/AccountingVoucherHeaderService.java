package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;

/**
 * Service Interface for managing AccountingVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since July 28, 2016
 */
public interface AccountingVoucherHeaderService {

	String PID_PREFIX = "ACCVH-";

	/**
	 * Save a accountingVoucherHeader.
	 * 
	 * @param accountingVoucherHeaderDTO the entity to save
	 * @return the persisted entity
	 */
	AccountingVoucherHeaderDTO save(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO);

	/**
	 * Get all the accountingVoucherHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<AccountingVoucherHeader> findAll(Pageable pageable);

	/**
	 * Get all the accountingVoucherHeaders.
	 * 
	 * @return the list of entities
	 */
	List<AccountingVoucherHeaderDTO> findAllByCompany();

	/**
	 * Get all the accountingVoucherHeaders of a company.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<AccountingVoucherHeaderDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" accountingVoucherHeader.
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */
	AccountingVoucherHeaderDTO findOne(Long id);

	/**
	 * Get the accountingVoucherHeader by "pid".
	 * 
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	Optional<AccountingVoucherHeaderDTO> findOneByPid(String pid);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetween(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetween(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	void updateAccountingVoucherHeaderStatus(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO);

	List<AccountingVoucherHeaderDTO> findAllByExecutiveTaskExecutionPid(String accountingVoucherHeaderPid);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdDocumentPidAndDateBetween(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(String userPid,
			String accountPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetween(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	List<AccountingVoucherHeaderDTO> findAllByCompanyIdAccountPidDocumentPidAndDateBetween(String accountPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate);

	AccountingVoucherHeaderDTO findAllByCompanyIdAccountPidDocumentPidAndExecutiveTaskExecutionIn(String accountPid,
			String documentPid, List<ExecutiveTaskExecutionDTO> executiveTaskExecutionDTOs);

	Set<Document> findDocumentsByUserIdIn(List<Long> userIds);

	public List<AccountingVoucherHeaderDTO> getAllByCompanyIdUserPidAccountPidDocumentPidAndDateBetween(
			List<String> userPids, String accountPid, List<String> documentPid,
			List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate);

	public List<AccountingVoucherHeaderDTO> getAllByCompanyIdUserPidDocumentPidAndDateBetween(List<String> userPids,
			List<String> documentPid, List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate,
			LocalDateTime toDate);

	void updateAccountingVoucherHeaderSalesManagementStatus(AccountingVoucherHeaderDTO accountingVoucherHeaderDTO);

	/**
	 * This method working to update Recipt amount
	 * 
	 * @pram New Amount
	 * 
	 * @pram Accounting voucher details id
	 * 
	 * @param Accounting voucher Header Pid
	 * 
	 * @author Nandakumar M
	 * 
	 * @since September 29, 2022
	 */
	void updateAccountingVoucherAmount(double amount, long detailid, String pid);

}
