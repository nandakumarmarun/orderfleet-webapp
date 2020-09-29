package com.orderfleet.webapp.web.vendor.odoo.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.Department;
import com.orderfleet.webapp.domain.Designation;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.Location;
import com.orderfleet.webapp.domain.LocationAccountProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooAccountProfile;
import com.orderfleet.webapp.web.vendor.odoo.dto.OdooUser;
import com.orderfleet.webapp.web.vendor.odoo.dto.ResultOdooAccountProfile;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class UserOdooUploadService {

	private final Logger log = LoggerFactory.getLogger(UserOdooUploadService.class);

	private final BulkOperationRepositoryCustom bulkOperationRepositoryCustom;

	private final EmployeeProfileRepository employeeProfileRepository;

	private final UserRepository userRepository;

	private final DesignationRepository designationRepository;

	private final DepartmentRepository departmentRepository;

	private final CompanyRepository companyRepository;

	public UserOdooUploadService(BulkOperationRepositoryCustom bulkOperationRepositoryCustom,
			EmployeeProfileRepository employeeProfileRepository, UserRepository userRepository,
			CompanyRepository companyRepository, DesignationRepository designationRepository,
			DepartmentRepository departmentRepository) {
		super();
		this.bulkOperationRepositoryCustom = bulkOperationRepositoryCustom;
		this.employeeProfileRepository = employeeProfileRepository;
		this.userRepository = userRepository;
		this.companyRepository = companyRepository;
		this.designationRepository = designationRepository;
		this.departmentRepository = departmentRepository;
	}

	@Transactional
	public void saveUpdateUsers(final List<OdooUser> list) {

		log.info("Saving Employee Profiles...");
		long start = System.nanoTime();

		final User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		final Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		Company company = companyRepository.findOne(companyId);
		Set<EmployeeProfile> saveUpdateEmployeeProfiles = new HashSet<>();

		// find all exist account profiles
		List<String> empNames = list.stream().map(apDto -> apDto.getName().toUpperCase()).collect(Collectors.toList());
		List<EmployeeProfile> employeeProfiles = employeeProfileRepository.findByCompanyIdAndNameIgnoreCaseIn(companyId,
				empNames);
		List<Designation> designations = designationRepository.findAllByCompanyId();
		List<Department> departments = departmentRepository.findAllByCompanyId();

		for (OdooUser empDto : list) {
			// check exist by name, only one exist with a name
			Optional<EmployeeProfile> optionalEmp = employeeProfiles.stream()
					.filter(pc -> pc.getName().equalsIgnoreCase(empDto.getName())).findAny();
			EmployeeProfile employeeProfile;
			if (optionalEmp.isPresent()) {
				employeeProfile = optionalEmp.get();
				// if not update, skip this iteration. Not implemented now
				// if (!accountProfile.getThirdpartyUpdate()) { continue; }
			} else {
				employeeProfile = new EmployeeProfile();
				employeeProfile.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
				employeeProfile.setName(empDto.getName());
				employeeProfile.setCompany(company);
				employeeProfile.setActivated(true);
				employeeProfile.setDepartment(departments.get(0));
				employeeProfile.setDesignation(designations.get(0));
				employeeProfile.setAddress("No Address");

			}

			employeeProfile.setAlias(String.valueOf(empDto.getId()));

			Optional<EmployeeProfile> opEmp = saveUpdateEmployeeProfiles.stream()
					.filter(so -> so.getName().equalsIgnoreCase(empDto.getName())).findAny();
			if (opEmp.isPresent()) {
				continue;
			}

			saveUpdateEmployeeProfiles.add(employeeProfile);
		}

		employeeProfileRepository.save(saveUpdateEmployeeProfiles);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table

		log.info("Sync completed in {} ms", elapsedTime);
	}

}
