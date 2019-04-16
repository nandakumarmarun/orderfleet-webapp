package com.orderfleet.webapp.report.enums;

public enum DynamicDocumentHeaderColumn implements TableColumns {
	
	CREATEDDATE("Created Date"), DOUMENTDATE("Document Date"), EMPLOYEE("Employee");

	private final String columnName;

	private DynamicDocumentHeaderColumn(String colName) {
		this.columnName = colName;
	}

	@Override
	public String getColumnName() {
		return this.columnName;
	}
}
