package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.ReceivablePayableColumnConfig;

public class ReceivablePayableColumnConfigDTO {

	private Long id;
	
	private String column;

	private boolean enabled;

	public ReceivablePayableColumnConfigDTO(ReceivablePayableColumnConfig receivablePayableColumnConfig) {
		super();
		this.id = receivablePayableColumnConfig.getId();
		this.column = receivablePayableColumnConfig.getReceivablePayableColumn().getName();
		this.enabled = receivablePayableColumnConfig.getEnabled();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


}
