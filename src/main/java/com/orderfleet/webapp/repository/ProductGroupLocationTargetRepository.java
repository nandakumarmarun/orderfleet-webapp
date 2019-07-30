package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.ProductGroupLocationTarget;

public interface ProductGroupLocationTargetRepository extends JpaRepository<ProductGroupLocationTarget, Long> {

	List<ProductGroupLocationTarget> findByLocationPidAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			String locationPid, String pid, LocalDate firstDateMonth, LocalDate lastDateMonth);

	List<ProductGroupLocationTarget> findByLocationPidInAndProductGroupPidAndFromDateGreaterThanEqualAndToDateLessThanEqual(
			List<String> locationPids, String productGroupPid, LocalDate fromDate, LocalDate toDate);

	Optional<ProductGroupLocationTarget> findOneByPid(String pid);

}
