package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.enums.BestPerformanceType;

/**
 * Spring Data JPA repository for the SalesTargetBlock entity.
 *
 * @author Sarath
 * @since Feb 17, 2017
 */
public interface SalesTargetBlockRepository extends JpaRepository<SalesTargetBlock, Long> {

	Optional<SalesTargetBlock> findByCompanyIdAndNameIgnoreCase(Long id, String name);

	Optional<SalesTargetBlock> findOneByPid(String pid);

	@Query("select salesTargetBlock from SalesTargetBlock salesTargetBlock where salesTargetBlock.company.id = ?#{principal.companyId}")
	List<SalesTargetBlock> findAllByCompanyId();
	
	@Query("select salesTargetBlock from SalesTargetBlock salesTargetBlock where salesTargetBlock.company.id = ?#{principal.companyId} and salesTargetBlock.targetSettingType=?1")
	List<SalesTargetBlock> findAllByCompanyIdAndtargetSettingType(BestPerformanceType targetSettingType);
}
