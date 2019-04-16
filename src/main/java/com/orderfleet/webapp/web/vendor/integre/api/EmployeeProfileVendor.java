package com.orderfleet.webapp.web.vendor.integre.api;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.async.MailService;
import com.orderfleet.webapp.web.vendor.integre.dto.EmployeeProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.AssignUploadDataService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class EmployeeProfileVendor {
	
	@Inject
	private MasterDataUploadService masterDataUploadService;
	
	@Inject
	private AssignUploadDataService assignUploadDataService;
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private EmployeeProfileRepository employeeProfileRepository;
	
	@Inject
	private MailService mailService;
	
	private static final String EMAIL_SUBJECT = "SalesNrich - Employee & User Association!";
	

	@PostMapping("/employee.json")
	@ResponseBody
	public ResponseEntity<String> uploadEmployeeProfiles(@RequestBody List<EmployeeProfileVendorDTO> employeeProfileDTOs,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			List<EmployeeProfile> existingEmployeeProfiles = employeeProfileRepository
					.findAllByCompanyId(company.getId());
			masterDataUploadService.saveOrUpdateEmployeeProfile(employeeProfileDTOs,company);
			for(EmployeeProfile employeeProfile : existingEmployeeProfiles) {
				employeeProfileDTOs.removeIf(eProfile -> eProfile.getCode().equals(employeeProfile.getAlias()));
			}
			List<String> newEmployeeProfileCodes = employeeProfileDTOs.stream()
					.map(eProfile -> eProfile.getCode()).collect(Collectors.toList());
			assignUploadDataService.employeeProfileLocationAssociation(newEmployeeProfileCodes, company);
			assignUploadDataService.employeeUserAssociation(newEmployeeProfileCodes, company);
			
			//sending email to admin about employee/user association
			List<EmployeeProfile> employeeProfiles = employeeProfileRepository
					.findAllByCompanyId(company.getId());
			StringBuilder employeeUser = new StringBuilder();
			if(employeeProfiles.size() > 0){
				for(EmployeeProfile employee : employeeProfiles){
					if(employee.getUser() != null) {
						employeeUser.append("Employee : "+employee.getName()+"    User : "+employee.getUser().getFirstName()+"\n");
					}
				}
				mailService.sendEmailCompanyRegistered(company.getEmail(),EMAIL_SUBJECT,"Dear Sir/Madam,\n\n\tThese are the Users assigned to the Company Employees.\n\n "
						+employeeUser, 
						false, false, false);
			}
			
			return new ResponseEntity<String>("Success",HttpStatus.OK);	
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
