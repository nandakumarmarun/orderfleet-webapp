package com.orderfleet.webapp.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.domain.DynamicReportHeader;
import com.orderfleet.webapp.domain.DynamicReportName;
import com.orderfleet.webapp.web.rest.dto.DynamicReportUploadDTO;

public interface DynamicReportUploadService {

	DynamicReportUploadDTO save(DynamicReportUploadDTO dynamicReportUploadDTO);
	
	DynamicReportUploadDTO update(DynamicReportUploadDTO dynamicReportUploadDTO);
	
	Optional<DynamicReportUploadDTO> findByName(String name);
	
	void delete(long id);
	
	void deleteDynamicrReportHeader(long id);
	
	List<DynamicReportUploadDTO> findAllDynamicReportUploadByCompanyId();
	
	String readFromExcelAndSaveToDb(InputStream inputStream, DynamicReportName dynamicReportName) throws IOException;
	
	Optional<DynamicReportHeader> findByDynamicReportNameId(long id);
	
	Optional<DynamicReportUploadDTO> findById(long id);
	
}
