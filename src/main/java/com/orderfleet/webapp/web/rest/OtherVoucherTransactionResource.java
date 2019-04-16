package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.Files;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.DocumentPrintEmail;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.DynamicDocumentHeaderHistory;
import com.orderfleet.webapp.domain.EmployeeProfileLocation;
import com.orderfleet.webapp.domain.ExecutiveTaskExecution;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.UserForm;
import com.orderfleet.webapp.domain.enums.AccountStatus;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.LocationType;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.DocumentPrintEmailRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderHistoryRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileLocationRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FileRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.UserFormRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountProfileService;
import com.orderfleet.webapp.service.AccountTypeService;
import com.orderfleet.webapp.service.ActivityService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.ExecutiveTaskSubmissionService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.UserActivityService;
import com.orderfleet.webapp.service.async.TaskSubmissionPostSave;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionDTO;
import com.orderfleet.webapp.web.rest.api.dto.ExecutiveTaskSubmissionTransactionWrapper;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.AccountTypeDTO;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExevTaskExenDTO;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormFileDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementTransactionDTO;
import com.orderfleet.webapp.web.rest.util.HeaderUtil;

/**
 * Web controller for managing Other Voucher.
 * 
 * @author Muhammed Riyas T
 * @since November 23, 2016
 */
@Controller
@RequestMapping("/web")
public class OtherVoucherTransactionResource {

	private final Logger log = LoggerFactory.getLogger(OtherVoucherTransactionResource.class);

	@Inject
	private UserActivityService userActivityService;

	@Inject
	private ActivityService activityService;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private DocumentFormsRepository documentFormRepository;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private ExecutiveTaskSubmissionService executiveTaskSubmissionService;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private DynamicDocumentHeaderHistoryRepository dynamicDocumentHeaderHistoryRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private AccountTypeService accountTypeService;

	@Inject
	private AccountProfileService accountProfileService;

	@Inject
	private EmployeeProfileLocationRepository employeeProfileLocationRepository;

	@Inject
	private UserFormRepository userFormRepository;

	@Inject
	private DocumentPrintEmailRepository documentPrintEmailRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private FileRepository fileRepository;
	
