package com.orderfleet.webapp.domain.model;

import java.io.Serializable;

/**
 * This is not a normal entity. this is just a dependency for CompanySetting
 * persisted inside a JSON column
 * 
 * @author Shaheer
 * @since December 05, 2016
 * 
 * @see CompanySetting
 * @see ObjectType
 *
 */
public class PrintConfiguration implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String printHtmlView;
	
	private String otherVoucherEmailView;

	public PrintConfiguration() {
		super();
	}

	public String getPrintHtmlView() {
		return printHtmlView;
	}

	public void setPrintHtmlView(String printHtmlView) {
		this.printHtmlView = printHtmlView;
	}
	
	public String getOtherVoucherEmailView() {
		return otherVoucherEmailView;
	}

	public void setOtherVoucherEmailView(String otherVoucherEmailView) {
		this.otherVoucherEmailView = otherVoucherEmailView;
	}

	@Override
	public String toString() {
		return "PrintConfiguration [printHtmlView=" + printHtmlView + ", otherVoucherEmailView=" + otherVoucherEmailView
				+ "]";
	}

}
