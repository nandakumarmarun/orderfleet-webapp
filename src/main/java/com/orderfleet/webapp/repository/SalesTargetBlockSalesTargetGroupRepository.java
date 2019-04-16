package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.SalesTargetBlock;
import com.orderfleet.webapp.domain.SalesTargetBlockSalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroup;

/**
 * repository for SalesTargetBlockSalesTargetGroup
 *
 * @author Sarath
 * @since Feb 22, 2017
 */
public interface SalesTargetBlockSalesTargetGroupRepository
		extends JpaRepository<SalesTargetBlockSalesTargetGroup, Long> {

	@Query("select stbstg from SalesTargetBlockSalesTargetGroup stbstg where stbstg.company.id = ?#{principal.companyId}")
	List<SalesTargetBlockSalesTargetGroup> findAllByCompanyId();

	void deleteBySalesTargetBlockPid(String salesTargetBlockPid);

	@Query("select stbstg.salesTargetGroup from SalesTargetBlockSalesTargetGroup stbstg where stbstg.company.id = ?#{principal.companyId} and stbstg.salesTargetBlock.pid = ?1")
	List<SalesTargetGroup> findSalesTargetGroupsBySalesTargetBlockPid(String salesTargetBlockPid);
	
	@Query("select stbstg.salesTargetGroup from SalesTargetBlockSalesTargetGroup stbstg where stbstg.company.id = ?#{principal.companyId} and stbstg.salesTargetBlock.pid in ?1")
	List<SalesTargetGroup> findSalesTargetGroupsBySalesTargetBlockPidIn(List<String> salesTargetBlockPids);
	
	@Query("select stbstg.salesTargetGroup.id from SalesTargetBlockSalesTargetGroup stbstg where stbstg.company.id = ?#{principal.companyId} and stbstg.salesTargetBlock.pid = ?1")
	Set<Long> findSalesTargetGroupIdsBySalesTargetBlockPid(String salesTargetBlockPid);

	@Query("select stbstg.salesTargetBlock from SalesTargetBlockSalesTargetGroup stbstg where stbstg.company.id = ?#{principal.companyId} and stbstg.salesTargetGroup.pid = ?1")
	List<SalesTargetBlock> findSalesTargetBlockBySalesTargetGroupPid(String salesTargetGroupPid);
	
}
