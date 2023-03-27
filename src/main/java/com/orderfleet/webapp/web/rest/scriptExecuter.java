package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.BillingJSCode;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.BillingJsCodeRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.SlabRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.service.CompanyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.inject.Inject;
import java.net.URISyntaxException;

@Controller
@RequestMapping("/web")
public class scriptExecuter {

    private final Logger log = LoggerFactory.getLogger(StaticJsCodeResource.class);

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private SlabRepository slabRepository;

    @Inject
    private UserRepository userRepository;

    @Inject
    private CompanyService companyService;

    @Inject
    private BillingJsCodeRepository billingJsCodeRepository;

    /**
     * GET /billing-js-code
     *
     * @param pageable
     *            the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of companies
     *         in body
     * @throws URISyntaxException
     *             if there is an error to generate the pagination HTTP headers
     */
    @Timed
    @Transactional(readOnly = true)
    @RequestMapping(value = "/billing-js-code", method = RequestMethod.GET)
    public String getAllCompanies(Pageable pageable, Model model) throws URISyntaxException {
        log.debug("Web request to get a page of Companies");
        model.addAttribute("companies", companyService.findAll());
        return "site_admin/billingJsCode";
    }


    @Timed
    @RequestMapping(value = "/billing-js-code/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCompany(@RequestParam String companyPid, @RequestParam String jsCode) throws URISyntaxException {
        log.debug("Web request to save jsCode : {}", companyPid);
        Company company = companyRepository.findOneByPid(companyPid).get();
        System.out.println(company.getId());
        BillingJSCode JSCode = billingJsCodeRepository.findByCompanyId(company.getId());
//        System.out.println(JSCode.getId());
        if (JSCode != null) {
            log.debug("updating js script ");
            JSCode.setJsCode(jsCode);
        } else {
            log.debug("saving js script ");
            JSCode = new BillingJSCode();
             JSCode.setCompany(company);
            JSCode.setJsCodeName("calculateSlabRate");
             JSCode.setJsCode(jsCode);

        }
        billingJsCodeRepository.save(JSCode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Timed
    @Transactional(readOnly = true)
    @RequestMapping(value = "/billing-js-code/change-document/{companyPid}", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> changeDocument(@PathVariable String companyPid) {
        log.debug("Web request to get Document js code : {}", companyPid);
        String jSCode = "";
        BillingJSCode billingJSCode = billingJsCodeRepository.findByCompanyId(companyRepository.findOneByPid(companyPid).get().getId());
        if (billingJSCode != null) {
            jSCode = billingJSCode.getJsCode();
        }
        return new ResponseEntity<>(jSCode, HttpStatus.OK);
    }


   
}
