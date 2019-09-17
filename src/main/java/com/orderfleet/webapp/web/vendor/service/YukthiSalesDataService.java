package com.orderfleet.webapp.web.vendor.service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountingVoucherAllocation;
import com.orderfleet.webapp.domain.AccountingVoucherDetail;
import com.orderfleet.webapp.domain.AccountingVoucherHeader;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.InventoryVoucherHeader;
import com.orderfleet.webapp.domain.enums.PaymentMode;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.snr.yukti.model.sales.CustomerPayment;
import com.snr.yukti.model.sales.PaymentLine;
import com.snr.yukti.model.sales.SalesRequest;
import com.snr.yukti.service.YuktiSalesService;

@Service
public class YukthiSalesDataService {
	
	@Inject
	private YuktiSalesService yuktiSalesService;
	
	@Async
	@Transactional
	public void saveTransactions(ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper, Long companyId) {
		if (tsTransactionWrapper != null) {
			if (tsTransactionWrapper.getInventoryVouchers() != null
					&& !tsTransactionWrapper.getInventoryVouchers().isEmpty()) {
				yuktiSalesService.saveSalesOrder(createSalesRequest(tsTransactionWrapper.getInventoryVouchers()), companyId);
			}
			if (tsTransactionWrapper.getAccountingVouchers() != null
					&& !tsTransactionWrapper.getAccountingVouchers().isEmpty()) {
				yuktiSalesService.saveReceipts(createReceiptRequest(tsTransactionWrapper.getAccountingVouchers()), companyId);
			}
		}
	}

	private List<SalesRequest> createSalesRequest(List<InventoryVoucherHeader> inventoryVouchers) {
		List<SalesRequest> newSalesList = new ArrayList<>();
		ExecutiveTaskExecution execution = inventoryVouchers.get(0).getExecutiveTaskExecution();
		EmployeeProfile employeeProfile = inventoryVouchers.get(0).getEmployee();
		if(employeeProfile == null) {
			throw new IllegalArgumentException("No employee found for user : " + execution.getUser().getLogin());
		}
		AccountProfile accProfile = execution.getAccountProfile();
		LocalDate reqDate = execution.getCreatedDate().toLocalDate();
		DecimalFormat mFormat = new DecimalFormat("00");
		String requestDate = mFormat.format(Double.valueOf(reqDate.getDayOfMonth())) + "-"
				+ mFormat.format(Double.valueOf(reqDate.getMonthValue())) + "-"
				+ mFormat.format(Double.valueOf(reqDate.getYear()));
		for (InventoryVoucherHeader ivHeader : inventoryVouchers) {
			for (InventoryVoucherDetail ivDetail : ivHeader.getInventoryVoucherDetails()) {
				SalesRequest sales = new SalesRequest();
				sales.setDebtorNo(accProfile.getAlias());
				sales.setBranchName(accProfile.getName());
				sales.setBranchCode(accProfile.getAlias());
				sales.setRequestDate(requestDate);
				sales.setRequiredDeliveryDate(requestDate);
				sales.setUnitPrice(ivDetail.getSellingRate() + "");
				sales.setStkCode(ivDetail.getProduct().getAlias());
				sales.setStkDescription(ivDetail.getProduct().getName());
				sales.setQuantity(ivDetail.getQuantity() + "");
				sales.setSalesmanCode(employeeProfile.getReferenceId());
				sales.setSalesmanName(employeeProfile.getName());
				sales.setForeignRequestId(ivHeader.getPid());

				newSalesList.add(sales);
			}
		}
		return newSalesList;
	}
	
	private List<CustomerPayment> createReceiptRequest(List<AccountingVoucherHeader> accountingVouchers) {
		List<CustomerPayment> customerPayments = new ArrayList<>();
		for (AccountingVoucherHeader accountingVoucherHeader : accountingVouchers) {
			for (AccountingVoucherDetail avDetail : accountingVoucherHeader.getAccountingVoucherDetails()) {
				CustomerPayment customerPayment = new CustomerPayment();
				customerPayment.setDebtorNo(accountingVoucherHeader.getAccountProfile().getAlias());
				LocalDate pDate = avDetail.getVoucherDate().toLocalDate();
				DecimalFormat mFormat = new DecimalFormat("00");
				String paymentDate = mFormat.format(Double.valueOf(pDate.getDayOfMonth())) + "-"
						+ mFormat.format(Double.valueOf(pDate.getMonthValue())) + "-"
						+ mFormat.format(Double.valueOf(pDate.getYear()));
				customerPayment.setPaymentDate(paymentDate);
				customerPayment.setDueDate(paymentDate);
				if (avDetail.getMode() == PaymentMode.Cheque) {
					customerPayment.setPaymentType("1");
				} else if (avDetail.getMode() == PaymentMode.Cash) {
					customerPayment.setPaymentType("3");
				}
				customerPayment.setChequeNo(avDetail.getInstrumentNumber());
				customerPayment.setNotes(avDetail.getRemarks());
				customerPayment.setTotal(avDetail.getAmount());
				// set bank
				if (avDetail.getBank() != null) {
					customerPayment.setBank(avDetail.getBank().getName());
				} else {
					customerPayment.setBank(avDetail.getBankName());
				}
				customerPayment.setPaymentLines(createPaymentLines(avDetail));
				customerPayments.add(customerPayment);
			}
		}
		return customerPayments;
	}
	
	private List<PaymentLine> createPaymentLines(AccountingVoucherDetail avDetail) {
		List<PaymentLine> paymentLines = new ArrayList<>();
		if (avDetail.getAccountingVoucherAllocations() != null
				&& !avDetail.getAccountingVoucherAllocations().isEmpty()) {
			for (AccountingVoucherAllocation avAllocation : avDetail.getAccountingVoucherAllocations()) {
				PaymentLine paymentLine = new PaymentLine();
				paymentLine.setTransNo(avAllocation.getId() + "");
				paymentLine.setTransAmount(avAllocation.getAmount());
				paymentLine.setAllocAmount(avAllocation.getAmount());
				paymentLines.add(paymentLine);
			}
		}
		return paymentLines;
	}

}
