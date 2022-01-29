package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AccountType;
import com.orderfleet.webapp.domain.ReceivablePayable;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.mapper.ReceivablePayableMapper;

@Component
public class ReceivablePayableMapperImpl extends ReceivablePayableMapper{

@Override
    public ReceivablePayableDTO receivablePayableToReceivablePayableDTO(ReceivablePayable receivablePayable) {
        if ( receivablePayable == null ) {
            return null;
        }

        ReceivablePayableDTO receivablePayableDTO = new ReceivablePayableDTO();

        receivablePayableDTO.setAccountPid( receivablePayableAccountProfilePid( receivablePayable ) );
        receivablePayableDTO.setAccountName( receivablePayableAccountProfileName( receivablePayable ) );
        receivablePayableDTO.setAccountAddress( receivablePayableAccountProfileAddress( receivablePayable ) );
        receivablePayableDTO.setAccountType( receivablePayableAccountProfileAccountTypeName( receivablePayable ) );
        receivablePayableDTO.setPid( receivablePayable.getPid() );
        receivablePayableDTO.setReceivablePayableType( receivablePayable.getReceivablePayableType() );
        receivablePayableDTO.setReferenceDocumentNumber( receivablePayable.getReferenceDocumentNumber() );
        receivablePayableDTO.setReferenceDocumentDate( receivablePayable.getReferenceDocumentDate() );
        receivablePayableDTO.setReferenceDocumentType( receivablePayable.getReferenceDocumentType() );
        receivablePayableDTO.setReferenceDocumentAmount( receivablePayable.getReferenceDocumentAmount() );
        receivablePayableDTO.setReferenceDocumentBalanceAmount( receivablePayable.getReferenceDocumentBalanceAmount() );
        receivablePayableDTO.setRemarks( receivablePayable.getRemarks() );
        receivablePayableDTO.setBillOverDue( receivablePayable.getBillOverDue() );
        receivablePayableDTO.setLastModifiedDate( receivablePayable.getLastModifiedDate() );
        receivablePayableDTO.setReceivablePayableId( receivablePayable.getReceivablePayableId() );

        return receivablePayableDTO;
    }

    @Override
    public List<ReceivablePayableDTO> receivablePayablesToReceivablePayableDTOs(List<ReceivablePayable> productCategories) {
        if ( productCategories == null ) {
            return null;
        }

        List<ReceivablePayableDTO> list = new ArrayList<ReceivablePayableDTO>();
        for ( ReceivablePayable receivablePayable : productCategories ) {
            list.add( receivablePayableToReceivablePayableDTO( receivablePayable ) );
        }

        return list;
    }

    @Override
    public ReceivablePayable receivablePayableDTOToReceivablePayable(ReceivablePayableDTO receivablePayableDTO) {
        if ( receivablePayableDTO == null ) {
            return null;
        }

        ReceivablePayable receivablePayable = new ReceivablePayable();

        receivablePayable.setPid( receivablePayableDTO.getPid() );
        receivablePayable.setReceivablePayableType( receivablePayableDTO.getReceivablePayableType() );
        receivablePayable.setReferenceDocumentNumber( receivablePayableDTO.getReferenceDocumentNumber() );
        receivablePayable.setReferenceDocumentDate( receivablePayableDTO.getReferenceDocumentDate() );
        receivablePayable.setReferenceDocumentType( receivablePayableDTO.getReferenceDocumentType() );
        receivablePayable.setReferenceDocumentAmount( receivablePayableDTO.getReferenceDocumentAmount() );
        receivablePayable.setReferenceDocumentBalanceAmount( receivablePayableDTO.getReferenceDocumentBalanceAmount() );
        receivablePayable.setRemarks( receivablePayableDTO.getRemarks() );
        receivablePayable.setBillOverDue( receivablePayableDTO.getBillOverDue() );
        receivablePayable.setLastModifiedDate( receivablePayableDTO.getLastModifiedDate() );
        receivablePayable.setReceivablePayableId( receivablePayableDTO.getReceivablePayableId() );

        return receivablePayable;
    }

    @Override
    public List<ReceivablePayable> receivablePayableDTOsToProductCategories(List<ReceivablePayableDTO> receivablePayableDTOs) {
        if ( receivablePayableDTOs == null ) {
            return null;
        }

        List<ReceivablePayable> list = new ArrayList<ReceivablePayable>();
        for ( ReceivablePayableDTO receivablePayableDTO : receivablePayableDTOs ) {
            list.add( receivablePayableDTOToReceivablePayable( receivablePayableDTO ) );
        }

        return list;
    }

    private String receivablePayableAccountProfilePid(ReceivablePayable receivablePayable) {

        if ( receivablePayable == null ) {
            return null;
        }
        AccountProfile accountProfile = receivablePayable.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String pid = accountProfile.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String receivablePayableAccountProfileName(ReceivablePayable receivablePayable) {

        if ( receivablePayable == null ) {
            return null;
        }
        AccountProfile accountProfile = receivablePayable.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String name = accountProfile.getName();
        if ( name == null ) {
            return null;
        }
        if(accountProfile.getDescription()!=null && getCompanyCofig() && !accountProfile.getDescription().equals("common")) {
	        return accountProfile.getDescription();
	        }

        return name;
    }

    private String receivablePayableAccountProfileAddress(ReceivablePayable receivablePayable) {

        if ( receivablePayable == null ) {
            return null;
        }
        AccountProfile accountProfile = receivablePayable.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String address = accountProfile.getAddress();
        if ( address == null ) {
            return null;
        }
        return address;
    }

    private String receivablePayableAccountProfileAccountTypeName(ReceivablePayable receivablePayable) {

        if ( receivablePayable == null ) {
            return null;
        }
        AccountProfile accountProfile = receivablePayable.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        AccountType accountType = accountProfile.getAccountType();
        if ( accountType == null ) {
            return null;
        }
        String name = accountType.getName();
        if ( name == null ) {
            return null;
        }
        return name;
    }

}
