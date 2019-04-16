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
@Table(name = "tbl_receivable_payable_column_config")
public class ReceivablePayableColumnConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_receivable_payable_column_config_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_receivable_payable_column_config_id") })
	@GeneratedValue(generator = "seq_receivable_payable_column_config_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_receivable_payable_column_config_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private ReceivablePayableColumn receivablePayableColumn;

	@NotNull
	@Column(name = "enabled", nullable = false)
	private boolean enabled = true;

	@NotNull
	@ManyToOne
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReceivablePayableColumn getReceivablePayableColumn() {
		return receivablePayableColumn;
	}

	public void setReceivablePayableColumn(ReceivablePayableColumn receivablePayableColumn) {
		this.receivablePayableColumn = receivablePayableColumn;
	}

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
