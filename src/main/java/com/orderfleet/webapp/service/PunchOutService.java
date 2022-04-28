package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.PunchOutDTO;

/**
 * Service for managing PunchOut.
 * 
 * @author Athul
 * @since March 27,2018
 */

public interface PunchOutService {

	String PID_PREFIX = "PNGOUT-";

	PunchOutDTO savePunchOut(PunchOutDTO punchOutDTO);

	List<PunchOutDTO> findAllByCompanyIdAndPunchDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<PunchOutDTO> findAllByCompanyIdUserPidInAndPunchDateBetween(List<Long> userIds, LocalDateTime fromDate,LocalDateTime toDate);
	
	List<PunchOutDTO> findAllByCompanyIdUserPidAndPunchDate(String userPId,LocalDateTime fromDate,LocalDateTime toDate);
	
	List<PunchOutDTO> findAllByCompanyIdUserPidAndPunchDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<PunchOutDTO> findAllByCompanyIdAndCreatedDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<PunchOutDTO> findAllByCompanyIdUserPidInAndCreatedDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<PunchOutDTO> findAllByCompanyIdUserPidAndCreatedDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

}
