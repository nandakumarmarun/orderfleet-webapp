package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;

/**
 * Service Interface for set up company by site admin.
 * 
 * @author Shaheer
 * @since November 14, 2016
 */
public interface SetupCompanyService {

	public Company cloneCompany(String existingCompanyPid, String legalName, String email);

	public User cloneUser(String existingUserPid, String login, String email, Company company);

	public void cloneProductProfiles(String existingCompanyPid, Company company);

	public void cloneAccountProfiles(String existingCompanyPid, Company company);

	public void cloneEmployeeProfilesAndLocations(String existingCompanyPid, Company company);

	public void cloneDocuments(String existingCompanyPid, Company company);

}
