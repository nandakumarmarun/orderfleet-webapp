package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesTargetReportSettingSalesTargetBlock;
import com.orderfleet.webapp.domain.enums.TargetType;

public interface SalesTargetReportSettingSalesTargetBlockRepository
		extends JpaRepository<SalesTargetReportSettingSalesTargetBlock, Long> {

	@Query("select strsStb from SalesTargetReportSettingSalesTargetBlock strsStb where strsStb.company.id = ?#{principal.companyId}")
	List<SalesTargetReportSettingSalesTargetBlock> findAllByCompanyId();

	List<SalesTargetReportSettingSalesTargetBlock> findBySalesTargetReportSettingPidOrderBySortOrder(
			String salesTargetReportSettingPid);

	void deleteBySalesTargetReportSettingPid(String salesTargetReportSettingPid);
	
	@Query("select strsStb.salesTargetReportSetting.targetType from SalesTargetReportSettingSalesTargetBlock strsStb where strsStb.company.id = ?#{principal.companyId} and strsStb.salesTargetBlock.id=?1")
	List<TargetType> findTargetTypeBySalesTargetBlockId(Long salesTargetBlockId);
	
	@Query("select strsStb.salesTargetReportSetting.targetType from SalesTargetReportSettingSalesTargetBlock strsStb where strsStb.salesTargetBlock.pid=?1 and strsStb.company.id = ?2")
	List<TargetType> findTargetTypeBySalesTargetBlockPidAndCompanyId(String salesTargetBlockPid, Long companyId);


}
