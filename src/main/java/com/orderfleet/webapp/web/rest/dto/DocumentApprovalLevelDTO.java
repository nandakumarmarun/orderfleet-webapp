package com.orderfleet.webapp.web.rest.dto;

import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.orderfleet.webapp.domain.DocumentApprovalLevel;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;

/**
 * A DTO for the DocumentApprovalLevel entity.
 * 
 * @author Muhammed Riyas T
 * @since November 19, 2016
 */
public class DocumentApprovalLevelDTO {

	private String pid;

	@NotNull
	@Size(min = 1, max = 255)
	private String name;

	private boolean required;

	private int approvalOrder;

	private int approverCount;

	private String script;

	private String documentPid;

	private String documentName;

	private Set<UserDTO> users = new HashSet<UserDTO>();

	public DocumentApprovalLevelDTO() {

	}

	public DocumentApprovalLevelDTO(DocumentApprovalLevel documentApprovalLevel) {
		super();
		this.pid = documentApprovalLevel.getPid();
		this.name = documentApprovalLevel.getName();
		this.required = documentApprovalLevel.getRequired();
		this.approvalOrder = documentApprovalLevel.getApprovalOrder();
		this.approverCount = documentApprovalLevel.getApproverCount();
		this.script = documentApprovalLevel.getScript();
		this.documentPid = documentApprovalLevel.getDocument().getPid();
		this.documentName = documentApprovalLevel.getDocument().getName();
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

	public boolean getRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public int getApprovalOrder() {
		return approvalOrder;
	}

	public void setApprovalOrder(int approvalOrder) {
		this.approvalOrder = approvalOrder;
	}

	public int getApproverCount() {
		return approverCount;
	}

	public void setApproverCount(int approverCount) {
		this.approverCount = approverCount;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}

	public String getDocumentPid() {
		return documentPid;
	}

	public void setDocumentPid(String documentPid) {
		this.documentPid = documentPid;
	}

	public String getDocumentName() {
		return documentName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public Set<UserDTO> getUsers() {
		return users;
	}

	public void setUsers(Set<UserDTO> users) {
		this.users = users;
	}

}
