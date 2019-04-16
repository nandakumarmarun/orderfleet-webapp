package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.service.TallyConfigurationService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.tally.dto.TallyConfigurationDTO;

@Service
public class TallyConfigurationServiceImpl implements TallyConfigurationService {

	private final Logger log = LoggerFactory.getLogger(TallyConfigurationServiceImpl.class);
	@Inject
	TallyConfigurationRepository tallyConfigurationRepository;

	@Inject
	CompanyRepository companyRepository;

	@Override
	public TallyConfiguration save(TallyConfigurationDTO dto) {
		TallyConfiguration tallyConfiguration = new TallyConfiguration();
		Optional<Company> opCompany = companyRepository.findOneByPid(dto.getCompanyPid());
		if (opCompany.isPresent()) {
			if (!opCompany.get().getActivated()) {
				log.error(dto.getCompanyName() + ": Company Deactivated");
				return tallyConfiguration;
			}
			tallyConfiguration.setCompany(opCompany.get());
			tallyConfiguration.setActualBillStatus(dto.getActualBillStatus());
			tallyConfiguration.setDynamicDate(dto.getDynamicDate());
			tallyConfiguration.setGstNames(dto.getGstNames());
			tallyConfiguration.setOrderNumberWithEmployee(dto.getOrderNumberWithEmployee());
			tallyConfiguration.setPid(TallyConfigurationService.PID_PREFIX + RandomUtil.generatePid());
			tallyConfiguration.setReceiptVoucherType(dto.getReceiptVoucherType());
			tallyConfiguration.setSalesLedgerName(dto.getSalesLedgerName());
			tallyConfiguration.setStaticGodownNames(dto.getStaticGodownNames());
			tallyConfiguration.setRoundOffLedgerName(dto.getRoundOffLedgerName());
			tallyConfiguration.setTallyCompanyName(dto.getTallyCompanyName());
			tallyConfiguration.setTallyProductKey(dto.getTallyProductKey());
			tallyConfiguration.setBankName(dto.getBankName());
			tallyConfiguration.setBankReceiptType(dto.getBankReceiptType());
			tallyConfiguration.setCashReceiptType(dto.getCashReceiptType());
			tallyConfiguration.setItemRemarksEnabled(dto.getItemRemarksEnabled());
			tallyConfiguration.setPdcVoucherType(dto.getPdcVoucherType());
			tallyConfiguration.setTransactionType(dto.getTransactionType());
		}
		return tallyConfigurationRepository.save(tallyConfiguration);
	}

	@Override
	public TallyConfiguration update(TallyConfigurationDTO dto) {
		Optional<TallyConfiguration> opTallyConfiguration = tallyConfigurationRepository.findOneByPid(dto.getPid());
		TallyConfiguration tallyConfiguration = new TallyConfiguration();
		if (opTallyConfiguration.isPresent()) {
			tallyConfiguration = opTallyConfiguration.get();
			tallyConfiguration.setActualBillStatus(dto.getActualBillStatus());
			tallyConfiguration.setDynamicDate(dto.getDynamicDate());
			tallyConfiguration.setGstNames(dto.getGstNames());
			tallyConfiguration.setOrderNumberWithEmployee(dto.getOrderNumberWithEmployee());
			tallyConfiguration.setReceiptVoucherType(dto.getReceiptVoucherType());
			tallyConfiguration.setSalesLedgerName(dto.getSalesLedgerName());
			tallyConfiguration.setStaticGodownNames(dto.getStaticGodownNames());
			tallyConfiguration.setRoundOffLedgerName(dto.getRoundOffLedgerName());
			tallyConfiguration.setTallyCompanyName(dto.getTallyCompanyName());
			tallyConfiguration.setTallyProductKey(dto.getTallyProductKey());
			tallyConfiguration.setBankName(dto.getBankName());
			tallyConfiguration.setBankReceiptType(dto.getBankReceiptType());
			tallyConfiguration.setCashReceiptType(dto.getCashReceiptType());
			tallyConfiguration.setItemRemarksEnabled(dto.getItemRemarksEnabled());
			tallyConfiguration.setPdcVoucherType(dto.getPdcVoucherType());
			tallyConfiguration.setTransactionType(dto.getTransactionType());
			tallyConfigurationRepository.save(tallyConfiguration);
		}
		return tallyConfiguration;
	}

