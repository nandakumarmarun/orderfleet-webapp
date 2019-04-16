package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.StageGroup;

public interface StageGroupRepository extends JpaRepository<StageGroup, Long> {

	Optional<StageGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<StageGroup> findOneByPid(String pid);
	
	@Query("select sg from StageGroup sg where sg.company.id = ?1 and sg.name = ?2 order by sg.name ASC")
	List<StageGroup> findByCompanyIdAndName(Long companyId, String name);

	@Query("select sg from StageGroup sg where sg.company.id = ?#{principal.companyId} order by sg.name ASC")
	List<StageGroup> findAllByCompanyId();
	
	@Query("select sg from StageGroup sg where sg.company.id = ?#{principal.companyId} and sg.activated = ?1 order by sg.name ASC")
	List<StageGroup> findAllByCompanyIdAndActivated(boolean activated);
	
}
