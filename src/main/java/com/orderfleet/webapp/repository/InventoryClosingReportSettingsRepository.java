package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryClosingReportSettings;

public interface InventoryClosingReportSettingsRepository extends JpaRepository<InventoryClosingReportSettings, Long>{

	Optional<InventoryClosingReportSettings> findByCompanyIdAndDocumentPid(Long id, String documentPid);
	
	@Query("select inventoryClosingReport from InventoryClosingReportSettings inventoryClosingReport where inventoryClosingReport.company.id = ?#{principal.companyId}")
	List<InventoryClosingReportSettings> findAllByCompanyId();
	
	@Query("select inventoryClosingReport.document from InventoryClosingReportSettings inventoryClosingReport where inventoryClosingReport.company.id = ?#{principal.companyId} and inventoryClosingReport.inventoryClosingReportSettingGroup.pid = ?1")
	List<Document> findDocumentByReportSettingGroupPid(String reportSettingGroupPid);
	
	@Query("select inventoryClosingReport.document.id from InventoryClosingReportSettings inventoryClosingReport where inventoryClosingReport.company.id = ?#{principal.companyId} and inventoryClosingReport.inventoryClosingReportSettingGroup.pid = ?1")
	Set<Long> findDocumentIdsByReportSettingGroupPid(String reportSettingGroupPid);
}
