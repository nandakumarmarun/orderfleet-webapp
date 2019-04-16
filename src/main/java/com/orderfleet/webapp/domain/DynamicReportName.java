package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_dynamic_report_name")
public class DynamicReportName implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_dynamic_report_name_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_dynamic_report_name_id") })
	@GeneratedValue(generator = "seq_dynamic_report_name_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_dynamic_report_name_id')")
	private Long id;
	
	@NotNull
	@Size(min = 1, max = 255)
	@Column(name = "name", length = 255, nullable = false)
	private String name;
	
	@NotNull
	@Column(name = "overwrite", nullable = false, columnDefinition = "boolean DEFAULT 'TRUE'")
	private boolean overwrite;
	
	@ManyToOne
	@NotNull
	private Company company;
	
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

	public boolean isOverwrite() {
		return overwrite;
	}

	public void setOverwrite(boolean overwrite) {
		this.overwrite = overwrite;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@Override
	public String toString() {
		return "DynamicReportUpload [id=" + id + ", name=" + name + ", overwrite=" + overwrite + ", company=" + company
				+ "]";
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		DynamicReportName dynamicReportUpload = (DynamicReportName) o;
		if (dynamicReportUpload.id == null || id == null) {
			return false;
		}
		return Objects.equals(id, dynamicReportUpload.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
}
