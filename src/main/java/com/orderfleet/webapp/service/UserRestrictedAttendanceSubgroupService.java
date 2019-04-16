package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.web.rest.dto.UserRestrictedAttendanceSubgroupDTO;


public interface UserRestrictedAttendanceSubgroupService {

	void save(String userPid,List<Long>attendanceSubgroupIds);
	
	List<UserRestrictedAttendanceSubgroupDTO>findByUserPid(String userPid);
	
	List<UserRestrictedAttendanceSubgroupDTO> findAllByUserLogin();
}
