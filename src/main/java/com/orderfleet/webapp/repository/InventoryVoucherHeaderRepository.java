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
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

public interface InventoryVoucherHeaderRepository extends JpaRepository<InventoryVoucherHeader, Long> {

	public static final String SALES_ORDER_TALLY = "select ivh.document_date as date,ap.trim_char as trimChar,ap.name as ledgerName,ap.address as ledgerAddress,pl.name as priceLevelName,pp.name as productProfileName,pp.tax_rate as taxRate,pp.sku as unit,ivd.selling_rate as sellingRate,ivd.quantity as quantity,ivd.discount_percentage as discountPercent,ivh.document_total as documentTotal,ivd.free_quantity as freeQuantity,ivd.remarks as remarks,sl.name as stockLocationName,ivh.pid as pid,ep.name as empName, d.id as documentId, pg.tax_rate as productGroupTax from tbl_inventory_voucher_header ivh "
			+ " INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id LEFT JOIN tbl_stock_location sl on sl.id = ivd.source_stock_location_id INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id LEFT  JOIN tbl_price_level pl on pl.id =ivh.price_level_id INNER JOIN tbl_document d on d.id = ivh.document_id INNER JOIN tbl_product_group_product pgp on pgp.product_id = pp.id "
			+ " INNER JOIN tbl_product_group pg on pg.id = pgp.product_group_id "
			+ " where ivh.company_id = ?1 and ivh.tally_download_status = 'PENDING' and d.id in ?2 order by ivh.created_date desc";
	public static final String SALES_ORDER_VENDOR = "select ivh.document_date as date,ap.alias as customerId,ep.alias as employeeId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.discount_amount as discountAmount,ivd.row_total as netAmount,ivd.tax_amount as addnAmount,ivh.document_total as billAmount,ivh.pid as inventoryPid "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' and ivh.document_id in(?2) order by ivh.created_date desc";
	public static final String SALES_ORDER_DOWNLOAD = "select ivh.document_number_local as document_number_local , u.first_name as user_name , ivh.document_date as document_date ,rap.name as receiver_account , sap.name as supplier_account , pp.name as product_name,ivd.quantity as quantity, pp.unit_qty as product_unit_qty,ivd.free_quantity as free_quantity,ivd.selling_rate as selling_rate,ivd.discount_amount as discount_amount,ivd.discount_percentage as discount_percentage, ivd.tax_amount as tax_amount, ivd.row_total as row_total\r\n"
			+ "from tbl_inventory_voucher_header ivh "
			+ "INNER JOIN tbl_inventory_voucher_detail ivd on ivd.inventory_voucher_header_id = ivh.id "
			+ "INNER JOIN tbl_user u on ivh.created_by_id = u.id "
			+ "INNER JOIN tbl_account_profile rap on ivh.receiver_account_id = rap.id "
			+ "INNER JOIN tbl_account_profile sap on ivh.supplier_account_id = sap.id "
			+ "INNER JOIN tbl_product_profile pp on ivd.product_id = pp.id " + "where ivh.pid IN ?1";
	public static final String SALES_ORDER_EXCEl = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity,ap.name as customerName,ivd.mrp as mrp,ivd.discount_amount as discountAmt "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' order by ivh.created_date desc";

	public static final String PRIMARY_SALES_ORDER_EXCEl = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' and ivh.document_id in(?2) order by ivh.created_date desc";

	public static final String PRIMARY_SALES_EXCEl = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' and ivh.document_id in(?2) order by ivh.created_date desc";

	public static final String STOCK_DETAILS = "select "
			+ "ivh.created_by_id as users,ivh.created_date,pp.name as productName,ivd.product_id,ivd.quantity as sales_qty,"
			+ "op.quantity  as op_qty,sl.id,sl.name,ivh.id as ivh,ivd.id as ivd "
			+ "from tbl_inventory_voucher_detail ivd "
			+ "inner join tbl_inventory_voucher_header ivh on ivd.inventory_voucher_header_id = ivh.id "
			+ "inner join tbl_user_stock_location usl on usl.user_id = ivh.created_by_id "
			+ "inner join tbl_stock_location sl on sl.id = usl.stock_location_id "
			+ "inner join tbl_opening_stock op on op.stock_location_id = sl.id "
			+ "inner join tbl_product_profile pp on pp.id = ivd.product_id where ivh.company_id = ?1 and "
			+ "ivh.created_by_id in (?2) and ivh.created_date BETWEEN ?3 AND ?4 and "
			+ "ivd.product_id = op.product_profile_id order by ivd.product_id";

