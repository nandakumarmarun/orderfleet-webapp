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

/**
 * A OverduePeriod.
 * 
 * @author Muhammed Riyas T
 * @since Mar 07, 2017
 */
@Entity
@Table(name = "tbl_overdue_period")
public class OverduePeriod implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_overdue_period_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_overdue_period_id") })
	@GeneratedValue(generator = "seq_overdue_period_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_overdue_period_id')")
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Column(name = "overdue_from", nullable = false)
	private int overdueFrom;

	@Column(name = "overdue_to", nullable = false)
	private int overdueTo;

	@ManyToOne
	private Company company;

	public OverduePeriod() {
		super();

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getOverdueFrom() {
		return overdueFrom;
	}

	public void setOverdueFrom(int overdueFrom) {
		this.overdueFrom = overdueFrom;
	}

	public int getOverdueTo() {
		return overdueTo;
	}

	public void setOverdueTo(int overdueTo) {
		this.overdueTo = overdueTo;
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
		OverduePeriod overduePeriod = (OverduePeriod) o;
		if (overduePeriod.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, overduePeriod.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
