package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.web.rest.dto.OrderStatusDTO;

public interface OrderStatusService {

	String PID_PREFIX = "ORDSTS-";

	/**
	 * Save a orderStatus.
	 * 
	 * @param orderStatusDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	OrderStatusDTO save(OrderStatusDTO orderStatusDTO);

	/**
	 * Update a orderStatus.
	 * 
	 * @param orderStatusDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	OrderStatusDTO update(OrderStatusDTO orderStatusDTO);

	/**
	 * Get all the orderStatuss.
	 * 
	 * @return the list of entities
	 */
	List<OrderStatusDTO> findAllByCompany();

	/**
	 * Get the "id" orderStatus.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	OrderStatusDTO findOne(Long id);

	/**
	 * Get the orderStatusDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<OrderStatusDTO> findByName(String name);

	/**
	 * Delete the "id" orderStatus.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(Long id);

	/**
	 * Get all the orderStatuss.
	 * 
	 * @return the list of entities
	 */
	List<OrderStatusDTO> findAllByDocumentType(DocumentType documentType);
}
