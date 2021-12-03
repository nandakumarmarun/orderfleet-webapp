package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.orderfleet.webapp.security.SecurityUtils;
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
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "FORM_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Get the one filled form by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		return filledFormRepository.findOneByPid(pid).map(filledForm -> {
			FilledFormDTO filledFormDTO = new FilledFormDTO(filledForm);
			filledFormDTO.setFilledFormDetails(filledForm.getFilledFormDetails().stream().map(FilledFormDetailDTO::new)
					.collect(Collectors.toList()));
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
			return filledFormDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<FilledFormDTO> findByDynamicDocumentHeaderDocumentPid(String documentHeaderpid) {
		log.debug("Request to get FilledForm by documentHeaderpid : {}", documentHeaderpid);
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "FORM_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the form by dynamic document header document Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderDocumentPid(documentHeaderpid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		List<FilledFormDTO> result = new ArrayList<>();
		for (FilledForm filledForm : filledForms) {
			FilledFormDTO filledFormDTO = new FilledFormDTO(filledForm);
			result.add(filledFormDTO);
		}
		return result;

	}

}
