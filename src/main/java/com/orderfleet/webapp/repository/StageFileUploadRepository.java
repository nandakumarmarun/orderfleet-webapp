package com.orderfleet.webapp.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.orderfleet.webapp.domain.StageFileUpload;

public interface StageFileUploadRepository extends JpaRepository<StageFileUpload, Long> {
	
	@Query("select stageFile from StageFileUpload stageFile where stageFile.company.id = ?#{principal.companyId} and stageFile.stage.pid =?1")
	StageFileUpload findByCompanyAndStagePid(String stagePid);
	
	@Query("select stage from StageFileUpload stage where stage.company.id = ?#{principal.companyId}")
	List<StageFileUpload> findAllStageByCompany();
	
	Optional<StageFileUpload> findOneByPid(String pid);

}
