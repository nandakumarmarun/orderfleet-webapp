package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.SalesLedgerWiseTarget;
import com.orderfleet.webapp.domain.UserWiseSalesTarget;

public interface SalesLedgerWiseTargetRepository extends JpaRepository<SalesLedgerWiseTarget, Long> {

	List<SalesLedgerWiseTarget> findBySalesLedgerPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(String pid,
			LocalDate firstDateMonth, LocalDate lastDateMonth);

	Optional<SalesLedgerWiseTarget> findOneByPid(String userWiseSalesTargetPid);

	List<SalesLedgerWiseTarget> findBySalesLedgerPidInAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			List<String> salesLedgerPids, LocalDate fromDate, LocalDate toDate);

}
