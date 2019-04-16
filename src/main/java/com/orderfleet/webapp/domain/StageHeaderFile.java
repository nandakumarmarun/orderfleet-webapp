package com.orderfleet.webapp.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

@Entity
@Table(name = "tbl_stage_header_file")
public class StageHeaderFile {

	@Id
	@GenericGenerator(name = "seq_stage_header_file_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_stage_header_file_id") })
	@GeneratedValue(generator = "seq_stage_header_file_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_stage_header_file_id')")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stage_header_id")
	private StageHeader stageHeader;

	@ManyToOne
	@NotNull
	private File file;

	private Long companyId;
	
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

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof StageHeaderFile))
			return false;
		return id != null && id.equals(((StageHeaderFile) o).id);
	}

	@Override
	public int hashCode() {
		return 31;
	}

}
