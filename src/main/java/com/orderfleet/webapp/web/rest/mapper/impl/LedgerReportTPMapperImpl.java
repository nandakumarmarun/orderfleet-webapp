package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Division;
import com.orderfleet.webapp.domain.LedgerReportTP;
import com.orderfleet.webapp.web.rest.dto.LedgerReportTPDTO;
import com.orderfleet.webapp.web.rest.mapper.LedgerReportTPMapper;
@Component
public class LedgerReportTPMapperImpl extends LedgerReportTPMapper {

   @Override
    public LedgerReportTPDTO ledgerReportTPToLedgerReportTPDTO(LedgerReportTP ledgerReportTP) {
        if ( ledgerReportTP == null ) {
            return null;
        }

        LedgerReportTPDTO ledgerReportTPDTO = new LedgerReportTPDTO();

        ledgerReportTPDTO.setDivisionName( ledgerReportTPDivisionName( ledgerReportTP ) );
        ledgerReportTPDTO.setDivisionPid( ledgerReportTPDivisionPid( ledgerReportTP ) );
        ledgerReportTPDTO.setAccountProfilePid( ledgerReportTPAccountProfilePid( ledgerReportTP ) );
        ledgerReportTPDTO.setAccountProfileName( ledgerReportTPAccountProfileName( ledgerReportTP ) );
        ledgerReportTPDTO.setDivisionAlias( ledgerReportTPDivisionAlias( ledgerReportTP ) );
        ledgerReportTPDTO.setId( ledgerReportTP.getId() );
        ledgerReportTPDTO.setVoucheNo( ledgerReportTP.getVoucheNo() );
        ledgerReportTPDTO.setVoucherDate( ledgerReportTP.getVoucherDate() );
        ledgerReportTPDTO.setNarration( ledgerReportTP.getNarration() );
        ledgerReportTPDTO.setAmount( ledgerReportTP.getAmount() );
        ledgerReportTPDTO.setType( ledgerReportTP.getType() );
        ledgerReportTPDTO.setDebitCredit( ledgerReportTP.getDebitCredit() );

        return ledgerReportTPDTO;
    }

    @Override
    public List<LedgerReportTPDTO> ledgerReportTPsToLedgerReportTPDTOs(List<LedgerReportTP> ledgerReportTPs) {
        if ( ledgerReportTPs == null ) {
            return null;
        }

        List<LedgerReportTPDTO> list = new ArrayList<LedgerReportTPDTO>();
        for ( LedgerReportTP ledgerReportTP : ledgerReportTPs ) {
            list.add( ledgerReportTPToLedgerReportTPDTO( ledgerReportTP ) );
        }

        return list;
    }

    @Override
    public LedgerReportTP ledgerReportTPDTOToLedgerReportTP(LedgerReportTPDTO ledgerReportTPDTO) {
        if ( ledgerReportTPDTO == null ) {
            return null;
        }

        LedgerReportTP ledgerReportTP = new LedgerReportTP();

        ledgerReportTP.setDivision( divisionFromPid( ledgerReportTPDTO.getDivisionPid() ) );
        ledgerReportTP.setAccountProfile( accountProfileFromPid( ledgerReportTPDTO.getAccountProfilePid() ) );
        ledgerReportTP.setId( ledgerReportTPDTO.getId() );
        ledgerReportTP.setVoucheNo( ledgerReportTPDTO.getVoucheNo() );
        ledgerReportTP.setVoucherDate( ledgerReportTPDTO.getVoucherDate() );
        ledgerReportTP.setNarration( ledgerReportTPDTO.getNarration() );
        ledgerReportTP.setAmount( ledgerReportTPDTO.getAmount() );
        ledgerReportTP.setType( ledgerReportTPDTO.getType() );
        ledgerReportTP.setDebitCredit( ledgerReportTPDTO.getDebitCredit() );

        return ledgerReportTP;
    }

    @Override
    public List<LedgerReportTP> ledgerReportTPDTOsToLedgerReportTPs(List<LedgerReportTPDTO> ledgerReportTPDTOs) {
        if ( ledgerReportTPDTOs == null ) {
            return null;
        }

        List<LedgerReportTP> list = new ArrayList<LedgerReportTP>();
        for ( LedgerReportTPDTO ledgerReportTPDTO : ledgerReportTPDTOs ) {
            list.add( ledgerReportTPDTOToLedgerReportTP( ledgerReportTPDTO ) );
        }

        return list;
    }

    private String ledgerReportTPDivisionName(LedgerReportTP ledgerReportTP) {

        if ( ledgerReportTP == null ) {
            return null;
        }
        Division division = ledgerReportTP.getDivision();
        if ( division == null ) {
            return null;
        }
        String name = division.getName();
        if ( name == null ) {
            return null;
        }
        if(division.getDescription()!=null && getCompanyCofig() && !division.getDescription().equals("common")) {
	        return division.getDescription();
	        }
	       
        return name;
    }

    private String ledgerReportTPDivisionPid(LedgerReportTP ledgerReportTP) {

        if ( ledgerReportTP == null ) {
            return null;
        }
        Division division = ledgerReportTP.getDivision();
        if ( division == null ) {
            return null;
        }
        String pid = division.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String ledgerReportTPAccountProfilePid(LedgerReportTP ledgerReportTP) {

        if ( ledgerReportTP == null ) {
            return null;
        }
        AccountProfile accountProfile = ledgerReportTP.getAccountProfile();
        if ( accountProfile == null ) {
            return null;
        }
        String pid = accountProfile.getPid();
        if ( pid == null ) {
            return null;
        }
        return pid;
    }

    private String ledgerReportTPAccountProfileName(LedgerReportTP ledgerReportTP) {

        if ( ledgerReportTP == null ) {
            return null;
        }
        AccountProfile accountProfile = ledgerReportTP.getAccountProfile();
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

    private String ledgerReportTPDivisionAlias(LedgerReportTP ledgerReportTP) {

        if ( ledgerReportTP == null ) {
            return null;
        }
        Division division = ledgerReportTP.getDivision();
        if ( division == null ) {
            return null;
        }
        String alias = division.getAlias();
        if ( alias == null ) {
            return null;
        }
        return alias;
    }

}
