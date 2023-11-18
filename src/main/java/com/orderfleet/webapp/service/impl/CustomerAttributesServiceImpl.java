package com.orderfleet.webapp.service.impl;

import com.orderfleet.webapp.domain.Attributes;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.CompanyAttributes;
import com.orderfleet.webapp.repository.AttributesRepository;
import com.orderfleet.webapp.repository.CompanyAttributesRepository;
import com.orderfleet.webapp.repository.CompanyRepository;

import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.CompanyService;
import com.orderfleet.webapp.service.CustomerAttributesService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.CustomerAttributesResource;
import com.orderfleet.webapp.web.rest.api.dto.CompanyDTO;
import com.orderfleet.webapp.web.rest.dto.CompanyViewDTO;
import com.orderfleet.webapp.web.rest.dto.CustomerAttributesDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CustomerAttributesServiceImpl implements CustomerAttributesService {
    private final Logger log = LoggerFactory.getLogger(CustomerAttributesServiceImpl.class);

    @Inject
    CompanyAttributesRepository companyAttributesRepository;
    @Autowired
    CompanyService companyService;
    @Autowired
    CompanyRepository companyRepository;

    Attributes attributes;
    @Autowired
    AttributesRepository attributesRepository;
    @Override
    public CustomerAttributesDTO save(CustomerAttributesDTO customerAttributesDTO) {
        System.out.println("Enters the service implementation");
        Attributes attributes = convertToAttributesEntity(customerAttributesDTO);
        System.out.println(attributes);

        // Save the attributes directly
        Attributes savedAttributes = attributesRepository.save(attributes);
        System.out.println(savedAttributes);

        // Convert the saved attributes back to DTO and return
        return convertAttributesToDTO(savedAttributes);
    }

    @Override
    public List<CustomerAttributesDTO> findAttributesByCompanyId() {
        log.debug("Request to get all Attributes under a company");
        Long companyId= SecurityUtils.getCurrentUsersCompanyId();
        List<CompanyAttributes> companyAttributesList = companyAttributesRepository.findAttributesByCompanyId(companyId);
        log.debug("the result is ; {}",companyAttributesList);

        List<CustomerAttributesDTO> result = convertCompanyAttributesToCustomerAttributesDTO(companyAttributesList);
        log.debug("the result is ; {}",result);
        return result;
    }

    @Override
    public List<CustomerAttributesDTO> getAllCompanyAttributes() {

        List<CompanyAttributes> companyAttributes = companyAttributesRepository.findAllByCompanyId();
        List<CustomerAttributesDTO> customerAttributesDTOS = new ArrayList<>();
        for(CompanyAttributes compAttr :companyAttributes)
        {
            CustomerAttributesDTO customerAttributesDTO= new CustomerAttributesDTO();
            customerAttributesDTO.setAttributePid(compAttr.getAttributes().getPid());
            customerAttributesDTO.setQuestion(compAttr.getAttributes().getQuestions());
            customerAttributesDTO.setSortOrder(compAttr.getSortOrder());

            customerAttributesDTOS.add(customerAttributesDTO);
        }
        return customerAttributesDTOS;
    }

    @Override
    public List<CustomerAttributesDTO> findAttributesByCompanyPid(String companyPid) {
        log.debug("Request to get all Attributes under a company");
        List<CompanyAttributes> companyAttributesList = companyAttributesRepository.findAttributesByCompanyPid(companyPid);
        log.debug("the result is ; {}",companyAttributesList);

        List<CustomerAttributesDTO> result = convertCompanyAttributesToCustomerAttributesDTO(companyAttributesList);
        log.debug("the result is ; {}",result);
        return result;
    }
    @Override
    public List<CustomerAttributesDTO> getAllAttributes() {
        List<Attributes> attributesList = attributesRepository.findAll();
        // Fetch all attributes from the repository

        // Map the list of entities to a list of DTOs
        return attributesList.stream()
                .map(this::convertAttributesToDTO)
                .collect(Collectors.toList());
    }

    private CustomerAttributesDTO convertToDTO(CompanyAttributes companyAttributes) {
        CustomerAttributesDTO customerAttributesDTO = new CustomerAttributesDTO();
        customerAttributesDTO.setAttributePid(attributes.getPid());
        customerAttributesDTO.setQuestion(attributes.getQuestions());
        customerAttributesDTO.setType(attributes.getType());
        // Set other attributes as needed
        return customerAttributesDTO;
    }
    private List<CustomerAttributesDTO> convertAttributesToCustomerAttributesDTO(List<Attributes> attributesList) {
        CustomerAttributesDTO customerAttributesDTOs = new CustomerAttributesDTO();
        System.out.println(customerAttributesDTOs );
        List<CustomerAttributesDTO> result = new ArrayList<>();
        System.out.println(result);

        for (Attributes attribute : attributesList) {

            // Assuming the CustomerAttributesDTO has setters that match the fields in Attributes
            customerAttributesDTOs.setAttributePid(attribute.getPid());
            customerAttributesDTOs.setQuestion(attribute.getQuestions());
            customerAttributesDTOs.setType(attribute.getType());
            customerAttributesDTOs.setSortOrder(customerAttributesDTOs.getSortOrder());
            // Set other fields accordingly
            System.out.println(customerAttributesDTOs);
            result.add(customerAttributesDTOs);
        }
        return result;
    }
    private Attributes convertToAttributesEntity(CustomerAttributesDTO customerAttributesDTO) {

        Attributes attribute=new Attributes();

        attribute.setPid(CustomerAttributesService.PID_PREFIX + RandomUtil.generatePid());
        attribute.setQuestions(customerAttributesDTO.getQuestion());
        attribute.setType(customerAttributesDTO.getType());

        System.out.println(attribute);

        return attribute;
    }

    private CustomerAttributesDTO convertAttributesToDTO(Attributes attributes) {
        CustomerAttributesDTO customerAttributesDTO = new CustomerAttributesDTO();
        System.out.println();
        customerAttributesDTO.setAttributePid(attributes.getPid());
        customerAttributesDTO.setQuestion(attributes.getQuestions());
        customerAttributesDTO.setType(attributes.getType());
        System.out.println(customerAttributesDTO);
        // Any other attribute mappings

        return customerAttributesDTO;
    }


    public List<CustomerAttributesDTO> convertCompanyAttributesToCustomerAttributesDTO(List<CompanyAttributes> companyAttributesList) {
        List<CustomerAttributesDTO> customerAttributesDTOs = new ArrayList<>();
        for (CompanyAttributes companyAttributes : companyAttributesList) {
            if (companyAttributes != null && companyAttributes.getAttributes() != null) {
                CustomerAttributesDTO customerAttributesDTO = new CustomerAttributesDTO();
                customerAttributesDTO.setAttributePid(companyAttributes.getAttributes().getPid());
                customerAttributesDTO.setQuestion(companyAttributes.getAttributes().getQuestions());
                customerAttributesDTO.setType(companyAttributes.getAttributes().getType());


                // Additional conditions or checks
                if (companyAttributes.getSortOrder() != null) {
                    customerAttributesDTO.setSortOrder(companyAttributes.getSortOrder());
                } else {
                    customerAttributesDTO.setSortOrder(0L); // Default value if sortOrder is null
                }

                // Add other necessary fields to the DTO object
                customerAttributesDTOs.add(customerAttributesDTO);
            }
        }
        return customerAttributesDTOs;
    }

}
