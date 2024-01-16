package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.AttributesRepository;
import com.orderfleet.webapp.repository.CompanyAttributesRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
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
    CompanyAttributesRepository companyAttributesRepository;

    @Autowired
    DocumentRepository documentRepository ;
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
        List<CompanyAttributes> companyAttributes = companyAttributesRepository.findAll();
        List<CustomerAttributesDTO> customerAttributesDTOs = new ArrayList<>();
        List<Document>documents=documentRepository.findAll();

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
        model.addAttribute("Documents", documents);

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
    @GetMapping("/customer-attributes/getDocumentByCompany")
    @Timed
    public ResponseEntity<List<Document>> getDocumentByCompany(@RequestParam String companyPid) {
        log.debug("Web request to get Documents by company: {}", companyPid);

        // Fetch locations based on the selected user
        List<Document> documents = documentRepository.findAllDocumentsByCompanyPid(companyPid);

        return new ResponseEntity<>(documents, HttpStatus.OK);
    }

    @RequestMapping(value = "/customer-attributes/update", method = RequestMethod.POST)
    @Transactional
    public ResponseEntity<String> updateCustomerAttributes(
            @RequestParam(name = "documentPid") String documentPid,
            @RequestParam(name = "companyPid") String companyPid,
            @RequestParam(name = "selectedQuestions") List<String> selectedQuestions,
            @RequestParam(name = "sortOrder") List<Long> sortOrder
    ) {
        try {
            if (documentPid.equalsIgnoreCase("all")) {
                // Update all questions for all documents
                customerAttributesService.updateAttributesForAllDocuments(companyPid,selectedQuestions,sortOrder);
            } else {
                // Update questions for a specific document
                customerAttributesService.updateAttributes(documentPid, companyPid, selectedQuestions, sortOrder);
            }

            return new ResponseEntity<>("Completed.", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Company not found.", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



    @RequestMapping(value = "/customer-attributes/get-attributes-by-document", method = RequestMethod.GET)
    @Timed
    public ResponseEntity<List<CustomerAttributesDTO>> customerAttributes(@RequestParam String documentPid,@RequestParam String companyPid) {
        log.debug("REST request to Company Attributes : {}", documentPid);
        List<CustomerAttributesDTO> customerAttributesDTOs;

        if (documentPid.equalsIgnoreCase("all")) {
            customerAttributesDTOs = customerAttributesService.getAllAttributesByCompany(companyPid);
            log.debug("all",customerAttributesDTOs);
        } else {
            customerAttributesDTOs = customerAttributesService.findAttributesByDocumentPid(documentPid);
            log.debug("document",customerAttributesDTOs);
        }

        return new ResponseEntity<>(customerAttributesDTOs, HttpStatus.OK);
    }
}
