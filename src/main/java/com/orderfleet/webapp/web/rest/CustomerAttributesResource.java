package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;

import com.orderfleet.webapp.domain.Attributes;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyAttributes;
import com.orderfleet.webapp.repository.AttributesRepository;
import com.orderfleet.webapp.repository.CompanyAttributesRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.AuthoritiesConstants;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.CustomerAttributesService;
import com.orderfleet.webapp.web.rest.api.dto.CompanyDTO;
import com.orderfleet.webapp.web.rest.dto.CustomerAttributesDTO;
import com.orderfleet.webapp.web.rest.dto.DepartmentDTO;
import com.orderfleet.webapp.web.rest.dto.TransferAccountDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/web")
public class CustomerAttributesResource {
    private final Logger log = LoggerFactory.getLogger(CustomerAttributesResource.class);


    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    CustomerAttributesService customerAttributesService;
    @Autowired
    CompanyAttributesRepository customerAttributesRepository;
    @Autowired
    CompanyService companyService;
    @Autowired
    AttributesRepository attributesRepository;

    @GetMapping("/customer-attributes")
    @Timed
    @Secured(AuthoritiesConstants.SITE_ADMIN)
    public ModelAndView getAllCustomerAttributes(Model model, Pageable pageable) {
        log.debug("Web request to get a page of Customer Attributes");

        List<Attributes> customerAttributes = attributesRepository.findAll();
        List<Company> companies = companyRepository.findAllCompaniesByActivatedTrue();
        List<CompanyAttributes> companyAttributes = customerAttributesRepository.findAll();
        List<CustomerAttributesDTO> customerAttributesDTOs = new ArrayList<>();

        for (Attributes attribute : customerAttributes) {
            CustomerAttributesDTO customerAttributesDTO = new CustomerAttributesDTO();
            customerAttributesDTO.setAttributePid(attribute.getPid());
            customerAttributesDTO.setQuestion(attribute.getQuestions());
            customerAttributesDTO.setType(attribute.getType());

            // Find the CompanyAttributes object corresponding to the current attribute
            CompanyAttributes relevantCompanyAttribute = null;
            for (CompanyAttributes companyAttribute : companyAttributes) {
                if (companyAttribute.getAttributes().equals(attribute)) {
                    relevantCompanyAttribute = companyAttribute;
                    break;
                }
            }

            // Set sortOrder if the relevant CompanyAttributes object is found
            if (relevantCompanyAttribute != null) {
                customerAttributesDTO.setSortOrder(relevantCompanyAttribute.getSortOrder());
            }
            // Set other fields accordingly
            customerAttributesDTOs.add(customerAttributesDTO);
        }

        model.addAttribute("companies", companies);
        model.addAttribute("customerAttributes", customerAttributesDTOs);

        return new ModelAndView("site_admin/customerAttributes");
    }



    @RequestMapping(value = "/customer-attributes/create-attribute", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    public ResponseEntity<CustomerAttributesDTO> createCustomerAttribute(@Valid @RequestBody CustomerAttributesDTO customerAttributesDTO) throws URISyntaxException {


        log.debug("Web request to save Customer Attributes: {}", customerAttributesDTO.getQuestion());
        log.debug("Web request to save Customer Attributes: {}", customerAttributesDTO.getType());


        CustomerAttributesDTO result = customerAttributesService.save(customerAttributesDTO);


        return ResponseEntity.created(URI.create("/customer-attributes/create-attribute")).body(result);
    }

    @GetMapping("/customer-attributes/get-all")
    @Timed
    public ResponseEntity<List<CustomerAttributesDTO>> getAllAttributes() {
        log.debug("Web request for getAllAttributes");
        List<CustomerAttributesDTO> allAttributes = customerAttributesService.getAllAttributes();
        log.debug("In controller: {}", allAttributes);

        if (allAttributes.isEmpty()) {
            return ResponseEntity.noContent().build(); // If no content is found, return 204 No Content status
        }

        return ResponseEntity.ok().body(allAttributes); // Return the list of all attributes
    }


    @RequestMapping(value = "/customer-attributes/update", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateCustomerAttributes(
            @RequestParam(name = "companyPid") String companyPid,
            @RequestParam(name = "selectedQuestions") List<String> selectedQuestions,
            @RequestParam(name = "sortOrder") List<Long> sortOrder
    ) {
        log.debug(companyPid);
        try {
            Company company = companyRepository.findOneByPid(companyPid).orElse(null);
            if (company == null) {
                return new ResponseEntity<>("Company not found.", HttpStatus.NOT_FOUND);
            }
            customerAttributesRepository.deleteByCompanyPid(companyPid);



            // Iterate through selected questions and save in the table
            for (int i = 0; i < selectedQuestions.size(); i++) {
                String attributesPid = selectedQuestions.get(i);
                Attributes attributes = attributesRepository.findOneByPid(attributesPid).orElse(null);

                if (attributes != null) {
                    // Create a new CompanyAttributes instance for the selected attributes
                    CompanyAttributes companyAttributes = new CompanyAttributes();
                    companyAttributes.setAttributes(attributes);
                    companyAttributes.setCompany(company);
                    companyAttributes.setSortOrder(sortOrder.get(i));

                    // Save the CompanyAttributes to the repository
                    customerAttributesRepository.save(companyAttributes);

                }
            }

            return new ResponseEntity<>("Completed.", HttpStatus.OK);
        } catch (Exception e) {
            // Handle any errors or exceptions
            return new ResponseEntity<>("Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @RequestMapping(value = "/customer-attributes/get-attributes-by-company", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<CustomerAttributesDTO>> customerAttributes(@RequestParam String companyPid) {
        log.debug("REST request to Company Attributes : {}", companyPid);
                List<CustomerAttributesDTO> customerAttributesDTOs = customerAttributesService
                        .findAttributesByCompanyPid(companyPid);
        return new ResponseEntity<>(customerAttributesDTOs, HttpStatus.OK);
    }
}
