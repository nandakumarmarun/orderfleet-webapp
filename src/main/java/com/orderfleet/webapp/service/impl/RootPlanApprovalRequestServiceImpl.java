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

import com.orderfleet.webapp.domain.RootPlanApprovalRequest;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.repository.RootPlanApprovalRequestRepository;
import com.orderfleet.webapp.repository.RootPlanDetailRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RootPlanApprovalRequestService;
import com.orderfleet.webapp.web.rest.dto.RootPlanApprovalRequestDTO;

@Transactional
@Service
public class RootPlanApprovalRequestServiceImpl implements RootPlanApprovalRequestService {

	private final Logger log = LoggerFactory.getLogger(RootPlanApprovalRequestServiceImpl.class);

	@Inject
	private RootPlanApprovalRequestRepository rootPlanApprovalRequestRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private RootPlanDetailRepository rootPlanDetailRepository;

	@Override
	public List<RootPlanApprovalRequestDTO> findAllByCompany() {
		log.debug("find all by company id");
		List<RootPlanApprovalRequest> rootPlanApprovalRequests = rootPlanApprovalRequestRepository.findAllByCompanyId();
		List<RootPlanApprovalRequestDTO> rootPlanApprovalRequestDTOs = new ArrayList<>();
		for (RootPlanApprovalRequest rootPlanApprovalRequest : rootPlanApprovalRequests) {
			RootPlanApprovalRequestDTO rootPlanApprovalRequestDTO = new RootPlanApprovalRequestDTO(
					rootPlanApprovalRequest);
			rootPlanApprovalRequestDTOs.add(rootPlanApprovalRequestDTO);
		}
		return rootPlanApprovalRequestDTOs;
	}

	@Override
	public List<RootPlanApprovalRequestDTO> findAllByUsers(String userPid) {
		log.debug("find all by user pid");
		List<RootPlanApprovalRequest> rootPlanApprovalRequests = rootPlanApprovalRequestRepository
				.findAllByUserPid(userPid);
		List<RootPlanApprovalRequestDTO> rootPlanApprovalRequestDTOs = new ArrayList<>();
		for (RootPlanApprovalRequest rootPlanApprovalRequest : rootPlanApprovalRequests) {
			RootPlanApprovalRequestDTO rootPlanApprovalRequestDTO = new RootPlanApprovalRequestDTO(
					rootPlanApprovalRequest);
			rootPlanApprovalRequestDTOs.add(rootPlanApprovalRequestDTO);
		}
		return rootPlanApprovalRequestDTOs;
	}

	@Override
	public boolean approveRootPlanRequest(Long id, String message) {
		RootPlanApprovalRequest rootPlanApprovalRequest = rootPlanApprovalRequestRepository.findOne(id);
		LocalDateTime fromDate = LocalDate.now().atTime(0, 0);
		LocalDateTime toDate = LocalDate.now().atTime(23, 59);
		RootPlanDetail rootPlanDetail1 = rootPlanDetailRepository.findByApprovalStatusAndDownloadDateAndHeaderPid(
				ApprovalStatus.DOWNLOADED, fromDate, toDate,
				rootPlanApprovalRequest.getRootPlanDetail().getRootPlanHeader().getPid());
		if (rootPlanDetail1 == null) {
			rootPlanApprovalRequest.setApprovalStatus(ApprovalStatus.APPROVED);
			rootPlanApprovalRequest.setApprovedDate(LocalDateTime.now());
			User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
			rootPlanApprovalRequest.setApproverUser(user);
			rootPlanApprovalRequest.setApproverUserMessage(message);
			rootPlanApprovalRequestRepository.save(rootPlanApprovalRequest);
			RootPlanDetail rootPlanDetail = rootPlanApprovalRequest.getRootPlanDetail();
			List<RootPlanDetail> rootPlanDetails = rootPlanDetailRepository
					.findAllByHeaderPid(rootPlanDetail.getRootPlanHeader().getPid());
			for (RootPlanDetail rootPlanDetail2 : rootPlanDetails) {
				if (rootPlanDetail2.getPid().equals(rootPlanDetail.getPid())) {
					rootPlanDetail2.setApprovalStatus(ApprovalStatus.APPROVED);
				} else if (rootPlanDetail2.getApprovalStatus().equals(ApprovalStatus.APPROVED)) {
					rootPlanDetail2.setApprovalStatus(ApprovalStatus.PENDING);
				}
			}
			rootPlanDetailRepository.save(rootPlanDetails);
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void rejectRootPlanRequest(Long id, String message) {
		RootPlanApprovalRequest rootPlanApprovalRequest = rootPlanApprovalRequestRepository.findOne(id);
		rootPlanApprovalRequest.setApprovalStatus(ApprovalStatus.PENDING);
		rootPlanApprovalRequest.setApprovedDate(LocalDateTime.now());
		User user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin()).get();
		rootPlanApprovalRequest.setApproverUser(user);
		rootPlanApprovalRequest.setApproverUserMessage(message);
		rootPlanApprovalRequestRepository.save(rootPlanApprovalRequest);
		RootPlanDetail rootPlanDetail = rootPlanApprovalRequest.getRootPlanDetail();
		rootPlanDetail.setApprovalStatus(ApprovalStatus.PENDING);
		rootPlanDetailRepository.save(rootPlanDetail);
	}

	@Override
	public List<RootPlanApprovalRequestDTO> findAllByRequestedDateBetween(LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<RootPlanApprovalRequest> rootPlanApprovalRequests = rootPlanApprovalRequestRepository
				.findAllByDateBetween(fromDate, toDate);
		List<RootPlanApprovalRequestDTO> rootPlanApprovalRequestDTOs = new ArrayList<>();
		for (RootPlanApprovalRequest rootPlanApprovalRequest : rootPlanApprovalRequests) {
			RootPlanApprovalRequestDTO rootPlanApprovalRequestDTO = new RootPlanApprovalRequestDTO(
					rootPlanApprovalRequest);
			rootPlanApprovalRequestDTOs.add(rootPlanApprovalRequestDTO);
		}
		return rootPlanApprovalRequestDTOs;
	}

}
