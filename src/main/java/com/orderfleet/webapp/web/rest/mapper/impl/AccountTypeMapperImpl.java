package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.mapper.AccountTypeMapper;
@Component
public class AccountTypeMapperImpl extends AccountTypeMapper {

	 @Override
	    public AccountTypeDTO accountTypeToAccountTypeDTO(AccountType accountType) {
	        if ( accountType == null ) {
	            return null;
	        }

	        AccountTypeDTO accountTypeDTO = new AccountTypeDTO();

	        accountTypeDTO.setActivated( accountType.getActivated() );
	        accountTypeDTO.setPid( accountType.getPid() );
	        accountTypeDTO.setName(accountTypeName(accountType));
	        accountTypeDTO.setAlias( accountType.getAlias() );
	        accountTypeDTO.setDescription( accountType.getDescription() );
	        accountTypeDTO.setLastModifiedDate( accountType.getLastModifiedDate() );
	        accountTypeDTO.setAccountNameType( accountType.getAccountNameType() );
	        accountTypeDTO.setReceiverSupplierType( accountType.getReceiverSupplierType() );

	        return accountTypeDTO;
	    }

	    @Override
	    public List<AccountTypeDTO> accountTypesToAccountTypeDTOs(List<AccountType> accountTypes) {
	        if ( accountTypes == null ) {
	            return null;
	        }

	        List<AccountTypeDTO> list = new ArrayList<AccountTypeDTO>();
	        for ( AccountType accountType : accountTypes ) {
	            list.add( accountTypeToAccountTypeDTO( accountType ) );
	        }

	        return list;
	    }

	    @Override
	    public AccountType accountTypeDTOToAccountType(AccountTypeDTO accountTypeDTO) {
	        if ( accountTypeDTO == null ) {
	            return null;
	        }

	        AccountType accountType = new AccountType();

	        if ( accountTypeDTO.getActivated() != null ) {
	            accountType.setActivated( accountTypeDTO.getActivated() );
	        }
	        accountType.setPid( accountTypeDTO.getPid() );
	        accountType.setName( accountTypeDTO.getName() );
	        accountType.setAlias( accountTypeDTO.getAlias() );
	        accountType.setDescription( accountTypeDTO.getDescription() );
	        accountType.setAccountNameType( accountTypeDTO.getAccountNameType() );
	        accountType.setReceiverSupplierType( accountTypeDTO.getReceiverSupplierType() );

	        return accountType;
	    }

	    @Override
	    public List<AccountType> accountTypeDTOsToAccountTypes(List<AccountTypeDTO> accountTypeDTOs) {
	        if ( accountTypeDTOs == null ) {
	            return null;
	        }

	        List<AccountType> list = new ArrayList<AccountType>();
	        for ( AccountTypeDTO accountTypeDTO : accountTypeDTOs ) {
	            list.add( accountTypeDTOToAccountType( accountTypeDTO ) );
	        }

	        return list;
	    }
	    private String accountTypeName(AccountType accountType) {
	        if(accountType.getDescription()!=null && getCompanyCofig() && !accountType.getDescription().equals("common")) {
	        return accountType.getDescription();
	        }
	       
	    return accountType.getName();
	    }
}
