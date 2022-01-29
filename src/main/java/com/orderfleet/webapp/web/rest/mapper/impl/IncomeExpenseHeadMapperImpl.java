package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.IncomeExpenseHead;
import com.orderfleet.webapp.web.rest.dto.IncomeExpenseHeadDTO;
import com.orderfleet.webapp.web.rest.mapper.IncomeExpenseHeadMapper;
@Component
public class IncomeExpenseHeadMapperImpl extends IncomeExpenseHeadMapper {
	
  @Override
    public IncomeExpenseHeadDTO incomeExpenseHeadToIncomeExpenseHeadDTO(IncomeExpenseHead incomeExpenseHead) {
        if ( incomeExpenseHead == null ) {
            return null;
        }

        IncomeExpenseHeadDTO incomeExpenseHeadDTO = new IncomeExpenseHeadDTO();

        incomeExpenseHeadDTO.setActivated( incomeExpenseHead.getActivated() );
        incomeExpenseHeadDTO.setPid( incomeExpenseHead.getPid() );
        incomeExpenseHeadDTO.setName( incomeExpenseHeadName(incomeExpenseHead) );
        incomeExpenseHeadDTO.setAlias( incomeExpenseHead.getAlias() );
        incomeExpenseHeadDTO.setDescription( incomeExpenseHead.getDescription() );
        incomeExpenseHeadDTO.setLastModifiedDate( incomeExpenseHead.getLastModifiedDate() );

        return incomeExpenseHeadDTO;
    }

    @Override
    public List<IncomeExpenseHeadDTO> incomeExpenseHeadsToIncomeExpenseHeadDTOs(List<IncomeExpenseHead> incomeExpenseHeads) {
        if ( incomeExpenseHeads == null ) {
            return null;
        }

        List<IncomeExpenseHeadDTO> list = new ArrayList<IncomeExpenseHeadDTO>();
        for ( IncomeExpenseHead incomeExpenseHead : incomeExpenseHeads ) {
            list.add( incomeExpenseHeadToIncomeExpenseHeadDTO( incomeExpenseHead ) );
        }

        return list;
    }

    @Override
    public IncomeExpenseHead incomeExpenseHeadDTOToIncomeExpenseHead(IncomeExpenseHeadDTO incomeExpenseHeadDTO) {
        if ( incomeExpenseHeadDTO == null ) {
            return null;
        }

        IncomeExpenseHead incomeExpenseHead = new IncomeExpenseHead();

        incomeExpenseHead.setActivated( incomeExpenseHeadDTO.getActivated() );
        incomeExpenseHead.setPid( incomeExpenseHeadDTO.getPid() );
        incomeExpenseHead.setName( incomeExpenseHeadDTO.getName() );
        incomeExpenseHead.setAlias( incomeExpenseHeadDTO.getAlias() );
        incomeExpenseHead.setDescription( incomeExpenseHeadDTO.getDescription() );

        return incomeExpenseHead;
    }

    @Override
    public List<IncomeExpenseHead> incomeExpenseHeadDTOsToIncomeExpenseHeads(List<IncomeExpenseHeadDTO> incomeExpenseHeadDTOs) {
        if ( incomeExpenseHeadDTOs == null ) {
            return null;
        }

        List<IncomeExpenseHead> list = new ArrayList<IncomeExpenseHead>();
        for ( IncomeExpenseHeadDTO incomeExpenseHeadDTO : incomeExpenseHeadDTOs ) {
            list.add( incomeExpenseHeadDTOToIncomeExpenseHead( incomeExpenseHeadDTO ) );
        }

        return list;
    }
    private String incomeExpenseHeadName(IncomeExpenseHead incomeExpenseHead) {
        if(incomeExpenseHead.getDescription()!=null && getCompanyCofig() && !incomeExpenseHead.getDescription().equals("common")) {
        return incomeExpenseHead.getDescription();
        }
       
    return incomeExpenseHead.getName();
    }

}
