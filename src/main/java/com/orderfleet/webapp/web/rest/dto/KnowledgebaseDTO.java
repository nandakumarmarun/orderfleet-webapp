package com.orderfleet.webapp.web.rest.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.Knowledgebase;
import com.orderfleet.webapp.domain.ProductGroup;

/**
 * A DTO for the Knowledgebase entity.
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
public class KnowledgebaseDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	@Size(max = 55)
	private String alias;

	private String description;

	private String productGroupPid;

	private String productGroupName;

	private boolean activated;

	private LocalDateTime lastModifiedDate;

	public KnowledgebaseDTO() {
		super();
	}

	public KnowledgebaseDTO(Knowledgebase knowledgebase) {
		this(knowledgebase.getPid(), knowledgebase.getName(), knowledgebase.getAlias(), knowledgebase.getDescription(),
				knowledgebase.getProductGroup(), knowledgebase.getLastModifiedDate());
	}

	public KnowledgebaseDTO(String pid, String name, String alias, String description, ProductGroup productGroup,
			LocalDateTime lastModifiedDate) {
		super();
		this.pid = pid;
		this.name = name;
		this.alias = alias;
		this.description = description;
		this.productGroupPid = productGroup == null ? null : productGroup.getPid();
		this.productGroupName = productGroup == null ? null : productGroup.getName();
		this.lastModifiedDate = lastModifiedDate;
	}

	public boolean getActivated() {
		return activated;
	}

	public void setActivated(boolean activated) {
		this.activated = activated;
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

	public String getProductGroupPid() {
		return productGroupPid;
	}

	public void setProductGroupPid(String productGroupPid) {
		this.productGroupPid = productGroupPid;
	}

	public String getProductGroupName() {
		return productGroupName;
	}

	public void setProductGroupName(String productGroupName) {
		this.productGroupName = productGroupName;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}

		KnowledgebaseDTO knowledgebaseDTO = (KnowledgebaseDTO) o;

		if (!Objects.equals(pid, knowledgebaseDTO.pid))
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(pid);
	}

	@Override
	public String toString() {
		return "KnowledgebaseDTO [pid=" + pid + ", name=" + name + ", alias=" + alias + ", description=" + description
				+ ", productGroupPid=" + productGroupPid + ", productGroupName=" + productGroupName + ", activated="
				+ activated + "]";
	}

}
