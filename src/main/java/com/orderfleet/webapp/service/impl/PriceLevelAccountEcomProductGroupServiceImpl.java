package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountEcomProductGroup;
import com.orderfleet.webapp.domain.EcomProductGroup;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.EcomProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountEcomProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.UserAccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceLevelAccountEcomProductGroupService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountEcomProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceLevelMapper;

@Service
@Transactional
public class PriceLevelAccountEcomProductGroupServiceImpl implements PriceLevelAccountEcomProductGroupService{

	private final Logger log = LoggerFactory.getLogger(PriceLevelAccountEcomProductGroupServiceImpl.class);
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private PriceLevelAccountEcomProductGroupRepository priceLevelAccountProductGroupRepository;
	
	@Inject
	private AccountProfileRepository accountProfileRepository;
	
	@Inject
	private EcomProductGroupRepository ecomProductGroupRepository;
	
	@Inject
	private PriceLevelRepository priceLevelRepository;
	
	@Inject
	private UserAccountProfileRepository userAccountProfileRepository;
	
	@Inject
	private PriceLevelMapper priceLevelMapper;
	
	@Inject
	private PriceLevelListService priceLevelListService;
	
	@Override
	public PriceLevelAccountEcomProductGroupDTO save(PriceLevelAccountEcomProductGroupDTO priceLevelAccountProductGroupDTO) {
		log.debug("Request to save PriceLevelAccountEcomProductGroup : {}", priceLevelAccountProductGroupDTO);
		PriceLevelAccountEcomProductGroup priceLevelAccountProductGroup=new PriceLevelAccountEcomProductGroup();
		priceLevelAccountProductGroup.setAccountProfile(accountProfileRepository.findOneByPid(priceLevelAccountProductGroupDTO.getAccountPid()).get());
		priceLevelAccountProductGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		priceLevelAccountProductGroup.setPid(PriceLevelAccountEcomProductGroupService.PID_PREFIX+RandomUtil.generatePid());
		priceLevelAccountProductGroup.setPriceLevel(priceLevelRepository.findOneByPid(priceLevelAccountProductGroupDTO.getPriceLevelPid()).get());
		priceLevelAccountProductGroup.setProductGroup(ecomProductGroupRepository.findOneByPid(priceLevelAccountProductGroupDTO.getProductGroupPid()).get());
		priceLevelAccountProductGroup=priceLevelAccountProductGroupRepository.save(priceLevelAccountProductGroup);
		PriceLevelAccountEcomProductGroupDTO result=new PriceLevelAccountEcomProductGroupDTO(priceLevelAccountProductGroup);
		return result;
	}

	@Override
	public PriceLevelAccountEcomProductGroupDTO update(PriceLevelAccountEcomProductGroupDTO priceLevelAccountProductGroupDTO) {
		log.debug("Request to save PriceLevelAccountEcomProductGroup : {}", priceLevelAccountProductGroupDTO);
		PriceLevelAccountEcomProductGroup priceLevelAccountProductGroup=priceLevelAccountProductGroupRepository.findOneByPid(priceLevelAccountProductGroupDTO.getPid()).get();
		priceLevelAccountProductGroup.setAccountProfile(accountProfileRepository.findOneByPid(priceLevelAccountProductGroupDTO.getAccountPid()).get());
		priceLevelAccountProductGroup.setPriceLevel(priceLevelRepository.findOneByPid(priceLevelAccountProductGroupDTO.getPriceLevelPid()).get());
		priceLevelAccountProductGroup.setProductGroup(ecomProductGroupRepository.findOneByPid(priceLevelAccountProductGroupDTO.getProductGroupPid()).get());
		priceLevelAccountProductGroup=priceLevelAccountProductGroupRepository.save(priceLevelAccountProductGroup);
		PriceLevelAccountEcomProductGroupDTO result=new PriceLevelAccountEcomProductGroupDTO(priceLevelAccountProductGroup);
		return result;
	}

