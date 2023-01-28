package com.orderfleet.webapp.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.SalesOrderStatus;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
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
	public static final String SALES_ORDER_EXCEl = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity,ap.name as customerName,ivd.mrp as mrp,ivd.discount_amount as discountAmt,ep.alias as empAlias, ap.customer_id as customerCode, pp.product_code as productCode, pp.name as productName "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' order by ivh.created_date desc";

	public static final String PRIMARY_SECONDARY_SALES_ORDER_EXCEL = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity,ap.name as customerName,ivd.mrp as mrp,ivd.discount_amount as discountAmt,ep.alias as empAlias,ivd.remarks as remarks, ap.customer_id as customerCode, pp.product_code as productCode, pp.name as productName "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' and ivh.document_id in(?2)  order by ivh.created_date desc";

	public static final String PRIMARY_SALES_ORDER_EXCEl = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity,ap.name as customerName,ivd.mrp as mrp,ivd.discount_amount as discountAmt, ep.alias as empAlias, ap.customer_id as customerCode, pp.product_code as productCode, pp.name as productName "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' and ivh.document_id in(?2) order by ivh.created_date desc";

	public static final String PRIMARY_SALES_EXCEl = "select ivh.document_number_server as billno,ivh.document_date as date,ap.alias as customerId,pp.alias as itemId,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity,ap.name as customerName,ivd.mrp as mrp,ivd.discount_amount as discountAmt,ep.alias as empAlias "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile ap on ap.id = ivh.receiver_account_id where ivh.company_id = ?1  and ivh.tally_download_status = 'PENDING' and ivh.document_id in(?2) order by ivh.created_date desc";

	public static final String SECONDARY_SALES_ORDER_EXCEl = "select ivh.document_number_server as billno,ivh.created_date as date,rap.name as receiverName,sap.name as supplierName,sap.email_1 as supplierEmail,pp.name as itemName,ivd.quantity as qty,ivd.selling_rate as rate,ivd.discount_percentage as discountPercentage,ivd.tax_percentage as taxpercentage,ivd.row_total as total,ivh.pid as inventoryPid,ep.name as empName,ivh.reference_document_number as refDocNo,ivd.free_quantity as freeQuantity,ap.name as customerName,ivd.mrp as mrp,ivd.discount_amount as discountAmt,ep.alias as empAlias  "
			+ "from tbl_inventory_voucher_header ivh INNER JOIN tbl_inventory_voucher_detail ivd on ivh.id = ivd.inventory_voucher_header_id "
			+ "INNER JOIN tbl_product_profile pp on pp.id = ivd.product_id INNER JOIN tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "INNER JOIN tbl_account_profile rap on rap.id = ivh.receiver_account_id "
			+ "INNER JOIN tbl_account_profile sap on sap.id = ivh.supplier_account_id where ivh.company_id = ?1  and ivh.send_sales_order_status = 'NOT_SENT' order by ivh.created_date desc";

	public static final String STOCK_DETAILS = "select "
			+ "ivh.created_by_id as users,ivh.created_date,ivh.document_id,pp.name as productName,ivd.product_id,ivd.quantity as sales_qty,"
			+ "op.quantity  as op_qty,sl.id,sl.name,ivh.id as ivh,ivd.id as ivd,ivd.free_quantity as free_quantity,pp.pid as productPid "
			+ "from tbl_inventory_voucher_detail ivd "
			+ "inner join tbl_inventory_voucher_header ivh on ivd.inventory_voucher_header_id = ivh.id "
			+ "inner join tbl_user_stock_location usl on usl.user_id = ivh.created_by_id "
			+ "inner join tbl_stock_location sl on sl.id = usl.stock_location_id "
			+ "inner join tbl_opening_stock op on op.stock_location_id = sl.id "
			+ "inner join tbl_product_profile pp on pp.id = ivd.product_id where ivh.company_id = ?1 and "
			+ "ivh.created_by_id in (?2) and ivh.created_date BETWEEN ?3 AND ?4 and "
			+ "ivd.product_id = op.product_profile_id order by ivd.product_id";

	public static final String SALES_DETAILS = "select "
			+ "ivh.created_by_id as users,ivh.created_date,ivh.document_id,pp.name as productName,ivd.product_id,ivd.quantity as sales_qty,"
			+ "ivh.id as ivh,ivd.id as ivd,ivd.free_quantity as free_quantity,pp.pid as productPid,ep.name as empName,ap.name as accName "
			+ "from tbl_inventory_voucher_detail ivd "
			+ "inner join tbl_inventory_voucher_header ivh on ivd.inventory_voucher_header_id = ivh.id "
			+ "inner join tbl_employee_profile ep on ep.id =ivh.employee_id "
			+ "inner join tbl_account_profile ap on ap.id =ivh.receiver_account_id "
			+ "inner join tbl_product_profile pp on pp.id = ivd.product_id where ivh.company_id = ?1 and "
			+ "ivh.created_by_id in (?2) and ivh.created_date BETWEEN ?3 AND ?4 " + "order by ivd.product_id";

	public static final String STOCK_DETAILS_STOCKLOCATION_BASED = "select "
			+ "ivh.created_by_id as users,ivh.created_date,ivh.document_id,PP.name as productName,ivd.product_id,ivd.quantity as sales_qty,"
			+ "op.quantity  as op_qty,sl.id,sl.name,ivh.id as ivh,ivd.id as ivd,ivd.free_quantity as free_quantity,pp.pid as productPid "
			+ "from tbl_inventory_voucher_detail ivd "
			+ "inner join tbl_inventory_voucher_header ivh on ivd.inventory_voucher_header_id = ivh.id "
			+ "inner join tbl_stock_location sl on sl.id IN ?5 "
			+ "inner join tbl_opening_stock op on op.stock_location_id = sl.id "
			+ "inner join tbl_product_profile pp on pp.id = ivd.product_id where ivh.company_id = ?1 and "
			+ "ivh.created_by_id in (?2) and ivh.created_date BETWEEN ?3 AND ?4 and "
			+ "ivd.product_id = op.product_profile_id order by ivd.product_id";

