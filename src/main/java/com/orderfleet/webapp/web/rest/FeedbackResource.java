package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import com.codahale.metrics.annotation.Timed;
import com.google.common.io.Files;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FeedbackGroup;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.FormElement;
import com.orderfleet.webapp.domain.GuidedSellingConfig;
import com.orderfleet.webapp.domain.enums.FeedbackElementType;
import com.orderfleet.webapp.repository.DocumentFormsRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.FeedbackGroupFormElementRepository;
import com.orderfleet.webapp.repository.FeedbackGroupRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.GuidedSellingConfigRepository;
import com.orderfleet.webapp.repository.UserFeedbackGroupRepository;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.FormFormElementService;
import com.orderfleet.webapp.web.rest.api.dto.TaskSubmissionResponse;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FeedbackGroupDTO;
import com.orderfleet.webapp.web.rest.dto.FileDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;
import com.orderfleet.webapp.web.rest.dto.FormElementValueDTO;
import com.orderfleet.webapp.web.rest.dto.FormFileDTO;
import com.orderfleet.webapp.web.rest.dto.FormFormElementTransactionDTO;

/**
 * Web controller for managing DynamicDocumentHeader.
 * 
 * @author Muhammed Riyas T
 * @since August 09, 2016
 */
@Controller
@RequestMapping("/web")
public class FeedbackResource {

	private final Logger log = LoggerFactory.getLogger(FeedbackResource.class);

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private DocumentFormsRepository documentFormRepository;

	@Inject
	private FormFormElementService formFormElementService;

	@Inject
	private UserFeedbackGroupRepository userFeedbackGroupRepository;

	@Inject
	private GuidedSellingConfigRepository guidedSellingConfigRepository;

	@Inject
	private FeedbackGroupFormElementRepository feedbackGroupFormElementRepository;

	@Inject
	private FeedbackGroupRepository feedbackGroupRepository;
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;
	
	@Inject
	private EmployeeProfileService employeeProfileService;


