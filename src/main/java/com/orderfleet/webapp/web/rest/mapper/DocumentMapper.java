package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;

/**
 * Mapper for the entity Document and its DTO DocumentDTO.
 * 
 * @author Muhammed Riyas T
 * @since June 21, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public interface DocumentMapper {

	@Mapping(target = "sourceStockLocationPid", ignore = true)
	@Mapping(target = "sourceStockLocationName", ignore = true)
	@Mapping(target = "destinationStockLocationPid", ignore = true)
	@Mapping(target = "destinationStockLocationName", ignore = true)
	DocumentDTO documentToDocumentDTO(Document document);

	List<DocumentDTO> documentsToDocumentDTOs(List<Document> documents);

	@Mapping(target = "company", ignore = true)
	@Mapping(target = "id", ignore = true)
	Document documentDTOToDocument(DocumentDTO documentDTO);

	List<Document> documentDTOsToDocuments(List<DocumentDTO> documentDTOs);

}