//	public static final String DOCUMENT_NUMBER = "SELECT iv.document_number_local,doc.pid,iv.created_date from tbl_inventory_voucher_header iv "
//			+ "INNER JOIN tbl_company cmp on iv.company_id = cmp.id "
//			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id "
//			+ "INNER JOIN tbl_user u on iv.created_by_id = u.id where "
//			+ "cmp.pid = ?1 and u.pid = ?2 and doc.pid IN(?3) and iv.created_date IN "
//			+ "(select MAX(ivh.created_date) from tbl_inventory_voucher_header ivh where "
//			+ "ivh.company_id = cmp.id and ivh.created_by_id = u.id and ivh.document_id = doc.id group by ivh.document_id)";
//	
	public static final String DOCUMENT_NUMBER_OPTIMISED = "SELECT iv.document_number_local,doc.pid,iv.created_date from tbl_inventory_voucher_header iv "
			+ "INNER JOIN tbl_company cmp on iv.company_id = cmp.id "
			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id "
			+ "INNER JOIN tbl_user u on iv.created_by_id = u.id where "
			+ "cmp.pid = ?1 and u.pid = ?2 and doc.pid IN(?3) and iv.created_date = ?4";

	public static final String DOCUMENT_NUMBER_OPTIMISED_DATE = "SELECT iv.document_number_local,doc.pid,iv.created_date from tbl_inventory_voucher_header iv "
			+ "INNER JOIN tbl_company cmp on iv.company_id = cmp.id "
			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id "
			+ "INNER JOIN tbl_user u on iv.created_by_id = u.id where "
			+ "cmp.pid = ?1 and u.pid = ?2 and doc.id = ?3 and iv.created_date = ?4";

	public static final String LAST_DOCUMENT_DATE = "select MAX(ivh.created_date) from tbl_inventory_voucher_header ivh "
			+ "INNER JOIN tbl_company cmp on ivh.company_id = cmp.id   "
			+ "INNER JOIN tbl_document doc on ivh.document_id = doc.id  "
			+ "INNER JOIN tbl_user u on ivh.created_by_id = u.id where  "
			+ "cmp.pid = ?1 and u.pid = ?2  and doc.pid IN (?3) and "
			+ "ivh.company_id = cmp.id and ivh.created_by_id = u.id and ivh.document_id = doc.id group by ivh.document_id";

