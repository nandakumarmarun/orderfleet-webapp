package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.service.DocumentService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDTO;
import com.orderfleet.webapp.web.rest.dto.FilledFormDetailDTO;
import com.orderfleet.webapp.web.rest.dto.VarnaDynamicDocumentDTO;

@Controller
@RequestMapping("/web")
public class VarnaDynamicDocumentResource {

	private final Logger log = LoggerFactory.getLogger(DynamicDocumentResource.class);

	static final String ORDERNO = "Order No";
	static final String Item = "Item";
	static final String Nos = "Nos";
	static final String WorkType = "Work Type";
	static final String Polishing = "Polishing";
	static final String PolishingType = "Polishing Type";
	static final String Height = "Height";
	static final String Width = "Width";
	static final String Holes = "Holes";
	static final String CutOuts = "Cut outs";
	static final String Etching = "Etching";
	static final String AcidWorks = "Acid works";
	static final String EtchingType = "Etching Type";
	static final String AcidWorksType = "Acid Works Type";
	static final String MachinePolish = "Machine Polish";
	static final String ManualPolish = "Manual Polish";
	
	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DocumentService documentService;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Timed
	@RequestMapping(value = "/dynamic-documents/varna", method = RequestMethod.GET)
	public String getAllDynamicDocumentHeaders(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of filled forms");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/varnaDynamicDocuments";
	}

