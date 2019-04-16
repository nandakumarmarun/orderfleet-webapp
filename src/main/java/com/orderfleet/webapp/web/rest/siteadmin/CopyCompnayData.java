package com.orderfleet.webapp.web.rest.siteadmin;

import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.custom.CopyCompanyRepositoryCustom;

@Controller
@RequestMapping("/web/siteadmin")
public class CopyCompnayData {

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private CopyCompanyRepositoryCustom copyCompanyRepositoryCustom;
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
	private String defaultSchema;
	
	@Timed
	@RequestMapping(value = "/copy-compnay-data", method = RequestMethod.GET)
	public String getCopyCompanyDataView(Model model) {
		model.addAttribute("currentSchema", defaultSchema);
		model.addAttribute("companies", companyRepository.findAllCompaniesByActivatedTrue());
		return "site_admin/copy-compnay-data";
	}
	
	@Timed
	@ResponseBody
	@RequestMapping(value = "/copy-compnay-data", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> copyCompanyMasterData(@Valid @RequestBody CCMasterDataDTO ccmdDto) {
		try {
			if(ccmdDto.getTblNames().length > 0) {
				for (String tblName : ccmdDto.getTblNames()) {
					copyCompanyRepositoryCustom.copyCompanyMasterData(defaultSchema, ccmdDto, tblName);
				}
			}
		} catch (PersistenceException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().body("Success");
	}

}
