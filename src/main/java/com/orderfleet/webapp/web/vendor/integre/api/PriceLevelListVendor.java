package com.orderfleet.webapp.web.vendor.integre.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.orderfleet.webapp.domain.PriceLevelList;
import com.orderfleet.webapp.domain.ProductProfile;
import com.orderfleet.webapp.domain.SnrichPartnerCompany;
import com.orderfleet.webapp.repository.PriceLevelListRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductProfileRepository;
import com.orderfleet.webapp.repository.SnrichPartnerCompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.vendor.integre.dto.PriceLevelListVendorDTO;

@RestController
@RequestMapping(value = "/api/orderpro/integra/v1")
@Secured({ AuthoritiesConstants.PARTNER })
public class PriceLevelListVendor {
	
	@Inject
	private SnrichPartnerCompanyRepository snrichPartnerCompanyRepository;
	
	@Inject
	private PriceLevelRepository  priceLevelRepository;
	
	@Inject
	private PriceLevelListRepository priceLevelListRepository;
	
	@Inject
	private ProductProfileRepository productProfileRepository;
	

	@PostMapping("/price-level-list.json")
	@ResponseBody
	public ResponseEntity<String> uploadPriceLevelList(@RequestBody List<PriceLevelListVendorDTO> priceLevelListDTOs,
			@RequestHeader("X-COMPANY") String companyId) {
		SnrichPartnerCompany snrichPartnerCompany = snrichPartnerCompanyRepository.findByDbCompany(companyId);
		Company company = snrichPartnerCompany.getCompany();
		
		if(company != null){
			List<PriceLevel> priceLevels = priceLevelRepository.findByCompanyId(company.getId());
			List<ProductProfile> productProfiles = productProfileRepository.findAllByCompanyId(company.getId());
			List<PriceLevelList> priceLevelLists = new ArrayList<>();
			
			for(PriceLevelListVendorDTO priceLevelListVendorDTO : priceLevelListDTOs){
				PriceLevelList priceLevelList = new PriceLevelList();
				
				Optional<ProductProfile> productProfile = productProfiles.stream()
						.filter(pp -> pp.getAlias()!=null ? pp.getAlias().equals(priceLevelListVendorDTO.getProductProfileCode()):false).findAny();
				Optional<PriceLevel> priceLevel = priceLevels.stream()
						.filter(pl -> pl.getAlias()!=null ?pl.getAlias().equals(priceLevelListVendorDTO.getPriceLevelCode()):false).findAny();
				
				if(productProfile.isPresent() && priceLevel.isPresent()){
					priceLevelList.setProductProfile(productProfile.get());
					priceLevelList.setPriceLevel(priceLevel.get());
					priceLevelList.setPrice(priceLevelListVendorDTO.getPrice());
					priceLevelList.setCompany(company);
					priceLevelList.setPid(PriceLevelListService.PID_PREFIX + RandomUtil.generatePid());
					priceLevelLists.add(priceLevelList);
				}
			}
			if(priceLevelLists.size() != 0){
				priceLevelListRepository.deleteByCompanyId(company.getId());
				priceLevelListRepository.save(priceLevelLists);
			}
			return new ResponseEntity<String>("Success",HttpStatus.OK);	
		}else{
			return new ResponseEntity<String>("Failed",HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
