package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.ProductGroupInfoSection;
import com.orderfleet.webapp.web.ecom.dto.ProductGroupInfoSectionDTO;

/**
 * A DTO for the ProductGroupInfoSection entity.
 * 
 * @author Muhammed Riyas T
 * @since Sep 21, 2016
 */
public interface ProductGroupInfoSectionService {

	String PID_PREFIX = "PGIS-";

	/**
	 * Save a productGroupInfoSection.
	 * 
	 * @param productGroupInfoSectionDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	ProductGroupInfoSectionDTO save(ProductGroupInfoSectionDTO productGroupInfoSectionDTO);

	/**
	 * Update a productGroupInfoSection.
	 * 
	 * @param productGroupInfoSectionDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	ProductGroupInfoSectionDTO update(ProductGroupInfoSectionDTO productGroupInfoSectionDTO);

	/**
	 * Get all the productGroupInfoSections.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductGroupInfoSection> findAll(Pageable pageable);

	/**
	 * Get all the productGroupInfoSections.
	 * 
	 * @return the list of entities
	 */
	List<ProductGroupInfoSectionDTO> findAllByCompany();

	/**
	 * Get all the productGroupInfoSections.
	 * 
	 * @param companyPid
	 * @return the list of entities
	 */
	List<ProductGroupInfoSectionDTO> findAllByCompanyWithRichText();

	/**
	 * Get all the productGroupInfoSections of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<ProductGroupInfoSectionDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" productGroupInfoSection.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	ProductGroupInfoSectionDTO findOne(Long id);

	/**
	 * Get the productGroupInfoSection by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<ProductGroupInfoSectionDTO> findOneByPid(String pid);

	/**
	 * Get the productGroupInfoSectionDTO by "name".
	 * 
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	Optional<ProductGroupInfoSectionDTO> findByName(String name);

	/**
	 * Delete the "id" productGroupInfoSection.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

}
