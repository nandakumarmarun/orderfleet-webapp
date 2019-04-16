package com.orderfleet.webapp.web.rest.mapper;

import java.util.List;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.orderfleet.webapp.domain.Knowledgebase;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;

/**
 * Mapper for the entity Knowledgebase and its DTO KnowledgebaseDTO.
 * 
 * @author Muhammed Riyas T
 * @since August 08, 2016
 */
@Mapper(componentModel = "spring", uses = {})
public abstract class KnowledgebaseMapper {

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Mapping(source = "productGroup.pid", target = "productGroupPid")
	@Mapping(source = "productGroup.name", target = "productGroupName")
	public abstract KnowledgebaseDTO knowledgebaseToKnowledgebaseDTO(Knowledgebase knowledgebase);

	public abstract List<KnowledgebaseDTO> knowledgebasesToKnowledgebaseDTOs(List<Knowledgebase> knowledgebases);

	@Mapping(target = "company", ignore = true)
	@Mapping(source = "productGroupPid", target = "productGroup")
	@Mapping(target = "id", ignore = true)
	public abstract Knowledgebase knowledgebaseDTOToKnowledgebase(KnowledgebaseDTO knowledgebaseDTO);

	public abstract List<Knowledgebase> knowledgebaseDTOsToKnowledgebases(List<KnowledgebaseDTO> knowledgebaseDTOs);

	public ProductGroup productGroupFromPid(String pid) {
		if (pid == null || pid.trim().length() == 0) {
			return null;
		}
		return productGroupRepository.findOneByPid(pid).map(group -> group).orElse(null);
	}
}
