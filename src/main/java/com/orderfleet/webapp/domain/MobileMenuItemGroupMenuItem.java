package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * A MobileMenuItemGroup
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
@Entity
@Table(name = "tbl_mobile_menu_item_group_menu_item")
public class MobileMenuItemGroupMenuItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_mobile_menu_item_group_menu_item_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_mobile_menu_item_group_menu_item_id") })
	@GeneratedValue(generator = "seq_mobile_menu_item_group_menu_item_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_mobile_menu_item_group_menu_item_id')")
	private Long id;

	@ManyToOne
	private MobileMenuItemGroup mobileMenuItemGroup;

	@ManyToOne
	private MobileMenuItem mobileMenuItem;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "label", length = 255, nullable = false)
	private String label;

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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public MobileMenuItemGroup getMobileMenuItemGroup() {
		return mobileMenuItemGroup;
	}

	public void setMobileMenuItemGroup(MobileMenuItemGroup mobileMenuItemGroup) {
		this.mobileMenuItemGroup = mobileMenuItemGroup;
	}

	public MobileMenuItem getMobileMenuItem() {
		return mobileMenuItem;
	}

	public void setMobileMenuItem(MobileMenuItem mobileMenuItem) {
		this.mobileMenuItem = mobileMenuItem;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

}
