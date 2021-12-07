package com.orderfleet.webapp.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AccountProfile;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PriceLevel;
import com.orderfleet.webapp.domain.PriceLevelAccountProductGroup;
import com.orderfleet.webapp.domain.ProductGroup;
import com.orderfleet.webapp.domain.UserAccountProfile;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PriceLevelAccountProductGroupRepository;
import com.orderfleet.webapp.repository.PriceLevelRepository;
import com.orderfleet.webapp.repository.ProductGroupRepository;
import com.orderfleet.webapp.repository.UserAccountProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.PriceLevelAccountProductGroupService;
import com.orderfleet.webapp.service.PriceLevelListService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelListDTO;
import com.orderfleet.webapp.web.rest.mapper.PriceLevelMapper;

@Service
@Transactional
public class PriceLevelAccountProductGroupServiceImpl implements PriceLevelAccountProductGroupService {

	private final Logger log = LoggerFactory.getLogger(PriceLevelAccountProductGroupServiceImpl.class);
	  private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private PriceLevelAccountProductGroupRepository priceLevelAccountProductGroupRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private ProductGroupRepository productGroupRepository;

	@Inject
	private PriceLevelRepository priceLevelRepository;

	@Inject
	private UserAccountProfileRepository userAccountProfileRepository;

	@Inject
	private PriceLevelMapper priceLevelMapper;

	@Inject
	private PriceLevelListService priceLevelListService;

	@Override
	public PriceLevelAccountProductGroupDTO save(PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO) {
		log.debug("Request to save PriceLevelAccountProductGroup : {}", priceLevelAccountProductGroupDTO);
		PriceLevelAccountProductGroup priceLevelAccountProductGroup = new PriceLevelAccountProductGroup();
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get one by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		priceLevelAccountProductGroup.setAccountProfile(
				accountProfileRepository.findOneByPid(priceLevelAccountProductGroupDTO.getAccountPid()).get());
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		priceLevelAccountProductGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		priceLevelAccountProductGroup
				.setPid(PriceLevelAccountProductGroupService.PID_PREFIX + RandomUtil.generatePid());
		priceLevelAccountProductGroup.setPriceLevel(
				priceLevelRepository.findOneByPid(priceLevelAccountProductGroupDTO.getPriceLevelPid()).get());
		priceLevelAccountProductGroup.setProductGroup(
				productGroupRepository.findOneByPid(priceLevelAccountProductGroupDTO.getProductGroupPid()).get());
		priceLevelAccountProductGroup = priceLevelAccountProductGroupRepository.save(priceLevelAccountProductGroup);
		PriceLevelAccountProductGroupDTO result = new PriceLevelAccountProductGroupDTO(priceLevelAccountProductGroup);
		return result;
	}

	@Override
	public PriceLevelAccountProductGroupDTO update(PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO) {
		log.debug("Request to save PriceLevelAccountProductGroup : {}", priceLevelAccountProductGroupDTO);
		PriceLevelAccountProductGroup priceLevelAccountProductGroup = priceLevelAccountProductGroupRepository
				.findOneByPid(priceLevelAccountProductGroupDTO.getPid()).get();
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "AP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get one by pid";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		priceLevelAccountProductGroup.setAccountProfile(
				accountProfileRepository.findOneByPid(priceLevelAccountProductGroupDTO.getAccountPid()).get());
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);

