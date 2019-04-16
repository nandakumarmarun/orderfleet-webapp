package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.StageGroup;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StageGroupRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StageGroupService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.StageGroupDTO;

@Service
@Transactional
public class StageGroupServiceImpl implements StageGroupService {

	@Inject
	private StageGroupRepository stageGroupRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public StageGroupDTO save(StageGroupDTO stageGroupDto) {
		StageGroup stageGroup = new StageGroup();
		stageGroup.setPid(StageGroupService.PID_PREFIX + RandomUtil.generatePid());
		stageGroup.setName(stageGroupDto.getName());
		stageGroup.setAlias(stageGroupDto.getAlias());
		stageGroup.setDescription(stageGroupDto.getDescription());
		stageGroup.setActivated(stageGroupDto.getActivated());
		// set company
		stageGroup.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		stageGroup = stageGroupRepository.save(stageGroup);
		return new StageGroupDTO(stageGroup);
	}

	@Override
	public StageGroupDTO update(StageGroupDTO stageGroupDto) {
		return stageGroupRepository.findOneByPid(stageGroupDto.getPid()).map(sg -> {
			sg.setName(stageGroupDto.getName());
			sg.setAlias(stageGroupDto.getAlias());
			sg.setDescription(stageGroupDto.getDescription());
			sg = stageGroupRepository.save(sg);
			return new StageGroupDTO(sg);
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public StageGroup findOne(Long id) {
		return stageGroupRepository.findOne(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<StageGroup> findOneByPid(String pid) {
		return stageGroupRepository.findOneByPid(pid).map(stageGroup -> stageGroup);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<StageGroup> findByName(String name) {
		return stageGroupRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(stageGroup -> stageGroup);
	}

	@Override
	public void delete(String pid) {
		stageGroupRepository.findOneByPid(pid).ifPresent(stageGroup -> stageGroupRepository.delete(stageGroup.getId()));
	}
	
	@Override
	public StageGroupDTO updateStageGroupStatus(String pid, boolean activate) {
		return stageGroupRepository.findOneByPid(pid).map(stageGroup -> {
			stageGroup.setActivated(activate);
			stageGroup = stageGroupRepository.save(stageGroup);
			return new StageGroupDTO(stageGroup);
		}).orElse(null);
	}
	
	@Override
	public List<StageGroup> findAllByCompany() {
		return stageGroupRepository.findAllByCompanyId();
	}

	@Override
	public List<StageGroup> findAllByCompanyAndActivatedTrue() {
		return stageGroupRepository.findAllByCompanyIdAndActivated(true);
	}

	@Override
	public List<StageGroup> findAllByCompanyAndActivated(boolean activated) {
		return stageGroupRepository.findAllByCompanyIdAndActivated(activated);
	}

}
