package com.orderfleet.webapp.service.impl;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.mapper.EmployeeProfileMapper;

/**
 * Service Implementation for managing EmployeeProfile.
 * 
 * @author Shaheer
 * @since June 06, 2016
 */
@Service
@Transactional
public class EmployeeProfileServiceImpl implements EmployeeProfileService {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileServiceImpl.class);

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeProfileMapper employeeProfileMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private UserRepository userRepository;

	/**
	 * Save a employeeProfile.
	 * 
	 * @param employeeProfileDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	public EmployeeProfileDTO save(EmployeeProfileDTO employeeProfileDTO) {
		log.debug("Request to save EmployeeProfile : {}", employeeProfileDTO);
		employeeProfileDTO.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
		EmployeeProfile employeeProfile = employeeProfileMapper.employeeProfileDTOToEmployeeProfile(employeeProfileDTO);
		// set company
		employeeProfile.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		employeeProfile = employeeProfileRepository.save(employeeProfile);
		return employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile);
	}

	/**
	 * Update a employeeProfile.
	 * 
	 * @param employeeProfileDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public EmployeeProfileDTO update(EmployeeProfileDTO employeeProfileDTO) {
		log.debug("Request to Update Employee Profile : {}", employeeProfileDTO);
		return employeeProfileRepository.findOneByPid(employeeProfileDTO.getPid()).map(employeeProfile -> {
			employeeProfile.setName(employeeProfileDTO.getName());
			employeeProfile.setAlias(employeeProfileDTO.getAlias());
			employeeProfile
					.setDesignation(designationRepository.findOneByPid(employeeProfileDTO.getDesignationPid()).get());
			employeeProfile
					.setDepartment(departmentRepository.findOneByPid(employeeProfileDTO.getDepartmentPid()).get());
			employeeProfile.setEmail(employeeProfileDTO.getEmail());
			employeeProfile.setPhone(employeeProfileDTO.getPhone());
			employeeProfile.setAddress(employeeProfileDTO.getAddress());
			employeeProfile.setOrgEmpId(employeeProfileDTO.getOrgEmpId());
            employeeProfile.setProfileImage(employeeProfileDTO.getProfileImage());
			employeeProfile = employeeProfileRepository.save(employeeProfile);
			return employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile);
		}).orElse(null);
	}

	/**
	 * Get all the employeeProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	public Page<EmployeeProfileDTO> findAll(Pageable pageable) {
		log.debug("Request to get all EmployeeProfiles by page");
		Page<EmployeeProfile> employees = employeeProfileRepository.findAll(pageable);
		return new PageImpl<>(
				employeeProfileMapper.employeeProfilesToEmployeeProfileDTOs(employees.getContent()), pageable,
				employees.getTotalElements());
	}

	@Override
	@Transactional(readOnly = true)
	public List<EmployeeProfileDTO> findAll() {
		log.debug("Request to get all EmployeeProfiles");
		List<EmployeeProfile> employeeProfileList = employeeProfileRepository.findAll();
		return employeeProfileMapper
				.employeeProfilesToEmployeeProfileDTOs(employeeProfileList);
	}

	/**
	 * Get all the employeeProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<EmployeeProfileDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all EmployeeProfiles");
		Page<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyId(pageable);
		return new PageImpl<>(
				employeeProfileMapper.employeeProfilesToEmployeeProfileDTOs(employeeProfiles.getContent()), pageable,
				employeeProfiles.getTotalElements());
	}

	/**
	 * Get all the employeeProfiles.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<EmployeeProfileDTO> findAllByCompany() {
		log.debug("Request to get all EmployeeProfiles by company");
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findAllByCompanyId(true);
		return employeeProfileMapper.employeeProfilesToEmployeeProfileDTOs(employeeProfiles);
	}

	/**
	 * Get one employeeProfile by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public EmployeeProfileDTO findOne(Long id) {
		log.debug("Request to get EmployeeProfile : {}", id);
		/*
		 * EmployeeProfile employeeProfile =
		 * employeeProfileRepository.findOneWithEagerRelationships(id);
		 * EmployeeProfileDTO employeeProfileDTO = employeeProfileMapper
		 * .employeeProfileToEmployeeProfileDTO(employeeProfile);
		 */
		return null;
	}

	/**
	 * Get one employeeProfile by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EmployeeProfileDTO> findOneByPid(String pid) {
		log.debug("Request to get EmployeeProfile by pid : {}", pid);
		return employeeProfileRepository.findOneByPid(pid).map(employeeProfile -> employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile));
	}

	/**
	 * Get one employeeProfile by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<EmployeeProfileDTO> findByName(String name) {
		log.debug("Request to get EmployeeProfile by name : {}", name);
		return employeeProfileRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(employeeProfile -> employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile));
	}

	/**
	 * Delete the employeeProfile by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete EmployeeProfile : {}", pid);
		employeeProfileRepository.findOneByPid(pid).ifPresent(employeeProfile -> employeeProfileRepository.delete(employeeProfile.getId()));
	}

	/**
	 * Delete the employeeProfile by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	@Override
	public void delete(Long id) {
		log.debug("Request to delete EmployeeProfile : {}", id);
		employeeProfileRepository.delete(id);
	}

	@Override
	public Optional<EmployeeProfileDTO> saveEmployeeUser(String employeePid, String userPid) {
		log.debug("Request to save EmployeeProfile : {} and user {}", employeePid, userPid);
		Optional<User> user = userRepository.findOneByPid(userPid);
		return employeeProfileRepository.findOneByPid(employeePid).map(employeeProfile1 -> {
			employeeProfile1.setUser(user.get());
			employeeProfile1 = employeeProfileRepository.save(employeeProfile1);
			return employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile1);
		});
	}

	@Override
	public Optional<EmployeeProfile> findByUserPid(String userPid) {
		return employeeProfileRepository.findByUserPid(userPid);
	}

	@Override
	public EmployeeProfileDTO findtCurrentUserEmployeeProfileImage() {
		byte[] profileImage = employeeProfileRepository.findtCurrentUserEmployeeProfileImage();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		employeeProfileDTO.setProfileImage(profileImage);
		return employeeProfileDTO;
	}

	@Override
	public EmployeeProfileDTO findEmployeeProfileByUserLogin(String userLogin) {
		EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUserLogin(userLogin);
		if (employeeProfile != null) {
			return employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile);
		}
		return null;
	}

	/**
	 * @author Fahad
	 * @since Feb 07, 2017
	 * 
	 *        Update the EmployeeProfile status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public EmployeeProfileDTO updateEmployeeProfileStatus(String pid, boolean activate) {
		log.debug("Request to update EmployeeProfile status: {}");
		return employeeProfileRepository.findOneByPid(pid).map(employeeProfile -> {
			employeeProfile.setActivated(activate);
			employeeProfile = employeeProfileRepository.save(employeeProfile);
			return employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile);
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 07, 2017
	 * 
	 *        get the EmployeeProfile order by name.
	 * 
	 * @param pageable
	 *            the pageable of the entity
	 * @return the Page of entity
	 */
	@Override
	public Page<EmployeeProfileDTO> findAllByCompanyOrderByEmployeeProfileName(Pageable pageable) {
		Page<EmployeeProfile> employeePage = employeeProfileRepository
				.findAllByCompanyIdOrderByEmployeeProfileName(pageable);
		return new PageImpl<>(
				employeeProfileMapper.employeeProfilesToEmployeeProfileDTOs(employeePage.getContent()), pageable,
				employeePage.getTotalElements());
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 *
	 * @param pageable
	 *            the pageable of the entity
	 * @return the entity
	 */
	public Page<EmployeeProfileDTO> findAllByCompanyAndActivatedEmployeeProfileOrderByName(Pageable pageable,
			boolean active) {
		log.debug("Request to get Activated EmployeeProfile  : {}");
		Page<EmployeeProfile> pageEmployeeProfile = employeeProfileRepository
				.findAllByCompanyAndActivatedEmployeeProfileOrderByName(pageable, active);
		return new PageImpl<>(
				employeeProfileMapper.employeeProfilesToEmployeeProfileDTOs(pageEmployeeProfile.getContent()), pageable,
				pageEmployeeProfile.getTotalElements());
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all deactive company
	 * 
	 * @param deactive
	 *            the deactive of the entity
	 * @return the list
	 */
	@Override
	public List<EmployeeProfileDTO> findAllByCompanyAndDeactivatedEmployeeProfile(boolean deactive) {
		log.debug("Request to get Deactivated EmployeeProfile  : {}");
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository
				.findAllByCompanyAndDeativatedEmployeeProfile(deactive);
		return employeeProfileMapper
				.employeeProfilesToEmployeeProfileDTOs(employeeProfiles);
	}

	@Override
	public void copyEmployeeUser(String fromUserPid, List<String> toUserPids) {
		Optional<EmployeeProfile> optionalEmployee = employeeProfileRepository.findByUserPid(fromUserPid);
		List<EmployeeProfile> savedEmployeeProfiles = employeeProfileRepository.findByUserPidIn(toUserPids);
		if (optionalEmployee.isPresent()) {
			List<User> users = userRepository.findByUserPidIn(toUserPids);
			for (User user : users) {
				// if already assigned don't change, else
				if (savedEmployeeProfiles.stream().noneMatch(e -> e.getUser().getId().equals(user.getId()))) {
					EmployeeProfile newEmployeeProfile = new EmployeeProfile();
					newEmployeeProfile.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
					newEmployeeProfile.setName(user.getFirstName() == null ? "Default Name" : user.getFirstName());
					newEmployeeProfile.setAddress(optionalEmployee.get().getAddress());
					newEmployeeProfile.setCreatedDate(ZonedDateTime.now());
					newEmployeeProfile.setCompany(optionalEmployee.get().getCompany());
					newEmployeeProfile.setDesignation(optionalEmployee.get().getDesignation());
					newEmployeeProfile.setDepartment(optionalEmployee.get().getDepartment());
					//set user
					newEmployeeProfile.setUser(user);
					employeeProfileRepository.save(newEmployeeProfile);
				}
			}
		}
	}

	@Override
	public List<EmployeeProfileDTO> findAllEmployeeByUserIdsIn(List<Long> userIds) {
		List<EmployeeProfile>employeeProfiles=employeeProfileRepository.findByUserIdIn(userIds);
		employeeProfiles.sort((EmployeeProfile e1,EmployeeProfile e2)->e1.getName().compareToIgnoreCase(e2.getName()));
		return employeeProfileMapper
				.employeeProfilesToEmployeeProfileDTOs(employeeProfiles);
	}
	


	@Override
	public List<EmployeeProfileDTO> findAllEmployeeByUserIdsInAndActivated(List<Long> userPids) {
		List<EmployeeProfile>employeeProfiles=employeeProfileRepository.findByUserIdInAndActivated(userPids, true);
		employeeProfiles.sort((EmployeeProfile e1,EmployeeProfile e2)->e1.getName().compareToIgnoreCase(e2.getName()));
		return employeeProfileMapper
				.employeeProfilesToEmployeeProfileDTOs(employeeProfiles);
	}

	@Override
	public List<EmployeeProfileDTO> findAllByCompanyIdNotInUserIds(boolean activate, List<Long> toUserPids) {
		List<EmployeeProfile>employeeProfiles=employeeProfileRepository.findAllByCompanyIdNotInUserIds(activate, toUserPids);
		employeeProfiles.sort((EmployeeProfile e1,EmployeeProfile e2)->e1.getName().compareToIgnoreCase(e2.getName()));
		return employeeProfileMapper
				.employeeProfilesToEmployeeProfileDTOs(employeeProfiles);
	}

	@Override
	public List<EmployeeProfileDTO> findByEmployeeByUserPidIn(List<String> userPids) {
		List<EmployeeProfileDTO> employeeProfileDTOs = new ArrayList<>();
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findByUserPidInOrderByNameAsc(userPids);
		employeeProfiles.forEach(emp -> {
			EmployeeProfileDTO employeeProfileDto = new EmployeeProfileDTO();
			employeeProfileDto.setName(emp.getName());
			employeeProfileDto.setUserPid(emp.getUser().getPid());
			employeeProfileDTOs.add(employeeProfileDto);
		});
		return employeeProfileDTOs;
	}

	@Override
	public EmployeeProfileDTO findDtoByUserPid(String userPid) {
		Optional<EmployeeProfile> employeeProfile =  employeeProfileRepository.findByUserPid(userPid);
		if (employeeProfile.isPresent()) {
			return employeeProfileMapper.employeeProfileToEmployeeProfileDTO(employeeProfile.get());
		}
		return null;
	}

	@Override
	public EmployeeProfile updateEmployee(EmployeeProfile employeeProfile) {
		
		return employeeProfileRepository.save(employeeProfile);
	}

}
