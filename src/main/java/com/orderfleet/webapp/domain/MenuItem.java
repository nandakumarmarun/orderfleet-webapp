package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A MenuItem
 * 
 * @author Shaheer
 * @since December 24, 2016
 */
@Entity
@Table(name = "tbl_menu_item")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class MenuItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_menu_item_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_menu_item_id") })
	@GeneratedValue(generator = "seq_menu_item_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_menu_item_id')")
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "label", length = 255, nullable = false)
	private String label;

	@Column(name = "link", length = 555)
	private String link;
	
	/** The parent menu, can be null if this is the root menu. */
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private MenuItem parent;
	
	@NotNull
    @Column(name = "activated", nullable = false)
    private Boolean activated = true;
	
	@NotNull
	@Column(name = "sort_order", nullable = false)
	private Integer sortOrder = 0;

	@Column(name = "description")
	private String description;
	
	@NotNull
	@Column(name = "icon_class", length = 255)
	private String iconClass = "entypo-users";

	public MenuItem() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public MenuItem getParent() {
		return parent;
	}

	public void setParent(MenuItem parent) {
		this.parent = parent;
	}

	public Boolean getActivated() {
		return activated;
	}

	public void setActivated(Boolean activated) {
		this.activated = activated;
	}

	public Integer getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(Integer sortOrder) {
		this.sortOrder = sortOrder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getIconClass() {
		return iconClass;
	}

	public void setIconClass(String iconClass) {
		this.iconClass = iconClass;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		MenuItem menuItem = (MenuItem) o;
		if (menuItem.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, menuItem.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

	@Override
	public String toString() {
		return "MenuItem [id=" + id + ", label=" + label + ", link=" + link + ", parent=" + parent + ", activated="
				+ activated + ", sortOrder=" + sortOrder + ", description=" + description + ", iconClass=" + iconClass
				+ "]";
	}

}
