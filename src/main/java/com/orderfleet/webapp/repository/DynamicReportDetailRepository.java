package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.orderfleet.webapp.domain.DynamicReportDetail;

public interface DynamicReportDetailRepository extends JpaRepository<DynamicReportDetail, Long>{

//	@Query("select dynamicReportDetail from DynamicReportDetail dynamicReportDetail where dynamicReportDetail.dynamicReportHeader.id = ?1 ")
	List<DynamicReportDetail> findByDynamicReportHeaderId(long id);
	
	Optional<DynamicReportDetail> findOneById(long id);
}
