package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.ProcessFlowStatus;
import com.orderfleet.webapp.domain.enums.SalesManagementStatus;
import com.orderfleet.webapp.domain.enums.SendSalesOrderEmailStatus;
import com.orderfleet.webapp.domain.enums.SourceModule;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;

/**
 * A InventoryVoucherHeader.
 * 
 * @author Muhammed Riyas T
 * @since July 18, 2016
 */
@Entity
@Table(name = "tbl_location_hierarchy_account")
public class LocationHierarchyAccount implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_location_hierarchy_account_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_location_hierarchy_account_id") })
	@GeneratedValue(generator = "seq_location_hierarchy_account_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_location_hierarchy_account_id')")
	private Long id;

	private String locations;

	@NotNull
	@Column(name = "created_date", nullable = false)
	private LocalDateTime createdDate = LocalDateTime.now();

	@NotNull
	@ManyToOne
	private AccountProfile accountProfile;

	@ManyToOne
	@NotNull
	private Company company;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLocations() {
		return locations;
	}

	public void setLocations(String locations) {
		this.locations = locations;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

}
