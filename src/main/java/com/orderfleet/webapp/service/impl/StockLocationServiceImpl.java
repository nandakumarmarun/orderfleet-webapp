package com.orderfleet.webapp.service.impl;

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

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StockLocationService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.StockLocationDTO;
import com.orderfleet.webapp.web.rest.mapper.StockLocationMapper;

/**
 * Service Implementation for managing StockLocation.
 *
 * @author Muhammed Riyas T
 * @since July 15, 2016
 */
@Service
@Transactional
public class StockLocationServiceImpl implements StockLocationService {

	private final Logger log = LoggerFactory.getLogger(StockLocationServiceImpl.class);

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private StockLocationMapper stockLocationMapper;

	/**
	 * Save a stockLocation.
	 *
	 * @param stockLocationDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public StockLocationDTO save(StockLocationDTO stockLocationDTO) {
		log.debug("Request to save StockLocation : {}", stockLocationDTO);
		stockLocationDTO.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid()); // set
		// pid
		StockLocation stockLocation = stockLocationMapper.stockLocationDTOToStockLocation(stockLocationDTO);
		stockLocation.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		stockLocation = stockLocationRepository.save(stockLocation);
		StockLocationDTO result = stockLocationMapper.stockLocationToStockLocationDTO(stockLocation);
		return result;
	}
	
	@Override
	public StockLocationDTO save(StockLocationDTO stockLocationDTO, Company company) {
		log.debug("Request to save StockLocation : {}", stockLocationDTO);
		stockLocationDTO.setPid(StockLocationService.PID_PREFIX + RandomUtil.generatePid()); // set
		// pid
		StockLocation stockLocation = stockLocationMapper.stockLocationDTOToStockLocation(stockLocationDTO);
		stockLocation.setCompany(company);
		stockLocation = stockLocationRepository.save(stockLocation);
		StockLocationDTO result = stockLocationMapper.stockLocationToStockLocationDTO(stockLocation);
		return result;
	}

	/**
	 * Update a stockLocation.
	 *
	 * @param stockLocationDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public StockLocationDTO update(StockLocationDTO stockLocationDTO) {
		log.debug("Request to Update StockLocation : {}", stockLocationDTO);

		return stockLocationRepository.findOneByPid(stockLocationDTO.getPid()).map(stockLocation -> {
			stockLocation.setName(stockLocationDTO.getName());
			stockLocation.setAlias(stockLocationDTO.getAlias());
			stockLocation.setDescription(stockLocationDTO.getDescription());
			stockLocation.setStockLocationType(stockLocationDTO.getStockLocationType());
			stockLocation = stockLocationRepository.save(stockLocation);
			StockLocationDTO result = stockLocationMapper.stockLocationToStockLocationDTO(stockLocation);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StockLocationDTO> findAllByCompany() {
		log.debug("Request to get all StockLocations");
		List<StockLocation> stockLocationList = stockLocationRepository.findAllByCompanyId();
		List<StockLocationDTO> result = stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocationList);
		return result;
	}

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StockLocationDTO> findAllActualByCompanyId() {
		log.debug("Request to get all StockLocations");
		List<StockLocation> stockLocationList = stockLocationRepository.findAllActualByCompanyId();
		List<StockLocationDTO> result = stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocationList);
		return result;
	}

	/**
	 * Get all the stockLocations.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<StockLocationDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all StockLocations");
		Page<StockLocation> stockLocations = stockLocationRepository
				.findAllByCompanyIdOrderByStockLocationName(pageable);
		Page<StockLocationDTO> result = new PageImpl<StockLocationDTO>(
				stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocations.getContent()), pageable,
				stockLocations.getTotalElements());
		return result;
	}

	/**
	 * Get one stockLocation by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public StockLocationDTO findOne(Long id) {
		log.debug("Request to get StockLocation : {}", id);
		StockLocation stockLocation = stockLocationRepository.findOne(id);
		StockLocationDTO stockLocationDTO = stockLocationMapper.stockLocationToStockLocationDTO(stockLocation);
		return stockLocationDTO;
	}

	/**
	 * Get one stockLocation by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<StockLocationDTO> findOneByPid(String pid) {
		log.debug("Request to get StockLocation by pid : {}", pid);
		return stockLocationRepository.findOneByPid(pid).map(stockLocation -> {
			StockLocationDTO stockLocationDTO = stockLocationMapper.stockLocationToStockLocationDTO(stockLocation);
			return stockLocationDTO;
		});
	}

	/**
	 * Get one stockLocation by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<StockLocationDTO> findByName(String name) {
		log.debug("Request to get StockLocation by name : {}", name);
		return stockLocationRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(stockLocation -> {
					StockLocationDTO stockLocationDTO = stockLocationMapper
							.stockLocationToStockLocationDTO(stockLocation);
					return stockLocationDTO;
				});
	}

	/**
	 * Delete the stockLocation by id.
	 *
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete StockLocation : {}", pid);
		stockLocationRepository.findOneByPid(pid).ifPresent(stockLocation -> {
			stockLocationRepository.delete(stockLocation.getId());
		});
	}

	/**
	 * @author Fahad
	 * @since Feb 7, 2017
	 *
	 *        Update the StockLocation status by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public StockLocationDTO updateStockLocationStatus(String pid, boolean activate) {
		log.debug("Request to update StockLocation status");
		return stockLocationRepository.findOneByPid(pid).map(stockLocation -> {
			stockLocation.setActivated(activate);
			stockLocation = stockLocationRepository.save(stockLocation);
			StockLocationDTO result = stockLocationMapper.stockLocationToStockLocationDTO(stockLocation);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 *
	 *        find all active company
	 *
	 * @param active
	 *            the active of the entity
	 *
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	@Override
	public Page<StockLocationDTO> findAllByCompanyAndActivatedStockLocationOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get Activated StockLocation ");
		Page<StockLocation> pageStockLocation = stockLocationRepository
				.findAllByCompanyIdAndActivatedStockLocationOrderByName(pageable, active);
		Page<StockLocationDTO> pageStockLocationDTO = new PageImpl<StockLocationDTO>(
				stockLocationMapper.stockLocationsToStockLocationDTOs(pageStockLocation.getContent()), pageable,
				pageStockLocation.getTotalElements());
		return pageStockLocationDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 15, 2017
	 *
	 *        find all deactive company
	 *
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<StockLocationDTO> findAllByCompanyAndDeactivatedStockLocation(boolean deactive) {
		log.debug("Request to get Deactivated StockLocation ");
		List<StockLocation> stockLocations = stockLocationRepository
				.findAllByCompanyIdAndDeactivatedStockLocation(deactive);
		List<StockLocationDTO> stockLocationDTOs = stockLocationMapper
				.stockLocationsToStockLocationDTOs(stockLocations);
		return stockLocationDTOs;
	}

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StockLocationDTO> findAllByCompanyId(Long companyId) {
		log.debug("Request to get all StockLocations");
		List<StockLocation> stockLocationList = stockLocationRepository.findAllByCompanyId(companyId);
		List<StockLocationDTO> result = stockLocationMapper.stockLocationsToStockLocationDTOs(stockLocationList);
		return result;
	}

	/**
	 * Get one stockLocation by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<StockLocationDTO> findByCompanyIdAndName(Long companyId, String name) {
		log.debug("Request to get StockLocation by name : {}", name);
		return stockLocationRepository.findByCompanyIdAndNameIgnoreCase(companyId, name)
				.map(stockLocation -> {
					StockLocationDTO stockLocationDTO = stockLocationMapper
							.stockLocationToStockLocationDTO(stockLocation);
					return stockLocationDTO;
				});
	}


	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public StockLocationDTO findFirstByCompanyId(Long companyId) {
		log.debug("Request to get all StockLocations");
		StockLocation stockLocationList = stockLocationRepository.findFirstByCompanyId(companyId);
		StockLocationDTO result = stockLocationMapper.stockLocationToStockLocationDTO(stockLocationList);
		return result;
	}

	/**
	 * Get all the stockLocations.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StockLocation> findAllStockLocationByCompanyId(Long companyId) {
		log.debug("Request to get all StockLocations");
		List<StockLocation> stockLocationList = stockLocationRepository.findAllByCompanyId(companyId);
		return stockLocationList;
	}
}
