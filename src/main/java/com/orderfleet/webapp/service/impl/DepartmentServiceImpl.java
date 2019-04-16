package com.orderfleet.webapp.service.impl;

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

import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DepartmentService;
import com.orderfleet.webapp.service.util.RandomUtil;

import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;
import com.orderfleet.webapp.web.rest.mapper.DepartmentMapper;

/**
 * Service Implementation for managing Department.
 * 
 * @author Muhammed Riyas T
 * @since May 24, 2016
 */
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {

	private final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DepartmentMapper departmentMapper;

	/**
	 * Save a department.
	 * 
	 * @param departmentDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public DepartmentDTO save(DepartmentDTO departmentDTO) {
		log.debug("Request to save Department : {}", departmentDTO);

		// set pid
		departmentDTO.setPid(DepartmentService.PID_PREFIX + RandomUtil.generatePid());
		Department department = departmentMapper.departmentDTOToDepartment(departmentDTO);
		// set company
		department.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		department = departmentRepository.save(department);
		DepartmentDTO result = departmentMapper.departmentToDepartmentDTO(department);
		return result;
	}

	/**
	 * Update a department.
	 * 
	 * @param departmentDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public DepartmentDTO update(DepartmentDTO departmentDTO) {
		log.debug("Request to Update Department : {}", departmentDTO);
		return departmentRepository.findOneByPid(departmentDTO.getPid()).map(department -> {
			department.setName(departmentDTO.getName());
			department.setAlias(departmentDTO.getAlias());
			department.setDescription(departmentDTO.getDescription());
			department = departmentRepository.save(department);
			DepartmentDTO result = departmentMapper.departmentToDepartmentDTO(department);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the departments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Department> findAll(Pageable pageable) {
		log.debug("Request to get all Departments");
		Page<Department> result = departmentRepository.findAll(pageable);
		return result;
	}

	@Override
	public List<DepartmentDTO> findAllByCompany() {
		log.debug("Request to get all Departments");
		List<Department> departmentList = departmentRepository.findAllByCompanyId();
		List<DepartmentDTO> result = departmentMapper.departmentsToDepartmentDTOs(departmentList);
		return result;
	}

	/**
	 * Get all the departments.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<DepartmentDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all Departments");
		Page<Department> departments = departmentRepository.findAllByCompanyIdOrderByDepartmentName(pageable);
		Page<DepartmentDTO> result = new PageImpl<DepartmentDTO>(
				departmentMapper.departmentsToDepartmentDTOs(departments.getContent()), pageable,
				departments.getTotalElements());
		return result;
	}

	/**
	 * Get one department by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public DepartmentDTO findOne(Long id) {
		log.debug("Request to get Department : {}", id);
		Department department = departmentRepository.findOne(id);
		DepartmentDTO departmentDTO = departmentMapper.departmentToDepartmentDTO(department);
		return departmentDTO;
	}

	/**
	 * Get one department by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DepartmentDTO> findOneByPid(String pid) {
		log.debug("Request to get Department by pid : {}", pid);
		return departmentRepository.findOneByPid(pid).map(department -> {
			DepartmentDTO departmentDTO = departmentMapper.departmentToDepartmentDTO(department);
			return departmentDTO;
		});
	}

	/**
	 * Get one department by name.
	 *
	 * @param name
	 *            the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<DepartmentDTO> findByName(String name) {
		log.debug("Request to get Department by name : {}", name);
		return departmentRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(department -> {
					DepartmentDTO departmentDTO = departmentMapper.departmentToDepartmentDTO(department);
					return departmentDTO;
				});
	}

	/**
	 * Delete the department by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Department : {}", pid);
		departmentRepository.findOneByPid(pid).ifPresent(department -> {
			departmentRepository.delete(department.getId());
		});
	}

	/**
	 * Update the Department status by pid.
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @param activate
	 *            the activate of the entity
	 * @return the entity
	 */
	@Override
	public DepartmentDTO updateDepartmentStatus(String pid, boolean activate) {
		log.debug("Request to update Department status");
		return departmentRepository.findOneByPid(pid).map(department -> {
			department.setActivated(activate);
			department = departmentRepository.save(department);
			DepartmentDTO result = departmentMapper.departmentToDepartmentDTO(department);
			return result;
		}).orElse(null);
	}

	/**
	 * @author Fahad
	 * @since Feb 14, 2017
	 * 
	 *        find all active company
	 * 
	 * @param active
	 *            the active of the entity
	 * @return the entity
	 */
	@Override
	public Page<DepartmentDTO> findAllByCompanyAndActivatedDepartmentOrderByName(Pageable pageable, boolean active) {
		log.debug("Request to get Activated Department ");
		Page<Department> pageDepartments = departmentRepository
				.findAllByCompanyAndActivatedDepartmentOrderByName(pageable, active);
		Page<DepartmentDTO> pageDepartmentDTOs = new PageImpl<DepartmentDTO>(
				departmentMapper.departmentsToDepartmentDTOs(pageDepartments.getContent()), pageable,
				pageDepartments.getTotalElements());
		return pageDepartmentDTOs;
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
	public List<DepartmentDTO> findAllByCompanyAndDeactivatedDepartment(boolean deactive) {
		log.debug("Request to get Deactivated Department ");
		List<Department> departments = departmentRepository.findAllByCompanyAndDeactivatedDepartment(deactive);
		List<DepartmentDTO> departmentDTOs = departmentMapper.departmentsToDepartmentDTOs(departments);
		return departmentDTOs;
	}

}
