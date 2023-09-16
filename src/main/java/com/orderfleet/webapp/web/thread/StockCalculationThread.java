package com.orderfleet.webapp.web.thread;

import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.service.StockCalculationService;
import com.orderfleet.webapp.service.impl.StockCalculationServiceImpl;
import com.orderfleet.webapp.web.vendor.focus.Thread.SaleOrderFocusThread;
import com.orderfleet.webapp.web.vendor.focus.service.SendSalesOrderFocusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;

public class StockCalculationThread extends Thread {

    private final Logger log = LoggerFactory.getLogger(SaleOrderFocusThread.class);

    @Inject
    private final StockCalculationService stockCalculationService;

    private final List<InventoryVoucherHeader> inventoryVoucherHeaders;

    private final String threadNo;

    public StockCalculationThread(List<InventoryVoucherHeader> inventoryVouchers, String thread, StockCalculationService stockCalculationService) {
        this.inventoryVoucherHeaders = inventoryVouchers;
        this.threadNo = thread;
        this.stockCalculationService = stockCalculationService;

    }

    @Override
    public void run() {
        log.debug(" Thread " + threadNo + "running");
        try {
            log.debug("please wait some time");
            log.debug("thread " + threadNo + " Sleeping at " + LocalDateTime.now());
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.debug("Restarting" + threadNo + LocalDateTime.now());
        inventoryVoucherHeaders.forEach(data -> {
            stockCalculationService.saveSolidOrders(data, threadNo);
        });

    }
}
