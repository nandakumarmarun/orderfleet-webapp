package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A LocationHierarchy.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
@Entity
@Table(name = "tbl_location_hierarchy")
public class LocationHierarchy implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_locationhierarchy_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_locationhierarchy_id") })
	@GeneratedValue(generator = "seq_locationhierarchy_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_locationhierarchy_id')")
	private Long id;

	@ManyToOne
	@NotNull
	private Company company;

	@ManyToOne
	@NotNull
	@JoinColumn(name = "location_id")
	private Location location;

	/** The parent location, can be null if this is the root location. */
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private Location parent;

	@NotNull
	@Column(name = "activated", nullable = false)
	private Boolean activated = true;

	@NotNull
	@Column(name = "activated_date", nullable = false)
	private ZonedDateTime activatedDate = ZonedDateTime.now();

	@Column(name = "inactivated_date", nullable = true)
	private ZonedDateTime inactivatedDate = null;

	@NotNull
	@Column(name = "version", nullable = false)
	private Long version;

	public LocationHierarchy() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean isActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public ZonedDateTime getActivatedDate() {
		return activatedDate;
	}

	public void setActivatedDate(ZonedDateTime activatedDate) {
		this.activatedDate = activatedDate;
	}

	public ZonedDateTime getInactivatedDate() {
		return inactivatedDate;
	}

	public void setInactivatedDate(ZonedDateTime inactivatedDate) {
		this.inactivatedDate = inactivatedDate;
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

	public Location getParent() {
		return parent;
	}

	public void setParent(Location location) {
		this.parent = location;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		LocationHierarchy locationHierarchy = (LocationHierarchy) o;
		if (locationHierarchy.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, locationHierarchy.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

//	@Override
//	public String toString() {
//		return "LocationHierarchy{" + "id=" + id + ", location='" + location + "'" + ", parent='" + parent + "'"
//				+ ", activated='" + activated + "'" + ", activatedDate='" + activatedDate + "'" + ", inactivatedDate='"
//				+ inactivatedDate + "'" + ", version='" + version + "'" + '}';
//	}
}
