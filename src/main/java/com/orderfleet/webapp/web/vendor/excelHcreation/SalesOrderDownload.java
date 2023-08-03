package com.orderfleet.webapp.web.vendor.excelHcreation;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.InventoryVoucherHeaderRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.excel.dto.SalesOrderExcelDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api/excel/v1")
public class SalesOrderDownload {

    private final Logger log = LoggerFactory.getLogger(SalesOrderDownload.class);

    @Inject
    private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

    @Inject
    private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private UserRepository userRepository;

    @RequestMapping(value = "/download-sales-orders.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Transactional
    public List<SalesOrderExcelDTO> downloadSalesOrderJSON() throws URISyntaxException {
        log.debug("REST request to download sales orders : {}");
        List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
        List<String> inventoryHeaderPid = new ArrayList<String>();

        Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
        System.out.println("companyName :"+company.getLegalName());
        List<Object[]> inventoryVoucherHeaders = inventoryVoucherHeaderRepository.getSalesOrderForExcel(company.getId());

        log.debug("REST request to download sales orders : " + inventoryVoucherHeaders.size());

        salesOrderDTOs = downloadInventoryVoucherList(inventoryVoucherHeaders);

        return salesOrderDTOs;
    }

    private List<SalesOrderExcelDTO> downloadInventoryVoucherList(List<Object[]> inventoryVoucherHeaders)
    {
         log.info("Size :"+inventoryVoucherHeaders.size());
        List<SalesOrderExcelDTO> salesOrderDTOs = new ArrayList<>();
        List<String> inventoryHeaderPid = new ArrayList<String>();

        for (Object[] obj : inventoryVoucherHeaders) {
            SalesOrderExcelDTO salesOrderDTO = new SalesOrderExcelDTO();


            String pattern = "dd-MMM-yy";
            DateFormat df = new SimpleDateFormat(pattern);
            String dateAsString = df.format(obj[1]);

            salesOrderDTO.setDate(dateAsString != null ? dateAsString : "");
            salesOrderDTO.setCustomerCode(obj[2] != null ? obj[2].toString() : "");
            salesOrderDTO.setItemCode(obj[3] != null ? obj[3].toString() : "");
            salesOrderDTO.setQuantity(obj[4] != null ? Double.parseDouble(obj[4].toString()) : 0.0);
            salesOrderDTO.setRate(obj[5] != null ? Double.parseDouble(obj[5].toString()) : 0.0);

            double qty = Double.parseDouble(obj[4] != null ? obj[4].toString() : "0.0");
            double rate = Double.parseDouble(obj[5] != null ? obj[5].toString() : "0.0");


            double amountValue = qty * rate;
            DecimalFormat round = new DecimalFormat(".##");

            salesOrderDTO.setTotal(Double.parseDouble(round.format(amountValue)));
            salesOrderDTO.setInventoryPid(obj[9] != null ? obj[9].toString() : "");

            String uniquString = RandomUtil.generateServerDocumentNo().substring(0,
                    RandomUtil.generateServerDocumentNo().length() - 10);

            salesOrderDTO.setBillNo(obj[0].toString());
            salesOrderDTO.setCustomerName(obj[13] != null ? obj[13].toString() : "");
            inventoryHeaderPid.add(obj[9] != null ? obj[9].toString() : "");
            System.out.println("================================================================");
            System.out.println(salesOrderDTO.toString());
            System.out.println("================================================================");
            salesOrderDTOs.add(salesOrderDTO);

        }

        if (!salesOrderDTOs.isEmpty()) {
log.debug("Enter here...................:"+salesOrderDTOs.size());
            inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
                    TallyDownloadStatus.PROCESSING, inventoryHeaderPid);

            log.debug("updated  to PROCESSING");
        }
        return salesOrderDTOs;
    }
    @PostMapping("/update-salesorder-status.json")
    @Timed
    @Transactional
    public ResponseEntity<String> updateOrderStatus(@RequestBody List<String> inventoryVoucherHeaderPids) {
        Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

        log.debug("REST request to update Inventory Voucher Header Status (" + company.getLegalName() + ") : {}",
                inventoryVoucherHeaderPids.size());

        if (!inventoryVoucherHeaderPids.isEmpty()) {

            int updated = inventoryVoucherHeaderRepository.updateInventoryVoucherHeaderTallyDownloadStatusUsingPid(
                    TallyDownloadStatus.COMPLETED, inventoryVoucherHeaderPids);
            log.debug("updated " + updated + " to Completed");
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


}
