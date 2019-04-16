package com.orderfleet.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_stage_header_rca")
public class StageHeaderRca {

	@Id
	@GenericGenerator(name = "seq_stage_header_rca_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_stage_header_rca_id") })
	@GeneratedValue(generator = "seq_stage_header_rca_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_stage_header_rca_id')")
	private Long id;
	
	@ManyToOne
	private StageHeader stageHeader;
	
	@ManyToOne
	private RootCauseAnalysisReason rootCauseAnalysisReason;
	
	@ManyToOne
	private Company company;
	
	@Column(name = "reason", length = 500)
	private String reason;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StageHeader getStageHeader() {
		return stageHeader;
	}

	public void setStageHeader(StageHeader stageHeader) {
		this.stageHeader = stageHeader;
	}

	public RootCauseAnalysisReason getRootCauseAnalysisReason() {
		return rootCauseAnalysisReason;
	}

	public void setRootCauseAnalysisReason(RootCauseAnalysisReason rootCauseAnalysisReason) {
		this.rootCauseAnalysisReason = rootCauseAnalysisReason;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}
	
	
	
}
