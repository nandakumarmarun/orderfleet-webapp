package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.TargetFrequency;

/**
 * A SalesTargetGroupUserTarget.
 */
@Entity
@Table(name = "tbl_sales_product_group_user_target")
public class SalesProductGroupUserTarget implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_sales_product_group_user_target_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_sales_product_group_user_target_id") })
	@GeneratedValue(generator = "seq_sales_product_group_user_target_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_sales_product_group_user_target_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Column(name = "from_date", nullable = false)
	private LocalDate fromDate;

	@NotNull
	@Column(name = "to_date", nullable = false)
	private LocalDate toDate;

	@Column(name = "volume")
	private double volume;

	@Column(name = "amount")
	private double amount;

	@ManyToOne
	@NotNull
	private ProductGroup productGroup;

	@ManyToOne
	private User user;

	@ManyToOne
	@NotNull
	private Company company;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "target_frequency", nullable = false)
	private TargetFrequency targetFrequency = TargetFrequency.MONTH;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
	}

	public SalesProductGroupUserTarget() {
		super();
	}

	public SalesProductGroupUserTarget(Long id, String pid, LocalDate fromDate, LocalDate toDate, double volume,
			double amount, ProductGroup productGroup, User user, Company company) {
		super();
		this.id = id;
		this.pid = pid;
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.volume = volume;
		this.amount = amount;
		this.productGroup = productGroup;
		this.user = user;
		this.company = company;
	}

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

	public LocalDate getFromDate() {
		return fromDate;
	}

	public void setFromDate(LocalDate fromDate) {
		this.fromDate = fromDate;
	}

	public LocalDate getToDate() {
		return toDate;
	}

	public void setToDate(LocalDate toDate) {
		this.toDate = toDate;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(ProductGroup productGroup) {
		this.productGroup = productGroup;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public TargetFrequency getTargetFrequency() {
		return targetFrequency;
	}

	public void setTargetFrequency(TargetFrequency targetFrequency) {
		this.targetFrequency = targetFrequency;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SalesProductGroupUserTarget salesTargetGroupUserTarget = (SalesProductGroupUserTarget) o;
		if (salesTargetGroupUserTarget.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, salesTargetGroupUserTarget.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "SalesTargetGroupUserTarget [id=" + id + ", pid=" + pid + ", fromDate=" + fromDate + ", toDate=" + toDate
				+ ", volume=" + volume + ", amount=" + amount + ", productGroup=" + productGroup + "]";
	}

}
