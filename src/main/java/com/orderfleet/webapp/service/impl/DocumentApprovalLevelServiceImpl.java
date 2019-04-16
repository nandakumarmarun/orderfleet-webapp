package com.orderfleet.webapp.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DocumentApprovalLevel;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DocumentApprovalLevelRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentApprovalLevelService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentApprovalLevelDTO;

/**
 * Service Implementation for managing DocumentApprovalLevel.
 * 
 * @author Muhammed Riyas T
 * @since November 19, 2016
 */
@Service
@Transactional
public class DocumentApprovalLevelServiceImpl implements DocumentApprovalLevelService {

	private final Logger log = LoggerFactory.getLogger(DocumentApprovalLevelServiceImpl.class);

	@Inject
	private DocumentApprovalLevelRepository documentApprovalLevelRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a documentApprovalLevel.
	 * 
	 * @param documentApprovalLevelDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DocumentApprovalLevelDTO save(DocumentApprovalLevelDTO documentApprovalLevelDTO) {
		log.debug("Request to save DocumentApprovalLevel : {}", documentApprovalLevelDTO);

		DocumentApprovalLevel documentApprovalLevel = new DocumentApprovalLevel();
		// set pid
		documentApprovalLevel.setPid(DocumentApprovalLevelService.PID_PREFIX + RandomUtil.generatePid());
		documentApprovalLevel.setName(documentApprovalLevelDTO.getName());
		documentApprovalLevel.setApproverCount(documentApprovalLevelDTO.getApproverCount());
		documentApprovalLevel.setRequired(documentApprovalLevelDTO.getRequired());
		documentApprovalLevel.setScript(documentApprovalLevelDTO.getScript());
		documentApprovalLevel
				.setDocument(documentRepository.findOneByPid(documentApprovalLevelDTO.getDocumentPid()).get());
		// set company
		documentApprovalLevel.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		documentApprovalLevel = documentApprovalLevelRepository.save(documentApprovalLevel);
		DocumentApprovalLevelDTO result = new DocumentApprovalLevelDTO(documentApprovalLevel);
		return result;
	}

	@Override
	public void saveAssignedUsers(String pid, String assignedUsers) {
		DocumentApprovalLevel documentApprovalLevel = documentApprovalLevelRepository.findOneByPid(pid).get();
		String[] usersPid = assignedUsers.split(",");
		Set<User> users = new HashSet<User>();
		for (String userPid : usersPid) {
			User user = userRepository.findOneByPid(userPid).get();
			users.add(user);
		}
		documentApprovalLevel.setUsers(users);
		documentApprovalLevelRepository.save(documentApprovalLevel);
	}

