package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.DebitCredit;

/**
 * A LedgerReportTP.
 *
 * @author Sarath
 * @since Nov 1, 2016
 */
@Entity
@Table(name = "tbl_ledger_report_tp")
public class LedgerReportTP implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_ledger_report_tp_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_ledger_report_tp_id") })
	@GeneratedValue(generator = "seq_ledger_report_tp_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_ledger_report_tp_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Division division;

	@Column(name = "voucher_no")
	private String voucheNo;

	@Column(name = "voucher_date")
	private LocalDate voucherDate;

	@Column(name = "narration")
	private String narration;

	@ManyToOne
	@NotNull
	private AccountProfile accountProfile;

	@Column(name = "amount")
	private Double amount;

	@Column(name = "type")
	private String type;

	@Column(name = "dr_cr")
	private DebitCredit debitCredit;

	@ManyToOne
	@NotNull
	private Company company;

	public LedgerReportTP() {
		super();
	}

	public LedgerReportTP(Long id, Division division, String voucheNo, LocalDate voucherDate, String narration,
			AccountProfile accountProfile, Double amount, String type, DebitCredit debitCredit) {
		super();
		this.id = id;
		this.division = division;
		this.voucheNo = voucheNo;
		this.voucherDate = voucherDate;
		this.narration = narration;
		this.accountProfile = accountProfile;
		this.amount = amount;
		this.type = type;
		this.debitCredit = debitCredit;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Division getDivision() {
		return division;
	}

	public void setDivision(Division division) {
		this.division = division;
	}

	public String getVoucheNo() {
		return voucheNo;
	}

	public void setVoucheNo(String voucheNo) {
		this.voucheNo = voucheNo;
	}

	public LocalDate getVoucherDate() {
		return voucherDate;
	}

	public void setVoucherDate(LocalDate voucherDate) {
		this.voucherDate = voucherDate;
	}

	public String getNarration() {
		return narration;
	}

	public void setNarration(String narration) {
		this.narration = narration;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public DebitCredit getDebitCredit() {
		return debitCredit;
	}

	public void setDebitCredit(DebitCredit debitCredit) {
		this.debitCredit = debitCredit;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LedgerReportTP ledgerReportTP = (LedgerReportTP) o;
		if (ledgerReportTP.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, ledgerReportTP.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "LedgerReportTP [id=" + id + ", division=" + division + ", voucheNo=" + voucheNo + ", voucherDate="
				+ voucherDate + ", narration=" + narration + ", accountProfile=" + accountProfile + ", amount=" + amount
				+ ", type=" + type + ", debitCredit=" + debitCredit + "]";
	}

}
