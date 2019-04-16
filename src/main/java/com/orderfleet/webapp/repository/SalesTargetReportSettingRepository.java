package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesTargetReportSetting;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;

/**
 * repository for SalesTargetReportSetting
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
public interface SalesTargetReportSettingRepository extends JpaRepository<SalesTargetReportSetting, Long> {

	Optional<SalesTargetReportSetting> findOneByPid(String pid);

	@Query("select strs from SalesTargetReportSetting strs where strs.company.id = ?#{principal.companyId} order by strs.name")
	List<SalesTargetReportSetting> findAllByCompanyId();

	Optional<SalesTargetReportSetting> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	@Query("select strs from SalesTargetReportSetting strs where strs.company.id = ?#{principal.companyId} and strs.targetSettingType=?1 order by strs.name")
	List<SalesTargetReportSetting> findAllByCompanyIdAndTargetSettingType(BestPerformanceType targetSettingType);
}
