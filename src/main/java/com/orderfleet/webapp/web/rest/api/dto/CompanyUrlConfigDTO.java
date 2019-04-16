package com.orderfleet.webapp.web.rest.api.dto;

public class CompanyUrlConfigDTO {

	private boolean validUser; 
	
	private String baseUrl;
	
	private String webLoginUrl;
	
	private boolean showComanyNameAndLogo;
	
	public CompanyUrlConfigDTO() {
		this.validUser = Boolean.TRUE;
		this.baseUrl = "http://salesnrich.com";
		this.webLoginUrl = baseUrl + "/login";
		this.showComanyNameAndLogo = Boolean.FALSE;
	}

	public boolean getValidUser() {
		return validUser;
	}

	public void setValidUser(boolean validUser) {
		this.validUser = validUser;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getWebLoginUrl() {
		return webLoginUrl;
	}

	public void setWebLoginUrl(String webLoginUrl) {
		this.webLoginUrl = webLoginUrl;
	}

	public boolean getShowComanyNameAndLogo() {
		return showComanyNameAndLogo;
	}

	public void setShowComanyNameAndLogo(boolean showComanyNameAndLogo) {
		this.showComanyNameAndLogo = showComanyNameAndLogo;
	}
	
}
