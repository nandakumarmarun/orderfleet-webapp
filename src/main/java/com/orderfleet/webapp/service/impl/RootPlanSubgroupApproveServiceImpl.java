package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.RootPlanSubgroupApprove;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RootPlanSubgroupApproveRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RootPlanSubgroupApproveService;
import com.orderfleet.webapp.web.rest.dto.RootPlanSubgroupApproveDTO;

/**
 * Service Implementation for managing RootPlanSubgroupApprove.
 * 
 * @author fahad
 * @since Aug 28, 2017
 */
@Service
@Transactional
public class RootPlanSubgroupApproveServiceImpl implements RootPlanSubgroupApproveService{

	private final Logger log = LoggerFactory.getLogger(RootPlanSubgroupApproveServiceImpl.class);

	@Inject
	private RootPlanSubgroupApproveRepository rootPlanSubgroupApproveRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private AttendanceStatusSubgroupRepository attendanceStatusSubgroupRepository;
	
	@Inject
	private UserRepository userRepository;
	
	/**
	 * Save a rootPlanSubgroupApprove.
	 * 
	 * @param rootPlanSubgroupApproveDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public RootPlanSubgroupApproveDTO save(RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO) {
		log.debug("Request to save RootPlanSubgroupApprove : {}", rootPlanSubgroupApproveDTO);

		RootPlanSubgroupApprove rootPlanSubgroupApprove = new RootPlanSubgroupApprove();
		rootPlanSubgroupApprove.setApprovalRequired(rootPlanSubgroupApproveDTO.getApprovalRequired());
		AttendanceStatusSubgroup attendanceStatusSubgroup=attendanceStatusSubgroupRepository.findOne(rootPlanSubgroupApproveDTO.getAttendanceStatusSubgroupId());
		rootPlanSubgroupApprove.setAttendanceStatusSubgroup(attendanceStatusSubgroup);
		rootPlanSubgroupApprove.setRootPlanBased(rootPlanSubgroupApproveDTO.getRootPlanBased());
		User user= userRepository.findOneByPid(rootPlanSubgroupApproveDTO.getUserPid()).get();
		rootPlanSubgroupApprove.setUser(user);
		// set company
		rootPlanSubgroupApprove.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		rootPlanSubgroupApprove = rootPlanSubgroupApproveRepository.save(rootPlanSubgroupApprove);
		RootPlanSubgroupApproveDTO result = new RootPlanSubgroupApproveDTO(rootPlanSubgroupApprove);
		return result;
	}

	@Override
	public List<RootPlanSubgroupApproveDTO> findAllByCompany() {
		log.debug("Request to get all Documents");
		List<RootPlanSubgroupApprove> rootPlanSubgroupApproves = rootPlanSubgroupApproveRepository.findAllByCompanyId();
		List<RootPlanSubgroupApproveDTO>rootPlanSubgroupApproveDTOs=new ArrayList<>();
		for(RootPlanSubgroupApprove rootPlanSubgroupApprove:rootPlanSubgroupApproves) {
			RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO = new RootPlanSubgroupApproveDTO(rootPlanSubgroupApprove);
			rootPlanSubgroupApproveDTOs.add(rootPlanSubgroupApproveDTO);
		}
		
		return rootPlanSubgroupApproveDTOs;
	}

	@Override
	public RootPlanSubgroupApproveDTO update(RootPlanSubgroupApproveDTO rootPlanSubgroupApproveDTO) {
		log.debug("Request to Update RootPlanSubgroupApprove : {}", rootPlanSubgroupApproveDTO);
		RootPlanSubgroupApprove rootPlanSubgroupApprove = rootPlanSubgroupApproveRepository.findOne(rootPlanSubgroupApproveDTO.getId());
		AttendanceStatusSubgroup attendanceStatusSubgroup=attendanceStatusSubgroupRepository.findOne(rootPlanSubgroupApproveDTO.getAttendanceStatusSubgroupId());
		rootPlanSubgroupApprove.setAttendanceStatusSubgroup(attendanceStatusSubgroup);
		rootPlanSubgroupApprove.setApprovalRequired(rootPlanSubgroupApproveDTO.getApprovalRequired());
		rootPlanSubgroupApprove.setRootPlanBased(rootPlanSubgroupApproveDTO.getRootPlanBased());
		rootPlanSubgroupApprove = rootPlanSubgroupApproveRepository.save(rootPlanSubgroupApprove);
		RootPlanSubgroupApproveDTO result = new RootPlanSubgroupApproveDTO(rootPlanSubgroupApprove);
		return result;
	}

	@Override
	public RootPlanSubgroupApproveDTO findOneId(Long id) {
		RootPlanSubgroupApprove rootPlanSubgroupApprove=rootPlanSubgroupApproveRepository.findOne(id);
		RootPlanSubgroupApproveDTO result = new RootPlanSubgroupApproveDTO(rootPlanSubgroupApprove);
		return result;
	}

	@Override
	public void delete(Long id) {
		rootPlanSubgroupApproveRepository.delete(id);
	}

	@Override
	public Optional<RootPlanSubgroupApproveDTO> findOneByUserPidAndAttendanceStatusSubgroupId(String userPid, Long subgroupId) {
		return rootPlanSubgroupApproveRepository.findByUserPidAndAttendanceStatusSubgroupId(userPid, subgroupId).map(rootPlanSubgroupApprove -> {
			RootPlanSubgroupApproveDTO result = new RootPlanSubgroupApproveDTO(rootPlanSubgroupApprove);
			return result;
		});
	}

	@Override
	public List<RootPlanSubgroupApproveDTO> findAllByUserLogin() {
		return rootPlanSubgroupApproveRepository.findAllByUserLogin(SecurityUtils.getCurrentUserLogin()).stream()
				.map(RootPlanSubgroupApproveDTO::new).collect(Collectors.toList());
	}

	@Override
	public Optional<RootPlanSubgroupApproveDTO> findOneByUserPidAndId(String userPid, Long id) {
		return rootPlanSubgroupApproveRepository.findByUserPidAndId(userPid, id).map(rootPlanSubgroupApprove -> {
			RootPlanSubgroupApproveDTO result = new RootPlanSubgroupApproveDTO(rootPlanSubgroupApprove);
			return result;
		});
	}

}