		priceLevelAccountProductGroup.setPriceLevel(
				priceLevelRepository.findOneByPid(priceLevelAccountProductGroupDTO.getPriceLevelPid()).get());
		priceLevelAccountProductGroup.setProductGroup(
				productGroupRepository.findOneByPid(priceLevelAccountProductGroupDTO.getProductGroupPid()).get());
		priceLevelAccountProductGroup = priceLevelAccountProductGroupRepository.save(priceLevelAccountProductGroup);
		PriceLevelAccountProductGroupDTO result = new PriceLevelAccountProductGroupDTO(priceLevelAccountProductGroup);
		return result;
	}

	@Override
	public List<PriceLevelAccountProductGroupDTO> findAllByCompany() {
		log.debug("Request to get all PriceLevelAccountProductGroups");
		List<PriceLevelAccountProductGroup> priceLevelAccountProductGroups = priceLevelAccountProductGroupRepository
				.findAllByCompanyId();
		List<PriceLevelAccountProductGroupDTO> priceLevelAccountProductGroupDTOs = new ArrayList<>();
		for (PriceLevelAccountProductGroup priceLevelAccountProductGroup : priceLevelAccountProductGroups) {
			PriceLevelAccountProductGroupDTO result = new PriceLevelAccountProductGroupDTO(
					priceLevelAccountProductGroup);
			priceLevelAccountProductGroupDTOs.add(result);
		}
		return priceLevelAccountProductGroupDTOs;
	}

	@Override
	public Optional<PriceLevelAccountProductGroupDTO> findOneByPid(String pid) {
		log.debug("Request to get PriceLevelAccountProductGroup by pid : {}", pid);
		return priceLevelAccountProductGroupRepository.findOneByPid(pid).map(priceLevelAccountProductGroup -> {
			PriceLevelAccountProductGroupDTO priceLevelAccountProductGroupDTO = new PriceLevelAccountProductGroupDTO(
					priceLevelAccountProductGroup);
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
		List<PriceLevelAccountProductGroup> priceLevelAccountProductGroups = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "AP_QUERY_118" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by PidIn";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AccountProfile> accountProfiles = accountProfileRepository
				.findByPidIn(Arrays.asList(accountPids.split(",")));
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);
		List<ProductGroup> productGroups = productGroupRepository.findAllByCompanyPidAndGroupPidIn(company.getPid(),
				Arrays.asList(productGroupPids.split(",")));
		PriceLevel priceLevel = priceLevelRepository.findOneByPid(priceLevelPid).get();
		for (ProductGroup productGroup : productGroups) {
			for (AccountProfile accountProfile : accountProfiles) {
				PriceLevelAccountProductGroup priceLevelAccountProductGroup = new PriceLevelAccountProductGroup();
				priceLevelAccountProductGroup.setAccountProfile(accountProfile);
				priceLevelAccountProductGroup.setCompany(company);
				priceLevelAccountProductGroup
						.setPid(PriceLevelAccountProductGroupService.PID_PREFIX + RandomUtil.generatePid());
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
		List<PriceLevelDTO> priceLevelDTOs = new ArrayList<>();
		List<PriceLevelDTO> newList = new ArrayList<PriceLevelDTO>();
		if (userAccountProfile.isPresent()) {
			List<PriceLevel> priceLevels = priceLevelAccountProductGroupRepository
					.findAllByAccountProfile(userAccountProfile.get().getAccountProfile().getPid());
			priceLevelDTOs = priceLevelMapper.priceLevelsToPriceLevelDTOs(priceLevels);

			List<PriceLevelListDTO> priceLevelListDTos = new ArrayList<>();

			for (PriceLevelDTO priceLevelDTO : priceLevelDTOs) {
				PriceLevelListDTO priceLevelListDTO = new PriceLevelListDTO();
				priceLevelListDTO.setPriceLevelName(priceLevelDTO.getName());
				priceLevelListDTO.setPriceLevelPid(priceLevelDTO.getPid());
				priceLevelListDTO.setPrice(0);
				priceLevelListDTos.add(priceLevelListDTO);
				priceLevelDTO.setLevelListDTOs(priceLevelListDTos);
				newList.add(priceLevelDTO);
			}

//			priceLevelDTOs.forEach(priceLevelDTO -> priceLevelDTO
//					.setLevelListDTOs(priceLevelListService.findAllByCompanyIdAndPriceLevelPid(priceLevelDTO.getPid())));
		}
		return newList;
	}

}
