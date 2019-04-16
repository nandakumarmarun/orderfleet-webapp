package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Activity;
import com.orderfleet.webapp.domain.ReceivablePayableColumnConfig;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ReceivablePayableColumnConfigRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.ActivityNotificationService;
import com.orderfleet.webapp.service.DayPlanExecutionConfigService;
import com.orderfleet.webapp.service.GuidedSellingConfigService;
import com.orderfleet.webapp.service.MobileConfigurationService;
import com.orderfleet.webapp.service.UserFavouriteDocumentService;
import com.orderfleet.webapp.web.rest.api.dto.ActivityNotificationDTO;
import com.orderfleet.webapp.web.rest.api.dto.UserTargetAchievedDTO;
import com.orderfleet.webapp.web.rest.dto.DayPlanExecutionConfigDTO;
import com.orderfleet.webapp.web.rest.dto.GuidedSellingConfigDTO;
import com.orderfleet.webapp.web.rest.dto.MobileConfigurationDTO;
import com.orderfleet.webapp.web.rest.dto.MobileSettingsDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableColumnConfigDTO;
import com.orderfleet.webapp.web.rest.dto.UserFavouriteDocumentDTO;

/**
 * REST controller for serve Mobile Settings.
 *
 * @author Muhammed Riyas T
 * @since Jan 10, 2017
 */
@RestController
@RequestMapping("/api")
public class MobileSettingsController {

	private final Logger log = LoggerFactory.getLogger(MasterDataController.class);

	private final DayPlanExecutionConfigService dayPlanExecutionConfigService;

	private final GuidedSellingConfigService guidedSellingConfigService;

	private final UserFavouriteDocumentService userFavouriteDocumentService;

	private final MobileConfigurationService mobileConfigurationService;

	private final ActivityNotificationService activityNotificationService;

	private final UserRepository userRepository;

	private final ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	private final UserActivityRepository userActivityRepository;
	
	@Inject
	private ReceivablePayableColumnConfigRepository receivablePayableColumnConfigRepository;

	public MobileSettingsController(DayPlanExecutionConfigService dayPlanExecutionConfigService,
			GuidedSellingConfigService guidedSellingConfigService,
			UserFavouriteDocumentService userFavouriteDocumentService,
			MobileConfigurationService mobileConfigurationService,
			ActivityNotificationService activityNotificationService, UserRepository userRepository,
			ExecutiveTaskExecutionRepository executiveTaskExecutionRepository,
			UserActivityRepository userActivityRepository) {
		super();
		this.dayPlanExecutionConfigService = dayPlanExecutionConfigService;
		this.guidedSellingConfigService = guidedSellingConfigService;
		this.userFavouriteDocumentService = userFavouriteDocumentService;
		this.mobileConfigurationService = mobileConfigurationService;
		this.activityNotificationService = activityNotificationService;
		this.userRepository = userRepository;
		this.executiveTaskExecutionRepository = executiveTaskExecutionRepository;
		this.userActivityRepository = userActivityRepository;
	}

	/**
	 * GET /mobile-settings : get all mobile settings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body
	 *         MobileSettingsDTO
	 * @throws URISyntaxException
	 *             if the pagination headers couldn't be generated
	 */
	@Timed
	@GetMapping("/mobile-settings")
	public ResponseEntity<MobileSettingsDTO> getMobileSettings() throws URISyntaxException {
		log.debug("REST request to get mobile settings");
		return new ResponseEntity<>(createMobileSettingsObject(), HttpStatus.OK);
	}

	/**
	 * GET /mobile-settings : get all mobile settings.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and with body
	 *         MobileSettingsDTO
	 * @throws URISyntaxException
	 *             if the pagination headers couldn't be generated
	 */
	@Timed
	@GetMapping("/user-all-activity-count")
	public ResponseEntity<UserTargetAchievedDTO> getUserTargetAchieved() throws URISyntaxException {
		log.debug("REST request to get user all activity count");
		return new ResponseEntity<>(userAllActivityCount(), HttpStatus.OK);
	}

	private MobileSettingsDTO createMobileSettingsObject() {

		MobileSettingsDTO mobileSettingsDTO = new MobileSettingsDTO();

		Optional<MobileConfigurationDTO> mobileConfigurationDTO = mobileConfigurationService.findOneByCompanyId();
		if (mobileConfigurationDTO.isPresent()) {
			mobileSettingsDTO = new MobileSettingsDTO(mobileConfigurationDTO.get());
		}

		// set day plan execution pages
		List<DayPlanExecutionConfigDTO> dayPlanExecutionConfigs = dayPlanExecutionConfigService
				.findAllByCompanyIdAndEnabledTrueCurrentUser();
		mobileSettingsDTO.setDayPlanExecutionConfigs(dayPlanExecutionConfigs);

		// set guided selling config
		GuidedSellingConfigDTO guidedSellingConfig = guidedSellingConfigService.findByCompany();
		mobileSettingsDTO.setGuidedSellingConfig(guidedSellingConfig);

		// set user favourite document
		List<UserFavouriteDocumentDTO> userFavouriteDocuments = userFavouriteDocumentService
				.findFavouriteDocumentsByUserIsCurrentUser();
		mobileSettingsDTO.setFavouriteDocuments(userFavouriteDocuments);

		// set activity notification settings
		List<ActivityNotificationDTO> activityNotificationDtos = activityNotificationService
				.findAllActivityNotificationByCompanyId();
		mobileSettingsDTO.setActivityNotificationDtos(activityNotificationDtos);

		return mobileSettingsDTO;
	}

	/**
	 * showing dashboard of mobile.day wise and mont wise. call will came from
	 * mobile settings show all activity count =true.
	 * 
	 * @return UserTargetAchievedDTO
	 */
	private UserTargetAchievedDTO userAllActivityCount() {

		String userPid = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get().getPid();

		LocalDate currentDate = LocalDate.now();
		LocalDate monthStartDate = currentDate.withDayOfMonth(1);
		LocalDate monthEndDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());

		List<Activity> activities = userActivityRepository.findActivitiesByUserPid(userPid);
		UserTargetAchievedDTO userTargetAchievedDTO = new UserTargetAchievedDTO();

		Long monthAchieved = executiveTaskExecutionRepository.countByDateBetweenAndActivities(
				monthStartDate.atTime(0, 0), monthEndDate.atTime(23, 59), activities, userPid);
		Long dayAchieved = executiveTaskExecutionRepository.countByDateBetweenAndActivities(currentDate.atTime(0, 0),
				currentDate.atTime(23, 59), activities, userPid);

		userTargetAchievedDTO.setDayAchieved(dayAchieved != 0 ? dayAchieved : 0);
		userTargetAchievedDTO.setMonthAchieved(monthAchieved != 0 ? monthAchieved : 0);

		return userTargetAchievedDTO;
	}
	
	@GetMapping("/mobile-settings/receivable-payable-column-config")
	public ResponseEntity<List<ReceivablePayableColumnConfigDTO>> getReceivablePayableColumnConfig() {
		log.debug("REST request to get ReceivablePayableColumnConfig");
		List<ReceivablePayableColumnConfig> rpColumnConfigs = receivablePayableColumnConfigRepository.findAllByCompanyId();
		if(!rpColumnConfigs.isEmpty()) {
			return new ResponseEntity<>(rpColumnConfigs.stream().map(ReceivablePayableColumnConfigDTO::new).collect(Collectors.toList()), HttpStatus.OK);	
		}
		return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
		
	}
}
