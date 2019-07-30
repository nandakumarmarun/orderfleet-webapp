package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.UserWiseReceiptTarget;

public interface UserWiseReceiptTargetRepository extends JpaRepository<UserWiseReceiptTarget, Long> {

	List<UserWiseReceiptTarget> findByEmployeeProfilePidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String pid,
			LocalDate firstDateMonth, LocalDate lastDateMonth);

	Optional<UserWiseReceiptTarget> findOneByPid(String userWiseReceiptTargetPid);

	List<UserWiseReceiptTarget> findByEmployeeProfilePidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			List<String> employeePids, LocalDate fromDate, LocalDate toDate);

}
