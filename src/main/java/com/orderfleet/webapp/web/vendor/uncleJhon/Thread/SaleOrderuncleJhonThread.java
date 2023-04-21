package com.orderfleet.webapp.web.vendor.uncleJhon.Thread;

import java.util.List;

import javax.mail.MessagingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.web.vendor.uncleJhon.service.SendSalesOrderEmailService;

public class SaleOrderuncleJhonThread extends Thread{
	
private final Logger log = LoggerFactory.getLogger(SaleOrderuncleJhonThread.class);
	
	
	private SendSalesOrderEmailService sendSalesOrderEmailService;
	
	
	private List<InventoryVoucherHeader> inventoryVoucherHeaders;

	public SaleOrderuncleJhonThread(List<InventoryVoucherHeader> inventoryVouchers, SendSalesOrderEmailService sendSalesOrderEmailService) {
		this.inventoryVoucherHeaders = inventoryVouchers;
		this.sendSalesOrderEmailService = sendSalesOrderEmailService;
	}

	
      @Override
	public void run() {
		log.debug("Entering to thread");
         inventoryVoucherHeaders.forEach(data -> log.debug(data.getDocumentNumberLocal() + " Id :"+data.getCompany().getId()));
		try {
			log.debug("please wait some time.....");
            log.info("Started");
			Thread.sleep(30000);
			sendSalesOrderEmailService.salesOrderEmailToSupplier(inventoryVoucherHeaders);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		 catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.debug("Ended");
		
	}
	

}
