package com.orderfleet.webapp.web.rest.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * a dto for BestPerformer.
 *
 * @author Sarath
 * @since Mar 26, 2018
 *
 */
public class BestPerformerDTO {

	Map<String, Double> salesPerformer = new HashMap<>();
	Map<String, Double> receiptPerformer = new HashMap<>();
	
	public Map<String, Double> getSalesPerformer() {
		return salesPerformer;
	}
	public void setSalesPerformer(Map<String, Double> salesPerformer) {
		this.salesPerformer = salesPerformer;
	}
	public Map<String, Double> getReceiptPerformer() {
		return receiptPerformer;
	}
	public void setReceiptPerformer(Map<String, Double> receiptPerformer) {
		this.receiptPerformer = receiptPerformer;
	}
	

}
