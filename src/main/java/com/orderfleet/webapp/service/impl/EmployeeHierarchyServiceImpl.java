package com.orderfleet.webapp.service.impl;

import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
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

import com.orderfleet.webapp.domain.EmployeeHierarchy;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EmployeeHierarchyRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.web.rest.dto.EmployeeHierarchyDTO;
import com.orderfleet.webapp.web.rest.mapper.EmployeeHierarchyMapper;

/**
 * Service Implementation for managing EmployeeHierarchy.
 * 
 * @author Shaheer
 * @since June 02, 2016
 */
@Service
@Transactional
public class EmployeeHierarchyServiceImpl implements EmployeeHierarchyService {

	private final Logger log = LoggerFactory.getLogger(EmployeeHierarchyServiceImpl.class);

	@Inject
	private EmployeeHierarchyRepository employeeHierarchyRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyMapper employeeHierarchyMapper;

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private UserRepository userRepository;

	/**
	 * Save a employeeHierarchy.
	 * 
	 * @param employeeHierarchyDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	public EmployeeHierarchyDTO save(EmployeeHierarchyDTO employeeHierarchyDTO) {
		log.debug("Request to save EmployeeHierarchy : {}", employeeHierarchyDTO);
		EmployeeHierarchy employeeHierarchy = employeeHierarchyMapper
				.employeeHierarchyDTOToEmployeeHierarchy(employeeHierarchyDTO);
		employeeHierarchy = employeeHierarchyRepository.save(employeeHierarchy);
		EmployeeHierarchyDTO result = employeeHierarchyMapper
				.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchy);
		return result;
	}

	/**
	 * Save a employeeHierarchy.
	 * 
	 * @param employeePid
	 * @param parentPid
	 * @return the persisted entity
	 */
	public EmployeeHierarchyDTO save(String employeePid, String parentPid) {
			log.debug("Request to save EmployeeHierarchy : {}", employeePid);
			EmployeeHierarchy employeeHierarchy = new EmployeeHierarchy();
			EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByPid(employeePid);
			EmployeeProfile parent = employeeProfileRepository.findEmployeeProfileByPid(parentPid);
			employeeHierarchy.setParent(parent);
			employeeHierarchy.setEmployee(employee);
			
			employeeHierarchy.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
			employeeHierarchy = employeeHierarchyRepository.save(employeeHierarchy);
			EmployeeHierarchyDTO result = employeeHierarchyMapper
					.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchy);
			return result;
		}
	/**
	 * Save a employeeHierarchyRoot.
	 * 
	 * @param employeePid
	 * @param parentPid
	 * @return the persisted entity
	 */
	public EmployeeHierarchyDTO saveRoot(String employeePid, String parentPid) {
		log.debug("Request to save EmployeeHierarchy : {}", employeePid);
		EmployeeHierarchy employeeHierarchy = new EmployeeHierarchy();
		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByPid(employeePid);
		if(parentPid==null){
		EmployeeProfile parent = employeeProfileRepository.findEmployeeProfileByPid(parentPid);
		employeeHierarchy.setParent(parent);
		}else{
			employeeHierarchy.setParent(null);
		}
		
		employeeHierarchy.setEmployee(employee);
		
		employeeHierarchy.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		employeeHierarchy = employeeHierarchyRepository.save(employeeHierarchy);
		EmployeeHierarchyDTO result = employeeHierarchyMapper
				.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchy);
		return result;
	}

	/**
	 * update a employeeHierarchy.
	 * 
	 * @param employeeHierarchyDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public EmployeeHierarchyDTO update(String employeePid, String parentPid) {
		log.debug("Request to Update EmployeeHierarchyDTO by employeePid: {}", employeePid);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfile> employee = employeeProfileRepository.findOneByPid(employeePid);
		Optional<EmployeeProfile> parentEmployee = employeeProfileRepository.findOneByPid(parentPid);
		if (!employee.isPresent() || !parentEmployee.isPresent()) {
			return null;
		}

		// Assume there will be one parent for an employee
		return employeeHierarchyRepository
				.findByEmployeeIdAndCompanyIdAndActivatedTrue(employee.get().getId(), companyId)
				.map(employeeHierarchy -> {
					// update
					employeeHierarchy.setActivated(false);
					employeeHierarchy.setInactivatedDate(ZonedDateTime.now());
					employeeHierarchy = employeeHierarchyRepository.save(employeeHierarchy);

					// save
					EmployeeHierarchy employeeHierarchyEntity = new EmployeeHierarchy();
					employeeHierarchyEntity.setCompany(employeeHierarchy.getCompany());
					employeeHierarchyEntity.setEmployee(employee.get());
					employeeHierarchyEntity.setParent(parentEmployee.get());
					employeeHierarchyEntity = employeeHierarchyRepository.save(employeeHierarchyEntity);
					EmployeeHierarchyDTO result = employeeHierarchyMapper
							.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchyEntity);

					return result;
				}).orElse(null);
	}

	/**
	 * Get all the employeeHierarchies.
	 * 
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	@Override
	public List<EmployeeHierarchyDTO> findAllByCompanyAndActivatedTrue() {
		log.debug("Request to get all EmployeeHierarchies");
		List<EmployeeHierarchy> employeesHierarchies = employeeHierarchyRepository
				.findByCompanyIdAndActivatedTrue();
		List<EmployeeHierarchyDTO> result = employeeHierarchyMapper
				.employeeHierarchiesToEmployeeHierarchyDTOs(employeesHierarchies);
		return result;
	}

	/**
	 * Get all the employeeHierarchies.
	 * 
	 * @return the list of entities
	 */
	@Transactional(readOnly = true)
	@Override
	public Page<EmployeeHierarchyDTO> findAllByCompanyAndActivatedTrue(Pageable pageable) {
		log.debug("Request to get all EmployeeHierarchies");
		Page<EmployeeHierarchy> employeesHierarchies = employeeHierarchyRepository
				.findByCompanyIdAndActivatedTrue(pageable);
		Page<EmployeeHierarchyDTO> result = new PageImpl<>(
				employeeHierarchyMapper.employeeHierarchiesToEmployeeHierarchyDTOs(employeesHierarchies.getContent()),
				pageable, employeesHierarchies.getTotalElements());
		return result;
	}

	/**
	 * Get one employeeHierarchy by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Transactional(readOnly = true)
	public EmployeeHierarchyDTO findOne(Long id) {
		log.debug("Request to get EmployeeHierarchy : {}", id);
		EmployeeHierarchy employeeHierarchy = employeeHierarchyRepository.findOne(id);
		EmployeeHierarchyDTO employeeHierarchyDTO = employeeHierarchyMapper
				.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchy);
		return employeeHierarchyDTO;
	}

	public Long countByParentIdAndActivatedTrue(String parentId) {
		log.debug("Request to get count of children by pid : {}", parentId);
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		return employeeProfileRepository.findOneByPid(parentId).map(employee -> {
			Long count = employeeHierarchyRepository.countByParentIdAndCompanyIdAndActivatedTrue(employee.getId(),
					companyId);
			if (count > 0) {
				return count;
			} else {
				return null;
			}
		}).orElse(null);
	}

	@Override
	public EmployeeHierarchyDTO findOneByEmployeeIdAndActivatedTrue(String employeePid) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Optional<EmployeeProfile> employee = employeeProfileRepository.findOneByPid(employeePid);
		if (!employee.isPresent()) {
			return null;
		}

		return employeeHierarchyRepository
				.findByEmployeeIdAndCompanyIdAndActivatedTrue(employee.get().getId(), companyId)
				.map(employeeHierarchy -> {
					return employeeHierarchyMapper.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchy);
				}).orElse(null);
	}

	@Override
	public void inactivateEmployeeHierarchy(String employeePid) {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		employeeProfileRepository.findOneByPid(employeePid).ifPresent(employee -> {
			employeeHierarchyRepository.findByEmployeeIdAndCompanyIdAndActivatedTrue(employee.getId(), companyId)
					.ifPresent(employeeHierarchy -> {
						// update
						employeeHierarchy.setActivated(false);
						employeeHierarchy.setInactivatedDate(ZonedDateTime.now());
						employeeHierarchy = employeeHierarchyRepository.save(employeeHierarchy);
					});
		});
	}
	
	@Override
	public List<Long> getCurrentUsersSubordinateIds() {
		String login = SecurityUtils.getCurrentUserLogin();
		log.info("User :"+login);
		Optional<User> optionalUser = userRepository.findOneByLogin(login);
		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByUserLogin(login);
		//log.info("Employee Profile"+employee);
		if (optionalUser.isPresent() && !optionalUser.get().getShowAllUsersData() && employee != null) {
			// get users from employee hierarchy
			List<Object> result = employeeHierarchyRepository.findChildrenByEmployeeIdAndActivatedTrue(employee.getId());
			log.info("Hierarchy result size :"+result.size());
			List<Long> employeeIds= new ArrayList<>();
			for (Object object : result) {
				employeeIds.add(((BigInteger)object).longValue());
			}
			log.info("Subordinate employee size :"+employeeIds.size());
			if(!employeeIds.isEmpty()){
				return employeeProfileRepository.findUserIdByEmployeeIdIn(employeeIds);	
			}
		}
		return Collections.emptyList();
	}
	
	@Override
	public List<Long> getEmployeeSubordinateIds(String employeePid) {
		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByPid(employeePid);
		if (employee != null) {
			// get users from employee hierarchy
			List<Object> result = employeeHierarchyRepository.findChildrenByEmployeeIdAndActivatedTrue(employee.getId());
			List<Long> employeeIds= new ArrayList<>();
			employeeIds.add(employee.getId());
			for (Object object : result) {
				employeeIds.add(((BigInteger)object).longValue());
			}
			if(!employeeIds.isEmpty()){
				return employeeProfileRepository.findUserIdByEmployeeIdIn(employeeIds);	
			}
		}
		return Collections.emptyList();
	}

	
	@Override
	public List<Long> getEmployeeSubordinateEmployeeIds(String employeePid) {
		EmployeeProfile employee = employeeProfileRepository.findEmployeeProfileByPid(employeePid);
		if (employee != null) {
			// get users from employee hierarchy
			List<Object> result = employeeHierarchyRepository.findChildrenByEmployeeIdAndActivatedTrue(employee.getId());
			List<Long> employeeIds= new ArrayList<>();
			employeeIds.add(employee.getId());
			for (Object object : result) {
				employeeIds.add(((BigInteger)object).longValue());
			}
			return employeeIds;
		}
		return Collections.emptyList();
	}
	
	/**
	 * Delete the employeeHierarchy by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(Long id) {
		log.debug("Request to delete EmployeeHierarchy : {}", id);
		employeeHierarchyRepository.delete(id);
	}

	@Override
	public EmployeeHierarchyDTO findEmployeeHierarchyRootByCompany() {
		EmployeeHierarchy employeeHierarchy = employeeHierarchyRepository.findEmployeeHierarchyRootByCompany();
		EmployeeHierarchyDTO employeeHierarchyDTO = employeeHierarchyMapper
				.employeeHierarchyToEmployeeHierarchyDTO(employeeHierarchy);
		return employeeHierarchyDTO;
	}

	@Override
	public void deleteByEmployeePid(String employeePid) {
		employeeHierarchyRepository.deleteByEmployeePid(employeePid);		
	}



}
