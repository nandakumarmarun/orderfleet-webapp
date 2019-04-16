package com.orderfleet.webapp.report.enums;

public enum ExecutiveTaskExecutionColumn implements TableColumns {
	
	LOGIN("Username"), ACTIVITY("Activity"), ACCOUNTPROFILE("Account Profile"), LOCATION("Location"), REMARKS(
			"Remarks");

	private final String columnName;

	private ExecutiveTaskExecutionColumn(String colName) {
		this.columnName = colName;
	}

	@Override
	public String getColumnName() {
		return this.columnName;
	}

}
