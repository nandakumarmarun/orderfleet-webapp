package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountGroup;

import com.orderfleet.webapp.web.rest.dto.AccountGroupDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountGroupMapper;

@Component
public class AccountGroupMapperImpl extends AccountGroupMapper{

	 @Override
	    public AccountGroupDTO accountGroupToAccountGroupDTO(AccountGroup accountGroup) {
	        if ( accountGroup == null ) {
	            return null;
	        }

	        AccountGroupDTO accountGroupDTO = new AccountGroupDTO();

	        accountGroupDTO.setActivated( accountGroup.getActivated() );
	        accountGroupDTO.setPid( accountGroup.getPid() );
	        accountGroupDTO.setName( accountGroupName(accountGroup));
	        accountGroupDTO.setAlias( accountGroup.getAlias() );
	        accountGroupDTO.setDescription( accountGroup.getDescription() );
	        accountGroupDTO.setLastModifiedDate( accountGroup.getLastModifiedDate() );

	        return accountGroupDTO;
	    }

	    @Override
	    public List<AccountGroupDTO> accountGroupsToAccountGroupDTOs(List<AccountGroup> productCategories) {
	        if ( productCategories == null ) {
	            return null;
	        }

	        List<AccountGroupDTO> list = new ArrayList<AccountGroupDTO>();
	        for ( AccountGroup accountGroup : productCategories ) {
	            list.add( accountGroupToAccountGroupDTO( accountGroup ) );
	        }

	        return list;
	    }

	    @Override
	    public AccountGroup accountGroupDTOToAccountGroup(AccountGroupDTO accountGroupDTO) {
	        if ( accountGroupDTO == null ) {
	            return null;
	        }

	        AccountGroup accountGroup = new AccountGroup();

	        accountGroup.setActivated( accountGroupDTO.getActivated() );
	        accountGroup.setPid( accountGroupDTO.getPid() );
	        accountGroup.setName( accountGroupDTO.getName() );
	        accountGroup.setAlias( accountGroupDTO.getAlias() );
	        accountGroup.setDescription( accountGroupDTO.getDescription() );

	        return accountGroup;
	    }

	    @Override
	    public List<AccountGroup> accountGroupDTOsToProductCategories(List<AccountGroupDTO> accountGroupDTOs) {
	        if ( accountGroupDTOs == null ) {
	            return null;
	        }

	        List<AccountGroup> list = new ArrayList<AccountGroup>();
	        for ( AccountGroupDTO accountGroupDTO : accountGroupDTOs ) {
	            list.add( accountGroupDTOToAccountGroup( accountGroupDTO ) );
	        }

	        return list;
	    }
	    private String accountGroupName(AccountGroup accountGroup) {
	        if(accountGroup.getDescription()!=null && getCompanyCofig() && !accountGroup.getDescription().equals("common")) {
	        return accountGroup.getDescription();
	        }
	       
	    return accountGroup.getName();
	    }
}
