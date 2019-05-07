package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.Stage;

public interface StageRepository extends JpaRepository<Stage, Long> {

	Optional<Stage> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<Stage> findOneByPid(String pid);

	List<Stage> findByPidIn(List<String> pids);

	@Query("select stage from Stage stage where stage.company.id = ?1 and stage.name = ?2 order by stage.sortOrder ASC")
	List<Stage> findByCompanyIdAndName(Long companyId, String name);

	@Query("select stage from Stage stage where stage.company.id = ?#{principal.companyId} order by stage.sortOrder ASC")
	List<Stage> findAllByCompanyId();

	@Query("select stage from Stage stage where stage.company.id = ?#{principal.companyId} and stage.activated = ?1 order by stage.sortOrder ASC")
	List<Stage> findAllByCompanyIdAndActivated(boolean activated);

	@Query(value = "select count(sh.stage_id),s.name,s.target  from tbl_stage_header sh ,tbl_stage s where sh.company_id = ?#{principal.companyId} and sh.stage_id = s.id group by sh.stage_id , s.name ,s.target", nativeQuery = true)
	List<Object[]> findAllStageTargetReports();

}
