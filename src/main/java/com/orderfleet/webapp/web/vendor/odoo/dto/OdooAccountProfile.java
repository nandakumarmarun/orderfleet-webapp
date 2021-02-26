package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class OdooAccountProfile {

	private long price_list_id;

	private boolean customer_blocked;

	private String street2;

	private String phone;

	private String street;

	private long id;

	private String city;

	private String credit_limit;

	private String name;

	private String trn;

	private String credit;

	private String debit;

	private String ref;

	private String email;

	private List<OdooAccountProfileCreditTerms> credit_terms;

	public long getPrice_list_id() {
		return price_list_id;
	}

	public void setPrice_list_id(long price_list_id) {
		this.price_list_id = price_list_id;
	}

	public boolean isCustomer_blocked() {
		return customer_blocked;
	}

	public void setCustomer_blocked(boolean customer_blocked) {
		this.customer_blocked = customer_blocked;
	}

	public String getStreet2() {
		return street2;
	}

	public void setStreet2(String street2) {
		this.street2 = street2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCredit_limit() {
		return credit_limit;
	}

	public void setCredit_limit(String credit_limit) {
		this.credit_limit = credit_limit;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTrn() {
		return trn;
	}

	public void setTrn(String trn) {
		this.trn = trn;
	}

	public String getCredit() {
		return credit;
	}

	public void setCredit(String credit) {
		this.credit = credit;
	}

	public String getDebit() {
		return debit;
	}

	public void setDebit(String debit) {
		this.debit = debit;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public List<OdooAccountProfileCreditTerms> getCredit_terms() {
		return credit_terms;
	}

	public void setCredit_terms(List<OdooAccountProfileCreditTerms> credit_terms) {
		this.credit_terms = credit_terms;
	}

}
