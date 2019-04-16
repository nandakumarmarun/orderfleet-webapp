package com.orderfleet.webapp.web.rest.util;

import java.util.Arrays;
import java.util.List;

import com.orderfleet.webapp.domain.AccountingVoucherColumn;
import com.orderfleet.webapp.domain.InventoryVoucherColumn;

public final class DocumentColumnsUtil {

	//MRP, DISCOUNT_PERCENTAGE newly added
	public static List<InventoryVoucherColumn> inventory_voucher_column = Arrays.asList(new InventoryVoucherColumn("PRODUCT"),new InventoryVoucherColumn("QUANTITY"),new InventoryVoucherColumn("SELLING_RATE"),new InventoryVoucherColumn("STOCK"),new InventoryVoucherColumn("MRP"),new InventoryVoucherColumn("DISCOUNT_PERCENTAGE"));
	
	public static List<AccountingVoucherColumn> accounting_voucher_column = Arrays.asList(new AccountingVoucherColumn("AMOUNT"),new AccountingVoucherColumn("BANK_NAME"),new AccountingVoucherColumn("CHEQUE_DATE"),new AccountingVoucherColumn("CHEQUE_NO"),new AccountingVoucherColumn("OUTSTANDING_AMOUNT"),new AccountingVoucherColumn("TYPE"),new AccountingVoucherColumn("REMARKS"));
}
