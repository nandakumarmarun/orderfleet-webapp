package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_funnel")
public class Funnel implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_funnel_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_funnel_id") })
	@GeneratedValue(generator = "seq_funnel_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_funnel_id')")
	private Long id;

	private String name;

	private int sortOrder;
	
	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	private Company company;

	@OneToMany(mappedBy = "funnel", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<FunnelStage> funnelStages = new ArrayList<>();

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

	public int getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(int sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public List<FunnelStage> getFunnelStages() {
		return funnelStages;
	}

	public void setFunnelStages(List<FunnelStage> funnelStages) {
		this.funnelStages = funnelStages;
	}

	public void addStage(FunnelStage stage) {
		funnelStages.add(stage);
		stage.setFunnel(this);
	}

	public void removeStage(FunnelStage stage) {
		funnelStages.remove(stage);
		stage.setFunnel(null);
	}
}
