package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.service.FilledFormService;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;

/**
 * Service Implementation for managing FilledForm.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class FilledFormServiceServiceImpl implements FilledFormService {

	private final Logger log = LoggerFactory.getLogger(FilledFormServiceServiceImpl.class);

	@Inject
	private FilledFormRepository filledFormRepository;

	/**
	 * Get one filledForm by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public FilledFormDTO findOne(Long id) {
		log.debug("Request to get FilledForm : {}", id);
		FilledForm filledForm = filledFormRepository.findOne(id);
		FilledFormDTO filledFormDTO = new FilledFormDTO(filledForm);
		filledFormDTO.setFilledFormDetails(
				filledForm.getFilledFormDetails().stream().map(FilledFormDetailDTO::new).collect(Collectors.toList()));
		return filledFormDTO;
	}

	/**
	 * Get one filledForm by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<FilledFormDTO> findOneByPid(String pid) {
		log.debug("Request to get FilledForm by pid : {}", pid);
		String id="FORM_QUERY_101";
		String description="Get the one filled form by pid ";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

		return filledFormRepository.findOneByPid(pid).map(filledForm -> {
			FilledFormDTO filledFormDTO = new FilledFormDTO(filledForm);
			filledFormDTO.setFilledFormDetails(filledForm.getFilledFormDetails().stream().map(FilledFormDetailDTO::new)
					.collect(Collectors.toList()));
			return filledFormDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<FilledFormDTO> findByDynamicDocumentHeaderDocumentPid(String documentHeaderpid) {
		log.debug("Request to get FilledForm by documentHeaderpid : {}", documentHeaderpid);
		String id="FORM_QUERY_105";
		String description="get the form by dynamic document header document Pid";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderDocumentPid(documentHeaderpid);
		List<FilledFormDTO> result = new ArrayList<>();
		for (FilledForm filledForm : filledForms) {
			FilledFormDTO filledFormDTO = new FilledFormDTO(filledForm);
			result.add(filledFormDTO);
		}
		return result;

	}

}
