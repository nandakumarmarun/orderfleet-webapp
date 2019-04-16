package com.orderfleet.webapp.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.RootPlanDetail;
import com.orderfleet.webapp.domain.TaskList;
import com.orderfleet.webapp.domain.enums.ApprovalStatus;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.RootPlanDetailRepository;
import com.orderfleet.webapp.repository.RootPlanHeaderRepository;
import com.orderfleet.webapp.repository.TaskListRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.RootPlanDetailService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.RootPlanDetailDTO;

@Transactional
@Service
public class RootPlanDetailServiceImpl implements RootPlanDetailService {

	@Inject
	private RootPlanDetailRepository rootPlanDetailRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private RootPlanHeaderRepository rootPlanHeaderRepository;

	@Inject
	private TaskListRepository taskListRepository;

	@Override
	public List<RootPlanDetailDTO> findAllByCompany() {
		List<RootPlanDetail> rootPlanDetails = rootPlanDetailRepository.findAllByCompanyId();
		List<RootPlanDetailDTO> rootPlanDetailDTOs = rootPlanDetails.stream().map(RootPlanDetailDTO::new)
				.collect(Collectors.toList());
		return rootPlanDetailDTOs;
	}

	@Override
	public void save(List<RootPlanDetailDTO> rootPlanDetailDTOs) {
		List<RootPlanDetail> rootPlanDetails = new ArrayList<>();
		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
		// sort
		rootPlanDetailDTOs.sort((h1, h2) -> h1.getRootPlanOrder().compareTo(h2.getRootPlanOrder()));
		rootPlanHeaderRepository.findOneByPid(rootPlanDetailDTOs.get(0).getRootPlanHeaderPid()).ifPresent(rh -> {
			boolean updateApprovalStatus = false;
			// first one as approved
			Optional<RootPlanDetail> optionalRootplanDetail = rootPlanDetailRepository
					.findFirstByApprovalStatusEqualsAndCompanyIdAndRootPlanHeaderIdOrderByRootPlanOrderDesc(
							ApprovalStatus.APPROVED, company.getId(), rh.getId());
			if (!optionalRootplanDetail.isPresent()) {
				updateApprovalStatus = true;
			}
			for (RootPlanDetailDTO rootPlanDetailDTO : rootPlanDetailDTOs) {
				RootPlanDetail rootPlanDetail;
				if (rootPlanDetailDTO.getPid() != null || !"".equals(rootPlanDetailDTO.getPid())) {
					// find the saved plan
					Optional<RootPlanDetail> optionalRPD = rootPlanDetailRepository
							.findOneByPid(rootPlanDetailDTO.getPid());
					if (optionalRPD.isPresent()) {
						rootPlanDetail = optionalRPD.get();
						rootPlanDetail.setRootPlanOrder(rootPlanDetailDTO.getRootPlanOrder());
						rootPlanDetails.add(rootPlanDetail);
						continue;
					} else {
						rootPlanDetail = new RootPlanDetail();
					}
				} else {
					rootPlanDetail = new RootPlanDetail();
				}
				if (updateApprovalStatus) {
					// update
					rootPlanDetail.setApprovalStatus(ApprovalStatus.APPROVED);
					updateApprovalStatus = false;
				}
				rootPlanDetail.setCompany(company);
				rootPlanDetail.setPid(RootPlanDetailService.PID_PREFIX + RandomUtil.generatePid());
				rootPlanDetail.setRootPlanHeader(rh);
				rootPlanDetail.setRootPlanOrder(rootPlanDetailDTO.getRootPlanOrder());
				TaskList taskList = taskListRepository.findOneByPid(rootPlanDetailDTO.getTaskListPid()).get();
				rootPlanDetail.setTaskList(taskList);
				rootPlanDetails.add(rootPlanDetail);
			}
		});
		List<RootPlanDetail> rootPlanDetails2 = rootPlanDetailRepository
				.findAllByHeaderPid(rootPlanDetailDTOs.get(0).getRootPlanHeaderPid());
		List<RootPlanDetail> rootPlanDetailPendings = rootPlanDetailRepository.findByApprovalStatusAndHeaderPid(
				ApprovalStatus.PENDING, rootPlanDetailDTOs.get(0).getRootPlanHeaderPid());
		List<RootPlanDetail> intersectRootPlanDetail = rootPlanDetailPendings.stream().filter(rootPlanDetails::contains)
				.collect(Collectors.toList());

		Set<String> pids = rootPlanDetails2.stream().map(RootPlanDetail::getPid).collect(Collectors.toSet());
		List<RootPlanDetail> diffs = rootPlanDetails.stream()
				.filter(rootPlanDetail -> !pids.contains(rootPlanDetail.getPid())).collect(Collectors.toList());
		diffs.addAll(intersectRootPlanDetail);
		for (RootPlanDetail rootPlanDetail : rootPlanDetailPendings) {
			deleteByDetailId(rootPlanDetail.getId());
		}
		rootPlanDetailRepository.flush();
		rootPlanDetailRepository.save(diffs);
	}

	@Override
	public List<RootPlanDetailDTO> findAllByRootPlanHeaderPid(String pid) {
		List<RootPlanDetail> rootPlanDetails = rootPlanDetailRepository.findAllByHeaderPid(pid);
		List<RootPlanDetailDTO> rootPlanDetailDTOs = new ArrayList<>();
		for (RootPlanDetail rootPlanDetail : rootPlanDetails) {
			RootPlanDetailDTO rootPlanDetailDTO = new RootPlanDetailDTO(rootPlanDetail);
			rootPlanDetailDTOs.add(rootPlanDetailDTO);
		}
		Collections.sort(rootPlanDetailDTOs, (p1, p2) -> p1.getRootPlanOrder().compareTo(p2.getRootPlanOrder()));
		Collections.reverse(rootPlanDetailDTOs);
		return rootPlanDetailDTOs;
	}

	@Override
	public void changeApprovalStatusDownload(RootPlanDetail rootPlanDetail) {
		rootPlanDetail.setApprovalStatus(ApprovalStatus.DOWNLOADED);
		rootPlanDetail.setDownloadDate(LocalDateTime.now());
		rootPlanDetailRepository.save(rootPlanDetail);
	}

	@Transactional
	private void deleteByDetailId(Long id) {
		rootPlanDetailRepository.delete(id);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RootPlanDetailDTO> findAllByUserPidAndDownloadDateBetween(String userpid,
			LocalDateTime startdownloadDate, LocalDateTime endDownloadDate) {
		List<RootPlanDetail> rootPlanDetails = rootPlanDetailRepository.findAllByUserPidAndDownloadDateBetween(userpid,
				startdownloadDate, endDownloadDate);
		List<RootPlanDetailDTO> rootPlanDetailDTOs = rootPlanDetails.stream().map(RootPlanDetailDTO::new)
				.collect(Collectors.toList());
		return rootPlanDetailDTOs;
	}

	@Override
	public void revokeRoutePlan(RootPlanDetail rootPlanDetail) {
		rootPlanDetail.setApprovalStatus(ApprovalStatus.APPROVED);
		rootPlanDetail.setDownloadDate(null);
		rootPlanDetailRepository.save(rootPlanDetail);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<RootPlanDetail> findOneByPid(String pid) {
		return rootPlanDetailRepository.findOneByPid(pid).map(rootPlanDetail -> {
			return rootPlanDetail;
		});
	}

}
