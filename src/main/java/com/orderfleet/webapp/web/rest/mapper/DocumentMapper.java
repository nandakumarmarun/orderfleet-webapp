package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Mapper for the entity Document and its DTO DocumentDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Component
public abstract class DocumentMapper {
	
	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;
//
//	@Mapping(target = "sourceStockLocationPid", ignore = true)
//	@Mapping(target = "sourceStockLocationName", ignore = true)
//	@Mapping(target = "destinationStockLocationPid", ignore = true)
//	@Mapping(target = "destinationStockLocationName", ignore = true)
	public abstract DocumentDTO documentToDocumentDTO(Document document);

	public abstract List<DocumentDTO> documentsToDocumentDTOs(List<Document> documents);

//	@Mapping(target = "company", ignore = true)
//	@Mapping(target = "id", ignore = true)
	public abstract Document documentDTOToDocument(DocumentDTO documentDTO);

	public abstract List<Document> documentDTOsToDocuments(List<DocumentDTO> documentDTOs);

	public boolean getCompanyCofig(){
		Optional<CompanyConfiguration> optconfig = companyConfigurationRepository.findByCompanyIdAndName(SecurityUtils.getCurrentUsersCompanyId(), CompanyConfig.DESCRIPTION_TO_NAME);
		if(optconfig.isPresent()) {
		if(Boolean.valueOf(optconfig.get().getValue())) {
		return true;
		}
		}
		return false;
		}
}
