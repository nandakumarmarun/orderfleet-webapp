package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Attendance;
import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.domain.CompanyConfiguration;
import com.orderfleet.webapp.domain.DashboardAttendance;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.RootPlanSubgroupApprove;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.geolocation.api.GeoLocationServiceException;
import com.orderfleet.webapp.repository.AttendanceRepository;
import com.orderfleet.webapp.repository.AttendanceSubgroupApprovalRequestRepository;
import com.orderfleet.webapp.repository.CompanyConfigurationRepository;
import com.orderfleet.webapp.repository.DashboardAttendanceUserRepository;
import com.orderfleet.webapp.repository.RootPlanSubgroupApproveRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.KilometreCalculationService;
import com.orderfleet.webapp.service.PunchOutService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.dto.AttendanceDTO;
import com.orderfleet.webapp.web.rest.dto.KilometerCalculationDTO;
import com.orderfleet.webapp.web.rest.dto.PunchOutDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;
import com.orderfleet.webapp.web.websocket.dto.ActivityDTO;

/**
 * REST controller for managing Attendance.
 * 
 * @author Muhammed Riyas T
 * @since October 15, 2016
 */
@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AttendanceController {

	private final Logger log = LoggerFactory.getLogger(AttendanceController.class);

	@Inject
	private AttendanceService attendanceService;

	@Inject
	private AttendanceRepository attendanceRepository;

	@Inject
	private KilometreCalculationService kilometreCalculationService;

	@Inject
	private PunchOutService punchOutService;

	@Inject
	private SimpMessagingTemplate simpMessagingTemplate;

	@Inject
	private DashboardAttendanceUserRepository dashboardAttendanceUserRepository;

	@Inject
	private RootPlanSubgroupApproveRepository rootPlanSubgroupApproveRepository;

	@Inject
	private AttendanceSubgroupApprovalRequestRepository attendanceSubgroupApprovalRequestRepository;

	@Inject
	private CompanyConfigurationRepository companyConfigurationRepository;

	@Inject
	private FileManagerService fileManagerService;

	/**
	 * POST /attendance : mark attendance.
	 * 
	 * @param attendanceDTO the attendanceDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body thex
	 */
	@RequestMapping(value = "/attendance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> attendance(@RequestBody AttendanceDTO attendanceDTO, Principal principal) {
		log.debug("Rest request to save attendance : {}", attendanceDTO);
		try {
			Attendance attendance = attendanceService.saveAttendance(attendanceDTO);

			Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
					.findByCompanyPidAndName(attendance.getCompany().getPid(), CompanyConfig.DISTANCE_TRAVELED);
			if (optDistanceTraveled.isPresent()) {
				if (Boolean.valueOf(optDistanceTraveled.get().getValue())) {
					log.info("Update Distance travelled");
					// saveUpdate distance
					saveKilometreDifference(attendance);
				}
			}

			if (attendance.getAttendanceStatusSubgroup() != null) {
				// Save for approval if approval required status equal to true.
				List<RootPlanSubgroupApprove> optionalRootPlanSubGroupApproval = rootPlanSubgroupApproveRepository
						.findByUserLoginAndAttendanceStatusSubgroupId(SecurityUtils.getCurrentUserLogin(),
								attendance.getAttendanceStatusSubgroup().getId());
				if (!optionalRootPlanSubGroupApproval.isEmpty()) {
					RootPlanSubgroupApprove rootPlanSubgroupApprove = optionalRootPlanSubGroupApproval
							.get(optionalRootPlanSubGroupApproval.size() - 1);
					if (rootPlanSubgroupApprove.getApprovalRequired()) {
						// save for approval
						AttendanceSubgroupApprovalRequest asApprovalRequest = new AttendanceSubgroupApprovalRequest();
						asApprovalRequest.setCompany(attendance.getCompany());
						asApprovalRequest.setRequestedUser(attendance.getUser());
						asApprovalRequest.setAttendanceStatusSubgroup(attendance.getAttendanceStatusSubgroup());
						asApprovalRequest.setApprovalStatus(ApprovalStatus.REQUEST_FOR_APPROVAL);
						asApprovalRequest.setRequestedDate(attendance.getPlannedDate());
						attendanceSubgroupApprovalRequestRepository.save(asApprovalRequest);

						return new ResponseEntity<>(HttpStatus.CREATED);
					}
				}
			}
			// mark attendance in dash board view
			ActivityDTO activityDTO = new ActivityDTO();
			activityDTO.setUserPid(attendance.getUser().getPid());
			activityDTO.setRemarks(attendance.getRemarks());
			activityDTO.setTime(attendance.getPlannedDate().toString());
			if (attendance.getAttendanceStatusSubgroup() != null) {
				// dash board item configured to user
				List<DashboardAttendance> dashboardAttendances = dashboardAttendanceUserRepository
						.findDashboardAttendanceByUserLogin(SecurityUtils.getCurrentUserLogin());
				long dashBoardItemId = 0;
				for (DashboardAttendance dashboardAttendance : dashboardAttendances) {
					if (dashboardAttendance.getAttendanceStatusSubgroup() != null
							&& (dashboardAttendance.getAttendanceStatusSubgroup().getId() == attendance
									.getAttendanceStatusSubgroup().getId())) {
						dashBoardItemId = dashboardAttendance.getId();
						break;
					}
				}
				activityDTO.setDashboardItemId(dashBoardItemId);
				activityDTO.setAttendanceSubGroupName(attendance.getAttendanceStatusSubgroup().getName());
				activityDTO.setAttendanceSubGroupCode(attendance.getAttendanceStatusSubgroup().getCode());
			}
			simpMessagingTemplate.convertAndSend(
					"/live-tracking/attendance/" + SecurityUtils.getCurrentUsersCompanyId(), activityDTO);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@Transactional
	@RequestMapping(value = "/upload/attendanceImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadAttendanceImageFile(@RequestParam("imageRefNo") String imageRefNo,
			@RequestParam("file") MultipartFile file) {
		log.debug("Request Attendance Image to upload a file : {}", file);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		return attendanceRepository.findOneByImageRefNo(imageRefNo).map(attendance -> {
			try {
				File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
						file.getOriginalFilename(), file.getContentType());
				// update filledForm with file
				attendance.getFiles().add(uploadedFile);
				attendanceRepository.save(attendance);
				log.debug("uploaded file for Attendance: {}", attendance);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (FileManagerException | IOException ex) {
				log.debug("File upload exception : {}", ex.getMessage());
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
			}
		}).orElse(ResponseEntity.badRequest()
				.headers(HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
				.body(null));
	}

	@RequestMapping(value = "/update-attendance-status-subgroup", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> changeSubgroup(@RequestParam Long subgroupId) {

		Attendance attendance = attendanceService.update(subgroupId);

		if (attendance.getAttendanceStatusSubgroup() != null) {
			// Save for approval if approval required status equal to true.
			List<RootPlanSubgroupApprove> optionalRootPlanSubGroupApproval = rootPlanSubgroupApproveRepository
					.findByUserLoginAndAttendanceStatusSubgroupId(SecurityUtils.getCurrentUserLogin(),
							attendance.getAttendanceStatusSubgroup().getId());

			if (!optionalRootPlanSubGroupApproval.isEmpty()) {

				RootPlanSubgroupApprove rootPlanSubgroupApprove = optionalRootPlanSubGroupApproval
						.get(optionalRootPlanSubGroupApproval.size() - 1);
				if (rootPlanSubgroupApprove.getApprovalRequired()) {
					// save for approval
					AttendanceSubgroupApprovalRequest asApprovalRequest = new AttendanceSubgroupApprovalRequest();
					asApprovalRequest.setCompany(attendance.getCompany());
					asApprovalRequest.setRequestedUser(attendance.getUser());
					asApprovalRequest.setAttendanceStatusSubgroup(attendance.getAttendanceStatusSubgroup());
					asApprovalRequest.setApprovalStatus(ApprovalStatus.REQUEST_FOR_APPROVAL);
					asApprovalRequest.setRequestedDate(attendance.getPlannedDate());
					attendanceSubgroupApprovalRequestRepository.save(asApprovalRequest);

				}
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);

	}

	/**
	 * POST /punchout : mark punchout.
	 * 
	 * @param punchOutDTO the punchOutDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body thex
	 */
	@RequestMapping(value = "/punchOut", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> punchOut(@RequestBody PunchOutDTO punchOutDTO) {
		log.debug("Rest request to save punchOut : {}", punchOutDTO);
		try {
			punchOutService.savePunchOut(punchOutDTO);
			return new ResponseEntity<>(HttpStatus.CREATED);

		} catch (GeoLocationServiceException e) {
			log.error("Gelocation service exception :---"+e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.error("Exception service exception :---"+e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

	}

	private void saveKilometreDifference(Attendance attendance) {
		KilometerCalculationDTO kiloCalDTO = new KilometerCalculationDTO();
		kiloCalDTO.setKilometre(0.0);
		kiloCalDTO.setMetres(0.0);
		kiloCalDTO.setUserPid(attendance.getUser().getPid());
		kiloCalDTO.setUserName(attendance.getUser().getFirstName());
		kiloCalDTO.setDate(attendance.getCreatedDate().toLocalDate());
		kiloCalDTO.setStartLocation(attendance.getLocation());
		kiloCalDTO.setEndLocation(attendance.getLocation());
		kiloCalDTO.setTaskExecutionPid(null);
		kilometreCalculationService.save(kiloCalDTO, attendance.getCompany().getId());
	}
}
