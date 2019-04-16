package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

/**
 * A MobileMenuItem
 * 
 * @author Muhammed Riyas T
 * @since Feb 01, 2017
 */
@Entity
@Table(name = "tbl_mobile_menu_item")
public class MobileMenuItem implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_mobile_menu_item_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_mobile_menu_item_id") })
	@GeneratedValue(generator = "seq_mobile_menu_item_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_mobile_menu_item_id')")
	private Long id;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", unique = true, length = 255, nullable = false)
	private String name;

	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "label", length = 255, nullable = false)
	private String label;

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

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
