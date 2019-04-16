package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "tbl_receivable_payable_column")
public class ReceivablePayableColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotNull
	@Size(min = 1, max = 150)
	@Id
	@Column(length = 150)
	private String name;

	public ReceivablePayableColumn() {
	}

	public ReceivablePayableColumn(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		ReceivablePayableColumn rpc = (ReceivablePayableColumn) o;
		if (name != null ? !name.equals(rpc.name) : rpc.name != null) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "ReceivablePayableColumn{" + "name='" + name + '\'' + "}";
	}
}
