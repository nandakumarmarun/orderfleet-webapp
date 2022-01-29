package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.Knowledgebase;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.web.rest.dto.KnowledgebaseDTO;
import com.orderfleet.webapp.web.rest.mapper.KnowledgebaseMapper;
@Component
public class KnowledgebaseMapperImpl extends KnowledgebaseMapper{


    @Override
    public KnowledgebaseDTO knowledgebaseToKnowledgebaseDTO(Knowledgebase knowledgebase) {
        if ( knowledgebase == null ) {
            return null;
        }

        KnowledgebaseDTO knowledgebaseDTO = new KnowledgebaseDTO();

        knowledgebaseDTO.setProductGroupPid( knowledgebaseProductGroupPid( knowledgebase ) );
        knowledgebaseDTO.setProductGroupName( knowledgebaseProductGroupName( knowledgebase ) );
        knowledgebaseDTO.setActivated( knowledgebase.getActivated() );
        knowledgebaseDTO.setPid( knowledgebase.getPid() );
        knowledgebaseDTO.setName( knowledgebaseName(knowledgebase) );
        knowledgebaseDTO.setAlias( knowledgebase.getAlias() );
        knowledgebaseDTO.setDescription( knowledgebase.getDescription() );
        knowledgebaseDTO.setLastModifiedDate( knowledgebase.getLastModifiedDate() );

        return knowledgebaseDTO;
    }

    @Override
    public List<KnowledgebaseDTO> knowledgebasesToKnowledgebaseDTOs(List<Knowledgebase> knowledgebases) {
        if ( knowledgebases == null ) {
            return null;
        }

        List<KnowledgebaseDTO> list = new ArrayList<KnowledgebaseDTO>();
        for ( Knowledgebase knowledgebase : knowledgebases ) {
            list.add( knowledgebaseToKnowledgebaseDTO( knowledgebase ) );
        }

        return list;
    }

    @Override
    public Knowledgebase knowledgebaseDTOToKnowledgebase(KnowledgebaseDTO knowledgebaseDTO) {
        if ( knowledgebaseDTO == null ) {
            return null;
        }

        Knowledgebase knowledgebase = new Knowledgebase();

        knowledgebase.setProductGroup( productGroupFromPid( knowledgebaseDTO.getProductGroupPid() ) );
        knowledgebase.setActivated( knowledgebaseDTO.getActivated() );
        knowledgebase.setPid( knowledgebaseDTO.getPid() );
        knowledgebase.setName( knowledgebaseDTO.getName() );
        knowledgebase.setAlias( knowledgebaseDTO.getAlias() );
        knowledgebase.setDescription( knowledgebaseDTO.getDescription() );

        return knowledgebase;
    }

    @Override
    public List<Knowledgebase> knowledgebaseDTOsToKnowledgebases(List<KnowledgebaseDTO> knowledgebaseDTOs) {
        if ( knowledgebaseDTOs == null ) {
            return null;
        }

        List<Knowledgebase> list = new ArrayList<Knowledgebase>();
        for ( KnowledgebaseDTO knowledgebaseDTO : knowledgebaseDTOs ) {
            list.add( knowledgebaseDTOToKnowledgebase( knowledgebaseDTO ) );
        }

        return list;
    }

    private String knowledgebaseProductGroupPid(Knowledgebase knowledgebase) {

        if ( knowledgebase == null ) {
            return null;
        }
        ProductGroup productGroup = knowledgebase.getProductGroup();
        if ( productGroup == null ) {
            return null;
        }
        String pid = productGroup.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String knowledgebaseProductGroupName(Knowledgebase knowledgebase) {

        if ( knowledgebase == null ) {
            return null;
        }
        ProductGroup productGroup = knowledgebase.getProductGroup();
        if ( productGroup == null ) {
            return null;
        }
        String name = productGroup.getName();
        if ( name == null ) {
            return null;
        }
        if(productGroup.getDescription()!=null && getCompanyCofig() && !productGroup.getDescription().equals("common")) {
            return productGroup.getDescription();
            }
        return name;
    }
    private String knowledgebaseName(Knowledgebase knowledgebase) {
        if(knowledgebase.getDescription()!=null && getCompanyCofig() && !knowledgebase.getDescription().equals("common")) {
        return knowledgebase.getDescription();
        }
       
    return knowledgebase.getName();
    }
}
