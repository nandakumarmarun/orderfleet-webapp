package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DocumentPrint;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentPrintRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentPrintService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintDTO;
import com.orderfleet.webapp.web.rest.mapper.DocumentPrintMapper;

/**
 * 
 * Service Implementation for managing DocumentPrint.
 *
 * @author Sarath
 * @since Aug 12, 2017
 *
 */
@Service
@Transactional
public class DocumentPrintServiceImpl implements DocumentPrintService {

	private final Logger log = LoggerFactory.getLogger(DocumentPrintServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private DocumentPrintRepository documentPrintRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentPrintMapper documentPrintMapper;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private ActivityRepository activityRepository;

	/**
	 * Save a documentPrint.
	 * 
	 * @param documentPrintDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DocumentPrintDTO save(DocumentPrintDTO documentPrintDTO) {
		log.debug("Request to save DocumentPrint : {}", documentPrintDTO);
		documentPrintDTO.setPid(DocumentPrintService.PID_PREFIX + RandomUtil.generatePid()); // set
		DocumentPrint documentPrint = documentPrintMapper.documentPrintDTOToDocumentPrint(documentPrintDTO);
		documentPrint.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		documentPrint = documentPrintRepository.save(documentPrint);
		DocumentPrintDTO result = documentPrintMapper.documentPrintToDocumentPrintDTO(documentPrint);
		return result;
	}

	/**
	 * Update a documentPrint.
	 * 
	 * @param documentPrintDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DocumentPrintDTO update(DocumentPrintDTO documentPrintDTO) {
		log.debug("Request to Update DocumentPrint : {}", documentPrintDTO);
		return documentPrintRepository.findOneByPid(documentPrintDTO.getPid()).map(documentPrint -> {
			// documentPrint.setName(documentPrintDTO.getName());
			// documentPrint.setAlias(documentPrintDTO.getAlias());
			// documentPrint.setDescription(documentPrintDTO.getDescription());

			documentPrint = documentPrintRepository.save(documentPrint);
			DocumentPrintDTO result = documentPrintMapper.documentPrintToDocumentPrintDTO(documentPrint);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the documentPrints.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DocumentPrintDTO> findAllByCompany() {
		log.debug("Request to get all DocumentPrints");
		List<DocumentPrint> documentPrintList = documentPrintRepository.findAllByCompanyId();
		List<DocumentPrintDTO> result = documentPrintMapper.documentPrintsToDocumentPrintDTOs(documentPrintList);
		return result;
	}

	/**
	 * Get one documentPrint by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DocumentPrintDTO findOne(Long id) {
		log.debug("Request to get DocumentPrint : {}", id);
		DocumentPrint documentPrint = documentPrintRepository.findOne(id);
		DocumentPrintDTO documentPrintDTO = documentPrintMapper.documentPrintToDocumentPrintDTO(documentPrint);
		return documentPrintDTO;
	}

	/**
	 * Get one documentPrint by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocumentPrintDTO> findOneByPid(String pid) {
		log.debug("Request to get DocumentPrint by pid : {}", pid);
		return documentPrintRepository.findOneByPid(pid).map(documentPrint -> {
			DocumentPrintDTO documentPrintDTO = documentPrintMapper.documentPrintToDocumentPrintDTO(documentPrint);
			return documentPrintDTO;
		});
	}

	/**
	 * Delete the documentPrint by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete DocumentPrint : {}", pid);
		documentPrintRepository.findOneByPid(pid).ifPresent(documentPrint -> {
			documentPrintRepository.delete(documentPrint.getId());
		});
	}

	@Override
	public void saveDocumentPrint(String userPid, String activityPid, String printEnableDocuments) {
		log.debug("Request to save DocumentPrint : {}");

		List<DocumentPrint> existingDocs = documentPrintRepository.findAllByUserPidAndActivityPid(userPid, activityPid);
		if (!existingDocs.isEmpty()) {
			documentPrintRepository.delete(existingDocs);
		}
		List<String> documentPids = Arrays.asList(printEnableDocuments.split(","));
		if (!documentPids.isEmpty()) {
			for (String docPid : documentPids) {
				if (!docPid.equalsIgnoreCase("")) {
					DocumentPrint documentPrint = new DocumentPrint();
					documentPrint.setPid(DocumentPrintService.PID_PREFIX + RandomUtil.generatePid());
					documentPrint.setDocument(documentRepository.findOneByPid(docPid).get());
					DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
					DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					String id = "ACTIVITY_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
					String description ="get one by pid";
					LocalDateTime startLCTime = LocalDateTime.now();
					String startTime = startLCTime.format(DATE_TIME_FORMAT);
					String startDate = startLCTime.format(DATE_FORMAT);
					logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
					documentPrint.setActivity(activityRepository.findOneByPid(activityPid).get());
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

					documentPrint.setUser(userRepository.findOneByPid(userPid).get());
					documentPrint.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
					documentPrint.setPrintStatus(true);
					documentPrint = documentPrintRepository.save(documentPrint);
				}
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentDTO> findAllDocumentsByActivityPidAndUserPid(String activityPid, String userPid) {
		log.debug("Request to get DocumentPrint documents by activityPid : {}", activityPid);
		return documentPrintRepository.findAllDocumentsByActivityPid(activityPid, userPid).stream()
				.map(DocumentDTO::new).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<DocumentPrintDTO> findAllByUserLogin() {
		log.debug("Request to get DocumentPrint  by user login : {}");
		return documentPrintRepository.findAllByUserLogin(SecurityUtils.getCurrentUserLogin()).stream()
				.map(DocumentPrintDTO::new).collect(Collectors.toList());
	}
}
