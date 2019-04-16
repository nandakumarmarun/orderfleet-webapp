package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.StaticFormJSCode;

/**
 * a DTO for StaticFormJSCode
 * 
 * @author Sarath
 * @since Aug 3, 2016
 */
public class StaticFormJSCodeDTO {
	private Long id;
	private String jsCode;
	private String jsCodeName;
	private String documentPid;
	private String documentName;

	public StaticFormJSCodeDTO() {
		super();
	}

	public StaticFormJSCodeDTO(StaticFormJSCode dynamicFormula) {
		this(dynamicFormula.getId(), dynamicFormula.getJsCode(), dynamicFormula.getJsCodeName(), dynamicFormula
				.getDocument().getPid(), dynamicFormula.getDocument().getName());
	}

	public StaticFormJSCodeDTO(Long id, String jsCode, String jsCodeName, String documentPid, String documentName) {
		super();
		this.id = id;
		this.jsCode = jsCode;
		this.jsCodeName = jsCodeName;
		this.documentPid = documentPid;
		this.documentName = documentName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getJsCode() {
		return jsCode;
	}

	public void setJsCode(String jsCode) {
		this.jsCode = jsCode;
	}

	public String getJsCodeName() {
		return jsCodeName;
	}

	public void setJsCodeName(String jsCodeName) {
		this.jsCodeName = jsCodeName;
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
}
