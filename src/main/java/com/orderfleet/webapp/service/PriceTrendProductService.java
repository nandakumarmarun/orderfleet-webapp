package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.PriceTrendProduct;
import com.orderfleet.webapp.web.rest.dto.PriceTrendProductDTO;

/**
 * Service Interface for managing PriceTrendProduct.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface PriceTrendProductService {

	String PID_PREFIX = "PTPR-";

	/**
	 * Save a priceTrendProduct.
	 * 
	 * @param priceTrendProductDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PriceTrendProductDTO save(PriceTrendProductDTO priceTrendProductDTO);

	/**
	 * Update a priceTrendProduct.
	 * 
	 * @param priceTrendProductDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PriceTrendProductDTO update(PriceTrendProductDTO priceTrendProductDTO);

	/**
	 * Get all the priceTrendProducts.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceTrendProduct> findAll(Pageable pageable);

	/**
	 * Get all the priceTrendProducts.
	 * 
	 * @return the list of entities
	 */
	List<PriceTrendProductDTO> findAllByCompany();

	/**
	 * Get all the priceTrendProducts of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceTrendProductDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" priceTrendProduct.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	PriceTrendProductDTO findOne(Long id);

	/**
	 * Get the priceTrendProduct by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PriceTrendProductDTO> findOneByPid(String pid);

	/**
	 * Get the priceTrendProductDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<PriceTrendProductDTO> findByName(String name);

	/**
	 * Delete the "id" priceTrendProduct.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);
	
	/**
	 * Update  the PriceTrendProduct status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	PriceTrendProductDTO updatePriceTrendProductStatus(String pid,boolean active);
	
	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all priceTrendProductDTO from PriceTrendProduct by status and company.
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the list of entity
	 */
	List<PriceTrendProductDTO> findAllByCompanyIdAndPriceTrendProductActivated(boolean active);
	
	/**
	 *  @author Fahad
	 * @since Feb 11, 2017
	 * 
	 * find all active company 
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 *@param active
	 *            the active of the entity
	 * @return the entity
	 */
	Page<PriceTrendProductDTO> findAllByCompanyAndPriceTrendProductActivatedOrderByName(Pageable pageable,boolean active);

	List<PriceTrendProductDTO> findAllByCompanyIdAndPriceTrendProductActivatedAndLastModifiedDate(boolean active,
			LocalDateTime lastModifiedDate);
	
}
