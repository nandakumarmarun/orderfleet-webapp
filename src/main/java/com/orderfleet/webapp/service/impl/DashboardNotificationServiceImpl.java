package com.orderfleet.webapp.service.impl;

import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DashboardNotification;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.repository.DashboardNotificationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DashboardNotificationService;

/**
 * Service Implementation for managing DashboardNotification.
 * 
 * @author Shaheer
 * @since January 11, 2017
 */
@Service
@Transactional
public class DashboardNotificationServiceImpl implements DashboardNotificationService {

	private static final Logger log = LoggerFactory.getLogger(DashboardNotificationServiceImpl.class);

	@Inject
	DashboardNotificationRepository dashboardNotificationRepository;

	@Override
	public DashboardNotification saveDashboardNotification( NotificationMessageType notificationType,ExecutiveTaskPlan executiveTaskPlan,boolean dayClose) {
		DashboardNotification dashboardNotification = new DashboardNotification();
		dashboardNotification.setCreatedBy(SecurityUtils.getCurrentUserLogin());
		dashboardNotification.setMessage(executiveTaskPlan.getUserRemarks());
		dashboardNotification.setNotificationType(notificationType);
		dashboardNotification.setCompany(executiveTaskPlan.getCompany());
		dashboardNotification.setDependentId(executiveTaskPlan.getId());
		if(dayClose) {
			dashboardNotification.setCreatedDate(executiveTaskPlan.getPlannedDate());
		}
		return dashboardNotificationRepository.save(dashboardNotification);
	}

	@Override
	public void updateReadById(Long id) {
		Optional.of(dashboardNotificationRepository.findOne(id)).ifPresent(dn -> {
			dn.setRead(true);
			dashboardNotificationRepository.save(dn);
			log.debug("Changed Information for DashboardNotification: {}", dn);
		});
	}

}
