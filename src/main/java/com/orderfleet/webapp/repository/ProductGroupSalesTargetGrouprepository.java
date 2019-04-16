package com.orderfleet.webapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ProductGroupSalesTargetGroup;
import com.orderfleet.webapp.domain.SalesTargetGroup;

public interface ProductGroupSalesTargetGrouprepository extends JpaRepository<ProductGroupSalesTargetGroup, Long> {

	@Transactional
	void deleteByProductGroupPid(String productGroupPid);

	@Query("select productGroupSalesTargetGroup from ProductGroupSalesTargetGroup productGroupSalesTargetGroup where productGroupSalesTargetGroup.company.id = ?#{principal.companyId}")
	List<ProductGroupSalesTargetGroup> findAllByCompanyId();

	@Query("select productGroupSalesTargetGroup.salesTargetGroup from ProductGroupSalesTargetGroup productGroupSalesTargetGroup where productGroupSalesTargetGroup.company.id = ?#{principal.companyId} and productGroupSalesTargetGroup.productGroup.pid = ?1")
	List<SalesTargetGroup> findSalesTargetGroupByProductGroupPid(String productGroupPid);

	@Query("select productGroupSalesTargetGroup.productGroup from ProductGroupSalesTargetGroup productGroupSalesTargetGroup where productGroupSalesTargetGroup.company.id = ?#{principal.companyId}")
	List<ProductGroup> findAllProductGroupByCompanyId();
	
	@Query("select productGroupSalesTargetGroup.salesTargetGroup from ProductGroupSalesTargetGroup productGroupSalesTargetGroup where productGroupSalesTargetGroup.company.id = ?#{principal.companyId} and productGroupSalesTargetGroup.productGroup.pid in ?1")
	List<SalesTargetGroup> findSalesTargetGroupByProductGroupPidIn(List<String> productGroupPid);
}
