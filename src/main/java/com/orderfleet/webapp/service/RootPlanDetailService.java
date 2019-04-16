package com.orderfleet.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.web.rest.dto.RootPlanDetailDTO;

public interface RootPlanDetailService {

	String PID_PREFIX = "RPDL-";

	/**
	 * Get all the rootPlanDetails.
	 * 
	 * @return the list of entities
	 */
	List<RootPlanDetailDTO> findAllByCompany();

	/**
	 * Save a rootPlanDetail.
	 * 
	 * @param rootPlanDetailDTO
	 *            the entity to save
	 * @return the persisted entity
	 */
	void save(List<RootPlanDetailDTO> rootPlanDetailDTOs);

	List<RootPlanDetailDTO> findAllByRootPlanHeaderPid(String pid);

	void changeApprovalStatusDownload(RootPlanDetail rootPlanDetail);
	
	void revokeRoutePlan(RootPlanDetail rootPlanDetail);

	List<RootPlanDetailDTO> findAllByUserPidAndDownloadDateBetween(String userpid, LocalDateTime startdownloadDate,
			LocalDateTime endDownloadDate);
	
	Optional<RootPlanDetail> findOneByPid(String pid);
}
