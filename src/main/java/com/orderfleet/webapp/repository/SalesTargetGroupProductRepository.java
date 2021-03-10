package com.orderfleet.webapp.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SalesTargetGroupProduct;

/**
 * Spring Data JPA repository for the SalesTargetGroupProduct entity.
 *
 * @author Sarath
 * @since Oct 14, 2016
 */
public interface SalesTargetGroupProductRepository extends JpaRepository<SalesTargetGroupProduct, Long> {

	void deleteBySalesTargetGroupPid(String salesTargetGroupPid);

	@Query("select salesTargetGroupProduct.product from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.salesTargetGroup.pid = ?1")
	List<ProductProfile> findProductsBySalesTargetGroupPid(String salesTargetGroupPid);

	@Query("select salesTargetGroupProduct.product.id from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.salesTargetGroup.pid = ?1")
	Set<Long> findProductIdBySalesTargetGroupPid(String salesTargetGroupPid);

	@Query("select salesTargetGroupProduct.product from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.salesTargetGroup.name = ?1 and salesTargetGroupProduct.company.id = ?#{principal.companyId}")
	List<ProductProfile> findProductsBySalesTargetGroupName(String salesTargetGroupName);

	@Query("select salesTargetGroupProduct.product.id from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.salesTargetGroup.name = ?1 and salesTargetGroupProduct.company.id = ?#{principal.companyId}")
	Set<Long> findProductIdsBySalesTargetGroupName(String salesTargetGroupName);

	@Query("select salesTargetGroupProduct.product from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.salesTargetGroup.name = ?1 and salesTargetGroupProduct.product in ?2 and salesTargetGroupProduct.company.id = ?#{principal.companyId}")
	List<ProductProfile> findProductsBySalesTargetGroupNameAndProductProfileIn(String salesTargetGroupName,
			List<ProductProfile> products);

	@Query("select salesTargetGroupDocument.document.id, salesTargetGroupDocument.salesTargetGroup.name  from SalesTargetGroupDocument salesTargetGroupDocument where salesTargetGroupDocument.salesTargetGroup.pid in ?1 and salesTargetGroupDocument.company.id = ?#{principal.companyId}")
	Set<Object[]> findDocumentId();

	@Query("select salesTargetGroupProduct.product.id, salesTargetGroupProduct.salesTargetGroup.name from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.salesTargetGroup.pid in ?1 and salesTargetGroupProduct.company.id = ?#{principal.companyId}")
	Set<Object[]> findProductIdWithSalesTargetGroupNameBySalesTargetGroupPid(List<String> salesTargetGroupPids);

	@Query("select salesTargetGroupProduct from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.company.id = ?#{principal.companyId}")
	List<SalesTargetGroupProduct> findAllByCompanyId();

	@Query("select salesTargetGroupProduct.salesTargetGroup.pid,salesTargetGroupProduct.product.id from SalesTargetGroupProduct salesTargetGroupProduct where salesTargetGroupProduct.company.id = ?#{principal.companyId}")
	List<Object[]> findAllSalesTargetGroupByCompanyIdOptimised();

}