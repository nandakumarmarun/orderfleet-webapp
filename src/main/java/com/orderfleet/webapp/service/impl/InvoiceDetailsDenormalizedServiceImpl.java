package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.service.InvoiceDetailsDenormalizedService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvoiceDetailsDenormalizedServiceImpl implements InvoiceDetailsDenormalizedService {
    private final Logger log = LoggerFactory.getLogger(InvoiceDetailsDenormalizedServiceImpl.class);
    @Inject
    private UserRepository userRepository;
    @Inject
    private EmployeeProfileRepository employeeProfileRepository;
    @Inject
    private  UserVehicleAssociationRepository userVehicleAssociationRepository;
    @Inject
    private AccountProfileRepository accountProfileRepository;
    @Inject
    private AccountTypeRepository accountTypeRepository;

    @Inject
    private ActivityRepository activityRepository;
    @Inject
    private ExecutiveTaskPlanRepository executiveTaskPlanRepository;
    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;

    @Inject
    private InventoryVoucherHeaderRepository inventoryVoucherHeaderRepository;

    @Inject
    private OrderStatusRepository orderStatusRepository;

    @Inject
    private SalesLedgerRepository salesLedgerRepository;

    @Inject
    private DocumentRepository documentRepository;
    @Inject
    private PriceLevelRepository priceLevelRepository;
    
    @Inject
    private StockLocationRepository stockLocationRepository;

    @Inject
    private InventoryVoucherDetailRepository inventoryVoucherDetailRepository;

    @Inject
    private ProductProfileRepository productProfileRepository;
    @Inject
    private InvoiceDetailsDenormalizedRepository invoiceInventoryDetailsRepository;

    @Inject
    private IncomeExpenseHeadRepository incomeExpenseHeadRepository;
    @Inject
    private BankRepository bankRepository;

    @Inject
    private FormRepository formRepository;
    @Inject
    private FormElementRepository formElementRepository;
    @Inject
    private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;
    @Override
    @Async
    public void SaveExecutivetaskExecutionWithInventory(ExecutiveTaskSubmissionTransactionWrapper executiveTaskSubmissionDTO, User user) {
       log.info("Enter here to save invoice Details................:"+user.getFirstName() +" "+user.getCompany().getLegalName());
        Company company = user.getCompany();
        EmployeeProfile employeeProfile = employeeProfileRepository.findEmployeeProfileByUser(user);
        List<InventoryVoucherHeader> inventoryVoucherDTOs = executiveTaskSubmissionDTO.getInventoryVouchers();
        List<DynamicDocumentHeader> dynamicDocumentHeaders = executiveTaskSubmissionDTO.getDynamicDocuments();
        List<AccountingVoucherHeader> accountingVoucherHeaders = executiveTaskSubmissionDTO.getAccountingVouchers();
        ExecutiveTaskExecution executionDTO = executiveTaskSubmissionDTO.getExecutiveTaskExecution();

        AccountProfile accountProfile = accountProfileRepository.findOneByPid(executionDTO.getAccountProfile().getPid()).get();
        AccountType accountType = null;
        if (executionDTO.getAccountType().getPid() != null) {
             accountType = accountTypeRepository.findOneByPid(executionDTO.getAccountType().getPid()).get();
             executionDTO.setAccountType(accountType);
        } else {
            executionDTO.setAccountType(accountProfile.getAccountType());

        }
        Activity activity = activityRepository.findOneByPid(executionDTO.getActivity().getPid()).get();

        List<Object[]> documentWiseCount = new ArrayList<Object[]>();
        documentWiseCount = inventoryVoucherHeaderRepository
                .findCountOfInventoryVoucherHeader(company.getId());


        List<InvoiceDetailsDenormalized> inventoryDetailsList = new ArrayList<>();


        if(inventoryVoucherDTOs != null) {
            log.info("Request to Save Inventory Details :"+company.getLegalName());
            for (InventoryVoucherHeader inventoryVoucher : inventoryVoucherDTOs) {
                for (InventoryVoucherDetail voucherDetail : inventoryVoucher.getInventoryVoucherDetails()) {
                    InvoiceDetailsDenormalized inventoryDetails = new InvoiceDetailsDenormalized();
                    inventoryDetails.setExecutionPid(executionDTO.getPid());
                    inventoryDetails.setEmployeeName(employeeProfile.getName());
                    inventoryDetails.setEmployeePid(employeeProfile.getPid());
                    inventoryDetails.setPid(InvoiceDetailsDenormalizedService.PID_PREFIX + RandomUtil.generatePid());
                    inventoryDetails.setAccountProfileName(accountProfile.getName());
                    inventoryDetails.setAccountProfilePid(accountProfile.getPid());
                    inventoryDetails.setAccountTypeName(executionDTO.getAccountType().getName());
                    inventoryDetails.setAccountTypePid(executionDTO.getAccountType().getPid());
                    inventoryDetails.setActivityName(activity.getName());
                    inventoryDetails.setActivityPid(activity.getPid());
                    inventoryDetails.setLocation(executionDTO.getLocation());
                    inventoryDetails.setTowerLocation(executionDTO.getTowerLocation());
                    inventoryDetails.setLocationType(executionDTO.getLocationType());
                    inventoryDetails.setMockLocationStatus(executionDTO.getMockLocationStatus());
                    inventoryDetails.setWithCustomer(executionDTO.getWithCustomer());
                    inventoryDetails.setPunchInDate(executionDTO.getPunchInDate());
                    inventoryDetails.setDate(executionDTO.getDate());
                    inventoryDetails.setSendDate(executionDTO.getSendDate());
                    inventoryDetails.setUserName(user.getFirstName());
                    inventoryDetails.setCompanyName(company.getLegalName());
                    inventoryDetails.setRemarks(executionDTO.getRemarks());
                    inventoryDetails.setUserPid(user.getPid());
                    inventoryDetails.setUserId(user.getId());
                    inventoryDetails.setCompanyPid(company.getPid());
                    inventoryDetails.setCompanyId(company.getId());
                    // InventoryVoucher Header
                    inventoryDetails.setCreatedBy(user.getFirstName());
                    inventoryDetails.setInventoryPid(inventoryVoucher.getPid());
                    Document document = documentRepository.findOneByPid(inventoryVoucher.getDocument().getPid()).get();
                    inventoryDetails.setDocumentName(document.getName());
                    inventoryDetails.setDocumentPid(document.getPid());
                    inventoryDetails.setDocumentDate(inventoryVoucher.getDocumentDate());
                    inventoryDetails.setDocumentType(DocumentType.INVENTORY_VOUCHER);
                    inventoryDetails.setDocumentNumberLocal(inventoryVoucher.getDocumentNumberLocal());
                    inventoryDetails.setDocumentNumberServer(inventoryVoucher.getDocumentNumberLocal());
                    inventoryDetails.setDocumentTotal(inventoryVoucher.getDocumentTotal());
                    inventoryDetails.setSalesOrderTotalAmount(inventoryVoucher.getDocumentTotal());
                    inventoryDetails.setDocumentVolume(inventoryVoucher.getDocumentVolume());
                    inventoryDetails.setDocDiscountAmount(inventoryVoucher.getDocDiscountAmount());
                    inventoryDetails.setDocDiscountPercentage(inventoryVoucher.getDocDiscountPercentage());
                    inventoryDetails.setImageRefNo(inventoryVoucher.getImageRefNo());

                    if (inventoryVoucher.getEmployee() != null) {
                        EmployeeProfile employeeProfile1 = employeeProfileRepository.findEmployeeProfileByPid(inventoryVoucher.getEmployee().getPid());
                        inventoryDetails.setEmployeeName(employeeProfile1.getName());

                    } else {
                        inventoryDetails.setEmployeeName(employeeProfile.getName());

                    }
                    AccountProfile accountProfile1 = accountProfileRepository.findOneByPid(inventoryVoucher.getReceiverAccount().getPid()).get();
                    inventoryDetails.setReceiverAccountName(accountProfile1.getName());
                    AccountProfile accountProfile2 = accountProfileRepository.findOneByPid(inventoryVoucher.getSupplierAccount().getPid()).get();
                    if (inventoryVoucher.getSupplierAccount().getPid() != null)
                        inventoryDetails.setSupplierAccountName(accountProfile2.getName());

                    Optional<ProductProfile> opProductProfile = productProfileRepository
                            .findOneByPid(voucherDetail.getProduct().getPid());
                    if (!opProductProfile.isPresent()) {
                        throw new IllegalArgumentException(
                                voucherDetail.getProduct().getName() + "Product Not found");
                    }
                    inventoryDetails.setQuantity(voucherDetail.getQuantity());
                    inventoryDetails.setFreeQuantity(voucherDetail.getFreeQuantity());
                    inventoryDetails.setProductPid(opProductProfile.get().getPid());
                    inventoryDetails.setProductName(opProductProfile.get().getName());
                    inventoryDetails.setProductRemarks(opProductProfile.get().getRemarks());
                    inventoryDetails.setSellingRate(voucherDetail.getSellingRate());
                    inventoryDetails.setTaxPercentage(voucherDetail.getTaxPercentage());
                    inventoryDetails.setDiscountPercentage(voucherDetail.getDiscountPercentage());
                    inventoryDetails.setDiscountAmount(voucherDetail.getDiscountAmount());
                    inventoryDetails.setRowTotal(voucherDetail.getRowTotal());
                    inventoryDetails.setLengthType(voucherDetail.getLengthType());
                    inventoryDetails.setLengthInInch(voucherDetail.getLengthInInch());
                    inventoryDetails.setLengthInMeter(voucherDetail.getLengthInMeter());
                    inventoryDetails.setLengthInFeet(voucherDetail.getLengthInFeet());

                    inventoryDetailsList.add(inventoryDetails);
                }
            }
            log.info("Enter the Inventory Details To Denormalized Table");
        }
        if(accountingVoucherHeaders != null) {
            log.info("Request to save Accounting Details :"+company.getLegalName());
            for (AccountingVoucherHeader accountingVoucherHeader : accountingVoucherHeaders) {
                for (AccountingVoucherDetail voucherDetail : accountingVoucherHeader.getAccountingVoucherDetails()) {
                    InvoiceDetailsDenormalized inventoryDetails = new InvoiceDetailsDenormalized();
                    inventoryDetails.setExecutionPid(executionDTO.getPid());
                    inventoryDetails.setEmployeeName(employeeProfile.getName());
                    inventoryDetails.setEmployeePid(employeeProfile.getPid());
                    inventoryDetails.setPid(InvoiceDetailsDenormalizedService.PID_PREFIX + RandomUtil.generatePid());
                    inventoryDetails.setAccountProfileName(accountProfile.getName());
                    inventoryDetails.setAccountProfilePid(accountProfile.getPid());
                    inventoryDetails.setAccountTypeName(executionDTO.getAccountType().getName());
                    inventoryDetails.setAccountTypePid(executionDTO.getAccountType().getPid());
                    inventoryDetails.setActivityName(activity.getName());
                    inventoryDetails.setActivityPid(activity.getPid());
                    inventoryDetails.setLocation(executionDTO.getLocation());
                    inventoryDetails.setTowerLocation(executionDTO.getTowerLocation());
                    inventoryDetails.setLocationType(executionDTO.getLocationType());
                    inventoryDetails.setMockLocationStatus(executionDTO.getMockLocationStatus());
                    inventoryDetails.setWithCustomer(executionDTO.getWithCustomer());
                    inventoryDetails.setPunchInDate(executionDTO.getPunchInDate());
                    inventoryDetails.setDate(executionDTO.getDate());
                    inventoryDetails.setSendDate(executionDTO.getSendDate());
                    inventoryDetails.setUserName(user.getFirstName());
                    inventoryDetails.setUserId(user.getId());
                    inventoryDetails.setCompanyName(company.getLegalName());
                    inventoryDetails.setRemarks(executionDTO.getRemarks());
                    inventoryDetails.setUserPid(user.getPid());
                    inventoryDetails.setCompanyPid(company.getPid());
                    inventoryDetails.setCompanyId(company.getId());

                    // InventoryVoucher Header
                    inventoryDetails.setCreatedBy(user.getFirstName());
                    inventoryDetails.setAccountingPid(accountingVoucherHeader.getPid());
                    Document document = documentRepository.findOneByPid(accountingVoucherHeader.getDocument().getPid()).get();
                    inventoryDetails.setDocumentName(document.getName());
                    inventoryDetails.setDocumentPid(document.getPid());
                    inventoryDetails.setDocumentNumberLocal(accountingVoucherHeader.getDocumentNumberLocal());
                    inventoryDetails.setDocumentNumberServer(accountingVoucherHeader.getDocumentNumberLocal());
                    inventoryDetails.setDocumentType(DocumentType.ACCOUNTING_VOUCHER);
                    inventoryDetails.setDocumentDate(accountingVoucherHeader.getDocumentDate());
                    inventoryDetails.setOutstandingAmount(accountingVoucherHeader.getOutstandingAmount());
                    inventoryDetails.setTotalAmount(accountingVoucherHeader.getTotalAmount());
                    inventoryDetails.setReceiptAmount(accountingVoucherHeader.getTotalAmount());
                    inventoryDetails.setAmount(voucherDetail.getAmount());
                    AccountProfile byAccount = accountProfileRepository.findOneByPid(voucherDetail.getBy().getPid()).get();
                    AccountProfile toAccount = accountProfileRepository.findOneByPid(voucherDetail.getTo().getPid()).get();
                    inventoryDetails.setByAccountName(byAccount.getName());
                    inventoryDetails.setByAccountPid(byAccount.getPid());
                    inventoryDetails.setToAccountPid(toAccount.getPid());
                    inventoryDetails.setToAccountName(toAccount.getName());
                    inventoryDetails.setInstrumentDate(voucherDetail.getInstrumentDate());
                    inventoryDetails.setInstrumentNumber(voucherDetail.getInstrumentNumber());
                    inventoryDetails.setMode(voucherDetail.getMode());
                    inventoryDetails.setReferenceNumber(voucherDetail.getReferenceNumber());
                    inventoryDetails.setVoucherDate(voucherDetail.getVoucherDate());
                    inventoryDetails.setVoucherNumber(voucherDetail.getVoucherNumber());
                    inventoryDetails.setProvisionalReceiptNo(voucherDetail.getProvisionalReceiptNo());
                    if (voucherDetail.getBank() != null) {
                        Bank bank = bankRepository.findOneByPid(voucherDetail.getBank().getPid()).get();
                        inventoryDetails.setBankPid(bank.getPid());
                    } else {
                        inventoryDetails.setBankName(voucherDetail.getBankName());
                    }
                    inventoryDetails.setReceiptRemarks(voucherDetail.getRemarks());
                    // set Income Expense
                    if (voucherDetail.getIncomeExpenseHead() != null) {
                        voucherDetail.setIncomeExpenseHead(incomeExpenseHeadRepository
                                .findOneByPid(voucherDetail.getIncomeExpenseHead().getPid()).get());
                    }
                    inventoryDetails.setProvisionalReceiptNo(voucherDetail.getProvisionalReceiptNo());
                    if (voucherDetail.getAccountingVoucherAllocations() != null) {
                        for (AccountingVoucherAllocation voucherAllocation : voucherDetail.getAccountingVoucherAllocations()) {
                            inventoryDetails.setReferenceNumber(voucherAllocation.getReferenceNumber());
                            inventoryDetails.setVoucherNumber(voucherAllocation.getVoucherNumber());

                        }
                    }
                    inventoryDetailsList.add(inventoryDetails);
                }
            }
            log.info("Enter the accounting Details to Denormalized table");
        }
        if(dynamicDocumentHeaders != null) {
            log.info("Request to save Dynamic Details :"+company.getLegalName());
            for (DynamicDocumentHeader docHeader : dynamicDocumentHeaders) {
                for (FilledForm filledForm : docHeader.getFilledForms()) {
                    for (FilledFormDetail formDetail : filledForm.getFilledFormDetails()) {

                            InvoiceDetailsDenormalized inventoryDetails = new InvoiceDetailsDenormalized();
                            inventoryDetails.setExecutionPid(executionDTO.getPid());
                            inventoryDetails.setEmployeeName(employeeProfile.getName());
                            inventoryDetails.setEmployeePid(employeeProfile.getPid());
                            inventoryDetails.setPid(InvoiceDetailsDenormalizedService.PID_PREFIX + RandomUtil.generatePid());
                            inventoryDetails.setAccountProfileName(accountProfile.getName());
                            inventoryDetails.setAccountProfilePid(accountProfile.getPid());
                            inventoryDetails.setAccountTypeName(executionDTO.getAccountType().getName());
                            inventoryDetails.setAccountTypePid(executionDTO.getAccountType().getPid());
                            inventoryDetails.setActivityName(activity.getName());
                            inventoryDetails.setActivityPid(activity.getPid());
                            inventoryDetails.setLocation(executionDTO.getLocation());
                            inventoryDetails.setTowerLocation(executionDTO.getTowerLocation());
                            inventoryDetails.setLocationType(executionDTO.getLocationType());
                            inventoryDetails.setMockLocationStatus(executionDTO.getMockLocationStatus());
                            inventoryDetails.setWithCustomer(executionDTO.getWithCustomer());
                            inventoryDetails.setUserPid(user.getPid());
                            inventoryDetails.setUserId(user.getId());
                            inventoryDetails.setCompanyPid(company.getPid());
                            inventoryDetails.setPunchInDate(executionDTO.getPunchInDate());
                            inventoryDetails.setDate(executionDTO.getDate());
                            inventoryDetails.setSendDate(executionDTO.getSendDate());
                            inventoryDetails.setUserName(user.getFirstName());
                            inventoryDetails.setCompanyName(company.getLegalName());
                            inventoryDetails.setRemarks(executionDTO.getRemarks());
                            inventoryDetails.setCompanyId(company.getId());

                            inventoryDetails.setCreatedBy(user.getFirstName());
                            inventoryDetails.setDynamicPid(docHeader.getPid());
                            inventoryDetails.setDocumentNumberLocal(docHeader.getDocumentNumberLocal());
                            inventoryDetails.setDocumentNumberServer(docHeader.getDocumentNumberLocal());
                            Document document = documentRepository.findOneByPid(docHeader.getDocument().getPid()).get();
                            inventoryDetails.setDocumentName(document.getName());
                            inventoryDetails.setDocumentType(DocumentType.DYNAMIC_DOCUMENT);
                            inventoryDetails.setDocumentPid(document.getPid());
                            inventoryDetails.setAccountPhNo(accountProfile.getPhone1());
                            inventoryDetails.setDocumentDate(docHeader.getDocumentDate());
                            if (docHeader.getEmployee() != null) {
                                inventoryDetails.setEmployeeName(employeeProfile.getName());
                            }
                            inventoryDetails.setFilledFormPid(filledForm.getPid());
                            inventoryDetails.setImageRefNo(filledForm.getImageRefNo());
                            inventoryDetails.setFilledFormId(filledForm.getId());
                            Form form = formRepository.findOneByPid(filledForm.getForm().getPid()).get();
                            inventoryDetails.setFormName(form.getName());
                            FormElement formElement = formElementRepository
                                    .findOneByPid(formDetail.getFormElement().getPid()).get();
                            inventoryDetails.setFormElementName(formElement.getName());
                            inventoryDetails.setFormElementPid(formElement.getPid());
                            inventoryDetails.setFormElementType(formElement.getFormElementType().getName());
                            inventoryDetails.setValue(formDetail.getValue());
                            if (formDetail.getValue() == null || formDetail.getValue().isEmpty()) {
                                inventoryDetails.setValue(formElement.getDefaultValue());
                            }
                            inventoryDetailsList.add(inventoryDetails);
                        }
                    }
                }
            log.info("Enter Dynamic Details to the Denormalized Table");
            }

        invoiceInventoryDetailsRepository.save(inventoryDetailsList);
        log.info("Successfully saved order details.........................:"+executionDTO.getPid());
        Optional<ExecutiveTaskExecution> executionData = executiveTaskExecutionRepository.findOneByPid(executionDTO.getPid());
            executionData.get().setInvoiceStatus(true);
        executiveTaskExecutionRepository.save(executionData.get());
        log.info("Save The status to execution table :"+ executionData.get().isInvoiceStatus());
    }
}
