package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.RealtimeAPI;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RealtimeAPIRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RealtimeAPIService;
import com.orderfleet.webapp.web.rest.dto.RealtimeAPIDTO;

/**
 * Service Implementation for managing RealtimeAPI.
 *
 * @author Sarath
 * @since Apr 10, 2018
 *
 */
@Transactional
@Service
public class RealtimeAPIServiceImpl implements RealtimeAPIService {

	private final Logger log = LoggerFactory.getLogger(RealtimeAPIServiceImpl.class);

	@Inject
	private RealtimeAPIRepository realtimeAPIRepository;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * Save a realtimeAPI.
	 * 
	 * @param realtimeAPIDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public RealtimeAPIDTO save(RealtimeAPIDTO realtimeAPIDTO) {
		log.debug("Request to save RealtimeAPI : {}", realtimeAPIDTO);

		Optional<Company> opCompany = companyRepository.findOneByPid(realtimeAPIDTO.getCompanyPid());

		if (opCompany.isPresent()) {
			RealtimeAPI realtimeAPI = new RealtimeAPI();

			realtimeAPI.setName(realtimeAPIDTO.getName());
			realtimeAPI.setApi(realtimeAPIDTO.getApi());
			realtimeAPI.setVersion(realtimeAPIDTO.getVersion());
			realtimeAPI.setActivated(realtimeAPIDTO.isActivated());

			realtimeAPI.setCompany(opCompany.get());
			realtimeAPI = realtimeAPIRepository.save(realtimeAPI);

			RealtimeAPIDTO result = new RealtimeAPIDTO(realtimeAPI);
			return result;
		}
		return null;

	}

	/**
	 * Update a realtimeAPI.
	 * 
	 * @param realtimeAPIDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public RealtimeAPIDTO update(RealtimeAPIDTO realtimeAPIDTO) {
		log.debug("Request to Update RealtimeAPI : {}", realtimeAPIDTO);
		return realtimeAPIRepository.findOneById(realtimeAPIDTO.getId()).map(realtimeAPI -> {
			realtimeAPI.setName(realtimeAPIDTO.getName());
			realtimeAPI.setApi(realtimeAPIDTO.getApi());
			realtimeAPI.setVersion(realtimeAPIDTO.getVersion());

			realtimeAPI = realtimeAPIRepository.save(realtimeAPI);
			RealtimeAPIDTO result = new RealtimeAPIDTO(realtimeAPI);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the realtimeAPIs.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<RealtimeAPIDTO> findAll() {
		log.debug("Request to get all RealtimeAPIs");
		List<RealtimeAPI> realtimeAPIList = realtimeAPIRepository.findAll();
		List<RealtimeAPIDTO> result = realtimeAPIList.stream().map(RealtimeAPIDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get one realtimeAPI by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public RealtimeAPIDTO findOne(Long id) {
		log.debug("Request to get RealtimeAPI : {}", id);
		RealtimeAPI realtimeAPI = realtimeAPIRepository.findOne(id);
		RealtimeAPIDTO realtimeAPIDTO = new RealtimeAPIDTO(realtimeAPI);
		return realtimeAPIDTO;
	}

	/**
	 * Get one realtimeAPI by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<RealtimeAPIDTO> findOneById(Long id) {
		log.debug("Request to get RealtimeAPI by id : {}", id);
		return realtimeAPIRepository.findOneById(id).map(realtimeAPI -> {
			RealtimeAPIDTO realtimeAPIDTO = new RealtimeAPIDTO(realtimeAPI);
			return realtimeAPIDTO;
		});
	}

	/**
	 * Get one realtimeAPI by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<RealtimeAPIDTO> findByName(String name) {
		log.debug("Request to get RealtimeAPI by name : {}", name);
		return realtimeAPIRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(realtimeAPI -> {
					RealtimeAPIDTO realtimeAPIDTO = new RealtimeAPIDTO(realtimeAPI);
					return realtimeAPIDTO;
				});
	}

	/**
	 * Delete the realtimeAPI by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete RealtimeAPI : {}", id);
		realtimeAPIRepository.findOneById(id).ifPresent(realtimeAPI -> {
			realtimeAPIRepository.delete(realtimeAPI.getId());
		});
	}

	@Override
	public RealtimeAPIDTO updateRealtimeAPIStatus(Long id, boolean activate) {
		log.debug("Request to update realtimeAPI status: {}");
		return realtimeAPIRepository.findOneById(id).map(realtimeAPI -> {
			realtimeAPI.setActivated(activate);
			realtimeAPI = realtimeAPIRepository.save(realtimeAPI);
			RealtimeAPIDTO result = new RealtimeAPIDTO(realtimeAPI);
			return result;
		}).orElse(null);
	}

	@Override
	public List<RealtimeAPIDTO> findAllByActivatedRealtimeAPI(boolean active) {
		log.debug("Request to get Activated RealtimeAPI ");
		List<RealtimeAPI> realtimeAPIs = realtimeAPIRepository.findAllByActivatedRealtimeAPI(active);
		List<RealtimeAPIDTO> realtimeAPIDTOs = realtimeAPIs.stream().map(RealtimeAPIDTO::new)
				.collect(Collectors.toList());
		return realtimeAPIDTOs;
	}

	@Override
	public List<RealtimeAPIDTO> findAllByCompanyPid(String companyPid) {
		List<RealtimeAPI> realTimeAPIs = realtimeAPIRepository.findAllByCompanyPid(companyPid);
		List<RealtimeAPIDTO> realtimeAPIDTOs = realTimeAPIs.stream().map(RealtimeAPIDTO::new)
				.collect(Collectors.toList());
		return realtimeAPIDTOs;
	}

}
