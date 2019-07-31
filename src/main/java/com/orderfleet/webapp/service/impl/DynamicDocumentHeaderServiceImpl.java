package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
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

import com.orderfleet.webapp.domain.AccountProfileDynamicDocumentAccountprofile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileDynamicDocumentAccountprofileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderHistoryRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.FilledFormService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;

/**
 * Service Implementation for managing DynamicDocumentHeader.
 * 
 * @author Muhammed Riyas T
 * @since June 04, 2016
 */
@Service
@Transactional
public class DynamicDocumentHeaderServiceImpl implements DynamicDocumentHeaderService {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentHeaderServiceImpl.class);

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private FormElementRepository formElementRepository;

	@Inject
	private DynamicDocumentHeaderHistoryRepository documentHeaderHistoryRepository;

	@Inject
	private AccountProfileDynamicDocumentAccountprofileRepository accountProfileDynamicDocumentAccountprofileRepository;

	/**
	 * Save a dynamicDocumentHeader.
	 * 
	 * @param dynamicDocumentHeaderDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DynamicDocumentHeaderDTO save(DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO) {
		log.debug("Request to save DynamicDocumentHeader : {}", dynamicDocumentHeaderDTO);

		DynamicDocumentHeader dynamicDocumentHeader = new DynamicDocumentHeader();
		// set pid
		dynamicDocumentHeader.setPid(DynamicDocumentHeaderService.PID_PREFIX + RandomUtil.generatePid());
		dynamicDocumentHeader.setCreatedBy(userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get());
		dynamicDocumentHeader
				.setDocument(documentRepository.findOneByPid(dynamicDocumentHeaderDTO.getDocumentPid()).get());
		dynamicDocumentHeader.setDocumentDate(dynamicDocumentHeaderDTO.getDocumentDate());
		dynamicDocumentHeader.setDocumentNumberLocal(dynamicDocumentHeaderDTO.getDocumentNumberLocal());
		// Set unique server number
		dynamicDocumentHeader.setDocumentNumberServer(dynamicDocumentHeaderDTO.getDocumentNumberLocal());
		dynamicDocumentHeader
				.setEmployee(employeeProfileRepository.findEmployeeProfileByUser(dynamicDocumentHeader.getCreatedBy()));
		// set company
		dynamicDocumentHeader.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));

		// set voucher details
		List<FilledForm> filledForms = new ArrayList<FilledForm>();
		dynamicDocumentHeaderDTO.getFilledForms().forEach(filledFormDTO -> {
			FilledForm filledForm = new FilledForm();
			// set pid
			filledForm.setPid(FilledFormService.PID_PREFIX + RandomUtil.generatePid());
			filledForm.setImageRefNo(filledFormDTO.getImageRefNo());
			filledForm.setForm(formRepository.findOneByPid(filledFormDTO.getFormPid()).get());
			// set voucher details
			List<FilledFormDetail> filledFormDetails = new ArrayList<FilledFormDetail>();
			List<FilledFormDetailDTO> filledFormDetailDTOs = filledFormDTO.getFilledFormDetails();
			for (FilledFormDetailDTO filledFormDetailDTO : filledFormDetailDTOs) {
				FilledFormDetail filledFormDetail = new FilledFormDetail();
				filledFormDetail.setValue(filledFormDetailDTO.getValue());
				filledFormDetail.setFormElement(
						formElementRepository.findOneByPid(filledFormDetailDTO.getFormElementPid()).get());
				filledFormDetails.add(filledFormDetail);
			}
			filledForm.setFilledFormDetails(filledFormDetails);
			filledForms.add(filledForm);
		});
		dynamicDocumentHeader.setFilledForms(filledForms);

		dynamicDocumentHeader = dynamicDocumentHeaderRepository.save(dynamicDocumentHeader);
		DynamicDocumentHeaderDTO result = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
		return result;
	}

	@Override
	public void update(DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO) {
		log.debug("Request to save update feedback documents");
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentHeaderDTO.getPid()).get();
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		dynamicDocumentHeader.setUpdatedBy(user);
		dynamicDocumentHeader.setUpdatedDate(LocalDateTime.now());

		// save history
		DynamicDocumentHeaderHistory dynamicDocumentHeaderHistory = new DynamicDocumentHeaderHistory(
				dynamicDocumentHeader);
		documentHeaderHistoryRepository.save(dynamicDocumentHeaderHistory);

		// update forms
		for (FilledForm filledForm : dynamicDocumentHeader.getFilledForms()) {
			for (FilledFormDTO filledFormDTO : dynamicDocumentHeaderDTO.getFilledForms()) {
				if (filledForm.getPid().equals(filledFormDTO.getPid())) {
					for (FilledFormDetail filledFormDetail : filledForm.getFilledFormDetails()) {
						for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
							if (filledFormDetail.getFormElement().getPid()
									.equals(filledFormDetailDTO.getFormElementPid())) {
								filledFormDetail.setValue(filledFormDetailDTO.getValue());
							}
						}
					}
					// set new form elements
					filledForm.getFilledFormDetails().addAll(findNewFormElements(filledForm.getFilledFormDetails(),
							filledFormDTO.getFilledFormDetails()));
				}
			}
		}
		// update filled form
		dynamicDocumentHeaderRepository.save(dynamicDocumentHeader);
	}

	/**
	 * @param filledFormDetails
	 * @param filledFormDetailDTOs
	 * @return
	 */
	private List<FilledFormDetail> findNewFormElements(List<FilledFormDetail> filledFormDetails,
			List<FilledFormDetailDTO> filledFormDetailDTOs) {
		List<FilledFormDetail> newFilledFormDetails = new ArrayList<>();
		for (FilledFormDetailDTO filledFormDetailDTO : filledFormDetailDTOs) {
			boolean isNew = true;
			for (FilledFormDetail filledFormDetail : filledFormDetails) {
				if (filledFormDetail.getFormElement().getPid().equals(filledFormDetailDTO.getFormElementPid())) {
					isNew = false;
					break;
				}
			}
			if (isNew) {
				FilledFormDetail filledFormDetail = new FilledFormDetail();
				filledFormDetail.setValue(filledFormDetailDTO.getValue());
				filledFormDetail.setFormElement(
						formElementRepository.findOneByPid(filledFormDetailDTO.getFormElementPid()).get());
				filledFormDetails.add(filledFormDetail);
				newFilledFormDetails.add(filledFormDetail);
			}
		}
		return newFilledFormDetails;
	}

	/**
	 * Get all the dynamicDocumentHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DynamicDocumentHeader> findAll(Pageable pageable) {
		log.debug("Request to get all DynamicDocumentHeaders");
		Page<DynamicDocumentHeader> result = dynamicDocumentHeaderRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the dynamicDocumentHeaders.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompany() {
		log.debug("Request to get all DynamicDocumentHeaders");
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc();
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the dynamicDocumentHeaders.
	 * 
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DynamicDocumentHeaderDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all DynamicDocumentHeaders");
		Page<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc(pageable);
		List<DynamicDocumentHeaderDTO> dynamicDocumentHeaderList = dynamicDocumentHeaders.getContent().stream()
				.map(DynamicDocumentHeaderDTO::new).collect(Collectors.toList());
		Page<DynamicDocumentHeaderDTO> result = new PageImpl<DynamicDocumentHeaderDTO>(dynamicDocumentHeaderList,
				pageable, dynamicDocumentHeaders.getTotalElements());
		return result;
	}

	/**
	 * Get one dynamicDocumentHeader by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DynamicDocumentHeaderDTO findOne(Long id) {
		log.debug("Request to get DynamicDocumentHeader : {}", id);
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository.findOne(id);
		DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
		dynamicDocumentHeaderDTO.setFilledForms(
				dynamicDocumentHeader.getFilledForms().stream().map(FilledFormDTO::new).collect(Collectors.toList()));
		return dynamicDocumentHeaderDTO;
	}

	/**
	 * Get one dynamicDocumentHeader by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DynamicDocumentHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get DynamicDocumentHeader by pid : {}", pid);
		return dynamicDocumentHeaderRepository.findOneByPid(pid).map(dynamicDocumentHeader -> {
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
					.map(FilledFormDTO::new).collect(Collectors.toList()));
			return dynamicDocumentHeaderDTO;
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetween(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
						fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdDocumentPidAndDateBetween(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(documentPid, fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdAndDocumentNameStatusFalseOrderByCreatedDateDesc(
			String documentName) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdAndDocumentNameAndStatusFalseOrderByCreatedDateDesc(documentName);
		List<DynamicDocumentHeaderDTO> result = new ArrayList<>();
		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
					.map(FilledFormDTO::new).collect(Collectors.toList()));
			result.add(dynamicDocumentHeaderDTO);
		}
		return result;
	}

	@Override
	public void updateDynamicDocumentStatus(DynamicDocumentHeaderDTO documentHeaderDTO) {
		DynamicDocumentHeader documentHeader = dynamicDocumentHeaderRepository.findOneByPid(documentHeaderDTO.getPid())
				.get();
		documentHeader.setStatus(true);
		dynamicDocumentHeaderRepository.save(documentHeader);
	}

	@Override
	public DynamicDocumentHeaderDTO findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(
			String executiveTaskExecutionPid, String documentName) {
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(executiveTaskExecutionPid, documentName);
		DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO();
		if (dynamicDocumentHeader != null) {
			dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			if (dynamicDocumentHeader.getFilledForms() != null) {
				List<FilledFormDTO> filledFormDTOs = new ArrayList<>();
				for (FilledForm filledForm : dynamicDocumentHeader.getFilledForms()) {
					FilledFormDTO filledFormDTO = new FilledFormDTO(filledForm);
					filledFormDTOs.add(filledFormDTO);
				}
				dynamicDocumentHeaderDTO.setFilledForms(filledFormDTOs);
			}

			return dynamicDocumentHeaderDTO;
		}
		return dynamicDocumentHeaderDTO;
	}

	@Override
	public Set<Document> findDocumentsByUserIdIn(List<Long> userIds) {
		return dynamicDocumentHeaderRepository.findDocumentsByUserIdIn(userIds);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findByFilledFormsIn(List<FilledForm> filledForms) {
		log.debug("Request to get all filledForms");
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findByFilledFormsIn(filledForms);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetweenSetFilledForm(
			String userPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
						fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = new ArrayList<>();
		if (!dynamicDocumentHeaders.isEmpty()) {
			for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
				DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
				dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
						.map(FilledFormDTO::new).collect(Collectors.toList()));
				result.add(dynamicDocumentHeaderDTO);
			}
		}
		return result;
	}

	@Override
	public List<DynamicDocumentHeaderDTO> findAllDynamicDocumentByExecutiveTaskExecutionPid(String pid) {
		Set<DynamicDocumentHeaderDTO> dynamics = new HashSet<>();
		List<DynamicDocumentHeader> documentHeaders = dynamicDocumentHeaderRepository
				.findAllByExecutiveTaskExecutionPid(pid);
		List<AccountProfileDynamicDocumentAccountprofile> acProfileDyDocumentAcprofiles = accountProfileDynamicDocumentAccountprofileRepository
				.findAllByCompany();
		for (AccountProfileDynamicDocumentAccountprofile accountProfileDynamicDocumentAccountprofile : acProfileDyDocumentAcprofiles) {
			for (DynamicDocumentHeader dynamicDocumentHeader : documentHeaders) {
				if (accountProfileDynamicDocumentAccountprofile.getDocument().getPid()
						.equals(dynamicDocumentHeader.getDocument().getPid())) {
					DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(
							dynamicDocumentHeader);
					dynamics.add(dynamicDocumentHeaderDTO);
				}
			}
		}
		return new ArrayList<>(dynamics);
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndTallyDownloadStatusAndDateBetween(
			String userPid, String documentPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate,
			LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(userPid,
						documentPid, tallyStatus, fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetween(String userPid,
			List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(userPid,
						tallyStatus, fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetween(
			String documentPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate) {
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(documentPid,
						tallyStatus, fromDate, toDate);
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateDynamicDocumentHeaderStatus(DynamicDocumentHeaderDTO dynamicDocumentDTO) {
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentDTO.getPid()).get();
		dynamicDocumentHeader.setStatus(true);
		dynamicDocumentHeader.setTallyDownloadStatus(dynamicDocumentDTO.getTallyDownloadStatus());
		dynamicDocumentHeaderRepository.save(dynamicDocumentHeader);

	}

}
