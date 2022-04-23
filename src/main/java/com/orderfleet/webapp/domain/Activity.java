package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.orderfleet.webapp.domain.enums.ContactManagement;

/**
 * A Activity.
 * 
 * @author Muhammed Riyas T
 * @since May 19, 2016
 */
@Entity
@Table(name = "tbl_activity")
public class Activity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_activity_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_activity_id") })
	@GeneratedValue(generator = "seq_activity_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_activity_id')")
	private Long id;

	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;

	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;

	@Column(name = "description")
	private String description;

	@Column(name = "has_default_account", nullable = false)
	private boolean hasDefaultAccount;

	@Column(name = "complete_plans", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean completePlans;

	@NotNull
	@ManyToOne
	private Company company;

	@JsonIgnore
	@ManyToMany
	@JoinTable(name = "tbl_activity_account_type", joinColumns = @JoinColumn(name = "activity_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "account_type_id", referencedColumnName = "id"))
	private Set<AccountType> activityAccountTypes = new HashSet<>();

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated;

	@NotNull
	@Column(name = "has_secondary_sales", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean hasSecondarySales;
	
	@NotNull
	@Column(name = "geo_fencing", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean geoFencing;
	
	@NotNull
	@Column(name = "has_telephonic_order", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean hasTelephonicOrder;
	
	@NotNull
	@Column(name = "email_to_complaint", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean emailTocomplaint;


	@NotNull
	@Column(name = "target_display_on_day_plan", nullable = false, columnDefinition = "boolean DEFAULT 'FALSE'")
	private boolean targetDisplayOnDayplan;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@Column(name = "last_modified_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime lastModifiedDate = LocalDateTime.now();

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "contact_management", nullable = false, columnDefinition = "varchar(20) DEFAULT 'ENABLED'")
	private ContactManagement contactManagement;

	@PreUpdate
	public void preUpdate() {
		this.lastModifiedDate = LocalDateTime.now();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public boolean getHasDefaultAccount() {
		return hasDefaultAccount;
	}

	public void setHasDefaultAccount(boolean hasDefaultAccount) {
		this.hasDefaultAccount = hasDefaultAccount;
	}

	public Set<AccountType> getActivityAccountTypes() {
		return activityAccountTypes;
	}

	public void setActivityAccountTypes(Set<AccountType> activityAccountTypes) {
		this.activityAccountTypes = activityAccountTypes;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public boolean getTargetDisplayOnDayplan() {
		return targetDisplayOnDayplan;
	}

	public void setTargetDisplayOnDayplan(boolean targetDisplayOnDayplan) {
		this.targetDisplayOnDayplan = targetDisplayOnDayplan;
	}

	public boolean getCompletePlans() {
		return completePlans;
	}

	public void setCompletePlans(boolean completePlans) {
		this.completePlans = completePlans;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public ContactManagement getContactManagement() {
		return contactManagement;
	}

	public void setContactManagement(ContactManagement contactManagement) {
		this.contactManagement = contactManagement;
	}

	public boolean getHasSecondarySales() {
		return hasSecondarySales;
	}

	public void setHasSecondarySales(boolean hasSecondarySales) {
		this.hasSecondarySales = hasSecondarySales;
	}
	

	public boolean getGeoFencing() {
		return geoFencing;
	}

	public void setGeoFencing(boolean geoFencing) {
		this.geoFencing = geoFencing;
	}

	public boolean getHasTelephonicOrder() {
		return hasTelephonicOrder;
	}

	public void setHasTelephonicOrder(boolean hasTelephonicOrder) {
		this.hasTelephonicOrder = hasTelephonicOrder;
	}
	
	

	public boolean getEmailTocomplaint() {
		return emailTocomplaint;
	}

	public void setEmailTocomplaint(boolean emailTocomplaint) {
		this.emailTocomplaint = emailTocomplaint;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Activity activity = (Activity) o;
		if (activity.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, activity.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "Activity [id=" + id + ", pid=" + pid + ", name=" + name + ", alias=" + alias + ", description="
				+ description + ", hasDefaultAccount=" + hasDefaultAccount + ", completePlans=" + completePlans
				+ ", company=" + company + ", activityAccountTypes=" + activityAccountTypes + ", activated=" + activated
				+ ", hasSecondarySales=" + hasSecondarySales + ", geoFencing=" + geoFencing + ", hasTelephonicOrder="
				+ hasTelephonicOrder + ", targetDisplayOnDayplan=" + targetDisplayOnDayplan + ", createdDate="
				+ createdDate + ", lastModifiedDate=" + lastModifiedDate + ", contactManagement=" + contactManagement
				+ "]";
	}

	

}