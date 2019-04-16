package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
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

import com.orderfleet.webapp.domain.OpeningStock;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.OpeningStockRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.StockLocationRepository;
import com.orderfleet.webapp.repository.UserStockLocationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.OpeningStockService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.OpeningStockDTO;
import com.orderfleet.webapp.web.rest.mapper.OpeningStockMapper;

/**
 * Service Implementation for managing OpeningStock.
 * 
 * @author Muhammed Riyas T
 * @since July 16, 2016
 */
@Service
@Transactional
public class OpeningStockServiceImpl implements OpeningStockService {

	private final Logger log = LoggerFactory.getLogger(OpeningStockServiceImpl.class);

	@Inject
	private OpeningStockRepository openingStockRepository;

	@Inject
	private OpeningStockMapper openingStockMapper;

	@Inject
	private ProductProfileRepository productProfileRepository;

	@Inject
	private StockLocationRepository stockLocationRepository;

	@Inject
	private UserStockLocationRepository userStockLocationRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a openingStock.
	 * 
	 * @param openingStockDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public OpeningStockDTO save(OpeningStockDTO openingStockDTO) {
		log.debug("Request to save EmployeeProfile : {}", openingStockDTO);
		openingStockDTO.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
		openingStockDTO.setOpeningStockDate(LocalDateTime.now());
		openingStockDTO.setCreatedDate(LocalDateTime.now());
		OpeningStock openingStock = openingStockMapper.openingStockDTOToOpeningStock(openingStockDTO);
		// set company
		openingStock.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		openingStock = openingStockRepository.save(openingStock);
		OpeningStockDTO result = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
		return result;
	}

