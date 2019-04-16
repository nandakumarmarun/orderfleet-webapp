package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DynamicDocumentReportDetail;

/**
 * Spring Data JPA repository for the DynamicDocumentReportDetail entity.
 *
 * @author Sarath
 * @since Mar 26, 2018
 *
 */
public interface DynamicDocumentReportDetailRepository extends JpaRepository<DynamicDocumentReportDetail, Long> {

	@Query("select dynamicDocumentReportDetail from DynamicDocumentReportDetail dynamicDocumentReportDetail where dynamicDocumentReportDetail.company.id = ?#{principal.companyId}")
	List<DynamicDocumentReportDetail> findAllByCompanyId();

	@Transactional
	void deleteByDynamicDocumentSettingsHeaderPid(String dynamicDocumentSettingsHeaderPid);
	
	@Transactional
	void deleteByDynamicDocumentSettingsHeaderId(Long dynamicDocumentSettingsHeaderId);
}
