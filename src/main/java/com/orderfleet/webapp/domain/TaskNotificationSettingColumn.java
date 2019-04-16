package com.orderfleet.webapp.domain;

import javax.persistence.Embeddable;

@Embeddable
public class TaskNotificationSettingColumn {

	private String columnName;
	
	private String columnLabel;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnLabel() {
		return columnLabel;
	}

	public void setColumnLabel(String columnLabel) {
		this.columnLabel = columnLabel;
	}
	
	

}
