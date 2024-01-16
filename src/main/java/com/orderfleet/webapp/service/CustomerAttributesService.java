package com.orderfleet.webapp.service;

import com.orderfleet.webapp.domain.Attributes;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.web.rest.dto.CustomerAttributesDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerAttributesService {
    String PID_PREFIX = "CA-";
    CustomerAttributesDTO save (CustomerAttributesDTO customerAttributesDTO);

    List<CustomerAttributesDTO> getAllAttributes();
    List<CustomerAttributesDTO> findAttributesByDocumentPid(String companyPid);
    List<CustomerAttributesDTO> findAttributesByCompanyIdAndDocumentPid();

    List<CustomerAttributesDTO> getAllCompanyAttributes();
   void updateAttributes(String documentPid,String companyPid, List<String> selectedQuestions, List<Long> sortOrder);
   void  updateAttributesForAllDocuments(String companyPid, List<String> selectedQuestions, List<Long> sortOrder);
    List<CustomerAttributesDTO> getAllAttributesByCompany(String companyPid);
}