	/**
	 * GET /feedbacks : get all the filled forms.
	 *
	 * @return the ResponseEntity with status 200 (OK) and the list of filled
	 *         forms in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedbacks", method = RequestMethod.GET)
	public String getAllDynamicDocumentHeaders(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<FeedbackGroup> feedbackGroups = userFeedbackGroupRepository.findFeedbackGroupsByUserIsCurrentUser();
		List<FeedbackGroupDTO> feedbackGroupDTOs = feedbackGroups.stream().map(FeedbackGroupDTO::new)
				.collect(Collectors.toList());
		model.addAttribute("feedbackGroups", feedbackGroupDTOs);
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/feedbacks";
	}
	
	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedback-history", method = RequestMethod.GET)
	public String getAllFeedbackHistory(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<FeedbackGroup> feedbackGroups = userFeedbackGroupRepository.findFeedbackGroupsByUserIsCurrentUser();
		List<FeedbackGroupDTO> feedbackGroupDTOs = feedbackGroups.stream().map(FeedbackGroupDTO::new)
				.collect(Collectors.toList());
		model.addAttribute("feedbackGroups", feedbackGroupDTOs);
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if(userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/feedbackHistory";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedbacks/load-forms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Map<FormDTO, List<FormFormElementTransactionDTO>> loadForms(
			@RequestParam("feedbackGroupPid") String feedbackGroupPid, @RequestParam("documentPid") String documentPid,
			@RequestParam("userPid") String userPid) {
		log.debug("Web request to  load forms");
		List<Form> formList = documentFormRepository.findFormsByDocumentPid(documentPid);
		Map<FormDTO, List<FormFormElementTransactionDTO>> formFormElements = formFormElementService
				.findFormFormElementByFormsGrouped(formList);
		return hideNotApplicableElements(feedbackGroupPid, documentPid, userPid, formFormElements);
	}

	private Map<FormDTO, List<FormFormElementTransactionDTO>> hideNotApplicableElements(String feedbackGroupPid,
			String documentPid, String userPid, Map<FormDTO, List<FormFormElementTransactionDTO>> formFormElements) {
		List<FormElement> formElements = feedbackGroupFormElementRepository
				.findFormElementsByGroupPidAnd(feedbackGroupPid, FeedbackElementType.ANSWER);
		for (Map.Entry<FormDTO, List<FormFormElementTransactionDTO>> obj : formFormElements.entrySet()) {
			for (FormFormElementTransactionDTO formFormElementTransactionDTO : obj.getValue()) {
				formFormElementTransactionDTO.setEditable(false);
				for (FormElement formElement : formElements) {
					if (formElement.getPid().equals(formFormElementTransactionDTO.getFormElementPid())) {
						formFormElementTransactionDTO.setEditable(true);
					}
				}
			}
		}
		return formFormElements;
	}

	@Timed
	@RequestMapping(value = "/feedbacks/filled-forms", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TaskSubmissionResponse> filledForms(
			@RequestBody DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO) throws URISyntaxException {
		log.debug("Web request to update feedback documents");
		dynamicDocumentHeaderService.update(dynamicDocumentHeaderDTO);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	/**
	 * GET /feedbacks/:id : get the "id" DynamicDocumentHeader.
	 * 
	 * @param id
	 *            the id of the DynamicDocumentHeaderDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         DynamicDocumentHeaderDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/feedbacks/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DynamicDocumentHeaderDTO> getDynamicDocument(@PathVariable String pid) {
		log.debug("Web request to get DynamicDocument by pid : {}", pid);
		return dynamicDocumentHeaderService.findOneByPid(pid)
				.map(dynamicDocumentHeaderDTO -> new ResponseEntity<>(dynamicDocumentHeaderDTO, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * GET /feedbacks/images/:pid : get the "id" FormFileDTO.
	 * 
	 * @param pid
	 *            the pid of the FormFileDTO to retrieve
	 * @return the ResponseEntity with status 200 (OK) and with body the
	 *         FormFileDTO, or with status 404 (Not Found)
	 */
	@Timed
	@RequestMapping(value = "/feedbacks/images/{pid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<FormFileDTO>> getDynamicDocumentImages(@PathVariable String pid) {
		log.debug("Web request to get DynamicDocument images by pid : {}", pid);
		String id="FORM_QUERY_103";
		String description="get the form by dynamic document headerPid";
		log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

		List<FilledForm> filledForms = filledFormRepository.findByDynamicDocumentHeaderPid(pid);
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

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedbacks/status-values", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Set<FormElementValueDTO>> getStatusValues(
			@RequestParam("feedbackGroupPid") String feedbackGroupPid) {
		FeedbackGroup feedbackGroup = feedbackGroupRepository.findOneByPid(feedbackGroupPid).get();
		Set<FormElementValueDTO> formElementValues = new HashSet<>();
		if (feedbackGroup.getStatusField() != null) {
			formElementValues = feedbackGroup.getStatusField().getFormElementValues().stream()
					.map(FormElementValueDTO::new).collect(Collectors.toSet());
		}
		return new ResponseEntity<>(formElementValues, HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/feedbacks/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<DynamicDocumentHeaderDTO>> filterDynamicDocuments(
			@RequestParam("employeePid") String employeePid, @RequestParam("feedbackGroupPid") String feedbackGroupPid,
			@RequestParam("status") String status, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter DynamicDocuments");
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<DynamicDocumentHeaderDTO>();
		GuidedSellingConfig guidedSellingConfig = guidedSellingConfigRepository.findByCompanyId();
		if (guidedSellingConfig == null || guidedSellingConfig.getGuidedSellingInfoDocument() == null) {
			return new ResponseEntity<>(dynamicDocuments, HttpStatus.OK);
		}
		if (filterBy.equals("TODAY")) {
			dynamicDocuments = getFilterData(guidedSellingConfig.getGuidedSellingInfoDocument().getPid(), employeePid,
					feedbackGroupPid, LocalDate.now(), LocalDate.now(), status);
		} else if (filterBy.equals("YESTERDAY")) {
			LocalDate yeasterday = LocalDate.now().minusDays(1);
			dynamicDocuments = getFilterData(guidedSellingConfig.getGuidedSellingInfoDocument().getPid(), employeePid,
					feedbackGroupPid, yeasterday, yeasterday, status);
		} else if (filterBy.equals("WTD")) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
			dynamicDocuments = getFilterData(guidedSellingConfig.getGuidedSellingInfoDocument().getPid(), employeePid,
					feedbackGroupPid, weekStartDate, LocalDate.now(), status);
		} else if (filterBy.equals("MTD")) {
			LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
			dynamicDocuments = getFilterData(guidedSellingConfig.getGuidedSellingInfoDocument().getPid(), employeePid,
					feedbackGroupPid, monthStartDate, LocalDate.now(), status);
		} else if (filterBy.equals("CUSTOM")) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy"); 
			LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
			LocalDate toFateTime = LocalDate.parse(toDate, formatter);
			dynamicDocuments = getFilterData(guidedSellingConfig.getGuidedSellingInfoDocument().getPid(), employeePid,
					feedbackGroupPid, fromDateTime, toFateTime, status);
		}
		return new ResponseEntity<>(dynamicDocuments, HttpStatus.OK);
	}

