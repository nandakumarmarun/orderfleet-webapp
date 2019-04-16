package com.orderfleet.webapp.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.RootPlanApprovalRequest;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.RootPlanHeader;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RootPlanApprovalRequestRepository;
import com.orderfleet.webapp.repository.RootPlanDetailRepository;
import com.orderfleet.webapp.repository.RootPlanHeaderRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RootPlanHeaderService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.RootPlanHeaderUserTaskListDTO;
import com.orderfleet.webapp.web.rest.dto.RootPlanHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.TaskDTO;

@Transactional
@Service
public class RootPlanHeaderServiceImpl implements RootPlanHeaderService {

	private final Logger log = LoggerFactory.getLogger(RootPlanHeaderServiceImpl.class);

	@Inject
	private RootPlanHeaderRepository rootPlanHeaderRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private RootPlanDetailRepository rootPlanDetailRepository;

	@Inject
	private RootPlanApprovalRequestRepository rootPlanApprovalRequestRepository;

	@Override
	public RootPlanHeaderDTO save(RootPlanHeaderDTO rootPlanHeaderDTO) {
		RootPlanHeader rootPlanHeader = new RootPlanHeader();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		rootPlanHeader.setCompany(company);
		Optional<User> opUser = userRepository.findOneByPid(rootPlanHeaderDTO.getUserPid());
		if (opUser.isPresent()) {
			rootPlanHeader.setUser(opUser.get());
			rootPlanHeader.setName(rootPlanHeaderDTO.getName());
			rootPlanHeader.setFromDate(rootPlanHeaderDTO.getFromDate());
			rootPlanHeader.setToDate(rootPlanHeaderDTO.getToDate());
			rootPlanHeader.setPid(RootPlanHeaderService.PID_PREFIX + RandomUtil.generatePid());
			rootPlanHeader = rootPlanHeaderRepository.save(rootPlanHeader);
		}
		RootPlanHeaderDTO rootPlanHeaderDTO2 = new RootPlanHeaderDTO(rootPlanHeader);
		return rootPlanHeaderDTO2;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByCompany() {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository.findAllByCompanyId();
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RootPlanHeaderDTO> findOneByPid(String pid) {
		log.debug("Request to get RootPlanHeaderDTO by pid : {}", pid);
		return rootPlanHeaderRepository.findOneByPid(pid).map(rootPlanHeader -> {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			return rootPlanHeaderDTO;
		});
	}

	@Override
	public RootPlanHeaderDTO update(RootPlanHeaderDTO rootPlanHeaderDTO) {
		Optional<RootPlanHeader> optRootPlanHeader = rootPlanHeaderRepository.findOneByPid(rootPlanHeaderDTO.getPid());
		RootPlanHeader rootPlanHeader = new RootPlanHeader();
		if (optRootPlanHeader.isPresent()) {
			optRootPlanHeader.get().setName(rootPlanHeaderDTO.getName());
			optRootPlanHeader.get().setFromDate(rootPlanHeaderDTO.getFromDate());
			optRootPlanHeader.get().setToDate(rootPlanHeaderDTO.getToDate());
			optRootPlanHeader.get().setActivated(rootPlanHeaderDTO.getActivated());
			rootPlanHeader = rootPlanHeaderRepository.save(optRootPlanHeader.get());
		}
		RootPlanHeaderDTO rootPlanHeaderDTO2 = new RootPlanHeaderDTO(rootPlanHeader);
		return rootPlanHeaderDTO2;
	}

	@Override
	public Optional<RootPlanHeaderDTO> findByName(String name) {
		log.debug("Request to get RootPlanHeader by name : {}", name);
		return rootPlanHeaderRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(rootPlanHeader -> {
					RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
					return rootPlanHeaderDTO;
				});
	}

	@Override
	public void delete(String pid) {
		log.debug("Request to delete RootPlanHeader : {}", pid);
		rootPlanHeaderRepository.findOneByPid(pid).ifPresent(rootPlanHeader -> {
			rootPlanHeaderRepository.delete(rootPlanHeader.getId());
		});
	}

	@Override
	public List<RootPlanHeaderUserTaskListDTO> findAllDetailsByUserLogin() {
		Long companyId = SecurityUtils.getCurrentUsersCompanyId();
		List<RootPlanHeaderUserTaskListDTO> rootPlanHeaderUserTaskListDTOs = new ArrayList<>();

		// find root plan header
		RootPlanHeader rootPlanHeader = rootPlanHeaderRepository
				.findRootPlanHeaderByUserLoginAndActivated(SecurityUtils.getCurrentUserLogin(), true);
		if (rootPlanHeader != null) {
			RootPlanDetail lastDownloadedPlan = null;
			// query to find last download plan
			Optional<RootPlanDetail> optionalLastDownloadPlanDetail = rootPlanDetailRepository
					.findFirstByApprovalStatusEqualsAndCompanyIdAndRootPlanHeaderIdOrderByRootPlanOrderDesc(
							ApprovalStatus.DOWNLOADED, companyId, rootPlanHeader.getId());
			// query to find all plans which is not download
			List<RootPlanDetail> allPlans = rootPlanDetailRepository
					.findByApprovalStatusNotEqualAndHeaderIdOrderByRootOrder(ApprovalStatus.DOWNLOADED,
							rootPlanHeader.getId());
			// Approved plan
			Optional<RootPlanDetail> optionalApprovedRootPlanDetail = allPlans.stream()
					.filter(p -> p.getApprovalStatus().equals(ApprovalStatus.APPROVED)).findAny();
			boolean updateApprovalStatus = false;
			if (optionalLastDownloadPlanDetail.isPresent()) {
				lastDownloadedPlan = optionalLastDownloadPlanDetail.get();
				if (lastDownloadedPlan.getDownloadDate().toLocalDate().equals(LocalDate.now())) {
					RootPlanHeaderUserTaskListDTO rootPlanHeaderUserTaskListDTO = new RootPlanHeaderUserTaskListDTO(
							lastDownloadedPlan.getId(), lastDownloadedPlan.getTaskList().getName(),
							lastDownloadedPlan.getTaskList().getTasks().stream().map(TaskDTO::new)
									.collect(Collectors.toSet()),
							lastDownloadedPlan.getApprovalStatus(), lastDownloadedPlan.getRootPlanOrder());
					rootPlanHeaderUserTaskListDTOs.add(rootPlanHeaderUserTaskListDTO);
				} else if (!optionalApprovedRootPlanDetail.isPresent()) {
					updateApprovalStatus = true;
				}
			} else if (!optionalApprovedRootPlanDetail.isPresent()) {
				updateApprovalStatus = true;
			}

			// convert all plans to plan dtos
			for (int i = 0; i < allPlans.size(); i++) {
				RootPlanDetail rootPlanDetail = allPlans.get(i);
				if (updateApprovalStatus && rootPlanDetail.getApprovalStatus().equals(ApprovalStatus.PENDING)) {
					// update db
					rootPlanDetail.setApprovalStatus(ApprovalStatus.APPROVED);
					rootPlanDetail = rootPlanDetailRepository.save(rootPlanDetail);
					updateApprovalStatus = false;
				} else {
					if (rootPlanDetail.getApprovalStatus().equals(ApprovalStatus.REQUEST_FOR_APPROVAL)) {
						Optional<RootPlanApprovalRequest> optionalApprovalRequest = rootPlanApprovalRequestRepository
								.findFirstByRootPlanDetailOrderByRootPlanDetailIdDesc(rootPlanDetail);
						if (optionalApprovalRequest.isPresent()
								&& optionalApprovalRequest.get().getPlannedDate().isBefore(LocalDate.now())) {
							continue;
						}
					}
				}
				RootPlanHeaderUserTaskListDTO rootPlanHeaderUserTaskListDTO = new RootPlanHeaderUserTaskListDTO(
						rootPlanDetail.getId(), rootPlanDetail.getTaskList().getName(),
						rootPlanDetail.getTaskList().getTasks().stream().map(TaskDTO::new).collect(Collectors.toSet()),
						rootPlanDetail.getApprovalStatus(), rootPlanDetail.getRootPlanOrder());
				rootPlanHeaderUserTaskListDTOs.add(rootPlanHeaderUserTaskListDTO);

			}
		}
		return rootPlanHeaderUserTaskListDTOs;
	}

	@Override
	public RootPlanDetail findTaskListFromDetailByUserLoginAndStatusApproved() {
		RootPlanHeader rootPlanHeader = rootPlanHeaderRepository
				.findRootPlanHeaderByUserLoginAndActivated(SecurityUtils.getCurrentUserLogin(), true);
		RootPlanDetail newRootPlanDetail = null;
		if (rootPlanHeader != null) {
			Optional<RootPlanDetail> optionalRootplanDetail = rootPlanDetailRepository
					.findFirstByApprovalStatusEqualsAndCompanyIdAndRootPlanHeaderIdOrderByRootPlanOrderDesc(
							ApprovalStatus.APPROVED, SecurityUtils.getCurrentUsersCompanyId(), rootPlanHeader.getId());
			if (optionalRootplanDetail.isPresent()) {
				newRootPlanDetail = optionalRootplanDetail.get();
			}
		}
		return newRootPlanDetail;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByUser() {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository
				.findAllByUser(SecurityUtils.getCurrentUserLogin());
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByUserPid(String pid) {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository.findAllByUserPid(pid);
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByDateGreaterFromDateAndLessThanToDate(LocalDate fromDate, LocalDate toDate) {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository
				.findAllByDateGreaterFromDateAndLessThanToDate(fromDate, toDate);
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByUserPidAndDateGreaterFromDateAndLessThanToDate(String userPid,
			LocalDate fromDate, LocalDate toDate) {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository
				.findAllByUserPidAndDateGreaterFromDateAndLessThanToDate(userPid, fromDate, toDate);
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByActivated(boolean active) {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository.findAllByActivated(active);
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

	@Override
	public List<RootPlanHeaderDTO> findAllByUserPidAndActivated(String userPid, boolean active) {
		List<RootPlanHeader> rootPlanHeaders = rootPlanHeaderRepository.findAllByUserPidAndActivated(userPid, active);
		List<RootPlanHeaderDTO> rootPlanHeaderDTOs = new ArrayList<>();
		for (RootPlanHeader rootPlanHeader : rootPlanHeaders) {
			RootPlanHeaderDTO rootPlanHeaderDTO = new RootPlanHeaderDTO(rootPlanHeader);
			rootPlanHeaderDTOs.add(rootPlanHeaderDTO);
		}
		return rootPlanHeaderDTOs;
	}

}
