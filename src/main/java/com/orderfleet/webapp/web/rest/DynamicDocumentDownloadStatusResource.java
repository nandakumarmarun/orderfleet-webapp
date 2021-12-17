package com.orderfleet.webapp.web.rest;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.Files;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.domain.enums.TallyDownloadStatus;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.UserNotApplicableElementRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormFileDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementTransactionDTO;
import com.orderfleet.webapp.web.rest.dto.InventoryVoucherHeaderDTO;

/**
 * Web controller for managing DynamicDocumentHeader.
 * 
 * @author Muhammed Riyas T
 * @since August 09, 2016
 */
@Controller
@RequestMapping("/web")
public class DynamicDocumentDownloadStatusResource {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentDownloadStatusResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFinding");
	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private DocumentFormsRepository documentFormRepository;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private UserNotApplicableElementRepository userNotApplicableElementRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private UserDocumentService userDocumentService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DocumentService documentService;

	/**
	 * GET /dynamic-documents : get all the filled forms.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of filled forms
	 *         in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/dynamic-documents-download-status", method = RequestMethod.GET)
	public String getAllDynamicDocumentHeaders(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		model.addAttribute("documents", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
		return "company/dynamicDocumentsDownloadStatus";
	}

	@RequestMapping(value = "/dynamic-documents-download-status/loaddocument", method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "employeePid") String employeePid) {
		List<DocumentDTO> documentDTOs = new ArrayList<>();
		if (employeePid.equals("no")) {
			documentDTOs = documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT);
		} else {
			EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
			String userPid = employeeProfileDTO.getUserPid();
			documentDTOs = userDocumentService.findDocumentsByUserAndDocumentType(userPid,
					DocumentType.DYNAMIC_DOCUMENT);
		}

		return documentDTOs;
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/dynamic-documents-download-status/load-forms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<FormDTO, List<FormFormElementTransactionDTO>> loadForms(
			@RequestParam("documentPid") String documentPid, @RequestParam("userPid") String userPid) {
		log.debug("Web request to  load forms");
		List<Form> formList = documentFormRepository.findFormsByDocumentPid(documentPid);
		Map<FormDTO, List<FormFormElementTransactionDTO>> formFormElements = formFormElementService
				.findFormFormElementByFormsGrouped(formList);
		return hideNotApplicableElements(documentPid, userPid, formFormElements);
	}

	private Map<FormDTO, List<FormFormElementTransactionDTO>> hideNotApplicableElements(String documentPid,
			String userPid, Map<FormDTO, List<FormFormElementTransactionDTO>> formFormElements) {
		for (Map.Entry<FormDTO, List<FormFormElementTransactionDTO>> obj : formFormElements.entrySet()) {
			List<FormElement> userNotApplicableElements = userNotApplicableElementRepository
					.findFormElementsByDocumentAndFormAndUser(documentPid, obj.getKey().getPid(), userPid);
			if (!userNotApplicableElements.isEmpty()) {
				for (FormElement formElement : userNotApplicableElements) {
					for (FormFormElementTransactionDTO formFormElementTransactionDTO : obj.getValue()) {
						if (formElement.getPid().equals(formFormElementTransactionDTO.getFormElementPid())) {
							formFormElementTransactionDTO.setEditable(false);
						}
					}
				}
			}
		}
		return formFormElements;
	}

	@Timed
	@RequestMapping(value = "/dynamic-documents-download-status/filled-forms", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> filledForms(
			@RequestBody DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO) throws URISyntaxException {
		log.debug("Web request to update dynamic documents");
		dynamicDocumentHeaderService.update(dynamicDocumentHeaderDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /dynamic-documents/:id : get the "id" DynamicDocumentHeader.
	 * 
	 * @param id the id of the DynamicDocumentHeaderDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         DynamicDocumentHeaderDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/dynamic-documents-download-status/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentHeaderDTO> getDynamicDocument(@PathVariable String pid) {
		log.debug("Web request to get DynamicDocument by pid : {}", pid);
		return dynamicDocumentHeaderService.findOneByPid(pid)
				.map(dynamicDocumentHeaderDTO -> new ResponseEntity<>(dynamicDocumentHeaderDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * GET /dynamic-documents/images/:pid : get the "id" FormFileDTO.
	 * 
	 * @param pid the pid of the FormFileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         FormFileDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/dynamic-documents-download-status/images/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFileDTO>> getDynamicDocumentImages(@PathVariable String pid) {
		log.debug("Web request to get DynamicDocument images by pid : {}", pid);
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "FORM_QUERY_103" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the form by dynamic document headerPid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderPid(pid);
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		List<FormFileDTO> formFileDTOs = new ArrayList<>();
		if (filledForms.size() > 0) {
			for (FilledForm filledForm : filledForms)
				if (filledForm.getFiles().size() > 0) {
					FormFileDTO formFileDTO = new FormFileDTO();
					formFileDTO.setFormName(filledForm.getForm().getName());
					formFileDTO.setFiles(new ArrayList<>());
					Set<File> files = filledForm.getFiles();
					for (File file : files) {
						FileDTO fileDTO = new FileDTO();
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
					formFileDTOs.add(formFileDTO);
				}
		}
		return new ResponseEntity<>(formFileDTOs, HttpStatus.OK);
	}

	@RequestMapping(value = "/dynamic-documents-download-status/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<DynamicDocumentHeaderDTO>> filterDynamicDocuments(
			@RequestParam("tallyDownloadStatus") String tallyDownloadStatus,
			@RequestParam("employeePid") String employeePid, @RequestParam("documentPid") String documentPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter DynamicDocuments");

		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<DynamicDocumentHeaderDTO>();
		if (filterBy.equals("TODAY")) {
			dynamicDocuments = getFilterData(employeePid, documentPid, LocalDate.now(), LocalDate.now(),
					tallyDownloadStatus);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			dynamicDocuments = getFilterData(employeePid, documentPid, yeasterday, yeasterday, tallyDownloadStatus);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			dynamicDocuments = getFilterData(employeePid, documentPid, weekStartDate, LocalDate.now(),
					tallyDownloadStatus);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			dynamicDocuments = getFilterData(employeePid, documentPid, monthStartDate, LocalDate.now(),
					tallyDownloadStatus);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toDateTime = LocalDate.parse(toDate, formatter);
			dynamicDocuments = getFilterData(employeePid, documentPid, fromDateTime, toDateTime, tallyDownloadStatus);
		}
		return new ResponseEntity<>(dynamicDocuments, HttpStatus.OK);
	}

	private List<DynamicDocumentHeaderDTO> getFilterData(String employeePid, String documentPid, LocalDate fDate,
			LocalDate tDate, String tallyDownloadStatus) {
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}

		List<TallyDownloadStatus> tallyStatus = null;

		switch (tallyDownloadStatus) {
		case "PENDING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PENDING);
			break;
		case "PROCESSING":
			tallyStatus = Arrays.asList(TallyDownloadStatus.PROCESSING);
			break;
		case "COMPLETED":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED);
			break;
		case "ALL":
			tallyStatus = Arrays.asList(TallyDownloadStatus.COMPLETED, TallyDownloadStatus.PROCESSING,
					TallyDownloadStatus.PENDING);
			break;
		}

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<DynamicDocumentHeaderDTO>();
		if (userPid.equals("no") && documentPid.equals("no")) {
			dynamicDocuments = dynamicDocumentHeaderService.findAllByCompanyIdAndDateBetween(fromDate, toDate);
		} else if (!userPid.equals("no") && !documentPid.equals("no")) {
			dynamicDocuments = dynamicDocumentHeaderService
					.findAllByCompanyIdUserPidDocumentPidAndTallyDownloadStatusAndDateBetween(userPid, documentPid,
							tallyStatus, fromDate, toDate);
		} else if (!userPid.equals("no") && documentPid.equals("no")) {
			dynamicDocuments = dynamicDocumentHeaderService
					.findAllByCompanyIdUserPidAndTallyDownloadStatusAndDateBetween(userPid, tallyStatus, fromDate,
							toDate);
		} else if (userPid.equals("no") && !documentPid.equals("no")) {
			dynamicDocuments = dynamicDocumentHeaderService
					.findAllByCompanyIdDocumentPidAndTallyDownloadStatusAndDateBetween(documentPid, tallyStatus,
							fromDate, toDate);
		}
		  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "FORM_QUERY_104" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the form by dynamic document headerPid in";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderPidIn(
				dynamicDocuments.stream().map(d -> d.getPid()).collect(Collectors.toSet()));
		 String flag = "Normal";
			LocalDateTime endLCTime = LocalDateTime.now();
			String endTime = endLCTime.format(DATE_TIME_FORMAT);
			String endDate = startLCTime.format(DATE_FORMAT);
			Duration duration = Duration.between(startLCTime, endLCTime);
			long minutes = duration.toMinutes();
			if (minutes <= 1 && minutes >= 0) {
				flag = "Fast";
			}
			if (minutes > 1 && minutes <= 2) {
				flag = "Normal";
			}
			if (minutes > 2 && minutes <= 10) {
				flag = "Slow";
			}
			if (minutes > 10) {
				flag = "Dead Slow";
			}
	                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
					+ description);
		if (!dynamicDocuments.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id1 = "FORM_QUERY_110" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description1 ="get the filled form pid and dynamic Document header pid by company";
			LocalDateTime startLCTime1 = LocalDateTime.now();
			String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
			String startDate1 = startLCTime1.format(DATE_FORMAT1);
			logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<Object[]> filledFormPidAndDynamicDocPid = filledFormRepository
					.findFilleFormPidAndDynamicDocumentHeaderPidByCompany(
							dynamicDocuments.stream().map(d -> d.getPid()).collect(Collectors.toSet()));
			 String flag1 = "Normal";
				LocalDateTime endLCTime1 = LocalDateTime.now();
				String endTime1 = endLCTime1.format(DATE_TIME_FORMAT1);
				String endDate1 = startLCTime1.format(DATE_FORMAT1);
				Duration duration1 = Duration.between(startLCTime1, endLCTime1);
				long minutes1 = duration1.toMinutes();
				if (minutes1 <= 1 && minutes1 >= 0) {
					flag1 = "Fast";
				}
				if (minutes1 > 1 && minutes1 <= 2) {
					flag1 = "Normal";
				}
				if (minutes1 > 2 && minutes1 <= 10) {
					flag1 = "Slow";
				}
				if (minutes1 > 10) {
					flag1 = "Dead Slow";
				}
		                logger.info(id1 + "," + endDate1 + "," + startTime1 + "," + endTime1 + "," + minutes1 + ",END," + flag1 + ","
						+ description1);

			for (DynamicDocumentHeaderDTO dto : dynamicDocuments) {
				for (Object[] objectArray : filledFormPidAndDynamicDocPid) {
					if (dto.getPid().equals(objectArray[1].toString())) {
						Optional<FilledForm> opFilledForms = filledForms.stream()
								.filter(ff -> ff.getPid().equals(objectArray[0].toString())).findAny();
						if (opFilledForms.isPresent()) {
							if (opFilledForms.get().getFiles() != null && !opFilledForms.get().getFiles().isEmpty()) {
								dto.setImageButtonVisible(true);
							}
						}
					}
				}
			}
		}

		return dynamicDocuments;
	}

	@RequestMapping(path = "/filled-forms-download-status/download/{id}", method = RequestMethod.GET)
	public void downloadFilledFormFile(@PathVariable("id") String pid, HttpServletResponse response)
			throws IOException {
		 DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "FORM_QUERY_101" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="Get the one filled form by pid";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
		Optional<FilledForm> savedfilledForm = filledFormRepository.findOneByPid(pid);
		String flag = "Normal";
		LocalDateTime endLCTime = LocalDateTime.now();
		String endTime = endLCTime.format(DATE_TIME_FORMAT);
		String endDate = startLCTime.format(DATE_FORMAT);
		Duration duration = Duration.between(startLCTime, endLCTime);
		long minutes = duration.toMinutes();
		if (minutes <= 1 && minutes >= 0) {
			flag = "Fast";
		}
		if (minutes > 1 && minutes <= 2) {
			flag = "Normal";
		}
		if (minutes > 2 && minutes <= 10) {
			flag = "Slow";
		}
		if (minutes > 10) {
			flag = "Dead Slow";
		}
                logger.info(id + "," + endDate + "," + startTime + "," + endTime + "," + minutes + ",END," + flag + ","
				+ description);

		if (savedfilledForm.isPresent()) {
			FilledForm filledForm = savedfilledForm.get();
			if (filledForm.getFiles().size() > 0) {
				Set<File> files = filledForm.getFiles();
				File file = files.iterator().next();
				java.io.File physicalFile = this.fileManagerService.getPhysicalFileByFile(file);
				if (physicalFile.exists()) {
					// String mimeType =
					// URLConnection.guessContentTypeFromName(physicalFile.getName());
					response.setContentType(file.getMimeType());
					response.setHeader("Content-Disposition",
							String.format("inline; filename=\"" + file.getFileName() + "\""));
					response.setContentLength((int) physicalFile.length());

					InputStream inputStream = new BufferedInputStream(new FileInputStream(physicalFile));

					FileCopyUtils.copy(inputStream, response.getOutputStream());
					return;
				}
			}
			String errorMessage = "Sorry. The file you are looking for does not exist";
			System.out.println(errorMessage);
			OutputStream outputStream = response.getOutputStream();
			outputStream.write(errorMessage.getBytes(Charset.forName("UTF-8")));
			outputStream.close();
		}
	}
	
	@RequestMapping(value = "/dynamic-documents-download-status/changeStatus", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<DynamicDocumentHeaderDTO> changeStatus(@RequestParam String pid,
			@RequestParam TallyDownloadStatus tallyDownloadStatus) {
		DynamicDocumentHeaderDTO dynamicDocumentDTO = dynamicDocumentHeaderService.findOneByPid(pid).get();
		dynamicDocumentDTO.setTallyDownloadStatus(tallyDownloadStatus);
		dynamicDocumentHeaderService.updateDynamicDocumentHeaderStatus(dynamicDocumentDTO);
		return new ResponseEntity<>(null, HttpStatus.OK);

	}

}
