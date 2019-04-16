package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.SnrichPartnerRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.SnrichPartnerCompanyService;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;
import com.orderfleet.webapp.web.rest.dto.SnrichPartnerCompanyDTO;

@Service
@Transactional
public class SnrichPartnerCompanyServiceImpl implements SnrichPartnerCompanyService{

	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private SnrichPartnerRepository snrichPartnerRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Override
	public void save(RegistrationDto regResource, Company company, int usersCount) {
		SnrichPartnerCompany snrichPartnerCompany = new SnrichPartnerCompany();
		snrichPartnerCompany.setPartner(snrichPartnerRepository.findOneByPid(regResource.getPartnerKey()).get());
		snrichPartnerCompany.setCompany(company);
		snrichPartnerCompany.setUsersCount(usersCount);
		snrichPartnerCompany.setDbCompany(regResource.getPkey()+"~"+regResource.getcCode());
		snrichPartnerCompanyRepository.save(snrichPartnerCompany);
	}

	@Override
	public List<SnrichPartnerCompanyDTO> findByUsersCountGreaterThanZeroAndUserAdminExist(int usersCount) {
		
		List<SnrichPartnerCompanyDTO> snrichPartnerCompanyDTOs = new ArrayList<>();
		List<SnrichPartnerCompany> snrichPartnerCompanies = snrichPartnerCompanyRepository.findByUsersCountGreaterThan(usersCount);
		
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority("ROLE_OP_ADMIN"));
		
		if(snrichPartnerCompanies != null && !snrichPartnerCompanies.isEmpty()){
			snrichPartnerCompanies.forEach(snrichPartnerCompany -> {
				Long userAdminCount = userRepository.countByCompanyPidAndAuthoritiesIn(
						snrichPartnerCompany.getCompany().getPid(), authorities);
				SnrichPartnerCompanyDTO snrichPartnerCompanyDTO = new SnrichPartnerCompanyDTO(snrichPartnerCompany);
				if(userAdminCount == 0){
					snrichPartnerCompanyDTO.setUserAdminExist(false);
				}else{
					snrichPartnerCompanyDTO.setUserAdminExist(true);
				}
				snrichPartnerCompanyDTOs.add(snrichPartnerCompanyDTO);
			});
		}
		return snrichPartnerCompanyDTOs;
	}
	
}
