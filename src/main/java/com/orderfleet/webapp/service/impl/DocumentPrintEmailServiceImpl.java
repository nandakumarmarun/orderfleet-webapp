package com.orderfleet.webapp.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DocumentPrintEmail;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DocumentPrintEmailRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.service.DocumentPrintEmailService;
import com.orderfleet.webapp.web.rest.dto.DocumentPrintEmailDTO;

@Service
@Transactional
public class DocumentPrintEmailServiceImpl implements DocumentPrintEmailService{

	@Inject
	private CompanyRepository companyRepository;
	
	@Inject
	private DocumentRepository documentRepository;
	
	@Inject
	private DocumentPrintEmailRepository documentPrintEmailRepository;
	
	
	@Override
	public DocumentPrintEmailDTO save(DocumentPrintEmailDTO documentPrintEmailDTO) {
		DocumentPrintEmail documentPrintEmail=new DocumentPrintEmail();
		documentPrintEmail.setCompany(companyRepository.findOneByPid(documentPrintEmailDTO.getCompanyPid()).get());
		documentPrintEmail.setDocument(documentRepository.findOneByPid(documentPrintEmailDTO.getDocumentPid()).get());
		documentPrintEmail.setEmailFilePath(documentPrintEmailDTO.getEmailFilePath());
		documentPrintEmail.setPrintFilePath(documentPrintEmailDTO.getPrintFilePath());
		documentPrintEmail.setName(documentPrintEmailDTO.getName());
		 documentPrintEmail= documentPrintEmailRepository.save(documentPrintEmail);
		 DocumentPrintEmailDTO documentPrintEmailDTO2=new DocumentPrintEmailDTO(documentPrintEmail);
		return documentPrintEmailDTO2;
	}

	@Override
	public DocumentPrintEmailDTO update(DocumentPrintEmailDTO documentPrintEmailDTO) {
		DocumentPrintEmail documentPrintEmail=new DocumentPrintEmail();
		documentPrintEmail.setCompany(companyRepository.findOneByPid(documentPrintEmailDTO.getCompanyPid()).get());
		documentPrintEmail.setDocument(documentRepository.findOneByPid(documentPrintEmailDTO.getDocumentPid()).get());
		documentPrintEmail.setEmailFilePath(documentPrintEmailDTO.getEmailFilePath());
		documentPrintEmail.setPrintFilePath(documentPrintEmailDTO.getPrintFilePath());
		documentPrintEmail.setName(documentPrintEmailDTO.getName());
		documentPrintEmail.setId(documentPrintEmailDTO.getDocumentPrintEmailId());
		 documentPrintEmail= documentPrintEmailRepository.save(documentPrintEmail);
		 DocumentPrintEmailDTO documentPrintEmailDTO2=new DocumentPrintEmailDTO(documentPrintEmail);
		return documentPrintEmailDTO2;
	}

	@Override
	public List<DocumentPrintEmailDTO> findDocumentPrintEmailByCompanyPid(String companyPid) {
		List<DocumentPrintEmail> documentPrintEmails=documentPrintEmailRepository.findAllDocumentPrintEmailByCompanyId(companyPid);
		List<DocumentPrintEmailDTO> documentPrintEmailDTOs =new ArrayList<>();
		for(DocumentPrintEmail documentPrintEmail:documentPrintEmails){
			DocumentPrintEmailDTO documentPrintEmailDTO=new DocumentPrintEmailDTO(documentPrintEmail);
			documentPrintEmailDTOs.add(documentPrintEmailDTO);
		}
		return documentPrintEmailDTOs;
	}


	@Override
	public DocumentPrintEmailDTO findDocumentPrintEmailByDocumentPrintEmailId(long id) {
		DocumentPrintEmail documentPrintEmail=documentPrintEmailRepository.findOne(id);
		DocumentPrintEmailDTO documentPrintEmailDTO=new DocumentPrintEmailDTO(documentPrintEmail);
		return documentPrintEmailDTO;
	}

	@Override
	public Optional<DocumentPrintEmailDTO> findOneByDocumentPidAndName(String pid,String name) {
		return documentPrintEmailRepository.findOneByDocumentPidAndNameIgnoreCase(pid, name).map(documentPrintEmail -> {
			DocumentPrintEmailDTO documentPrintEmailDTO = new DocumentPrintEmailDTO(documentPrintEmail);
					return documentPrintEmailDTO;
				});
	}

	@Override
	public Optional<DocumentPrintEmailDTO> findOne(Long documentPrintEmailId) {
		return documentPrintEmailRepository.findOneId(documentPrintEmailId).map(documentPrintEmail -> {
			DocumentPrintEmailDTO documentPrintEmailDTO = new DocumentPrintEmailDTO(documentPrintEmail);
					return documentPrintEmailDTO;
				});
	}

	@Override
	public Optional<DocumentPrintEmailDTO> findOneByDocumentPidAndPrintFilePathIgnoreCase(String pid,String filePathName) {
		return documentPrintEmailRepository.findOneByDocumentPidAndPrintFilePathIgnoreCase(pid, filePathName).map(documentPrintEmail -> {
			DocumentPrintEmailDTO documentPrintEmailDTO = new DocumentPrintEmailDTO(documentPrintEmail);
					return documentPrintEmailDTO;
				});
	}

	@Override
	public Optional<DocumentPrintEmailDTO> findOneByDocumentPidAndNames(String pid,String name) {
		return documentPrintEmailRepository.findOneByDocumentPidAndNamesIgnoreCase(pid, name).map(documentPrintEmail -> {
			DocumentPrintEmailDTO documentPrintEmailDTO = new DocumentPrintEmailDTO(documentPrintEmail);
					return documentPrintEmailDTO;
				});
	}


	
}
