package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.scheduler.ExternalApiScheduledTasks;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.InvoiceDetailsDenormalizedService;
import com.orderfleet.webapp.service.util.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class InvoiceDetailsToDenormalizedTable {

    private static final Logger log = LoggerFactory.getLogger(InvoiceDetailsToDenormalizedTable.class);
    @Inject
    private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;
    @Inject
    private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;
    @Inject
    private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;
    @Inject
    private AccountingVoucherHeaderRepository accountingVoucherHeaderRepository;
    @Inject
    private AccountingVoucherDetailRepository accountingVoucherDetailRepository;
    @Inject
    private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;
    @Inject
    private FilledFormRepository filledFormRepository;
    @Inject
    private FilledFormDetailRepository filledFormDetailRepository;

    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Inject
    private InvoiceDetailsDenormalizedRepository invoiceDetailsDenormalizedRepository;

    @Transactional
    public ResponseEntity<HttpStatus> filterExecutiveTaskExecutionsDenormalized(List<ExecutiveTaskExecution> executiveTaskExecutions) {

          Set<Long> exeids = executiveTaskExecutions.stream().map(exe->exe.getId()).collect(Collectors.toSet());
            List<InventoryVoucherHeader> inventoryVoucherHeaders = new ArrayList<>();
              if(exeids.size()>0){
                inventoryVoucherHeaders =  inventoryVoucherHeaderRepository.findAllByExecutiveTaskExecutionIdIn(exeids);
              }
              System.out.println("Size of Inventory :"+inventoryVoucherHeaders.size());
              Set<String>ivPids  =  inventoryVoucherHeaders.stream().map(obj->obj.getPid()).collect(Collectors.toSet());
              List<InventoryVoucherDetail> inventoryVoucherDetails = new ArrayList<>();
              if(ivPids.size()>0)
               {
                inventoryVoucherDetails = inventoryVoucherDetailRepository.findAllByInventoryVoucherHeaderPidIn(ivPids);
               }
               System.out.println("Size of Inventory Details :"+inventoryVoucherDetails.size());
           List<AccountingVoucherHeader> accountingVoucherHeaders= new ArrayList<>();
               if(exeids.size()>0) {
                    accountingVoucherHeaders = accountingVoucherHeaderRepository.findAllByExecutiveTaskExecutionIdIn(exeids);
               }
               System.out.println("Size of Accounting:"+accountingVoucherHeaders.size());
               Set<String> accPids = accountingVoucherHeaders.stream().map(acc -> acc.getPid()).collect(Collectors.toSet());
               List<AccountingVoucherDetail> accountingVoucherDetails = new ArrayList<>();
               if(accPids.size() >0)
               {
                   accountingVoucherDetails = accountingVoucherDetailRepository.findAllByAccountingVoucherHeaderPidIn(accPids);
               }
               System.out.println("Size of accounting Details :"+accountingVoucherDetails.size());
               List<DynamicDocumentHeader> dynamicDocumentHeaders = new ArrayList<>();
               if(exeids.size()>0)
               {
                   dynamicDocumentHeaders = dynamicDocumentHeaderRepository.findAllByExecutiveTaskExecutionIdIn(exeids);
               }
               System.out.println("Size of dynamicDocuments:"+dynamicDocumentHeaders.size());
               Set<String> dynamicPid = dynamicDocumentHeaders.stream().map(ddh -> ddh.getPid()).collect(Collectors.toSet());
               List<FilledForm> filledForms= new ArrayList<>();
               if(dynamicPid.size()>0)
               {
                   filledForms = filledFormRepository.findByDynamicDocumentHeaderPidIn(dynamicPid);
               }

               Set<String> filledformPid = filledForms.stream().map(ff->ff.getPid()).collect(Collectors.toSet());
               List<FilledFormDetail> filledFormDetails = new ArrayList<>();
               if(filledformPid.size()>0)
               {
                   filledFormDetails = filledFormDetailRepository.findByFilledFormPidIn(filledformPid);
               }
        LocalDate fDate = LocalDate.now();
        LocalDateTime fromDate = fDate.atTime(0, 0);
        LocalDateTime toDate = fDate.atTime(23, 59);
               List<InvoiceDetailsDenormalized> invoiceDetailsDenormalized = invoiceDetailsDenormalizedRepository.findAllByCreatedDateBetween(fromDate,toDate);
                  System.out.println("Size of Invoice:"+invoiceDetailsDenormalized.size());
                  List<InvoiceDetailsDenormalized> invoiceDetailsDenormalizedList = new ArrayList<>();
                for(ExecutiveTaskExecution executiveTaskExecution :executiveTaskExecutions)
                {
                    Optional<InvoiceDetailsDenormalized> invoice = invoiceDetailsDenormalized.stream().filter(a ->a.getExecutionPid().equalsIgnoreCase( executiveTaskExecution.getPid())).findAny();
                    System.out.println("Is invoice :"+invoice.isPresent());
                  if(!invoice.isPresent()) {
                      System.out.println("Enter executive Task Execution");
                      if (inventoryVoucherHeaders.size() > 0) {
                          for (InventoryVoucherHeader iVHeader : inventoryVoucherHeaders) {

                              System.out.println("Enter Inventory Voucher");
                              if (executiveTaskExecution.getPid().equalsIgnoreCase(iVHeader.getExecutiveTaskExecution().getPid())) {
                                  for (InventoryVoucherDetail voucherDetail : inventoryVoucherDetails) {
                                      if (voucherDetail.getInventoryVoucherHeader().getPid().equalsIgnoreCase(iVHeader.getPid())) {
                                          System.out.println("Enter the voucherDetail");
                                          InvoiceDetailsDenormalized invoiceDetails = new InvoiceDetailsDenormalized();
                                          //Execution Data
                                          invoiceDetails.setPid(InvoiceDetailsDenormalizedService.PID_PREFIX + RandomUtil.generatePid());
                                          invoiceDetails.setExecutionPid(executiveTaskExecution.getPid());
                                          invoiceDetails.setAccountProfilePid(executiveTaskExecution.getAccountProfile().getPid());
                                          invoiceDetails.setAccountProfileName(executiveTaskExecution.getAccountProfile().getName());
                                          invoiceDetails.setActivityPid(executiveTaskExecution.getActivity().getPid());
                                          invoiceDetails.setActivityName(executiveTaskExecution.getActivity().getName());
                                          invoiceDetails.setPunchInDate(executiveTaskExecution.getPunchInDate());
                                          invoiceDetails.setSendDate(executiveTaskExecution.getSendDate());
                                          invoiceDetails.setCreatedDate(executiveTaskExecution.getCreatedDate());
                                          invoiceDetails.setLocation(executiveTaskExecution.getLocation());
                                          invoiceDetails.setTowerLocation(executiveTaskExecution.getTowerLocation());
                                          invoiceDetails.setWithCustomer(executiveTaskExecution.getWithCustomer());
                                          invoiceDetails.setLocationType(executiveTaskExecution.getLocationType());
                                          invoiceDetails.setMockLocationStatus(executiveTaskExecution.getMockLocationStatus());
                                          invoiceDetails.setVehicleNumber(executiveTaskExecution.getVehicleNumber());
                                          invoiceDetails.setRemarks(executiveTaskExecution.getRemarks());
                                          invoiceDetails.setUserPid(executiveTaskExecution.getUser().getPid());
                                          invoiceDetails.setUserName(executiveTaskExecution.getUser().getFirstName());
                                          invoiceDetails.setUserId(executiveTaskExecution.getUser().getId());
                                          invoiceDetails.setCompanyPid(executiveTaskExecution.getCompany().getPid());
                                          invoiceDetails.setCompanyName(executiveTaskExecution.getCompany().getLegalName());
                                          invoiceDetails.setAccountTypeName(executiveTaskExecution.getAccountType().getName());
                                          invoiceDetails.setAccountTypePid(executiveTaskExecution.getAccountType().getPid());
                                          invoiceDetails.setDate(executiveTaskExecution.getDate());
                                          invoiceDetails.setCompanyId(executiveTaskExecution.getCompany().getId());


                                          //Inventory Data
                                          invoiceDetails.setInventoryPid(iVHeader.getPid());
                                          invoiceDetails.setDocumentNumberLocal(iVHeader.getDocumentNumberLocal());
                                          invoiceDetails.setDocumentNumberServer(iVHeader.getDocumentNumberServer());
                                          invoiceDetails.setEmployeeName(iVHeader.getEmployee().getName());
                                          invoiceDetails.setEmployeePid(iVHeader.getEmployee().getPid());
                                          invoiceDetails.setReceiverAccountName(iVHeader.getReceiverAccount().getName());
                                          invoiceDetails.setSupplierAccountName(iVHeader.getSupplierAccount().getName());
                                          invoiceDetails.setDocumentTotal(iVHeader.getDocumentTotal());
                                          invoiceDetails.setDocumentVolume(iVHeader.getDocumentVolume());
                                          invoiceDetails.setDocDiscountAmount(iVHeader.getDocDiscountAmount());
                                          invoiceDetails.setDocDiscountPercentage(iVHeader.getDocDiscountPercentage());
                                          invoiceDetails.setDocumentPid(iVHeader.getDocument().getPid());
                                          invoiceDetails.setDocumentName(iVHeader.getDocument().getName());
                                          invoiceDetails.setCreatedBy(iVHeader.getCreatedBy().getFirstName());
                                          invoiceDetails.setDocumentDate(iVHeader.getDocumentDate());
                                          invoiceDetails.setDocumentType(DocumentType.INVENTORY_VOUCHER);
                                          invoiceDetails.setSalesOrderTotalAmount(iVHeader.getDocumentTotal());

                                          // InventoryDetails
                                          invoiceDetails.setProductName(voucherDetail.getProduct().getName());
                                          invoiceDetails.setQuantity(voucherDetail.getQuantity());
                                          invoiceDetails.setFreeQuantity(voucherDetail.getFreeQuantity());
                                          invoiceDetails.setSellingRate(voucherDetail.getSellingRate());
                                          invoiceDetails.setTaxPercentage(voucherDetail.getTaxPercentage());
                                          invoiceDetails.setDiscountAmount(voucherDetail.getDiscountAmount());
                                          invoiceDetails.setDocDiscountPercentage(voucherDetail.getDiscountPercentage());
                                          invoiceDetailsDenormalizedList.add(invoiceDetails);
                                      }
                                  }
                              }

                          }
                      }
                      if (accountingVoucherHeaders.size() > 0) {
                          for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
                              if (accountingVoucherHeader.getExecutiveTaskExecution().getPid().equalsIgnoreCase(executiveTaskExecution.getPid())) {
                                  for (AccountingVoucherDetail voucherDetail : accountingVoucherDetails) {
                                      if (voucherDetail.getAccountingVoucherHeader().getPid().equals(accountingVoucherHeader.getPid())) {
                                          InvoiceDetailsDenormalized invoiceDetails = new InvoiceDetailsDenormalized();
                                          //Execution Data
                                          invoiceDetails.setPid(InvoiceDetailsDenormalizedService.PID_PREFIX + RandomUtil.generatePid());
                                          invoiceDetails.setExecutionPid(executiveTaskExecution.getPid());
                                          invoiceDetails.setAccountProfilePid(executiveTaskExecution.getAccountProfile().getPid());
                                          invoiceDetails.setAccountProfileName(executiveTaskExecution.getAccountProfile().getName());
                                          invoiceDetails.setActivityPid(executiveTaskExecution.getActivity().getPid());
                                          invoiceDetails.setActivityName(executiveTaskExecution.getActivity().getName());
                                          invoiceDetails.setPunchInDate(executiveTaskExecution.getPunchInDate());
                                          invoiceDetails.setSendDate(executiveTaskExecution.getSendDate());
                                          invoiceDetails.setCreatedDate(executiveTaskExecution.getCreatedDate());
                                          invoiceDetails.setLocation(executiveTaskExecution.getLocation());
                                          invoiceDetails.setTowerLocation(executiveTaskExecution.getTowerLocation());
                                          invoiceDetails.setWithCustomer(executiveTaskExecution.getWithCustomer());
                                          invoiceDetails.setLocationType(executiveTaskExecution.getLocationType());
                                          invoiceDetails.setMockLocationStatus(executiveTaskExecution.getMockLocationStatus());
                                          invoiceDetails.setVehicleNumber(executiveTaskExecution.getVehicleNumber());
                                          invoiceDetails.setRemarks(executiveTaskExecution.getRemarks());
                                          invoiceDetails.setUserPid(executiveTaskExecution.getUser().getPid());
                                          invoiceDetails.setUserName(executiveTaskExecution.getUser().getFirstName());
                                          invoiceDetails.setUserId(executiveTaskExecution.getUser().getId());
                                          invoiceDetails.setCompanyPid(executiveTaskExecution.getCompany().getPid());
                                          invoiceDetails.setCompanyName(executiveTaskExecution.getCompany().getLegalName());
                                          invoiceDetails.setAccountTypeName(executiveTaskExecution.getAccountType().getName());
                                          invoiceDetails.setAccountTypePid(executiveTaskExecution.getAccountType().getPid());
                                          invoiceDetails.setDate(executiveTaskExecution.getDate());
                                          invoiceDetails.setCompanyId(executiveTaskExecution.getCompany().getId());

                                          //Accounting Data
                                          invoiceDetails.setAccountingPid(accountingVoucherHeader.getPid());
                                          invoiceDetails.setDocumentNumberLocal(accountingVoucherHeader.getDocumentNumberLocal());
                                          invoiceDetails.setDocumentNumberServer(accountingVoucherHeader.getDocumentNumberServer());
                                          invoiceDetails.setDocumentName(accountingVoucherHeader.getDocument().getName());
                                          invoiceDetails.setDocumentPid(accountingVoucherHeader.getDocument().getPid());
                                          invoiceDetails.setDocumentDate(accountingVoucherHeader.getDocumentDate());
                                          invoiceDetails.setTotalAmount(accountingVoucherHeader.getTotalAmount());
                                          invoiceDetails.setOutstandingAmount(accountingVoucherHeader.getOutstandingAmount());
                                          invoiceDetails.setReceiptRemarks(accountingVoucherHeader.getRemarks());
                                          invoiceDetails.setCreatedBy(accountingVoucherHeader.getCreatedBy().getFirstName());
                                          invoiceDetails.setEmployeeName(accountingVoucherHeader.getEmployee().getName());
                                          invoiceDetails.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
                                          invoiceDetails.setReceiptAmount(accountingVoucherHeader.getTotalAmount());
                                          // Accounting voucher detail
                                          invoiceDetails.setAmount(voucherDetail.getAmount());
                                          invoiceDetails.setMode(voucherDetail.getMode());
                                          invoiceDetails.setInstrumentNumber(voucherDetail.getInstrumentNumber());
                                          invoiceDetails.setInstrumentDate(voucherDetail.getInstrumentDate());
                                          invoiceDetails.setBankName(voucherDetail.getBankName());
                                          invoiceDetails.setByAccountName(voucherDetail.getBy().getName());
                                          invoiceDetails.setByAccountPid(voucherDetail.getBy().getPid());
                                          invoiceDetails.setToAccountName(voucherDetail.getTo().getName());
                                          invoiceDetails.setToAccountPid(voucherDetail.getTo().getPid());
                                          invoiceDetails.setIncomeExpenseHead(voucherDetail.getIncomeExpenseHead());
                                          invoiceDetails.setVoucherNumber(voucherDetail.getVoucherNumber());
                                          invoiceDetails.setVoucherDate(voucherDetail.getVoucherDate());
                                          invoiceDetails.setReferenceNumber(voucherDetail.getReferenceNumber());
                                          invoiceDetails.setProvisionalReceiptNo(voucherDetail.getProvisionalReceiptNo());
                                          invoiceDetails.setRemarks(voucherDetail.getRemarks());


                                          invoiceDetailsDenormalizedList.add(invoiceDetails);

                                      }
                                  }
                              }
                          }
                      }
                      if(dynamicDocumentHeaders.size()>0)
                      {
                          for(DynamicDocumentHeader dynamicDocumentHeader:dynamicDocumentHeaders)
                          {
                              if(dynamicDocumentHeader.getExecutiveTaskExecution().getPid().equalsIgnoreCase(executiveTaskExecution.getPid()))
                              {
                                  for(FilledForm filledForm:filledForms)
                                  {
                                      if(filledForm.getDynamicDocumentHeader().getPid().equalsIgnoreCase(dynamicDocumentHeader.getPid()))
                                      {
                                          for(FilledFormDetail filledFormDetail:filledFormDetails)
                                          {
                                              if(filledFormDetail.getFilledForm().getPid().equalsIgnoreCase(filledForm.getPid()))
                                              {
                                                  InvoiceDetailsDenormalized invoiceDetails = new InvoiceDetailsDenormalized();
                                                                 //Execution Data
                                                  invoiceDetails.setPid(InvoiceDetailsDenormalizedService.PID_PREFIX + RandomUtil.generatePid());
                                                  invoiceDetails.setExecutionPid(executiveTaskExecution.getPid());
                                                  invoiceDetails.setAccountProfilePid(executiveTaskExecution.getAccountProfile().getPid());
                                                  invoiceDetails.setAccountProfileName(executiveTaskExecution.getAccountProfile().getName());
                                                  invoiceDetails.setActivityPid(executiveTaskExecution.getActivity().getPid());
                                                  invoiceDetails.setActivityName(executiveTaskExecution.getActivity().getName());
                                                  invoiceDetails.setPunchInDate(executiveTaskExecution.getPunchInDate());
                                                  invoiceDetails.setSendDate(executiveTaskExecution.getSendDate());
                                                  invoiceDetails.setCreatedDate(executiveTaskExecution.getCreatedDate());
                                                  invoiceDetails.setLocation(executiveTaskExecution.getLocation());
                                                  invoiceDetails.setTowerLocation(executiveTaskExecution.getTowerLocation());
                                                  invoiceDetails.setWithCustomer(executiveTaskExecution.getWithCustomer());
                                                  invoiceDetails.setLocationType(executiveTaskExecution.getLocationType());
                                                  invoiceDetails.setMockLocationStatus(executiveTaskExecution.getMockLocationStatus());
                                                  invoiceDetails.setVehicleNumber(executiveTaskExecution.getVehicleNumber());
                                                  invoiceDetails.setRemarks(executiveTaskExecution.getRemarks());
                                                  invoiceDetails.setUserPid(executiveTaskExecution.getUser().getPid());
                                                  invoiceDetails.setUserName(executiveTaskExecution.getUser().getFirstName());
                                                  invoiceDetails.setUserId(executiveTaskExecution.getUser().getId());
                                                  invoiceDetails.setCompanyPid(executiveTaskExecution.getCompany().getPid());
                                                  invoiceDetails.setCompanyName(executiveTaskExecution.getCompany().getLegalName());
                                                  invoiceDetails.setAccountTypeName(executiveTaskExecution.getAccountType().getName());
                                                  invoiceDetails.setAccountTypePid(executiveTaskExecution.getAccountType().getPid());
                                                  invoiceDetails.setDate(executiveTaskExecution.getDate());
                                                  invoiceDetails.setCompanyId(executiveTaskExecution.getCompany().getId());
                                                  invoiceDetails.setAccountPhNo(executiveTaskExecution.getAccountProfile().getPhone1());
                                                  //Dynamic data
                                                  invoiceDetails.setDynamicPid(dynamicDocumentHeader.getPid());
                                                  invoiceDetails.setDocumentNumberServer(dynamicDocumentHeader.getDocumentNumberServer());
                                                  invoiceDetails.setDocumentNumberLocal(dynamicDocumentHeader.getDocumentNumberLocal());
                                                  invoiceDetails.setDocumentType(DocumentType.DYNAMIC_DOCUMENT);
                                                  invoiceDetails.setEmployeePid(dynamicDocumentHeader.getEmployee().getPid());
                                                  invoiceDetails.setEmployeeName(dynamicDocumentHeader.getEmployee().getName());
                                                  invoiceDetails.setDocumentName(dynamicDocumentHeader.getDocument().getName());
                                                  invoiceDetails.setDocumentPid(dynamicDocumentHeader.getDocument().getPid());
                                                  invoiceDetails.setDocumentDate(dynamicDocumentHeader.getDocumentDate());
                                                  invoiceDetails.setCreatedBy(dynamicDocumentHeader.getCreatedBy().getFirstName());
                                                  invoiceDetails.setFormName(filledForm.getForm().getName());
                                                  invoiceDetails.setFormElementPid(filledFormDetail.getFormElement().getPid());
                                                  invoiceDetails.setFormElementName(filledFormDetail.getFormElement().getName());
                                                  invoiceDetails.setFormElementType(filledFormDetail.getFormElement().getFormElementType().getName());
                                                  invoiceDetails.setValue(filledFormDetail.getValue());
                                                  invoiceDetailsDenormalizedList.add(invoiceDetails);
                                                             }
                                                         }
                                                     }
                                                 }
                                             }
                                         }
                                     }
                      log.info(" Succesfully Saved to denormalized table");
                  }
                  else {
                      log.info("Already exist....");
                  }
                }
        invoiceDetailsDenormalizedRepository.save(invoiceDetailsDenormalizedList);

       for(ExecutiveTaskExecution execution :executiveTaskExecutions)
       {
           execution.setInvoiceStatus(true);
           ExecutiveTaskExecution executiondata = executiveTaskExecutionRepository.save(execution);

       }
               return ResponseEntity.ok(HttpStatus.OK);
           }
}
