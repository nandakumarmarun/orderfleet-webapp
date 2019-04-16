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
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.vendor.integre.dto.ProductProfileVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.AssignUploadDataService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class ProductProfileVendor {

	@Inject
	private MasterDataUploadService masterDataUploadService;
	
	@Inject
	private AssignUploadDataService assignUploadDataService;
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	
	
	@PostMapping("/product-profile.json")
	@ResponseBody
	public ResponseEntity<String> uploadProductProfiles(@RequestBody List<ProductProfileVendorDTO> productProfileDtos,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			List<ProductProfile> existingProductProfiles = productProfileRepository.findAllByCompanyId(company.getId());
			masterDataUploadService.saveOrUpdateProductProfiles(productProfileDtos, company);
			for(ProductProfile productProfile : existingProductProfiles) {
				productProfileDtos.removeIf(pProfile -> pProfile.getCode().equals(productProfile.getAlias()));
			}
			List<String> newProductProfileAlias = productProfileDtos.stream()
					.map(pProfile -> pProfile.getCode()).collect(Collectors.toList());
			assignUploadDataService.productProductGroupAssociation(newProductProfileAlias,company);
			return new ResponseEntity<String>("Success",HttpStatus.OK);		
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
