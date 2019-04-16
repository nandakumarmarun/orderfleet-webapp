package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesTargetGroup;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;

/**
 * Spring Data JPA repository for the SalesTargetGroup entity.
 * 
 * @author Sarath
 * @since Aug 12, 2016
 */
public interface SalesTargetGroupRepository extends JpaRepository<SalesTargetGroup, Long> {
	Optional<SalesTargetGroup> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<SalesTargetGroup> findOneByPid(String pid);

	@Query("select salesTargetGroup from SalesTargetGroup salesTargetGroup where salesTargetGroup.company.id = ?#{principal.companyId}")
	List<SalesTargetGroup> findAllByCompanyId();
	
	@Query("select salesTargetGroup.pid,salesTargetGroup.name  from SalesTargetGroup salesTargetGroup where salesTargetGroup.company.id = ?#{principal.companyId}")
	List<Object[]> findSalesTargetGroupPropertyByCompanyId();

	@Query("select salesTargetGroup from SalesTargetGroup salesTargetGroup where salesTargetGroup.company.id = ?#{principal.companyId}")
	Page<SalesTargetGroup> findAllByCompanyId(Pageable pageable);

	@Query("select salesTargetGroup from SalesTargetGroup salesTargetGroup where salesTargetGroup.company.id = ?#{principal.companyId} and salesTargetGroup.pid not in ?1")
	List<SalesTargetGroup> findAllByCompanyIdAndPidNotIn(List<String> salesTargetGroupPids);

	@Query("select salesTargetGroup from SalesTargetGroup salesTargetGroup where salesTargetGroup.company.id = ?#{principal.companyId} and salesTargetGroup.targetSettingType=?1")
	List<SalesTargetGroup> findAllByCompanyAndTargetSettingType(BestPerformanceType targetSettingType);
}