	@RequestMapping(value = "/dynamic-documents/varna/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<VarnaDynamicDocumentDTO>> filterDynamicDocuments(
			@RequestParam("employeePid") String employeePid, 
			@RequestParam("workType") String workType, @RequestParam("filterBy") String filterBy,
			@RequestParam String fromDate, @RequestParam String toDate) {
		log.debug("Web request to filter DynamicDocuments");
		String  documentName ="Basic Quotations";
		Optional<DocumentDTO> opDocumentDTO = documentService.findByName(documentName);

		List<VarnaDynamicDocumentDTO> varnaDynamicDocumentDTOs = new ArrayList<VarnaDynamicDocumentDTO>();
		if (opDocumentDTO.isPresent()) {
			if (filterBy.equals("TODAY")) {
				varnaDynamicDocumentDTOs = getFilterData(employeePid, opDocumentDTO.get().getPid(), workType,
						LocalDate.now(), LocalDate.now());
			} else if (filterBy.equals("YESTERDAY")) {
				LocalDate yeasterday = LocalDate.now().minusDays(1);
				varnaDynamicDocumentDTOs = getFilterData(employeePid, opDocumentDTO.get().getPid(), workType,
						yeasterday, yeasterday);
			} else if (filterBy.equals("WTD")) {
				TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
				LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
				varnaDynamicDocumentDTOs = getFilterData(employeePid, opDocumentDTO.get().getPid(), workType,
						weekStartDate, LocalDate.now());
			} else if (filterBy.equals("MTD")) {
				LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
				varnaDynamicDocumentDTOs = getFilterData(employeePid, opDocumentDTO.get().getPid(), workType,
						monthStartDate, LocalDate.now());
			} else if (filterBy.equals("CUSTOM")) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
				LocalDate toDateTime = LocalDate.parse(toDate, formatter);
				varnaDynamicDocumentDTOs = getFilterData(employeePid, opDocumentDTO.get().getPid(), workType,
						fromDateTime, toDateTime);
			}
		}
		return new ResponseEntity<>(varnaDynamicDocumentDTOs, HttpStatus.OK);
	}

	private List<VarnaDynamicDocumentDTO> getFilterData(String employeePid, String documentPid, String workType,
			LocalDate fDate, LocalDate tDate) {

		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		List<VarnaDynamicDocumentDTO> varnaDynamicDocumentDTOs = new ArrayList<>();
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		List<DynamicDocumentHeaderDTO> dynamicDocuments = new ArrayList<DynamicDocumentHeaderDTO>();
		if (!userPid.equals("")) {
			dynamicDocuments = dynamicDocumentHeaderService
					.findAllByCompanyIdUserPidDocumentPidAndDateBetweenSetFilledForm(userPid, documentPid, fromDate,
							toDate);

			for (DynamicDocumentHeaderDTO dynamicDocumentHeaderDTO : dynamicDocuments) {
				VarnaDynamicDocumentDTO varnaDynamicDocumentDTO = new VarnaDynamicDocumentDTO();
				varnaDynamicDocumentDTO.setCustomerName(dynamicDocumentHeaderDTO.getAccountName());
				varnaDynamicDocumentDTO.setDateTime(dynamicDocumentHeaderDTO.getDocumentDate());
				int n = 0;
				for (FilledFormDTO filledFormDTO : dynamicDocumentHeaderDTO.getFilledForms()) {
					for (FilledFormDetailDTO filledFormDetailDTO : filledFormDTO.getFilledFormDetails()) {
						if (filledFormDetailDTO.getFormElementName().equals(ORDERNO)) {
							varnaDynamicDocumentDTO.setOrderedNo(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals(Item)) {
							varnaDynamicDocumentDTO.setGlass(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals(Nos)) {
							if (filledFormDetailDTO.getValue() != null) {
								n = Integer.parseInt(filledFormDetailDTO.getValue());
							}
							varnaDynamicDocumentDTO.setQuantity(filledFormDetailDTO.getValue());
						} else if (filledFormDetailDTO.getFormElementName().equals(WorkType)) {
							int h = 0;
							int w = 0;
							if (workType.equals("Polishing")) {
								if (filledFormDetailDTO.getValue().equals(Polishing)) {
									for (FilledFormDetailDTO filledFormDetailDTO1 : filledFormDTO
											.getFilledFormDetails()) {
										if (filledFormDetailDTO1.getFormElementName().equals(PolishingType)) {
											varnaDynamicDocumentDTO.setType(filledFormDetailDTO1.getValue());
											for (FilledFormDetailDTO subTypeFilledFormDetail : filledFormDTO
													.getFilledFormDetails()) {
												if (subTypeFilledFormDetail.getFormElementName().equals(MachinePolish)) {
													if (subTypeFilledFormDetail.getValue() != null) {
													varnaDynamicDocumentDTO.setPolishTypeValue(subTypeFilledFormDetail.getValue());
													}
												}else if(subTypeFilledFormDetail.getFormElementName().equals(ManualPolish)) {
													if (subTypeFilledFormDetail.getValue() != null) {
													varnaDynamicDocumentDTO.setPolishTypeValue(subTypeFilledFormDetail.getValue());
													}
												}
											}
											
										} else if (filledFormDetailDTO1.getFormElementName().equals(Height)) {
											if (filledFormDetailDTO1.getValue() != null) {
												h = Integer.parseInt(filledFormDetailDTO1.getValue());
											}
										} else if (filledFormDetailDTO1.getFormElementName().equals(Width)) {
											if (filledFormDetailDTO1.getValue() != null) {
												w = Integer.parseInt(filledFormDetailDTO1.getValue());
											}
										}
									}
									int total = h + w * 2 * n;
									if (total != 0) {
										varnaDynamicDocumentDTO.setPolishing("" + total);
									} else {
										varnaDynamicDocumentDTO.setPolishing(null);
									}
								}
							}
							if (workType.equals("Holes")) {
								if (filledFormDetailDTO.getValue().equals(Holes)) {
									if (n != 0) {
										varnaDynamicDocumentDTO.setHoles("" + n);
									} else {
										varnaDynamicDocumentDTO.setHoles(null);
									}
								}
							}
							if (workType.equals("Cut_Outs")) {
								if (filledFormDetailDTO.getValue().equals(CutOuts)) {
									if (n != 0) {
										varnaDynamicDocumentDTO.setCutOuts("" + n);
									} else {
										varnaDynamicDocumentDTO.setCutOuts(null);
									}
								}
							}
							if (workType.equals("Etching")) {
								if (filledFormDetailDTO.getValue().equals(Etching)) {
									for (FilledFormDetailDTO filledFormDetailDTO1 : filledFormDTO
											.getFilledFormDetails()) {
										if (filledFormDetailDTO1.getFormElementName().equals(EtchingType)) {
											varnaDynamicDocumentDTO.setType(filledFormDetailDTO1.getValue());
										} else if (filledFormDetailDTO1.getFormElementName().equals(Height)) {
											if (filledFormDetailDTO1.getValue() != null) {
												h = Integer.parseInt(filledFormDetailDTO1.getValue());
											}
										} else if (filledFormDetailDTO1.getFormElementName().equals(Width)) {
											if (filledFormDetailDTO1.getValue() != null) {
												w = Integer.parseInt(filledFormDetailDTO1.getValue());
											}
										}
									}
									int total = h * w * n;
									if (total != 0) {
										varnaDynamicDocumentDTO.setEtching("" + total);
									} else {
										varnaDynamicDocumentDTO.setEtching(null);
									}
								}
							}
							if (workType.equals("Acid_Works")) {
								if (filledFormDetailDTO.getValue().equals(AcidWorks)) {
									for (FilledFormDetailDTO filledFormDetailDTO1 : filledFormDTO
											.getFilledFormDetails()) {
										if (filledFormDetailDTO1.getFormElementName().equals(AcidWorksType)) {
											varnaDynamicDocumentDTO.setType(filledFormDetailDTO1.getValue());
										} else if (filledFormDetailDTO1.getFormElementName().equals(Height)) {
											if (filledFormDetailDTO1.getValue() != null) {
												h = Integer.parseInt(filledFormDetailDTO1.getValue());
											}
										} else if (filledFormDetailDTO1.getFormElementName().equals(Width)) {
											if (filledFormDetailDTO1.getValue() != null) {
												w = Integer.parseInt(filledFormDetailDTO1.getValue());
											}
										}
									}
									int total = h * w * n;
									if (total != 0) {
										varnaDynamicDocumentDTO.setAcidWorks("" + total);
									} else {
										varnaDynamicDocumentDTO.setAcidWorks(null);
									}
								}
							}

						}
					}
				}
				varnaDynamicDocumentDTOs.add(varnaDynamicDocumentDTO);
			}
		}
		return varnaDynamicDocumentDTOs;
	}
}
