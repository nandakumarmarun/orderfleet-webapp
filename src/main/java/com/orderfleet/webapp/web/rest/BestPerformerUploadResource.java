package com.orderfleet.webapp.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.BestPerformer;
import com.orderfleet.webapp.domain.PartnerCompany;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.BestPerformerRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.BestPerformerService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.BestPerformerUploadDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

@Controller
@RequestMapping("/web")
public class BestPerformerUploadResource {

	private final Logger log = LoggerFactory.getLogger(BestPerformerUploadResource.class);

	private CompanyService companyService;	
	@Inject
	private BestPerformerRepository bestPerformerRepository;
	@Inject
	private CompanyRepository companyRepository;
	
	private BestPerformerService bestPerformerService;

	public BestPerformerUploadResource(CompanyService companyService, BestPerformerService bestPerformerService) {
		super();
		this.companyService = companyService;
		this.bestPerformerService = bestPerformerService;
	}



	@RequestMapping(value = "/best-performer-upload", method = RequestMethod.GET)
	@Timed
	@Transactional(readOnly = true)
	public String getCompanyDetail(Model model) throws URISyntaxException {
		BestPerformer bp = bestPerformerRepository.findOneByCompanyId();
		model.addAttribute("bestperformer", bp);
		
		return "company/best-performer-upload";
	}
	
	@RequestMapping(value = "/best-performer-upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Timed
	public ResponseEntity<CompanyViewDTO> createCompany(@Valid @RequestBody BestPerformerUploadDTO companyDTO)
			throws URISyntaxException {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();		
		BestPerformer cd = bestPerformerRepository.findOneByCompanyId();
		if (cd ==null )
		{
			cd = new BestPerformer();
			cd.setPid(BestPerformerService.PID_PREFIX + RandomUtil.generatePid());
		}
		Optional<Company> c = companyRepository.findById(companyId);
		cd.setCompany(c.get());
		cd.setLogo(companyDTO.getLogo());
		cd.setLogoContentType(companyDTO.getLogoContentType());
		bestPerformerRepository.save(cd);
		return ResponseEntity.ok().build();
	}
	
	@RequestMapping(value = "/best-performer-upload/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<BestPerformerUploadDTO> getCompanyDetail(@PathVariable String pid) {
		log.debug("Web request to get Company Detail by pid : {}", pid);
		return bestPerformerService.findOneByPid(pid).map(companyDTO -> new ResponseEntity<>(companyDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@RequestMapping(value = "/best-performer-upload", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<BestPerformerUploadDTO> updateCompanyDetail(@Valid @RequestBody BestPerformerUploadDTO companyDTO)
			throws URISyntaxException {
		log.debug("REST request to update Company : {}", companyDTO);
		if (companyDTO.getPid() == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "CompanyDetail must have an ID"))
					.body(null);
		}
		BestPerformerUploadDTO result = bestPerformerService.update(companyDTO);
		if (result == null) {
			return ResponseEntity.badRequest()
					.headers(HeaderUtil.createFailureAlert("company", "idNotexists", "Invalid Company ID")).body(null);
		}
		return ResponseEntity.ok()
				.headers(HeaderUtil.createEntityUpdateAlert("company", companyDTO.getPid().toString())).body(result);
	}
}
