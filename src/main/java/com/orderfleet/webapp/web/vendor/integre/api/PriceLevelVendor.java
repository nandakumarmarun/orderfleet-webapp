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
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.vendor.integre.dto.PriceLevelVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.AssignUploadDataService;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class PriceLevelVendor {

	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private MasterDataUploadService masterDataUploadService;
	
	@Inject
	private AssignUploadDataService assignUploadDataService;
	
	@Inject
	private PriceLevelRepository  priceLevelRepository;
	
	
	@PostMapping("/price-levels.json")
	@ResponseBody
	public ResponseEntity<String> uploadPriceLevels(@RequestBody List<PriceLevelVendorDTO> priceLevelDTOs,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			List<PriceLevel> existingPriceLevels = priceLevelRepository
					.findByCompanyId(company.getId());
			masterDataUploadService.saveOrUpdatePriceLevel(priceLevelDTOs, company);
			for(PriceLevel priceLevel : existingPriceLevels) {
				priceLevelDTOs.removeIf(pLevel -> pLevel.getCode().equals(priceLevel.getAlias()));
			}
			List<String> newPriceLevels = priceLevelDTOs.stream()
					.map(pLevel -> pLevel.getCode()).collect(Collectors.toList());
			assignUploadDataService.userPriceLevelAssociation(newPriceLevels, company);
			assignUploadDataService.documentPriceLevelAssociation(company);
			return new ResponseEntity<String>("Success",HttpStatus.OK);	
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
