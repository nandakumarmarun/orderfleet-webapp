package com.orderfleet.webapp.web.rest;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.DynamicDocumentHeader;
import com.orderfleet.webapp.domain.FilledForm;
import com.orderfleet.webapp.domain.FilledFormDetail;
import com.orderfleet.webapp.repository.DynamicDocumentHeaderRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.service.DynamicDocumentSettingsHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentReportSettingsViewDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentReportViewDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsColumnsDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentSettingsRowColourDTO;

/**
 * Web controller for managing DynamicDocumentReportResource.
 *
 * @author Sarath
 * @since Sep 8, 2017
 *
 */
@Controller
@RequestMapping("/web")
public class DynamicDocumentReportResource {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentReportResource.class);

	@Inject
	private DynamicDocumentHeaderRepository dynamicDocumentHeaderRepository;

	@Inject
	private DynamicDocumentSettingsHeaderService documentSettingsHeaderService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	/**
	 * GET /dynamic-document-report : get all the dynamic-document-report.
	 *
	 * @param pageable
	 *            the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of
	 *         dynamic-document-report in body
	 * @throws URISyntaxException
	 *             if there is an error to generate the pagination HTTP headers
	 */
	@RequestMapping(value = "/dynamic-document-report/{name}", method = RequestMethod.GET)
	@Timed
	public String getAllDynamicDocumentReports(Model model, @PathVariable String name) throws URISyntaxException {
		log.debug("Web request to get DynamicDocumentReports");

		Optional<DynamicDocumentSettingsHeaderDTO> opDocumentHeaderDTO = documentSettingsHeaderService
				.findOneByName(name);
		if (opDocumentHeaderDTO.isPresent()) {
			model.addAttribute("headings", opDocumentHeaderDTO.get());
			model.addAttribute("TitleName", opDocumentHeaderDTO.get().getTitle());
		} else {
			model.addAttribute("headings", new DynamicDocumentSettingsHeaderDTO());
		}
		model.addAttribute("reportName", name);

		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}

		return "company/dynamic-document-report";
	}

	@RequestMapping(value = "/dynamic-document-report/filter", method = RequestMethod.GET)
	public @ResponseBody DynamicDocumentReportViewDTO getByDocument(@RequestParam("userPid") String userPid,
			@RequestParam(value = "name") String name, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate,
			@RequestParam("includeAccount") boolean includeAccount) {
		log.debug("filter dynamic document report by settings name : {} ", name);

		DateTimeFormatter fmt = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

		DynamicDocumentReportViewDTO dynamicFormDTO = new DynamicDocumentReportViewDTO();
		Optional<DynamicDocumentSettingsHeaderDTO> documentSettingsHeaderDTOs = documentSettingsHeaderService
				.findOneByName(name);
		if (documentSettingsHeaderDTOs.isPresent()) {
			DynamicDocumentSettingsHeaderDTO documentSettingsHeaderDTO = documentSettingsHeaderDTOs.get();

			// The report-order greater than zero are the fields that will show
			// in reports
			List<String> elementNameToShow = new ArrayList<>();
			// Add four new field if include header is true
			if (includeAccount) {
				elementNameToShow.add("User");
				elementNameToShow.add("Account Profile");
				elementNameToShow.add("Address");
				elementNameToShow.add("City");
				elementNameToShow.add("Location");
				elementNameToShow.add("Phone Number");
				elementNameToShow.add("Timestamp");
			}

			Collections.sort(documentSettingsHeaderDTO.getDocumentSettingsColumnsDTOs(),
					new DynamicDocumentSettingsColumnsDTO());

			elementNameToShow.addAll(documentSettingsHeaderDTO.getDocumentSettingsColumnsDTOs().stream()
					.map(e -> e.getFormElementName()).collect(Collectors.toList()));

			List<FilledForm> filledForms = new ArrayList<>();
			if (filterBy.equals("TODAY")) {
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), LocalDate.now(),
						LocalDate.now());
			} else if (filterBy.equals("YESTERDAY")) {
				LocalDate yeasterday = LocalDate.now().minusDays(1);
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), LocalDate.now(),
						yeasterday);
			} else if (filterBy.equals("TOMORROW")) {
				LocalDate tommarow = LocalDate.now().plusDays(1);
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), LocalDate.now(),
						tommarow);
			} else if (filterBy.equals("WTD")) {
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), weekStartDate,
						LocalDate.now());
			} else if (filterBy.equals("WFD")) {
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekEndDate = LocalDate.now().with(fieldISO, 1).plusDays(7);
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), LocalDate.now(),
						weekEndDate);
			} else if (filterBy.equals("MTD")) {
				LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), monthStartDate,
						LocalDate.now());
			} else if (filterBy.equals("MFD")) {
				LocalDate monthEndDate = LocalDate.now().with(lastDayOfMonth());
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), LocalDate.now(),
						monthEndDate);
			}

			else if (filterBy.equals("CUSTOM")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
				LocalDate toDateTime = LocalDate.parse(toDate, formatter);
				filledForms = getFilterData(userPid, documentSettingsHeaderDTO.getDocumentPid(), fromDateTime,
						toDateTime);
			}

			List<Map<Integer, DynamicDocumentReportSettingsViewDTO>> elementValues = new ArrayList<>();
			for (FilledForm filledForm : filledForms) {
				// converting the formDetails values to a map with key as
				// elementVisible index and value as elementValue
				Map<Integer, DynamicDocumentReportSettingsViewDTO> elements = new TreeMap<>();

				if (includeAccount) {
					String accName = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
							.getAccountProfile() != null
									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
											.getAccountProfile().getName()
									: " ";
					String accPhone1 = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
							.getAccountProfile() != null
									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
											.getAccountProfile().getPhone1()
									: " ";
					String accAddress = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
							.getAccountProfile() != null
									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
											.getAccountProfile().getAddress()
									: " ";
					String accCity = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
							.getAccountProfile() != null
									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
											.getAccountProfile().getCity()
									: " ";
					String accLocation = filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
							.getAccountProfile() != null
									? filledForm.getDynamicDocumentHeader().getExecutiveTaskExecution()
											.getAccountProfile().getLocation()
									: " ";

					elements.put(elementNameToShow.indexOf("Account Profile"),
							new DynamicDocumentReportSettingsViewDTO(null, null, accName, null, 0));
					elements.put(elementNameToShow.indexOf("Address"),
							new DynamicDocumentReportSettingsViewDTO(null, null, accAddress, null, 0));
					elements.put(elementNameToShow.indexOf("City"),
							new DynamicDocumentReportSettingsViewDTO(null, null, accCity, null, 0));
					elements.put(elementNameToShow.indexOf("Location"),
							new DynamicDocumentReportSettingsViewDTO(null, null, accLocation, null, 0));
					elements.put(elementNameToShow.indexOf("Phone Number"),
							new DynamicDocumentReportSettingsViewDTO(null, null, accPhone1, null, 0));
					elements.put(elementNameToShow.indexOf("User"), new DynamicDocumentReportSettingsViewDTO(null, null,
							filledForm.getDynamicDocumentHeader().getCreatedBy().getFirstName(), null, 0));
					elements.put(elementNameToShow.indexOf("Timestamp"),
							new DynamicDocumentReportSettingsViewDTO(null, null,
									fmt.format(filledForm.getDynamicDocumentHeader().getDocumentDate()).toString(),
									null, 0));
				}

				double abp = 0;
				double indent = 0;
				double compliance = 0;
				for (FilledFormDetail ffd : filledForm.getFilledFormDetails()) {
					if (elementNameToShow.contains(ffd.getFormElement().getName())) {
						// Temporary Code for Changing (only for Indent and
						// Shipment status)
						if (documentSettingsHeaderDTO.getDocumentPid().equals("DOC-fZ3BVaVMcx1502865638565")) {
							if (ffd.getFormElement().getName().equals("ABP")) {
								if (ffd.getValue().getClass().equals(Integer.class)) {
									abp = Integer.parseInt(ffd.getValue());
								}
							}
							if (ffd.getFormElement().getName().equals("INDENT")) {
								if (ffd.getValue().getClass().equals(Integer.class)) {
									indent = Integer.parseInt(ffd.getValue());
								}
							}
							if (ffd.getFormElement().getName().equals("%Compliance")) {
								if (abp == 0 || indent == 0) {
									compliance = (indent / abp) * 100;
									double newCompliance = Math.round(compliance * 100.0) / 100.0;
									ffd.setValue(newCompliance + "%");
								}
							}
						}
						String colour = "";
						int sortOrder = 0;

						if (documentSettingsHeaderDTO.getDocumentSettingsRowColourDTOs() != null) {

							Optional<DynamicDocumentSettingsRowColourDTO> optionalRC = documentSettingsHeaderDTO
									.getDocumentSettingsRowColourDTOs().stream()
									.filter(pc -> pc.getFormElementPid().equalsIgnoreCase(ffd.getFormElement().getPid())
											&& pc.getFormElementValueName().equalsIgnoreCase(ffd.getValue()))
									.findAny();
							Optional<DynamicDocumentSettingsColumnsDTO> optionalC = documentSettingsHeaderDTO
									.getDocumentSettingsColumnsDTOs().stream().filter(pc -> pc.getFormElementPid()
											.equalsIgnoreCase(ffd.getFormElement().getPid()))
									.findAny();

							if (optionalRC.isPresent()) {
								colour = optionalRC.get().getColour();
							}
							if (optionalC.isPresent()) {
								sortOrder = optionalC.get().getSortOrder();
							}

							// End
							elements.put(elementNameToShow.indexOf(ffd.getFormElement().getName()),
									new DynamicDocumentReportSettingsViewDTO(ffd.getFormElement().getPid(),
											ffd.getFormElement().getName(), ffd.getValue(), colour, sortOrder));
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

	private List<FilledForm> getFilterData(String userPid, String documentPid, LocalDate fDate, LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<FilledForm> filledForms = new ArrayList<>();

		List<DynamicDocumentHeader> dynamicDocuments;
		if ("no".equals(userPid)) {
			String id="DYN_QUERY_106";
			String description="get all document by company id,document pid and date between";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

			dynamicDocuments = dynamicDocumentHeaderRepository
					.findAllByCompanyIdDocumentPidAndDateBetweenOrderByCreatedDateDesc(documentPid, fromDate, toDate);
		} else {
			String id="DYN_QUERY_104";
			String description="get all document by company id, UserPid,documentPid and date between and order by created date in desc";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");
			dynamicDocuments = dynamicDocumentHeaderRepository
					.findAllByCompanyIdUserPidDocumentPidAndDateBetweenOrderByCreatedDateDesc(userPid, documentPid,
							fromDate, toDate);
		}
		for (DynamicDocumentHeader dynamicDocumentHeader : dynamicDocuments) {
			String id="FORM_QUERY_103";
			String description="get the form by dynamic document headerPid";
			log.info("{ Query Id:- "+id+" Query Description:- "+description+" }");

			List<FilledForm> fillForms = filledFormRepository
					.findByDynamicDocumentHeaderPid(dynamicDocumentHeader.getPid());
			for (FilledForm filledForm : fillForms) {
				filledForm.setDynamicDocumentHeader(dynamicDocumentHeader);
				filledForms.add(filledForm);
			}
		}
		return filledForms;

	}
}
