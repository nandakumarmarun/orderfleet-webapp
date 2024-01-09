package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.enums.PaymentMode;

import java.time.LocalDateTime;

public class AccountingData {

    private PaymentMode mode;
    private double amount;
    private String instrumentNumber;
    private LocalDateTime instrumentDate;
    private String bankPid;
    private String bankName;
    private String byAccountPid;
    private String byAccountName;
    private String toAccountPid;
    private String toAccountName;
    private String voucherNumber;
    private LocalDateTime voucherDate;
    private String referenceNumber;
    private String incomeExpenseHeadPid;
    private String incomeExpenseHeadName;
    private String provisionalReceiptNo;

    private String remarks;

    public AccountingData(PaymentMode mode, double amount, String instrumentNumber, LocalDateTime instrumentDate, String bankPid, String bankName, String byAccountPid, String byAccountName, String toAccountPid, String toAccountName, String voucherNumber, LocalDateTime voucherDate, String referenceNumber, String incomeExpenseHeadPid, String incomeExpenseHeadName, String provisionalReceiptNo, String remarks) {
        this.mode = mode;
        this.amount = amount;
        this.instrumentNumber = instrumentNumber;
        this.instrumentDate = instrumentDate;
        this.bankPid = bankPid;
        this.bankName = bankName;
        this.byAccountPid = byAccountPid;
        this.byAccountName = byAccountName;
        this.toAccountPid = toAccountPid;
        this.toAccountName = toAccountName;
        this.voucherNumber = voucherNumber;
        this.voucherDate = voucherDate;
        this.referenceNumber = referenceNumber;
        this.incomeExpenseHeadPid = incomeExpenseHeadPid;
        this.incomeExpenseHeadName = incomeExpenseHeadName;
        this.provisionalReceiptNo = provisionalReceiptNo;
        this.remarks = remarks;
    }

    public AccountingData() {

    }

    public PaymentMode getMode() {
        return mode;
    }

    public void setMode(PaymentMode mode) {
        this.mode = mode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getInstrumentNumber() {
        return instrumentNumber;
    }

    public void setInstrumentNumber(String instrumentNumber) {
        this.instrumentNumber = instrumentNumber;
    }

    public LocalDateTime getInstrumentDate() {
        return instrumentDate;
    }

    public void setInstrumentDate(LocalDateTime instrumentDate) {
        this.instrumentDate = instrumentDate;
    }

    public String getBankPid() {
        return bankPid;
    }

    public void setBankPid(String bankPid) {
        this.bankPid = bankPid;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getByAccountPid() {
        return byAccountPid;
    }

    public void setByAccountPid(String byAccountPid) {
        this.byAccountPid = byAccountPid;
    }

    public String getByAccountName() {
        return byAccountName;
    }

    public void setByAccountName(String byAccountName) {
        this.byAccountName = byAccountName;
    }

    public String getToAccountPid() {
        return toAccountPid;
    }

    public void setToAccountPid(String toAccountPid) {
        this.toAccountPid = toAccountPid;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public void setToAccountName(String toAccountName) {
        this.toAccountName = toAccountName;
    }

    public String getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(String voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public LocalDateTime getVoucherDate() {
        return voucherDate;
    }

    public void setVoucherDate(LocalDateTime voucherDate) {
        this.voucherDate = voucherDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getIncomeExpenseHeadPid() {
        return incomeExpenseHeadPid;
    }

    public void setIncomeExpenseHeadPid(String incomeExpenseHeadPid) {
        this.incomeExpenseHeadPid = incomeExpenseHeadPid;
    }

    public String getIncomeExpenseHeadName() {
        return incomeExpenseHeadName;
    }

    public void setIncomeExpenseHeadName(String incomeExpenseHeadName) {
        this.incomeExpenseHeadName = incomeExpenseHeadName;
    }

    public String getProvisionalReceiptNo() {
        return provisionalReceiptNo;
    }

    public void setProvisionalReceiptNo(String provisionalReceiptNo) {
        this.provisionalReceiptNo = provisionalReceiptNo;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
