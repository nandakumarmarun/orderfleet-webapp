package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.enums.Industry;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;

public interface CompanyService {

	String PID_PREFIX = "COMPANY-";

	void saveCompanyPeriod(LocalDate fromDate, LocalDate toDate);

	Company save(CompanyViewDTO companyDTO);

	CompanyViewDTO update(CompanyViewDTO companyDTO);

	List<CompanyViewDTO> findAll();

	Page<Company> findAll(Pageable pageable);

	CompanyViewDTO findOne(Long id);

	Optional<CompanyViewDTO> findOneByPid(String pid);

	Optional<CompanyViewDTO> findByName(String name);

	Optional<CompanyViewDTO> findByAlias(String alias);
	
	Optional<CompanyViewDTO> findByEmail(String email);

	CompanyViewDTO changeStatusCompany(CompanyViewDTO companyDTO);

	List<CompanyViewDTO> findAllCompaniesByActivatedTrue();

	CompanyViewDTO findtCurrentCompanyLogo();

	void saveDefaultSettings(Company company);

	void delete(String pid);

	List<Company> findAllCompany();

	List<CompanyViewDTO> findAllCompanySortedByName();

	List<CompanyViewDTO> findAllCompanyByIndustrySortedByName(Industry industry);
	
	Company createCompany(RegistrationDto registrationDto);
}
