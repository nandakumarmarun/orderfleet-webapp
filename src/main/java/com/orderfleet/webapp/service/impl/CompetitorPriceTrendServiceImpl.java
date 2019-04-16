package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.CompetitorPriceTrendRepository;
import com.orderfleet.webapp.repository.CompetitorProfileRepository;
import com.orderfleet.webapp.repository.PriceTrendProductGroupRepository;
import com.orderfleet.webapp.repository.PriceTrendProductRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.CompetitorPriceTrendService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendDTO;
import com.orderfleet.webapp.web.rest.mapper.CompetitorPriceTrendMapper;

/**
 * Service Implementation for managing CompetitorPriceTrend.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
@Service
@Transactional
public class CompetitorPriceTrendServiceImpl implements CompetitorPriceTrendService {

	private final Logger log = LoggerFactory.getLogger(CompetitorPriceTrendServiceImpl.class);

	@Inject
	private CompetitorPriceTrendRepository competitorPriceTrendRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private CompetitorPriceTrendMapper competitorPriceTrendMapper;

	@Inject
	private PriceTrendProductRepository priceTrendProductRepository;

	@Inject
	private PriceTrendProductGroupRepository priceTrendProductGroupRepository;

	@Inject
	private CompetitorProfileRepository competitorProfileRepository;
	
	@Inject
	private UserRepository userRepository;

	/**
	 * Save a competitorPriceTrend.
	 * 
	 * @param competitorPriceTrendDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(List<CompetitorPriceTrendDTO> competitorPriceTrendDTOs) {
		log.debug("Request to save CompetitorPriceTrend");
		if (competitorPriceTrendDTOs != null && competitorPriceTrendDTOs.size() > 0) {
			List<CompetitorPriceTrend> competitorPriceTrends = new ArrayList<>();
			for (CompetitorPriceTrendDTO competitorPriceTrendDTO : competitorPriceTrendDTOs) {
				//validate only one exist a date,user,compitator,product
				//if exist update else insert
				LocalDate currentDate = LocalDate.now();
				CompetitorPriceTrend competitorPriceTrend;
				List<CompetitorPriceTrend> existCompetitorPriceTrends = competitorPriceTrendRepository.findByLoginCompetitorPidProductPidAndDateBetween(SecurityUtils.getCurrentUserLogin(),competitorPriceTrendDTO.getCompetitorProfilePid(),competitorPriceTrendDTO.getPriceTrendProductPid(),currentDate.atTime(0, 0), currentDate.atTime(23, 59));
				if(existCompetitorPriceTrends.isEmpty()){
					// set pid
					competitorPriceTrendDTO.setPid(CompetitorPriceTrendService.PID_PREFIX + RandomUtil.generatePid());
					competitorPriceTrend = competitorPriceTrendMapper
							.competitorPriceTrendDTOToCompetitorPriceTrend(competitorPriceTrendDTO);
					competitorPriceTrend.setPriceTrendProductGroup(priceTrendProductGroupRepository
							.findOneByPid(competitorPriceTrendDTO.getPriceTrendProductGroupPid()).get());
					competitorPriceTrend.setPriceTrendProduct(priceTrendProductRepository
							.findOneByPid(competitorPriceTrendDTO.getPriceTrendProductPid()).get());
					competitorPriceTrend.setCompetitorProfile(competitorProfileRepository
							.findOneByPid(competitorPriceTrendDTO.getCompetitorProfilePid()).get());
					competitorPriceTrend.setCreatedDate(LocalDateTime.now());
					// set company
					User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
					competitorPriceTrend.setUser(user);
					competitorPriceTrend.setCompany(user.getCompany());
					
					
					// set company
					competitorPriceTrend.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
					competitorPriceTrends.add(competitorPriceTrend);
				} else {
					competitorPriceTrend = existCompetitorPriceTrends.get(0);
					competitorPriceTrend.setPriceTrendProductGroup(priceTrendProductGroupRepository
							.findOneByPid(competitorPriceTrendDTO.getPriceTrendProductGroupPid()).get());
					competitorPriceTrend.setPriceTrendProduct(priceTrendProductRepository
							.findOneByPid(competitorPriceTrendDTO.getPriceTrendProductPid()).get());
					competitorPriceTrend.setCreatedDate(LocalDateTime.now());
					competitorPriceTrend.setPrice1(competitorPriceTrendDTO.getPrice1());
					competitorPriceTrend.setPrice2(competitorPriceTrendDTO.getPrice2());
					competitorPriceTrend.setPrice3(competitorPriceTrendDTO.getPrice3());
					competitorPriceTrend.setPrice4(competitorPriceTrendDTO.getPrice4());
					competitorPriceTrend.setPrice5(competitorPriceTrendDTO.getPrice5());
					competitorPriceTrend.setRemarks(competitorPriceTrendDTO.getRemarks());
				}
				competitorPriceTrends.add(competitorPriceTrend);
			}
			competitorPriceTrends = competitorPriceTrendRepository.save(competitorPriceTrends);
		} else {
			log.debug("CompetitorPriceTrends is empty");
		}
	}

	/**
	 * Get all the competitorPriceTrends.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<CompetitorPriceTrend> findAll(Pageable pageable) {
		return competitorPriceTrendRepository.findAll(pageable);
	}

	@Override
	public List<CompetitorPriceTrendDTO> findAllByCompany() {
		log.debug("Request to get all CompetitorPriceTrends");
		List<CompetitorPriceTrend> competitorPriceTrendList = competitorPriceTrendRepository.findAllByCompanyId();
		return competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrendList);
	}

	/**
	 * Get all the competitorPriceTrends.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<CompetitorPriceTrendDTO> findAllByCompany(Pageable pageable) {
		log.debug("Request to get all CompetitorPriceTrends");
		Page<CompetitorPriceTrend> competitorPriceTrends = competitorPriceTrendRepository.findAllByCompanyId(pageable);
		return new PageImpl<>(competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends.getContent()), pageable,
				competitorPriceTrends.getTotalElements());
	}

	/**
	 * Get one competitorPriceTrend by id.
	 *
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public CompetitorPriceTrendDTO findOne(Long id) {
		log.debug("Request to get CompetitorPriceTrend : {}", id);
		CompetitorPriceTrend competitorPriceTrend = competitorPriceTrendRepository.findOne(id);
		return competitorPriceTrendMapper
				.competitorPriceTrendToCompetitorPriceTrendDTO(competitorPriceTrend);
	}

	/**
	 * Get one competitorPriceTrend by pid.
	 *
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	@Override
	@Transactional(readOnly = true)
	public Optional<CompetitorPriceTrendDTO> findOneByPid(String pid) {
		log.debug("Request to get CompetitorPriceTrend by pid : {}", pid);
		return competitorPriceTrendRepository.findOneByPid(pid).map(competitorPriceTrend ->  competitorPriceTrendMapper
					.competitorPriceTrendToCompetitorPriceTrendDTO(competitorPriceTrend)
		);
	}

	@Override
	public CompetitorPriceTrend findByProductPidAndCompetitorPidAndDateBetween(String priceTrendProductPid,
			String competitorPid, LocalDateTime fromDate, LocalDateTime toDate) {
		return competitorPriceTrendRepository
				.findTop1ByPriceTrendProductPidAndCompetitorProfilePidAndCreatedDateBetweenOrderByCreatedDateDesc(
						priceTrendProductPid, competitorPid, fromDate, toDate);
	}

	@Override
	public List<CompetitorPriceTrendDTO> findByProductProductGroupCompetitor(List<String> productPids,
			List<String> productgroupPids, List<String> competitorPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndProductGroupPidInAndCompetitorPidIn(productPids, productgroupPids, competitorPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductPidIn(List<String> productPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidIn(productPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductGroupPidIn(List<String> productGroupPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductGroupPidIn(productGroupPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByCompetitorPidIn(List<String> competitorPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByCompetitorPidIn(competitorPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductPidInAndProductGroupPidIn(List<String> productPids,
			List<String> productGroupPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndProductGroupPidIn(productPids, productGroupPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductGroupPidInAndCompetitorPidIn(List<String> productGroupPids,
			List<String> competitorPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductGroupPidInAndCompetitorPidIn(productGroupPids, competitorPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductPidInAndCompetitorPidIn(List<String> productPids,
			List<String> competitorPids) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndCompetitorPidIn(productPids, competitorPids);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByUserAndCreatedDate(String userPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByUserAndCreatedDateBetWeen(userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findByProductProductGroupCompetitorAndUserAndCreatedDate(List<String> productPids,
			List<String> productgroupPids, List<String> competitorPids, String userPid,LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndProductGroupPidInAndCompetitorPidInAndUserAndCreatedDateBetween(productPids, productgroupPids, competitorPids, userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductPidInAndUserAndCreatedDate(List<String> productPids,String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndUserAndCreatedDateBetween(productPids,userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductGroupPidInAndUserAndCreatedDate(List<String> productGroupPids,String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductGroupPidInAndUserAndCreatedDateBetween(productGroupPids,userPid,fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByCompetitorPidInAndUserAndCreatedDate(List<String> competitorPids,String userPid,
			LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByCompetitorPidInAndUserAndCreatedDateBetween(competitorPids,userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductPidInAndProductGroupPidInAndUserAndCreatedDate(
			List<String> productPids, List<String> productGroupPids,String userPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndProductGroupPidInAndUserAndCreatedDateBetween(productPids, productGroupPids,userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductGroupPidInAndCompetitorPidInAndUserAndCreatedDate(
			List<String> productGroupPids, List<String> competitorPids,String userPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductGroupPidInAndCompetitorPidInAndUserAndCreatedDateBetween(productGroupPids, competitorPids,userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

	@Override
	public List<CompetitorPriceTrendDTO> findProductByProductPidInAndCompetitorPidInAndUserAndCreatedDate(
			List<String> productPids, List<String> competitorPids,String userPid, LocalDateTime fromDate, LocalDateTime toDate) {
		List<CompetitorPriceTrend>competitorPriceTrends=competitorPriceTrendRepository.findProductByProductPidInAndCompetitorPidInAndUserAndCreatedDateBetween(productPids, competitorPids,userPid, fromDate, toDate);
		List<CompetitorPriceTrendDTO> result = competitorPriceTrendMapper
				.competitorPriceTrendsToCompetitorPriceTrendDTOs(competitorPriceTrends);
		return result;
	}

}
