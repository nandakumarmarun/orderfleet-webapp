package com.orderfleet.webapp.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.StockLocation;
import com.orderfleet.webapp.repository.projections.CustomInventoryVoucherDetail;

/**
 * Spring Data JPA repository for the InventoryVoucherDetail entity.
 * 
 * @author Shaheer
 * @since OCtoner 01, 2016
 */
public interface InventoryVoucherDetailRepository extends JpaRepository<InventoryVoucherDetail, Long> {

	public String CUSTOMER_BASED_PRODUCTS = "select sum(ivd.quantity) as total_qty ,pp.name ,pp.pid from tbl_inventory_voucher_detail ivd "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id where ivd.inventory_voucher_header_id in ("
			+ "	select ivh.id from tbl_inventory_voucher_header ivh "
			+ "	INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id "
			+ "	INNER JOIN tbl_user us on us.id = ivh.created_by_id where ivh.company_id = ?#{principal.companyId} and us.login = ?1 and "
			+ "	ap.pid = ?2 and ivh.created_date between ?3 and ?4 ) group by pp.name ,pp.pid";

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.sourceStockLocation in ?2 and voucherDetail.inventoryVoucherHeader.createdDate > ?3")
	Double getClosingStockBySourceStockLocation(String productPid, List<StockLocation> stockLocations,
			LocalDateTime date);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.destinationStockLocation in ?2 and voucherDetail.inventoryVoucherHeader.createdDate > ?3")
	Double getClosingStockByDestinationStockLocation(String productPid, List<StockLocation> stockLocations,
			LocalDateTime date);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.sourceStockLocation in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	Double getClosingStockBySourceStockLocationAndCreatedDateBetween(String productPid,
			List<StockLocation> stockLocations, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.destinationStockLocation in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	Double getClosingStockByDestinationStockLocationAndCreatedDateBetween(String productPid,
			List<StockLocation> stockLocations, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.sourceStockLocation.id = ?2 ")
	Double getClosingStockBySourceStockLocationId(String productPid, Long stockLocationId);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.destinationStockLocation.id = ?2")
	Double getClosingStockByDestinationStockLocationId(String productPid, Long stockLocationId);

	@Query("select vd.inventoryVoucherHeader.createdBy.firstName,vd.inventoryVoucherHeader.receiverAccount.name,vd.inventoryVoucherHeader.supplierAccount.name,vd.inventoryVoucherHeader.createdDate,vd.quantity,vd.rowTotal,vd.inventoryVoucherHeader.pid from InventoryVoucherDetail vd where vd.inventoryVoucherHeader.company.id = ?#{principal.companyId} and vd.product.pid = ?1 and vd.inventoryVoucherHeader.createdDate between ?2 and ?3 and vd.inventoryVoucherHeader.document in ?4 Order By vd.inventoryVoucherHeader.createdDate desc")
	List<Object[]> findByProductPidAndDateBetween(String productPid, LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents);

	@Query("select vd.inventoryVoucherHeader.employee.name,vd.inventoryVoucherHeader.receiverAccount.name,vd.inventoryVoucherHeader.supplierAccount.name,vd.inventoryVoucherHeader.createdDate,vd.quantity,vd.rowTotal,vd.inventoryVoucherHeader.pid from InventoryVoucherDetail vd where vd.inventoryVoucherHeader.company.id = ?#{principal.companyId} and vd.product.pid = ?1 and vd.inventoryVoucherHeader.createdDate between ?2 and ?3 and vd.inventoryVoucherHeader.document in ?4 Order By vd.inventoryVoucherHeader.createdDate desc")
	List<Object[]> findByProductPidAndDateBetweenAndDocumentIn(String productPid, LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	// Dashboard start
	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.id in ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfAmountByUserIdInAndDocumentsAndProductsAndCreatedDateBetween(List<Long> userIds,
			List<Document> documents, List<ProductProfile> productProfiles, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.id = ?1 and voucherDetail.inventoryVoucherHeader.document.id in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5")
	Double sumOfAmountByUserIdAndDocumentsAndProductsAndCreatedDateBetween(Long userId, Set<Long> documentIds,
			Set<Long> productProfileIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.pid = ?1 and  voucherDetail.inventoryVoucherHeader.document.id in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5")
	Double sumOfAmountByUserPidAndDocumentsAndProductsAndCreatedDateBetween(String userPid, Set<Long> documentIds,
			Set<Long> productProfileIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.product.id in ?1 and voucherDetail.inventoryVoucherHeader.id in ?2")
	Double sumOfAmountByAndProductIdsAndHeaderIds(Set<Long> productProfileIds, Set<Long> ivHeaderIds);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.id in ?1")
	Double sumOfAmountByHeaderIds(Set<Long> ivHeaderIds);

	@Query("select sum((voucherDetail.quantity*voucherDetail.product.unitQty)/1000) from InventoryVoucherDetail voucherDetail where voucherDetail.product.id in ?1 and voucherDetail.inventoryVoucherHeader.id in ?2")
	Double sumOfVolumeByAndProductIdsAndHeaderIds(Set<Long> productProfileIds, Set<Long> ivHeaderIds);

	@Query("select sum((voucherDetail.quantity*voucherDetail.product.unitQty)/1000) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.id in ?1")
	Double sumOfVolumeByAndHeaderIds(Set<Long> ivHeaderIds);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.pid = ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5")
	Double sumOfAmountByUserPidAndDocumentsAndProductIdInAndDocumentDateBetween(String userPid,
			List<Document> documents, Set<Long> productProfilesIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfAmountByDocumentsAndProductsAndCreatedDateBetween(List<Document> documents,
			List<ProductProfile> productProfiles, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.id in ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfVolumeByUserIdInAndDocumentsAndProductsAndCreatedDateBetween(List<Long> userIds,
			List<Document> documents, List<ProductProfile> productProfiles, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.id = ?1 and  voucherDetail.inventoryVoucherHeader.document.id in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5")
	Double sumOfVolumeByUserIdAndDocumentsAndProductsAndCreatedDateBetween(Long userId, Set<Long> documentIds,
			Set<Long> productProfileIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.pid = ?1 and  voucherDetail.inventoryVoucherHeader.document.id in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5")
	Double sumOfVolumeByUserPidAndDocumentsAndProductsAndCreatedDateBetween(String userPid, Set<Long> documentIds,
			Set<Long> productProfileIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.createdBy.pid = ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5")
	Double sumOfVolumeByUserPidAndDocumentsAndProductIdInAndDocumentDateBetween(String userPid,
			List<Document> documents, Set<Long> productProfileIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	Double sumOfVolumeByDocumentsAndProductsAndCreatedDateBetween(List<Document> documents,
			List<ProductProfile> productProfiles, LocalDateTime fromDate, LocalDateTime toDate);
	// Dashboard end

	// territory wise
	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount in ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfAmountByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(
			List<AccountProfile> accountProfiles, List<Document> documents, List<ProductProfile> productProfiles,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.id in ?1 and  voucherDetail.inventoryVoucherHeader.document.id in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfAmountByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(Set<Long> accountProfileIds,
			Set<Long> documents, Set<Long> productProfiles, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.rowTotal) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid = ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfAmountByDocumentsAndProductsAndAccountProfilePidAndCreatedDateBetween(String accountProfilePid,
			List<Document> documents, List<ProductProfile> productProfiles, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount in ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.product in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfVolumeByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(
			List<AccountProfile> accountProfiles, List<Document> documents, List<ProductProfile> productProfiles,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.id in ?1 and  voucherDetail.inventoryVoucherHeader.document.id in ?2 and voucherDetail.product.id in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	Double sumOfVolumeByDocumentsAndProductsAndAccountProfilesAndCreatedDateBetween(Set<Long> accountProfileIds,
			Set<Long> documents, Set<Long> productProfiles, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail.product.pid,voucherDetail.product.name,sum(voucherDetail.quantity) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.executiveTaskExecution.accountProfile.pid = ?1 and  voucherDetail.inventoryVoucherHeader.document in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 group by voucherDetail.product.pid,voucherDetail.product.name")
	List<Object[]> findPurchaseByAccountDocumentsAndDateBetween(String accountPid, List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.product in ?1 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.accountProfile.pid = ?2 and  voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	Double findPurchaseByProductsAccountAndDateBetween(List<ProductProfile> products, String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail.product.id,sum(voucherDetail.quantity) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.executiveTaskExecution.accountProfile.pid = ?1 and voucherDetail.inventoryVoucherHeader.createdDate between ?2 and ?3 group by voucherDetail.product.id ")
	List<Object[]> findPurchaseByAccountAndDateBetween(String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail.product.id,sum(voucherDetail.quantity) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.accountProfile.pid = ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 group by voucherDetail.product.id ")
	List<Object[]> findPurchaseByDocumentsAccountAndDateBetween(List<Document> documents, String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.sourceStockLocation not in ?2 and voucherDetail.inventoryVoucherHeader.createdDate > ?3")
	List<InventoryVoucherDetail> findByProductPidAndSourceStockLocationNotIn(String productPid,
			List<StockLocation> stockLocations, LocalDateTime date);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.product.pid = ?1 and voucherDetail.destinationStockLocation not in ?2 and voucherDetail.inventoryVoucherHeader.createdDate > ?3")
	List<InventoryVoucherDetail> findByProductPidAndDestinationStockLocationNotIn(String productPid,
			List<StockLocation> stockLocations, LocalDateTime date);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.inventoryVoucherHeader.createdDate between ?2 and ?3")
	List<InventoryVoucherDetail> findByDocumentInAndDateBetween(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select coalesce(sum(voucherDetail.quantity),0) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	Double findPurchaseByDocumentsProductsAndDateBetween(List<Document> documents, List<ProductProfile> productProfiles,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Transactional
	void deleteByInventoryVoucherHeaderIdIn(List<Long> inventoryVoucherHeaderIds);

	// for sorting

	@Query("select voucherDetail.inventoryVoucherHeader.createdDate as createdDateTime,voucherDetail.inventoryVoucherHeader.employee.name as employeeName,voucherDetail.product.productCategory.name as productCategoryName,voucherDetail.product.name as productName,voucherDetail.quantity as quantity,voucherDetail.sellingRate as sellingRate,voucherDetail.rowTotal as rowTotal,voucherDetail.product.pid as productPid from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 and voucherDetail.inventoryVoucherHeader.employee.pid in ?4")
	List<CustomInventoryVoucherDetail> findAllByCompanyIdAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents, List<String> employeePid);

	@Query("select voucherDetail.inventoryVoucherHeader.createdDate,voucherDetail.inventoryVoucherHeader.employee.name,voucherDetail.product.productCategory.name,voucherDetail.product.name,voucherDetail.quantity,voucherDetail.sellingRate,voucherDetail.rowTotal,voucherDetail.product.pid from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid = ?3 and voucherDetail.inventoryVoucherHeader.employee.pid in ?4")
	List<Object[]> findAllByCompanyIdAndDocumentPidAndDateBetween(LocalDateTime fromDate, LocalDateTime toDate,
			String document, List<String> employeePid);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.inventoryVoucherHeader.createdDate desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.inventoryVoucherHeader.createdDate desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByCreatedDateDesc(
			LocalDateTime fromDate, LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.inventoryVoucherHeader.createdDate asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByCreatedDateAsc(
			LocalDateTime fromDate, LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.inventoryVoucherHeader.createdDate asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByCreatedDateAsc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.product.name desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByItemDesc(LocalDateTime fromDate,
			LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.product.name asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByItemAsc(LocalDateTime fromDate,
			LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.quantity desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByQuantityDesc(
			LocalDateTime fromDate, LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.quantity asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByQuantityAsc(
			LocalDateTime fromDate, LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.product.productCategory.name desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByCategoryDesc(
			LocalDateTime fromDate, LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document.pid= ?3 Order By voucherDetail.product.productCategory.name asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDocumentPidAndDateBetweenOrderByCategoryAsc(
			LocalDateTime fromDate, LocalDateTime toDate, String documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.product.name asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByItemAsc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.product.name desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByItemDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.quantity asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByQuantityAsc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.quantity desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByQuantityDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.product.productCategory.name asc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByCategoryAsc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.company.id = ?#{principal.companyId} and voucherDetail.inventoryVoucherHeader.createdDate between ?1 and ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 Order By voucherDetail.product.productCategory.name desc")
	List<InventoryVoucherDetail> findAllByCompanyIdAndDateBetweenOrderByCategoryDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.inventoryVoucherHeader.documentDate between ?2 and ?3")
	List<InventoryVoucherDetail> findByDocumentInAndHeaderDocumentDateBetween(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid in ?1 and voucherDetail.product in ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	List<InventoryVoucherDetail> findAllByCompanyIdAndAccountPidInAndProductPidInAndDateBetween(
			List<String> accountProfilePids, List<ProductProfile> products, List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid = ?1 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.user.pid = ?2 and voucherDetail.inventoryVoucherHeader.document in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	List<InventoryVoucherDetail> findAllByCompanyIdAndAccountPidAndEmployeePidAndDocumentsInAndDateBetween(
			String accountProfilePid, String userPid, List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid = ?1 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.user.pid = ?2 and  voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	List<InventoryVoucherDetail> findAllByCompanyIdAndAccountPidAndEmployeePidAndDateBetween(String accountProfilePid,
			String userPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid in ?1 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.user.pid in ?2 and  voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	List<InventoryVoucherDetail> findAllByCompanyIdAndAccountPidInAndEmployeePidInAndDateBetween(
			List<String> accountProfilePids, List<String> userPids, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid in ?1 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.user.id in ?2 and voucherDetail.inventoryVoucherHeader.document.id in ?3 and voucherDetail.inventoryVoucherHeader.createdDate between ?4 and ?5")
	List<InventoryVoucherDetail> findByCompanyIdAndAccountPidInAndEmployeeIdInAndDocumentIdInDateBetween(
			List<String> accountProfilePids, List<Long> userIds, List<Long> documentIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.product in ?1 and voucherDetail.inventoryVoucherHeader.receiverAccount.pid in ?2 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.user.id in ?3 and voucherDetail.inventoryVoucherHeader.document.id in ?4 and voucherDetail.inventoryVoucherHeader.createdDate between ?5 and ?6")
	List<InventoryVoucherDetail> findByCompanyIdAndProductProfileInAccountPidInAndEmployeeIdInAndDocumentIdInDateBetween(
			List<ProductProfile> productProfiles, List<String> accountProfilePids, List<Long> userIds,
			List<Long> documentIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = "select voucherDetail.inventoryVoucherHeader.employee.pid, voucherDetail.inventoryVoucherHeader.employee.name, voucherDetail.inventoryVoucherHeader.createdDate,voucherDetail.quantity from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document.id in ?1 and voucherDetail.inventoryVoucherHeader.createdDate between ?2 and ?3")
	List<Object[]> findByDocumentIdInAndDateBetween(Set<Long> documentIds, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query(value = "select voucherDetail.inventoryVoucherHeader.employee.name, voucherDetail.product.pid, voucherDetail.product.name, voucherDetail.product.unitQty, voucherDetail.quantity,voucherDetail.inventoryVoucherHeader.receiverAccount.name, voucherDetail.inventoryVoucherHeader.documentDate, voucherDetail.inventoryVoucherHeader.executiveTaskExecution.remarks,voucherDetail.inventoryVoucherHeader.receiverAccount.pid,voucherDetail.rowTotal,voucherDetail.inventoryVoucherHeader.document.name,voucherDetail.volume,voucherDetail.inventoryVoucherHeader.createdDate from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.receiverAccount.pid in ?1 and voucherDetail.inventoryVoucherHeader.createdBy.id in ?2 and voucherDetail.inventoryVoucherHeader.document.id in ?3 and voucherDetail.inventoryVoucherHeader.documentDate between ?4 and ?5 and voucherDetail.product.activated=true order by voucherDetail.inventoryVoucherHeader.documentDate desc")
	List<Object[]> findByAccountPidInAndEmployeeIdInAndDocumentIdInDateBetween(List<String> accountProfilePids,
			List<Long> userIds, List<Long> documentIds, LocalDateTime fromDate, LocalDateTime toDate);

	// Dash board Start
	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetween(List<Document> documents, Set<Long> productIds,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.createdBy.id in ?5")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdIn(List<Document> documents,
			Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate, List<Long> userIds);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.createdBy.id in ?5 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
			List<Document> documents, Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate,
			List<Long> userIds);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndTaskPlanIsNotNull(List<Document> documents,
			Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.createdBy.id in ?5 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNull(
			List<Document> documents, Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate,
			List<Long> userIds);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndTaskPlanIsNull(List<Document> documents,
			Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.createdBy.pid = ?5")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUser(List<Document> documents,
			Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate, String userPid);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.createdBy.pid = ?5 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(
			List<Document> documents, Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate,
			String userPid);

	@Query("select sum(voucherDetail.inventoryVoucherHeader.documentTotal),sum(voucherDetail.quantity * coalesce(voucherDetail.product.unitQty, 1)) from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.document in ?1 and voucherDetail.product.id in ?2 and voucherDetail.inventoryVoucherHeader.createdDate between ?3 and ?4 and voucherDetail.inventoryVoucherHeader.createdBy.pid = ?5 and voucherDetail.inventoryVoucherHeader.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getAmountAndVolumeByDocumentsInAndProductsInAndDateBetweenAndUserIdInAndTaskPlanIsNull(
			List<Document> documents, Set<Long> productIds, LocalDateTime fromDate, LocalDateTime toDate,
			String userPid);

	// Dash board End

	@Query(value = "select voucherDetail.inventoryVoucherHeader.pid, voucherDetail.product.pid, voucherDetail.product.name, voucherDetail.product.unitQty, voucherDetail.quantity ,voucherDetail.updatedQuantity ,voucherDetail.updatedStatus ,voucherDetail.taxPercentage , voucherDetail.sellingRate , voucherDetail.rowTotal ,voucherDetail.discountPercentage from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.pid in ?1")
	List<Object[]> findByInventoryVoucherHeaderPidIn(Set<String> inventoryVoucherHeaderPids);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.pid in ?1")
	List<InventoryVoucherDetail> findAllByInventoryVoucherHeaderPidIn(Set<String> inventoryVoucherHeaderPids);

	@Query(value = "select voucherDetail from InventoryVoucherDetail voucherDetail where voucherDetail.inventoryVoucherHeader.id =?1")
	List<InventoryVoucherDetail> findAllByInventoryVoucherHeaderId(Long inventoryVoucherHeaderids);

	@Query(value = CUSTOMER_BASED_PRODUCTS, nativeQuery = true)
	List<Object[]> getProductTotalQuantityForCustomerByDate(String userLogin, String accountProfilePid,
			LocalDateTime fromDate, LocalDateTime toDate);

	List<InventoryVoucherDetail> findAllById(Long referenceInventoryVoucherDetailId);

	InventoryVoucherDetail findOneById(Long detailId);
}
