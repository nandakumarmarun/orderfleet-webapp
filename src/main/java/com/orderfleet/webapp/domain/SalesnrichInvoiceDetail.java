package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_salesnrich_invoice_detail")
public class SalesnrichInvoiceDetail implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_salesnrich_invoice_detail_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_salesnrich_invoice_detail_id") })
	@GeneratedValue(generator = "seq_salesnrich_invoice_detail_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_salesnrich_invoice_detail_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;

	@Column(name = "particulars", updatable = false)
	private String particulars;

	@Column(name = "quantity")
	private double quantity;

	@Column(name = "price")
	private double price;

	@Column(name = "total")
	private double total;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "salesnrich_invoice_header_id")
	private SalesnrichInvoiceHeader salesnrichInvoiceHeader;

	public SalesnrichInvoiceDetail() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public double getQuantity() {
		return quantity;
	}

	public void setQuantity(double quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public SalesnrichInvoiceHeader getSalesnrichInvoiceHeader() {
		return salesnrichInvoiceHeader;
	}

	public void setSalesnrichInvoiceHeader(SalesnrichInvoiceHeader salesnrichInvoiceHeader) {
		this.salesnrichInvoiceHeader = salesnrichInvoiceHeader;
	}

}
