package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_tally_configuration")
public class TallyConfiguration  implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_tally_configuration_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
	@Parameter(name = "sequence_name", value = "seq_tally_configuration_id") })
	@GeneratedValue(generator = "seq_tally_configuration_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_tally_configuration_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@NotNull
	@ManyToOne
	private Company company;
	
	@Column(name = "tally_company_name")
	private String tallyCompanyName;
	
	@Column(name = "dynamic_date")
	private String dynamicDate;
	
	@Column(name = "order_number_with_employee",columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean orderNumberWithEmployee = false;
	
	@Column(name = "actual_bill_status",columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean actualBillStatus = false;
	
	@Column(name = "gst_names")
	private String gstNames;
	
	@Column(name = "static_godown_names")
	private String staticGodownNames;
	
	@Column(name = "roundoff_ledger_name")
	private String roundOffLedgerName;
	
	@Column(name = "sales_ledger_name")
	private String salesLedgerName;
	
	@Column(name = "tally_product_key")
	private String tallyProductKey;
	
	@Column(name = "receipt_voucher_type")
	private String receiptVoucherType;
	
	@Column(name = "bank_receipt_type")
	private String bankReceiptType;
	
	@Column(name = "bank_name")
	private String bankName; //bank receipt voucher type bank name
	
	@Column(name = "cash_receipt_type")
	private String cashReceiptType;
	
	@Column(name = "transaction_type")
	private String transactionType;//for bank receipt
	
	@Column(name = "item_remarks_enabled",columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean itemRemarksEnabled;
	
	@Column(name = "pdc_voucher_type")
	private String pdcVoucherType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getTallyCompanyName() {
		return tallyCompanyName;
	}

	public void setTallyCompanyName(String tallyCompanyName) {
		this.tallyCompanyName = tallyCompanyName;
	}

	public String getDynamicDate() {
		return dynamicDate;
	}

	public void setDynamicDate(String dynamicDate) {
		this.dynamicDate = dynamicDate;
	}

	public boolean getOrderNumberWithEmployee() {
		return orderNumberWithEmployee;
	}

	public void setOrderNumberWithEmployee(boolean orderNumberWithEmployee) {
		this.orderNumberWithEmployee = orderNumberWithEmployee;
	}

	public boolean getActualBillStatus() {
		return actualBillStatus;
	}

	public void setActualBillStatus(boolean actualBillStatus) {
		this.actualBillStatus = actualBillStatus;
	}

	public String getGstNames() {
		return gstNames;
	}

	public void setGstNames(String gstNames) {
		this.gstNames = gstNames;
	}

	public String getStaticGodownNames() {
		return staticGodownNames;
	}

	public void setStaticGodownNames(String staticGodownNames) {
		this.staticGodownNames = staticGodownNames;
	}


	public String getRoundOffLedgerName() {
		return roundOffLedgerName;
	}

	public void setRoundOffLedgerName(String roundOffLedgerName) {
		this.roundOffLedgerName = roundOffLedgerName;
	}

	public String getSalesLedgerName() {
		return salesLedgerName;
	}

	public void setSalesLedgerName(String salesLedgerName) {
		this.salesLedgerName = salesLedgerName;
	}

	public String getTallyProductKey() {
		return tallyProductKey;
	}

	public void setTallyProductKey(String tallyProductKey) {
		this.tallyProductKey = tallyProductKey;
	}

	public String getReceiptVoucherType() {
		return receiptVoucherType;
	}

	public void setReceiptVoucherType(String receiptVoucherType) {
		this.receiptVoucherType = receiptVoucherType;
	}

	public String getBankReceiptType() {
		return bankReceiptType;
	}

	public void setBankReceiptType(String bankReceiptType) {
		this.bankReceiptType = bankReceiptType;
	}
	
	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getCashReceiptType() {
		return cashReceiptType;
	}

	public void setCashReceiptType(String cashReceiptType) {
		this.cashReceiptType = cashReceiptType;
	}

	public String getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}

	public boolean getItemRemarksEnabled() {
		return itemRemarksEnabled;
	}

	public void setItemRemarksEnabled(boolean itemRemarksEnabled) {
		this.itemRemarksEnabled = itemRemarksEnabled;
	}

	public String getPdcVoucherType() {
		return pdcVoucherType;
	}

	public void setPdcVoucherType(String pdcVoucherType) {
		this.pdcVoucherType = pdcVoucherType;
	}
	
	
}
