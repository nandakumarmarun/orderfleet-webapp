package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.domain.enums.StageType;

@Entity
@Table(name = "tbl_stage")
public class Stage implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_stage_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_stage_id") })
	@GeneratedValue(generator = "seq_stage_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_stage_id')")
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

	@NotNull
	@Column(name = "activated", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean activated;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "stage_type", nullable = false)
	private StageType stageType;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "stage_name_type", nullable = false, columnDefinition = "character varying DEFAULT 'GENERAL'")
	private StageNameType stageNameType;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private Company company;

	@Column(name = "sort_order", nullable = false, columnDefinition = "int DEFAULT 0")
	private int sortOrder;

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

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
	}

	public StageType getStageType() {
		return stageType;
	}

	public void setStageType(StageType stageType) {
		this.stageType = stageType;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public StageNameType getStageNameType() {
		return stageNameType;
	}

	public void setStageNameType(StageNameType stageNameType) {
		this.stageNameType = stageNameType;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Stage accountType = (Stage) o;
		if (accountType.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, accountType.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}

}
