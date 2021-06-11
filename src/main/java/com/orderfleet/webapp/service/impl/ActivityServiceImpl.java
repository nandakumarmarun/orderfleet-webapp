package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ActivityDocument;
import com.orderfleet.webapp.domain.ActivityStage;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.enums.ContactManagement;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.ActivityDocumentRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.repository.ActivityStageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDocumentDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountTypeMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;

/**
 * Service Implementation for managing Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

	private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityMapper activityMapper;

	@Inject
	private AccountTypeRepository accountTypeRepository;

	@Inject
	private AccountTypeMapper accountTypeMapper;

	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private StageRepository stageRepository;

	@Inject
	private ActivityDocumentRepository activityDocumentRepository;
	
	@Inject
	private ActivityStageRepository activityStageRepository;
	
	/**
	 * Save a activity.
	 * 
	 * @param activityDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public ActivityDTO save(ActivityDTO activityDTO) {
		log.debug("Request to save Activity : {}", activityDTO);

		// set pid
		activityDTO.setPid(ActivityService.PID_PREFIX + RandomUtil.generatePid());
		Activity activity = activityMapper.activityDTOToActivity(activityDTO);
		//code added to fix issue of modern sales uploading
		if(activity.getContactManagement()==null) {
			activity.setContactManagement(ContactManagement.ENABLED);
		}
		// set company
		activity.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		activity = activityRepository.save(activity);
		ActivityDTO result = activityMapper.activityToActivityDTO(activity);
		return result;
	}

	@Override
	public void saveAssignedAccountTypes(String pid, String assignedAccountTypes) {
		log.debug("Request to save Assigned Account Types");
		Activity activity = activityRepository.findOneByPid(pid).get();
		String[] accountTypes = assignedAccountTypes.split(",");
		Set<AccountType> activityAccountTypes = new HashSet<AccountType>();
		for (String acountTypePid : accountTypes) {
			AccountType accountType = accountTypeRepository.findOneByPid(acountTypePid).get();
			activityAccountTypes.add(accountType);
		}
		activity.setActivityAccountTypes(activityAccountTypes);
		activityRepository.save(activity);
	}

	@Override
	public void saveAssignedDocuments(String pid, List<ActivityDocumentDTO> assignedDocuments) {
		Activity activity = activityRepository.findOneByPid(pid).get();
		List<ActivityDocument> listDocuments = new ArrayList<>();
		for (ActivityDocumentDTO activityDocumentDTO : assignedDocuments) {
			ActivityDocument activityDocument = new ActivityDocument();
			Document document = documentRepository.findOneByPid(activityDocumentDTO.getDocumentPid()).get();
			activityDocument.setDocument(document);
			activityDocument.setActivity(activity);
			activityDocument.setRequired(activityDocumentDTO.getRequired());
			activityDocument.setSortOrder(activityDocumentDTO.getSortOrder());
			listDocuments.add(activityDocument);
		}
		activityDocumentRepository.deleteByActivityPid(pid);
		activityDocumentRepository.flush();
		activityDocumentRepository.save(listDocuments);
	}
	
	@Override
	public void saveAssignedDocumentsWithCompany(String pid, List<ActivityDocumentDTO> assignedDocuments) {
		Activity activity = activityRepository.findOneByPid(pid).get();
		List<ActivityDocument> listDocuments = new ArrayList<>();
		for (ActivityDocumentDTO activityDocumentDTO : assignedDocuments) {
			ActivityDocument activityDocument = new ActivityDocument();
			Document document = documentRepository.findOneByPid(activityDocumentDTO.getDocumentPid()).get();
			activityDocument.setDocument(document);
			activityDocument.setActivity(activity);
			activityDocument.setRequired(activityDocumentDTO.getRequired());
			activityDocument.setSortOrder(activityDocumentDTO.getSortOrder());
			activityDocument.setCompany(document.getCompany());
			listDocuments.add(activityDocument);
		}
		activityDocumentRepository.deleteByActivityDocumentActivityId(activity.getId());
		activityDocumentRepository.flush();
		activityDocumentRepository.save(listDocuments);
	}
	
	@Override
	public void saveAssignedStages(String pid, String[] stagePids) {
		activityRepository.findOneByPid(pid).ifPresent(activity -> {
			List<ActivityStage> activityStages = new ArrayList<>();
			for (String stagePid : stagePids) {
				ActivityStage activityStage = new ActivityStage();
				Stage stage = stageRepository.findOneByPid(stagePid).get();
				activityStage.setStage(stage);
				activityStage.setActivity(activity);
				activityStage.setCompany(activity.getCompany());
				activityStages.add(activityStage);
			}
			activityStageRepository.deleteByActivityPid(pid);
			activityStageRepository.flush();
			activityStageRepository.save(activityStages);
		});
		
	}

	/**
	 * Update a activity.
	 * 
	 * @param activityDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public ActivityDTO update(ActivityDTO activityDTO) {
		log.debug("Request to Update Activity : {}", activityDTO);

		return activityRepository.findOneByPid(activityDTO.getPid()).map(activity -> {
			activity.setName(activityDTO.getName());
			activity.setAlias(activityDTO.getAlias());
			activity.setDescription(activityDTO.getDescription());
			activity.setHasDefaultAccount(activityDTO.getHasDefaultAccount());
			activity.setTargetDisplayOnDayplan(activityDTO.getTargetDisplayOnDayplan());
			activity.setCompletePlans(activityDTO.getCompletePlans());
			activity.setContactManagement(activityDTO.getContactManagement());
			activity.setHasSecondarySales(activityDTO.getHasSecondarySales());
			activity.setGeoFencing(activityDTO.getGeoFencing());
			activity.setHasTelephonicOrder(activityDTO.getHasTelephonicOrder());


			activity = activityRepository.save(activity);
			ActivityDTO result = activityMapper.activityToActivityDTO(activity);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Activity> findAll(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<Activity> result = activityRepository.findAll(pageable);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findAllByCompany() {
		log.debug("Request to get all Activities");
		List<Activity> activityList = activityRepository.findAllByCompanyIdOrderByName();
		List<ActivityDTO> result = activityMapper.activitiesToActivityDTOs(activityList);
		return result;
	}

	/**
	 * Get all the activities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ActivityDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Activities");
		Page<Activity> activities = activityRepository.findAllByCompanyId(pageable);
		Page<ActivityDTO> result = new PageImpl<ActivityDTO>(
				activityMapper.activitiesToActivityDTOs(activities.getContent()), pageable,
				activities.getTotalElements());
		return result;
	}

	/**
	 * Get one activity by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public ActivityDTO findOne(Long id) {
		log.debug("Request to get Activity : {}", id);
		Activity activity = activityRepository.findOne(id);
		ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
		return activityDTO;
	}

	/**
	 * Get one activity by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityDTO> findOneByPid(String pid) {
		log.debug("Request to get Activity by pid : {}", pid);
		return activityRepository.findOneByPid(pid).map(activity -> {
			ActivityDTO activityDTO = new ActivityDTO(activity);
			return activityDTO;
		});
	}

	/**
	 * Get one activity by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityDTO> findByName(String name) {
		log.debug("Request to get Activity by name : {}", name);
		return activityRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(activity -> {
					ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
					return activityDTO;
				});
	}

	/**
	 * Delete the activity by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Activity : {}", pid);
		activityRepository.findOneByPid(pid).ifPresent(activity -> {
			activityRepository.delete(activity.getId());
		});
	}

	@Override
	@Transactional(readOnly = true)
	public List<AccountTypeDTO> findActivityAccountTypesByPid(String pid) {
		List<AccountType> accountTypes = activityRepository.findActivityAccountTypesByPid(pid);
		List<AccountTypeDTO> result = accountTypeMapper.accountTypesToAccountTypeDTOs(accountTypes);
		return result;
	}

	/**
	 * Update the activity status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public ActivityDTO updateActivityStatus(String pid, boolean active) {
		log.debug("Request to update Activity  status: {}", pid);
		return activityRepository.findOneByPid(pid).map(activity -> {
			if (!active) {
				Set<AccountType> activityAccountTypes = null;
				activityDocumentRepository.deleteByActivityPid(pid);
				activityDocumentRepository.flush();
				activity.setActivityAccountTypes(activityAccountTypes);
			}
			activity.setActivated(active);
			activity = activityRepository.save(activity);
			ActivityDTO result = activityMapper.activityToActivityDTO(activity);
			return result;
		}).orElse(null);
	}

	@Override
	public Page<ActivityDTO> findAllByCompanyAndActivatedActivityOrderByName(Pageable pageable, boolean active) {
		Page<Activity> pageActivity = activityRepository.findAllByCompanyIdAndActivatedActivityOrderByName(pageable,
				active);
		Page<ActivityDTO> pageActivityDTO = new PageImpl<ActivityDTO>(
				activityMapper.activitiesToActivityDTOs(pageActivity.getContent()), pageable,
				pageActivity.getTotalElements());
		return pageActivityDTO;
	}

	@Override
	public List<ActivityDTO> findAllByCompanyAndDeactivatedActivity(boolean deactive) {
		List<Activity> activities = activityRepository.findAllByCompanyIdAndActivatedOrDeactivatedActivity(deactive);
		List<ActivityDTO> activityDTOs = activityMapper.activitiesToActivityDTOs(activities);
		return activityDTOs;
	}

	@Override
	public List<ActivityDTO> findAllByCompanyPid(String companyPid) {
		log.debug("Request to get all Activities by companyPid");
		List<Activity> activityList = activityRepository.findAllByCompanyPid(companyPid);
		List<ActivityDTO> result = activityMapper.activitiesToActivityDTOs(activityList);
		return result;
	}

	@Override
	public ActivityDTO saveFormSAdmin(ActivityDTO activityDTO) {
		log.debug("Request to save Activity : {}", activityDTO);
		Optional<Company> opCompany = companyRepository.findOneByPid(activityDTO.getCompanyPid());
		if (opCompany.isPresent()) {
			// set pid
			activityDTO.setPid(ActivityService.PID_PREFIX + RandomUtil.generatePid());
			Activity activity = activityMapper.activityDTOToActivity(activityDTO);
			// set company
			activity.setCompany(opCompany.get());
			activity = activityRepository.save(activity);
			ActivityDTO result = activityMapper.activityToActivityDTO(activity);
			return result;
		}
		return null;
	}
	
	/**
	 * Get one activity by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<ActivityDTO> findByNameAndCompanyPid(String name,String companyPid) {
		log.debug("Request to get Activity by name : {}", name);
		return activityRepository.findByCompanyPidAndNameIgnoreCase(companyPid, name)
				.map(activity -> {
					ActivityDTO activityDTO = activityMapper.activityToActivityDTO(activity);
					return activityDTO;
				});
	}
}
