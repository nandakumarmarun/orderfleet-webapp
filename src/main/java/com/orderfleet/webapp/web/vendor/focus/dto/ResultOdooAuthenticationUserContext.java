package com.orderfleet.webapp.web.vendor.focus.dto;

public class ResultOdooAuthenticationUserContext {

	private String lang;
	private String tz;
	private long uid;

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getTz() {
		return tz;
	}

	public void setTz(String tz) {
		this.tz = tz;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	@Override
	public String toString() {
		return "ResultOdooAuthenticationUserContext [lang=" + lang + ", tz=" + tz + ", uid=" + uid + "]";
	}

}
