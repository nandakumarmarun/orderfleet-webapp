package com.orderfleet.webapp.service.impl;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.enums.StageType;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.StageService;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.dto.StageDTO;

@Service
@Transactional
public class StageServiceImpl implements StageService {

	@Inject
	private StageRepository stageRepository;

	@Inject
	private CompanyRepository companyRepository;

	@Override
	public StageDTO save(StageDTO stageDto) {
		Stage stage = new Stage();
		stage.setPid(StageService.PID_PREFIX + RandomUtil.generatePid());
		stage.setName(stageDto.getName());
		stage.setAlias(stageDto.getAlias());
		stage.setDescription(stageDto.getDescription());
		stage.setSortOrder(stageDto.getSortOrder());
		stage.setActivated(stageDto.getActivated());
		stage.setStageType(StageType.CUSTOMER_JOURNEY);
		stage.setStageNameType(stageDto.getStageNameType());
		// set company
		stage.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		stage = stageRepository.save(stage);
		return new StageDTO(stage);
	}

	@Override
	public StageDTO update(StageDTO stageDto) {
		return stageRepository.findOneByPid(stageDto.getPid()).map(stage -> {
			stage.setName(stageDto.getName());
			stage.setAlias(stageDto.getAlias());
			stage.setDescription(stageDto.getDescription());
			stage.setSortOrder(stageDto.getSortOrder());
			stage.setStageNameType(stageDto.getStageNameType());
			stage = stageRepository.save(stage);
			return new StageDTO(stage);
		}).orElse(null);
	}

	@Override
	@Transactional(readOnly = true)
	public Stage findOne(Long id) {
		return stageRepository.findOne(id);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Stage> findOneByPid(String pid) {
		return stageRepository.findOneByPid(pid).map(stage -> stage);
	}

	@Override
	@Transactional(readOnly = true)
	public Optional<Stage> findByName(String name) {
		return stageRepository.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(stage -> stage);
	}

	@Override
	public void delete(String pid) {
		stageRepository.findOneByPid(pid).ifPresent(stage -> stageRepository.delete(stage.getId()));
	}
	
	@Override
	public StageDTO updateStageStatus(String pid, boolean activate) {
		return stageRepository.findOneByPid(pid).map(stage -> {
			stage.setActivated(activate);
			stage = stageRepository.save(stage);
			return new StageDTO(stage);
		}).orElse(null);
	}
	
	@Override
	public List<Stage> findAllByCompany() {
		return stageRepository.findAllByCompanyId();
	}

	@Override
	public List<Stage> findAllByCompanyAndActivatedTrue() {
		return stageRepository.findAllByCompanyIdAndActivated(true);
	}

	@Override
	public List<Stage> findAllByCompanyAndActivated(boolean activated) {
		return stageRepository.findAllByCompanyIdAndActivated(activated);
	}

}
