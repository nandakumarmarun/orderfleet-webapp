package com.orderfleet.webapp.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "tbl_stage_detail")
public class StageDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
    @MapsId
	private StageHeader stageHeader;

	@NotNull
	@ManyToOne
	private ExecutiveTaskExecution executiveTaskExecution;
	
	@NotNull
	private String dynamicDocumentHeaderPid;

	@ManyToOne
	@NotNull
	private Activity activity;

	@ManyToOne
	@NotNull
	private Company company;
	
	@NotNull
	@ManyToOne
	private Document document;

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

	public ExecutiveTaskExecution getExecutiveTaskExecution() {
		return executiveTaskExecution;
	}

	public void setExecutiveTaskExecution(ExecutiveTaskExecution executiveTaskExecution) {
		this.executiveTaskExecution = executiveTaskExecution;
	}

	public String getDynamicDocumentHeaderPid() {
		return dynamicDocumentHeaderPid;
	}

	public void setDynamicDocumentHeaderPid(String dynamicDocumentHeaderPid) {
		this.dynamicDocumentHeaderPid = dynamicDocumentHeaderPid;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof StageDetail))
			return false;
		return id != null && id.equals(((StageDetail) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

}
