package com.orderfleet.webapp.web.vendor.CochinDistributorsexcel.service;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.inject.Inject;

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
import com.orderfleet.webapp.domain.LocationHierarchy;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DataSourceType;
import com.orderfleet.webapp.domain.enums.ReceivablePayableType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.AccountTypeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DepartmentRepository;
import com.orderfleet.webapp.repository.DesignationRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.LocationHierarchyRepository;
import com.orderfleet.webapp.repository.LocationRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.ReceivablePayableRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.integration.BulkOperationRepositoryCustom;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.PriceLevelAccountProductGroupService;
import com.orderfleet.webapp.service.PriceLevelService;
import com.orderfleet.webapp.service.ReceivablePayableService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.EmployeeProfileExcelDTO;
import com.orderfleet.webapp.web.vendor.integre.dto.EmployeeProfileVendorDTO;

/**
 * Service for save/update account profile related data from third party
 * softwares like tally.
 * <p>
 * Use the @Async annotation to process asynchronously.
 * </p>
 */
@Service
public class EmployeeProfileUploadServiceCochinDistributors {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileUploadServiceCochinDistributors.class);

	private final SyncOperationRepository syncOperationRepository;

	@Inject
	private DepartmentRepository departmentRepository;

	@Inject
	private DesignationRepository designationRepository;

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	public EmployeeProfileUploadServiceCochinDistributors(SyncOperationRepository syncOperationRepository) {
		super();
		this.syncOperationRepository = syncOperationRepository;
	}

	public void saveUpdateEmployeeProfiles(List<EmployeeProfileExcelDTO> employeeProfileDTOs,
			SyncOperation syncOperation) {

		long start = System.nanoTime();
		final Company company = syncOperation.getCompany();
		Department deafultDepartment = departmentRepository.findFirstByCompanyId(company.getId());
		Designation deafultDesignation = designationRepository.findFirstByCompanyId(company.getId());
		List<EmployeeProfile> employeeProfileList = employeeProfileRepository.findAllByCompanyPid(company.getPid());
		for (EmployeeProfileExcelDTO employeeDTO : employeeProfileDTOs) {
			EmployeeProfile employeeProfile = new EmployeeProfile();
			Optional<EmployeeProfile> employee = employeeProfileList.stream()
					.filter(emp -> emp.getAlias() != null ? emp.getAlias().equals(employeeDTO.getCode()) : false)
					.findAny();
			if (employee.isPresent()) {
				employeeProfile = employee.get();
			} else {
				employeeProfile.setPid(EmployeeProfileService.PID_PREFIX + RandomUtil.generatePid());
				employeeProfile.setActivated(true);
				employeeProfile.setCompany(company);
				employeeProfile.setDepartment(deafultDepartment);
				employeeProfile.setDesignation(deafultDesignation);
				employeeProfile.setUser(null);
				employeeProfile.setAlias(employeeDTO.getCode());
			}
			employeeProfile.setCreatedDate(ZonedDateTime.now());
			employeeProfile.setName(employeeDTO.getName());
			employeeProfile.setAddress(employeeDTO.getAddress() == null || employeeDTO.getAddress() == "" ? "No Address"
					: employeeDTO.getAddress());
			employeeProfileList.add(employeeProfile);
		}
		employeeProfileRepository.save(employeeProfileList);

		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		// update sync table
		syncOperation.setCompleted(true);
		syncOperation.setLastSyncCompletedDate(LocalDateTime.now());
		syncOperation.setLastSyncTime(elapsedTime);
		syncOperationRepository.save(syncOperation);
		log.info("Sync completed in {} ms", elapsedTime);
	}
}
