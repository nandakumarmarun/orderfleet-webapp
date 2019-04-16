package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import com.orderfleet.webapp.domain.PerformanceReportMobile;
import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.enums.MobileUINames;

/**
 * Spring Data JPA repository for the PerformanceReportMobile entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 23, 2017
 */
public interface PerformanceReportMobileRepository extends JpaRepository<PerformanceReportMobile, Long> {

	@Query("select performanceReportMobile from PerformanceReportMobile performanceReportMobile where performanceReportMobile.company.id = ?#{principal.companyId}")
	List<PerformanceReportMobile> findAllByCompanyId();

	@Query("select prm.salesTargetReportSetting from PerformanceReportMobile prm where prm.salesTargetReportSetting.accountWiseTarget = true and prm.mobileUINames = ?1 and prm.company.id = ?#{principal.companyId}")
	List<SalesTargetReportSetting> findAccountSalesTargetReportSettingByMobileUI(MobileUINames mobileUIName);
	
	@Query("select prm.salesTargetReportSetting from PerformanceReportMobile prm where prm.salesTargetReportSetting.accountWiseTarget = false and prm.mobileUINames = ?1 and prm.company.id = ?#{principal.companyId}")
	List<SalesTargetReportSetting> findUserSalesTargetReportSettingByMobileUI(MobileUINames mobileUIName);

	@Query("select prm from PerformanceReportMobile prm where prm.company.id = ?#{principal.companyId} and prm.mobileUINames = ?1 and prm.salesTargetReportSetting.pid=?2")
	Optional<PerformanceReportMobile> findOneByMobileUINameAndalesTargetReportSetting(MobileUINames mobileUIName,
			String salesTargetReportSettingPid);

	@Query("select prm from PerformanceReportMobile prm where prm.company.id = ?#{principal.companyId} and  prm.salesTargetReportSetting.pid=?1")
	Optional<PerformanceReportMobile> findOneBySalesTargetReportSettingPid(String salesTargetReportSettingPid);

	void deleteByCompanyIdAndSalesTargetReportSettingPid(Long companyid, String salesTargetReportSettingPid);
}
