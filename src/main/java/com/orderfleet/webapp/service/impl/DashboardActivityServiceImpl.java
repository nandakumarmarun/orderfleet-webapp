package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.DashboardActivity;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardActivityRepository;
import com.orderfleet.webapp.repository.ActivityRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardActivityService;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.mapper.ActivityMapper;

/**
 * Service Implementation for managing DashboardActivity.
 * 
 * @author Sarath
 * @since Oct 27, 2016
 */
@Service
@Transactional
public class DashboardActivityServiceImpl implements DashboardActivityService {

	private final Logger log = LoggerFactory.getLogger(DashboardActivityServiceImpl.class);

	@Inject
	private DashboardActivityRepository dashboardActivityRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private ActivityRepository activityRepository;

	@Inject
	private ActivityMapper activityMapper;

	/**
	 * Save a dashboardActivity.
	 * 
	 * @param activitiesPids
	 *            the entity to save
	 * @return the persisted entity
	 */
	@Override
	public void save(List<String> activitiesPids) {
		log.debug("Request to save DashboardActivity : {}", activitiesPids);
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		List<DashboardActivity> dashboardActivities = new ArrayList<>();
		for (String activityPid : activitiesPids) {
			DashboardActivity dashboardActivity = new DashboardActivity();
			dashboardActivity.setActivity(activityRepository.findOneByPid(activityPid).get());
			dashboardActivity.setCompany(company);
			dashboardActivities.add(dashboardActivity);
		}
		dashboardActivityRepository.deleteDashboardActivities();
		dashboardActivityRepository.save(dashboardActivities);
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityDTO> findActivitiesByCompanyId() {
		log.debug("Request to get all DashboardActivities");
		List<Activity> dashboardActivityList = dashboardActivityRepository.findActivitiesByCompanyId();
		List<ActivityDTO> result = activityMapper.activitiesToActivityDTOs(dashboardActivityList);
		return result;
	}

	/**
	 * Get all the dashboardActivities.
	 * 
	 * @param pageable
	 *            the pagination information
	 * @return the list of entities
	 */
	@Override
	@Transactional(readOnly = true)
	public Page<ActivityDTO> findActivitiesByCompanyId(Pageable pageable) {
		log.debug("Request to get all DashboardActivities");
		Page<Activity> dashboardActivities = dashboardActivityRepository.findActivitiesByCompanyId(pageable);
		Page<ActivityDTO> result = new PageImpl<ActivityDTO>(
				activityMapper.activitiesToActivityDTOs(dashboardActivities.getContent()), pageable,
				dashboardActivities.getTotalElements());
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public Long countByCompanyId() {
		return dashboardActivityRepository.countByCompanyId();
	}
}
