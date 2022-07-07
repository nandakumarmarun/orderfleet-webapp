package com.orderfleet.webapp.service.impl;

import java.util.Optional;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.AlterIdMaster;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.repository.AlterIdMasterRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AlterIdMasterService;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AlterIdMasterDTO;

@Service
@Transactional
public class AlterIdMasterServiceImpl implements AlterIdMasterService {

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private AlterIdMasterRepository alterIdMasterRepository;

	@Override
	public AlterIdMasterDTO save(AlterIdMasterDTO alteridMasterDTO) {

		Optional<AlterIdMaster> optionalAlterIdMaster = alterIdMasterRepository
				.findOnByMasterNameAndCompany(alteridMasterDTO.getMasterName(), alteridMasterDTO.getCompanyId());
		AlterIdMasterDTO result = new AlterIdMasterDTO();
		if (optionalAlterIdMaster.isPresent()) {
			System.out.println("updating alterIDMAster");
			AlterIdMaster alterIdMaster = alterIdMasterDTOToAlterIdMaster(alteridMasterDTO);
			AlterIdMaster alterId = optionalAlterIdMaster.get();
			alterIdMaster.setId(alterId.getId());
			alterIdMaster.setCompany(companyRepository.findOne(alteridMasterDTO.getCompanyId()));
			alterIdMaster = alterIdMasterRepository.save(alterIdMaster);
			result = alterIdMasterToAlterIdMasterDTO(alterIdMaster);
			System.out.println("updating alterIDMAster successfull");
		} else {
			System.out.println("saving alterIDMAster");
			AlterIdMaster alterIdMaster = alterIdMasterDTOToAlterIdMaster(alteridMasterDTO);
			alterIdMaster.setCompany(companyRepository.findOne(alteridMasterDTO.getCompanyId()));
			alterIdMaster = alterIdMasterRepository.save(alterIdMaster);
			result = alterIdMasterToAlterIdMasterDTO(alterIdMaster);
			System.out.println("saved alterIDMAster successfull");
		}
		return result;
	}

	@Override
	public AlterIdMasterDTO findByMasterName(String masterName, Long companyId) {
		Optional<AlterIdMaster> optionalAlterIdMaster = alterIdMasterRepository.findOnByMasterNameAndCompany(masterName,
				companyId);
		AlterIdMaster alterIdMaster = new AlterIdMaster();
		AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
		if (optionalAlterIdMaster.isPresent()) {
			alterIdMaster = optionalAlterIdMaster.get();
		    alterIdMasterDTO = alterIdMasterToAlterIdMasterDTO(alterIdMaster);
		}
		return alterIdMasterDTO;
	}

	@Override
	public AlterIdMaster alterIdMasterDTOToAlterIdMaster(AlterIdMasterDTO alterIdMasterDTO) {
		AlterIdMaster alterIdMaster = new AlterIdMaster();
		alterIdMaster.setAlterId(alterIdMasterDTO.getAlterId());
		alterIdMaster.setMasterName(alterIdMasterDTO.getMasterName());
		return alterIdMaster;
	}

	@Override
	public AlterIdMasterDTO alterIdMasterToAlterIdMasterDTO(AlterIdMaster alterIdMaster) {
		AlterIdMasterDTO alterIdMasterDTO = new AlterIdMasterDTO();
		alterIdMasterDTO.setId(alterIdMaster.getId());
		alterIdMasterDTO.setAlterId(alterIdMaster.getAlterId());
		alterIdMasterDTO.setCompanyId(alterIdMaster.getCompany().getId());
		alterIdMasterDTO.setCreatedDate(alterIdMaster.getCreatedDate());
		alterIdMasterDTO.setLastModifiedDate(alterIdMaster.getLastModifiedDate());
		return alterIdMasterDTO;
	}

}