	private List<DynamicDocumentHeaderDTO> getFilterData(String feedbackDocumentPid, String employeePid,
			String feedbackGroupPid, LocalDate fDate, LocalDate tDate, String status) {
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "no";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<DynamicDocumentHeader> dynamicDocumentHeaders = new ArrayList<DynamicDocumentHeader>();
		if (userPid.equals("no")) {
			String id="DYN_QUERY_106";
			String description="get all document by company id,document pid and date between";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

			dynamicDocumentHeaders = dynamicDocumentHeaderRepository
					.findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(feedbackDocumentPid, fromDate,
							toDate);
		} else if (!userPid.equals("no")) {
			String id="DYN_QUERY_104";
			String description="get all document by company id, UserPid,documentPid and date between and order by created date in desc";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			dynamicDocumentHeaders = dynamicDocumentHeaderRepository
					.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid,
							feedbackDocumentPid, fromDate, toDate);
		}
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<DynamicDocumentHeaderDTO>();
		if (dynamicDocumentHeaders.size() > 0) {
			dynamicDocuments = checkGroupAndStatus(feedbackGroupPid, status, dynamicDocumentHeaders);
		}
		return dynamicDocuments;
	}

	private List<DynamicDocumentHeaderDTO> checkGroupAndStatus(String feedbackGroupPid, String status,
			List<DynamicDocumentHeader> dynamicDocumentHeaders) {
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<DynamicDocumentHeaderDTO>();
		List<FormElement> formElements = feedbackGroupFormElementRepository
				.findFormElementsByGroupPidAnd(feedbackGroupPid, FeedbackElementType.QUESTION);
		FormElement statusElement = null;
		if (!status.equals("no")) {
			FeedbackGroup feedbackGroup = feedbackGroupRepository.findOneByPid(feedbackGroupPid).get();
			statusElement = feedbackGroup.getStatusField();
		}
		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocumentHeaders) {
			for (FilledForm filledForm : dynamicDocumentHeader.getFilledForms()) {
				boolean exist = false;
				for (FilledFormDetail filledFormDetail : filledForm.getFilledFormDetails()) {
					for (FormElement formElement : formElements) {
						if (statusElement == null) {
							if (formElement.getPid().equals(filledFormDetail.getFormElement().getPid())) {
								dynamicDocuments.add(new DynamicDocumentHeaderDTO(dynamicDocumentHeader));
								exist = true;
								break;
							}
						} else {
							if (statusElement.getPid().equals(filledFormDetail.getFormElement().getPid())) {
								if (status.equals(filledFormDetail.getValue())) {
									dynamicDocuments.add(new DynamicDocumentHeaderDTO(dynamicDocumentHeader));
									exist = true;
									break;
								}
							}
						}
					}
					if (exist) {
						break;
					}
				}
				if (exist) {
					break;
				}
			}
		}
		return dynamicDocuments;
	}

}
