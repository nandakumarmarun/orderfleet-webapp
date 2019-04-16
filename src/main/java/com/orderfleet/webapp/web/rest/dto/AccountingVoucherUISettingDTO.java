package com.orderfleet.webapp.web.rest.dto;

import com.orderfleet.webapp.domain.AccountingVoucherUISetting;
import com.orderfleet.webapp.domain.enums.PaymentMode;

public class AccountingVoucherUISettingDTO {
	private Long id;
	
	private String name;
	
	private String title;
	
	private String activityPid;
	
	private String activityName;
	
	
	private String documentPid;
	
	private String documentName;
	
	private PaymentMode paymentMode;

	public AccountingVoucherUISettingDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AccountingVoucherUISettingDTO(Long id, String name, String title, String activityPid, String activityName, String documentPid, String documentName, PaymentMode paymentMode) {
		super();
		this.id = id;
		this.name = name;
		this.title = title;
		this.activityPid = activityPid;
		this.activityName = activityName;
		this.documentPid = documentPid;
		this.documentName = documentName;
		this.paymentMode = paymentMode;
	}
	public AccountingVoucherUISettingDTO(AccountingVoucherUISetting accountingVoucherUISetting) {
		super();
		this.id = accountingVoucherUISetting.getId();
		this.name = accountingVoucherUISetting.getName();
		this.title = accountingVoucherUISetting.getTitle();
		this.activityPid = accountingVoucherUISetting.getActivity().getPid();
		this.activityName = accountingVoucherUISetting.getActivity().getName();
		this.documentPid = accountingVoucherUISetting.getDocument().getPid();
		this.documentName = accountingVoucherUISetting.getDocument().getName();
		this.paymentMode = accountingVoucherUISetting.getPaymentMode();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getActivityPid() {
		return activityPid;
	}

	public void setActivityPid(String activityPid) {
		this.activityPid = activityPid;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
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

	public PaymentMode getPaymentMode() {
		return paymentMode;
	}

	public void setPaymentMode(PaymentMode paymentMode) {
		this.paymentMode = paymentMode;
	}

	@Override
	public String toString() {
		return "AccountingVoucherUISettingDTO [id=" + id + ", name=" + name + ", title=" + title + ", activityPid="
				+ activityPid + ", activityName=" + activityName + ", documentPid="
				+ documentPid + ", documentName=" + documentName + ", paymentMode=" + paymentMode + "]";
	}
	
	
}
