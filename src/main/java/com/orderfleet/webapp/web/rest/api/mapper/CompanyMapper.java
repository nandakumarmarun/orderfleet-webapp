package com.orderfleet.webapp.web.rest.api.mapper;

import org.mapstruct.Mapper;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.web.rest.api.dto.CompanyDTO;

/**
 * Mapper for the entity Company and its DTO CompanyDTO.
 * 
 * @author Shaheer
 * @since May 18, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface CompanyMapper {
	
    CompanyDTO companyToCompanyDTO(Company company);
    
}
