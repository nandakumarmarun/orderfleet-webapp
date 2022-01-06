package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Document;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.domain.Form;
import com.orderfleet.webapp.domain.InventoryVoucherDetail;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.AccountProfileRepository;
import com.orderfleet.webapp.repository.DocumentRepository;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.FilledFormDetailRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.FormFormElementRepository;
import com.orderfleet.webapp.repository.FormRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.DocumentFormsService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FormService;
import com.orderfleet.webapp.service.UserDocumentService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentFilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentFilledFormDetailsDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicFormDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FormDTO;

/**
 * Web controller for managing DynamicDocumentForm.
 */
@Controller
@RequestMapping("/web")
public class DynamicDocumentFormResource {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentFormResource.class);
	private final Logger logger = LoggerFactory.getLogger("QueryFormatting");
	private FilledFormRepository filledFormRepository;

	private final DocumentFormsService documentFormsService;

	private final FormFormElementRepository formFormElementRepository;

	private final FormService formService;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private UserDocumentService userDocumentService;

	@Inject
	private DocumentRepository documentRepository;

	@Inject
	private FormRepository formRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private FilledFormDetailRepository filledFormDetailRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private AccountProfileRepository accountProfileRepository;

	@Inject
	private EmployeeProfileRepository employeeRepository;

	@Inject
	private UserService userService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	public DynamicDocumentFormResource(FilledFormRepository filledFormRepository,
			DocumentFormsService documentFormsService, FormFormElementRepository formFormElementRepository,
			FormService formService) {
		super();
		this.filledFormRepository = filledFormRepository;
		this.documentFormsService = documentFormsService;
		this.formFormElementRepository = formFormElementRepository;
		this.formService = formService;
	}

	/**
	 * GET /dynamic-documents : get all the filled forms.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of filled forms
	 *         in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@GetMapping("/dynamic-document-forms")
	@Timed
	public String getDynamicDocumentForms(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/dynamicDocumentForms";
	}

	@RequestMapping(value = "/dynamic-document-forms/document", method = RequestMethod.GET)
	public @ResponseBody List<DocumentDTO> getDocumentsByUser(@RequestParam(value = "employeePid") String employeePid) {
		List<DocumentDTO> documentDTOs = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (employeePid.equals("all")) {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			System.out.println("Subordinate user id :" + userIds.toString());
			if (!userIds.isEmpty()) {
				documentDTOs = userDocumentService.findDocumentsByUserIdsAndDocumentType(userIds,
						DocumentType.DYNAMIC_DOCUMENT);
			} else {
				documentDTOs = userDocumentService.findDocumentsByUserIsCurrentUser();
				documentDTOs.removeIf(doc -> doc.getDocumentType() != DocumentType.DYNAMIC_DOCUMENT);
			}
		} else {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
			String userPid = employeeProfileDTO.getUserPid();
			documentDTOs = userDocumentService.findDocumentsByUserAndDocumentType(userPid,
					DocumentType.DYNAMIC_DOCUMENT);
		}

		return documentDTOs;
	}

	@GetMapping(value = "/dynamic-document-forms", params = { "documentPid" })
	public @ResponseBody List<FormDTO> getFormsByDocument(@RequestParam(value = "documentPid") String documentPid) {
		return documentFormsService.findFormsByDocumentPid(documentPid);
	}

//ORGINAL Filters	

//	@RequestMapping(value = "/dynamic-document-forms/filter", method = RequestMethod.GET, params = { "documentPid",
//			"formPid" })
//	@Transactional
//	public @ResponseBody DynamicFormDTO getByDocument(@RequestParam("employeePid") String employeePid,
//			@RequestParam(value = "documentPid") String documentPid, @RequestParam(value = "formPid") String formPid,
//			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
//			@RequestParam("includeHeader") boolean isHeaderIncluded,
//			@RequestParam("includeAccount") boolean includeAccount) {
//		log.debug("filter dynamic document forms by documentPid : {} and formPid : {}", documentPid, formPid);
//
//		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
//		DateTimeFormatter date = DateTimeFormatter.ofPattern("MMM dd,yyyy");
//		DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:mm:ss a");
//
//		DynamicFormDTO dynamicFormDTO = new DynamicFormDTO();
//		Optional<FormDTO> optionalFormDto = formService.findOneByPid(formPid);
//		if (optionalFormDto.isPresent()) {
//			FormDTO formDTO = optionalFormDto.get();
//			// The report-order greater than zero are the fields that will show
//			// in reports
//			List<String> elementNameToShow = new ArrayList<>();
//			// Add four new field if include header is true
//			if (isHeaderIncluded && includeAccount) {
//				elementNameToShow.add("Account Profile");
//				elementNameToShow.add("Address");
//				elementNameToShow.add("City");
//				elementNameToShow.add("Location");
//				elementNameToShow.add("Phone Number");
//				elementNameToShow.add("User");
//				elementNameToShow.add("Employee");
//				elementNameToShow.add("Date");
//				elementNameToShow.add("Time");
//				elementNameToShow.add("GPSLocation");
//
//			} else if (isHeaderIncluded && !includeAccount) {
//				elementNameToShow.add("Account Profile");
//				elementNameToShow.add("Phone Number");
//				elementNameToShow.add("User");
//				elementNameToShow.add("Employee");
//				elementNameToShow.add("Date");
//				elementNameToShow.add("Time");
//				elementNameToShow.add("GPSLocation");
//			}
//			elementNameToShow
//					.addAll(formFormElementRepository.findByFormPidAndReportOrderGreaterThanZero(formDTO.getPid())
//							.stream().map(e -> e.getFormElement().getName()).collect(Collectors.toList()));
//			List<FilledForm> filledForms = new ArrayList<>();
//			if (filterBy.equals("TODAY")) {
//				filledForms = getFilterData(employeePid, documentPid, LocalDate.now(), LocalDate.now(),
//						formDTO.getPid());
//			} else if (filterBy.equals("YESTERDAY")) {
//				LocalDate yeasterday = LocalDate.now().minusDays(1);
//				filledForms = getFilterData(employeePid, documentPid, yeasterday, yeasterday, formDTO.getPid());
//			} else if (filterBy.equals("WTD")) {
//				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
//				LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
//				filledForms = getFilterData(employeePid, documentPid, weekStartDate, LocalDate.now(), formDTO.getPid());
//			} else if (filterBy.equals("MTD")) {
//				LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
//				filledForms = getFilterData(employeePid, documentPid, monthStartDate, LocalDate.now(),
//						formDTO.getPid());
//			} else if (filterBy.equals("CUSTOM")) {
//				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
//				LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
//				LocalDate toDateTime = LocalDate.parse(toDate, formatter);
//				filledForms = getFilterData(employeePid, documentPid, fromDateTime, toDateTime, formDTO.getPid());
//			}
//			List<Map<Integer, String>> elementValues = new ArrayList<>();
//			for (FilledForm filledForm : filledForms) {
//				// converting the formDetails values to a map with key as
//				// elementVisible index and value as elementValue
//				Map<Integer, String> elements = new TreeMap<>();
//				if (isHeaderIncluded && !includeAccount) {
//					String accName = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getName()
//									: " ";
//					String accPhone1 = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getPhone1()
//									: " ";
//					elements.put(elementNameToShow.indexOf("Account Profile"), accName);
//					elements.put(elementNameToShow.indexOf("Phone Number"), accPhone1);
//					elements.put(elementNameToShow.indexOf("User"),
//							filledForm.getDynamicDocumentHeader().getCreatedBy().getFirstName());
//					elements.put(elementNameToShow.indexOf("Employee"),
//							filledForm.getDynamicDocumentHeader().getEmployee().getName());
//					elements.put(elementNameToShow.indexOf("Date"),
//							date.format(filledForm.getDynamicDocumentHeader().getDocumentDate()).toString());
//					elements.put(elementNameToShow.indexOf("Time"),
//							time.format(filledForm.getDynamicDocumentHeader().getDocumentDate()).toString());
//					elements.put(elementNameToShow.indexOf("GPSLocation"),
//							filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution().getLocation());
//				} else if (isHeaderIncluded && includeAccount) {
//					String accName = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getName()
//									: " ";
//					String accPhone1 = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getPhone1()
//									: " ";
//					String accAddress = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getAddress()
//									: " ";
//					String accCity = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getCity()
//									: " ";
//					String accLocation = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//							.getAccountProfile() != null
//									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
//											.getAccountProfile().getLocation()
//									: " ";
//					elements.put(elementNameToShow.indexOf("Account Profile"), accName);
//					elements.put(elementNameToShow.indexOf("Address"), accAddress);
//					elements.put(elementNameToShow.indexOf("City"), accCity);
//					elements.put(elementNameToShow.indexOf("Location"), accLocation);
//					elements.put(elementNameToShow.indexOf("Phone Number"), accPhone1);
//					elements.put(elementNameToShow.indexOf("User"),
//							filledForm.getDynamicDocumentHeader().getCreatedBy().getFirstName());
//					elements.put(elementNameToShow.indexOf("Employee"),
//							filledForm.getDynamicDocumentHeader().getEmployee().getName());
//					elements.put(elementNameToShow.indexOf("Date"),
//							date.format(filledForm.getDynamicDocumentHeader().getDocumentDate()).toString());
//					elements.put(elementNameToShow.indexOf("Time"),
//							time.format(filledForm.getDynamicDocumentHeader().getDocumentDate()).toString());
//					elements.put(elementNameToShow.indexOf("GPSLocation"),
//							filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution().getLocation());
//				}
//
//				double abp = 0;
//				double indent = 0;
//				double compliance = 0;
//				for (FilledFormDetail ffd : filledForm.getFilledFormDetails()) {
//					if (elementNameToShow.contains(ffd.getFormElement().getName())) {
//						// Temporary Code for Changing (only for Indent and
//						// Shipment status)
//						if (documentPid.equals("DOC-fZ3BVaVMcx1502865638565")) {
//							if (ffd.getFormElement().getName().equals("ABP")) {
//								if (ffd.getValue().getClass().equals(Integer.class)) {
//									abp = Integer.parseInt(ffd.getValue());
//								}
//							}
//							if (ffd.getFormElement().getName().equals("INDENT")) {
//								if (ffd.getValue().getClass().equals(Integer.class)) {
//									indent = Integer.parseInt(ffd.getValue());
//								}
//							}
//							if (ffd.getFormElement().getName().equals("%Compliance")) {
//								if (abp == 0 || indent == 0) {
//									compliance = (indent / abp) * 100;
//									double newCompliance = Math.round(compliance * 100.0) / 100.0;
//									ffd.setValue(newCompliance + "%");
//								}
//							}
//						}
//						// End
//						String c = elements.get(elementNameToShow.indexOf(ffd.getFormElement().getName()));
//						if (c != null) {
//							elements.put(elementNameToShow.indexOf(ffd.getFormElement().getName()),
//									c + "," + ffd.getValue());
//						} else {
//							elements.put(elementNameToShow.indexOf(ffd.getFormElement().getName()), ffd.getValue());
//						}
//
//					}
//				}
//				// sort the elements by key
//				elementValues.add(elements);
//			}
//			dynamicFormDTO.setElementNameToShow(elementNameToShow);
//			dynamicFormDTO.setElementValues(elementValues);
//		}
//		return dynamicFormDTO;
//	}

// OPTIMIZED filter	
	@RequestMapping(value = "/dynamic-document-forms/filter", method = RequestMethod.GET, params = { "documentPid",
			"formPid" })
	@Transactional
	public @ResponseBody DynamicFormDTO getByDocument(@RequestParam("employeePid") String employeePid,
			@RequestParam(value = "documentPid") String documentPid, @RequestParam(value = "formPid") String formPid,
			@RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam("includeHeader") boolean isHeaderIncluded,
			@RequestParam("includeAccount") boolean includeAccount) {
		log.debug("filter dynamic document forms by documentPid : {} and formPid : {}", documentPid, formPid);

		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
		DateTimeFormatter date = DateTimeFormatter.ofPattern("MMM dd,yyyy");
		DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:mm:ss a");

		DynamicFormDTO dynamicFormDTO = new DynamicFormDTO();
		Optional<FormDTO> optionalFormDto = formService.findOneByPid(formPid);
		if (optionalFormDto.isPresent()) {
			FormDTO formDTO = optionalFormDto.get();
			// The report-order greater than zero are the fields that will show
			// in reports
			List<String> elementNameToShow = new ArrayList<>();
			// Add four new field if include header is true
			if (isHeaderIncluded && includeAccount) {
				elementNameToShow.add("Account Profile");
				elementNameToShow.add("Account Type");
				elementNameToShow.add("Address");
				elementNameToShow.add("City");
				elementNameToShow.add("Location");
				elementNameToShow.add("Phone Number");
				elementNameToShow.add("User");
				elementNameToShow.add("Employee");
				elementNameToShow.add("Date");
				elementNameToShow.add("Time");
				elementNameToShow.add("GPSLocation");

			} else if (isHeaderIncluded && !includeAccount) {
				elementNameToShow.add("Account Profile");
				elementNameToShow.add("Account Type");
				elementNameToShow.add("Phone Number");
				elementNameToShow.add("User");
				elementNameToShow.add("Employee");
				elementNameToShow.add("Date");
				elementNameToShow.add("Time");
				elementNameToShow.add("GPSLocation");
			}
			elementNameToShow
					.addAll(formFormElementRepository.findByFormPidAndReportOrderGreaterThanZero(formDTO.getPid())
							.stream().map(e -> e.getFormElement().getName()).collect(Collectors.toList()));
			List<DynamicDocumentFilledFormDTO> filledForms = new ArrayList<>();
			if (filterBy.equals("TODAY")) {
				filledForms = getFilterData(employeePid, documentPid, LocalDate.now(), LocalDate.now(),
						formDTO.getPid());
			} else if (filterBy.equals("YESTERDAY")) {
				LocalDate yeasterday = LocalDate.now().minusDays(1);
				filledForms = getFilterData(employeePid, documentPid, yeasterday, yeasterday, formDTO.getPid());
			} else if (filterBy.equals("WTD")) {
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
				filledForms = getFilterData(employeePid, documentPid, weekStartDate, LocalDate.now(), formDTO.getPid());
			} else if (filterBy.equals("MTD")) {
				LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
				filledForms = getFilterData(employeePid, documentPid, monthStartDate, LocalDate.now(),
						formDTO.getPid());
			} else if (filterBy.equals("CUSTOM")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
				LocalDate toDateTime = LocalDate.parse(toDate, formatter);
				filledForms = getFilterData(employeePid, documentPid, fromDateTime, toDateTime, formDTO.getPid());
			}
			List<Map<Integer, String>> elementValues = new ArrayList<>();
			for (DynamicDocumentFilledFormDTO filledForm : filledForms) {
				// converting the formDetails values to a map with key as
				// elementVisible index and value as elementValue
				Map<Integer, String> elements = new TreeMap<>();
				if (isHeaderIncluded && !includeAccount) {

					elements.put(elementNameToShow.indexOf("Account Profile"),
							filledForm.getDynamicDocumentHeaderAccountProfileName());
					elements.put(elementNameToShow.indexOf("Account Type"),
							filledForm.getDynamicDocumentHeaderAccountTypeName());
					elements.put(elementNameToShow.indexOf("Phone Number"),
							filledForm.getDynamicDocumentHeaderAccountProfilePhone1());
					elements.put(elementNameToShow.indexOf("User"),
							filledForm.getDynamicDocumentHeaderCreatedByFirstName());
					elements.put(elementNameToShow.indexOf("Employee"),
							filledForm.getDynamicDocumentHeaderEmployeeName());
					elements.put(elementNameToShow.indexOf("Date"),
							date.format(filledForm.getDynamicDocumentHeaderDocumentDate()).toString());
					elements.put(elementNameToShow.indexOf("Time"),
							time.format(filledForm.getDynamicDocumentHeaderDocumentDate()).toString());
					elements.put(elementNameToShow.indexOf("GPSLocation"),
							filledForm.getDynamicDocumentHeaderTaskExecutionLocation());
				} else if (isHeaderIncluded && includeAccount) {

					elements.put(elementNameToShow.indexOf("Account Profile"),
							filledForm.getDynamicDocumentHeaderAccountProfileName());
					elements.put(elementNameToShow.indexOf("Account Type"),
							filledForm.getDynamicDocumentHeaderAccountTypeName());
					elements.put(elementNameToShow.indexOf("Address"),
							filledForm.getDynamicDocumentHeaderAccountProfileAddress());
					elements.put(elementNameToShow.indexOf("City"),
							filledForm.getDynamicDocumentHeaderAccountProfileCity());
					elements.put(elementNameToShow.indexOf("Location"),
							filledForm.getDynamicDocumentHeaderAccountProfileLocation());
					elements.put(elementNameToShow.indexOf("Phone Number"),
							filledForm.getDynamicDocumentHeaderAccountProfilePhone1());
					elements.put(elementNameToShow.indexOf("User"),
							filledForm.getDynamicDocumentHeaderCreatedByFirstName());
					elements.put(elementNameToShow.indexOf("Employee"),
							filledForm.getDynamicDocumentHeaderEmployeeName());
					elements.put(elementNameToShow.indexOf("Date"),
							date.format(filledForm.getDynamicDocumentHeaderDocumentDate()).toString());
					elements.put(elementNameToShow.indexOf("Time"),
							time.format(filledForm.getDynamicDocumentHeaderDocumentDate()).toString());
					elements.put(elementNameToShow.indexOf("GPSLocation"),
							filledForm.getDynamicDocumentHeaderTaskExecutionLocation());
				}

				double abp = 0;
				double indent = 0;
				double compliance = 0;
				for (DynamicDocumentFilledFormDetailsDTO ffd : filledForm.getFilledFormDetails()) {
					if (elementNameToShow.contains(ffd.getFormElementName())) {
						// Temporary Code for Changing (only for Indent and
						// Shipment status)
						if (documentPid.equals("DOC-fZ3BVaVMcx1502865638565")) {
							if (ffd.getFormElementName().equals("ABP")) {
								if (ffd.getValue().getClass().equals(Integer.class)) {
									abp = Integer.parseInt(ffd.getValue());
								}
							}
							if (ffd.getFormElementName().equals("INDENT")) {
								if (ffd.getValue().getClass().equals(Integer.class)) {
									indent = Integer.parseInt(ffd.getValue());
								}
							}
							if (ffd.getFormElementName().equals("%Compliance")) {
								if (abp == 0 || indent == 0) {
									compliance = (indent / abp) * 100;
									double newCompliance = Math.round(compliance * 100.0) / 100.0;
									ffd.setValue(newCompliance + "%");
								}
							}
						}
						// End
						String c = elements.get(elementNameToShow.indexOf(ffd.getFormElementName()));
						if (c != null) {
							elements.put(elementNameToShow.indexOf(ffd.getFormElementName()), c + "," + ffd.getValue());
						} else {
							elements.put(elementNameToShow.indexOf(ffd.getFormElementName()), ffd.getValue());
						}

					}
				}
				// sort the elements by key
				elementValues.add(elements);
			}
			dynamicFormDTO.setElementNameToShow(elementNameToShow);
			dynamicFormDTO.setElementValues(elementValues);
		}
		return dynamicFormDTO;
	}

//	private List<FilledForm> getFilterData(String employeePid, String documentPid, LocalDate fDate, LocalDate tDate,
//			String formPid) {
//		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
//		if (!employeePid.equals("no")) {
//			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
//		}
//		String userPid = "no";
//		if (employeeProfileDTO.getPid() != null) {
//			userPid = employeeProfileDTO.getUserPid();
//		}
//		LocalDateTime fromDate = fDate.atTime(0, 0);
//		LocalDateTime toDate = tDate.atTime(23, 59);
//		List<FilledForm> filledForms = new ArrayList<>();
//		if (userPid.equals("no")) {
//			filledForms = filledFormRepository.findFilledFormsByDocumentAndFormPidAndCreatedDateBetween(documentPid,
//					formPid, fromDate, toDate);
//		} else if (!userPid.equals("no")) {
//			filledForms = filledFormRepository.findFilledFormsByDocumentAndFormPidAndCreatedByAndCreatedDateBetween(
//					documentPid, formPid, userPid, fromDate, toDate);
//		}
//		return filledForms;
//	}

//ORIGINAL getFilter

//	private List<FilledForm> getFilterData(String employeePid, String documentPid, LocalDate fDate, LocalDate tDate,
//			String formPid) {
//		log.info("Starting of get filter data..........................");
//		long start = System.nanoTime();
//		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
//		if (!employeePid.equals("no") && !employeePid.equals("all")) {
//			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
//		}
//		List<String> userPids = new ArrayList<>();
//		if (employeeProfileDTO.getPid() != null) {
//			userPids.add(employeeProfileDTO.getUserPid());
//		} else {
//			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
//			System.out.println("UserIds" + userIds.toString());
//			List<UserDTO> userDtoList = userService.findByUserIdIn(userIds);
//			userPids = userDtoList.stream().map(UserDTO::getPid).collect(Collectors.toList());
//		}
//		log.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//		LocalDateTime fromDate = fDate.atTime(0, 0);
//		LocalDateTime toDate = tDate.atTime(23, 59);
//		List<FilledForm> filledForms = new ArrayList<>();
//		if (userPids.isEmpty()) {
//			filledForms = filledFormRepository.findFilledFormsByDocumentAndFormPidAndCreatedDateBetween(documentPid,
//					formPid, fromDate, toDate);
//		} else if (!userPids.isEmpty()) {
//			filledForms = filledFormRepository.findFilledFormsByDocumentAndFormPidAndAndCreatedDateBetween(documentPid,
//					formPid, userPids, fromDate, toDate);
//		}
//		long end = System.nanoTime();
//		double elapsedTime = (end - start) / 1000000.0;
//		log.info("Sync completed in {} ms", elapsedTime);
//		log.info("Ending of get filter data..........................");
//		return filledForms;
//	}

//OPTIMIZED getFilter

	private List<DynamicDocumentFilledFormDTO> getFilterData(String employeePid, String documentPid, LocalDate fDate,
			LocalDate tDate, String formPid) {

		long start = System.nanoTime();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no") && !employeePid.equals("all")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		List<String> userPids = new ArrayList<>();
		if (employeeProfileDTO.getPid() != null) {
			userPids.add(employeeProfileDTO.getUserPid());
		} else {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			System.out.println("UserIds" + userIds.toString());
			List<UserDTO> userDtoList = userService.findByUserIdIn(userIds);
			userPids = userDtoList.stream().map(UserDTO::getPid).collect(Collectors.toList());
		}

		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);

		List<Object[]> filledFormsObjArray = new ArrayList<>();
		if (userPids.isEmpty()) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "FORM_QUERY_111" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get the filled form id by documents form pid and create date between";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			filledFormsObjArray = filledFormRepository.findFilledFormsIdsByDocumentAndFormPidAndCreatedDateBetween(
					documentPid, formPid, fromDate, toDate);

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

		} else if (!userPids.isEmpty()) {
			  DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id = "FORM_QUERY_112" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description ="get the filled form id by documents form pid userPid and create date between";
				LocalDateTime startLCTime = LocalDateTime.now();
				String startTime = startLCTime.format(DATE_TIME_FORMAT);
				String startDate = startLCTime.format(DATE_FORMAT);
				logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			filledFormsObjArray = filledFormRepository
					.findFilledFormsIdsByDocumentAndFormPidAndUserPidCreatedByAndCreatedDateBetween(documentPid,
							formPid, userPids, fromDate, toDate);
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
		}

		List<DynamicDocumentFilledFormDTO> dynamicDocumentFilledFormDTOs = new ArrayList<>();

		Set<Long> filledFormIds = new HashSet<>();
		Set<Long> dynamicDocumentHeaderIds = new HashSet<>();

		for (Object[] ffObj : filledFormsObjArray) {

			filledFormIds.add(ffObj != null ? Long.parseLong(ffObj[0].toString()) : 0);
			dynamicDocumentHeaderIds.add(ffObj != null ? Long.parseLong(ffObj[1].toString()) : 0);

		}

		Set<Long> userIds = new HashSet<>();
		Set<Long> employeeIds = new HashSet<>();
		Set<Long> executiveTaskExecutionIds = new HashSet<>();
		if (dynamicDocumentHeaderIds.size() > 0) {
			DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm:ss a");
			DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String id = "DYN_QUERY_141" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
			String description ="get  document by filled dynamic document header id in";
			LocalDateTime startLCTime = LocalDateTime.now();
			String startTime = startLCTime.format(DATE_TIME_FORMAT);
			String startDate = startLCTime.format(DATE_FORMAT);
			logger.info(id + "," + startDate + "," + startTime + ",_ ,0 ,START,_," + description);
			List<Object[]> dynamiDocumentObjArray = dynamicDocumentHeaderRepository
					.findByFilledDynamicDocumentHeaderIdIn(dynamicDocumentHeaderIds);
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

			for (Object[] ffObj : filledFormsObjArray) {

				Optional<Object[]> opDynamicDocHeader = dynamiDocumentObjArray.stream()
						.filter(u -> Long.parseLong(u[0].toString()) == Long.parseLong(ffObj[1].toString())).findAny();

				if (opDynamicDocHeader.isPresent()) {

					executiveTaskExecutionIds.add(
							opDynamicDocHeader.get()[1] != null ? Long.parseLong(opDynamicDocHeader.get()[1].toString())
									: 0);
					userIds.add(
							opDynamicDocHeader.get()[2] != null ? Long.parseLong(opDynamicDocHeader.get()[2].toString())
									: 0);
					employeeIds.add(
							opDynamicDocHeader.get()[3] != null ? Long.parseLong(opDynamicDocHeader.get()[3].toString())
									: 0);

				}
			}

			List<Object[]> executiveTaskExecutionObjArray = executiveTaskExecutionRepository
					.findByExeIdIn(executiveTaskExecutionIds);

			List<Object[]> employeeArray = employeeRepository.findByEmpIdIn(employeeIds);

			List<Object[]> userArray = userRepository.findByUserIdIn(userIds);
			 DateTimeFormatter DATE_TIME_FORMAT1 = DateTimeFormatter.ofPattern("hh:mm:ss a");
				DateTimeFormatter DATE_FORMAT1 = DateTimeFormatter.ofPattern("dd/MM/yyyy");
				String id1 = "FFD_QUERY_102" + "_" + SecurityUtils.getCurrentUserLogin() + "_" + LocalDateTime.now();
				String description1 ="get all form detail by formId in";
				LocalDateTime startLCTime1 = LocalDateTime.now();
				String startTime1 = startLCTime1.format(DATE_TIME_FORMAT1);
				String startDate1 = startLCTime1.format(DATE_FORMAT1);
				logger.info(id1 + "," + startDate1 + "," + startTime1 + ",_ ,0 ,START,_," + description1);
			List<Object[]> filedFormDetails = filledFormDetailRepository.findAllByFormIdIn(filledFormIds);
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

			for (Object[] ffObj : filledFormsObjArray) {

				DynamicDocumentFilledFormDTO dynamicDocumentFilledFormDTO = new DynamicDocumentFilledFormDTO();

				Optional<Object[]> opDynamicDocHeader = dynamiDocumentObjArray.stream()
						.filter(u -> Long.parseLong(u[0].toString()) == Long.parseLong(ffObj[1].toString())).findAny();

				if (opDynamicDocHeader.isPresent()) {

					Optional<Object[]> opExecutiveTaskExecution = executiveTaskExecutionObjArray.stream()
							.filter(u -> Long.parseLong(u[0].toString()) == Long
									.parseLong(opDynamicDocHeader.get()[1].toString()))
							.findAny();

					Optional<Object[]> opUser = userArray.stream().filter(u -> Long.parseLong(u[0].toString()) == Long
							.parseLong(opDynamicDocHeader.get()[2].toString())).findAny();

					Optional<Object[]> opEmployee = employeeArray.stream().filter(u -> Long
							.parseLong(u[0].toString()) == Long.parseLong(opDynamicDocHeader.get()[3].toString()))
							.findAny();

					if (opExecutiveTaskExecution.isPresent()) {

						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderTaskExecutionLocation(
								opExecutiveTaskExecution.get()[1] != null ? opExecutiveTaskExecution.get()[1].toString()
										: "");
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderAccountProfileName(
								opExecutiveTaskExecution.get()[2] != null ? opExecutiveTaskExecution.get()[2].toString()
										: "");
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderAccountProfilePhone1(
								opExecutiveTaskExecution.get()[3] != null ? opExecutiveTaskExecution.get()[3].toString()
										: "");
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderAccountProfileCity(
								opExecutiveTaskExecution.get()[4] != null ? opExecutiveTaskExecution.get()[4].toString()
										: "");
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderAccountProfileLocation(
								opExecutiveTaskExecution.get()[5] != null ? opExecutiveTaskExecution.get()[5].toString()
										: "");
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderAccountProfileAddress(
								opExecutiveTaskExecution.get()[6] != null ? opExecutiveTaskExecution.get()[6].toString()
										: "");
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderAccountTypeName(
								opExecutiveTaskExecution.get()[7] != null ? opExecutiveTaskExecution.get()[7].toString()
										: "");

					}

					if (opUser.isPresent()) {
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderCreatedByFirstName(
								opUser.get()[1] != null ? opUser.get()[1].toString() : "");
					}

					if (opEmployee.isPresent()) {
						dynamicDocumentFilledFormDTO.setDynamicDocumentHeaderEmployeeName(
								opEmployee.get()[1] != null ? opEmployee.get()[1].toString() : "");
					}

					if (opDynamicDocHeader.get()[4] != null) {

						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

						String[] date = opDynamicDocHeader.get()[4].toString().split("\\.");

						dynamicDocumentFilledFormDTO
								.setDynamicDocumentHeaderDocumentDate(LocalDateTime.parse(date[0], formatter));
					}

				}

//			List<Object[]> ffdDetails = filedFormDetails.stream()
//					.filter(ffd -> Long.parseLong(ffd[1].toString()) == Long.parseLong(ffObj[0].toString()))
//					.collect(Collectors.toList()).stream().sorted(Comparator.comparingLong(FilledFormDetail::getId))
//					.collect(Collectors.toList());

				List<Object[]> ffdObjectDetails = filedFormDetails.stream()
						.filter(ffd -> Long.parseLong(ffd[1].toString()) == Long.parseLong(ffObj[0].toString()))
						.collect(Collectors.toList());

				List<DynamicDocumentFilledFormDetailsDTO> ffdDetails = new ArrayList<>();

				for (Object[] objFfdDetail : ffdObjectDetails) {
					DynamicDocumentFilledFormDetailsDTO dynamicDocumentFilledFormDetailsDTO = new DynamicDocumentFilledFormDetailsDTO();
					dynamicDocumentFilledFormDetailsDTO
							.setFormElementName(objFfdDetail[2] != null ? objFfdDetail[2].toString() : "");
					dynamicDocumentFilledFormDetailsDTO
							.setValue(objFfdDetail[3] != null ? objFfdDetail[3].toString() : "");
					ffdDetails.add(dynamicDocumentFilledFormDetailsDTO);
				}

				dynamicDocumentFilledFormDTO.setFilledFormDetails(ffdDetails);

				dynamicDocumentFilledFormDTOs.add(dynamicDocumentFilledFormDTO);

			}
		}
		long end = System.nanoTime();
		double elapsedTime = (end - start) / 1000000.0;
		log.info("Sync completed in {} ms", elapsedTime);
		return dynamicDocumentFilledFormDTOs;
	}

}
