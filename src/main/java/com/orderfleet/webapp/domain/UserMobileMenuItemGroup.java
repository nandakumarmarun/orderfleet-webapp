package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A UserMobileMenuItemGroup.
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
@Entity
@Table(name = "tbl_user_mobile_menu_item_group")
public class UserMobileMenuItemGroup implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_user_mobile_menu_item_group_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_user_mobile_menu_item_group_id") })
	@GeneratedValue(generator = "seq_user_mobile_menu_item_group_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_user_mobile_menu_item_group_id')")
	private Long id;

	@NotNull
	@ManyToOne
	private User user;

	@NotNull
	@ManyToOne
	private MobileMenuItemGroup mobileMenuItemGroup;

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

	public UserMobileMenuItemGroup() {
		super();
	}

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

	public MobileMenuItemGroup getMobileMenuItemGroup() {
		return mobileMenuItemGroup;
	}

	public void setMobileMenuItemGroup(MobileMenuItemGroup mobileMenuItemGroup) {
		this.mobileMenuItemGroup = mobileMenuItemGroup;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		UserMobileMenuItemGroup userMenuItem = (UserMobileMenuItemGroup) o;
		if (userMenuItem.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, userMenuItem.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
