package com.orderfleet.webapp.web.vendor.CochinDistributorsexcel.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.vendor.excel.dto.EmployeeProfileExcelDTO;
import com.orderfleet.webapp.web.vendor.excel.service.AccountProfileUploadService;
import com.orderfleet.webapp.web.vendor.excel.service.EmployeeProfileUploadService;
import com.orderfleet.webapp.web.vendor.integre.dto.EmployeeProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.AssignUploadDataService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/excelCD/v1")
public class EmployeeProfileExcelCochinDistributors {

	private final Logger log = LoggerFactory.getLogger(EmployeeProfileExcelCochinDistributors.class);
	
	@Inject
	private SyncOperationRepository syncOperationRepository;
	
	@Inject
	private EmployeeProfileUploadService employeeProfileUploadService;
	
	@RequestMapping(value = "/employee-profiles.json", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> bulkSaveEmployeeProfiles(
			@Valid @RequestBody List<EmployeeProfileExcelDTO> employeeProfileDTOs) {
		log.debug("REST request to save EmployeeProfiles : {}", employeeProfileDTOs.size());
		return syncOperationRepository.findOneByCompanyIdAndOperationType(SecurityUtils.getCurrentUsersCompanyId(),
				SyncOperationType.ACCOUNT_PROFILE).map(so -> {
					// update sync status
					so.setCompleted(false);
					so.setLastSyncStartedDate(LocalDateTime.now());
					syncOperationRepository.save(so);
					// save/update
					employeeProfileUploadService.saveUpdateEmployeeProfiles(employeeProfileDTOs, so);
					return new ResponseEntity<>("Uploaded", HttpStatus.OK);
				}).orElse(new ResponseEntity<>("Account-Profile sync operation not registered for this company",
						HttpStatus.BAD_REQUEST));
	}
	
}
