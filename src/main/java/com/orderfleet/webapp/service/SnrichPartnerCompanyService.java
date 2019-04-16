package com.orderfleet.webapp.service;

import java.util.List;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;
import com.orderfleet.webapp.web.rest.dto.SnrichPartnerCompanyDTO;

public interface SnrichPartnerCompanyService {

	void save(RegistrationDto partnerPid,Company company,int usersCount);
	
	List<SnrichPartnerCompanyDTO> findByUsersCountGreaterThanZeroAndUserAdminExist(int usersCount);
}