	/**
	 * Update a documentApprovalLevel.
	 * 
	 * @param documentApprovalLevelDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DocumentApprovalLevelDTO update(DocumentApprovalLevelDTO documentApprovalLevelDTO) {
		log.debug("Request to Update DocumentApprovalLevel : {}", documentApprovalLevelDTO);

		return documentApprovalLevelRepository.findOneByPid(documentApprovalLevelDTO.getPid())
				.map(documentApprovalLevel -> {
					documentApprovalLevel.setName(documentApprovalLevelDTO.getName());
					documentApprovalLevel.setApproverCount(documentApprovalLevelDTO.getApproverCount());
					documentApprovalLevel.setRequired(documentApprovalLevelDTO.getRequired());
					documentApprovalLevel.setScript(documentApprovalLevelDTO.getScript());
					documentApprovalLevel.setDocument(
							documentRepository.findOneByPid(documentApprovalLevelDTO.getDocumentPid()).get());
					documentApprovalLevel = documentApprovalLevelRepository.save(documentApprovalLevel);
					DocumentApprovalLevelDTO result = new DocumentApprovalLevelDTO(documentApprovalLevel);
					return result;
				}).orElse(null);
	}

	/**
	 * Get all the DocumentApprovalLevels.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DocumentApprovalLevelDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all DocumentApprovalLevels");
		Page<DocumentApprovalLevel> documentApprovalLevels = documentApprovalLevelRepository
				.findAllByCompanyId(pageable);
		List<DocumentApprovalLevelDTO> documentApprovalLevelDTOs = documentApprovalLevels.getContent().stream()
				.map(DocumentApprovalLevelDTO::new).collect(Collectors.toList());
		Page<DocumentApprovalLevelDTO> result = new PageImpl<DocumentApprovalLevelDTO>(documentApprovalLevelDTOs,
				pageable, documentApprovalLevels.getTotalElements());
		return result;
	}

	/**
	 * Get one documentApprovalLevel by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DocumentApprovalLevelDTO findOne(Long id) {
		log.debug("Request to get DocumentApprovalLevel : {}", id);
		DocumentApprovalLevel documentApprovalLevel = documentApprovalLevelRepository.findOne(id);
		DocumentApprovalLevelDTO documentApprovalLevelDTO = new DocumentApprovalLevelDTO(documentApprovalLevel);
		return documentApprovalLevelDTO;
	}

	/**
	 * Get one documentApprovalLevel by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocumentApprovalLevelDTO> findOneByPid(String pid) {
		log.debug("Request to get DocumentApprovalLevel by pid : {}", pid);
		return documentApprovalLevelRepository.findOneByPid(pid).map(documentApprovalLevel -> {
			DocumentApprovalLevelDTO documentApprovalLevelDTO = new DocumentApprovalLevelDTO(documentApprovalLevel);
			documentApprovalLevel.getUsers().forEach(user -> {
				UserDTO userDTO = new UserDTO();
				userDTO.setPid(user.getPid());
				userDTO.setFirstName(user.getFirstName());
				userDTO.setLastName(user.getLastName());
				userDTO.setLogin(user.getLogin());
				documentApprovalLevelDTO.getUsers().add(userDTO);
			});
			return documentApprovalLevelDTO;
		});
	}

	/**
	 * Get one documentApprovalLevel by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DocumentApprovalLevelDTO> findByName(String name) {
		log.debug("Request to get DocumentApprovalLevel by name : {}", name);
		return documentApprovalLevelRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(documentApprovalLevel -> {
					DocumentApprovalLevelDTO documentApprovalLevelDTO = new DocumentApprovalLevelDTO(
							documentApprovalLevel);
					return documentApprovalLevelDTO;
				});
	}

	/**
	 * Delete the documentApprovalLevel by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete DocumentApprovalLevel : {}", pid);
		documentApprovalLevelRepository.findOneByPid(pid).ifPresent(documentApprovalLevel -> {
			documentApprovalLevelRepository.delete(documentApprovalLevel.getId());
		});
	}

	@Override
	public List<DocumentApprovalLevelDTO> findAllByDocumentPid(String documentPid) {
		List<DocumentApprovalLevel> documents = documentApprovalLevelRepository.findAllByDocumentPid(documentPid);
		List<DocumentApprovalLevelDTO> result = documents.stream().map(DocumentApprovalLevelDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public DocumentApprovalLevelDTO saveApprovalOrder(DocumentApprovalLevelDTO approvalLevelDTO) {
		log.debug("Request to save DocumentApprovalLevel order : {}", approvalLevelDTO);
		Optional<DocumentApprovalLevel> documentApprovalLevel = documentApprovalLevelRepository
				.findOneByPid(approvalLevelDTO.getPid());

		if (documentApprovalLevel.isPresent()) {
			DocumentApprovalLevel documentApproval = documentApprovalLevel.get();
			documentApproval.setApprovalOrder(approvalLevelDTO.getApprovalOrder());
			documentApproval = documentApprovalLevelRepository.save(documentApproval);
			DocumentApprovalLevelDTO result = new DocumentApprovalLevelDTO(documentApproval);
			return result;
		}
		return approvalLevelDTO;
	}

	@Override
	public List<DocumentApprovalLevelDTO> findAllByCompany() {
		List<DocumentApprovalLevel> documents=documentApprovalLevelRepository.findAllByCompanyId();
		List<DocumentApprovalLevelDTO> result = documents.stream().map(DocumentApprovalLevelDTO::new)
				.collect(Collectors.toList());
		return result;
	}
}
