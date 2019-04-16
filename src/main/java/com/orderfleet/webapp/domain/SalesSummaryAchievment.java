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

/**
 * A SalesSummaryAchievment.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
@Entity
@Table(name = "tbl_sales_summary_achievment")
public class SalesSummaryAchievment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sales_summary_achievment_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sales_summary_achievment_id") })
	@GeneratedValue(generator = "seq_sales_summary_achievment_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sales_summary_achievment_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@ManyToOne
	private SalesTargetGroup salesTargetGroup;

	@NotNull
	@ManyToOne
	private User user;

	@Column(name = "amount", nullable = false)
	private double amount;

	@Column(name = "achieved_date", nullable = false)
	private LocalDate achievedDate;

	@NotNull
	@ManyToOne
	private Company company;
	
	@ManyToOne
	private Location location;

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

	public SalesTargetGroup getSalesTargetGroup() {
		return salesTargetGroup;
	}

	public void setSalesTargetGroup(SalesTargetGroup salesTargetGroup) {
		this.salesTargetGroup = salesTargetGroup;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public LocalDate getAchievedDate() {
		return achievedDate;
	}

	public void setAchievedDate(LocalDate achievedDate) {
		this.achievedDate = achievedDate;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}
	
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SalesSummaryAchievment salesSummaryAchievment = (SalesSummaryAchievment) o;
		if (salesSummaryAchievment.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, salesSummaryAchievment.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
