package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AttendanceStatusSubgroup;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserRestrictedAttendanceSubgroup;
import com.orderfleet.webapp.repository.AttendanceStatusSubgroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.UserRestrictedAttendanceSubgroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.UserRestrictedAttendanceSubgroupService;
import com.orderfleet.webapp.web.rest.dto.UserRestrictedAttendanceSubgroupDTO;

@Service
@Transactional
public class UserRestrictedAttendanceSubgroupServiceImpl implements UserRestrictedAttendanceSubgroupService{

	@Inject
	private UserRestrictedAttendanceSubgroupRepository userRestrictedAttendanceSubgroupRepository;
	
	@Inject
	private AttendanceStatusSubgroupRepository attendanceStatusSubgroupRepository;
	
	@Inject
	private  UserRepository userRepository;
	@Override
	public void save(String userPid, List<Long> attendanceSubgroupIds) {
		List<UserRestrictedAttendanceSubgroup>userRestrictedAttendanceSubgroups=userRestrictedAttendanceSubgroupRepository.findByUserPid(userPid);
		User user=userRepository.findOneByPid(userPid).get();
		if(!userRestrictedAttendanceSubgroups.isEmpty()) {
			userRestrictedAttendanceSubgroupRepository.deleteByUserPid(userPid);
		}
		for(Long id:attendanceSubgroupIds) {
			UserRestrictedAttendanceSubgroup userRestrictedAttendanceSubgroup=new UserRestrictedAttendanceSubgroup();
			AttendanceStatusSubgroup attendanceStatusSubgroup=attendanceStatusSubgroupRepository.findOne(id);
			userRestrictedAttendanceSubgroup.setAttendanceStatusSubgroup(attendanceStatusSubgroup);
			userRestrictedAttendanceSubgroup.setCompany(user.getCompany());
			userRestrictedAttendanceSubgroup.setUser(user);
			userRestrictedAttendanceSubgroupRepository.save(userRestrictedAttendanceSubgroup);
		}
		
	}
	@Override
	public List<UserRestrictedAttendanceSubgroupDTO> findByUserPid(String userPid) {
		List<UserRestrictedAttendanceSubgroup>userRestrictedAttendanceSubgroups=userRestrictedAttendanceSubgroupRepository.findByUserPid(userPid);
		List<UserRestrictedAttendanceSubgroupDTO> userRestrictedAttendanceSubgroupDTOs=new ArrayList<>();
		for (UserRestrictedAttendanceSubgroup userRestrictedAttendanceSubgroup : userRestrictedAttendanceSubgroups) {
			UserRestrictedAttendanceSubgroupDTO userRestrictedAttendanceSubgroupDTO=new UserRestrictedAttendanceSubgroupDTO(userRestrictedAttendanceSubgroup);
			userRestrictedAttendanceSubgroupDTOs.add(userRestrictedAttendanceSubgroupDTO);
		}
		return userRestrictedAttendanceSubgroupDTOs;
	}
	@Override
	public List<UserRestrictedAttendanceSubgroupDTO> findAllByUserLogin() {
		List<UserRestrictedAttendanceSubgroup>userRestrictedAttendanceSubgroups=userRestrictedAttendanceSubgroupRepository.findAllByUserLogin(SecurityUtils.getCurrentUserLogin());
		List<UserRestrictedAttendanceSubgroupDTO> userRestrictedAttendanceSubgroupDTOs=new ArrayList<>();
		for (UserRestrictedAttendanceSubgroup userRestrictedAttendanceSubgroup : userRestrictedAttendanceSubgroups) {
			UserRestrictedAttendanceSubgroupDTO userRestrictedAttendanceSubgroupDTO=new UserRestrictedAttendanceSubgroupDTO(userRestrictedAttendanceSubgroup);
			userRestrictedAttendanceSubgroupDTOs.add(userRestrictedAttendanceSubgroupDTO);
		}
		return userRestrictedAttendanceSubgroupDTOs;
	}

}
