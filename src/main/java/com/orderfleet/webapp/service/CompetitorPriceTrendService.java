package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.orderfleet.webapp.domain.CompetitorPriceTrend;
import com.orderfleet.webapp.web.rest.dto.CompetitorPriceTrendDTO;

/**
 * Service Interface for managing CompetitorPriceTrend.
 * 
 * @author Muhammed Riyas T
 * @since August 26, 2016
 */
public interface CompetitorPriceTrendService {

	String PID_PREFIX = "CMTP-";

	/**
	 * Save list of competitorPriceTrend.
	 * 
	 * @param competitorPriceTrendDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(List<CompetitorPriceTrendDTO> competitorPriceTrends);

	/**
	 * Get all the competitorPriceTrends.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<CompetitorPriceTrend> findAll(Pageable pageable);

	/**
	 * Get all the competitorPriceTrends.
	 * 
	 * @return the list of entities
	 */
	List<CompetitorPriceTrendDTO> findAllByCompany();

	/**
	 * Get all the competitorPriceTrends of a company.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	Page<CompetitorPriceTrendDTO> findAllByCompany(Pageable pageable);

	CompetitorPriceTrend findByProductPidAndCompetitorPidAndDateBetween(String priceTrendProductPid,
			String competitorPid, LocalDateTime fromDate, LocalDateTime toDate);

	/**
	 * Get the "id" competitorPriceTrend.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	CompetitorPriceTrendDTO findOne(Long id);

	/**
	 * Get the competitorPriceTrend by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<CompetitorPriceTrendDTO> findOneByPid(String pid);

	List<CompetitorPriceTrendDTO> findByProductProductGroupCompetitor(List<String> productPids, List<String> productgroupPids,List<String> competitorPids);
	
	List<CompetitorPriceTrendDTO> findProductByProductPidIn(List<String> productPids);
	
	List<CompetitorPriceTrendDTO> findProductByProductGroupPidIn(List<String> productGroupPids);
	
	List<CompetitorPriceTrendDTO> findProductByCompetitorPidIn(List<String> competitorPids);
	
	List<CompetitorPriceTrendDTO> findProductByProductPidInAndProductGroupPidIn(List<String> productPids,
			List<String> productGroupPids);
	
	List<CompetitorPriceTrendDTO> findProductByProductGroupPidInAndCompetitorPidIn(List<String> productGroupPids,
			List<String> competitorPids);
	
	List<CompetitorPriceTrendDTO> findProductByProductPidInAndCompetitorPidIn(List<String> productPids,
			List<String> competitorPids);
	
	List<CompetitorPriceTrendDTO> findProductByUserAndCreatedDate(String userPid,LocalDateTime fromDate, LocalDateTime toDate);

	List<CompetitorPriceTrendDTO> findByProductProductGroupCompetitorAndUserAndCreatedDate(List<String> productPids, List<String> productgroupPids,List<String> competitorPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	List<CompetitorPriceTrendDTO> findProductByProductPidInAndUserAndCreatedDate(List<String> productPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	List<CompetitorPriceTrendDTO> findProductByProductGroupPidInAndUserAndCreatedDate(List<String> productGroupPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	List<CompetitorPriceTrendDTO> findProductByCompetitorPidInAndUserAndCreatedDate(List<String> competitorPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	List<CompetitorPriceTrendDTO> findProductByProductPidInAndProductGroupPidInAndUserAndCreatedDate(List<String> productPids,
			List<String> productGroupPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	List<CompetitorPriceTrendDTO> findProductByProductGroupPidInAndCompetitorPidInAndUserAndCreatedDate(List<String> productGroupPids,
			List<String> competitorPids,String userPid,LocalDateTime fromDate, LocalDateTime toDate);
	
	List<CompetitorPriceTrendDTO> findProductByProductPidInAndCompetitorPidInAndUserAndCreatedDate(List<String> productPids,
			List<String> competitorPids,String userPid, LocalDateTime fromDate, LocalDateTime toDate);
	
}
