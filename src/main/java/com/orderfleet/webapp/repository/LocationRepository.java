package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Location;

/**
 * Spring Data JPA repository for the Location entity.
 *
 * @author Shaheer
 * @since May 26, 2016
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

	
	@Query("select location from Location location where location.company.id = ?#{principal.companyId} and location.activatedLocations=?1 order by location.name asc")
	List<Location> findAllLocationsByCompanyAndActivatedLocations(boolean activated);
	
	
	@Query("select location from Location location where location.company.id = ?#{principal.companyId} and location.name=?1 ")
	Location findByName(String name);

	Optional<Location> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Location> findOneByPid(String pid);

	@Query("select location from Location location where location.company.id = ?#{principal.companyId}")
	List<Location> findAllByCompanyId();

	@Query("select location from Location location where location.company.id = ?#{principal.companyId}")
	Page<Location> findAllByCompanyId(Pageable pageable);

	@Query("select location from Location location where location.company.id = ?#{principal.companyId} and location.id not in (select locationHierarchy.location.id from LocationHierarchy locationHierarchy where locationHierarchy.company.id = ?#{principal.companyId} and locationHierarchy.activated=true)")
	List<Location> findByCompanyIdAndIdNotIn();
	

	@Query("select location from Location location where location.company.id = ?#{principal.companyId} and location.id  in (select locationHierarchy.location.id from LocationHierarchy locationHierarchy where locationHierarchy.company.id = ?#{principal.companyId} and locationHierarchy.activated=true)")
	List<Location> findByCompanyIdAndIdIn();

	List<Location> findAllByCompanyPid(String companyPid);

	@Query("select location from Location location where location.company.id= ?#{principal.companyId} and location.activated= ?1")
	Page<Location> findAllByCompanyIdAndLocationActivated(Pageable pageable,boolean active);

	@Query("select location from Location location where location.company.id= ?#{principal.companyId} and location.activated= ?1 order by location.name asc")
	List<Location> findAllByCompanyIdAndLocationActivatedOrDeactivated(boolean active);
	
	@Query("select location from Location location where location.company.id= ?1 and location.activated= ?2 order by location.name asc")
	List<Location> findAllByCompanyIdAndLocationActivatedOrDeactivated(Long companyId, boolean active);
	
	@Query("select location from Location location where location.company.pid= ?1 and location.activated= ?2 order by location.name asc")
	List<Location> findAllByCompanyPidAndLocationActivatedOrDeactivated(String companyPid, boolean active);

	@Query("select location from Location location where location.company.id= ?#{principal.companyId} and location.activated= ?1 order by location.name asc")
	Page<Location> findAllByCompanyIdAndActivatedLocationOrderByName(Pageable pageable,boolean active);

	List<Location> findAllByCompanyId(Long companyId);

	List<Location> findByCompanyIdAndActivatedTrue(Long companyId);

	@Query("select location from Location location where location.company.id = ?#{principal.companyId} and location.activated = 'TRUE' order by location.name asc")
	List<Location> findAllByCompanyIdOrderByName();

	@Query("select location from Location location where location.company.id= ?#{principal.companyId} and location.activated= ?1 and location.lastModifiedDate > ?2")
	List<Location> findAllByCompanyIdAndLocationActivatedLastModified(boolean active, LocalDateTime lastModifiedDate);
	
	@Query("select location from Location location where location.company.id= ?#{principal.companyId} and location.activated = true and location.id in ?1")
	List<Location> findAllByCompanyIdAndActivatedLocationIn(List<Long> locationIds);
	
	@Query("select location from Location location where location.company.id= ?#{principal.companyId} and location.activated = true and location.pid in ?1")
	List<Location> findAllByCompanyIdAndActivatedLocationPidIn(List<String> locationPids);
	
	@Query("select location.id, location.name,location.alias from Location location where location.company.id = ?1")
	List<Object[]> findAllLocationByCompanyId(Long companyId);
	
	@Modifying
	@Query("update Location location set location.name = CONCAT(location.name, '_old'),location.activated = false where location.company.id = ?#{principal.companyId} AND location.id IN ?1")
	void deactivatelocationId(Set<Long> id);

}
