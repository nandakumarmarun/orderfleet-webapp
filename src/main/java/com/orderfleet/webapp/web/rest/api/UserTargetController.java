package com.orderfleet.webapp.web.rest.api;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ActivityGroupUserTarget;
import com.orderfleet.webapp.domain.ActivityUserTarget;
import com.orderfleet.webapp.domain.enums.ActivityStatus;
import com.orderfleet.webapp.repository.ActivityGroupUserTargetRepository;
import com.orderfleet.webapp.repository.ActivityUserTargetRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.web.rest.dto.ActivityGroupUserTargetDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityUserTargetDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityGroupUserTargetMapper;
import com.orderfleet.webapp.web.rest.mapper.ActivityUserTargetMapper;

/**
 * 
 * @author Sarath
 * @since Aug 2, 2016
 */
@RestController
@RequestMapping("/api")
@Transactional(readOnly = true)
public class UserTargetController {

	private final Logger log = LoggerFactory.getLogger(UserTargetController.class);

	@Inject
	ActivityGroupUserTargetRepository activityGroupUserTargetsRepository;

	@Inject
	ActivityGroupUserTargetMapper activityGroupUserTargetMapper;

	@Inject
	ActivityUserTargetRepository activityUserTargetRepository;

	@Inject
	ActivityUserTargetMapper activityUserTargetMapper;

	@Inject
	ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	/**
	 * GET /activity-group-user-targets : get all the ActivityGroupUserTarget.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityGroupUserTargets in body
	 */
	@RequestMapping(value = "/activity-group-user-targets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ActivityGroupUserTargetDTO> getAllActivityGroupUserTargets() {
		log.debug("REST request to get all activityGroupUserTargets");
		List<ActivityGroupUserTarget> activityGroupUserTargets = activityGroupUserTargetsRepository
				.findAllByCompanyId();
		return activityGroupUserTargetMapper
				.activityGroupUserTargetsToActivityGroupUserTargetDTOs(activityGroupUserTargets);
	}

	/**
	 * GET /activity-user-targets : get all the ActivityUserTarget.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         activityUserTargets in body
	 */
	@RequestMapping(value = "/activity-user-targets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<ActivityUserTargetDTO> getActivityUserTargets() {
		log.debug("REST request to get all activityUserTargets");
		LocalDate startDate = LocalDate.now().withDayOfMonth(1);
		LocalDate endDate = LocalDate.now().withDayOfMonth(startDate.lengthOfMonth());
		List<ActivityUserTarget> activityUserTargets = activityUserTargetRepository.findByUserIsCurrentUserAndDateBetween(startDate, endDate);
		List<ActivityUserTargetDTO> listActivityUserTargetDTO = activityUserTargetMapper
				.activityUserTargetsToActivityUserTargetDTOs(activityUserTargets);
		List<ActivityUserTargetDTO> result = new ArrayList<>();
		// one month has only one user-target
		for (ActivityUserTargetDTO activityUserTargetDTO : listActivityUserTargetDTO) {
			Long achieved = executiveTaskExecutionRepository
					.countByActivityPidAndUserPidAndActivityStatusNotAndDateBetween(
							activityUserTargetDTO.getActivityPid(), activityUserTargetDTO.getUserPid(),
							ActivityStatus.REJECTED, activityUserTargetDTO.getStartDate().atTime(0, 0),
							activityUserTargetDTO.getEndDate().atTime(23, 59));
			activityUserTargetDTO.setAchivedNumber(achieved);
			result.add(activityUserTargetDTO);
		}
		return result;
	}

}
