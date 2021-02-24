package com.orderfleet.webapp.domain.enums;

public enum ProcessFlowStatus {
	DEFAULT, PO_PLACED, IN_STOCK, PO_ACCEPTED_AT_TSL, UNDER_PRODUCTION, READY_TO_DISPATCH_AT_TSL,
	READY_TO_DISPATCH_AT_PS, DELIVERED, NOT_DELIVERED, INSTALLATION_PLANNED, INSTALLED
}