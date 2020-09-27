package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.UnitOfMeasure;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureProductRepository;
import com.orderfleet.webapp.repository.UnitOfMeasureRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UnitOfMeasureService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.SetTaxRate;
import com.orderfleet.webapp.web.rest.dto.UnitOfMeasureDTO;

/**
 * Service Implementation for managing UnitOfMeasure.
 * 
 * @author Muhammed Riyas T
 * @since May 17, 2016
 */
@Service
@Transactional
public class UnitOfMeasueServiceImpl implements UnitOfMeasureService {

	private final Logger log = LoggerFactory.getLogger(UnitOfMeasueServiceImpl.class);

	@Inject
	private UnitOfMeasureRepository unitOfMeasureRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UnitOfMeasureProductRepository unitOfMeasureProductRepository;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private TaxMasterRepository taxMasterRepository;

	/**
	 * Save a unitOfMeasure.
	 * 
	 * @param unitOfMeasureDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UnitOfMeasureDTO save(UnitOfMeasureDTO unitOfMeasureDTO) {
		log.debug("Request to save UnitOfMeasure : {}", unitOfMeasureDTO);
		// set pid
		unitOfMeasureDTO.setPid(UnitOfMeasureService.PID_PREFIX + RandomUtil.generatePid());
		UnitOfMeasure unitOfMeasure = unitOfMeasureDTOToUnitOfMeasure(unitOfMeasureDTO);
		// set company
		unitOfMeasure.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
		UnitOfMeasureDTO result = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
		return result;
	}

	private UnitOfMeasure unitOfMeasureDTOToUnitOfMeasure(UnitOfMeasureDTO unitOfMeasureDTO) {
		if (unitOfMeasureDTO == null) {
			return null;
		}

		UnitOfMeasure unitOfMeasure = new UnitOfMeasure();

		unitOfMeasure.setPid(unitOfMeasureDTO.getPid());
		unitOfMeasure.setName(unitOfMeasureDTO.getName());
		unitOfMeasure.setAlias(unitOfMeasureDTO.getAlias());
		unitOfMeasure.setDescription(unitOfMeasureDTO.getDescription());
		unitOfMeasure.setUomId(unitOfMeasureDTO.getUomId());

		unitOfMeasure.setActivated(unitOfMeasureDTO.getActivated());

		return unitOfMeasure;
	}

	private UnitOfMeasureDTO unitOfMeasureToUnitOfMeasureDTO(UnitOfMeasure unitOfMeasure) {
		UnitOfMeasureDTO unitOfMeasureDTO = new UnitOfMeasureDTO();

		unitOfMeasureDTO.setPid(unitOfMeasure.getPid());
		unitOfMeasureDTO.setName(unitOfMeasure.getName());
		unitOfMeasureDTO.setAlias(unitOfMeasure.getAlias());
		unitOfMeasureDTO.setDescription(unitOfMeasure.getDescription());
		unitOfMeasureDTO.setLastModifiedDate(unitOfMeasure.getLastModifiedDate());
		unitOfMeasureDTO.setActivated(unitOfMeasure.getActivated());
		unitOfMeasureDTO.setUomId(unitOfMeasure.getUomId());

		return unitOfMeasureDTO;
	}

	/**
	 * Update a unitOfMeasure.
	 * 
	 * @param unitOfMeasure the entity to update
	 * @return the persisted entity
	 */
	@Override
	public UnitOfMeasureDTO update(UnitOfMeasureDTO unitOfMeasureDTO) {
		log.debug("Request to Update UnitOfMeasure : {}", unitOfMeasureDTO);
		return unitOfMeasureRepository.findOneByPid(unitOfMeasureDTO.getPid()).map(unitOfMeasure -> {
			unitOfMeasure.setName(unitOfMeasureDTO.getName());
			unitOfMeasure.setAlias(unitOfMeasureDTO.getAlias());
			unitOfMeasure.setDescription(unitOfMeasureDTO.getDescription());
			unitOfMeasure.setUomId(unitOfMeasureDTO.getUomId());
			unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
			UnitOfMeasureDTO result = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the unitOfMeasures.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UnitOfMeasure> findAll(Pageable pageable) {
		log.debug("Request to get all UnitOfMeasures");
		Page<UnitOfMeasure> result = unitOfMeasureRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the unitOfMeasures.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<UnitOfMeasureDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all UnitOfMeasures");
//		Page<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findAllByCompanyId(pageable, true);
//		Page<UnitOfMeasureDTO> result = new PageImpl<UnitOfMeasureDTO>(
//				unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures.getContent()), pageable,
//				unitOfMeasures.getTotalElements());
		return null;
	}

	/**
	 * Get one unitOfMeasure by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public UnitOfMeasureDTO findOne(Long id) {
		log.debug("Request to get UnitOfMeasure : {}", id);
		UnitOfMeasure unitOfMeasure = unitOfMeasureRepository.findOne(id);
		UnitOfMeasureDTO unitOfMeasureDTO = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
		return unitOfMeasureDTO;
	}

	/**
	 * Get one unitOfMeasure by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UnitOfMeasureDTO> findOneByPid(String pid) {
		log.debug("Request to get UnitOfMeasure by pid : {}", pid);
		return unitOfMeasureRepository.findOneByPid(pid).map(unitOfMeasure -> {
			UnitOfMeasureDTO unitOfMeasureDTO = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
			return unitOfMeasureDTO;
		});
	}

	/**
	 * Get one unitOfMeasure by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UnitOfMeasureDTO> findByName(String name) {
		log.debug("Request to get UnitOfMeasure by name : {}", name);
		return unitOfMeasureRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(unitOfMeasure -> {
					UnitOfMeasureDTO unitOfMeasureDTO = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
					return unitOfMeasureDTO;
				});
	}

	/**
	 * Delete the unitOfMeasure by id.
	 * 
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete UnitOfMeasure : {}", pid);
		unitOfMeasureRepository.findOneByPid(pid).ifPresent(unitOfMeasure -> {
			unitOfMeasureProductRepository.deleteByUnitOfMeasurePid(pid);

			unitOfMeasureRepository.delete(unitOfMeasure.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<UnitOfMeasureDTO> findAllByCompany() {
		log.debug("Request to get all UnitOfMeasure");
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findAllByCompanyId(true);
		List<UnitOfMeasureDTO> result = unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures);
		return result;
	}

	private List<UnitOfMeasureDTO> unitOfMeasuresToUnitOfMeasureDTOs(List<UnitOfMeasure> unitOfMeasures) {
		if (unitOfMeasures == null) {
			return null;
		}

		List<UnitOfMeasureDTO> list = new ArrayList<UnitOfMeasureDTO>();
		for (UnitOfMeasure unitOfMeasure : unitOfMeasures) {
			list.add(unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure));
		}

		return list;
	}

	/**
	 * Update the unitOfMeasure status by pid.
	 * 
	 * @param pid    the pid of the entity
	 * @param active the active of the entity
	 * @return the entity
	 */
	@Override
	public UnitOfMeasureDTO updateUnitOfMeasureStatus(String pid, boolean active) {
		log.debug("Request to Update UnitOfMeasure activate or deactivate: {}", pid);
		return unitOfMeasureRepository.findOneByPid(pid).map(unitOfMeasure -> {
			if (!active) {
				unitOfMeasureProductRepository.deleteByUnitOfMeasurePid(pid);

			}
			unitOfMeasure.setActivated(active);
			unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
			UnitOfMeasureDTO res = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
			return res;
		}).orElse(null);

	}

	@Override
	public List<UnitOfMeasureDTO> findAllByCompanyOrderByName() {
		log.debug("Request to get UnitOfMeasure by company: {}");
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findAllByCompanyOrderByName();
		List<UnitOfMeasureDTO> unitOfMeasureDTOs = unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures);
		return unitOfMeasureDTOs;
	}

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
	@Override
	public Page<UnitOfMeasureDTO> findAllByCompanyAndActivatedUnitOfMeasureOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get activated UnitOfMeasure ");
		Page<UnitOfMeasure> pageUnitOfMeasures = unitOfMeasureRepository
				.findAllByCompanyIdAndActivatedUnitOfMeasureOrderByName(active, pageable);
		Page<UnitOfMeasureDTO> pageUnitOfMeasureDTO = new PageImpl<UnitOfMeasureDTO>(
				unitOfMeasuresToUnitOfMeasureDTOs(pageUnitOfMeasures.getContent()), pageable,
				pageUnitOfMeasures.getTotalElements());
		return pageUnitOfMeasureDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<UnitOfMeasureDTO> findAllByCompanyAndDeactivatedUnitOfMeasure(boolean deactive) {
		log.debug("Request to get deactivated UnitOfMeasure ");
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository
				.findAllByCompanyIdAndDeactivatedUnitOfMeasure(deactive);
		List<UnitOfMeasureDTO> unitOfMeasureDTOs = unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures);
		return unitOfMeasureDTOs;
	}

	/**
	 * Get one unitOfMeasure by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<UnitOfMeasureDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get UnitOfMeasure by name : {}", name);
		return unitOfMeasureRepository.findByCompanyIdAndNameIgnoreCase(companyId, name).map(unitOfMeasure -> {
			UnitOfMeasureDTO unitOfMeasureDTO = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
			return unitOfMeasureDTO;
		});
	}

	/**
	 * Save a unitOfMeasure.
	 * 
	 * @param unitOfMeasureDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public UnitOfMeasureDTO saveUnitOfMeasure(Long companyId, UnitOfMeasureDTO unitOfMeasureDTO) {
		log.debug("Request to save UnitOfMeasure : {}", unitOfMeasureDTO);
		// set pid
		unitOfMeasureDTO.setPid(UnitOfMeasureService.PID_PREFIX + RandomUtil.generatePid());
		UnitOfMeasure unitOfMeasure = unitOfMeasureDTOToUnitOfMeasure(unitOfMeasureDTO);
		// set company
		unitOfMeasure.setCompany(companyRepository.findOne(companyId));
		unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
		UnitOfMeasureDTO result = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
		return result;
	}

	@Override
	public UnitOfMeasureDTO updateUnitOfMeasureThirdpartyUpdate(String pid, boolean Thirdparty) {

		return unitOfMeasureRepository.findOneByPid(pid).map(unitOfMeasure -> {
			System.out.println(Thirdparty);
			unitOfMeasure.setThirdpartyUpdate(Thirdparty);
			unitOfMeasure = unitOfMeasureRepository.save(unitOfMeasure);
			System.out.println(unitOfMeasure);
			UnitOfMeasureDTO unitOfMeasureDTO = unitOfMeasureToUnitOfMeasureDTO(unitOfMeasure);
			return unitOfMeasureDTO;
		}).orElse(null);
	}

	@Override
	public List<UnitOfMeasureDTO> findAllUnitOfMeasureByCompanyOrderByName() {
		log.debug("Request to get UnitOfMeasure by company: {}");
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findAllByCompanyOrderByName();
		List<UnitOfMeasureDTO> unitOfMeasureDTOs = unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures);
		return unitOfMeasureDTOs;
	}

	@Override
	public List<UnitOfMeasureDTO> findAllUnitOfMeasureByCompanyPid(String companyPid) {
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository.findAllByCompanyPidOrderByProductId(companyPid);
		List<UnitOfMeasureDTO> unitOfMeasureDTOs = unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures);
		return unitOfMeasureDTOs;
	}

	@Override
	public List<UnitOfMeasureDTO> findAllUnitOfMeasureByCompanyPidAndUnitOfMeasurePid(String companyPid,
			List<String> groupPids) {
		List<UnitOfMeasure> unitOfMeasures = unitOfMeasureRepository
				.findAllByCompanyPidAndUnitOfMeasurePidIn(companyPid, groupPids);
		List<UnitOfMeasureDTO> unitOfMeasureDTOs = unitOfMeasuresToUnitOfMeasureDTOs(unitOfMeasures);
		return unitOfMeasureDTOs;
	}

}