package com.orderfleet.webapp.web.rest.integration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.itextpdf.text.pdf.PdfChunk;
import com.orderfleet.webapp.domain.ClientAppLogFiles;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.PostDatedVoucherAllocation;
import com.orderfleet.webapp.domain.SyncOperation;
import com.orderfleet.webapp.domain.TallyConfiguration;
import com.orderfleet.webapp.domain.enums.SyncOperationType;
import com.orderfleet.webapp.repository.ClientAppLogFilesRepository;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherAllocationRepository;
import com.orderfleet.webapp.repository.PostDatedVoucherRepository;
import com.orderfleet.webapp.repository.SyncOperationRepository;
import com.orderfleet.webapp.repository.TallyConfigurationRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.PostDatedVoucherAllocationService;
import com.orderfleet.webapp.service.PostDatedVoucherService;
import com.orderfleet.webapp.service.async.TPAccountProfileManagementService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.service.util.RandomUtil;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherAllocationDTO;
import com.orderfleet.webapp.web.rest.api.dto.PostDatedVoucherDTO;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationAccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.LocationHierarchyDTO;
import com.orderfleet.webapp.web.rest.dto.PriceLevelAccountProductGroupDTO;
import com.orderfleet.webapp.web.rest.dto.ReceivablePayableDTO;
import com.orderfleet.webapp.web.rest.tallypartner.DocumentUserWiseUpdateController;
import com.orderfleet.webapp.web.tally.dto.GstLedgerDTO;
import com.orderfleet.webapp.web.tally.service.TallyDataUploadService;

@RestController
@RequestMapping(value = "/api/tp/v1")
public class LogFileServerSaveResource {

	private final Logger log = LoggerFactory.getLogger(LogFileServerSaveResource.class);

	@Inject
	private SyncOperationRepository syncOperationRepository;

	@Inject
	private TPAccountProfileManagementService tpAccountProfileManagementService;

	@Inject
	private DocumentUserWiseUpdateController documentUserWiseUpdateController;

	@Inject
	private TallyConfigurationRepository tallyConfigRepository;

	@Inject
	private TallyDataUploadService tallyDataUploadService;

	@Inject
	PostDatedVoucherService postDatedVoucherService;

	@Inject
	PostDatedVoucherRepository postDatedVoucherRepository;

	@Inject
	PostDatedVoucherAllocationService postDatedVoucherAllocationService;

	@Inject
	private ClientAppLogFilesRepository clientAppLogFilesRepository;

	@Inject
	private CompanyRepository companyRepository;

	private static final String LOG_FILE_SAVE_DIRECTORY = "C:/orderfleet_clientapp_logFiles/";

	@RequestMapping(value = "/save-log-files", method = RequestMethod.POST)
	@Timed
	public ResponseEntity<String> bulkSaveAccountProfiles(@RequestParam("file") MultipartFile file)
			throws IOException, FileManagerException {
		log.info("REST request to save multipartFile : {}", file.getOriginalFilename());

		Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());

		LocalDate currentDate = LocalDate.now();

		Optional<ClientAppLogFiles> opClientAppLogFiles = clientAppLogFilesRepository
				.findOneByLogDateAndFileNameAndCompanyId(currentDate, file.getOriginalFilename(), company.getId());

		ClientAppLogFiles clientAppLogFile = new ClientAppLogFiles();

		if (!opClientAppLogFiles.isPresent()) {

			String PID_PREFIX = "LOGFILE-";

			clientAppLogFile.setPid(PID_PREFIX + RandomUtil.generatePid());
			clientAppLogFile.setCompany(company);
			clientAppLogFile.setFileName(file.getOriginalFilename());
			clientAppLogFile.setLogDate(currentDate);

		} else {
			clientAppLogFile = opClientAppLogFiles.get();
			clientAppLogFile.setLastModifiedDate(LocalDateTime.now());

		}
		clientAppLogFilesRepository.save(clientAppLogFile);

		processFileUpload(file.getBytes(), file.getOriginalFilename(), file.getContentType(), company);

		return null;
	}

	private File processFileUpload(byte[] fileBytes, String originalFilename, String contentType, Company company)
			throws FileManagerException {

		String filePath = LOG_FILE_SAVE_DIRECTORY + company.getLegalName() + "/";

		String fileLocation = filePath + originalFilename;

		log.info("Saving multipartFile in path: " + fileLocation);

		try {
			File fileDir = new File(filePath);
			if (!fileDir.exists()) {
				fileDir.mkdirs();
			}
			FileCopyUtils.copy(fileBytes, new File(fileLocation));
		} catch (IOException ioe) {
			throw new FileManagerException(ioe.getMessage());
		}

		// TODO Auto-generated method stub
		return null;
	}

}
