package com.orderfleet.webapp.service;

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

	List<PunchOutDTO> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<PunchOutDTO> findAllByCompanyIdUserPidInAndDateBetween(List<Long> userIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	List<PunchOutDTO> findAllByCompanyIdUserPidAndDateBetween(String userPid, LocalDateTime fromDate,
			LocalDateTime toDate);

}
