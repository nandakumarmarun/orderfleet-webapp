package com.orderfleet.webapp.service.impl;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Authority;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.CompanyType;
import com.orderfleet.webapp.domain.enums.DashboardUIType;
import com.orderfleet.webapp.domain.enums.Industry;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.PartnerService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.ManagedUserDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.PartnerDTO;

/**
 * Service Implementation for managing Partner.
 *
 * @author Sarath
 * @since Feb 22, 2018
 *
 */

@Service
@Transactional
public class PartnerServiceImpl implements PartnerService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Inject
	private CompanyService companyService;

	@Inject
	private UserRepository userRepository;

	@Inject
	private UserService userService;

	@Override
	public PartnerDTO save(PartnerDTO partnerDTO) {
		log.debug("request to save Partner : {}", partnerDTO);
		CompanyViewDTO companyViewDTO = saveCompany(partnerDTO);
		User user = saveUser(partnerDTO, companyViewDTO);
		PartnerDTO result = new PartnerDTO(user);
		return result;
	}

	@Override
	public PartnerDTO update(PartnerDTO partnerDTO) {
		log.debug("request to update Partner : {}", partnerDTO);
		CompanyViewDTO companyViewDTO = updateCompany(partnerDTO);
		User user = updateUser(partnerDTO, companyViewDTO);
		PartnerDTO result = new PartnerDTO(user);
		return result;
	}

	@Override
	public List<PartnerDTO> GetAllActivatedPartners(boolean activated) {
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority("ROLE_PARTNER"));
		List<User> users = userService.findByAuthoritiesInAndActivated(authorities, activated);
		List<PartnerDTO> result = users.stream().map(PartnerDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public List<PartnerDTO> GetAllPartners() {
		Set<Authority> authorities = new HashSet<>();
		authorities.add(new Authority("ROLE_PARTNER"));
		List<User> users = userService.findByAuthoritiesIn(authorities);
		List<PartnerDTO> result = users.stream().map(PartnerDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Optional<PartnerDTO> findOneByPid(String pid) {
		return userRepository.findOneByPid(pid).map(user -> {
			PartnerDTO partnerDTO = new PartnerDTO(user);
			return partnerDTO;
		});
	}

	@Override
	public PartnerDTO updatePartnerStatus(String pid, Boolean activated) {
		log.debug(" request to update Partner status : {}");
		ManagedUserDTO managedUserDTO = userService.changeStatusUser(pid, activated);
		PartnerDTO result = new PartnerDTO(managedUserDTO);
		return result;
	}

	@Transactional
	private User updateUser(PartnerDTO partnerDTO, CompanyViewDTO companyViewDTO) {
		log.debug(" request to save Partner User : {}", partnerDTO.getLogin());
		ManagedUserDTO user = userService.findOneByPid(partnerDTO.getPid());
		// common datas are setting to user
		ManagedUserDTO managedUserDTO = createUserSetCommonValues(partnerDTO, companyViewDTO);
		managedUserDTO.setPid(user.getPid());
		User result = userService.updateUser(managedUserDTO);
		return result;
	}

	@Transactional
	private CompanyViewDTO updateCompany(PartnerDTO partnerDTO) {
		log.debug(" request to update Partner Company : {}", partnerDTO.getLogin());
		CompanyViewDTO companyViewDTO = new CompanyViewDTO();
		companyViewDTO = createCompanySetCommonValues(partnerDTO);
		ManagedUserDTO user = userService.findOneByPid(partnerDTO.getPid());
		companyViewDTO.setPid(user.getCompanyPid());
		companyViewDTO.setIndustry(Industry.GENERAL);
		companyViewDTO.setCompanyType(CompanyType.GENERAL);
		companyViewDTO.setActivated(true);
		CompanyViewDTO result = companyService.update(companyViewDTO);
		return result;
	}

	@Transactional
	private CompanyViewDTO saveCompany(PartnerDTO partnerDTO) {
		log.debug(" request to save Partner Company : {}", partnerDTO.getLogin());

		CompanyViewDTO companyViewDTO = new CompanyViewDTO();

		companyViewDTO = createCompanySetCommonValues(partnerDTO);
		companyViewDTO.setAddress2("");
		companyViewDTO.setAlias("");
		companyViewDTO.setCompanyType(CompanyType.GENERAL);
		companyViewDTO.setIndustry(Industry.GENERAL);
		companyViewDTO.setLegalName(partnerDTO.getLogin() + " PartnerCompany");
		companyViewDTO.setActivated(partnerDTO.getActivated());
		Company company = companyService.save(companyViewDTO);
		CompanyViewDTO result = new CompanyViewDTO(company);
		return result;
	}

	@Transactional
	private User saveUser(PartnerDTO partnerDTO, CompanyViewDTO companyViewDTO) {
		log.debug(" request to save Partner User : {}", partnerDTO.getLogin());
		Set<String> authorities = new HashSet<>(Arrays.asList("ROLE_PARTNER"));

		ManagedUserDTO managedUserDTO = new ManagedUserDTO();
		// common datas are setting to user
		managedUserDTO = createUserSetCommonValues(partnerDTO, companyViewDTO);

		managedUserDTO.setCompanyPid(companyViewDTO.getPid());
		managedUserDTO.setCompanyName(companyViewDTO.getLegalName());
		managedUserDTO.setAuthorities(authorities);
		managedUserDTO.setDashboardUIType(DashboardUIType.TW);

		User user = userService.createUser(managedUserDTO);
		return user;
	}

	private ManagedUserDTO createUserSetCommonValues(PartnerDTO partnerDTO, CompanyViewDTO companyViewDTO) {
		ManagedUserDTO managedUserDTO = new ManagedUserDTO();

		managedUserDTO.setEmail(partnerDTO.getEmail());
		managedUserDTO.setFirstName(partnerDTO.getFirstName());
		managedUserDTO.setLastName(partnerDTO.getLastName());
		managedUserDTO.setLogin(partnerDTO.getLogin());
		managedUserDTO.setMobile(partnerDTO.getMobile());
		managedUserDTO.setActivated(partnerDTO.getActivated());
		return managedUserDTO;
	}

	private CompanyViewDTO createCompanySetCommonValues(PartnerDTO partnerDTO) {
		CompanyViewDTO companyViewDTO = new CompanyViewDTO();

		companyViewDTO.setAddress1(partnerDTO.getAddress());
		companyViewDTO.setCountryCode(partnerDTO.getCountryCode());
		companyViewDTO.setCountryName(partnerDTO.getCountry());
		companyViewDTO.setDistrictCode(partnerDTO.getDistrictCode());
		companyViewDTO.setDistrictName(partnerDTO.getDistrict());
		companyViewDTO.setEmail(partnerDTO.getEmail());
		companyViewDTO.setLocation(partnerDTO.getLocation());
		companyViewDTO.setPincode(partnerDTO.getPincode());
		companyViewDTO.setStateCode(partnerDTO.getStateCode());
		companyViewDTO.setStateName(partnerDTO.getState());

		return companyViewDTO;
	}

}
