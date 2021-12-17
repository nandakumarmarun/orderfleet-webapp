package com.orderfleet.webapp.web.rest.api;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.RootPlanApprovalRequest;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.repository.AttendanceSubgroupApprovalRequestRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RootPlanApprovalRequestRepository;
import com.orderfleet.webapp.repository.RootPlanDetailRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class RootPlanController {

	private final Logger log = LoggerFactory.getLogger(RootPlanController.class);
	 private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private RootPlanDetailRepository rootPlanDetailRepository;
	
	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private RootPlanApprovalRequestRepository rootPlanApprovalRequestRepository;
	
	@Inject
	private AttendanceSubgroupApprovalRequestRepository attendanceSubgroupApprovalRequestRepository;
	
	@Timed
	@Transactional
	@RequestMapping(value = "/root-plan/request-for-approval", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> requsetForApproval(@RequestParam Long rootPlanId,@RequestParam(required = false, name = "date") LocalDate date) throws URISyntaxException {
		log.debug("Web request to Approve a TaskList");
		RootPlanDetail rootPlanDetail=rootPlanDetailRepository.findOneById(rootPlanId);
		if(rootPlanDetail!=null) {
			RootPlanApprovalRequest rootPlanApprovalRequest=new RootPlanApprovalRequest();
			rootPlanDetail.setApprovalStatus(ApprovalStatus.REQUEST_FOR_APPROVAL);
			rootPlanApprovalRequest.setApprovalStatus(ApprovalStatus.REQUEST_FOR_APPROVAL);
			User requestUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
			rootPlanApprovalRequest.setRequestUser(requestUser);
			rootPlanApprovalRequest.setRequestUserMessage(null);
			rootPlanApprovalRequest.setPlannedDate(date);
			Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
			rootPlanApprovalRequest.setCompany(company);
			rootPlanApprovalRequest.setRootPlanDetail(rootPlanDetail);
			rootPlanApprovalRequest.setRequestedDate(LocalDateTime.now());
			rootPlanApprovalRequestRepository.save(rootPlanApprovalRequest);
			rootPlanDetailRepository.save(rootPlanDetail);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//If approval required config is true, then check approval status by calling this method
	@Timed
	@RequestMapping(value = "/attendance-subgroup-approval-status", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkAttendanceSubgroupApprovalStatus(@RequestParam Long subgroupId, @RequestParam String subgroupName) {
		LocalDate currentDate = LocalDate.now();
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "ASAR_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get by user login and AttendanceStatusSubgroupId";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<AttendanceSubgroupApprovalRequest> optionalApprovalRequest = attendanceSubgroupApprovalRequestRepository.findByUserLoginAndAttendanceStatusSubgroupIdAndRequestedDateIsCurrentDate(SecurityUtils.getCurrentUserLogin(), subgroupId, currentDate.atTime(0, 0), currentDate.atTime(23, 59));
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

		ApprovalStatus approvalStatus =ApprovalStatus.REQUEST_FOR_APPROVAL;
		if (!optionalApprovalRequest.isEmpty()) {
			AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest=optionalApprovalRequest.get(optionalApprovalRequest.size() -1);
			approvalStatus=attendanceSubgroupApprovalRequest.getApprovalStatus();
		}
		//check with current date
		return new ResponseEntity<>(approvalStatus.toString(),HttpStatus.OK);
	}
	
		
}
