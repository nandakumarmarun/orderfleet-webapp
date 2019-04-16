package com.orderfleet.webapp.web.rest.dto;

public class CreateAccountDTO {

	private String name;

	private String email;

	private String password;

	private String companyName;

	private String shortName;

	private String gstNo;

	private int selectedProduct;

	private String country;

	private int usersCount;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public int getSelectedProduct() {
		return selectedProduct;
	}

	public void setSelectedProduct(int selectedProduct) {
		this.selectedProduct = selectedProduct;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public int getUsersCount() {
		return usersCount;
	}

	public void setUsersCount(int usersCount) {
		this.usersCount = usersCount;
	}

	public String getGstNo() {
		return gstNo;
	}

	public void setGstNo(String gstNo) {
		this.gstNo = gstNo;
	}

	@Override
	public String toString() {
		return "CreateAccountDTO [name=" + name + ", email=" + email + ", password=" + password + ", companyName="
				+ companyName + ", shortName=" + shortName + ", selectedProduct=" + selectedProduct + ", country="
				+ country + ", usersCount=" + usersCount + ", gstNo=" + gstNo + "]";
	}

}
