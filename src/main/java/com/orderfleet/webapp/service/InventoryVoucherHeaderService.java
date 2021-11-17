package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.web.rest.api.dto.LastSellingDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.StockDetailsDTO;

/**
 * Service Interface for managing InventoryVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since July 19, 2016
 */
public interface InventoryVoucherHeaderService {

	String PID_PREFIX = "INVH-";

	/**
	 * Save a inventoryVoucherHeader.
	 * 
	 * @param inventoryVoucherHeaderDTO the entity to save
	 * @return the persisted entity
	 */
	InventoryVoucherHeaderDTO save(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<InventoryVoucherHeader> findAll(Pageable pageable);

	/**
	 * Get all the inventoryVoucherHeaders.
	 * 
	 * @return the list of entities
	 */
	List<InventoryVoucherHeaderDTO> findAllByCompany();

	/**
	 * Get all the inventoryVoucherHeaders of a company.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<InventoryVoucherHeaderDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" inventoryVoucherHeader.
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */
	InventoryVoucherHeaderDTO findOne(Long id);

	/**
	 * Get the inventoryVoucherHeader by "pid".
	 * 
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	Optional<InventoryVoucherHeaderDTO> findOneByPid(String pid);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetween(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetween(String userPid, String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdLocationPidAndDateBetween(String locationPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidLocationPidAndDateBetween(String userPid,
			String locationPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	void updateInventoryVoucherHeaderStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderSalesManagementStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	List<InventoryVoucherHeaderDTO> findAllByExecutiveTaskExecutionPid(String executiveTaskExecutionPid);

	Set<Document> findDocumentsByUserIdIn(List<Long> userIds);

	// filter for status

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndDateBetweenAndStatus(String status, LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAndDateBetweenAndStatus(String status, String userPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdAccountPidAndDateBetweenAndStatus(String status,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdUserPidAccountPidAndDateBetweenAndStatus(String status,
			String userPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	List<InventoryVoucherHeaderDTO> findAllByDocumentPidAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, String documentPid);

	List<InventoryVoucherHeaderDTO> findAllByDocumentPidInAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	void updateProcessStatus(String inventoryVoucherHeaderPid, String status);

	List<InventoryVoucherHeaderDTO> findAllByCompanyIdAndInventoryPidIn(List<String> inventoryPids);

	void updateInventoryVoucherHeadersStatus(List<InventoryVoucherHeaderDTO> inventoryVoucherHeaders);

	List<Object[]> findByCompanyIdAndInventoryPidIn(List<String> inventoryPids);

	List<StockDetailsDTO> findAllStockDetails(Long companyId, Long userId, LocalDateTime fromDate, LocalDateTime toDate,
			Set<StockLocation> stockLocation);

	InventoryVoucherHeader updateInventoryVoucherHeader(InventoryVoucherHeaderDTO inventoryVoucherHeaderDto);

	void updateInventoryVoucherHeaderProcessFlowStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderPaymentReceived(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderBookingId(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderRemarks(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderRejectedStatus(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderDeliveryDate(InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	void updateInventoryVoucherHeaderBookingIdDeliveryDateAndPaymentReceived(
			InventoryVoucherHeaderDTO inventoryVoucherHeaderDTO);

	

	List<LastSellingDetailsDTO> findHeaderByAccountPidUserPidandDocPid(String accountPid, String userPid, String documentPid,
			String productPid);

}
