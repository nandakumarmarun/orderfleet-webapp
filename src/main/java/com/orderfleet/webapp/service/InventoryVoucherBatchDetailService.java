package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.InventoryVoucherBatchDetailDTO;

/**
 * 
 * Service Interface for the InventoryVoucherBatchDetail entity.
 *
 * @author Sarath
 * @since Dec 7, 2016
 */
public interface InventoryVoucherBatchDetailService {

	/**
	 * Save a inventoryVoucherBatchDetail.
	 * 
	 * @param inventoryVoucherBatchDetailDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	InventoryVoucherBatchDetailDTO save(InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO);

	/**
	 * Update a inventoryVoucherBatchDetail.
	 * 
	 * @param inventoryVoucherBatchDetailDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	InventoryVoucherBatchDetailDTO update(InventoryVoucherBatchDetailDTO inventoryVoucherBatchDetailDTO);

	/**
	 * Get all the inventoryVoucherBatchDetails.
	 * 
	 * @return the list of entities
	 */
	List<InventoryVoucherBatchDetailDTO> findAllByCompany();

	/**
	 * Get all the inventoryVoucherBatchDetails of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<InventoryVoucherBatchDetailDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" inventoryVoucherBatchDetail.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	InventoryVoucherBatchDetailDTO findOne(Long id);

	/**
	 * Get the inventoryVoucherBatchDetail by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<InventoryVoucherBatchDetailDTO> findOneByPid(String pid);
}
