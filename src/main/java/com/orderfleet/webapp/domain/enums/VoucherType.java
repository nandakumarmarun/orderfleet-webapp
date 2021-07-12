package com.orderfleet.webapp.domain.enums;

/**
 * The VoucherType enumeration.
 * 
 * This is used in primary/secondary sales document settings
 * 
 * @author Shaheer
 * @since December 31, 2016
 */
public enum VoucherType {
	PRIMARY_SALES, PRIMARY_SALES_ORDER, SECONDARY_SALES, SECONDARY_SALES_ORDER, PURCHASE, PURCHASE_ORDER,
	ECOM_SALES_ORDER, PRIMARY_SALES_RETURN, OTHERS, QUOTATION, DISTRIBUTOR_STOCK_CHECK, RETAILER_STOCK_CHECK_SHELF,
	RETAILER_STOCK_CHECK_GODOWN, TALLY_SALES;
}
