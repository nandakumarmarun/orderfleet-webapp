package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.InventoryClosingReportSettingGroup;

public interface InventoryClosingReportSettingGroupRepository extends JpaRepository<InventoryClosingReportSettingGroup, Long>{
	Optional<InventoryClosingReportSettingGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<InventoryClosingReportSettingGroup> findOneByPid(String pid);

	@Query("select inventoryClosingReportSettingGroup from InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup where inventoryClosingReportSettingGroup.company.id = ?#{principal.companyId} and inventoryClosingReportSettingGroup.activated = ?1")
	List<InventoryClosingReportSettingGroup> findAllByCompanyId(boolean active);

	@Query("select inventoryClosingReportSettingGroup from InventoryClosingReportSettingGroup inventoryClosingReportSettingGroup where inventoryClosingReportSettingGroup.company.id = ?#{principal.companyId} and inventoryClosingReportSettingGroup.activated = ?1")
	List<InventoryClosingReportSettingGroup> findAllByCompanyAndDeactivatedInventoryClosingReportSettingGroup(boolean deactive);
}
