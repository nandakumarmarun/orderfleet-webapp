package com.orderfleet.webapp.repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationHierarchy;

/**
 * Spring Data JPA repository for the LocationHierarchy entity.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
public interface LocationHierarchyRepository extends JpaRepository<LocationHierarchy, Long> {

	List<LocationHierarchy> findByCompanyIdAndActivatedTrue(Long companyId);
	
	List<LocationHierarchy> findByCompanyPidAndActivatedTrue(String companyPid);
	
	List<LocationHierarchy> findByLocationInAndActivatedTrue(List<Location> locations);
	
	List<LocationHierarchy> findByLocationPidInAndActivatedTrue(List<String> locationPids);

	Optional<LocationHierarchy> findFirstByCompanyIdAndActivatedTrueOrderByIdDesc(Long companyId);
	
	LocationHierarchy findTopByLocationPidAndParentPidAndActivatedTrueOrderByActivatedDateDesc(String locationPid, String parentPid);
	
	@Query("select lh.location.id from LocationHierarchy lh where lh.parent.id = ?1 and lh.activated = true")
	List<Long> findIdsByParentIdAndActivatedTrue(Long parentLocationId);
	
	@Query("select lh from LocationHierarchy lh where lh.company.id = ?1 and activated = true and parent = null")
	Optional<LocationHierarchy> findRootLocationByCompanyId(Long companyId);

	@Query("select coalesce(max(lh.version),0) from LocationHierarchy lh where lh.company.id = ?#{principal.companyId}")
	Long findMaxVersionByCompanyId();

	@Modifying(clearAutomatically = true)
	@Query("UPDATE LocationHierarchy lh SET lh.activated = FALSE , lh.inactivatedDate = ?1 WHERE  lh.version = ?2 AND lh.company.id = ?#{principal.companyId}")
	int updateLocationHierarchyInactivatedFor(ZonedDateTime inactivatedDate, Long version);

	@Modifying
	@Query(value = "INSERT INTO tbl_location_hierarchy(activated, activated_date, version, company_id, location_id, parent_id)"
			+ "VALUES (TRUE, now(), ?1, ?#{principal.companyId},?2, ?3)", nativeQuery = true)
	void insertLocationHierarchyWithParent(Long version, Long locationId, Long parentId);

	@Modifying
	@Query(value = "INSERT INTO tbl_location_hierarchy(activated, activated_date, version, company_id, location_id, parent_id)"
			+ "VALUES (TRUE, now(), ?1, ?#{principal.companyId}, ?2, null)", nativeQuery = true)
	void insertLocationHierarchyWithNoParent(Long version, Long locationId);
	
	
	//................. used to save asyc from tally ................//
	@Modifying(clearAutomatically = true)
	@Query("UPDATE LocationHierarchy lh SET lh.activated = FALSE , lh.inactivatedDate = ?1 WHERE  lh.version = ?2 AND lh.company.id = ?3")
	int updateLocationHierarchyInactivatedFor(ZonedDateTime inactivatedDate, Long version,Long companyId);

	
	@Modifying
	@Query(value = "INSERT INTO tbl_location_hierarchy(activated, activated_date, version, company_id, location_id, parent_id)"
			+ "VALUES (TRUE, now(), ?1, ?3, ?2, null)", nativeQuery = true)
	void insertLocationHierarchyWithNoParent(Long version, Long locationId,long companyId);
	
	@Query("select coalesce(max(lh.version),0) from LocationHierarchy lh where lh.company.id = ?1")
	Long findMaxVersionByCompanyId(Long companyId);
	
	@Modifying
	@Query(value = "INSERT INTO tbl_location_hierarchy(activated, activated_date, version, company_id, location_id, parent_id)"
			+ "VALUES (TRUE, now(), ?1, ?4,?2, ?3)", nativeQuery = true)
	void insertLocationHierarchyWithParent(Long version, Long locationId, Long parentId,long companyId);
	
	@Query(value = "WITH RECURSIVE children AS (SELECT activated,location_id, parent_id FROM tbl_location_hierarchy WHERE location_id = ?1 UNION SELECT lh.activated,lh.location_id, lh.parent_id FROM tbl_location_hierarchy lh INNER JOIN children c ON c.location_id = lh.parent_id) SELECT COALESCE(location_id, 0 ) FROM children WHERE activated = TRUE", nativeQuery = true)
	List<Object> findChildrenByParentLocationIdAndActivatedTrue(Long locationId);
	
	@Query("select lh.location from LocationHierarchy lh where lh.parent.id = ?1 and lh.activated = true")
	List<Location> findLocationByParentIdAndActivatedTrue(Long parentLocationId);
	
	@Query("select lh.location from LocationHierarchy lh where lh.parent.id in ?1 and lh.activated = true")
	List<Location> findLocationByParentIdsAndActivatedTrue(List<Long> parentLocationIdw);
	
	@Query("select lh.parent from LocationHierarchy lh where lh.location.id = ?1 and lh.activated = true")
	Location findParentLocationByLocationIdAndActivatedTrue(Long locationId);
	
	@Query("select lh.parent from LocationHierarchy lh where lh.location.pid in ?1 and lh.activated = true")
	List<Location> findParentLocationsByLocationIdAndActivatedTrue(List<String> locationPids);
	
	//Only for Trial Setup
	@Modifying
	@Query(value = "INSERT INTO tbl_location_hierarchy(activated, activated_date, version, company_id, location_id, parent_id)"
			+ "VALUES (TRUE, now(), ?1, ?2, ?3, null)", nativeQuery = true)
	void insertTrialLocationHierarchyWithNoParent(Long version,Long companyId, Long locationId);
	
	@Transactional
	@Modifying
	@Query("delete from LocationHierarchy lh where lh.company.id = ?1 and lh.parent is not null")
	void deleteByCompanyIdAndParentNotNull(Long companyId);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE LocationHierarchy lh SET lh.activated = FALSE , lh.inactivatedDate = ?1 WHERE  lh.version = ?2 AND lh.company.id = ?3 AND lh.parent is not null")
	int updateLocationHierarchyInactivatedExceptTerritory(ZonedDateTime inactivatedDate, Long version,Long companyId);
}
