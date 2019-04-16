package com.orderfleet.webapp.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "tbl_stage_header")
public class StageHeader implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GenericGenerator(name = "seq_stage_header_id_GEN", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
			@Parameter(name = "sequence_name", value = "seq_stage_header_id") })
	@GeneratedValue(generator = "seq_stage_header_id_GEN")
	@Column(name = "id", insertable = false, updatable = false, columnDefinition = "bigint DEFAULT nextval('seq_stage_header_id')")
	private Long id;

	@ManyToOne
	private AccountProfile accountProfile;

	@ManyToOne
	private Stage stage;

	@ManyToOne
	private EmployeeProfile employeeProfile;

	@Column(name = "remarks", length = 500)
	private String remarks;
	
	//can store amount/price/quote/volume 
    @Column(name = "value")
    private BigDecimal value;
    
  //can store amount/price/quote/volume no 
    @Column(name = "quotation_no")
    private String quotationNo;

	@Column(name = "created_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	@JsonIgnore
	private LocalDateTime createdDate = LocalDateTime.now();

	@ManyToOne
	private User createdBy;

	@ManyToOne
	private Company company;

	@OneToOne(mappedBy = "stageHeader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private StageDetail stageDetails;
	
	@OneToMany(mappedBy = "stageHeader", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StageHeaderRca> stageHeaderRca;
	

	@OneToMany(mappedBy = "stageHeader", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<StageHeaderFile> stageHeaderFiles = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public AccountProfile getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountProfile accountProfile) {
		this.accountProfile = accountProfile;
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public EmployeeProfile getEmployeeProfile() {
		return employeeProfile;
	}

	public void setEmployeeProfile(EmployeeProfile employeeProfile) {
		this.employeeProfile = employeeProfile;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public String getQuotationNo() {
		return quotationNo;
	}

	public void setQuotationNo(String quotationNo) {
		this.quotationNo = quotationNo;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public StageDetail getStageDetails() {
		return stageDetails;
	}

	public void setStageDetails(StageDetail stageDetails) {
		if (stageDetails == null) {
			if (this.stageDetails != null) {
				this.stageDetails.setStageHeader(null);
			}
		} else {
			stageDetails.setStageHeader(this);
		}
		this.stageDetails = stageDetails;
	}

	public List<StageHeaderFile> getStageHeaderFiles() {
		return stageHeaderFiles;
	}

	public void setStageHeaderFiles(List<StageHeaderFile> stageHeaderFiles) {
		this.stageHeaderFiles = stageHeaderFiles;
	}

	public void addStageFiles(StageHeaderFile stageFile) {
		this.stageHeaderFiles.add(stageFile);
		stageFile.setStageHeader(this);
	}

	public void removeStageFiles(StageHeaderFile stageFile) {
		this.stageHeaderFiles.remove(stageFile);
		stageFile.setStageHeader(null);
	}

	public List<StageHeaderRca> getStageHeaderRca() {
		return stageHeaderRca;
	}

	public void setStageHeaderRca(List<StageHeaderRca> stageHeaderRca) {
		this.stageHeaderRca = stageHeaderRca;
	}
	
	
}
