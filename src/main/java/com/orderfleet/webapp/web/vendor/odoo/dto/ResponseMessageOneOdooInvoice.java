package com.orderfleet.webapp.web.vendor.odoo.dto;

import java.util.List;

public class ResponseMessageOneOdooInvoice {
	private List<List<Long>> message_follower_ids;
	
	private long account_id;
	
	private String date_invoice;
	
	private long inventory_location_id;
	
	private boolean picking;
	
	private long location_id;
	
	private long sale_type;
	
	private long currency_id;
	
	private long salesperson_id;
	
	private long partner_id;

	private long journal_id;
	
	private long pricelist_id;
	
	private String inv_ref;

	public List<List<Long>> getMessage_follower_ids() {
		return message_follower_ids;
	}

	public void setMessage_follower_ids(List<List<Long>> message_follower_ids) {
		this.message_follower_ids = message_follower_ids;
	}

	public long getAccount_id() {
		return account_id;
	}

	public void setAccount_id(long account_id) {
		this.account_id = account_id;
	}

	public String getDate_invoice() {
		return date_invoice;
	}

	public void setDate_invoice(String date_invoice) {
		this.date_invoice = date_invoice;
	}

	public long getInventory_location_id() {
		return inventory_location_id;
	}

	public void setInventory_location_id(long inventory_location_id) {
		this.inventory_location_id = inventory_location_id;
	}

	public boolean isPicking() {
		return picking;
	}

	public void setPicking(boolean picking) {
		this.picking = picking;
	}

	public long getLocation_id() {
		return location_id;
	}

	public void setLocation_id(long location_id) {
		this.location_id = location_id;
	}

	public long getSale_type() {
		return sale_type;
	}

	public void setSale_type(long sale_type) {
		this.sale_type = sale_type;
	}

	public long getCurrency_id() {
		return currency_id;
	}

	public void setCurrency_id(long currency_id) {
		this.currency_id = currency_id;
	}

	public long getSalesperson_id() {
		return salesperson_id;
	}

	public void setSalesperson_id(long salesperson_id) {
		this.salesperson_id = salesperson_id;
	}

	public long getPartner_id() {
		return partner_id;
	}

	public void setPartner_id(long partner_id) {
		this.partner_id = partner_id;
	}

	public long getJournal_id() {
		return journal_id;
	}

	public void setJournal_id(long journal_id) {
		this.journal_id = journal_id;
	}

	public long getPricelist_id() {
		return pricelist_id;
	}

	public void setPricelist_id(long pricelist_id) {
		this.pricelist_id = pricelist_id;
	}

	public String getInv_ref() {
		return inv_ref;
	}

	public void setInv_ref(String inv_ref) {
		this.inv_ref = inv_ref;
	}

	@Override
	public String toString() {
		return "ResponseMessageOneOdooInvoice [message_follower_ids=" + message_follower_ids + ", account_id="
				+ account_id + ", date_invoice=" + date_invoice + ", inventory_location_id=" + inventory_location_id
				+ ", picking=" + picking + ", location_id=" + location_id + ", sale_type=" + sale_type
				+ ", currency_id=" + currency_id + ", salesperson_id=" + salesperson_id + ", partner_id=" + partner_id
				+ ", journal_id=" + journal_id + ", pricelist_id=" + pricelist_id + ", inv_ref=" + inv_ref + "]";
	}
	
	
	
	
}
