package com.orderfleet.webapp.web.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.orderfleet.webapp.domain.ClientAppLogFiles;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.GeoTaggingType;
import com.orderfleet.webapp.repository.projections.CustomAccountProfiles;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientAppLogDTO {

	private String pid;

	private String logName;

	private LocalDate logDate;

	private LocalDateTime createdDate;

	private LocalDateTime lastModifiedDAte;

	public ClientAppLogDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ClientAppLogDTO(ClientAppLogFiles clientAppLogFile) {
		super();
		this.pid = clientAppLogFile.getPid();
		this.logName = clientAppLogFile.getFileName();
		this.logDate = clientAppLogFile.getLogDate();
		this.createdDate = clientAppLogFile.getCreatedDate();
		this.lastModifiedDAte = clientAppLogFile.getLastModifiedDate();
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getLogName() {
		return logName;
	}

	public void setLogName(String logName) {
		this.logName = logName;
	}

	public LocalDate getLogDate() {
		return logDate;
	}

	public void setLogDate(LocalDate logDate) {
		this.logDate = logDate;
	}

	public LocalDateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}

	public LocalDateTime getLastModifiedDAte() {
		return lastModifiedDAte;
	}

	public void setLastModifiedDAte(LocalDateTime lastModifiedDAte) {
		this.lastModifiedDAte = lastModifiedDAte;
	}

}