	@Override
	public TallyConfigurationDTO saveTallyConfiguration(TallyConfigurationDTO dto) {
		log.debug("Request to save TallyConfiguration : {}", dto);
		TallyConfiguration tallyConfiguration = new TallyConfiguration();

		Optional<Company> opCompany = companyRepository.findOneByPid(dto.getCompanyPid());
		if (opCompany.isPresent()) {
			if (!opCompany.get().getActivated()) {
				log.error(dto.getCompanyName() + ": Company Deactivated");

				TallyConfigurationDTO result = new TallyConfigurationDTO(tallyConfiguration);
				return result;

			}
			tallyConfiguration.setCompany(opCompany.get());
			tallyConfiguration.setActualBillStatus(dto.getActualBillStatus());
			tallyConfiguration.setDynamicDate(dto.getDynamicDate());
			tallyConfiguration.setGstNames(dto.getGstNames());
			tallyConfiguration.setOrderNumberWithEmployee(dto.getOrderNumberWithEmployee());
			tallyConfiguration.setPid(TallyConfigurationService.PID_PREFIX + RandomUtil.generatePid());
			tallyConfiguration.setReceiptVoucherType(dto.getReceiptVoucherType());
			tallyConfiguration.setSalesLedgerName(dto.getSalesLedgerName());
			tallyConfiguration.setStaticGodownNames(dto.getStaticGodownNames());
			tallyConfiguration.setRoundOffLedgerName(dto.getRoundOffLedgerName());
			tallyConfiguration.setTallyCompanyName(dto.getTallyCompanyName());
			tallyConfiguration.setTallyProductKey(dto.getTallyProductKey());
			tallyConfiguration.setBankName(dto.getBankName());
			tallyConfiguration.setBankReceiptType(dto.getBankReceiptType());
			tallyConfiguration.setCashReceiptType(dto.getCashReceiptType());
			tallyConfiguration.setItemRemarksEnabled(dto.getItemRemarksEnabled());
			tallyConfiguration.setPdcVoucherType(dto.getPdcVoucherType());
			tallyConfiguration.setTransactionType(dto.getTransactionType());
		}
		tallyConfiguration = tallyConfigurationRepository.save(tallyConfiguration);

		TallyConfigurationDTO result = new TallyConfigurationDTO(tallyConfiguration);
		return result;
	}

	@Override
	public TallyConfigurationDTO updateTallyConfiguration(TallyConfigurationDTO dto) {
		Optional<TallyConfiguration> opTallyConfiguration = tallyConfigurationRepository.findOneByPid(dto.getPid());
		TallyConfiguration tallyConfiguration = new TallyConfiguration();
		if (opTallyConfiguration.isPresent()) {
			tallyConfiguration = opTallyConfiguration.get();
			tallyConfiguration.setActualBillStatus(dto.getActualBillStatus());
			tallyConfiguration.setDynamicDate(dto.getDynamicDate());
			tallyConfiguration.setGstNames(dto.getGstNames());
			tallyConfiguration.setOrderNumberWithEmployee(dto.getOrderNumberWithEmployee());
			tallyConfiguration.setReceiptVoucherType(dto.getReceiptVoucherType());
			tallyConfiguration.setSalesLedgerName(dto.getSalesLedgerName());
			tallyConfiguration.setStaticGodownNames(dto.getStaticGodownNames());
			tallyConfiguration.setRoundOffLedgerName(dto.getRoundOffLedgerName());
			tallyConfiguration.setTallyCompanyName(dto.getTallyCompanyName());
			tallyConfiguration.setTallyProductKey(dto.getTallyProductKey());
			tallyConfiguration.setBankName(dto.getBankName());
			tallyConfiguration.setBankReceiptType(dto.getBankReceiptType());
			tallyConfiguration.setCashReceiptType(dto.getCashReceiptType());
			tallyConfiguration.setItemRemarksEnabled(dto.getItemRemarksEnabled());
			tallyConfiguration.setPdcVoucherType(dto.getPdcVoucherType());
			tallyConfiguration.setTransactionType(dto.getTransactionType());
			tallyConfiguration = tallyConfigurationRepository.save(tallyConfiguration);
		}
		TallyConfigurationDTO result = new TallyConfigurationDTO(tallyConfiguration);
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public TallyConfigurationDTO findByCompanyId(Long companyId) {
		log.debug("Request to get TallyConfigurations by company");
		TallyConfiguration configuration = tallyConfigurationRepository.findTallyByCompanyId(companyId);
		TallyConfigurationDTO tallyConfigurationDTO = null;
		if (configuration != null) {
			tallyConfigurationDTO = new TallyConfigurationDTO(configuration);
		}
		return tallyConfigurationDTO;
	}

	@Override
	@Transactional(readOnly = true)
	public List<TallyConfigurationDTO> findAll() {
		log.debug("Request to get all TallyConfigurations");
		return tallyConfigurationRepository.findAll().stream().map(TallyConfigurationDTO::new)
				.collect(Collectors.toList());
	}

	@Override
	public void deleteByPid(String pid) {
		log.debug("Request to delete TallyConfigurations");
		tallyConfigurationRepository.deleteByPid(pid);
	}

	@Override
	public Optional<TallyConfigurationDTO> findOneByCompanyId(Long companyId) {
		log.debug("Request to get all TallyConfigurations");
		return tallyConfigurationRepository.findOneByCompanyId(companyId).map(config -> {
			TallyConfigurationDTO tallyConfigurationDTO = new TallyConfigurationDTO(config);
			return tallyConfigurationDTO;
		});
	}

	@Override
	public Optional<TallyConfigurationDTO> findOneByCompanyId() {
		log.debug("Request to get all TallyConfigurations");
		return tallyConfigurationRepository.findOneByCompanyId().map(config -> {
			TallyConfigurationDTO tallyConfigurationDTO = new TallyConfigurationDTO(config);
			return tallyConfigurationDTO;
		});
	}

	@Override
	public Optional<TallyConfigurationDTO> findOneByPid(String pid) {
		// TODO Auto-generated method stub
		return null;
	}

}
