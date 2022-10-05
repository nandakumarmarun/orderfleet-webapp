package com.orderfleet.webapp.domain;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_accounting_voucher_update_history")
public class AccountingVoucherUpdateHistory {
	@ManyToOne
	@JoinColumn(name = "accounting_voucher_header_id")
	private AccountingVoucherHeader accountingVoucherHeader;

	@Column(name = "amount", columnDefinition = "double precision DEFAULT '0'")
	private double amount;

	@Column(name = "new_amount", columnDefinition = "double precision DEFAULT '0'")
	private double newAmount;

	@ManyToOne
	@NotNull
	private Company company;

	@Id
	@GenericGenerator(name = "seq_accounting_voucher_update_history_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_accounting_voucher_update_history_id") })
	@GeneratedValue(generator = "seq_accounting_voucher_update_history_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_accounting_voucher_update_history_id')")
	private Long id;

	@Column(name = "Updated", columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean isUpdated;

	private String pid;

	@NotNull
	@ManyToOne
	private User updateBy;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime updatedDate;

	public AccountingVoucherHeader getAccountingVoucherHeader() {
		return accountingVoucherHeader;
	}

	public double getAmount() {
		return amount;
	}

	public Company getCompany() {
		return company;
	}

	public Long getId() {
		return id;
	}

	public String getPid() {
		return pid;
	}

	public User getUpdateBy() {
		return updateBy;
	}

	public LocalDateTime getUpdatedDate() {
		return updatedDate;
	}

	public boolean isUpdated() {
		return isUpdated;
	}

	public void setAccountingVoucherHeader(AccountingVoucherHeader accountingVoucherHeader) {
		this.accountingVoucherHeader = accountingVoucherHeader;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setUpdateBy(User updateBy) {
		this.updateBy = updateBy;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	public void setUpdatedDate(LocalDateTime updatedDate) {
		this.updatedDate = updatedDate;
	}

	public double getNewAmount() {
		return newAmount;
	}

	public void setNewAmount(double newAmount) {
		this.newAmount = newAmount;
	}

	@Override
	public String toString() {
		return "AccountingVoucherUpdateHistory [accountingVoucherHeader=" + accountingVoucherHeader + ", amount="
				+ amount + ", newAmount=" + newAmount + ", company=" + company + ", id=" + id + ", isUpdated="
				+ isUpdated + ", pid=" + pid + ", updateBy=" + updateBy + ", updatedDate=" + updatedDate + "]";
	}

}
