package com.orderfleet.webapp.web.rest.api;

import java.io.IOException;
import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import com.orderfleet.webapp.async.event.EventProducer;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.geolocation.api.GeoLocationServiceException;
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
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
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
	
	@Inject
	private PunchOutRepository punchOutRepository;

	@Autowired
	private EventProducer eventProducer;

	@Inject
	private CompanyRepository companyRepository;

	/**
	 * POST /attendance : mark attendance.
	 * 
	 * @param attendanceDTO the attendanceDTO to create
	 * @return the ResponseEntity with status 201 (Created) and with body thex
	 */
	@RequestMapping(value = "/attendance", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> attendance(@RequestBody AttendanceDTO attendanceDTO, Principal principal) {
		/*
		 * try { PunchOutDTO punchOut = punchOutService.savePunchOut(punchOutDTO);
		 * 
		 * if (punchOut != null) { log.info("Saving punchOut Success"); return new
		 * ResponseEntity<>(HttpStatus.CREATED); } else {
		 * log.info("Saving punchOut Failed"); return new
		 * ResponseEntity<>(HttpStatus.CONFLICT); } } catch (GeoLocationServiceException
		 * e) { log.info("Saving punchOut failed");
		 * log.error("Gelocation service exception :---" + e.getMessage());
		 * e.printStackTrace(); return new ResponseEntity<>(HttpStatus.CONFLICT); }
		 * catch (Exception e) { log.info("Saving punchOut failed");
		 * log.error("Exception service exception :---" + e.getMessage());
		 * e.printStackTrace(); return new ResponseEntity<>(HttpStatus.CONFLICT); }
		 */

		log.debug("Rest request to save attendance : {}", attendanceDTO);

		Attendance attendance = null;

		try {

			attendance = attendanceService.saveAttendance(attendanceDTO);

			if (attendance != null) {
				log.info("Saving Attendance Success....");
			} else {
				log.info("Saving Attendance failed");
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} catch (HttpClientErrorException e) {
			log.info("Saving Attendance failed");
			log.error("HttpClientErrorException :---" + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (GeoLocationServiceException e) {
			log.info("Saving Attendance failed");
			log.error("Gelocation service exception :---" + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.info("Saving Attendane failed");
			log.error("Exception service exception :---" + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "COMP_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by compPid and name";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<CompanyConfiguration> optDistanceTraveled = companyConfigurationRepository
				.findByCompanyPidAndName(attendance.getCompany().getPid(), CompanyConfig.DISTANCE_TRAVELED);
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
				if (dashboardAttendance.getAttendanceStatusSubgroup() != null && (dashboardAttendance
						.getAttendanceStatusSubgroup().getId() == attendance.getAttendanceStatusSubgroup().getId())) {
					dashBoardItemId = dashboardAttendance.getId();
					break;
				}
			}
			activityDTO.setDashboardItemId(dashBoardItemId);
			activityDTO.setAttendanceSubGroupName(attendance.getAttendanceStatusSubgroup().getName());
			activityDTO.setAttendanceSubGroupCode(attendance.getAttendanceStatusSubgroup().getCode());
		}
		simpMessagingTemplate.convertAndSend("/live-tracking/attendance/" + SecurityUtils.getCurrentUsersCompanyId(),
				activityDTO);

				Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
				String companyPid = company.getPid();
				Optional<CompanyConfiguration> optCrm = companyConfigurationRepository
								.findByCompanyPidAndName(companyPid, CompanyConfig.CRM_ENABLE);
			if (optCrm.isPresent() && Boolean.valueOf(optCrm.get().getValue())) {
					if (attendance != null) {
							Attendance finalAttendance = attendance;
							CompletableFuture.supplyAsync(() -> {
									sendAttendanceModCApplication(finalAttendance);
									return "submitted successfully...";
								});
						}}

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
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the one attendance by image Ref no";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
	ResponseEntity<Object> upload	= attendanceRepository.findOneByImageRefNo(imageRefNo).map(attendance -> {
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
	return upload;
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
			PunchOutDTO punchOut = punchOutService.savePunchOut(punchOutDTO);

			if (punchOut != null) {
				log.info("Saving punchOut Success");
				return new ResponseEntity<>(HttpStatus.CREATED);
			} else {
				log.info("Saving punchOut Failed");
				return new ResponseEntity<>(HttpStatus.CONFLICT);
			}
		} catch (HttpClientErrorException e) {
			log.info("Saving Punch Out failed");
			log.error("HttpClientErrorException :---" + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (GeoLocationServiceException e) {
			log.info("Saving punchOut failed");
			log.error("Gelocation service exception :---" + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		} catch (Exception e) {
			log.info("Saving punchOut failed");
			log.error("Exception service exception :---" + e.getMessage());
			e.printStackTrace();
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}

	}
	
	@Transactional
	@RequestMapping(value = "/upload/punchOutImage", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> uploadpunchOutImageFile(@RequestParam("imageRefNo") String imageRefNo,
			@RequestParam("file") MultipartFile file) {
		log.debug("Request PunchOut Image to upload a file : {}", file);
		if (file.isEmpty()) {
			return ResponseEntity.badRequest()
					.headers(
							HeaderUtil.createFailureAlert("fileUpload", "Nocontent", "Invalid file upload: No content"))
					.body(null);
		}
		DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
		DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String id = "ATT_QUERY_120" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
		String description ="get the one PunchOut by image Ref no";
		LocalDateTime startLCTime = LocalDateTime.now();
		String startTime = startLCTime.format(DATE_TIME_FORMAT);
		String startDate = startLCTime.format(DATE_FORMAT);
		logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
	ResponseEntity<Object> upload	= punchOutRepository.findOneByImageRefNo(imageRefNo).map(punchout -> {
			try {
				File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
						file.getOriginalFilename(), file.getContentType());
				// update filledForm with file
				punchout.getFiles().add(uploadedFile);
				punchOutRepository.save(punchout);
				log.debug("uploaded file for PunchOut {}", punchout);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (FileManagerException | IOException ex) {
				log.debug("File upload exception : {}", ex.getMessage());
				return ResponseEntity.badRequest()
						.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage())).body(null);
			}
		}).orElse(ResponseEntity.badRequest()
				.headers(HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
				.body(null));
	return upload;
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

		public void sendAttendanceModCApplication(Attendance attendance){
				AttendanceDTO attendanceDTO = new AttendanceDTO(attendance);
				eventProducer.attendancePublish(attendanceDTO);
			}
}
