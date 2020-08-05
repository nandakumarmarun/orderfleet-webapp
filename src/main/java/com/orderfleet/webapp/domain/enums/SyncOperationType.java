package com.orderfleet.webapp.domain.enums;

/**
 * The SyncOperationType enumeration.
 *
 * @author Sarath
 * @since Mar 13, 2018
 *
 */
public enum SyncOperationType {

	PRODUCTCATEGORY, PRODUCTGROUP, PRODUCTPROFILE, PRODUCTGROUP_PRODUCTPROFILE, PRICE_LEVEL, 
	PRICE_LEVEL_LIST, STOCK_LOCATION, OPENING_STOCK, ACCOUNT_PROFILE, LOCATION, LOCATION_HIRARCHY, 
	LOCATION_ACCOUNT_PROFILE, RECEIVABLE_PAYABLE, PRODUCT_WISE_DEFAULT_LEDGER, 
	ACCOUNT_PROFILE_CLOSING_BALANCE, GST_PRODUCT_GROUP, TAX_MASTER, PRODUCT_PROFILE_TAX_MASTER, 
	EXECUTIVE_TASK_EXECUTION, SALES_ORDER, SALES_VOUCHER, RECEIPT, DAILY_RECEIPT, 
	ECOM_PRODUCT_PROFILE, ECOM_PRODUCT_PROFILE_PRODUCT, PRODUCT_GROUP_ECOM_PRODUCT, 
	PRICELEVEL_ACCOUNT_PRODUCTGROUP, DOWNLOAD_ACCOUNT_PROFILE, POST_DATED_VOUCHER, GST_LEDGER,
	SALES_BY_VOUCHER,JOURNAL;
}
