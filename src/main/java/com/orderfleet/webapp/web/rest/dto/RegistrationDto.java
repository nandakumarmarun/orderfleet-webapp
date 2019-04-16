package com.orderfleet.webapp.web.rest.dto;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotEmpty;

import com.orderfleet.webapp.domain.enums.PartnerIntegrationSystem;

public class RegistrationDto {
/*	
	@NotEmpty(message = "Please select a partner")
	private String partnerPid;*/
	
	@Pattern(regexp = "^[a-zA-Z ]*$", message = "Your company name must be characters")
	@Size(min = 3, max = 100 , message = "Your company name must between 3 and 50 characters")
	private String legalName;//company name of company under partner - name used for registering company in snrich

	@Size(min = 3, max = 20 , message = "Your company short name must between 3 and 20 characters")
	private String alias;
	
	@Size(min = 6, max = 100, message = "Your password must be minimum 6 characters")
	private String password;
	
    @Email( message = "Email not valid")
    @Size(min = 5, max = 254, message = "Your email must between 5 and 150 characters")
	private String email;
	
	@Pattern(regexp="(\\d{10})", message = "Your mobile number must be 10 digits")
	private String phone;
	
    @Size(min = 5, max = 500, message = "Your address must be minimum 5 characters")
	private String address;
    
    private String userName;
    
	private int mobileUserCount = 1;
	
	//product key of partner desktop application
	private String pkey;
	
	//company code of integra company ( id from integra db)
	private String cCode;
	
	//unique pid of partner - given after a partner is registered in salesnrich
	private String partnerKey;
	
	private String partnerName;
	
	//salesnrich product name provided by partner company
	private String snrichProduct;
	
	private String snrichProductKey;
	
	private PartnerIntegrationSystem partnerIntegrationSystem;

	
	/*public String getPartnerPid() {
		return partnerPid;
	}

	public void setPartnerPid(String partnerPid) {
		this.partnerPid = partnerPid;
	}*/

	public String getLegalName() {
		return legalName;
	}

	public void setLegalName(String legalName) {
		this.legalName = legalName;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getMobileUserCount() {
		return mobileUserCount;
	}

	public void setMobileUserCount(int mobileUserCount) {
		this.mobileUserCount = mobileUserCount;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public String getcCode() {
		return cCode;
	}

	public void setcCode(String cCode) {
		this.cCode = cCode;
	}

	public String getPartnerKey() {
		return partnerKey;
	}

	public void setPartnerKey(String partnerKey) {
		this.partnerKey = partnerKey;
	}
	
	public String getPartnerName() {
		return partnerName;
	}

	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}

	public String getSnrichProduct() {
		return snrichProduct;
	}

	public void setSnrichProduct(String snrichProduct) {
		this.snrichProduct = snrichProduct;
	}
	

	public String getSnrichProductKey() {
		return snrichProductKey;
	}

	public void setSnrichProductKey(String snrichProductKey) {
		this.snrichProductKey = snrichProductKey;
	}

	public PartnerIntegrationSystem getPartnerIntegrationSystem() {
		return partnerIntegrationSystem;
	}

	public void setPartnerIntegrationSystem(PartnerIntegrationSystem partnerIntegrationSystem) {
		this.partnerIntegrationSystem = partnerIntegrationSystem;
	}

	@Override
	public String toString() {
		return "RegistrationDto [legalName=" + legalName + ", alias=" + alias + ", password=" + password + ", email="
				+ email + ", phone=" + phone + ", address=" + address + ", userName=" + userName + ", mobileUserCount="
				+ mobileUserCount + ", pkey=" + pkey + ", cCode=" + cCode + ", partnerKey=" + partnerKey
				+ ", partnerName=" + partnerName + ", snrichProduct=" + snrichProduct + ", snrichProductKey="
				+ snrichProductKey + "]";
	}

	
	
	
}
