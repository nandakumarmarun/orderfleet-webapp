package com.orderfleet.webapp.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.SalesSummaryAchievment;
import com.orderfleet.webapp.web.rest.dto.SalesSummaryAchievmentDTO;

/**
 * Service Interface for managing SalesSummaryAchievment.
 * 
 * @author Muhammed Riyas T
 * @since October 17, 2016
 */
public interface SalesSummaryAchievmentService {

	String PID_PREFIX = "SSA-";

	void saveSalesSummaryAchievments(List<SalesSummaryAchievmentDTO> achievmentDTOs);

	/**
	 * Get the "id" salesSummaryAchievment.
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the entity
	 */
	SalesSummaryAchievmentDTO findOne(Long id);

	/**
	 * Get the salesSummaryAchievment by "pid".
	 * 
	 * @param pid
	 *            the pid of the entity
	 * @return the entity
	 */
	Optional<SalesSummaryAchievmentDTO> findOneByPid(String pid);

	/**
	 * Delete the "id" salesSummaryAchievment.
	 * 
	 * @param id
	 *            the id of the entity
	 */
	void delete(String pid);

	List<SalesSummaryAchievment> findByUserPidAndSalesTargetGroupPidAndDateBetween(String userPid, String salesTargetGroupPid,
			LocalDate startDate, LocalDate endDate);
	
	SalesSummaryAchievment findByUserPidAndSalesTargetGroupPidAndDateBetweenAndLocationPid(String userPid, String salesTargetGroupPid,
			LocalDate startDate, LocalDate endDate,String locationPid);

}
