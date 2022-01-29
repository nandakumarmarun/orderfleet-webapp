package com.orderfleet.webapp.web.rest.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;


import com.orderfleet.webapp.domain.Bank;
import com.orderfleet.webapp.web.rest.dto.BankDTO;
import com.orderfleet.webapp.web.rest.mapper.BankMapper;
@Component
public class BankMapperImpl extends BankMapper{

	 @Override
	    public BankDTO bankToBankDTO(Bank bank) {
	        if ( bank == null ) {
	            return null;
	        }

	        BankDTO bankDTO = new BankDTO();

	        bankDTO.setActivated( bank.getActivated() );
	        bankDTO.setPid( bank.getPid() );
	        bankDTO.setName( bankName(bank) );
	        bankDTO.setAlias( bank.getAlias() );
	        bankDTO.setDescription( bank.getDescription() );

	        return bankDTO;
	    }

	    @Override
	    public List<BankDTO> banksToBankDTOs(List<Bank> banks) {
	        if ( banks == null ) {
	            return null;
	        }

	        List<BankDTO> list = new ArrayList<BankDTO>();
	        for ( Bank bank : banks ) {
	            list.add( bankToBankDTO( bank ) );
	        }

	        return list;
	    }

	    @Override
	    public Bank bankDTOToBank(BankDTO bankDTO) {
	        if ( bankDTO == null ) {
	            return null;
	        }

	        Bank bank = new Bank();

	        bank.setActivated( bankDTO.getActivated() );
	        bank.setPid( bankDTO.getPid() );
	        bank.setName( bankDTO.getName() );
	        bank.setAlias( bankDTO.getAlias() );
	        bank.setDescription( bankDTO.getDescription() );

	        return bank;
	    }

	    @Override
	    public List<Bank> bankDTOsToBanks(List<BankDTO> bankDTOs) {
	        if ( bankDTOs == null ) {
	            return null;
	        }

	        List<Bank> list = new ArrayList<Bank>();
	        for ( BankDTO bankDTO : bankDTOs ) {
	            list.add( bankDTOToBank( bankDTO ) );
	        }

	        return list;
	    }
	    private String bankName(Bank bank) {
	        if(bank.getDescription()!=null && getCompanyCofig() && !bank.getDescription().equals("common")) {
	        return bank.getDescription();
	        }
	       
	    return bank.getName();
	    }
}
