package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.TaxMaster;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.TaxMasterRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.TaxMasterService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.TaxMasterDTO;
import com.orderfleet.webapp.web.rest.mapper.TaxMasterMapper;

/**
 * Service Implementation for managing TaxMaster.
 *
 * @author Sarath
 * @since Aug 8, 2017
 *
 */

@Service
@Transactional
public class TaxMasterServiceImpl implements TaxMasterService {

	private final Logger log = LoggerFactory.getLogger(TaxMasterServiceImpl.class);

	@Inject
	private TaxMasterMapper taxMasterMapper;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private TaxMasterRepository taxMasterRepository;

	@Override
	public TaxMasterDTO saveTaxMaster(TaxMasterDTO taxMasterDTO) {

		log.debug("Request to save  taxMaster : {}", taxMasterDTO);
		// set pid
		taxMasterDTO.setPid(TaxMasterService.PID_PREFIX + RandomUtil.generatePid());
		TaxMaster taxMaster = taxMasterMapper.taxMasterDTOToTaxMaster(taxMasterDTO);
		// set company
		taxMaster.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		taxMaster = taxMasterRepository.save(taxMaster);
		TaxMasterDTO result = taxMasterMapper.taxMasterToTaxMasterDTO(taxMaster);
		return result;
	}

	@Override
	public Optional<TaxMasterDTO> findByName(String name) {
		return taxMasterRepository.findByCompanyIdAndVatNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(taxMaster -> {
					TaxMasterDTO taxMasterDTO = taxMasterMapper.taxMasterToTaxMasterDTO(taxMaster);
					return taxMasterDTO;
				});
	}

	@Override
	public TaxMasterDTO updateTaxMaster(TaxMasterDTO taxMasterDTO) {
		return taxMasterRepository.findOneByPid(taxMasterDTO.getPid()).map(taxMaster -> {
		taxMaster.setVatName(taxMasterDTO.getVatName());
		taxMaster.setDescription(taxMasterDTO.getDescription());
		taxMaster.setVatPercentage(taxMasterDTO.getVatPercentage());
		taxMaster.setVatClass(taxMasterDTO.getVatClass());
		taxMaster = taxMasterRepository.save(taxMaster);
			TaxMasterDTO result = taxMasterMapper.taxMasterToTaxMasterDTO(taxMaster);
			return result;
		}).orElse(null);
	}

	@Override
	public List<TaxMasterDTO> findAllByCompany() {
		List<TaxMaster> taxMasterList = taxMasterRepository.findAllByCompanyId();
		List<TaxMasterDTO> result = taxMasterMapper.taxMastersToTaxMasterDTOs(taxMasterList);
		return result;
	}

	@Override
	public Optional<TaxMasterDTO> findOneByPid(String pid) {
		return taxMasterRepository.findOneByPid(pid).map(taxMaster -> {
			TaxMasterDTO taxMasterDTO = taxMasterMapper.taxMasterToTaxMasterDTO(taxMaster);
			return taxMasterDTO;
		});
	}

	@Override
	public void delete(String pid) {
		taxMasterRepository.findOneByPid(pid).ifPresent(taxMaster -> {
			taxMasterRepository.delete(taxMaster.getId());
		});
	}		
	

}
