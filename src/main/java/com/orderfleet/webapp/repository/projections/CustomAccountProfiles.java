package com.orderfleet.webapp.repository.projections;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;


@Projection(name = "customAccountProfiles",types = AccountProfile.class)
public interface CustomAccountProfiles {
	
	@Value("#{target.pid}")
	String getPid();
	
	@Value("#{target.name}")
	String getName();
	
	@Value("#{target.alias}")
	String getAlias();

	@Value("#{target.userName}")
	String getUserName();

	@Value("#{target.description}")
	String getDescription();

	@Value("#{target.accountTypeName}")
	String getAaccountTypeName();

	@Value("#{target.address}")
	String getAddress();

	@Value("#{target.city}")
	String getCity();

	@Value("#{target.location}")
	String getLocation();

	@Value("#{target.pin}")
	String getPin();

	@Value("#{target.latitude}")
	BigDecimal getLatitude();

	@Value("#{target.longitude}")
	BigDecimal getLongitude();

	@Value("#{target.phone1}") 
	String getPhone1();

	@Value("#{target.phone2}")
	String getPhone2();

	@Value("#{target.email1}")
	String getEmail1();

	@Value("#{target.email2}")
	String getEmail2();
	
	@Value("#{target.whatsAppNo}")
	String getWhatsAppNo();

	@Value("#{target.accountStatus}")
	AccountStatus getAccountStatus();

	@Value("#{target.importStatus}")
	boolean getImportStatus();

	@Value("#{target.creditDays}")
	Long getCreditDays();

	@Value("#{target.creditLimit}")
	Long getCreditLimit();

	@Value("#{target.contactPerson}")
	String getContactPerson();
	
	@Value("#{target.activated}")
	boolean getActivated();

	@Value("#{target.lastModifiedDate}")
	LocalDateTime getLastModifiedDate();

	@Value("#{target.createdDate}")
	LocalDateTime getCreatedDate();
	
	@Value("#{target.leadToCashStage}")
	String getLeadToCashStage();

	@Value("#{target.tinNo}")
	String getTinNo();

	@Value("#{target.closingBalance}")
	double getClosingBalance();

	@Value("#{target.defaultDiscountPercentage}")
	double getDefaultDiscountPercentage();

	@Value("#{target.trimChar}")
	String getTrimChar();
	
	@Value("#{target.locationRadius}")
	double getlocationRadius();
	
	

//	@Value("#{target.hasDefaultAccountInventory}")
//	boolean getHasDefaultAccountInventory();
//	
//	@Value("#{target.promptStockLocationInventory}")
//	boolean getPromptStockLocationInventory();
	
	@Value("#{target.dataSourceType}")
	DataSourceType getDataSourceType();
}
