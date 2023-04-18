package com.orderfleet.webapp.web.vendor.focus.Thread;

import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.web.vendor.focus.service.SendSalesOrderFocusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class SaleOrderFocusThread extends Thread {
	
	private static String AUTHENTICATE_API_URL = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/Getlogin";
	private static String API_URL_SALES_ORDER = "http://23.111.12.87/DevaSteelsIntegration/FocusService.svc/SalesOrder?Auth_Token=";

	private final Logger log = LoggerFactory.getLogger(SaleOrderFocusThread.class);
	
	
	private SendSalesOrderFocusService sendSalesOrderFocusService;
	
	
	private List<InventoryVoucherHeader> inventoryVoucherHeaders;

	public SaleOrderFocusThread(List<InventoryVoucherHeader> inventoryVouchers, SendSalesOrderFocusService sendSalesOrderFocusService) {
		this.inventoryVoucherHeaders = inventoryVouchers;
		this.sendSalesOrderFocusService = sendSalesOrderFocusService;
	}

	

	@Override
	public void run() {
		log.debug("Entering to thread");
         inventoryVoucherHeaders.forEach(data -> log.debug(data.getDocumentNumberLocal() + " Id :"+data.getCompany().getId()));
		try {
			log.debug("please wait some time.....");
            log.info("Started");
			Thread.sleep(30000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.debug("Ended");
     sendSalesOrderFocusService.salesOrderPushToFocus(inventoryVoucherHeaders);
	}
	

}
