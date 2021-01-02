package com.orderfleet.webapp.web.rest;

import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.ExecutiveTaskPlan;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.domain.enums.TaskPlanStatus;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskPlanRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.api.dto.UserDTO;
import com.orderfleet.webapp.web.rest.dto.DayPlanExecutionSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.ExecutiveTaskPlanDTO;

/**
 * Web controller for managing DayPlanExecutionSummery.
 *
 * @author Sarath
 * @since Jan 28, 2017
 */

@Controller
@RequestMapping("/web")
public class DayPlanExecutionSummaryResource {

	private final Logger log = LoggerFactory.getLogger(DayPlanExecutionSummaryResource.class);

	@Inject
	private UserService userService;

	@Inject
	private ExecutiveTaskPlanRepository executiveTaskPlanRepository;

	@Inject
	private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

	@Inject
	private EmployeeHierarchyService employeeHierarchyService;

	@Inject
	private EmployeeProfileService employeeProfileService;

	@Inject
	private DashboardUserRepository dashboardUserRepository;

	/**
	 * GET /day-plans-execution-summary : get all the DayPlanExecutionReport.
	 *
	 * @param pageable the pagination information
	 * @return the ResponseEntity with status 200 (OK) and the list of users in body
	 * @throws URISyntaxException if there is an error to generate the pagination
	 *                            HTTP headers
	 */
	@Timed
	@RequestMapping(value = "/day-plans-execution-summary", method = RequestMethod.GET)
	public String getAllDayPlanExecutionReport(Model model) throws URISyntaxException {
		log.debug("Web request to get a page of Day Plan Execution Summary");
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/dayPlanExecutionSummary";
	}

	@Timed
	@RequestMapping(value = "/day-plans-execution-summary-user-wise", method = RequestMethod.GET)
	public String getDayPlanExecutionSummaryUserwise(Model model) {
		return "company/dayPlanExecutionSummaryUserWise";
	}

	@Timed
	@RequestMapping(value = "/day-plans-execution-summary-activity-wise", method = RequestMethod.GET)
	public String getDayPlanExecutionSummaryActivitywise(Model model) {
		List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
		if (userIds.isEmpty()) {
			model.addAttribute("employees", employeeProfileService.findAllByCompany());
		} else {
			model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
		}
		return "company/dayPlanExecutionSummaryActivityWise";
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/day-plans-execution-summary/filter", method = RequestMethod.GET)
	public ResponseEntity<Map<DayPlanExecutionSummaryDTO, Map<DayPlanExecutionSummaryDTO, List<ExecutiveTaskPlanDTO>>>> getTaskPlanByUserAndPlanDateBetween(
			@RequestParam("employeePid") String employeePid,
			@RequestParam(required = true, name = "startDate") LocalDate startDate,
			@RequestParam(required = true, name = "endDate") LocalDate endDate) {
		log.debug("Web request to get Executive Task Plan");
		List<ExecutiveTaskPlan> actualEcutiveTaskPlans = null;

		List<String> userPids = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
			if (employeeProfileDTO.getPid() != null) {
				userPids.add(employeeProfileDTO.getUserPid());
			}
		} else {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.size() > 0) {
				List<UserDTO> users = userService.findByUserIdIn(userIds);
				for(UserDTO user:users) {
					userPids.add(user.getPid());
				}
			}
		}

