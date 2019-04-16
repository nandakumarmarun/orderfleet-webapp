package com.orderfleet.webapp.service.impl;

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.multipart.MultipartFile;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.KnowledgebaseFiles;
import com.orderfleet.webapp.domain.UserKnowledgebaseFile;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.KnowledgebaseFilesRepository;
import com.orderfleet.webapp.repository.KnowledgebaseRepository;
import com.orderfleet.webapp.repository.UserKnowledgebaseFileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.KnowledgebaseFilesService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseFileDTO;

/**
 * Service Implementation for managing KnowledgebaseFile.
 * 
 * @author Sarath
 * @since Aug 10, 2016
 */
@Service
@Transactional
public class KnowledgebaseFilesServiceImpl implements KnowledgebaseFilesService {

	private final Logger log = LoggerFactory.getLogger(KnowledgebaseFilesServiceImpl.class);

	@Inject
	private KnowledgebaseFilesRepository knowledgeBaseFilesRepository;

	@Inject
	private KnowledgebaseRepository knowledgebaseRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserKnowledgebaseFileRepository userKnowledgebaseFileRepository;

	@Override
	public void saveKnowledgebaseFile(KnowledgebaseFileDTO knowledgebaseFileDTO, MultipartFile file)
			throws FileManagerException, IOException {

		// save file
		File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(), file.getOriginalFilename(),
				file.getContentType());
		KnowledgebaseFiles knowledgeBaseFile = new KnowledgebaseFiles();
		// set pid
		knowledgeBaseFile.setPid(KnowledgebaseFilesService.PID_PREFIX + RandomUtil.generatePid());
		knowledgeBaseFile.setFileName(knowledgebaseFileDTO.getFileName());
		knowledgeBaseFile.setFile(uploadedFile);
		knowledgeBaseFile.setSearchTags(knowledgebaseFileDTO.getSearchTags());
		knowledgeBaseFile.setKnowledgebase(
				knowledgebaseRepository.findOneByPid(knowledgebaseFileDTO.getKnowledgebasePid()).get());
		knowledgeBaseFile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		knowledgeBaseFilesRepository.save(knowledgeBaseFile);
	}

	/**
	 * Update a knowledgebaseFile.
	 * 
	 * @param knowledgebaseFileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public KnowledgebaseFileDTO update(KnowledgebaseFileDTO knowledgebaseFileDTO, MultipartFile file)
			throws FileManagerException, IOException {
		log.debug("Request to Update knowledgebaseFile : {}", knowledgebaseFileDTO);

		return knowledgeBaseFilesRepository.findOneByPid(knowledgebaseFileDTO.getPid()).map(knowledgeBaseFile -> {
			knowledgeBaseFile.setFileName(knowledgebaseFileDTO.getFileName());
			knowledgeBaseFile.setSearchTags(knowledgebaseFileDTO.getSearchTags());
			knowledgeBaseFile.setKnowledgebase(
					knowledgebaseRepository.findOneByPid(knowledgebaseFileDTO.getKnowledgebasePid()).get());
			// save file
			try {
				if (file != null) {
					File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
							file.getOriginalFilename(), file.getContentType());
					knowledgeBaseFile.setFile(uploadedFile);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			knowledgeBaseFile = knowledgeBaseFilesRepository.save(knowledgeBaseFile);
			KnowledgebaseFileDTO result = new KnowledgebaseFileDTO(knowledgeBaseFile);
			return result;
		}).orElse(null);
	}

	@Override
	public void saveAssignedUsers(String pid, String assignedUsers) {
		
		userKnowledgebaseFileRepository.deleteByKnowledgebaseFilesPid(pid);
		KnowledgebaseFiles knowledgeBaseFiles = knowledgeBaseFilesRepository.findOneByPid(pid).get();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		String[] users = assignedUsers.split(",");

		List<UserKnowledgebaseFile> userKnowledgebaseFiles = new ArrayList<>();
		for (String userPid : users) {
			UserKnowledgebaseFile userKnowledgebaseFile = new UserKnowledgebaseFile();
			userKnowledgebaseFile.setUser(userRepository.findOneByPid(userPid).get());
			userKnowledgebaseFile.setKnowledgebaseFiles(knowledgeBaseFiles);
			userKnowledgebaseFile.setCompany(company);
			userKnowledgebaseFiles.add(userKnowledgebaseFile);
		}
		userKnowledgebaseFileRepository.save(userKnowledgebaseFiles);
	}

	@Override
	public Page<KnowledgebaseFileDTO> findAllByCompany(Pageable pageable) {
		Page<KnowledgebaseFiles> knowledgeBaseFilesList = knowledgeBaseFilesRepository.findAllByCompanyId(pageable);
		List<KnowledgebaseFileDTO> knowledgebaseFileDTOs = knowledgeBaseFilesList.getContent().stream()
				.map(KnowledgebaseFileDTO::new).collect(Collectors.toList());
		Page<KnowledgebaseFileDTO> result = new PageImpl<>(knowledgebaseFileDTOs, pageable,
				knowledgeBaseFilesList.getTotalElements());
		return result;
	}

	/**
	 * Get one inventoryVoucherHeader by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<KnowledgebaseFileDTO> findOneByPid(String pid) {
		log.debug("Request to get KnowledgebaseFile by pid : {}", pid);
		return knowledgeBaseFilesRepository.findOneByPid(pid).map(knowledgeBaseFile -> {
			KnowledgebaseFileDTO knowledgebaseFileDTO = new KnowledgebaseFileDTO(knowledgeBaseFile);
			return knowledgebaseFileDTO;
		});
	}

	/**
	 * Delete the KnowledgebaseFile by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete KnowledgebaseFile : {}", pid);
		knowledgeBaseFilesRepository.findOneByPid(pid).ifPresent(knowledgebaseFile -> {
			knowledgeBaseFilesRepository.delete(knowledgebaseFile.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public Set<String> findSearchTagsByCompany() {
		return knowledgeBaseFilesRepository.findSearchTagsByCompany();
	}

	@Override
	public List<KnowledgebaseFileDTO> findAllByCompanyId() {
		List<KnowledgebaseFiles> knowledgeBaseFilesList = knowledgeBaseFilesRepository.findAllByCompanyId();
		List<KnowledgebaseFileDTO> knowledgebaseFileDTOs = knowledgeBaseFilesList.stream()
				.map(KnowledgebaseFileDTO::new).collect(Collectors.toList());
		return knowledgebaseFileDTOs;

	}

}
