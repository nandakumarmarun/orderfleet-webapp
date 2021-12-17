package com.orderfleet.webapp.web.vendor.focus.dto;

import java.util.List;

public class ParamsOdoo {

	private String db;
	private String login;
	private String password;

	public String getDb() {
		return db;
	}

	public void setDb(String db) {
		this.db = db;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "ParamsOdoo [db=" + db + ", login=" + login + ", password=" + password + "]";
	}

}
