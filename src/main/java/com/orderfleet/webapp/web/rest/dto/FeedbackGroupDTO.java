package com.orderfleet.webapp.web.rest.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.FeedbackGroup;

/**
 * A DTO for the FeedbackGroup entity.
 * 
 * @author Muhammed Riyas T
 * @since Feb 11, 2017
 */
public class FeedbackGroupDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private String statusFieldPid;

	private String statusFieldName;

	public FeedbackGroupDTO() {
		super();
	}

	public FeedbackGroupDTO(FeedbackGroup feedbackGroup) {
		super();
		this.pid = feedbackGroup.getPid();
		this.name = feedbackGroup.getName();
		this.alias = feedbackGroup.getAlias();
		this.description = feedbackGroup.getDescription();
		if (feedbackGroup.getStatusField() != null) {
			this.statusFieldPid = feedbackGroup.getStatusField().getPid();
			this.statusFieldName = feedbackGroup.getStatusField().getName();
		}
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

	public String getStatusFieldPid() {
		return statusFieldPid;
	}

	public void setStatusFieldPid(String statusFieldPid) {
		this.statusFieldPid = statusFieldPid;
	}

	public String getStatusFieldName() {
		return statusFieldName;
	}

	public void setStatusFieldName(String statusFieldName) {
		this.statusFieldName = statusFieldName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		FeedbackGroupDTO feedbackGroupDTO = (FeedbackGroupDTO) o;

		if (!Objects.equals(pid, feedbackGroupDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "FeedbackGroupDTO{" + ", pid='" + pid + "'" + ", name='" + name + "'" + ", alias='" + alias + "'"
				+ ", description='" + description + "'" + '}';
	}
}
