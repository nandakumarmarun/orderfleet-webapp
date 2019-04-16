package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.dto.ProductProfileDTO;

/**
 * Service Interface for managing PriceLevelList.
 * 
 * @author Muhammed Riyas T
 * @since August 22, 2016
 */
public interface PriceLevelListService {

	String PID_PREFIX = "PCLL-";

	/**
	 * Save a priceLevelList.
	 * 
	 * @param priceLevelListDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	PriceLevelListDTO save(PriceLevelListDTO priceLevelListDTO);

	/**
	 * Update a priceLevelList.
	 * 
	 * @param priceLevelListDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	PriceLevelListDTO update(PriceLevelListDTO priceLevelListDTO);

	/**
	 * Get all the priceLevelLists.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceLevelList> findAll(Pageable pageable);

	/**
	 * Get all the priceLevelLists.
	 * 
	 * @return the list of entities
	 */
	List<PriceLevelListDTO> findAllByCompany();

	/**
	 * Get all the priceLevelLists of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<PriceLevelListDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" priceLevelList.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	PriceLevelListDTO findOne(Long id);

	/**
	 * Get the priceLevelList by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<PriceLevelListDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" priceLevelList.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<PriceLevelListDTO> findAllByCompanyIdPriceLevelPidAndProductProfilePid(String priceLevelPid,
			String productProfilePid);

	List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPid(String priceLevelPid);

	List<PriceLevelListDTO> findAllByCompanyIdAndProductProfilePid(String productProfilePid);

	Optional<PriceLevelListDTO> findAllByCompanyIdPriceLevelNameAndProductProfileName(Long companyId,
			String priceLevelName, String productProfileName);

	PriceLevelListDTO savePriceLevelList(PriceLevelListDTO priceLevelListDTO, Long companyId);

	List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndLastModifiedDate(String priceLevelPid,
			LocalDateTime lastModifiedDate);

	List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndpriceLevelLastModifiedDate(String priceLevelPid,
			LocalDateTime lastModifiedDate);

	List<ProductProfileDTO> findProductProfileByPriceLevel(String priceLevel);

	Optional<PriceLevelListDTO> findByCompanyIdAndPriceLevelPidAndProductProfilePidOrderByProductProfileNameDesc(Long companyId,String priceLevelPid,String productPid);

	List<PriceLevelListDTO> findAllByCompanyIdAndProductCategoryPid(String productCategoryPid);
	
	List<PriceLevelListDTO> findAllByCompanyIdAndProductProfilePidAndProductCategoryPid(String productProfilePid,String productCategoryPid);
	
	List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndProductCategoryPid(String priceLevelPid,String productCategoryPid);
	
	List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndProductProfilePidAndProductCategoryPid(String priceLevelPid,String productProfilePid,String productCategoryPid);
	
	List<PriceLevelListDTO> findAllByCompanyIdAndProductProfilePidIn(List<String> productPids);
	
	List<PriceLevelListDTO> findAllByCompanyIdAndPriceLevelPidAndProductProfilePidIn(String priceLevelPid,List<String> productPids);
}
