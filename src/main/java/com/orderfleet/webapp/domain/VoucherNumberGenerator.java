package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_voucher_number_generator")
public class VoucherNumberGenerator implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_voucher_number_generator_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_voucher_number_generator_id") })
	@GeneratedValue(generator = "seq_voucher_number_generator_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_voucher_number_generator_id')")
	private Long id;
	
	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private Document document;

	@NotNull
	@ManyToOne
	private Company company;
	
	@Size(max = 55)
	@Column(name = "prefix", length = 55)
	private String prefix;
	
	@Column(name = "start_with")
	private Long startwith;

	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public Long getStartwith() {
		return startwith;
	}

	public void setStartwith(Long startwith) {
		this.startwith = startwith;
	}

	@Override
	public String toString() {
		return "VoucherNumberGenerator [id=" + id + ", user=" + user + ", document=" + document + ", company=" + company
				+ ", prefix=" + prefix + ", startwith=" + startwith + "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VoucherNumberGenerator voucherNumberGenerator = (VoucherNumberGenerator) o;
		if (voucherNumberGenerator.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, voucherNumberGenerator.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
