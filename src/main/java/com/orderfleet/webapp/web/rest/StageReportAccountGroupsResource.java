package com.orderfleet.webapp.web.rest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.File;
import com.orderfleet.webapp.domain.RootCauseAnalysisReason;
import com.orderfleet.webapp.domain.Stage;
import com.orderfleet.webapp.domain.StageHeader;
import com.orderfleet.webapp.domain.StageHeaderFile;
import com.orderfleet.webapp.domain.StageHeaderRca;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.StageNameType;
import com.orderfleet.webapp.repository.AccountGroupAccountProfileRepository;
import com.orderfleet.webapp.repository.AccountGroupRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.FilledFormRepository;
import com.orderfleet.webapp.repository.LocationAccountProfileRepository;
import com.orderfleet.webapp.repository.RootCauseAnalysisReasonRepository;
import com.orderfleet.webapp.repository.StageGroupRepository;
import com.orderfleet.webapp.repository.StageHeaderRcaRepository;
import com.orderfleet.webapp.repository.StageHeaderRepository;
import com.orderfleet.webapp.repository.StageRepository;
import com.orderfleet.webapp.repository.StageStageGroupRepository;
import com.orderfleet.webapp.repository.UserRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.AccountGroupAccountProfileService;
import com.orderfleet.webapp.service.DynamicDocumentHeaderService;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.FileManagerService;
import com.orderfleet.webapp.service.LocationAccountProfileService;
import com.orderfleet.webapp.service.LocationService;
import com.orderfleet.webapp.service.impl.FileManagerException;
import com.orderfleet.webapp.web.rest.dto.AccountProfileDTO;
import com.orderfleet.webapp.web.rest.dto.DynamicDocumentHeaderDTO;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import com.orderfleet.webapp.web.rest.dto.StageDTO;
import com.orderfleet.webapp.web.rest.dto.StageHeaderDTO;

/**
 * Controller for Download Log
 *
 * @author Prashob Sasidharan
 * @since April 12, 2019
 */
@Controller
@RequestMapping("/web")
public class StageReportAccountGroupsResource {

	private static final String TODAY = "TODAY";
	private static final String YESTERDAY = "YESTERDAY";
	private static final String WTD = "WTD";
	private static final String MTD = "MTD";
	private static final String TILLDATE = "TILLDATE";

	@Inject
	private EmployeeProfileRepository employeeProfileRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	@Inject
	private UserRepository userRepository;

	@Inject
	private LocationService locationService;

	@Inject
	private StageRepository stageRepository;

	@Inject
	private StageGroupRepository stageGroupRepository;

	@Inject
	private LocationAccountProfileService locationAccountProfileService;

	@Inject
	private LocationAccountProfileRepository locationAccountProfileRepository;

	@Inject
	private StageHeaderRepository stageHeaderRepository;

	@Inject
	private DynamicDocumentHeaderService dynamicDocumentHeaderService;

	@Inject
	private FileManagerService fileManagerService;

	@Inject
	private FilledFormRepository filledFormRepository;

	@Inject
	private StageStageGroupRepository stageStageGroupRepository;

	@Inject
	private RootCauseAnalysisReasonRepository rootCauseRepository;

	@Inject
	private StageHeaderRcaRepository stageHeaderRcaRepository;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private AccountGroupRepository accountGroupRepository;

	@Inject
	private AccountGroupAccountProfileRepository accountGroupAccountProfileRepository;

	@Inject
	private AccountGroupAccountProfileService accountGroupAccountProfileService;

	private final Logger log = LoggerFactory.getLogger(AccountGroupResource.class);