	/**
	 * Update a openingStock.
	 *
	 * @param openingStockDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public OpeningStockDTO update(OpeningStockDTO openingStockDTO) {
		log.debug("Request to Update EmployeeProfile : {}", openingStockDTO);
		return openingStockRepository.findOneByPid(openingStockDTO.getPid()).map(openingStock -> {
			openingStock.setBatchNumber(openingStockDTO.getBatchNumber());
			openingStock.setProductProfile(
					productProfileRepository.findOneByPid(openingStockDTO.getProductProfilePid()).get());
			openingStock.setStockLocation(
					stockLocationRepository.findOneByPid(openingStockDTO.getStockLocationPid()).get());
			openingStock.setBatchNumber(openingStockDTO.getBatchNumber());
			// openingStock.setCreatedDate(openingStockDTO.getCreatedDate());
			// openingStock.setOpeningStockDate(openingStockDTO.getOpeningStockDate());
			openingStock.setQuantity(openingStockDTO.getQuantity());
			openingStock = openingStockRepository.save(openingStock);
			OpeningStockDTO result = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the OpeningStocks.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<OpeningStock> findAll(Pageable pageable) {
		log.debug("Request to get all OpeningStocks");
		Page<OpeningStock> result = openingStockRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the openingStocks.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<OpeningStockDTO> findAllByCompany() {
		log.debug("Request to get all openingStocks");
		List<OpeningStock> openingStockList = openingStockRepository.findAllByCompanyId();
		List<OpeningStockDTO> result = openingStockMapper.openingStocksToOpeningStockDTOs(openingStockList);
		return result;
	}

	/**
	 * Get all the categorys.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<OpeningStockDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all EmployeeProfiles");
		Page<OpeningStock> openingStocks = openingStockRepository.findAllByCompanyId(pageable);
		Page<OpeningStockDTO> result = new PageImpl<OpeningStockDTO>(
				openingStockMapper.openingStocksToOpeningStockDTOs(openingStocks.getContent()), pageable,
				openingStocks.getTotalElements());
		return result;
	}

	/**
	 * Get one openingStock by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public OpeningStockDTO findOne(Long id) {
		log.debug("Request to get EmployeeProfile : {}", id);
		OpeningStock openingStock = openingStockRepository.findOne(id);
		OpeningStockDTO openingStockDTO = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
		return openingStockDTO;
	}

	/**
	 * Get one openingStock by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<OpeningStockDTO> findOneByPid(String pid) {
		log.debug("Request to get EmployeeProfile by pid : {}", pid);
		return openingStockRepository.findOneByPid(pid).map(openingStock -> {
			OpeningStockDTO openingStockDTO = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
			return openingStockDTO;
		});
	}

	/**
	 * Get one openingStock by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<OpeningStockDTO> findByName(String name, String pid) {
		log.debug("Request to get EmployeeProfile by name : {}", name);
		return openingStockRepository.findByCompanyIdAndProductProfilePidAndBatchNumberIgnoreCase(
				SecurityUtils.getCurrentUsersCompanyId(), pid, name).map(openingStock -> {
					OpeningStockDTO openingStockDTO = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
					return openingStockDTO;
				});
	}

	@Override
	@Transactional(readOnly = true)
	public List<OpeningStockDTO> findAllOpeningStockByStockLocations() {
		List<StockLocation> stockLocations = userStockLocationRepository.findStockLocationsByUserIsCurrentUser();
		List<OpeningStock> result = openingStockRepository.findByStockLocationIn(stockLocations);
		return openingStockMapper.openingStocksToOpeningStockDTOs(result);
	}

	/**
	 * Delete the openingStock by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete EmployeeProfile : {}", pid);
		openingStockRepository.findOneByPid(pid).ifPresent(openingStock -> {
			openingStockRepository.delete(openingStock.getId());
		});
	}

	@Override
	public List<OpeningStockDTO> findAllOpeningStockByProductProfiles(List<ProductProfile> productProfiles) {
		List<OpeningStock> result = openingStockRepository.findByProductProfileIn(productProfiles);
		return openingStockMapper.openingStocksToOpeningStockDTOs(result);
	}

	/**
	 * Update the OpeningStock status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public OpeningStockDTO updateOpeningStockStatus(String pid, boolean activate) {
		log.debug("Request to update OpeningStock status");
		return openingStockRepository.findOneByPid(pid).map(openingStock -> {
			openingStock.setActivated(activate);
			openingStock = openingStockRepository.save(openingStock);
			OpeningStockDTO result = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * 
	 * @since Feb 9, 2017
	 * 
	 *        find all openingStockDTOs from OpeningStock by status and company
	 *        and productProfile.
	 * 
	 * @param active
	 *            the active of the entity
	 * @param productPid
	 *            the productPid of the entity
	 * @return the list of entity
	 */
	@Override
	public List<OpeningStockDTO> findByCompanyIdAndProductProfilePidAndOpeningStockActivated(String productPid,
			boolean active) {
		List<OpeningStock> openingStocks = openingStockRepository
				.findByCompanyIdAndProductProfilePidAndOpeningStockActivated(productPid, active);
		List<OpeningStockDTO> openingStockDTOs = openingStockMapper.openingStocksToOpeningStockDTOs(openingStocks);
		return openingStockDTOs;
	}

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
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
	public Page<OpeningStockDTO> findAllByCompanyAndActivatedOpeningStockOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get activated OpeningStock ");
		Page<OpeningStock> pageOpeningStock = openingStockRepository
				.findAllByCompanyIdAndActivatedOpeningStockOrderByName(pageable, active);
		Page<OpeningStockDTO> pageOpeningStockDTO = new PageImpl<OpeningStockDTO>(
				openingStockMapper.openingStocksToOpeningStockDTOs(pageOpeningStock.getContent()), pageable,
				pageOpeningStock.getTotalElements());
		return pageOpeningStockDTO;
	}

	/**
	 * @author Fahad
	 * @since Feb 16, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<OpeningStockDTO> findAllByCompanyAndDeactivatedOpeningStock(boolean deactive) {
		log.debug("Request to get deactivated OpeningStock ");
		List<OpeningStock> openingStocks = openingStockRepository
				.findAllByCompanyIdAndDeactivatedOpeningStock(deactive);
		List<OpeningStockDTO> openingStockDTOs = openingStockMapper.openingStocksToOpeningStockDTOs(openingStocks);
		return openingStockDTOs;
	}
	
	/**
	 * Save a openingStock.
	 * 
	 * @param openingStockDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public OpeningStockDTO saveOpeningStock(OpeningStockDTO openingStockDTO,Long companyId) {
		log.debug("Request to save EmployeeProfile : {}", openingStockDTO);
		openingStockDTO.setPid(OpeningStockService.PID_PREFIX + RandomUtil.generatePid()); // set
		openingStockDTO.setOpeningStockDate(LocalDateTime.now());
		openingStockDTO.setCreatedDate(LocalDateTime.now());
		OpeningStock openingStock = openingStockMapper.openingStockDTOToOpeningStock(openingStockDTO);
		// set company
		openingStock.setCompany(companyRepository.findOne(companyId));
		openingStock = openingStockRepository.save(openingStock);
		OpeningStockDTO result = openingStockMapper.openingStockToOpeningStockDTO(openingStock);
		return result;
	}
	@Override
	public List<OpeningStockDTO> findByCompanyIdAndProductProfilePidAndOpeningStockActivatedAndLastModifiedDate(String productPid,boolean active,LocalDateTime lastModifiedDate ) {
		List<OpeningStock> openingStocks = openingStockRepository
				.findByCompanyIdAndProductProfilePidAndOpeningStockActivatedAndLastModifiedDate(productPid, active,lastModifiedDate);
		List<OpeningStockDTO> openingStockDTOs = openingStockMapper.openingStocksToOpeningStockDTOs(openingStocks);
		return openingStockDTOs;
	}
}