//	public static final String LAST_DOCUMENT_PID_DATE = "select ivh.document_id,MAX(ivh.created_date) from tbl_inventory_voucher_header ivh "
//			+ "INNER JOIN tbl_company cmp on ivh.company_id = cmp.id   "
//			+ "INNER JOIN tbl_document doc on ivh.document_id = doc.id  "
//			+ "INNER JOIN tbl_user u on ivh.created_by_id = u.id where  "
//			+ "cmp.pid = ?1 and u.pid = ?2  and doc.pid IN (?3) and "
//			+ "ivh.company_id = cmp.id and ivh.created_by_id = u.id and ivh.document_id = doc.id group by ivh.document_id";

	public static final String LAST_DOCUMENT_PID_DATE = "select max(cast(coalesce(nullif(SPLIT_PART(ivh.document_number_local, ?4 , 2),''),'0') as bigint)) from tbl_inventory_voucher_header ivh "
			+ "INNER JOIN tbl_company cmp on ivh.company_id = cmp.id   "
			+ "INNER JOIN tbl_document doc on ivh.document_id = doc.id  "
			+ "INNER JOIN tbl_user u on ivh.created_by_id = u.id where  "
			+ "cmp.pid = ?1 and u.pid = ?2  and doc.pid = ?3 and "
			+ "ivh.company_id = cmp.id and ivh.created_by_id = u.id and ivh.document_id = doc.id";

	public static final String ALL_DOCUMENT_NUMBER = "SELECT iv.document_number_local,doc.pid,iv.created_date from tbl_inventory_voucher_header iv "
			+ "INNER JOIN tbl_company cmp on iv.company_id = cmp.id "
			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id "
			+ "INNER JOIN tbl_user u on iv.created_by_id = u.id where "
			+ "cmp.pid = ?1 and u.pid = ?2 and doc.pid IN(?3)";

	public static final String EXCEL_SALES_DOWNLOAD = "select ap.name accName,ivh.order_number,ivh.document_date,emp.name empName, "
			+ " pp.name ppName ,ivd.selling_rate,ivd.quantity , pp.sku  from tbl_inventory_voucher_header ivh "
			+ " INNER JOIN tbl_inventory_voucher_detail ivd on ivd.inventory_voucher_header_id = ivh.id  "
			+ " INNER JOIN tbl_employee_profile emp on ivh.employee_id = emp.id "
			+ " INNER JOIN tbl_product_profile pp on ivd.product_id = pp.id "
			+ " INNER JOIN tbl_account_profile ap on ivh.receiver_account_id = ap.id " + " where ivh.pid IN (?1) ";

	Optional<InventoryVoucherHeader> findOneByPid(String pid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.pid = ?1")
	Optional<InventoryVoucherHeader> findByPid(String pid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.pid = ?1")
	InventoryVoucherHeader findByPidSalesOrder(String pid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdOrderByCreatedDateDesc();

	@Query("select inventoryVoucher.id,inventoryVoucher.receiverAccount.id,inventoryVoucher.document.id,inventoryVoucher.documentDate from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} Order By inventoryVoucher.createdDate desc")
	List<Object[]> findAllByCompanyIdAndOrderByCreatedDateDesc();

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} Order By inventoryVoucher.createdDate desc")
	Page<InventoryVoucherHeader> findAllByCompanyIdOrderByCreatedDateDesc(Pageable pageable);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdDate between ?1 and ?2 and inventoryVoucher.document in ?3 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndDateBetweenOrderByCreatedDateDesc(LocalDateTime fromDate,
			LocalDateTime toDate, List<Document> documents);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.id in ?1 and inventoryVoucher.document in ?2 and inventoryVoucher.createdDate between ?3 and ?4 Order By inventoryVoucher.createdDate desc")
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

	@Query("select inventoryVoucher.id,inventoryVoucher.pid,inventoryVoucher.documentDate from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.pid = ?1 and inventoryVoucher.receiverAccount.pid = ?2 and inventoryVoucher.document.pid = ?3 Order By inventoryVoucher.createdDate desc")
	List<Object[]> findAllByCompanyIdUserPidAccountPidAndDocumentPid(String userPid, String accountPid,
			String documentPid);

	@Query("select sum(inventoryVoucher.documentTotal) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.createdBy.login = ?#{principal.username} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Double getCurrentUserAchievedAmount(List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select sum(inventoryVoucher.documentVolume) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.createdBy.login = ?#{principal.username} and inventoryVoucher.document in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Double getCurrentUserAchievedVolume(List<Document> documents, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select count(inventoryVoucher.id) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.sendSalesOrderEmailStatus = ?1")
	Long getCountOfSendSalesOrderEmailNotSent(SendSalesOrderEmailStatus sendSalesOrderEmailStatus);

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

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and status=false Order By inventoryVoucher.createdDate desc")
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

	@Query(value = "SELECT * FROM tbl_inventory_voucher_header WHERE created_by_id = :userLoginid AND document_id = :documentid ORDER BY created_date desc LIMIT 1", nativeQuery = true)
	InventoryVoucherHeader findTop1ByCreatedByLoginOrderAndDocumentPidByCreatedDateDesc(
			@Param("userLoginid") Long userLoginid, @Param("documentid") Long documentid);

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

	@Query("select inventoryVoucher.employee.name, inventoryVoucher.employee.user.login, inventoryVoucher.document.name,inventoryVoucher.id,inventoryVoucher.pid from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.document in ?1 and inventoryVoucher.executiveTaskExecution.id in ?2")
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

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.executiveTaskExecution.user.id = ?1 and inventoryVoucher.documentDate between ?2 and ?3 Order By inventoryVoucher.documentDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserIdAndDateBetweenOrderByDocumentDateDesc(Long userid,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.executiveTaskExecution.user.id = ?1 and inventoryVoucher.createdDate between ?2 and ?3 Order By inventoryVoucher.documentDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdUserPidAndDateBetweenOrderByDocumentDateDesc(Long userId,
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

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.sendSalesOrderEmailStatus =?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findAllByCompanyIdAndSendEmailStatusNotSent(SendSalesOrderEmailStatus notSent);

	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.status = TRUE  WHERE  iv.company.id = ?#{principal.companyId}  AND iv.pid in ?1")
	int updateInventoryVoucherHeaderStatusUsingPid(List<String> inventoryPids);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.status,iv.receiverAccount.description from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.document.pid in ?2 and iv.status in ?3 and iv.createdDate between ?4 and ?5 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndDocumentPidInAndStatusDateBetweenOrderByCreatedDateDesc(List<Long> userIds,
			List<String> documentPids, List<Boolean> status, LocalDateTime fromDate, LocalDateTime toDate);

	// using for tally download status filtering
	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks ,iv.orderNumber , iv.pdfDownloadStatus ,iv.salesManagementStatus,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,iv.executiveTaskExecution.sendDate,iv.receiverAccount.location,iv.receiverAccount.description,iv.salesOrderStatus,iv.executiveTaskExecution.location,iv.executiveTaskExecution.pid,iv.executiveTaskExecution.latitude,iv.deliveryDate from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.document.pid in ?2 and iv.tallyDownloadStatus in ?3 and iv.createdDate between ?4 and ?5 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks ,iv.orderNumber , iv.pdfDownloadStatus ,iv.salesManagementStatus,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,iv.executiveTaskExecution.sendDate,iv.receiverAccount.location,iv.receiverAccount.description,iv.salesOrderStatus,iv.executiveTaskExecution.location,iv.executiveTaskExecution.pid,iv.executiveTaskExecution.latitude,iv.deliveryDate from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.document.pid in ?2 and iv.tallyDownloadStatus in ?3 and iv.createdDate between ?4 and ?5 and iv.salesOrderStatus in ?6 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndDocumentPidInAndTallyDownloadStatusandsaleOrderstausDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, List<String> documentPids, List<TallyDownloadStatus> tallyDownloadStatus,
			LocalDateTime fromDate, LocalDateTime toDate, List<SalesOrderStatus> salesOrderStatus);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.status , iv.executiveTaskExecution.remarks, iv.orderNumber , iv.pdfDownloadStatus,iv.salesManagementStatus,iv.receiverAccount.description from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.receiverAccount.pid = ?2 and iv.document.pid in ?3 and iv.status in ?4 and iv.createdDate between ?5 and ?6 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndAccountPidInAndDocumentPidInAndStatusDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, String accountPids, List<String> documentPids, List<Boolean> status,
			LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks ,iv.orderNumber , iv.pdfDownloadStatus ,iv.salesManagementStatus ,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,iv.executiveTaskExecution.sendDate,iv.receiverAccount.location from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.pid in ?1  "
			+ " Order By iv.createdDate desc")
	List<Object[]> findByPidsOrderByCreatedDateDesc(List<String> inventoryHeaderPids);

	// using for tally download status filtering
	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks , iv.orderNumber , iv.pdfDownloadStatus,iv.salesManagementStatus,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,iv.executiveTaskExecution.sendDate,iv.receiverAccount.location,iv.receiverAccount.description,iv.salesOrderStatus from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.receiverAccount.pid = ?2 and iv.document.pid in ?3 and iv.tallyDownloadStatus in ?4 and iv.createdDate between ?5 and ?6 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, String accountPids, List<String> documentPids,
			List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks , iv.orderNumber , iv.pdfDownloadStatus,iv.salesManagementStatus,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,iv.executiveTaskExecution.sendDate,iv.receiverAccount.location,iv.receiverAccount.description,iv.salesOrderStatus,iv.executiveTaskExecution.location,iv.executiveTaskExecution.pid,iv.executiveTaskExecution.latitude,iv.deliveryDate from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.receiverAccount.pid = ?2 and iv.document.pid in ?3 and iv.tallyDownloadStatus in ?4 and iv.createdDate between ?5 and ?6 and  iv.salesOrderStatus in ?7 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndAccountPidInAndDocumentPidInAndTallyDownloadStatusaStatusdDateBetweenOrderByCreatedDateDesc(
			List<Long> userIds, String accountPids, List<String> documentPids,
			List<TallyDownloadStatus> tallyDownloadStatus, LocalDateTime fromDate, LocalDateTime toDate,
			List<SalesOrderStatus> salesOrderStatus);

	@Modifying(clearAutomatically = true)
	@Transactional
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
//	@Query(value = DOCUMENT_NUMBER, nativeQuery = true)
//	List<Object[]> getLastNumberForEachDocument(String companyPid, String userPid, List<String> documentPids);

	@Query(value = DOCUMENT_NUMBER_OPTIMISED, nativeQuery = true)
	List<Object[]> getLastNumberForEachDocumentOptimized(String companyPid, String userPid, List<String> documentPids,
			LocalDateTime lastDate);

	@Query(value = DOCUMENT_NUMBER_OPTIMISED_DATE, nativeQuery = true)
	List<Object[]> getLastNumberForEachDocumentsDateOptimized(String companyPid, String userPid, long documentId,
			LocalDateTime lastDate);

	@Query(value = LAST_DOCUMENT_DATE, nativeQuery = true)
	LocalDateTime lastDateWithCompanyUserDocument(String companyPid, String userPid, List<String> documentPids);

//	@Query(value = LAST_DOCUMENT_PID_DATE, nativeQuery = true)
//	List<Object[]> lastDatesWithCompanyUserDocuments(String companyPid, String userPid, List<String> documentPids);

	@Query(value = LAST_DOCUMENT_PID_DATE, nativeQuery = true)
	Long getHigestDocumentNumberwithoutPrefix(String companyPid, String userPid, String documentPid, String prefix);

	@Query(value = ALL_DOCUMENT_NUMBER, nativeQuery = true)
	List<Object[]> getAllDocumentNumberForEachDocument(String companyPid, String userPid, List<String> documentPids);

	@Query(value = "select count(iv.document_id) ,doc.pid ,iv.document_id from tbl_inventory_voucher_header iv "
			+ "INNER JOIN tbl_document doc on iv.document_id = doc.id where iv.company_id = ?1 group by iv.document_id,doc.pid	", nativeQuery = true)
	List<Object[]> findCountOfInventoryVoucherHeader(long company_id);

	@Query(value = SALES_ORDER_DOWNLOAD, nativeQuery = true)
	List<Object[]> findInventoryVouchersByPidIn(List<String> pids);

	@Query(value = SALES_ORDER_EXCEl, nativeQuery = true)
	List<Object[]> getSalesOrderForExcel(Long companyId);

	@Query(value = PRIMARY_SECONDARY_SALES_ORDER_EXCEL, nativeQuery = true)
	List<Object[]> getPrimarySecondarySalesOrderForExcel(Long companyId, List<Long> documentIds);

	@Query(value = STOCK_DETAILS_STOCKLOCATION_BASED, nativeQuery = true)
	List<Object[]> getAllStockDetailsByStockLocations(Long companyId, Long userId, LocalDateTime fromDate,
			LocalDateTime toDate, Set<Long> stockLocationIds);

	@Query(value = STOCK_DETAILS, nativeQuery = true)
	List<Object[]> getAllStockDetails(Long companyId, Long userId, LocalDateTime fromDate, LocalDateTime toDate);

	@Query(value = SALES_DETAILS, nativeQuery = true)
	List<Object[]> getAllSalesDetails(Long companyId, List<Long> userId, LocalDateTime fromDate, LocalDateTime toDate);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.tallyDownloadStatus = ?1  WHERE  iv.company.id = ?2  AND iv.pid in ?3")
	int updateInventoryVoucherHeaderTallyDownloadStatusUsingPidAndCompanyId(TallyDownloadStatus tallyDownloadStatus,
			Long companyId, List<String> inventoryPids);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.tallyDownloadStatus = ?1  WHERE  iv.company.id = ?2  AND iv.documentNumberServer in ?3")
	int updateInventoryVoucherHeaderDownloadStatusUsingDocumentNumberServerAndCompanyId(
			TallyDownloadStatus tallyDownloadStatus, Long companyId, List<String> documentNumberServer);

	@Query(value = PRIMARY_SALES_ORDER_EXCEl, nativeQuery = true)
	List<Object[]> getPrimarySalesOrderForExcel(Long companyId, List<Long> documentIds);

	@Query(value = PRIMARY_SALES_EXCEl, nativeQuery = true)
	List<Object[]> getPrimarySalesForExcel(Long companyId, List<Long> documentIds);

	@Query(value = SECONDARY_SALES_ORDER_EXCEl, nativeQuery = true)
	List<Object[]> getSecondarySalesForExcel(Long companyId);

	@Query(value = EXCEL_SALES_DOWNLOAD, nativeQuery = true)
	List<Object[]> getExcelFileSales(List<String> ivhPids);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE  InventoryVoucherHeader iv SET iv.pdfDownloadStatus=true WHERE iv.pid=?1")
	void updatePdfDownlodStatusByPid(String pid);

	@Query("select iv.pid,iv.document.name,iv.document.pid from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.executiveTaskExecution.pid = ?1")
	List<Object[]> findInventoryVoucherHeaderByExecutiveTaskExecutionPid(String exeTasKPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.pid = ?1 Order By inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findInventoryVoucherHeaderByPid(String inventoryVoucherHeaderPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.referenceDocumentNumber = ?1")
	List<InventoryVoucherHeader> findInventoryVoucherHeaderByDocumennumber(String rfrdocument);

	@Query("select iv.pid,iv.document.name,iv.document.pid,iv.documentTotal from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.executiveTaskExecution.id in ?1")
	List<Object[]> findInventoryVoucherHeaderByExecutiveTaskExecutionIdIn(Set<Long> exeIds);

	public static final String SALES_ORDER__MANAGEMENT_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status , document_total_updated, document_volume_updated, updated_status  "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and sales_management_status = 'APPROVE' and company_id = ?#{principal.companyId} order by created_date desc";

	public static final String PRIMARY_SALES_ORDER__MANAGEMENT_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status , reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and company_id = ?#{principal.companyId} order by created_date desc";

	// FOR VAN SALES TO TALLY
	public static final String DOCUMENT_BASED_ORDER_DOWNLOAD_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status , reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and document_id IN(?1) and company_id = ?#{principal.companyId} order by created_date desc";

	public static final String DOCUMENT_BASED_ORDER_DOWNLOAD_TALLY_LIMIT = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status , reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and document_id IN(?1) and company_id = ?#{principal.companyId} order by created_date asc LIMIT 20";

	public static final String DOCUMENT_BASED_ORDER_MANAGEMENT_DOWNLOAD_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status , reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and sales_management_status = 'APPROVE' and document_id IN(?1) and company_id = ?#{principal.companyId} order by created_date desc";

	public static final String DOCUMENT_BASED_SALE_ORDER_DOWNLOAD_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status , reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and sales_management_status = 'APPROVE' and sales_order_status = 'CONFIRM'  and document_id IN(?1) and company_id = ?#{principal.companyId} order by created_date desc";

	public static final String DOCUMENT_BASED_ORDER_MANAGEMENT_DOWNLOAD_TALLY_LIMIT = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status , reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and sales_management_status = 'APPROVE' and document_id IN(?1) and company_id = ?#{principal.companyId} order by created_date asc LIMIT 20";

	public static final String OPTIMISED_INVENTORY_QUERY = "SELECT id,receiver_account_id,document_id,document_date FROM tbl_inventory_voucher_header WHERE company_id = ?#{principal.companyId} order by created_date desc";

	public static final String EMPLOYEE_SALES_ORDER__MANAGEMENT_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status, reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and sales_management_status = 'APPROVE' and company_id = ?#{principal.companyId} and employee_id in ?1 order by created_date desc";

	public static final String EMPLOYEE_PRIMARY_SALES_ORDER__MANAGEMENT_TALLY = "SELECT id, created_date, doc_discount_amount, doc_discount_percentage, document_date, document_number_local, document_number_server, document_total, document_volume, pid, status, company_id, created_by_id, "
			+ "document_id, employee_id, executive_task_execution_id, receiver_account_id, supplier_account_id, price_level_id, reference_document_number, reference_document_type, source_module, process_status, order_status_id, updated_date, "
			+ "updated_by_id, tally_download_status, order_number, pdf_download_status, sales_management_status, document_total_updated, document_volume_updated, updated_status, reference_invoice_number , rounded_off "
			+ "FROM tbl_inventory_voucher_header where tally_download_status ='PENDING' and company_id = ?#{principal.companyId} and employee_id in ?1 order by created_date desc";

	@Query(value = SALES_ORDER__MANAGEMENT_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndSalesManagementStatusOrderByCreatedDateDesc();

	@Query(value = PRIMARY_SALES_ORDER__MANAGEMENT_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusOrderByCreatedDateDesc();

	@Query(value = DOCUMENT_BASED_ORDER_DOWNLOAD_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateDesc(List<Long> documentIds);

	@Query(value = DOCUMENT_BASED_ORDER_MANAGEMENT_DOWNLOAD_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateDesc(
			List<Long> documentIds);

	@Query(value = DOCUMENT_BASED_SALE_ORDER_DOWNLOAD_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndSalesOrderStatusAndDocumentOrderByCreatedDateDesc(
			List<Long> documentIds);

	@Query(value = EMPLOYEE_SALES_ORDER__MANAGEMENT_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndSalesManagementStatusAndEmployeeOrderByCreatedDateDesc(
			List<Long> empId);

	@Query(value = EMPLOYEE_PRIMARY_SALES_ORDER__MANAGEMENT_TALLY, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusOrderAndEmployeeByCreatedDateDesc(List<Long> empId);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query("UPDATE InventoryVoucherHeader iv SET iv.sendSalesOrderEmailStatus = ?1  WHERE  iv.company.id = ?2  AND iv.pid in ?3")
	int updateInventoryVoucherHeaderSendSalesOrderEmailStatusUsingPidAndCompanyId(
			SendSalesOrderEmailStatus sendSalesOrderEmailStatus, Long companyId, List<String> inventoryPids);

	@Query(value = "select count(*),doc.pid from tbl_inventory_voucher_header ivh "
			+ "inner join tbl_document doc on ivh.document_id = doc.id "
			+ "where ivh.company_id = ?#{principal.companyId} and ivh.created_by_id = ?1 "
			+ "and ivh.created_date between ?2 and ?3  group by doc.pid", nativeQuery = true)
	List<Object[]> findCountOfEachInventoryTypeDocuments(long userId, LocalDateTime fDate, LocalDateTime tDate);

	// using for process flow status filtering
	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks ,iv.orderNumber , iv.pdfDownloadStatus,"
			+ "iv.salesManagementStatus ,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,"
			+ "iv.executiveTaskExecution.sendDate ,iv.processFlowStatus ,iv.paymentReceived,iv.bookingId,iv.deliveryDate,"
			+ "iv.executiveTaskExecution.id,iv.executiveTaskExecution.pid,iv.remarks,iv.receiverAccount.description,iv.receiverAccount.phone1 from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.document.pid in ?2 and iv.processFlowStatus in ?3 and iv.createdDate between ?4 and ?5 and iv.rejectedStatus= ?6 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndDocumentPidInAndProcessFlowStatusStatusAndDateBetweenAndRejectedStatusOrderByCreatedDateDesc(
			List<Long> userIds, List<String> documentPids, List<ProcessFlowStatus> processStatus,
			LocalDateTime fromDate, LocalDateTime toDate, boolean rejectedStatus);

	// using for tally download status filtering accountPid
	@Query("select iv.pid, iv.documentNumberLocal, iv.documentNumberServer, iv.document.pid, iv.document.name, iv.createdDate, "
			+ "iv.documentDate, iv.receiverAccount.pid, iv.receiverAccount.name, iv.supplierAccount.pid, "
			+ "iv.supplierAccount.name, iv.employee.pid, iv.employee.name, iv.createdBy.firstName,iv.documentTotal, "
			+ "iv.documentVolume, iv.tallyDownloadStatus, iv.executiveTaskExecution.remarks , iv.orderNumber , iv.pdfDownloadStatus,"
			+ "iv.salesManagementStatus ,iv.documentTotalUpdated ,iv.documentVolumeUpdated ,iv.updatedStatus ,iv.sendSalesOrderEmailStatus ,"
			+ "iv.executiveTaskExecution.sendDate ,iv.processFlowStatus ,iv.paymentReceived,iv.bookingId,iv.deliveryDate,"
			+ "iv.executiveTaskExecution.id,iv.executiveTaskExecution.pid,iv.remarks,iv.receiverAccount.description,iv.receiverAccount.phone1 from InventoryVoucherHeader iv where iv.company.id = ?#{principal.companyId} and iv.createdBy.id in ?1 and "
			+ "iv.receiverAccount.pid = ?2 and iv.document.pid in ?3 and iv.processFlowStatus in ?4 and iv.createdDate between ?5 and ?6 and iv.rejectedStatus= ?7 Order By iv.createdDate desc")
	List<Object[]> findByUserIdInAndAccountPidInAndDocumentPidInAndProcessFlowStatusAndDateBetweenAndRejectedStatusOrderByCreatedDateDesc(
			List<Long> userIds, String accountPid, List<String> documentPids, List<ProcessFlowStatus> processStatus,
			LocalDateTime fromDate, LocalDateTime toDate, boolean rejectedStatus);

	@Query("select sum(inventoryVoucher.documentTotal) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.createdBy.id in ?1 and inventoryVoucher.createdDate between ?2 and ?3")
	Object[] findnetSaleAmountByUserIdandDateBetween(List<Long> userIds, LocalDateTime fromDate, LocalDateTime toDate);

	@Query("select inventoryVoucher.pid from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.documentNumberServer in ?1")
	List<String> findAllByDocumentNumberServer(List<String> references);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.documentNumberServer in ?1")
	List<InventoryVoucherHeader> findAllHeaderdByDocumentNumberServer(List<String> inventoryHeaderPids);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.pid = ?2 and inventoryVoucher.documentNumberServer = ?1")
	InventoryVoucherHeader findOneHeaderByDocumentNumberServerAndCompanyPid(String documentNumber, String companyPid);

//	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher LEFT JOIN FETCH inventoryVoucher.inventoryVoucherDetails where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.pid = ?1 Order By inventoryVoucher.createdDate desc")
//	List<InventoryVoucherHeader> findInventoryVoucherHeaderByPid(String inventoryVoucherHeaderPid);

	public static final String CUSTOMER_WISE_INVENTORY_HEADER = "select ivh.createdDate, ivh.docDiscountAmount, ivh.docDiscountPercentage, ivh.documentDate, "
			+ "ivh.documentNumberLocal, ivh.documentNumberServer, ivh.documentTotal, ivh.documentVolume, "
			+ "ivh.pid, ivh.processStatus, ivh.employee.pid, ivh.employee.name, ivh.employee.alias, "
			+ "ivh.receiverAccount.pid, ivh.receiverAccount.name, ivh.receiverAccount.alias, "
			+ "ivh.supplierAccount.pid, ivh.supplierAccount.name, " + "ivh.document.name, ivh.document.pid "
			+ "from InventoryVoucherHeader ivh where ivh.createdBy.login =?1 and ivh.receiverAccount.pid in ?2 "
			+ "and ivh.createdDate between ?3 and ?4 Order By ivh.createdDate desc";

	@Query(value = CUSTOMER_WISE_INVENTORY_HEADER)
	List<Object[]> getCustomerWiseInventoryHeader(String userName, List<String> accountPids, LocalDateTime fromDate,
			LocalDateTime toDate);

	public static final String CUSTOMER_WISE_INVENTORY_DETAIL = "SELECT ivd.product.name, ivd.quantity, ivd.product.unitQty, ivd.freeQuantity, ivd.sellingRate, "
			+ "ivd.taxPercentage, ivd.discountAmount, ivd.discountPercentage, ivd.rowTotal, ivd.product.pid "
			+ "	FROM InventoryVoucherDetail ivd where ivd.inventoryVoucherHeader.pid =?1";

	@Query(value = CUSTOMER_WISE_INVENTORY_DETAIL)
	List<Object[]> getCustomerWiseInventoryDetail(String headerPid);

	@Query(value = OPTIMISED_INVENTORY_QUERY, nativeQuery = true)
	List<Object[]> findAllByCompanyIdAndOrderByCreatedDateDescOptimised();

	@Query("select inventoryVoucher.pid,inventoryVoucher.document.name,inventoryVoucher.documentTotal,inventoryVoucher.document.documentType,inventoryVoucher.documentVolume,inventoryVoucher.executiveTaskExecution.pid,inventoryVoucher.documentNumberServer,inventoryVoucher.createdDate from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id IN ?1")
	List<Object[]> findByExecutiveTaskExecutionIdIn(Set<Long> exeIds);

	@Query("select inventoryVoucher.pid,inventoryVoucher.document.name,inventoryVoucher.documentTotal,inventoryVoucher.document.documentType,inventoryVoucher.documentVolume,inventoryVoucher.executiveTaskExecution.pid,inventoryVoucher.documentNumberServer,inventoryVoucher.createdDate from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id IN ?1 and inventoryVoucher.document.pid = ?2")
	List<Object[]> findByExecutiveTaskExecutionIdInAndDocumentPid(Set<Long> exeIds, String documentPid);

	@Query(value = DOCUMENT_BASED_ORDER_MANAGEMENT_DOWNLOAD_TALLY_LIMIT, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndSalesManagementStatusAndDocumentOrderByCreatedDateAscLimit(
			List<Long> documentIdList);

	@Query(value = DOCUMENT_BASED_ORDER_DOWNLOAD_TALLY_LIMIT, nativeQuery = true)
	List<Object[]> findByCompanyIdAndTallyStatusAndDocumentOrderByCreatedDateAscLimit(List<Long> documentIdList);

	@Query("select inventoryVoucher.documentNumberServer,inventoryVoucher.documentTotal,inventoryVoucher.documentDate from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.receiverAccount.pid = ?1 and inventoryVoucher.createdBy.pid = ?2 and inventoryVoucher.company.id = ?#{principal.companyId}")
	List<Object[]> findAllByCompanyIdAccountPidUserAndDateCreatedDateDesc(String accountPid, String userPid);

	@Query("select inventoryVoucher.executiveTaskExecution.id from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.pid = ?1  and inventoryVoucher.company.id = ?#{principal.companyId}")
	Long findExecutiveTaskExecutionIdByPId(String ivhPid);

	@Query("select sum(inventoryVoucher.documentTotal) from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.salesLedger.id = ?1 and inventoryVoucher.id in ?2")
	Double sumOfAmountByAndSalesLedgerIdAndHeaderIds(Long salesLedgerId, Set<Long> ivHeaderIds);

	Optional<InventoryVoucherHeader> findOneByExecutiveTaskExecutionPidAndImageRefNo(String executiveTaskExecutionPid,
			String imageRefNo);

	@Query("select count(inventoryVoucher),sum(inventoryVoucher.documentTotal),MIN(inventoryVoucher.createdDate),MAX(inventoryVoucher.createdDate),inventoryVoucher.employee.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.document in ?1 and inventoryVoucher.executiveTaskExecution.id in ?2 Group By inventoryVoucher.employee.name ")
	List<Object[]> findByDocumentsAndExecutiveTaskIdIn(List<Document> documents, List<Long> eteIds);

	@Query("select inventoryVoucher.pid from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.document in ?1 and inventoryVoucher.executiveTaskExecution.id in ?2")
	List<Object[]> findByDocumentsAndExecutiveIdIn(List<Document> documents, Set<Long> exeIds);

	@Query("select inventoryVoucher.documentNumberServer,inventoryVoucher.executiveTaskExecution.pid,inventoryVoucher.documentTotal,inventoryVoucher.document.documentType,inventoryVoucher.createdDate  from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id IN ?1 and inventoryVoucher.document.pid IN ?2 and inventoryVoucher.company.id = ?#{principal.companyId} order by inventoryVoucher.createdDate desc")
	List<Object[]> findByExecutiveTaskExecutionsIdInAndDocumentPidIn(Set<Long> exeIds, List<String> DocPid);

	@Query("select inventoryVoucher from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id IN ?1 and inventoryVoucher.document.pid IN ?2 and inventoryVoucher.company.id = ?#{principal.companyId} order by inventoryVoucher.createdDate desc")
	List<InventoryVoucherHeader> findByExecutiveTaskExecutionIdInAndDocumentsPidIn(Set<Long> exeIds, List<String> DocPid);

	Optional<InventoryVoucherHeader> findOneByImageRefNo(String imageRefNo);

	@Query("select inventoryVoucher.documentNumberServer,inventoryVoucher.executiveTaskExecution.id,inventoryVoucher.documentTotal,inventoryVoucher.document.documentType,inventoryVoucher.createdDate,inventoryVoucher.pid,inventoryVoucher.employee.name from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.documentNumberLocal IN ?1 and inventoryVoucher.company.id = ?#{principal.companyId} order by inventoryVoucher.createdDate desc")
	List<Object[]> findByDocumentNumberlocalIn(List<String> invoiceNo);

	@Query("select inventoryVoucher.documentTotal,inventoryVoucher.employee.name,inventoryVoucher.documentNumberServer from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.executiveTaskExecution.id in ?1 ")
	List<Object[]> findByExecutiveTaskExecutionIdIn(List<Long> eteIds);

	 @Query("select inventoryVoucher.id, inventoryVoucher.documentNumberLocal, inventoryVoucher.employee.name, inventoryVoucher.documentDate, inventoryVoucher.receiverAccount.name, inventoryVoucher.receiverAccount.address, inventoryVoucher.supplierAccount.name,inventoryVoucher.receiverAccount.location from InventoryVoucherHeader inventoryVoucher where inventoryVoucher.company.id = ?#{principal.companyId} and inventoryVoucher.pid in ?1 ")
 	List<Object[]> findByExecutiveTaskExecutionpIdIn(List<String> ids);
	
}
