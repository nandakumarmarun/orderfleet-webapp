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

import com.orderfleet.webapp.domain.StaticFormJSCode;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.StaticFormJSCodeRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StaticFormJSCodeService;
import com.orderfleet.webapp.web.rest.dto.StaticFormJSCodeDTO;
import com.orderfleet.webapp.web.rest.mapper.StaticFormJSCodeMapper;

/**
 * Service Implementation for managing StaticFormJSCode.
 * 
 * @author Sarath
 * @since Aug 4, 2016
 */
@Service
@Transactional
public class StaticFormJSCodeServiceImpl implements StaticFormJSCodeService {

	private final Logger log = LoggerFactory.getLogger(StaticFormJSCodeServiceImpl.class);

	@Inject
	private StaticFormJSCodeRepository staticFormJSCodeRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private StaticFormJSCodeMapper staticFormJSCodeMapper;

	@Inject
	private DocumentRepository documentRepository;

	/**
	 * Save a staticFormJSCode.
	 *
	 * @param staticFormJSCodeDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public StaticFormJSCodeDTO save(StaticFormJSCodeDTO staticFormJSCodeDTO) {
		log.debug("Request to save StaticFormJSCode : {}", staticFormJSCodeDTO);

		StaticFormJSCode staticFormJSCode = staticFormJSCodeMapper
				.staticFormJSCodeDTOToStaticFormJSCode(staticFormJSCodeDTO);
		// set company
		staticFormJSCode.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		staticFormJSCode.setDocument(documentRepository.findOneByPid(staticFormJSCodeDTO.getDocumentPid()).get());
		staticFormJSCode = staticFormJSCodeRepository.save(staticFormJSCode);
		StaticFormJSCodeDTO result = new StaticFormJSCodeDTO(staticFormJSCode);

		return result;
	}

	@Override
	public void save(String companyPid, String documentPid, String jsCode) {
		StaticFormJSCode staticFormJSCode = staticFormJSCodeRepository.findByCompanyPidAndDocumentPid(companyPid,
				documentPid);
		if (staticFormJSCode != null) {
			staticFormJSCode.setJsCode(jsCode);
		} else {
			staticFormJSCode = new StaticFormJSCode();
			staticFormJSCode.setJsCodeName("Calculate Total");
			staticFormJSCode.setCompany(companyRepository.findOneByPid(companyPid).get());
			staticFormJSCode.setDocument(documentRepository.findOneByPid(documentPid).get());
			staticFormJSCode.setJsCode(jsCode);
		}
		staticFormJSCodeRepository.save(staticFormJSCode);
	}

	/**
	 * Update a staticFormJSCode.
	 *
	 * @param staticFormJSCodeDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public StaticFormJSCodeDTO update(StaticFormJSCodeDTO staticFormJSCodeDTO) {
		log.debug("Request to Update StaticFormJSCode : {}", staticFormJSCodeDTO);

		return staticFormJSCodeRepository.findOneById(staticFormJSCodeDTO.getId()).map(staticFormJSCode -> {
			staticFormJSCode.setDocument(documentRepository.findOneByPid(staticFormJSCodeDTO.getDocumentPid()).get());
			staticFormJSCode.setJsCode(staticFormJSCodeDTO.getJsCode());
			staticFormJSCode.setJsCodeName(staticFormJSCodeDTO.getJsCodeName());
			staticFormJSCode = staticFormJSCodeRepository.save(staticFormJSCode);
			StaticFormJSCodeDTO result = staticFormJSCodeMapper.staticFormJSCodeToStaticFormJSCodeDTO(staticFormJSCode);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the staticFormJSCodes.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<StaticFormJSCode> findAll(Pageable pageable) {
		log.debug("Request to get all StaticFormJSCodes");
		Page<StaticFormJSCode> result = staticFormJSCodeRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the staticFormJSCodes.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<StaticFormJSCodeDTO> findAllByCompany() {
		log.debug("Request to get all StaticFormJSCodes");
		List<StaticFormJSCode> staticFormJSCodes = staticFormJSCodeRepository.findAllByCompanyId();
		List<StaticFormJSCodeDTO> result = staticFormJSCodeMapper
				.staticFormJSCodesToStaticFormJSCodeDTOs(staticFormJSCodes);
		return result;
	}

	/**
	 * Get all the staticFormJSCodes.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<StaticFormJSCodeDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all StaticFormJSCodes");
		Page<StaticFormJSCode> staticFormJSCodes = staticFormJSCodeRepository.findAllByCompanyId(pageable);
		Page<StaticFormJSCodeDTO> result = new PageImpl<StaticFormJSCodeDTO>(
				staticFormJSCodeMapper.staticFormJSCodesToStaticFormJSCodeDTOs(staticFormJSCodes.getContent()),
				pageable, staticFormJSCodes.getTotalElements());
		return result;
	}

	/**
	 * Get one staticFormJSCode by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public StaticFormJSCodeDTO findOne(Long id) {
		log.debug("Request to get StaticFormJSCode : {}", id);
		StaticFormJSCode staticFormJSCode = staticFormJSCodeRepository.findOne(id);
		StaticFormJSCodeDTO staticFormJSCodeDTO = staticFormJSCodeMapper
				.staticFormJSCodeToStaticFormJSCodeDTO(staticFormJSCode);
		return staticFormJSCodeDTO;
	}

	/**
	 * Get one staticFormJSCode by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<StaticFormJSCodeDTO> findOneById(Long id) {
		log.debug("Request to get StaticFormJSCode by id : {}", id);
		return staticFormJSCodeRepository.findOneById(id).map(staticFormJSCode -> {
			StaticFormJSCodeDTO staticFormJSCodeDTO = staticFormJSCodeMapper
					.staticFormJSCodeToStaticFormJSCodeDTO(staticFormJSCode);
			return staticFormJSCodeDTO;
		});
	}

	/**
	 * Delete the staticFormJSCode by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete StaticFormJSCode : {}", id);
		staticFormJSCodeRepository.findOneById(id).ifPresent(staticFormJSCode -> {
			staticFormJSCodeRepository.delete(staticFormJSCode.getId());
		});
	}

	@Override
	public StaticFormJSCodeDTO findByName(String JsCodeName) {
		log.debug("Request to get StaticFormJSCode : {}", JsCodeName);
		StaticFormJSCode staticFormJSCode = staticFormJSCodeRepository.findByJsCodeName(JsCodeName);
		StaticFormJSCodeDTO staticFormJSCodeDTO = staticFormJSCodeMapper
				.staticFormJSCodeToStaticFormJSCodeDTO(staticFormJSCode);
		return staticFormJSCodeDTO;
	}

}