	public static final String DOCUMENT_NUMBER = "SELECT iv.document_number_local,doc.pid,iv.created_date from tbl_inventory_voucher_header iv "
			+ "INNER JOIN tbl_company cmp on iv.company_id = cmp.id "
			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id "
			+ "INNER JOIN tbl_user u on iv.created_by_id = u.id where "
			+ "cmp.pid = ?1 and u.pid = ?2 and doc.pid IN(?3) and iv.created_date IN "
			+ "(select MAX(ivh.created_date) from tbl_inventory_voucher_header ivh where "
			+ "ivh.company_id = cmp.id and ivh.created_by_id = u.id and ivh.document_id = doc.id group by ivh.document_id)";

	Optional<InventoryVoucherHeader> findOneByPid(String pid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdOrderByCreatedDateDesc();

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} Order By inventoryVoucher.createdDate desc")
	Page<InventoryVoucherHeader> findAllByCompanyIdOrderByCreatedDateDesc(Pageable pageable);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdDate between ?1 and ?2 and inventoryVoucher.document in ?3 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.id in ?1 and inventoryVoucher.document in ?2 and inventoryVoucher.createdDate between ?3 and ?4 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndUserIdInAndDocumentsInAndDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.pid = ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.document in ?4 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.receiverAccount.pid = ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.document in ?4 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAccountPidAndDateBetweenOrderByCreatedDateDesc(String accountPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.pid = ?1 and inventoryVoucher.receiverAccount.pid = ?2 and inventoryVoucher.createdDate between ?3 and ?4 and inventoryVoucher.document in ?5 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserPidAccountPidAndDateBetweenOrderByCreatedDateDesc(String userPid,
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents);

	@Query("select sum(inventoryVoucher.documentTotal) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.createdBy.login = ?#{principal.username} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Double getCurrentUserAchievedAmount(List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.createdBy.login = ?#{principal.username} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Double getCurrentUserAchievedVolume(List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Object getCountAmountAndVolumeByDocumentsAndDateBetween(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.pid = ?4 ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUser(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, String userPid);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndTaskPlanIsNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.pid = ?4  and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserAndTaskPlanIsNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, String userPid);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.pid = ?4 and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, String userPid);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume)  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.createdBy.pid = ?4 and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserId(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, String userPid);

	@Query("select sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume)  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.accountProfile.pid = ?1 and  inventoryVoucher.createdDate between ?2 and ?3  and inventoryVoucher.document in ?4 ")
	Object getAmountAndVolumeByAccountPidAndDateBetween(String accountPid, LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents);

	@Query("select inventoryVoucher.pid,inventoryVoucher.document.name,inventoryVoucher.documentTotal,inventoryVoucher.document.documentType,inventoryVoucher.documentVolume from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id = ?1")
	List<Object[]> findByExecutiveTaskExecutionId(Long executiveTaskExecutionId);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdDate between ?1 and ?2")
	Object getCountAndAmountAndVolumeByDateBetween(LocalDateTime fromDate, LocalDateTime toDate);

	List<InventoryVoucherHeader> findByExecutiveTaskExecutionPidAndDocumentPid(String executiveTaskExecutionPid,
			String documentPid);

	List<InventoryVoucherHeader> findByExecutiveTaskExecutionPidAndDocumentEditableTrue(
			String executiveTaskExecutionPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and status=false Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndStatusOrderByCreatedDateDesc();

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?1 and status=false Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndStatusOrderByCreatedDateDesc(long companyId);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and tallyDownloadStatus='PENDING' Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndTallyStatusOrderByCreatedDateDesc();

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and tallyDownloadStatus='PENDING' and salesManagementStatus='APPROVE' Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and tallyDownloadStatus='PENDING' and inventoryVoucher.employee.id IN ?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndTallyStatusAndEmployeeOrderByCreatedDateDesc(
			List<Long> employeeIds);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and tallyDownloadStatus='PENDING' and salesManagementStatus='APPROVE' and inventoryVoucher.employee.id IN ?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndTallyStatusAndSalesManagementStatusAndEmployeeOrderByCreatedDateDesc(
			List<Long> employeeIds);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.executiveTaskExecution.pid=?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByExecutiveTaskExecutionPid(String executiveTaskExecutionPid);

	List<InventoryVoucherHeader> findByExecutiveTaskExecutionAccountProfilePidAndDocumentPid(String accountProfilePid,
			String documentPid);

	InventoryVoucherHeader findTop1ByCreatedByLoginOrderByCreatedDateDesc(String userLogin);

	@Query("select sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume)  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.accountProfile.pid in ?1 and inventoryVoucher.createdDate between ?2 and ?3  and inventoryVoucher.document in ?4 ")
	Object getAmountAndVolumeByAccountInAndDocumentsInDateBetween(List<String> accountPids, LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and status=false and inventoryVoucher.document in ?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndStatusAndDocumentsOrderByCreatedDateDesc(
			List<Document> documents);

	@Query("select inventoryVoucher.document from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.createdBy.id in ?1")
	Set<Document> findDocumentsByUserIdIn(List<Long> userIds);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.documentDate=?1 and inventoryVoucher.executiveTaskExecution.activity.pid=?2 and inventoryVoucher.document.pid=?3 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndDocumentDateAndActivityAndDocumentOrderByCreatedDateDesc(
			LocalDateTime documentDate, String activityPid, String documentPid);

	// User wise - start
	@Query("select inventoryVoucher.employee.name, inventoryVoucher.employee.user.login, inventoryVoucher.document.name  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.document in ?1 and inventoryVoucher.documentDate between ?2 and ?3 and inventoryVoucher.employee.user.id in ?4 ORDER BY inventoryVoucher.employee.name ASC")
	List<Object[]> findByDocumentsAndDateBetweenAndUserIdInOrderByEmployeeNameAsc(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<Long> userIds);

	@Query("select inventoryVoucher.employee.name, inventoryVoucher.employee.user.login, inventoryVoucher.document.name  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.document in ?1 and inventoryVoucher.executiveTaskExecution.id in ?2")
	List<Object[]> findByDocumentsAndExecutiveTaskExecutionIdIn(List<Document> documents, List<Long> eteIds);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.id in ?4")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdIn(List<Document> documents, LocalDateTime fromDate,
			LocalDateTime toDate, List<Long> userIds);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.id in ?4 and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNotNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<Long> userIds);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.id in ?4 and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndUserIdInAndTaskPlanIsNull(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<Long> userIds);
	// User wise - end

	// ##### Account wise - start #####
	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.receiverAccount in ?4 ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndAccountProfileIn(List<Document> documents,
			LocalDateTime fromDate, LocalDateTime toDate, List<AccountProfile> accountProfiles);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.receiverAccount in ?4 and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NOT NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNotNull(
			List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate,
			List<AccountProfile> accountProfiles);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.receiverAccount in ?4  and inventoryVoucher.executiveTaskExecution.executiveTaskPlan IS NULL ")
	Object getCountAmountAndVolumeByDocumentsAndDateBetweenAndAccountProfileInAndTaskPlanIsNull(
			List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate,
			List<AccountProfile> accountProfiles);

	// ##### Account wise - end #####
	@Query("select sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume)  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.accountProfile.pid = ?1 and  inventoryVoucher.documentDate between ?2 and ?3  and inventoryVoucher.document in ?4")
	Object getAmountAndVolumeByAccountPidAndDocumentDateBetween(String accountPid, LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdDate between ?1 and ?2 and inventoryVoucher.document in ?3 and inventoryVoucher.status = ?4 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndDateBetweenAndStatusOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents, boolean status);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.pid = ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.document in ?4 and inventoryVoucher.status = ?5 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserPidAndDateBetweenAndStatusOrderByCreatedDateDesc(String userPid,
			LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, boolean status);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.receiverAccount.pid = ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.document in ?4 and inventoryVoucher.status = ?5 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(
			String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents, boolean status);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.pid = ?1 and inventoryVoucher.receiverAccount.pid = ?2 and inventoryVoucher.createdDate between ?3 and ?4 and inventoryVoucher.document in ?5 and inventoryVoucher.status = ?6 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserPidAccountPidAndDateBetweenAndStatusOrderByCreatedDateDesc(
			String userPid, String accountPid, LocalDateTime fromDate, LocalDateTime toDate, List<Document> documents,
			boolean status);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdDate between ?1 and ?2 and inventoryVoucher.document.pid in ?3")
	Object getCountAndAmountAndVolumeByDateBetweenAndDocumentPid(LocalDateTime fromDate, LocalDateTime toDate,
			String documentPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdDate between ?1 and ?2 and inventoryVoucher.document.pid = ?3 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByDocumentPidAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, String documentPid);

	// for financial closing
	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.document.pid = ?1 and inventoryVoucher.createdDate between ?2 and ?3 and inventoryVoucher.createdBy.pid = ?4 ")
	List<InventoryVoucherHeader> getByDocumentPidAndDateBetweenAndUserPid(String documentPid, LocalDateTime fromDate,
			LocalDateTime toDate, String userPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.pid = ?1 and inventoryVoucher.receiverAccount.pid in ?2 and inventoryVoucher.createdDate between ?3 and ?4 and inventoryVoucher.document in ?5 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserPidAccountPidInAndDateBetweenOrderByCreatedDateDesc(
			String userPid, List<String> accountPids, LocalDateTime fromDate, LocalDateTime toDate,
			List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.executiveTaskExecution.user.id = ?1 and inventoryVoucher.documentDate between ?2 and ?3 Order By inventoryVoucher.documentDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserIdAndDateBetweenOrderByDocumentDateDesc(Long userid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.executiveTaskExecution.user.id = ?1 Order By inventoryVoucher.documentDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserIdOrderByDocumentDateDesc(Long userid);

	List<InventoryVoucherHeader> findTop3ByCompanyIdAndExecutiveTaskExecutionUserPidOrderByDocumentDateDesc(
			Long companyId, String userPid);

	// for set Order Status

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.documentDate between ?1 and ?2 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdDateBetweenOrderByDocumentDateDesc(LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.employee.pid = ?1 and inventoryVoucher.documentDate between ?2 and ?3 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdEmployeePidAndDateBetweenOrderByDocumentDateDesc(String employeePid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.orderStatus.id = ?1  and inventoryVoucher.documentDate between ?2 and ?3 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdOrderStatusAndDateBetweenOrderByDocumentDateDesc(Long status,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.receiverAccount.pid = ?1 and inventoryVoucher.documentDate between ?2 and ?3 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdAccountPidAndDateBetweenOrderByDocumentDateDesc(String accountPids,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.receiverAccount.pid = ?1 and inventoryVoucher.orderStatus.id = ?2 and inventoryVoucher.documentDate between ?3 and ?4 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdOrderStatusIdAccountPidAndDateBetweenOrderByDocumentDateDesc(String accountPid,
			Long status, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.employee.pid = ?1 and inventoryVoucher.orderStatus.id = ?2  and inventoryVoucher.documentDate between ?3 and ?4 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdEmployeePidOrderStatusIdAndDateBetweenOrderByDocumentDateDesc(String employeePid,
			Long status, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.employee.pid = ?1 and inventoryVoucher.orderStatus.id = ?2 and inventoryVoucher.receiverAccount.pid = ?3 and inventoryVoucher.documentDate between ?4 and ?5 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdEmployeePidOrderStatusIdAccountPidAndDateBetweenOrderByDocumentDateDesc(
			String employeePid, Long status, String accountPid, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.documentDate,inventoryVoucher.employee.name,inventoryVoucher.receiverAccount.name,inventoryVoucher.documentTotal,inventoryVoucher.orderStatus.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.employee.pid = ?1 and inventoryVoucher.receiverAccount.pid = ?2 and inventoryVoucher.documentDate between ?3 and ?4 Order By inventoryVoucher.documentDate desc")
	List<Object[]> findAllByCompanyIdEmployeePidAccountPidAndDateBetweenOrderByDocumentDateDesc(String employeePid,
			String accountPids, LocalDateTime fromDate, LocalDateTime toDate);

	// Activities and Transaction filter by document
	@Query("select inventoryVoucher.pid,inventoryVoucher.document.name,inventoryVoucher.documentTotal,inventoryVoucher.document.documentType,inventoryVoucher.documentVolume from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id = ?1 and inventoryVoucher.document.pid = ?2")
	List<Object[]> findByExecutiveTaskExecutionIdAndDocumentPid(Long executiveTaskExecutionId, String documentPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.pid in ?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndInventoryPidIn(List<String> inventoryPids);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.status = TRUE  WHERE  iv.company.id = ?#{principal.companyId}  AND iv.pid in ?1")
	int updateInventoryVoucherHeaderStatusUsingPid(List<String> inventoryPids);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.status from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.document.pid in ?2 and iv.status in ?3 and iv.createdDate between ?4 and ?5 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndDocumentPidInAndStatusDateBetweenOrderByCreatedDateDesc(List<Long> userIds,
			List<String> documentPids, List<Boolean> status, LocalDateTime fromDate, LocalDateTime toDate);

	// using for tally download status filtering
	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks ,iv.orderNumber , iv.pdfDownloadStatus ,iv.salesManagementStatus from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.document.pid in ?2 and iv.tallyDownloadStatus in ?3 and iv.createdDate between ?4 and ?5 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.status , iv.executiveTaskExecution.remarks, iv.orderNumber , iv.pdfDownloadStatus,iv.salesManagementStatus from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.receiverAccount.pid = ?2 and iv.document.pid in ?3 and iv.status in ?4 and iv.createdDate between ?5 and ?6 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndAccountPidInAndDocumentPidInAndStatusDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, String accountPids, List<String> documentPids, List<Boolean> status,
			LocalDateTime fromDate, LocalDateTime toDate);

	// using for tally download status filtering
	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks , iv.orderNumber , iv.pdfDownloadStatus,iv.salesManagementStatus from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.receiverAccount.pid = ?2 and iv.document.pid in ?3 and iv.tallyDownloadStatus in ?4 and iv.createdDate between ?5 and ?6 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, String accountPids, List<String> documentPids,
			List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.tallyDownloadStatus = ?1  WHERE  iv.company.id = ?#{principal.companyId}  AND iv.pid in ?2")
	int updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(TallyDownloadStatus tallyDownloadStatus,
			List<String> inventoryPids);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.processStatus, iv.executiveTaskExecution.remarks ,iv.orderNumber ,iv.pdfDownloadStatus from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and "
			+ "iv.receiverAccount.pid = ?1 and iv.document.id in ?2 Order By iv.documentDate desc")
	List<Object[]> findByAccountPidInAndDocumentPidInOrderByDocumentDateDesc(String accountPid, Set<Long> documentIds);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.processStatus, iv.executiveTaskExecution.remarks , iv.orderNumber, iv.pdfDownloadStatus from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and "
			+ "iv.receiverAccount.pid = ?1 and iv.document.id in ?2 and iv.status = ?3 and iv.documentDate between ?4 and ?5 Order By iv.documentDate desc")
	List<Object[]> findByAccountPidInAndDocumentPidInAndTallyStatusAndDocumentDateBetweenOrderByDocumentDateDesc(
			String accountPid, Set<Long> documentIds, Boolean tallyStatus, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.processStatus, iv.executiveTaskExecution.remarks from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and "
			+ "iv.receiverAccount.pid = ?1 and iv.document.id in ?2 and iv.documentDate between ?3 and ?4 Order By iv.documentDate desc")
	List<Object[]> findByAccountPidInAndDocumentPidInAndDocumentDateBetweenOrderByDocumentDateDesc(String accountPid,
			Set<Long> documentIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.processStatus, iv.executiveTaskExecution.remarks from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and "
			+ "iv.receiverAccount.id in ?1 and iv.document.pid = ?2 and iv.status in ?3 and iv.documentDate between ?4 and ?5 Order By iv.documentDate desc")
	List<Object[]> findByAccountIdInAndDocumentPidAndTallyStatusAndDocumentDateBetweenOrderByDocumentDateDesc(
			Set<Long> accountIds, String documentPid, List<Boolean> tallyStatus, LocalDateTime fromDate,
			LocalDateTime toDate);

	@Query("select iv.id from InventoryVoucherHeader iv where iv.createdBy.pid = ?1 and iv.document.id in ?2 and iv.documentDate between ?3 and ?4")
	Set<Long> findIdByUserPidAndDocumentsAndProductsAndCreatedDateBetween(String userPid, Set<Long> documentIds,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select iv.id from InventoryVoucherHeader iv where iv.receiverAccount.id in ?1 and iv.document.id in ?2 and iv.documentDate between ?3 and ?4")
	Set<Long> findIdByAccountProfileAndDocumentDateBetween(Set<Long> accountProfileIds, List<Long> documentIds,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Transactional
	@Modifying
	@Query("UPDATE InventoryVoucherHeader iv SET iv.status = TRUE WHERE iv.company.id = ?1 AND iv.pid in ?2")
	int updateAllInventoryVoucherHeaderStatusUsingPid(long companyId, List<String> inventoryPids);

	@Query("select inventoryVoucher.pid from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?1 and status=false Order By inventoryVoucher.createdDate desc")
	List<String> findPidByStatus(long companyId);

	@Query(value = SALES_ORDER_TALLY, nativeQuery = true)
	List<Object[]> getSalesOrdersForTally(Long companyId, Set<Long> documentIds);

	@Query(value = SALES_ORDER_VENDOR, nativeQuery = true)
	List<Object[]> getSalesOrderForVendor(long companyId, List<Long> documentId);

	// @Query("select MAX(iv.documentNumberLocal),iv.document.pid "
	// + "from InventoryVoucherHeader iv where iv.company.pid = ?1 and "
	// + "iv.employee.user.pid = ?2 and iv.document.pid IN ?3 group by
	// iv.document.pid")
	// List<Object[]> getLastNumberForEachDocument(String companyPid, String
	// userPid, List<String> documentPids);
	@Query(value = DOCUMENT_NUMBER, nativeQuery = true)
	List<Object[]> getLastNumberForEachDocument(String companyPid, String userPid, List<String> documentPids);

	@Query(value = "select count(iv.document_id) ,doc.pid ,iv.document_id from tbl_inventory_voucher_header iv "
			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id where iv.company_id = ?1 group by iv.document_id,doc.pid	", nativeQuery = true)
	List<Object[]> findCountOfInventoryVoucherHeader(long company_id);

	@Query(value = SALES_ORDER_DOWNLOAD, nativeQuery = true)
	List<Object[]> findInventoryVouchersByPidIn(List<String> pids);

	@Query(value = SALES_ORDER_EXCEl, nativeQuery = true)
	List<Object[]> getSalesOrderForExcel(Long companyId);

	@Query(value = STOCK_DETAILS, nativeQuery = true)
	List<Object[]> getAllStockDetails(Long companyId, Long userId, LocalDateTime fromDate, LocalDateTime toDate);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.tallyDownloadStatus = ?1  WHERE  iv.company.id = ?2  AND iv.pid in ?3")
	int updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(TallyDownloadStatus tallyDownloadStatus,
			Long companyId, List<String> inventoryPids);

	@Query(value = PRIMARY_SALES_ORDER_EXCEl, nativeQuery = true)
	List<Object[]> getPrimarySalesOrderForExcel(Long companyId, List<Long> documentIds);

	@Query(value = PRIMARY_SALES_EXCEl, nativeQuery = true)
	List<Object[]> getPrimarySalesForExcel(Long companyId, List<Long> documentIds);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  InventoryVoucherHeader iv SET iv.pdfDownloadStatus=true WHERE iv.pid=?1")
	void updatePdfDownlodStatusByPid(String pid);

}
