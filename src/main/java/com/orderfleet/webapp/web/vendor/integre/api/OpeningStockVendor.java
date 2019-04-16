package com.orderfleet.webapp.web.vendor.integre.api;

import java.util.List;

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
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.web.vendor.integre.dto.OpeningStockVendorDTO;
import com.orderfleet.webapp.web.vendor.integre.service.MasterDataUploadService;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class OpeningStockVendor {
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private MasterDataUploadService masterDataUploadService;

	@PostMapping("/opening-stock.json")
	@ResponseBody
	public ResponseEntity<String> uploadOpeningStock(@RequestBody List<OpeningStockVendorDTO> openingStockDtos,
			@RequestHeader("X-COMPANY") String companyId) {
		
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		if(company != null){
			masterDataUploadService.saveOrUpdateOpeningStock(openingStockDtos, company);
			return new ResponseEntity<String>("Success",HttpStatus.OK);	
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
