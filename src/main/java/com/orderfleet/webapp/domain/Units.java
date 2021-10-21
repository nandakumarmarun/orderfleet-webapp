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

@Entity
@Table(name = "tbl_units")
public class Units implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GenericGenerator(name = "seq_units_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_units_id") })
	@GeneratedValue(generator = "seq_units_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_units_id')")
	private Long id;
	
	@NotNull
	@Column(name = "pid", unique = true, nullable = false, updatable = false)
	private String pid;
	
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;
	
	@Column(name = "shortName")
	private String shortName;
	
	@Size(max = 55)
	@Column(name = "alias", length = 55)
	private String alias;
	
	@Column(name = "unitId")
	private String unitId;
	
	@Column(name = "unitCode")
	private String unitCode;

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

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getUnitCode() {
		return unitCode;
	}

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	@Override
	public String toString() {
		return "Units [id=" + id + ", pid=" + pid + ", name=" + name + ", shortName=" + shortName + ", alias=" + alias
				+ ", unitId=" + unitId + ", unitCode=" + unitCode + "]";
	}

	
	
}
