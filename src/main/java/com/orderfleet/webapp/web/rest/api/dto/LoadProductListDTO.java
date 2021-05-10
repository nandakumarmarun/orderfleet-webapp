package com.orderfleet.webapp.web.rest.api.dto;

import java.util.ArrayList;
import java.util.List;

import com.orderfleet.webapp.web.rest.dto.AccountingVoucherHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskExecutionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

/**
 * A LoadServerSentItemDTO DTO.
 * 
 * @author Prashob Sasidharan
 * @since August 22, 2019
 */
public class LoadProductListDTO {

	private String productName;

	private double productPrice;

	private double productStock;

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public double getProductPrice() {
		return productPrice;
	}

	public void setProductPrice(double productPrice) {
		this.productPrice = productPrice;
	}

	public double getProductStock() {
		return productStock;
	}

	public void setProductStock(double productStock) {
		this.productStock = productStock;
	}

}
