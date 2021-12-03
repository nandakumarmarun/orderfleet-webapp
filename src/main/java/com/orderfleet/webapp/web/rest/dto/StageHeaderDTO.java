
package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.StageDetail;
import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.domain.StageHeaderFile;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.OtherVoucherTransactionResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StageHeaderDTO {

	private final Logger log = LoggerFactory.getLogger( StageHeaderDTO.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private Long id;

	private String accountProfilePid;

	private String accountProfileName;

	private String stagePid;

	private String stageName;

	private String employeeProfilePid;

	private String employeeProfileName;

	private String remarks;
	
	private BigDecimal value;
	
	private String quotationNo;

	private LocalDateTime createdDate;

	private String createdByPid;

	private String createdByName;
	
	private Long stageDetailId;
	
	private String dynamicDocumentHeaderPid;
	
	private String activityName;
	
	Map<String, String> files = new LinkedHashMap<>();
	
	private List<String> rcas;
	
	private String[] rcaPids;
	
	List<MultipartFile> multipartFiles = new ArrayList<>();

	public StageHeaderDTO() {
	}

	public StageHeaderDTO(StageHeader stageHeader) {
		super();
		this.id = stageHeader.getId();
		this.accountProfilePid = stageHeader.getAccountProfile().getPid();
		this.accountProfileName = stageHeader.getAccountProfile().getName();
		this.stagePid = stageHeader.getStage().getPid();
		this.stageName = stageHeader.getStage().getName();
		this.employeeProfilePid = stageHeader.getEmployeeProfile().getPid();
		this.employeeProfileName = stageHeader.getEmployeeProfile().getName();
		this.remarks = stageHeader.getRemarks();
		this.value = stageHeader.getValue();
		this.quotationNo = stageHeader.getQuotationNo();
		this.createdDate = stageHeader.getCreatedDate();
		this.createdByPid = stageHeader.getCreatedBy().getPid();
		this.createdByName = stageHeader.getCreatedBy().getFirstName();
		//dynamic document files
		if(stageHeader.getStageDetails() != null) {
			this.stageDetailId = stageHeader.getStageDetails().getId();
			this.dynamicDocumentHeaderPid = stageHeader.getStageDetails().getDynamicDocumentHeaderPid();
		}
		//stage header files
		if(!stageHeader.getStageHeaderFiles().isEmpty()) {
			for (StageHeaderFile stageHeaderFile : stageHeader.getStageHeaderFiles()) {
				File file = stageHeaderFile.getFile();
				if(file != null) {
					files.put(file.getPid(), file.getFileName());
				}
			}
		}
		rcas = stageHeader.getStageHeaderRca().stream().map(s -> s.getReason()).collect(Collectors.toList());
		
	}
	
	public StageHeaderDTO(StageHeader stageHeader, FilledFormRepository filledFormRepository) {
		super();
		this.id = stageHeader.getId();
		this.accountProfilePid = stageHeader.getAccountProfile().getPid();
		this.accountProfileName = stageHeader.getAccountProfile().getName();
		this.stagePid = stageHeader.getStage().getPid();
		this.stageName = stageHeader.getStage().getName();
		this.employeeProfilePid = stageHeader.getEmployeeProfile().getPid();
		this.employeeProfileName = stageHeader.getEmployeeProfile().getName();
		this.remarks = stageHeader.getRemarks();
		this.value = stageHeader.getValue();
		this.quotationNo = stageHeader.getQuotationNo();
		this.createdDate = stageHeader.getCreatedDate();
		this.createdByPid = stageHeader.getCreatedBy().getPid();
		this.createdByName = stageHeader.getCreatedBy().getFirstName();
		//dynamic document files
		if(stageHeader.getStageDetails() != null) {
			StageDetail stageDetail = stageHeader.getStageDetails();
			this.stageDetailId = stageDetail.getId();
			this.dynamicDocumentHeaderPid = stageDetail.getDynamicDocumentHeaderPid();
			this.activityName = stageDetail.getActivity().getName();
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "FORM_QUERY_109" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the files by dynamic document header";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			Set<File> dSavedFiles = filledFormRepository.findFilesByDynamicDocumentHeaderPid(this.dynamicDocumentHeaderPid);
			 String flag = "Normal";
				LocalDateTime endLCTime = LocalDateTime.now();
				String endTime = endLCTime.format(DATE_TIME_FORMAT);
				String endDate = startLCTime.format(DATE_FORMAT);
				Duration duration = Duration.between(startLCTime, endLCTime);
				long minutes = duration.toMinutes();
				if (minutes <= 1 && minutes >= 0) {
					flag = "Fast";
				}
				if (minutes > 1 && minutes <= 2) {
					flag = "Normal";
				}
				if (minutes > 2 && minutes <= 10) {
					flag = "Slow";
				}
				if (minutes > 10) {
					flag = "Dead Slow";
				}
		                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
						+ description);
			for (File file : dSavedFiles) {
				files.put(file.getPid(), file.getFileName());
			}
		}
		//stage header files
		if(!stageHeader.getStageHeaderFiles().isEmpty()) {
			for (StageHeaderFile stageHeaderFile : stageHeader.getStageHeaderFiles()) {
				File file = stageHeaderFile.getFile();
				if(file != null) {
					files.put(file.getPid(), file.getFileName());
				}
			}
		}
		rcas = stageHeader.getStageHeaderRca().stream().map(s -> s.getReason()).collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getAccountProfilePid() {
		return accountProfilePid;
	}

	public void setAccountProfilePid(String accountProfilePid) {
		this.accountProfilePid = accountProfilePid;
	}

	public String getAccountProfileName() {
		return accountProfileName;
	}

	public void setAccountProfileName(String accountProfileName) {
		this.accountProfileName = accountProfileName;
	}

	public String getStagePid() {
		return stagePid;
	}

	public void setStagePid(String stagePid) {
		this.stagePid = stagePid;
	}

	public String getStageName() {
		return stageName;
	}

	public void setStageName(String stageName) {
		this.stageName = stageName;
	}

	public String getEmployeeProfilePid() {
		return employeeProfilePid;
	}

	public void setEmployeeProfilePid(String employeeProfilePid) {
		this.employeeProfilePid = employeeProfilePid;
	}

	public String getEmployeeProfileName() {
		return employeeProfileName;
	}

	public void setEmployeeProfileName(String employeeProfileName) {
		this.employeeProfileName = employeeProfileName;
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

	public String getCreatedByPid() {
		return createdByPid;
	}

	public void setCreatedByPid(String createdByPid) {
		this.createdByPid = createdByPid;
	}

	public String getCreatedByName() {
		return createdByName;
	}

	public void setCreatedByName(String createdByName) {
		this.createdByName = createdByName;
	}

	public Long getStageDetailId() {
		return stageDetailId;
	}

	public void setStageDetailId(Long stageDetailId) {
		this.stageDetailId = stageDetailId;
	}

	public String getDynamicDocumentHeaderPid() {
		return dynamicDocumentHeaderPid;
	}

	public void setDynamicDocumentHeaderPid(String dynamicDocumentHeaderPid) {
		this.dynamicDocumentHeaderPid = dynamicDocumentHeaderPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Map<String, String> getFiles() {
		return files;
	}

	public void setFiles(Map<String, String> files) {
		this.files = files;
	}

	
	public List<String> getRcas() {
		return rcas;
	}

	public void setRcas(List<String> rcas) {
		this.rcas = rcas;
	}

	public String[] getRcaPids() {
		return rcaPids;
	}

	public void setRcaPids(String[] rcaPids) {
		this.rcaPids = rcaPids;
	}

	public List<MultipartFile> getMultipartFiles() {
		return multipartFiles;
	}

	public void setMultipartFiles(List<MultipartFile> multipartFiles) {
		this.multipartFiles = multipartFiles;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StageHeaderDTO other = (StageHeaderDTO) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	

	
}