	@Override
	public List<PriceLevelAccountEcomProductGroupDTO> findAllByCompany() {
		log.debug("Request to get all PriceLevelAccountProductGroups");
		List<PriceLevelAccountEcomProductGroup>priceLevelAccountProductGroups=priceLevelAccountProductGroupRepository.findAllByCompanyId();
		List<PriceLevelAccountEcomProductGroupDTO>priceLevelAccountProductGroupDTOs=new ArrayList<>();
		for (PriceLevelAccountEcomProductGroup priceLevelAccountProductGroup : priceLevelAccountProductGroups) {
			PriceLevelAccountEcomProductGroupDTO result=new PriceLevelAccountEcomProductGroupDTO(priceLevelAccountProductGroup);
			priceLevelAccountProductGroupDTOs.add(result);
		}
		return priceLevelAccountProductGroupDTOs;
	}

	@Override
	public Optional<PriceLevelAccountEcomProductGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get PriceLevelAccountEcomProductGroup by pid : {}", pid);
		return priceLevelAccountProductGroupRepository.findOneByPid(pid).map(priceLevelAccountProductGroup -> {
			PriceLevelAccountEcomProductGroupDTO priceLevelAccountProductGroupDTO = new PriceLevelAccountEcomProductGroupDTO(priceLevelAccountProductGroup);
			return priceLevelAccountProductGroupDTO;
		});
	}


	@Override
	public void delete(String pid) {
		log.debug("Request to delete priceLevelAccountProductGroup : {}", pid);
		priceLevelAccountProductGroupRepository.findOneByPid(pid).ifPresent(priceLevelAccountProductGroup -> {
			priceLevelAccountProductGroupRepository.delete(priceLevelAccountProductGroup.getId());
		});
	}

	@Override
	public void saveMultiPriceLevelAccountProductGroup(String accountPids, String productGroupPids,
			String priceLevelPid) {
		List<PriceLevelAccountEcomProductGroup>priceLevelAccountProductGroups=new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<AccountProfile>accountProfiles = accountProfileRepository.findByPidIn(Arrays.asList(accountPids.split(",")));
		List<EcomProductGroup>productGroups=ecomProductGroupRepository.findAllByCompanyPidAndGroupPidIn(company.getPid(), Arrays.asList(productGroupPids.split(",")));
		PriceLevel priceLevel = priceLevelRepository.findOneByPid(priceLevelPid).get();
		for (EcomProductGroup productGroup : productGroups) {
			for (AccountProfile accountProfile : accountProfiles) {
				PriceLevelAccountEcomProductGroup priceLevelAccountProductGroup=new PriceLevelAccountEcomProductGroup();
				priceLevelAccountProductGroup.setAccountProfile(accountProfile);
				priceLevelAccountProductGroup.setCompany(company);
				priceLevelAccountProductGroup.setPid(PriceLevelAccountEcomProductGroupService.PID_PREFIX+RandomUtil.generatePid());
				priceLevelAccountProductGroup.setPriceLevel(priceLevel);
				priceLevelAccountProductGroup.setProductGroup(productGroup);
				priceLevelAccountProductGroups.add(priceLevelAccountProductGroup);
			}
		}
		priceLevelAccountProductGroupRepository.save(priceLevelAccountProductGroups);	
	}

	@Override
	public List<PriceLevelDTO> findAllByUserLogin() {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		String login = SecurityUtils.getCurrentUserLogin();
		Optional<UserAccountProfile> userAccountProfile = userAccountProfileRepository
				.findByCompanyIdAndUserLogin(companyId, login);
		List<PriceLevelDTO>priceLevelDTOs=new ArrayList<>();
		if (userAccountProfile.isPresent()) {
			List<PriceLevel>priceLevels=priceLevelAccountProductGroupRepository.findAllByAccountProfile(userAccountProfile.get().getAccountProfile().getPid());
			priceLevelDTOs = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevels);
			priceLevelDTOs.forEach(priceLevelDTO -> priceLevelDTO
					.setLevelListDTOs(priceLevelListService.findAllByCompanyIdAndPriceLevelPid(priceLevelDTO.getPid())));
		}
		return priceLevelDTOs;
	}

}
