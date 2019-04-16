package com.orderfleet.webapp.service;

import java.util.List;
import java.util.Optional;

import com.orderfleet.webapp.web.rest.dto.DocumentPrintEmailDTO;

public interface DocumentPrintEmailService {

	DocumentPrintEmailDTO save(DocumentPrintEmailDTO documentPrintEmailDTO);
	
	DocumentPrintEmailDTO update(DocumentPrintEmailDTO documentPrintEmailDTO);
	
	List<DocumentPrintEmailDTO> findDocumentPrintEmailByCompanyPid(String companyPid);
	
	DocumentPrintEmailDTO findDocumentPrintEmailByDocumentPrintEmailId(long id);
	
	Optional<DocumentPrintEmailDTO> findOneByDocumentPidAndName(String pid , String name);

	Optional<DocumentPrintEmailDTO> findOne(Long documentPrintEmailId);

	Optional<DocumentPrintEmailDTO> findOneByDocumentPidAndPrintFilePathIgnoreCase(String pid, String filePathName);
	
	Optional<DocumentPrintEmailDTO> findOneByDocumentPidAndNames(String pid, String name);
}
