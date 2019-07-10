package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.SalesTargetGroupLocation;

/**
 * Spring Data JPA repository for the SalesTargetGroupLocation entity.
 *
 * @author Sarath
 * @since Oct 14, 2016
 */
public interface SalesTargetGroupLocationRepository extends JpaRepository<SalesTargetGroupLocation, Long> {

	void deleteBySalesTargetGroupPid(String salesTargetGroupPid);

	@Query("select salesTargetGroupLocation.location from SalesTargetGroupLocation salesTargetGroupLocation where salesTargetGroupLocation.salesTargetGroup.pid = ?1")
	List<Location> findLocationsBySalesTargetGroupPid(String salesTargetGroupPid);
	
	@Query("select salesTargetGroupLocation.location.id from SalesTargetGroupLocation salesTargetGroupLocation where salesTargetGroupLocation.salesTargetGroup.pid = ?1")
	Set<Long> findLocationIdBySalesTargetGroupPid(String salesTargetGroupPid);

	@Query("select salesTargetGroupLocation.location from SalesTargetGroupLocation salesTargetGroupLocation where salesTargetGroupLocation.salesTargetGroup.name = ?1 and salesTargetGroupLocation.company.id = ?#{principal.companyId}")
	List<Location> findLocationsBySalesTargetGroupName(String salesTargetGroupName);
	
	@Query("select salesTargetGroupLocation.location.id from SalesTargetGroupLocation salesTargetGroupLocation where salesTargetGroupLocation.salesTargetGroup.name = ?1 and salesTargetGroupLocation.company.id = ?#{principal.companyId}")
	Set<Long> findLocationIdsBySalesTargetGroupName(String salesTargetGroupName);
	
	@Query("select salesTargetGroupLocation.location from SalesTargetGroupLocation salesTargetGroupLocation where salesTargetGroupLocation.salesTargetGroup.name = ?1 and salesTargetGroupLocation.location in ?2 and salesTargetGroupLocation.company.id = ?#{principal.companyId}")
	List<Location> findLocationsBySalesTargetGroupNameAndLocationProfileIn(String salesTargetGroupName, List<Location> locations);

	@Query("select salesTargetGroupDocument.document.id, salesTargetGroupDocument.salesTargetGroup.name  from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.pid in ?1 and salesTargetGroupDocument.company.id = ?#{principal.companyId}")
	Set<Object[]> findDocumentId();
	
	@Query("select salesTargetGroupLocation.location.id, salesTargetGroupLocation.salesTargetGroup.name from SalesTargetGroupLocation salesTargetGroupLocation where salesTargetGroupLocation.salesTargetGroup.pid in ?1 and salesTargetGroupLocation.company.id = ?#{principal.companyId}")
	Set<Object[]> findLocationIdWithSalesTargetGroupNameBySalesTargetGroupPid(List<String> salesTargetGroupPids);
	
}