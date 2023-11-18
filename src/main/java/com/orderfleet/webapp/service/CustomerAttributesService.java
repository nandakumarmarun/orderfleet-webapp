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
    List<CustomerAttributesDTO> findAttributesByCompanyPid(String companyPid);
    List<CustomerAttributesDTO> findAttributesByCompanyId();

    List<CustomerAttributesDTO> getAllCompanyAttributes();
}
