package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountProfileDynamicDocumentAccountprofile;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.AccountProfileDynamicDocumentAccountprofileRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
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
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentHeaderDTO.getPid()).get();
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all document by company Id";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc();
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all document by company Id and order by create date using page";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Page<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdOrderByCreatedDateDesc(pageable);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<DynamicDocumentHeaderDTO> ddhDTO = dynamicDocumentHeaderRepository.findOneByPid(pid)
				.map(dynamicDocumentHeader -> {
					DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(
							dynamicDocumentHeader);
					dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
							.map(FilledFormDTO::new).collect(Collectors.toList()));
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
					logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag
							+ "," + description);

					return dynamicDocumentHeaderDTO;
				});
		return ddhDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate,
			LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all document by company id and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(fromDate, toDate);
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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());

		boolean comp = getCompanyCofig();

		result.stream().forEach(ddh -> {
			if (comp) {
				ddh.setAccountName(ddh.getDescription());
			} else {
				ddh.setAccountName(ddh.getAccountName());
			}
		});
		return result;
	}

	public boolean getCompanyCofig() {
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository
				.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if (optconfig.isPresent()) {
			if (Boolean.valueOf(optconfig.get().getValue())) {
				return true;
			}
		}
		return false;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetween(String userPid,
			String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all document by company id UserPid documentPid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
						fromDate, toDate);
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

		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();

		result.stream().forEach(ddh -> {
			if (comp) {
				ddh.setAccountName(ddh.getDescription());
			} else {
				ddh.setAccountName(ddh.getAccountName());
			}
		});

		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_105" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by company id  user pid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(userPid, fromDate, toDate);
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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();

		result.stream().forEach(ddh -> {
			if (comp) {
				ddh.setAccountName(ddh.getDescription());
			} else {
				ddh.setAccountName(ddh.getAccountName());
			}
		});
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdDocumentPidAndDateBetween(String documentPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_106" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all document by company id document pid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(documentPid, fromDate, toDate);

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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		boolean comp = getCompanyCofig();

		result.stream().forEach(ddh -> {
			if (comp) {
				ddh.setAccountName(ddh.getDescription());
			} else {
				ddh.setAccountName(ddh.getAccountName());
			}
		});
		return result;
	}

	@Override
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdAndDocumentNameStatusFalseOrderByCreatedDateDesc(
			String documentName) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_113" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by companyId doc name and status false";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdAndDocumentNameAndStatusFalseOrderByCreatedDateDesc(documentName);

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_116" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get doc by executive task execution pid doc name ";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findByExecutiveTaskExecutionPidAndDocumentNameAndStatusFalse(executiveTaskExecutionPid, documentName);

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_125" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get the documents by UserId in";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		Set<Document> documents = dynamicDocumentHeaderRepository.findDocumentsByUserIdIn(userIds);

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
		return documents;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findByFilledFormsIn(List<FilledForm> filledForms) {
		log.debug("Request to get all filledForms");
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_132" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get documents by filled forms in";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findByFilledFormsIn(filledForms);

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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidDocumentPidAndDateBetweenSetFilledForm(
			String userPid, String documentPid, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all document by company id UserPid documentPid and date between";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);

		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
						fromDate, toDate);

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_133" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all documents by executive task execution pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> documentHeaders = dynamicDocumentHeaderRepository
				.findAllByExecutiveTaskExecutionPid(pid);
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
		DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id1 = "APDD_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description1 = "get all by company";
		LocalDateTime startLCTime1 = LocalDateTime.now();
		String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
		String startDate1 = startLCTime1.format(DATE_FORMAT1);
		logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
		List<AccountProfileDynamicDocumentAccountprofile> acProfileDyDocumentAcprofiles = accountProfileDynamicDocumentAccountprofileRepository
				.findAllByCompany();
		String flag1 = "Normal";
		LocalDateTime endLCTime1 = LocalDateTime.now();
		String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
		String endDate1 = startLCTime1.format(DATE_FORMAT1);
		Duration duration1 = Duration.between(startLCTime1, endLCTime1);
		long minutes1 = duration1.toMinutes();
		if (minutes1 <= 1 && minutes1 >= 0) {
			flag1 = "Fast";
		}
		if (minutes1 > 1 && minutes1 <= 2) {
			flag1 = "Normal";
		}
		if (minutes1 > 2 && minutes1 <= 10) {
			flag1 = "Slow";
		}
		if (minutes1 > 10) {
			flag1 = "Dead Slow";
		}
		logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
				+ description1);
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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_107" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by document by companyid userPid documentPid Tally download status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidDocumentPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(userPid,
						documentPid, tallyStatus, fromDate, toDate);
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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetween(String userPid,
			List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_108" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by document by company id userPid Tally download status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(userPid,
						tallyStatus, fromDate, toDate);
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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<DynamicDocumentHeaderDTO> findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetween(
			String documentPid, List<TallyDownloadStatus> tallyStatus, LocalDateTime fromDate, LocalDateTime toDate) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get all by document by company id documentPid and Tally download status";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetweenOrderByCreatedDateDesc(documentPid,
						tallyStatus, fromDate, toDate);
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
		List<DynamicDocumentHeaderDTO> result = dynamicDocumentHeaders.stream().map(DynamicDocumentHeaderDTO::new)
				.collect(Collectors.toList());
		return result;
	}

	@Override
	public void updateDynamicDocumentHeaderStatus(DynamicDocumentHeaderDTO dynamicDocumentDTO) {
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "DYN_QUERY_146" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description = "get one by Pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentDTO.getPid()).get();
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

		dynamicDocumentHeader.setStatus(true);
		dynamicDocumentHeader.setTallyDownloadStatus(dynamicDocumentDTO.getTallyDownloadStatus());
		dynamicDocumentHeaderRepository.save(dynamicDocumentHeader);

	}

}