		actualEcutiveTaskPlans = executiveTaskPlanRepository.findByUserPidInAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(
				userPids, startDate.atTime(0, 0), endDate.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
//		String userPid = "-1";
//		if (employeeProfileDTO.getPid() != null) {
//			userPid = employeeProfileDTO.getUserPid();
//		}

		// filter by user and date between
//		if (userPid != null && !"-1".equals(userPid)) {
//			actualEcutiveTaskPlans = executiveTaskPlanRepository
//					.findByUserPidAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPid, startDate.atTime(0, 0),
//							endDate.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
//		}
//		// filter by date between
//		if (userPid == null || "-1".equals(userPid)) {
//			actualEcutiveTaskPlans = executiveTaskPlanRepository.findByPlannedDateBetweenAndCompanyIdOrderByIdAsc(
//					startDate.atTime(0, 0), endDate.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
//		}
		Map<LocalDate, Map<String, List<ExecutiveTaskPlan>>> plansGroupedByDateThenByUser = null;
		// map for UI
		Map<DayPlanExecutionSummaryDTO, Map<DayPlanExecutionSummaryDTO, List<ExecutiveTaskPlanDTO>>> resultMap = new TreeMap<>();
		if (actualEcutiveTaskPlans.size() > 0) {
			// group by plan date, then by user
			plansGroupedByDateThenByUser = actualEcutiveTaskPlans.stream().collect(Collectors.groupingBy(
					etp -> etp.getPlannedDate().toLocalDate(), Collectors.groupingBy(etp -> etp.getUser().getLogin())));
			for (Map.Entry<LocalDate, Map<String, List<ExecutiveTaskPlan>>> entry : plansGroupedByDateThenByUser
					.entrySet()) {

				Map<DayPlanExecutionSummaryDTO, List<ExecutiveTaskPlanDTO>> resultChildMap = new LinkedHashMap<>();
				// parent
				DayPlanExecutionSummaryDTO dpesDateDTO = new DayPlanExecutionSummaryDTO();
				Long dgAchieved = 0L;
				int dgScheduled = 0;
				LocalDate today = LocalDate.now();
				if (today.isAfter(entry.getKey())) {
					for (Map.Entry<String, List<ExecutiveTaskPlan>> entry2 : entry.getValue().entrySet()) {
						List<ExecutiveTaskPlan> childValue = entry2.getValue();
						for (ExecutiveTaskPlan executiveTaskPlan : childValue) {
							if (executiveTaskPlan.getTaskPlanStatus().equals(TaskPlanStatus.PENDING)) {
								executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.SKIPPED);
								executiveTaskPlan.setUserRemarks("Unattended");
							} else if (executiveTaskPlan.getTaskPlanStatus().equals(TaskPlanStatus.CREATED)) {
								executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.SKIPPED);
								executiveTaskPlan.setUserRemarks("Unattended");
							}
						}
						List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
								.findExecutiveTaskPlanByExecutiveTaskPlanIn(childValue);
						Long achieved = childValue.stream()
								.filter(etp -> TaskPlanStatus.COMPLETED == etp.getTaskPlanStatus()).count();
						dgAchieved += achieved;
						int scheduled = childValue.size();
						dgScheduled += scheduled;
						List<ExecutiveTaskPlan> convertedTaskPlans = childValue.stream().map(etp -> {
							int index = savedExecutedPlan.indexOf(etp);
							if (index != -1) {
								etp.setSortOrder(index + 1);
							}
							return etp;
						}).collect(Collectors.toList());
						DayPlanExecutionSummaryDTO dpesUserDTO = new DayPlanExecutionSummaryDTO();
						dpesUserDTO.setDate(entry2.getKey()); // set user name
						dpesUserDTO.setScheduled(Long.valueOf(scheduled));
						dpesUserDTO.setAchieved(achieved);
						double total = (Double.valueOf(achieved) / Double.valueOf(scheduled));
						double percentage = total * 100;
						DecimalFormat decimalFormat = new DecimalFormat("#.##");
						String formatedTotal = decimalFormat.format(percentage);
						dpesUserDTO.setPercentage(Double.parseDouble(formatedTotal));
						if (entry2.getValue().get(0).getTaskList() != null) {
							dpesUserDTO.setAlias(entry2.getValue().get(0).getTaskList().getAlias());
						}
						resultChildMap.put(dpesUserDTO, convertedTaskPlans.stream().map(ExecutiveTaskPlanDTO::new)
								.collect(Collectors.toList()));
					}
				} else {
					for (Map.Entry<String, List<ExecutiveTaskPlan>> entry2 : entry.getValue().entrySet()) {
						List<ExecutiveTaskPlan> childValue = entry2.getValue();
						List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
								.findExecutiveTaskPlanByExecutiveTaskPlanIn(childValue);
						Long achieved = childValue.stream()
								.filter(etp -> TaskPlanStatus.COMPLETED == etp.getTaskPlanStatus()).count();
						dgAchieved += achieved;
						int scheduled = childValue.size();
						dgScheduled += scheduled;
						List<ExecutiveTaskPlan> convertedTaskPlans = childValue.stream().map(etp -> {
							int index = savedExecutedPlan.indexOf(etp);
							if (index != -1) {
								etp.setSortOrder(index + 1);
							}
							return etp;
						}).collect(Collectors.toList());
						DayPlanExecutionSummaryDTO dpesUserDTO = new DayPlanExecutionSummaryDTO();
						dpesUserDTO.setDate(entry2.getKey()); // set user name
						dpesUserDTO.setScheduled(Long.valueOf(scheduled));
						if (entry2.getValue().get(0).getTaskList() != null) {
							dpesUserDTO.setAlias(entry2.getValue().get(0).getTaskList().getAlias());
						}
						dpesUserDTO.setAchieved(achieved);
						double total = (Double.valueOf(achieved) / Double.valueOf(scheduled));
						double percentage = total * 100;
						DecimalFormat decimalFormat = new DecimalFormat("#.##");
						String formatedTotal = decimalFormat.format(percentage);
						dpesUserDTO.setPercentage(Double.parseDouble(formatedTotal));
						resultChildMap.put(dpesUserDTO, convertedTaskPlans.stream().map(ExecutiveTaskPlanDTO::new)
								.collect(Collectors.toList()));
					}
				}
				// set date.
				dpesDateDTO.setDate(entry.getKey().toString());
				dpesDateDTO.setScheduled(Long.valueOf(dgScheduled));
				dpesDateDTO.setAchieved(dgAchieved);
				double total = (Double.valueOf(dgAchieved) / Double.valueOf(dgScheduled));
				double percentage = total * 100;
				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				String formatedTotal = decimalFormat.format(percentage);
				dpesDateDTO.setPercentage(Double.parseDouble(formatedTotal));
				resultMap.put(dpesDateDTO, resultChildMap);
			}
		}
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/day-plans-execution-summary-user-wise/filter", method = RequestMethod.GET)
	public ResponseEntity<Map<DayPlanExecutionSummaryDTO, Map<DayPlanExecutionSummaryDTO, List<ExecutiveTaskPlanDTO>>>> getTaskPlanByUserAndPlanDateBetweenUserWise(
			@RequestParam("employeePid") String employeePid,
			@RequestParam(required = true, name = "startDate") LocalDate startDate,
			@RequestParam(required = true, name = "endDate") LocalDate endDate) {
		log.debug("Web request to get Executive Task Plan");
		List<ExecutiveTaskPlan> actualEcutiveTaskPlans = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no") && !employeePid.equals("Dashboard Employee")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "-1";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		List<String> userPids = new ArrayList<>();
		// filter by user and date between
		if (userPid != null && !"-1".equals(userPid)) {
			actualEcutiveTaskPlans = executiveTaskPlanRepository
					.findByUserPidAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPid, startDate.atTime(0, 0),
							endDate.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
		}
		// filter by date between

		if (userPid != null && "-1".equals(userPid) && employeePid.equals("Dashboard Employee")) {

			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();

			List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();

			List<Long> dashboardUserIds = dashboardUsers.stream().map(a -> a.getId()).collect(Collectors.toList());

			Set<Long> uniqueIds = new HashSet<>();
			if (!dashboardUserIds.isEmpty()) {
				for (Long uid : userIds) {
					for (Long sid : dashboardUserIds) {
						if (uid.equals(sid)) {
							uniqueIds.add(sid);
						}
					}
				}
			}
			if (userIds.isEmpty()) {
				uniqueIds.addAll(dashboardUserIds);
			}

			if (uniqueIds.size() > 0) {
				userIds = new ArrayList<>(uniqueIds);
			}

			if (!userIds.isEmpty()) {
				userPids = userService.findUserPidsByUserIdIn(userIds);
				actualEcutiveTaskPlans = executiveTaskPlanRepository.findByUserPidInAndPlannedDateBetweenOrderByIdAsc(
						userPids, startDate.atTime(0, 0), endDate.atTime(23, 59));
			}

		} else if (userPid == null || "-1".equals(userPid)) {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.isEmpty()) {
				userPids = userService.findAllUserPidsByCompany();
			} else {
				userPids = userService.findUserPidsByUserIdIn(userIds);
			}

			actualEcutiveTaskPlans = executiveTaskPlanRepository.findByUserPidInAndPlannedDateBetweenOrderByIdAsc(
					userPids, startDate.atTime(0, 0), endDate.atTime(23, 59));
		}
		Map<String, Map<LocalDate, List<ExecutiveTaskPlan>>> plansGroupedByUserThenByDate = null;
		// map for UI
		Map<DayPlanExecutionSummaryDTO, Map<DayPlanExecutionSummaryDTO, List<ExecutiveTaskPlanDTO>>> resultMap = new TreeMap<>();
		if (!actualEcutiveTaskPlans.isEmpty()) {
			// group by plan date, then by user
			plansGroupedByUserThenByDate = actualEcutiveTaskPlans.stream()
					.collect(Collectors.groupingBy(etp -> etp.getUser().getLogin(), Collectors
							.groupingBy(etp -> etp.getPlannedDate().toLocalDate(), TreeMap::new, Collectors.toList())));
			for (Map.Entry<String, Map<LocalDate, List<ExecutiveTaskPlan>>> entry : plansGroupedByUserThenByDate
					.entrySet()) {
				Map<DayPlanExecutionSummaryDTO, List<ExecutiveTaskPlanDTO>> resultChildMap = new LinkedHashMap<>();
				// parent
				DayPlanExecutionSummaryDTO dpesUserDTO = new DayPlanExecutionSummaryDTO();
				Long dgAchieved = 0L;
				int dgScheduled = 0;

				for (Map.Entry<LocalDate, List<ExecutiveTaskPlan>> entry2 : entry.getValue().entrySet()) {

					// check date for adding skipped for pending and created
					LocalDate today = LocalDate.now();
					if (today.isAfter(entry2.getKey())) {
						List<ExecutiveTaskPlan> childValue = entry2.getValue();
						for (ExecutiveTaskPlan executiveTaskPlan : childValue) {
							if (executiveTaskPlan.getTaskPlanStatus().equals(TaskPlanStatus.PENDING)) {
								executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.SKIPPED);
								executiveTaskPlan.setUserRemarks("Unattended");
							} else if (executiveTaskPlan.getTaskPlanStatus().equals(TaskPlanStatus.CREATED)) {
								executiveTaskPlan.setTaskPlanStatus(TaskPlanStatus.SKIPPED);
								executiveTaskPlan.setUserRemarks("Unattended");
							}
						}
						List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
								.findExecutiveTaskPlanByExecutiveTaskPlanIn(childValue);
						Long achieved = childValue.stream()
								.filter(etp -> TaskPlanStatus.COMPLETED == etp.getTaskPlanStatus()).count();
						dgAchieved += achieved;
						int scheduled = childValue.size();
						dgScheduled += scheduled;
						List<ExecutiveTaskPlan> convertedTaskPlans = childValue.stream().map(etp -> {
							int index = savedExecutedPlan.indexOf(etp);
							if (index != -1) {
								etp.setSortOrder(index + 1);
							}
							return etp;
						}).collect(Collectors.toList());
						// set date
						DayPlanExecutionSummaryDTO dpesDateDTO = new DayPlanExecutionSummaryDTO();
						dpesDateDTO.setDate(entry2.getKey().toString());
						dpesDateDTO.setScheduled(Long.valueOf(scheduled));
						dpesDateDTO.setAchieved(achieved);
						dpesDateDTO.setAlias(convertedTaskPlans.get(0).getTaskList().getAlias());
						double total = (Double.valueOf(achieved) / Double.valueOf(scheduled));
						double percentage = total * 100;
						DecimalFormat decimalFormat = new DecimalFormat("#.##");
						String formatedTotal = decimalFormat.format(percentage);
						dpesDateDTO.setPercentage(Double.parseDouble(formatedTotal));
						resultChildMap.put(dpesDateDTO, convertedTaskPlans.stream().map(ExecutiveTaskPlanDTO::new)
								.collect(Collectors.toList()));
					} else {
						List<ExecutiveTaskPlan> childValue = entry2.getValue();
						List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
								.findExecutiveTaskPlanByExecutiveTaskPlanIn(childValue);
						Long achieved = childValue.stream()
								.filter(etp -> TaskPlanStatus.COMPLETED == etp.getTaskPlanStatus()).count();
						dgAchieved += achieved;
						int scheduled = childValue.size();
						dgScheduled += scheduled;
						List<ExecutiveTaskPlan> convertedTaskPlans = childValue.stream().map(etp -> {
							int index = savedExecutedPlan.indexOf(etp);
							if (index != -1) {
								etp.setSortOrder(index + 1);
							}
							return etp;
						}).collect(Collectors.toList());
						// set date
						DayPlanExecutionSummaryDTO dpesDateDTO = new DayPlanExecutionSummaryDTO();
						dpesDateDTO.setDate(entry2.getKey().toString());
						dpesDateDTO.setScheduled(Long.valueOf(scheduled));
						dpesDateDTO.setAchieved(achieved);
						dpesDateDTO.setAlias(convertedTaskPlans.get(0).getTaskList().getAlias());
						double total = (Double.valueOf(achieved) / Double.valueOf(scheduled));
						double percentage = total * 100;
						DecimalFormat decimalFormat = new DecimalFormat("#.##");
						String formatedTotal = decimalFormat.format(percentage);
						dpesDateDTO.setPercentage(Double.parseDouble(formatedTotal));
						resultChildMap.put(dpesDateDTO, convertedTaskPlans.stream().map(ExecutiveTaskPlanDTO::new)
								.collect(Collectors.toList()));
					}
				}
				// set user/employee name.
				EmployeeProfileDTO employeeProfileDTO1 = employeeProfileService
						.findEmployeeProfileByUserLogin(entry.getKey());
				dpesUserDTO.setDate(employeeProfileDTO1.getName()); // set
																	// employee
																	// name
				dpesUserDTO.setScheduled(Long.valueOf(dgScheduled));
				dpesUserDTO.setAchieved(dgAchieved);
				double total = (Double.valueOf(dgAchieved) / Double.valueOf(dgScheduled));
				double percentage = total * 100;
				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				String formatedTotal = decimalFormat.format(percentage);
				dpesUserDTO.setPercentage(Double.parseDouble(formatedTotal));
				resultMap.put(dpesUserDTO, resultChildMap);
			}
		}
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@Timed
	@Transactional(readOnly = true)
	@RequestMapping(value = "/day-plans-execution-summary-activity-wise/filter", method = RequestMethod.GET)
	public ResponseEntity<Map<DayPlanExecutionSummaryDTO, Map<DayPlanExecutionSummaryDTO, Map<LocalDate, List<ExecutiveTaskPlanDTO>>>>> getTaskPlanByUserAndPlanDateBetweenActivityWise(
			@RequestParam("employeePid") String employeePid,
			@RequestParam(required = true, name = "startDate") LocalDate startDate,
			@RequestParam(required = true, name = "endDate") LocalDate endDate) {
		log.debug("Web request to get Executive Task Plan");
		List<ExecutiveTaskPlan> actualEcutiveTaskPlans = new ArrayList<>();
		EmployeeProfileDTO employeeProfileDTO = new EmployeeProfileDTO();
		if (!employeePid.equals("no")) {
			employeeProfileDTO = employeeProfileService.findOneByPid(employeePid).get();
		}
		String userPid = "-1";
		if (employeeProfileDTO.getPid() != null) {
			userPid = employeeProfileDTO.getUserPid();
		}
		List<String> userPids;
		// filter by user and date between
		if (userPid != null && !"-1".equals(userPid)) {
			actualEcutiveTaskPlans = executiveTaskPlanRepository
					.findByUserPidAndPlannedDateBetweenAndCompanyIdOrderByIdAsc(userPid, startDate.atTime(0, 0),
							endDate.atTime(23, 59), SecurityUtils.getCurrentUsersCompanyId());
		}
		// filter by date between
		if (userPid == null || "-1".equals(userPid)) {
			List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
			if (userIds.isEmpty()) {
				userPids = userService.findAllUserPidsByCompany();
			} else {
				userPids = userService.findUserPidsByUserIdIn(userIds);
			}
			actualEcutiveTaskPlans = executiveTaskPlanRepository.findByUserPidInAndPlannedDateBetweenOrderByIdAsc(
					userPids, startDate.atTime(0, 0), endDate.atTime(23, 59));
		}
		Map<String, Map<String, List<ExecutiveTaskPlan>>> plansGroupedByActivityThenByDate = null;
		// map for UI
		Map<DayPlanExecutionSummaryDTO, Map<DayPlanExecutionSummaryDTO, Map<LocalDate, List<ExecutiveTaskPlanDTO>>>> resultMap = new TreeMap<>();
		if (!actualEcutiveTaskPlans.isEmpty()) {
			// group by plan date, then by activity
			plansGroupedByActivityThenByDate = actualEcutiveTaskPlans.stream().collect(Collectors.groupingBy(
					etp -> etp.getUser().getLogin(), Collectors.groupingBy(etp -> etp.getActivity().getName())));
			for (Map.Entry<String, Map<String, List<ExecutiveTaskPlan>>> entry : plansGroupedByActivityThenByDate
					.entrySet()) {
				Map<DayPlanExecutionSummaryDTO, Map<LocalDate, List<ExecutiveTaskPlanDTO>>> resultChildMap = new LinkedHashMap<>();
				// parent
				DayPlanExecutionSummaryDTO dpesUserDTO = new DayPlanExecutionSummaryDTO();
				Long dgAchieved = 0L;
				int dgScheduled = 0;
				for (Map.Entry<String, List<ExecutiveTaskPlan>> entry2 : entry.getValue().entrySet()) {
					List<ExecutiveTaskPlan> childValue = entry2.getValue();
					List<ExecutiveTaskPlan> savedExecutedPlan = executiveTaskExecutionRepository
							.findExecutiveTaskPlanByExecutiveTaskPlanIn(childValue);
					Long achieved = childValue.stream()
							.filter(etp -> TaskPlanStatus.COMPLETED == etp.getTaskPlanStatus()).count();
					dgAchieved += achieved;
					int scheduled = childValue.size();
					dgScheduled += scheduled;
					List<ExecutiveTaskPlan> convertedTaskPlans = childValue.stream().map(etp -> {
						int index = savedExecutedPlan.indexOf(etp);
						if (index != -1) {
							etp.setSortOrder(index + 1);
						}
						return etp;
					}).collect(Collectors.toList());

					// group by date wise
					Map<LocalDate, List<ExecutiveTaskPlan>> planGroupByDate = convertedTaskPlans.stream()
							.collect(Collectors.groupingBy(etp -> etp.getPlannedDate().toLocalDate(), TreeMap::new,
									Collectors.toList()));
					Map<LocalDate, List<ExecutiveTaskPlanDTO>> planGroupByDateDto = new LinkedHashMap<>();
					planGroupByDate.forEach((k, v) -> planGroupByDateDto.put(k,
							v.stream().map(ExecutiveTaskPlanDTO::new).collect(Collectors.toList())));

					// set Activity
					DayPlanExecutionSummaryDTO dpesDateDTO = new DayPlanExecutionSummaryDTO();
					dpesDateDTO.setDate(entry2.getKey());
					dpesDateDTO.setScheduled(Long.valueOf(scheduled));
					dpesDateDTO.setAchieved(achieved);
					double total = (Double.valueOf(achieved) / Double.valueOf(scheduled));
					double percentage = total * 100;
					DecimalFormat decimalFormat = new DecimalFormat("#.##");
					String formatedTotal = decimalFormat.format(percentage);
					dpesDateDTO.setPercentage(Double.parseDouble(formatedTotal));
					resultChildMap.put(dpesDateDTO, planGroupByDateDto);
				}
				// set user/employee name.
				EmployeeProfileDTO employeeProfileDTO1 = employeeProfileService
						.findEmployeeProfileByUserLogin(entry.getKey());
				dpesUserDTO.setDate(employeeProfileDTO1.getName()); // set
																	// employee
																	// name
				dpesUserDTO.setScheduled(Long.valueOf(dgScheduled));
				dpesUserDTO.setAchieved(dgAchieved);
				double total = (Double.valueOf(dgAchieved) / Double.valueOf(dgScheduled));
				double percentage = total * 100;
				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				String formatedTotal = decimalFormat.format(percentage);
				dpesUserDTO.setPercentage(Double.parseDouble(formatedTotal));
				resultMap.put(dpesUserDTO, resultChildMap);
			}
		}
		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

}
