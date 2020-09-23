package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResultOdooAuthentication {

	private String username;

	private String db;

	private long uid;

	private String session_id;

	private ResultOdooAuthenticationUserContext user_context;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public long getUid() {
		return uid;
	}

	public void setUid(long uid) {
		this.uid = uid;
	}

	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public ResultOdooAuthenticationUserContext getUser_context() {
		return user_context;
	}

	public void setUser_context(ResultOdooAuthenticationUserContext user_context) {
		this.user_context = user_context;
	}

	@Override
	public String toString() {
		return "ResultOdooAuthentication [username=" + username + ", db=" + db + ", uid=" + uid + ", session_id="
				+ session_id + ", user_context=" + user_context + "]";
	}

}
