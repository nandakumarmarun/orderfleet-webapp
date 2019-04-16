package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.AttendanceSubgroupApprovalRequest;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.UserDevice;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.domain.enums.NotificationMessageType;
import com.orderfleet.webapp.domain.model.FirebaseData;
import com.orderfleet.webapp.repository.AttendanceSubgroupApprovalRequestRepository;
import com.orderfleet.webapp.repository.UserDeviceRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AttendanceSubgroupApprovalRequestService;
import com.orderfleet.webapp.service.async.FirebaseService;
import com.orderfleet.webapp.web.rest.dto.AttendanceSubgroupApprovalRequestDTO;
import com.orderfleet.webapp.web.rest.dto.FirebaseRequest;

@Service
@Transactional
public class AttendanceSubgroupApprovalRequestServiceImpl implements AttendanceSubgroupApprovalRequestService{

	private final Logger log = LoggerFactory.getLogger(AttendanceSubgroupApprovalRequestServiceImpl.class);
	
	@Inject
	private AttendanceSubgroupApprovalRequestRepository attendanceSubgroupApprovalRequestRepository;
	
	@Inject
	private UserRepository userRepository;
	
	@Inject
	private UserDeviceRepository userDeviceRepository;
	
	@Inject
	private FirebaseService firebaseService;
	
	@Override
	public List<AttendanceSubgroupApprovalRequestDTO> findAllByCompany() {
		log.debug("find all by company id");
		List<AttendanceSubgroupApprovalRequest>attendanceSubgroupApprovalRequests=attendanceSubgroupApprovalRequestRepository.findAllByCompanyId();
		List<AttendanceSubgroupApprovalRequestDTO>attendanceSubgroupApprovalRequestDTOs=new ArrayList<>();
		for(AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest:attendanceSubgroupApprovalRequests) {
			AttendanceSubgroupApprovalRequestDTO attendanceSubgroupApprovalRequestDTO=new AttendanceSubgroupApprovalRequestDTO(attendanceSubgroupApprovalRequest);
			attendanceSubgroupApprovalRequestDTOs.add(attendanceSubgroupApprovalRequestDTO);
		}
		return attendanceSubgroupApprovalRequestDTOs;
	}

	@Override
	public List<AttendanceSubgroupApprovalRequestDTO> findAllByUsers(String userPid) {
		log.debug("find all by user pid");
		List<AttendanceSubgroupApprovalRequest>attendanceSubgroupApprovalRequests=attendanceSubgroupApprovalRequestRepository.findAllByUserPid(userPid);
		List<AttendanceSubgroupApprovalRequestDTO>attendanceSubgroupApprovalRequestDTOs=new ArrayList<>();
		for(AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest:attendanceSubgroupApprovalRequests) {
			AttendanceSubgroupApprovalRequestDTO attendanceSubgroupApprovalRequestDTO=new AttendanceSubgroupApprovalRequestDTO(attendanceSubgroupApprovalRequest);
			attendanceSubgroupApprovalRequestDTOs.add(attendanceSubgroupApprovalRequestDTO);
		}
		return attendanceSubgroupApprovalRequestDTOs;
	}

	@Override
	public AttendanceSubgroupApprovalRequest approveAttendanceRequest(Long id, String message) {
		AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest=attendanceSubgroupApprovalRequestRepository.findOne(id);
		attendanceSubgroupApprovalRequest.setApprovalStatus(ApprovalStatus.APPROVED);
		attendanceSubgroupApprovalRequest.setApprovedUserMessage(message);
		attendanceSubgroupApprovalRequest.setApprovedDate(LocalDateTime.now());
		User user=userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		attendanceSubgroupApprovalRequest.setApprovedUser(user);
		attendanceSubgroupApprovalRequestRepository.save(attendanceSubgroupApprovalRequest);
		return attendanceSubgroupApprovalRequest;
	}

	@Override
	public void rejectAttendanceRequest(Long id, String message) {
		AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest=attendanceSubgroupApprovalRequestRepository.findOne(id);
		attendanceSubgroupApprovalRequest.setApprovalStatus(ApprovalStatus.REJECTED);
		attendanceSubgroupApprovalRequest.setApprovedUserMessage(message);
		attendanceSubgroupApprovalRequest.setApprovedDate(LocalDateTime.now());
		User user=userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		attendanceSubgroupApprovalRequest.setApprovedUser(user);
		attendanceSubgroupApprovalRequestRepository.save(attendanceSubgroupApprovalRequest);
	}

	@Override
	public void sendNotification(AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest) {
		UserDevice userDevice=userDeviceRepository.findByUserLoginAndActivatedTrue(attendanceSubgroupApprovalRequest.getRequestedUser().getLogin()).get();
		List<UserDevice>userDevices=new ArrayList<>();
		userDevices.add(userDevice);
		String[] usersFcmKeys=userDevices.stream().map(ud -> ud.getFcmKey()).toArray(String[]::new);
		FirebaseRequest firebaseRequest = new FirebaseRequest();
		firebaseRequest.setRegistrationIds(usersFcmKeys);
		FirebaseData data = new FirebaseData();
		data.setTitle("You have a new  ");
		data.setMessage("");
		data.setMessageType(NotificationMessageType.ATT_SG_APPROVAL);
		data.setPidUrl("");
		data.setNotificationPid("");
		data.setSentDate(LocalDateTime.now().toString());
		firebaseRequest.setData(data);
		firebaseService.sendNotificationToUsers(firebaseRequest, userDevices,SecurityUtils.getCurrentUserLogin());
	}

	@Override
	public List<AttendanceSubgroupApprovalRequestDTO> findAllByRequestedDateBetween(LocalDate fDate,
			LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<AttendanceSubgroupApprovalRequest>attendanceSubgroupApprovalRequests=attendanceSubgroupApprovalRequestRepository.findAllByDateBetween(fromDate, toDate);
		List<AttendanceSubgroupApprovalRequestDTO>attendanceSubgroupApprovalRequestDTOs=new ArrayList<>();
		for(AttendanceSubgroupApprovalRequest attendanceSubgroupApprovalRequest:attendanceSubgroupApprovalRequests) {
			AttendanceSubgroupApprovalRequestDTO attendanceSubgroupApprovalRequestDTO=new AttendanceSubgroupApprovalRequestDTO(attendanceSubgroupApprovalRequest);
			attendanceSubgroupApprovalRequestDTOs.add(attendanceSubgroupApprovalRequestDTO);
		}
		return attendanceSubgroupApprovalRequestDTOs;
	}

	
}