	@Inject
	private TaskSubmissionPostSave taskSubmissionPostSave;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	/**
	 * GET /other-voucher-transaction
	 *
	 * @param pageable
	 *            the pagination information
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/other-voucher-transaction", method = RequestMethod.GET)
	public String otherVoucherTransaction(Pageable pageable, Model model) {
		log.debug("Web request to get a page of Other Voucher Transaction");
		model.addAttribute("activityDocuments", getUserAcivityDocuments());
		model.addAttribute("employees", employeeProfileService.findAllByCompany());
		model.addAttribute("accountTypes", accountTypeService.findAllByCompany());
		EmployeeProfileDTO employeeProfile = employeeProfileService
				.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUserLogin());
		if (employeeProfile != null) {
			model.addAttribute("currentEmployeePid", employeeProfile.getPid());
		} else {
			model.addAttribute("currentEmployeePid", "no");
		}
		return "company/otherVoucherTransaction";
	}

	@Timed
	@RequestMapping(value = "/other-voucher-transaction/filled-forms", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> filledForms(
			@RequestBody ExecutiveTaskSubmissionDTO executiveTaskSubmissionDTO) {
		log.debug("Web request to save dynamic documents");
		TaskSubmissionResponse taskSubmissionResponse = null;
		if (executiveTaskSubmissionDTO.getDynamicDocuments().get(0).getPid() == null) {
			// save
			LocalDateTime localDateTime = LocalDateTime.now();
			executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().setDate(localDateTime);
			executiveTaskSubmissionDTO.getExecutiveTaskExecutionDTO().setLocationType(LocationType.NoLocation);
			executiveTaskSubmissionDTO.getDynamicDocuments().get(0).setDocumentDate(localDateTime);
			// create and set Document Number Local
			String documentNumberLocal = createDocumentNumber(
					executiveTaskSubmissionDTO.getDynamicDocuments().get(0).getDocumentPid());
			executiveTaskSubmissionDTO.getDynamicDocuments().get(0).setDocumentNumberLocal(documentNumberLocal);
			ExecutiveTaskSubmissionTransactionWrapper tsTransactionWrapper  = executiveTaskSubmissionService.executiveTaskSubmission(executiveTaskSubmissionDTO);
			if(tsTransactionWrapper != null) {
				taskSubmissionResponse = tsTransactionWrapper.getTaskSubmissionResponse();
				taskSubmissionPostSave.doPostSaveExecutivetaskSubmission(tsTransactionWrapper, executiveTaskSubmissionDTO);
			}
		} else {
			// update
			taskSubmissionResponse = executiveTaskSubmissionService
					.updateDynamicDocument(executiveTaskSubmissionDTO.getDynamicDocuments().get(0));
		}
		return new ResponseEntity<>(taskSubmissionResponse, HttpStatus.OK);
	}

	private String createDocumentNumber(String documentPid) {
		Optional<Document> document = documentRepository.findOneByPid(documentPid);
		String documentNumber = null;
		if (document.isPresent()) {
			documentNumber = System.currentTimeMillis() + "_" + SecurityUtils.getCurrentUserLogin() + "_"
					+ document.get().getDocumentPrefix();
		}
		// find previous document number
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findTop1ByCreatedByLogin(SecurityUtils.getCurrentUserLogin());
		if (dynamicDocumentHeader != null) {
			String[] arr = dynamicDocumentHeader.getDocumentNumberLocal().split("_");
			int i = Integer.valueOf(arr[arr.length - 1]) + 1;
			documentNumber += "_" + i;
		} else {
			documentNumber += "_0";
		}
		return documentNumber;
	}

	private List<DocumentDTO> getUserAcivityDocuments() {
		List<DocumentDTO> documents = new ArrayList<>();
		List<ActivityDTO> activityDTOs = userActivityService.findActivitiesByUserIsCurrentUser();
		for (ActivityDTO activityDTO : activityDTOs) {
			for (DocumentDTO documentDTO : activityDTO.getDocuments()) {
				if (documentDTO.getDocumentType().equals(DocumentType.DYNAMIC_DOCUMENT)) {
					documentDTO.setPid(documentDTO.getPid() + "~" + activityDTO.getPid());
					documentDTO.setName(documentDTO.getName() + " --- [" + activityDTO.getName() + "]");
					documents.add(documentDTO);
				}
			}
		}
		return documents;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/other-voucher-transaction/search", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<DynamicDocumentHeaderDTO> search(@RequestParam("documentPid") String documentPid,
			@RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid) {
		log.debug("Web request to  search other vouchers");
		return getDynamicDocuments(activityPid, accountPid, documentPid);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/other-voucher-transaction/filled-forms/{dynamicDocumentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody DynamicDocumentHeaderDTO getDynamicDocument(
			@PathVariable("dynamicDocumentPid") String dynamicDocumentPid) {
		Optional<DynamicDocumentHeader> optionalDynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid);
		if (optionalDynamicDocumentHeader.isPresent()) {
			DynamicDocumentHeader dynamicDocumentHeader = optionalDynamicDocumentHeader.get();
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			dynamicDocumentHeader.getFilledForms().size();
			dynamicDocumentHeaderDTO.setFilledForms(dynamicDocumentHeader.getFilledForms().stream()
					.map(FilledFormDTO::new).collect(Collectors.toList()));
			return dynamicDocumentHeaderDTO;
		}
		return null;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/other-voucher-transaction/previous-document-number", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	public @ResponseBody String getPreviousDocumentNumber(@RequestParam String documentPid) {
		DynamicDocumentHeader dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findTop1ByDocumentPidAndCreatedByLogin(documentPid, SecurityUtils.getCurrentUserLogin());
		if (dynamicDocumentHeader != null) {
			return dynamicDocumentHeader.getDocumentNumberLocal();
		}
		return "nothing";
	}

	private List<DynamicDocumentHeaderDTO> getDynamicDocuments(String activityPid, String accountPid,
			String documentPid) {
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<>();
		/*
		 * List<DynamicDocumentHeader> dynamicDocumentHeaders =
		 * dynamicDocumentHeaderRepository
		 * .findByExecutiveTaskExecutionActivityPidAndExecutiveTaskExecutionAccountProfilePidAndExecutiveTaskExecutionUserLoginAndDocumentPid(
		 * activityPid, accountPid, SecurityUtils.getCurrentUserLogin(),
		 * documentPid);
		 */
		List<DynamicDocumentHeader> dynamicDocumentHeaders = dynamicDocumentHeaderRepository
				.findByExecutiveTaskExecutionAccountProfilePidAndDocumentPid(accountPid, documentPid);
		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
			DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO = new DynamicDocumentHeaderDTO(dynamicDocumentHeader);
			// find history
			List<DynamicDocumentHeaderHistory> dynamicDocumentHeaderHistories = dynamicDocumentHeaderHistoryRepository
					.findDynamicDocumentHistoriesByPidOrderByIdDesc(dynamicDocumentHeader.getPid());
			if (!dynamicDocumentHeaderHistories.isEmpty()) {
				List<DynamicDocumentHeaderDTO> history = dynamicDocumentHeaderHistories.stream()
						.map(DynamicDocumentHeaderDTO::new).collect(Collectors.toList());
				dynamicDocumentHeaderDTO.setHistory(history);
			}
			// find print files for this document
			List<DocumentPrintEmail> docPrintEmails = documentPrintEmailRepository
					.findByDocumentPid(dynamicDocumentHeader.getDocument().getPid());
			dynamicDocumentHeaderDTO.setPrintEmailDocumentNames(
					docPrintEmails.stream().map(DocumentPrintEmail::getName).collect(Collectors.joining(",")));
			dynamicDocuments.add(dynamicDocumentHeaderDTO);
		}
		return dynamicDocuments;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/other-voucher-transaction/load-forms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<FormDTO, List<FormFormElementTransactionDTO>> loadForms(
			@RequestParam("documentPid") String documentPid, @RequestParam("accountPid") String accountPid) {
		log.debug("Web request to  load forms");
		// if not in user forms, default to all document forms
		List<Form> forms = documentFormRepository.findFormsByDocumentPid(documentPid);
		List<UserForm> userForms = userFormRepository.findByUserIsCurrentUserAndFormsIn(forms);
		if (userForms.isEmpty()) {
			return formFormElementService.findFormFormElementByFormsGrouped(forms);
		}
		return formFormElementService.getFormFormElementBySortedFormsGrouped(userForms);
	}

	@Timed
	@RequestMapping(value = "/load-account-profiles/{activityPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<AccountProfileDTO> loadAccountProfiles(@PathVariable("activityPid") String activityPid) {
		log.debug("Web request to  load AccountProfiles by activitypid  {}", activityPid);
		Optional<ActivityDTO> activityDTO = activityService.findOneByPid(activityPid);
		List<AccountProfileDTO> filteredAccounts = null;
		if (activityDTO.isPresent()) {
			Set<AccountTypeDTO> accountTypes = activityDTO.get().getActivityAccountTypes();
			List<AccountProfileDTO> accountProfileDTOs = locationAccountProfileService
					.findAccountProfilesByCurrentUserLocations();
			filteredAccounts = accountProfileDTOs.stream().filter(acc -> {
				AccountTypeDTO accountTypeDTO = accountTypes.stream()
						.filter(at -> at.getPid().equals(acc.getAccountTypePid())).findAny().orElse(null);
				if (accountTypeDTO == null) {
					return false;
				}
				return true;
			}).map(ap -> ap).collect(Collectors.toList());

		}
		return filteredAccounts;
	}

	@Timed
	@RequestMapping(value = "/other-voucher-transaction/saveAccountProfile/{employeePid}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<AccountProfileDTO> saveAccountProfiles(
			@Valid @RequestBody AccountProfileDTO accountProfileDTO, @PathVariable("employeePid") String employeePid)
			throws URISyntaxException {
		log.debug("Web request to save AccountProfile : {}", accountProfileDTO);
		if (accountProfileDTO.getPid() != null) {
			return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("accountProfile", "idexists",
					"A new account profile cannot already have an ID")).body(null);
		}
		if (accountProfileService.findByName(accountProfileDTO.getName()).isPresent()) {
			return ResponseEntity.badRequest().headers(
					HeaderUtil.createFailureAlert("accountProfile", "nameexists", "Account Profile already in use"))
					.body(null);
		}
		accountProfileDTO.setAccountStatus(AccountStatus.Verified);
		AccountProfileDTO result = accountProfileService.save(accountProfileDTO);
		EmployeeProfileLocation employeeProfileLocation = employeeProfileLocationRepository
				.findTop1ByEmployeeProfilePid(employeePid);
		locationAccountProfileService.saveLocationAccountProfile(employeeProfileLocation.getLocation(),
				result.getPid());
		return ResponseEntity.ok().headers(HeaderUtil.createEntityUpdateAlert("accountProfile", result.getPid()))
				.body(result);

	}

	@Timed
	@RequestMapping(value = "/other-voucher-transaction/load-forms/{dynamicDocumentPid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<FormDTO> getFormsByDocumentPids(
			@PathVariable("dynamicDocumentPid") String dynamicDocumentPid) {
		log.debug("Web request to  load FilledForms by dynamicDocumentPid  {}", dynamicDocumentPid);
		Optional<DynamicDocumentHeader> dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid);
		List<Form> forms = new ArrayList<>();
		if (dynamicDocumentHeader.isPresent()) {
			for (FilledForm filledForm : dynamicDocumentHeader.get().getFilledForms()) {
				forms.add(filledForm.getForm());
			}
		}
		return forms.stream().map(FormDTO::new).collect(Collectors.toList());
	}

	@Timed
	@RequestMapping(value = "/other-voucher-transaction/upload/filled-form-image", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FormFileDTO> getFilledFormImages(@RequestParam String dynamicDocumentPid,
			@RequestParam String formPid) {
		log.debug("Web request to  load FilledForms by dynamicDocumentPid  {}", dynamicDocumentPid);
		Optional<DynamicDocumentHeader> dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid);

		FormFileDTO formFileDTO = new FormFileDTO();
		if (dynamicDocumentHeader.isPresent()) {
			Optional<FilledForm> filledForms = filledFormRepository
					.findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndFormPid(
							dynamicDocumentHeader.get().getExecutiveTaskExecution().getPid(), formPid);
			if (filledForms.isPresent()) {
				FilledForm filledForm = filledForms.get();
				if (filledForm.getFiles().size() > 0) {
					formFileDTO.setFormName(filledForm.getForm().getName());
					formFileDTO.setFiles(new ArrayList<>());
					Set<File> files = filledForm.getFiles();
					for (File file : files) {
						FileDTO fileDTO = new FileDTO();
						fileDTO.setFilePid(file.getPid());
						fileDTO.setFileName(file.getFileName());
						fileDTO.setMimeType(file.getMimeType());
						java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
						if (physicalFile.exists()) {
							try {
								fileDTO.setContent(Files.toByteArray(physicalFile));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
						formFileDTO.getFiles().add(fileDTO);
					}

				}
			}
		}
		return new ResponseEntity<>(formFileDTO, HttpStatus.OK);
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/other-voucher-transaction/upload/filled-form-image", method = RequestMethod.POST)
	public ResponseEntity<List<String>> uploadFilledFormNewFile(MultipartHttpServletRequest request)
			throws URISyntaxException {
		log.debug("Request FilledForm to upload a file");

		String[] dynamicDocumentPid = request.getParameterValues("dynamicDocumentPid");
		String[] formPid = request.getParameterValues("formPid");

		List<String> filePid = new ArrayList<>();
		Iterator<String> itrator = request.getFileNames();
		MultipartFile file = request.getFile(itrator.next());

		Optional<DynamicDocumentHeader> dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid[0]);
		if (dynamicDocumentHeader.isPresent()) {
			filledFormRepository
					.findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndFormPid(
							dynamicDocumentHeader.get().getExecutiveTaskExecution().getPid(), formPid[0])
					.map(filledForm -> {
						try {
							File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
									file.getOriginalFilename(), file.getContentType());
							// update filledForm with file
							Set<File> files = new HashSet<>();
							files.add(uploadedFile);
							if (filledForm.getFiles().size() > 0) {
								files.addAll(filledForm.getFiles());
							}
							filledForm.setFiles(files);
							filledFormRepository.save(filledForm);
							log.debug("uploaded file for FilledForm: {}", filledForm);
							filePid.add(uploadedFile.getPid());
							return new ResponseEntity<>(filePid, HttpStatus.OK);
						} catch (FileManagerException | IOException ex) {
							log.debug("File upload exception : {}", ex.getMessage());
							return ResponseEntity.badRequest()
									.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage()))
									.body(null);
						}
					})
					.orElse(ResponseEntity.badRequest().headers(
							HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
							.body(null));
		}
		return new ResponseEntity<>(filePid, HttpStatus.OK);
	}

	@Timed
	@Transactional
	@RequestMapping(value = "/other-voucher-transaction/upload/filled-form-edit-image", method = RequestMethod.POST)
	public ResponseEntity<List<String>> uploadFilledFormEditedFile(MultipartHttpServletRequest request)
			throws URISyntaxException {
		log.debug("Request FilledForm to upload a file");

		String dynamicDocumentPid = request.getParameterValues("dynamicDocumentPid")[0];
		String formPid = request.getParameterValues("formPid")[0];
		String filePid = request.getParameterValues("ImgFilePid")[0];

		List<String> sucessfilePid = new ArrayList<>(Arrays.asList(filePid));
		Iterator<String> itrator = request.getFileNames();
		MultipartFile file = request.getFile(itrator.next());

		Optional<DynamicDocumentHeader> dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid);
		if (dynamicDocumentHeader.isPresent()) {
			filledFormRepository
					.findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndFormPid(
							dynamicDocumentHeader.get().getExecutiveTaskExecution().getPid(), formPid)
					.map(filledForm -> {
						try {
							File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
									file.getOriginalFilename(), file.getContentType());
							Optional<File> editedFile = fileRepository.findOneByPid(filePid);
							// update filledForm with file
							Set<File> files = new HashSet<>();
							if (editedFile.isPresent()) {
								editedFile.get().setPersistentFile(uploadedFile.getPersistentFile());
								editedFile.get().setDescription(uploadedFile.getDescription());
								editedFile.get().setFileName(uploadedFile.getFileName());
								editedFile.get().setMimeType(uploadedFile.getMimeType());
								editedFile.get().setUploadedDate(uploadedFile.getUploadedDate());
								files.add(editedFile.get());
							}
							if (filledForm.getFiles().size() > 0) {
								files.addAll(filledForm.getFiles());
							}
							filledForm.setFiles(files);
							filledFormRepository.save(filledForm);
							log.debug("uploaded file for FilledForm: {}", filledForm);
							return new ResponseEntity<>(HttpStatus.OK);
						} catch (FileManagerException | IOException ex) {
							log.debug("File upload exception : {}", ex.getMessage());
							return ResponseEntity.badRequest()
									.headers(HeaderUtil.createFailureAlert("fileUpload", "exception", ex.getMessage()))
									.body(null);
						}
					})
					.orElse(ResponseEntity.badRequest().headers(
							HeaderUtil.createFailureAlert("fileUpload", "formNotExists", "FilledForm not found."))
							.body(null));
		}
		return new ResponseEntity<>(sucessfilePid, HttpStatus.OK);
	}

	@Timed
	@RequestMapping(value = "/other-voucher-transaction/delete-image", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteFormByFilePid(@RequestParam String filePid,
			@RequestParam String dynamicDocumentPid, @RequestParam String formPid) {
		log.debug("Web request to  delete file by filePid  {}", filePid);
		Optional<File> file = fileRepository.findOneByPid(filePid);
		Optional<DynamicDocumentHeader> dynamicDocumentHeader = dynamicDocumentHeaderRepository
				.findOneByPid(dynamicDocumentPid);
		Optional<FilledForm> filledForms = filledFormRepository
				.findOneByDynamicDocumentHeaderExecutiveTaskExecutionPidAndFormPid(
						dynamicDocumentHeader.get().getExecutiveTaskExecution().getPid(), formPid);
		if (filledForms.isPresent() && file.isPresent()) {
			for (File savedFile : filledForms.get().getFiles()) {
				if (savedFile.getPid().equalsIgnoreCase(filePid)) {
					filledForms.get().getFiles().remove(savedFile);
					break;
				}
			}
		}
		filledFormRepository.save(filledForms.get());
		fileRepository.deleteByPid(filePid);
		return new ResponseEntity<>(filePid, HttpStatus.OK);
	}
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/other-voucher-transaction/loadExecutiveTaskExecution", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ExevTaskExenDTO loadExecutiveTaskExecution(@RequestParam("pid") String pid,@RequestParam("dynPid") String dynPid) {
		log.debug("Web request to get ExevTaskExenDTO");
		Optional<ExecutiveTaskExecution>optionalExecutiveTaskExecution=executiveTaskExecutionRepository.findOneByPid(pid); 
		Optional<DynamicDocumentHeader>optionalDynamicDocumentHeader=dynamicDocumentHeaderRepository.findOneByPid(dynPid);
		ExevTaskExenDTO exevTaskExenDTO=new ExevTaskExenDTO();
		if(optionalExecutiveTaskExecution.isPresent() && optionalDynamicDocumentHeader.isPresent()) {
			exevTaskExenDTO.setAccountPid(optionalDynamicDocumentHeader.get().getExecutiveTaskExecution().getAccountProfile().getPid());
			exevTaskExenDTO.setActivityPid(optionalExecutiveTaskExecution.get().getActivity().getPid());
			exevTaskExenDTO.setUserPid(optionalExecutiveTaskExecution.get().getUser().getPid());
			exevTaskExenDTO.setDocumentPid(optionalDynamicDocumentHeader.get().getDocument().getPid());
			exevTaskExenDTO.setEmployeePid(optionalDynamicDocumentHeader.get().getEmployee().getPid());
		}
		return exevTaskExenDTO;
	}

}
