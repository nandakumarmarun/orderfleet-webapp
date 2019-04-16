package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.VoucherNumberGenerator;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.repository.VoucherNumberGeneratorRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.VoucherNumberGeneratorService;
import com.orderfleet.webapp.web.rest.dto.VoucherNumberGeneratorDTO;

@Service
@Transactional
public class VoucherNumberGeneratorServiceImpl implements VoucherNumberGeneratorService{

	private final Logger log = LoggerFactory.getLogger(VoucherNumberGeneratorServiceImpl.class);
	
	@Inject
	private VoucherNumberGeneratorRepository voucherNumberGeneratorRepository; 
	
	@Inject
	private  CompanyRepository companyRepository;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private UserRepository userRepository;
	
	/**
	 * Save a VoucherNumberGenerator.
	 * 
	 * @param voucherNumberGeneratorDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public VoucherNumberGeneratorDTO save(VoucherNumberGeneratorDTO voucherNumberGeneratorDTO) {
		log.debug("Request to save VoucherNumberGenerator by prefix : {}", voucherNumberGeneratorDTO);
		VoucherNumberGenerator voucherNumberGenerator=new VoucherNumberGenerator();
		voucherNumberGenerator.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		voucherNumberGenerator.setPrefix(voucherNumberGeneratorDTO.getPrefix());
		voucherNumberGenerator.setStartwith(voucherNumberGeneratorDTO.getStartWith());
		Document document=documentRepository.findOneByPid(voucherNumberGeneratorDTO.getDocumentPid()).get();
		if(document!=null) {
			voucherNumberGenerator.setDocument(document);
		}
		User user=userRepository.findOneByPid(voucherNumberGeneratorDTO.getUserPid()).get();
		if(user!=null) {
			voucherNumberGenerator.setUser(user);
		}
		voucherNumberGenerator=voucherNumberGeneratorRepository.save(voucherNumberGenerator);
		voucherNumberGeneratorDTO=new VoucherNumberGeneratorDTO(voucherNumberGenerator);
		return voucherNumberGeneratorDTO;
	}
	
	/**
	 * Update a VoucherNumberGenerator.
	 * 
	 * @param voucherNumberGeneratorDTO
	 *            the entity to update
	 * @return the persisted entity
	 */
	@Override
	public VoucherNumberGeneratorDTO update(VoucherNumberGeneratorDTO voucherNumberGeneratorDTO) {
		log.debug("Request to update VoucherNumberGenerator by prefix : {}", voucherNumberGeneratorDTO);
		VoucherNumberGenerator voucherNumberGenerator=voucherNumberGeneratorRepository.findOne(voucherNumberGeneratorDTO.getId());
		voucherNumberGenerator.setPrefix(voucherNumberGeneratorDTO.getPrefix());
		voucherNumberGenerator.setStartwith(voucherNumberGeneratorDTO.getStartWith());
		Document document=documentRepository.findOneByPid(voucherNumberGeneratorDTO.getDocumentPid()).get();
		if(document!=null) {
			voucherNumberGenerator.setDocument(document);
		}
		User user=userRepository.findOneByPid(voucherNumberGeneratorDTO.getUserPid()).get();
		if(user!=null) {
			voucherNumberGenerator.setUser(user);
		}
		voucherNumberGenerator=voucherNumberGeneratorRepository.save(voucherNumberGenerator);
		voucherNumberGeneratorDTO=new VoucherNumberGeneratorDTO(voucherNumberGenerator);
		return voucherNumberGeneratorDTO;
	}

	
	/**
	 * Get the VoucherNumberGenerator by "prefix".
	 * 
	 * @param prefix
	 *            the prefix of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<VoucherNumberGeneratorDTO> findByPrefix(String prefix) {
		log.debug("Request to get VoucherNumberGenerator by prefix : {}", prefix);
		return voucherNumberGeneratorRepository.findByCompanyIdAndPrefixIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), prefix)
				.map(voucherNumberGenerator -> {
					VoucherNumberGeneratorDTO voucherNumberGeneratorDTO = new VoucherNumberGeneratorDTO(voucherNumberGenerator);
					return voucherNumberGeneratorDTO;
				});
	}

	@Override
	public List<VoucherNumberGeneratorDTO> findAllByCompany() {
		log.debug("Request to get all VoucherNumberGenerators");
		List<VoucherNumberGenerator> voucherNumberGenerators = voucherNumberGeneratorRepository.findAllByCompanyId();
		List<VoucherNumberGeneratorDTO> result = new ArrayList<>();
		for(VoucherNumberGenerator voucherNumberGenerator:voucherNumberGenerators) {
			VoucherNumberGeneratorDTO voucherNumberGeneratorDTO=new VoucherNumberGeneratorDTO(voucherNumberGenerator);
			result.add(voucherNumberGeneratorDTO);
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<VoucherNumberGeneratorDTO> findById(Long id) {
		log.debug("Web request to get VoucherNumberGenerator by id : {}", id);
		return voucherNumberGeneratorRepository.findById(id).map(voucherNumberGenerator->{
			VoucherNumberGeneratorDTO voucherNumberGeneratorDTO=new VoucherNumberGeneratorDTO(voucherNumberGenerator);
			return voucherNumberGeneratorDTO;
		});
	}

	/**
	 * Delete the voucherNumberGenerator by id.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	@Override
	public void delete(Long id) {
		log.debug("Request to delete VoucherNumberGenerator : {}", id);
		voucherNumberGeneratorRepository.findById(id).ifPresent(voucherNumberGenerator -> {
			voucherNumberGeneratorRepository.delete(voucherNumberGenerator.getId());
		});
	}

	
	
}
