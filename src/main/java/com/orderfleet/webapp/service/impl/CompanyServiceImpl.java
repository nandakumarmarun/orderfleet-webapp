package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanySetting;
import com.orderfleet.webapp.domain.enums.CompanyType;
import com.orderfleet.webapp.domain.enums.Industry;
import com.orderfleet.webapp.domain.model.DashboardConfiguration;
import com.orderfleet.webapp.domain.model.SalesConfiguration;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompanySettingRepository;
import com.orderfleet.webapp.repository.CountryRepository;
import com.orderfleet.webapp.repository.DistrictRepository;
import com.orderfleet.webapp.repository.StateRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.RegistrationDto;

/**
 * Service Implementation for managing Company.
 * 
 * @author sarath
 * @since July 27, 2016
 */
@Service
@Transactional
public class CompanyServiceImpl implements CompanyService {

	private final Logger log = LoggerFactory.getLogger(CompanyServiceImpl.class);

	@Inject
	private CountryRepository countryRepository;

	@Inject
	private DistrictRepository districtRepository;

	@Inject
	private StateRepository stateRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompanySettingRepository companySettingRepository;

	@Override
	public void saveCompanyPeriod(LocalDate fromDate, LocalDate toDate) {
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		company.setPeriodStartDate(fromDate);
		company.setPeriodEndDate(toDate);
		companyRepository.save(company);
	}

	/**
	 * Save a company.
	 * 
	 * @param companyDTO the entity to save
	 * @return the persisted entity
	 */
	@Override
	public Company save(CompanyViewDTO companyDTO) {
		log.debug("Request to save Company : {}", companyDTO);
		companyDTO.setPid(CompanyService.PID_PREFIX + RandomUtil.generatePid()); // set
		// pid
		Company company = new Company();
		company.setPid(companyDTO.getPid());
		company.setLegalName(companyDTO.getLegalName());
		company.setAlias(companyDTO.getAlias());
		company.setAddress1(companyDTO.getAddress1());
		company.setAddress2(companyDTO.getAddress2());
		company.setCompanyType(companyDTO.getCompanyType());
		company.setCountry(countryRepository.findByCode(companyDTO.getCountryCode()));
		company.setDistrict(districtRepository.findByCode(companyDTO.getDistrictCode()));
		company.setState(stateRepository.findByCode(companyDTO.getStateCode()));
		company.setEmail(companyDTO.getEmail());
		company.setIndustry(companyDTO.getIndustry());
		company.setLocation(companyDTO.getLocation());
		company.setGstNo(companyDTO.getGstNo());
		company.setLogo(companyDTO.getLogo());
		company.setLogoContentType(companyDTO.getLogoContentType());
		company.setPincode(companyDTO.getPincode());
		company.setWebsite(companyDTO.getWebsite());
		company.setIsActivated(false);
		company.setOnPremise(companyDTO.isOnPremise());
		// company = companyRepository.save(company);
		// CompanyViewDTO result = new CompanyViewDTO(company);
		return companyRepository.save(company);
	}

	@Override
	public void saveDefaultSettings(Company company) {
		// dash-board configuration
		DashboardConfiguration dashboardConfiguration = new DashboardConfiguration();
		dashboardConfiguration.setDashboardView("company/dashboard-sales");
		dashboardConfiguration.setDelayTime(30);
		// sales configuration
		SalesConfiguration salesConfiguration = new SalesConfiguration();
		salesConfiguration.setAchievementSummaryTableEnabled(false);

		CompanySetting companySetting = new CompanySetting();
		companySetting.setCompany(company);
		companySetting.setDashboardConfiguration(dashboardConfiguration);
		companySetting.setSalesConfiguration(salesConfiguration);
		companySettingRepository.save(companySetting);

	}

	/**
	 * Update a company.
	 *
	 * @param companyDTO the entity to update
	 * @return the persisted entity
	 */
	@Override
	public CompanyViewDTO update(CompanyViewDTO companyDTO) {
		log.debug("Request to Update Company : {}", companyDTO);

		return companyRepository.findOneByPid(companyDTO.getPid()).map(company -> {
			company.setAlias(companyDTO.getAlias());
			company.setAddress1(companyDTO.getAddress1());
			company.setAddress2(companyDTO.getAddress2());
			company.setCompanyType(companyDTO.getCompanyType());
			company.setEmail(companyDTO.getEmail());
			company.setIndustry(companyDTO.getIndustry());
			company.setLocation(companyDTO.getLocation());
			company.setGstNo(companyDTO.getGstNo());
			company.setLogo(companyDTO.getLogo());
			company.setPincode(companyDTO.getPincode());
			company.setWebsite(companyDTO.getWebsite());
			company.setOnPremise(companyDTO.isOnPremise());
			company = companyRepository.save(company);
			CompanyViewDTO result = new CompanyViewDTO(company);
			return result;
		}).orElse(null);
	}

