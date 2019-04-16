package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.web.rest.dto.UserDistanceDTO;

/**
 * Service Interface for managing UserDistanceService.
 *
 * @author Sarath
 * @since May 25, 2017
 *
 */
public interface UserDistanceService {

	Optional<UserDistanceDTO> findByCompanyIdAndUserIdAndDate(Long companyId, String userId, LocalDate date);

	UserDistanceDTO save(UserDistanceDTO userDistanceDTO, Long companyId);

	UserDistanceDTO update(UserDistanceDTO userDistanceDTO, Long companyId);

	List<UserDistanceDTO> findAllByCompany();

	Page<UserDistanceDTO> findAllByCompanyIdOrderByDateDesc(Pageable pageable);

	List<UserDistanceDTO> findAllByCompanyIdAndUserPid(String userPid);

	List<UserDistanceDTO> findAllByCompanyIdAndUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDate fromDate, LocalDate toDate);

	List<UserDistanceDTO> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDate fromDate, LocalDate toDate);

	List<UserDistanceDTO> findAllByCompanyIdAndUserPidAndDateOrderByCreatedDateDesc(String userPid, LocalDate fromDate);

	List<UserDistanceDTO> findAllByCompanyIdAndUserPidInAndDateBetweenOrderByCreatedDateDesc(List<String> userPid,
			LocalDate fromDate, LocalDate toDate);

	List<UserDistanceDTO> findAllByCompanyIdAndUserPidInAndDateOrderByCreatedDateDesc(List<String> userPid,
			LocalDate fromDate);
}
