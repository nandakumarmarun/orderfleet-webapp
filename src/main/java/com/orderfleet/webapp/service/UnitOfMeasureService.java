package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureDTO;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;

/**
 * Service Interface for managing UnitOfMeasure.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
public interface UnitOfMeasureService {

	String PID_PREFIX = "UOM-";

	/**
	 * Save a unitOfMeasure.
	 * 
	 * @param unitOfMeasureDTO the entity to save
	 * @return the persisted entity
	 */
	UnitOfMeasureDTO save(UnitOfMeasureDTO unitOfMeasureDTO);

	/**
	 * Update a unitOfMeasure.
	 * 
	 * @param unitOfMeasureDTO the entity to update
	 * @return the persisted entity
	 */
	UnitOfMeasureDTO update(UnitOfMeasureDTO unitOfMeasureDTO);

	/**
	 * Get all the unitOfMeasures.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<UnitOfMeasure> findAll(Pageable pageable);

	/**
	 * Get all the unitOfMeasures of a company.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	Page<UnitOfMeasureDTO> findAllByCompany(Pageable pageable);

	/**
	 * Get the "id" unitOfMeasure.
	 * 
	 * @param id the id of the entity
	 * @return the entity
	 */
	UnitOfMeasureDTO findOne(Long id);

	/**
	 * Get the unitOfMeasure by "pid".
	 * 
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	Optional<UnitOfMeasureDTO> findOneByPid(String pid);

	/**
	 * Get the unitOfMeasureDTO by "name".
	 * 
	 * @param name the name of the entity
	 * @return the entity
	 */
	Optional<UnitOfMeasureDTO> findByName(String name);

	/**
	 * Delete the "id" unitOfMeasure.
	 * 
	 * @param id the id of the entity
	 */
	void delete(String pid);

	/**
	 * Get all the unitOfMeasure of a company.
	 * 
	 * @return the list of entities
	 */
	List<UnitOfMeasureDTO> findAllByCompany();

	/**
	 * Update the unitOfMeasure status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	UnitOfMeasureDTO updateUnitOfMeasureStatus(String pid, boolean active);

	List<UnitOfMeasureDTO> findAllByCompanyOrderByName();

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active   the active of the entity
	 * 
	 * @param pageable the pageable of the entity
	 * @return the entity
	 */
	Page<UnitOfMeasureDTO> findAllByCompanyAndActivatedUnitOfMeasureOrderByName(Pageable pageable, boolean active);

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive the deactive of the entity
	 * @return the list
	 */
	List<UnitOfMeasureDTO> findAllByCompanyAndDeactivatedUnitOfMeasure(boolean deactive);

	Optional<UnitOfMeasureDTO> findByCompanyIdAndName(Long companyId, String name);

	UnitOfMeasureDTO saveUnitOfMeasure(Long companyId, UnitOfMeasureDTO unitOfMeasureDTO);

	UnitOfMeasureDTO updateUnitOfMeasureThirdpartyUpdate(String pid, boolean Thirdparty);

	List<UnitOfMeasureDTO> findAllUnitOfMeasureByCompanyOrderByName();

	List<UnitOfMeasureDTO> findAllUnitOfMeasureByCompanyPid(String companyPid);

	List<UnitOfMeasureDTO> findAllUnitOfMeasureByCompanyPidAndUnitOfMeasurePid(String companyPid, List<String> groupPids);
}
