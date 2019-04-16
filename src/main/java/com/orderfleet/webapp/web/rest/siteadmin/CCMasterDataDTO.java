package com.orderfleet.webapp.web.rest.siteadmin;

public class CCMasterDataDTO {
	
	private Long fromCompanyId;
	
	private Long toCompanyId;
	
	private String fromSchema;
	
	private String toSchema;
	
	private String[] tblNames;
	
	public CCMasterDataDTO() {
		super();
	}

	public Long getFromCompanyId() {
		return fromCompanyId;
	}

	public void setFromCompanyId(Long fromCompanyId) {
		this.fromCompanyId = fromCompanyId;
	}

	public Long getToCompanyId() {
		return toCompanyId;
	}

	public void setToCompanyId(Long toCompanyId) {
		this.toCompanyId = toCompanyId;
	}

	public String getFromSchema() {
		return fromSchema;
	}

	public void setFromSchema(String fromSchema) {
		this.fromSchema = fromSchema;
	}

	public String getToSchema() {
		return toSchema;
	}

	public void setToSchema(String toSchema) {
		this.toSchema = toSchema;
	}

	public String[] getTblNames() {
		return tblNames;
	}

	public void setTblNames(String[] tblNames) {
		this.tblNames = tblNames;
	}

}