	/**
	 * Get all the companys.
	 *
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public List<CompanyViewDTO> findAll() {
		log.debug("Request to get all Companys");
		List<Company> companyList = companyRepository.findAll();
		List<CompanyViewDTO> result = companyList.stream().map(CompanyViewDTO::new).collect(Collectors.toList());
		return result;
	}

	/**
	 * Get all the companys.
	 *
	 * @param pageable the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<Company> findAll(Pageable pageable) {
		log.debug("Request to get all Companys");
		Page<Company> companys = companyRepository.findAll(pageable);
		companys = new PageImpl<Company>(companys.getContent(), pageable, companys.getTotalElements());
		return companys;
	}

	/**
	 * Get one company by id.
	 *
	 * @param id the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public CompanyViewDTO findOne(Long id) {
		log.debug("Request to get Company : {}", id);
		Company company = companyRepository.findOne(id);
		CompanyViewDTO companyDTO = new CompanyViewDTO(company);
		return companyDTO;
	}

	/**
	 * Get one company by pid.
	 *
	 * @param pid the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<CompanyViewDTO> findOneByPid(String pid) {
		log.debug("Request to get Company by pid : {}", pid);
		return companyRepository.findOneByPid(pid).map(company -> {
			CompanyViewDTO companyDTO = new CompanyViewDTO(company);
			return companyDTO;
		});
	}

	/**
	 * Get one company by name.
	 *
	 * @param name the name of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<CompanyViewDTO> findByName(String name) {
		log.debug("Request to get Company by name : {}", name);
		return companyRepository.findByLegalName(name).map(company -> {
			CompanyViewDTO companyDTO = new CompanyViewDTO(company);
			return companyDTO;
		});
	}

	/**
	 * Delete the company by id.
	 *
	 * @param id the id of the entity
	 */
	public void delete(String pid) {
		log.debug("Request to delete Company : {}", pid);
		companyRepository.findOneByPid(pid).ifPresent(company -> {
			companyRepository.delete(company.getId());
		});
	}

	@Override
	public CompanyViewDTO changeStatusCompany(CompanyViewDTO companyDTO) {
		return companyRepository.findOneByPid(companyDTO.getPid()).map(company -> {
			company.setIsActivated(companyDTO.isActivated());
			company = companyRepository.save(company);
			CompanyViewDTO result = new CompanyViewDTO(company);
			return result;
		}).orElse(null);
	}

	@Override
	public List<CompanyViewDTO> findAllCompaniesByActivatedTrue() {
		List<Company> companyList = companyRepository.findAllCompaniesByActivatedTrue();
		List<CompanyViewDTO> result = companyList.stream().map(CompanyViewDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public CompanyViewDTO findtCurrentCompanyLogo() {
		byte[] logo = companyRepository.findtCurrentUserEmployeeProfileImage();
		CompanyViewDTO companyViewDTO = new CompanyViewDTO();
		companyViewDTO.setLogo(logo);
		return companyViewDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Company> findAllCompany() {
		log.debug("Request to get all Companys");
		List<Company> companyList = companyRepository.findAll();
		return companyList;
	}

	@Override
	public List<CompanyViewDTO> findAllCompanySortedByName() {
		log.debug("Request to get all Companys");
		List<Company> companyList = companyRepository.findAllCompanySortedByName();
		List<CompanyViewDTO> result = companyList.stream().map(CompanyViewDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Optional<CompanyViewDTO> findByAlias(String alias) {
		return companyRepository.findByAlias(alias).map(company -> {
			CompanyViewDTO companyDTO = new CompanyViewDTO(company);
			return companyDTO;
		});
	}
	
	@Override
	public Optional<CompanyViewDTO> findByEmail(String email) {
		return companyRepository.findByEmail(email).map(company -> {
			CompanyViewDTO companyDTO = new CompanyViewDTO(company);
			return companyDTO;
		});
	}

	@Override
	public List<CompanyViewDTO> findAllCompanyByIndustrySortedByName(Industry industry) {
		List<Company> companyList = companyRepository.findAllCompanyByIndustrySortedByName(industry);
		List<CompanyViewDTO> result = companyList.stream().map(CompanyViewDTO::new).collect(Collectors.toList());
		return result;
	}

	@Override
	public Company createCompany(RegistrationDto registrationDto) {
		// save company
		Company company = new Company();
		company.setPid(CompanyService.PID_PREFIX + RandomUtil.generatePid());
		company.setLegalName(registrationDto.getLegalName());
		company.setAlias(registrationDto.getAlias());
		company.setCompanyType(CompanyType.GENERAL);
		company.setCountry(countryRepository.findByCode("IN"));
		company.setDistrict(districtRepository.findByCode("ER"));
		company.setState(stateRepository.findByCode("KL"));
		company.setIndustry(Industry.GENERAL);
		company.setEmail(registrationDto.getEmail());
		// Change Later
		company.setLocation("No Location");
		company.setIsActivated(true);
		company = companyRepository.save(company);
		log.debug("Automatic company created successfully with company name : {}", company.getLegalName());
		return company;
	}

}