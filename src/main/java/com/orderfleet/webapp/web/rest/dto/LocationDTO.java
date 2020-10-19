package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Location;

/**
 * A DTO for the Location entity.
 * 
 * @author Shaheer
 * @since May 26, 2016
 */
public class LocationDTO {

	private String id;

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private BigDecimal latitude;

	private BigDecimal longitude;

	@NotNull
	private boolean activated;

	private LocalDateTime lastModifiedDate;

	public LocationDTO() {
		super();

	}

	public LocationDTO(Location location) {
		super();
		this.id = String.valueOf(location.getId());
		this.pid = location.getPid();
		this.name = location.getName();
		this.alias = location.getAlias();
		this.description = location.getDescription();
		this.latitude = location.getLatitude();
		this.longitude = location.getLongitude();
		this.activated = location.getActivated();
		this.lastModifiedDate = location.getLastModifiedDate();
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
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

	public BigDecimal getLatitude() {
		return latitude;
	}

	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}

	public BigDecimal getLongitude() {
		return longitude;
	}

	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		LocationDTO locationDTO = (LocationDTO) o;

		if (!Objects.equals(pid, locationDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

//	@Override
//	public String toString() {
//		return "LocationDTO [id=" + id + ", pid=" + pid + ", name=" + name + ", alias=" + alias + ", description="
//				+ description + ", latitude=" + latitude + ", longitude=" + longitude + "]";
//	}

}
