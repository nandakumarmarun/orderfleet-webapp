package com.orderfleet.webapp.repository.projections;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.orderfleet.webapp.domain.InventoryVoucherDetail;

@Projection(name = "customInventoryVoucherDetail",types = InventoryVoucherDetail.class)
public interface CustomInventoryVoucherDetail {

	@Value("#{target.createdDateTime}")
	LocalDateTime getCreatedDateTime();
	
	@Value("#{target.employeeName}")
	String getEmployeeName();
	
	@Value("#{target.sourceStockLocationName}")
	String getSourceStockLocationName();
	
	@Value("#{target.destinationStockLocationName}")
	String getDestinationStockLocationName();
	
	@Value("#{target.productCategoryName}")
	String getProductCategoryName();
	
	@Value("#{target.productName}")
	String getProductName();
	
	@Value("#{target.quantity}")
	double getQuantity();
	
	@Value("#{target.sellingRate}")
	double getSellingRate();
	
	@Value("#{target.rowTotal}")
	double getRowTotal();
	
	@Value("#{target.productPid}")
	String getProductPid();
	
}
