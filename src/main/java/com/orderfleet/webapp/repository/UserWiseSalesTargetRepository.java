package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.UserWiseSalesTarget;

public interface UserWiseSalesTargetRepository extends JpaRepository<UserWiseSalesTarget, Long> {

	List<UserWiseSalesTarget> findByEmployeeProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String pid,
			LocalDate firstDateMonth, LocalDate lastDateMonth);

	Optional<UserWiseSalesTarget> findOneByPid(String userWiseSalesTargetPid);

	List<UserWiseSalesTarget> findByEmployeeProfilePidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			List<String> employeePids, LocalDate fromDate, LocalDate toDate);

}