	@RequestMapping(value = "/stage-report-account-groups", method = RequestMethod.GET)
	public String stageReportAccountGroups(Model model) {
		List<LocationDTO> locationDTOs = locationService.findAllByCompanyAndLocationActivated(true);
		model.addAttribute("locations", locationDTOs);
		List<String> locationPids = locationDTOs.stream().map(LocationDTO::getPid).collect(Collectors.toList());
		model.addAttribute("accountProfiles", locationAccountProfileRepository
				.findAccountProfileByLocationPidInAndAccountProfileActivated(locationPids, Boolean.TRUE));
		model.addAttribute("stageGroups", stageGroupRepository.findAllByCompanyIdAndActivated(Boolean.TRUE));
		model.addAttribute("stages", stageRepository.findAllByCompanyIdAndActivated(Boolean.TRUE));
		model.addAttribute("rcas", rootCauseRepository.findAllByCompanyId());
		model.addAttribute("accountGroups", accountGroupRepository.findAllByCompanyId());
		model.addAttribute("fileUploadNames", Arrays.asList("Quotation", "Flowchart", "Design"));
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/stage/stage-report-account-groups";
	}

	@RequestMapping(value = "/stage-report-account-groups/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<StageHeaderDTO>> filterStageReport(@RequestParam("employeePid") String employeePid,
			@RequestParam("stagePid") String[] stagePids, @RequestParam("locationPid") String[] locationPids,
			@RequestParam("accountPid") String accountPid, @RequestParam("filterBy") String filterBy,
			@RequestParam LocalDate fromDate, @RequestParam LocalDate toDate, @RequestParam String[] accountGroupPid) {
		if (filterBy.equals(StageReportAccountGroupsResource.TODAY)) {
			fromDate = LocalDate.now();
			toDate = fromDate;
		} else if (filterBy.equals(StageReportAccountGroupsResource.YESTERDAY)) {
			fromDate = LocalDate.now().minusDays(1);
			toDate = fromDate;
		} else if (filterBy.equals(StageReportAccountGroupsResource.WTD)) {
			TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
			fromDate = LocalDate.now().with(fieldISO, 1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(StageReportAccountGroupsResource.MTD)) {
			fromDate = LocalDate.now().withDayOfMonth(1);
			toDate = LocalDate.now();
		} else if (filterBy.equals(StageReportAccountGroupsResource.TILLDATE)) {
			fromDate = LocalDate.of(2018, Month.JANUARY, 1);
			toDate = LocalDate.now();
		}
		List<StageHeaderDTO> stageHeaders = createStageReportByEmployeeDocumentAndDate(employeePid,
				Arrays.asList(stagePids), Arrays.asList(locationPids), Arrays.asList(accountGroupPid), accountPid,
				fromDate, toDate);
		return new ResponseEntity<>(stageHeaders, HttpStatus.OK);
	}

	@RequestMapping(value = "/stage-report-account-groups/getAccountGroupsAccountProfiles", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Transactional(readOnly = true)
	public ResponseEntity<List<AccountProfileDTO>> getAccountGroupAccountProfiles(
			@RequestParam("accountGroupPid") String accountGroupPid) {
		log.debug("REST request to accountGroup Account Profiles : {}", accountGroupPid);
		List<AccountProfileDTO> accountProfileDTOs = accountGroupAccountProfileService
				.findAccountProfileByAccountGroupPid(accountGroupPid);
		return new ResponseEntity<>(accountProfileDTOs, HttpStatus.OK);
	}

	private List<StageHeaderDTO> createStageReportByEmployeeDocumentAndDate(String employeePid, List<String> stagePids,
			List<String> locationPids, List<String> accountGroupPid, String accountPid, LocalDate fDate,
			LocalDate tDate) {
		LocalDateTime fromDate = fDate.atTime(0, 0);
		LocalDateTime toDate = tDate.atTime(23, 59);
		Set<Long> stageHeaderIds = stageHeaderRepository.findIdByCreatedDateBetween(fromDate, toDate);
		if (stageHeaderIds.isEmpty()) {
			return Collections.emptyList();
		}
		List<Long> userIds = getUserIdsUnderEmployee(employeePid, false);
		if (userIds.isEmpty()) {
			return Collections.emptyList();
		}
		Set<String> accountPids = new HashSet<>();
		if (!"-1".equals(accountPid)) {
			accountPids.add(accountPid);
		} else {
			accountPids.addAll(
					accountGroupAccountProfileRepository.findAccountProfilePidsByAccountGroupPidIn(accountGroupPid));
		}
		if (accountPids.isEmpty()) {
			return Collections.emptyList();
		}
		List<StageHeader> stageHeaders = stageHeaderRepository.findByUserIdInAndStagePidInAndAccountPidInAndDateBetween(
				userIds, stagePids, accountPids, stageHeaderIds);

		Collection<Optional<StageHeaderDTO>> stageHeaderDtos = stageHeaders.stream()
				.map(sh -> new StageHeaderDTO(sh, filledFormRepository))
				.collect(Collectors.groupingBy(StageHeaderDTO::getAccountProfilePid,
						Collectors.maxBy(Comparator.comparing(StageHeaderDTO::getCreatedDate))))
				.values();
		List<StageHeaderDTO> finalStageHeaderDtos = new ArrayList<>();
		for (Optional<StageHeaderDTO> optStageHeaderDto : stageHeaderDtos) {
			if (optStageHeaderDto.isPresent()) {
				finalStageHeaderDtos.add(optStageHeaderDto.get());
			}
		}
		return finalStageHeaderDtos;
	}

	@RequestMapping(value = "/stage-report-account-groups", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional
	public ResponseEntity<String> saveStageReport(StageHeaderDTO stageHeaderDTO) {
		if (stageHeaderDTO == null) {
			return ResponseEntity.badRequest().body("StageHeaderDTO must not be null");
		}
		if (stageHeaderDTO.getStagePid().isEmpty()) {
			return ResponseEntity.badRequest().body("Stage must not be null");
		}
		if (stageHeaderDTO.getId() <= 0) {
			return ResponseEntity.badRequest().body("Stage header must not be null");
		}
		StageHeader savedStageHeader = stageHeaderRepository.findOne(stageHeaderDTO.getId());
		if (savedStageHeader != null) {
			try {
				EmployeeProfile employee = employeeProfileRepository
						.findEmployeeProfileByUserLogin(SecurityUtils.getCurrentUserLogin());
				StageHeader newStageHeader = new StageHeader();
				newStageHeader.setAccountProfile(savedStageHeader.getAccountProfile());
				newStageHeader.setStage(stageRepository.findOneByPid(stageHeaderDTO.getStagePid()).get());
				if (employee == null) {
					return ResponseEntity.badRequest()
							.body("No employee assigned to user : " + SecurityUtils.getCurrentUserLogin());
				}
				newStageHeader.setEmployeeProfile(employee);
				newStageHeader.setRemarks(stageHeaderDTO.getRemarks());
				newStageHeader.setValue(stageHeaderDTO.getValue());
				newStageHeader.setQuotationNo(stageHeaderDTO.getQuotationNo());
				newStageHeader.setCreatedDate(LocalDateTime.now());
				newStageHeader.setCreatedBy(employee.getUser());
				newStageHeader.setCompany(savedStageHeader.getCompany());

				if (newStageHeader.getStage().getStageNameType() != null
						&& newStageHeader.getStage().getStageNameType() == StageNameType.CLOSED_LOST) {
					// get stage header rca list
					List<String> rcaPids = Arrays.asList(stageHeaderDTO.getRcaPids());
					List<RootCauseAnalysisReason> rcas = rootCauseRepository.findAllByPidsIn(rcaPids);
					List<StageHeaderRca> stageHeaderRcaList = new ArrayList<>();
					for (RootCauseAnalysisReason rca : rcas) {
						StageHeaderRca stageRca = new StageHeaderRca();
						stageRca.setStageHeader(newStageHeader);
						stageRca.setRootCauseAnalysisReason(rca);
						stageRca.setReason(rca.getName());
						stageRca.setCompany(newStageHeader.getCompany());
						stageHeaderRcaList.add(stageRca);
					}
					// save stage header and rca list
					stageHeaderRcaList = stageHeaderRcaRepository.save(stageHeaderRcaList);
					newStageHeader.setStageHeaderRca(stageHeaderRcaList);
				}
				List<MultipartFile> files = stageHeaderDTO.getMultipartFiles();
				// save file
				for (MultipartFile file : files) {
					File uploadedFile = this.fileManagerService.processFileUpload(file.getBytes(),
							file.getOriginalFilename(), file.getContentType());
					StageHeaderFile stageHeaderFile = new StageHeaderFile();
					stageHeaderFile.setFile(uploadedFile);
					stageHeaderFile.setCompanyId(savedStageHeader.getCompany().getId());
					newStageHeader.addStageFiles(stageHeaderFile);
				}
				stageHeaderRepository.save(newStageHeader);
				return new ResponseEntity<>(HttpStatus.OK);
			} catch (FileManagerException | IOException ex) {
				return ResponseEntity.badRequest().body("fileupload exception : " + ex.getMessage());
			}
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/stage-report-account-groups/accountProfile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<AccountProfileDTO> getAccountProfilesByLocationPid(@RequestParam("locationPid") String locationPid) {
		return locationAccountProfileService.findAccountProfileByLocationPid(locationPid);
	}

	@RequestMapping(value = "/stage-report-account-groups/stages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<StageDTO> getStagesByStageGroupPid(@RequestParam("stageGroupPid") String stageGroupPid) {
		List<Stage> stages;
		if ("-1".equals(stageGroupPid)) {
			stages = stageRepository.findAllByCompanyIdAndActivated(Boolean.TRUE);
		} else {
			stages = stageStageGroupRepository.findStageByStageGroupPid(stageGroupPid);
		}
		return stages.stream().map(s -> {
			StageDTO stageDTO = new StageDTO();
			stageDTO.setPid(s.getPid());
			stageDTO.setName(s.getName());
			return stageDTO;
		}).collect(Collectors.toList());
	}

	@RequestMapping(value = "/stage-report-account-groups/stage-header/{stageHeaderId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Transactional(readOnly = true)
	public ResponseEntity<DynamicDocumentHeaderDTO> getStageDetails(@PathVariable Long stageHeaderId) {
		StageHeader stageHeader = stageHeaderRepository.findOne(stageHeaderId);
		if (stageHeader != null && stageHeader.getStageDetails() != null) {
			return dynamicDocumentHeaderService
					.findOneByPid(stageHeader.getStageDetails().getDynamicDocumentHeaderPid())
					.map(dynamicDocumentHeaderDTO -> new ResponseEntity<>(dynamicDocumentHeaderDTO, HttpStatus.OK))
					.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/stage-report-account-groups/stage-header-files/{filepid}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filepid) throws FileManagerException {
		Resource file = this.fileManagerService.loadAsResource(filepid);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	private List<Long> getUserIdsUnderEmployee(String employeePid, boolean inclSubordinate) {
		List<Long> userIds = new ArrayList<>();
		if (employeePid.equals("Dashboard Employee") || employeePid.equals("no")) {
			userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (employeePid.equals("Dashboard Employee")) {
				List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
				List<Long> dashboardUserIds = dashboardUsers.stream().map(User::getId).collect(Collectors.toList());
				Set<Long> uniqueIds = new HashSet<>();
				if (!dashboardUserIds.isEmpty()) {
					if (!userIds.isEmpty()) {
						for (Long uid : userIds) {
							for (Long sid : dashboardUserIds) {
								if (uid.equals(sid)) {
									uniqueIds.add(sid);
								}
							}
						}
					} else {
						userIds = new ArrayList<>(dashboardUserIds);
					}
				}
				if (!uniqueIds.isEmpty()) {
					userIds = new ArrayList<>(uniqueIds);
				}
			} else {
				if (userIds.isEmpty()) {
					List<User> users = userRepository.findAllByCompanyId();
					userIds = users.stream().map(User::getId).collect(Collectors.toList());
				}
			}
		} else {
			if (inclSubordinate) {
				userIds = employeeHierarchyService.getEmployeeSubordinateIds(employeePid);
			} else {
				Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
				if (opEmployee.isPresent()) {
					userIds.add(opEmployee.get().getUser().getId());
				}
			}
		}
		return userIds;
	}

}
