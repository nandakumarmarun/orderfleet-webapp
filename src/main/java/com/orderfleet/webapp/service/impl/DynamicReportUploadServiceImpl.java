package com.orderfleet.webapp.service.impl;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.orderfleet.webapp.domain.DynamicReportDetail;
import com.orderfleet.webapp.domain.DynamicReportHeader;
import com.orderfleet.webapp.domain.DynamicReportName;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DynamicReportHeaderRepository;
import com.orderfleet.webapp.repository.DynamicReportUploadRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DynamicReportUploadService;
import com.orderfleet.webapp.web.rest.dto.DynamicReportUploadDTO;

@Service
@Transactional
public class DynamicReportUploadServiceImpl implements DynamicReportUploadService {

	private static final Logger log = LoggerFactory.getLogger(DynamicReportUploadServiceImpl.class);

	@Inject
	private CompanyRepository companyRepository;

	@Inject
	private DynamicReportUploadRepository dynamicReportUploadRepository;

	@Inject
	private DynamicReportHeaderRepository dynamicReportHeaderRepository;

	@Inject
	private UserRepository userRepository;


	@Override
	public DynamicReportUploadDTO save(DynamicReportUploadDTO dynamicReportUploadDTO) {
		DynamicReportName dynamicReportUpload = new DynamicReportName();
		dynamicReportUpload.setName(dynamicReportUploadDTO.getName());
		dynamicReportUpload.setOverwrite(true);
		dynamicReportUpload.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		dynamicReportUploadRepository.save(dynamicReportUpload);
		DynamicReportUploadDTO dynamicReportUploadDTO1 = new DynamicReportUploadDTO(dynamicReportUpload);
		return dynamicReportUploadDTO1;
	}

	@Override
	public DynamicReportUploadDTO update(DynamicReportUploadDTO dynamicReportUploadDTO) {
		DynamicReportName dynamicReportUpload = new DynamicReportName();
		dynamicReportUpload.setId(dynamicReportUploadDTO.getId());
		dynamicReportUpload.setName(dynamicReportUploadDTO.getName());
		dynamicReportUpload.setOverwrite(true);
		dynamicReportUpload.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
		dynamicReportUploadRepository.save(dynamicReportUpload);
		DynamicReportUploadDTO dynamicReportUploadDTO1 = new DynamicReportUploadDTO(dynamicReportUpload);
		return dynamicReportUploadDTO1;
	}
	
	@Override
	public Optional<DynamicReportUploadDTO> findByName(String name) {
		return dynamicReportUploadRepository
				.findByCompanyIdAndNameIgnoreCase(SecurityUtils.getCurrentUsersCompanyId(), name)
				.map(dynamicReportUpload -> {
					DynamicReportUploadDTO dynamicReportUploadDTO = new DynamicReportUploadDTO(dynamicReportUpload);
					return dynamicReportUploadDTO;
				});
	}

	@Override
	public void delete(long id) {
		dynamicReportUploadRepository.findOneById(id).ifPresent(dynamicReportUpload -> {
			dynamicReportUploadRepository.delete(dynamicReportUpload.getId());
		});
	}

	@Override
	public List<DynamicReportUploadDTO> findAllDynamicReportUploadByCompanyId() {
		List<DynamicReportName> dynamicReportUploads = dynamicReportUploadRepository.findAllByCompanyId();
		List<DynamicReportUploadDTO> dynamicReportUploadDTOs = new ArrayList<DynamicReportUploadDTO>();
		for (DynamicReportName dynamicReportUpload : dynamicReportUploads) {
			DynamicReportUploadDTO dynamicReportUploadDTO = new DynamicReportUploadDTO(dynamicReportUpload);
			dynamicReportUploadDTOs.add(dynamicReportUploadDTO);
		}
		return dynamicReportUploadDTOs;
	}

	@Override
	public String readFromExcelAndSaveToDb(InputStream inputStream, DynamicReportName dynamicReportName)
			throws IOException {
		User user = null;
		String value = null;
		// save dynamic report header
		Optional<User> optionalUser = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
		if (optionalUser.isPresent()) {
			user = optionalUser.get();
		}

		DynamicReportHeader dynamicReportHeader = new DynamicReportHeader();
		dynamicReportHeader.setDynamicReportName(dynamicReportName);
		dynamicReportHeader.setCreatedBy(user);

		List<DynamicReportDetail> dynamicReportDetails = new ArrayList<>();
		Workbook workbook = null;
		int lastcellNumber = 0;
		try {
			workbook = WorkbookFactory.create(inputStream);
			int firstRow = 1;
			Sheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				lastcellNumber = row.getLastCellNum();
				if (firstRow == 1) {
					firstRow = 2;
					for (int no = 0; no < lastcellNumber; no++) {
						if (row.getCell(no) == null) {
							int n = no + 1;
							value = "" + n;
							break;
						}

					}
				}
				System.out.println(row.getCell(0));
				if (value != null) {
					break;
				}
				 if(lastcellNumber >30){
					break;
				}
				if(row.getCell(0) == null || row.getCell(0).toString().equals("") ){
					break;
				}
				DynamicReportDetail dynamicReportDetail = new DynamicReportDetail();
				dynamicReportDetail.setDynamicReportHeader(dynamicReportHeader);
				for (int i = 0; i < lastcellNumber; i++) {
					String field = "col" + (i + 1);
					PropertyDescriptor pd;
					pd = new PropertyDescriptor(field, DynamicReportDetail.class);
					Method setter = pd.getWriteMethod();
					Cell cell = row.getCell(i);
					String data = cell == null ? "" : cell.toString();
					setter.invoke(dynamicReportDetail, data);
					dynamicReportDetails.add(dynamicReportDetail);
				}
			}
			// save to db
			dynamicReportHeader.setColumnCount(lastcellNumber);

			if (value != null) {
				return value;
			}else if(lastcellNumber >30){
				return ","+lastcellNumber;
			}
			else {
				if (lastcellNumber != 0) {
					dynamicReportHeader.setDynamicReportDetails(dynamicReportDetails);
					dynamicReportHeader = dynamicReportHeaderRepository.save(dynamicReportHeader);
				}
			}
		} catch (EncryptedDocumentException | InvalidFormatException | IntrospectionException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			log.debug("Could not initialise this excel", e);
		} finally {
			if (workbook != null) {
				workbook.close();
			}
		}
		return null;
	}

	@Override
	public void deleteDynamicrReportHeader(long id) {
		dynamicReportHeaderRepository.findOneById(id).ifPresent(dynamicReportHeader -> {
			dynamicReportHeaderRepository.delete(dynamicReportHeader.getId());
		});
	}

	@Override
	public Optional<DynamicReportHeader> findByDynamicReportNameId(long id) {
		return dynamicReportHeaderRepository.findOneByDynamicReportNameId(id).map(dynamicReportHeader -> {
			System.out.println(dynamicReportHeader);
			return dynamicReportHeader;
		});
	}

	@Override
	public Optional<DynamicReportUploadDTO> findById(long id) {
		return dynamicReportUploadRepository
				.findOneById(id).map(dynamicReportUpload -> {
					DynamicReportUploadDTO dynamicReportUploadDTO = new DynamicReportUploadDTO(dynamicReportUpload);
					return dynamicReportUploadDTO;
				});
	}

	

}
