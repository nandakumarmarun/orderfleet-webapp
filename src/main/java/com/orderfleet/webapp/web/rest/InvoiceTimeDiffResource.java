package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.domain.enums.DocumentType;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.repository.ExecutiveTaskExecutionRepository;
import com.orderfleet.webapp.repository.UserActivityRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.*;
import com.orderfleet.webapp.web.rest.dto.ActivityDTO;
import com.orderfleet.webapp.web.rest.dto.DocumentDTO;
import com.orderfleet.webapp.web.rest.dto.InvoiceWiseReportView;
import com.orderfleet.webapp.web.rest.dto.LocationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.inject.Inject;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/web")
public class InvoiceTimeDiffResource {
    private final Logger log = LoggerFactory.getLogger(InvoiceTimeDiffResource.class);

    @Inject
    private EmployeeHierarchyService employeeHierarchyService;
    @Inject
    private DocumentService documentService;

    @Inject
    private UserDocumentService userDocumentService;
    @Inject
    private AccountProfileService accountProfileService;
    @Inject
    private UserActivityService userActivityService;
    @Inject
    private LocationAccountProfileService locationAccountProfileService;
    @Inject
    private UserActivityRepository userActivityRepository;

    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    @Inject
    private ExecutiveTaskExecutionRepository executiveTaskExecutionRepository;

    @Inject
    private EmployeeProfileService employeeProfileService;

    @RequestMapping(value = "/invoice-time-diff", method = RequestMethod.GET)
    @Timed
    @Transactional(readOnly = true)
    public String getAllExecutiveTaskExecutions(Pageable pageable, Model model) {
        // user under current user
        List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
        model.addAttribute("dynamicdocuments", documentService.findAllByDocumentType(DocumentType.DYNAMIC_DOCUMENT));
        List<DocumentDTO> documentDTOs = userDocumentService.findDocumentsByUserIsCurrentUser();
        model.addAttribute("documents", documentDTOs);
        if (userIds.isEmpty()) {
            model.addAttribute("employees", employeeProfileService.findAllByCompany());
            model.addAttribute("accounts", accountProfileService.findAllByCompanyAndActivated(true));
            model.addAttribute("activities", userActivityService.findAllDistinctByUserActivityByCompany());
        } else {
            model.addAttribute("employees", employeeProfileService.findAllEmployeeByUserIdsIn(userIds));
            model.addAttribute("accounts", locationAccountProfileService.findAccountProfilesByCurrentUserLocations());
            List<UserActivity> activities = userActivityRepository.findByUserIsCurrentUser();
            List<Activity> activitys = new ArrayList<>();
            for (UserActivity userActivity : activities) {
                activitys.add(userActivity.getActivity());
            }
            model.addAttribute("activities", activitys);
        }

        return "company/InvoiceTimeDiff";
    }

    @RequestMapping(value = "/invoice-time-diff/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<InvoiceWiseReportView>> filterExecutiveTaskExecutions(
            @RequestParam("documentPid") String documentPid, @RequestParam("employeePid") String employeePid,
            @RequestParam("activityPid") String activityPid, @RequestParam("accountPid") String accountPid,
            @RequestParam("filterBy") String filterBy, @RequestParam String fromDate, @RequestParam String toDate,
            @RequestParam boolean inclSubordinate) {

        List<InvoiceWiseReportView> executiveTaskExecutions = new ArrayList<>();
        if (filterBy.equals("TODAY")) {
            executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, LocalDate.now(),
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("YESTERDAY")) {
            LocalDate yeasterday = LocalDate.now().minusDays(1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, yeasterday,
                    yeasterday, inclSubordinate);
        } else if (filterBy.equals("WTD")) {
            TemporalField fieldISO = WeekFields.of(Locale.getDefault()).dayOfWeek();
            LocalDate weekStartDate = LocalDate.now().with(fieldISO, 1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, weekStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("MTD")) {
            LocalDate monthStartDate = LocalDate.now().withDayOfMonth(1);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, monthStartDate,
                    LocalDate.now(), inclSubordinate);
        } else if (filterBy.equals("CUSTOM")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            LocalDate toFateTime = LocalDate.parse(toDate, formatter);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
                    toFateTime, inclSubordinate);
        } else if (filterBy.equals("SINGLE")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
            LocalDate fromDateTime = LocalDate.parse(fromDate, formatter);
            executiveTaskExecutions = getFilterData(employeePid, documentPid, activityPid, accountPid, fromDateTime,
                    fromDateTime, inclSubordinate);
        }
        return new ResponseEntity<>(executiveTaskExecutions, HttpStatus.OK);
    }

    private List<InvoiceWiseReportView> getFilterData(String employeePid, String documentPid, String activityPid, String accountPid, LocalDate fDate, LocalDate tDate, boolean inclSubordinate) {

        LocalDateTime fromDate = fDate.atTime(0, 0);
        LocalDateTime toDate = tDate.atTime(23, 59);

        List<String> activityPids;
        List<String> accountProfilePids;
        List<Long> userIds = employeeProfileRepository.findAllUserIdByEmployeePid(employeePid);

        if (activityPid.equalsIgnoreCase("no") || activityPid.equalsIgnoreCase("planed")
                || activityPid.equalsIgnoreCase("unPlaned")) {
            activityPids = getActivityPids(activityPid, userIds);
        } else {
            activityPids = Arrays.asList(activityPid);
        }
        List<ExecutiveTaskExecution> executiveTaskExecutions = new ArrayList<>();
        log.info("Finding executive Task execution");
        if (accountPid.equalsIgnoreCase("no")) {
            // accountProfilePids = getAccountPids(userIds);
            // if all accounts selected avoid account wise query
            executiveTaskExecutions = executiveTaskExecutionRepository
                    .getByCreatedDateBetweenAndActivityPidInAndUserIdIn(fromDate, toDate, activityPids, userIds);
        } else {
            // if a specific account is selected load data based on that particular account
            accountProfilePids = Arrays.asList(accountPid);
            executiveTaskExecutions = executiveTaskExecutionRepository
                    .getByCreatedDateBetweenAndActivityPidInAndUserIdInAndAccountPidIn(fromDate, toDate, activityPids, userIds,
                            accountProfilePids);
        }
        System.out.println("Size of executiveTaskExecutions:"+executiveTaskExecutions.size());
        EmployeeProfile employeeProfile= employeeProfileRepository.findEmployeeProfileByPid(employeePid);
        List<InvoiceWiseReportView> invoiceWiseReportViews = new ArrayList<>();
        LocalDateTime y = null;
        for (ExecutiveTaskExecution executiveTaskExecution : executiveTaskExecutions) {
              LocalDateTime x = executiveTaskExecution.getSendDate();

            InvoiceWiseReportView invoiceWiseReportView = new InvoiceWiseReportView(executiveTaskExecution);
            invoiceWiseReportView.setAccountProfileName(invoiceWiseReportView.getAccountProfileName());
            if (employeeProfile != null) {
                invoiceWiseReportView.setEmployeeName(employeeProfile.getName());
            }
            String timeSpend = findTimeSpend(executiveTaskExecution.getPunchInDate(),
                        executiveTaskExecution.getSendDate());
                invoiceWiseReportView.setTimeSpend(timeSpend);
                String timebetweenOrders = findTimeSpendBetweenOrder(x,y);
           invoiceWiseReportView.setTimeBetweenOrder(timebetweenOrders);
                 y = x;

            invoiceWiseReportViews.add(invoiceWiseReportView);
        }
        return invoiceWiseReportViews;
    }

    private String findTimeSpendBetweenOrder(LocalDateTime startTime, LocalDateTime endTime) {
        long hours = 00;
        long minutes = 00;
        long seconds = 00;
        long milliseconds = 00;
        if(endTime==null){
            return 00 + " : " + 00 + " : " + 00 + ":" + 000;
        }
        if (startTime != null && endTime != null) {
            long years = startTime.until(endTime, ChronoUnit.YEARS);
            startTime = startTime.plusYears(years);

            long months = startTime.until(endTime, ChronoUnit.MONTHS);
            startTime = startTime.plusMonths(months);

            long days = startTime.until(endTime, ChronoUnit.DAYS);
            startTime = startTime.plusDays(days);

            hours = startTime.until(endTime, ChronoUnit.HOURS);
            startTime = startTime.plusHours(hours);

            minutes = startTime.until(endTime, ChronoUnit.MINUTES);
            startTime = startTime.plusMinutes(minutes);

            seconds = startTime.until(endTime, ChronoUnit.SECONDS);
            startTime = startTime.plusSeconds(seconds);

            milliseconds = startTime.until(endTime, ChronoUnit.MILLIS);
        }

        return hours + " : " + minutes + " : " + seconds + ":" + milliseconds;

    }

    public String findTimeSpend(LocalDateTime startTime, LocalDateTime endTime){

            long hours = 00;
            long minutes = 00;
            long seconds = 00;
            long milliseconds = 00;
            if (startTime != null && endTime != null) {
                long years = startTime.until(endTime, ChronoUnit.YEARS);
                startTime = startTime.plusYears(years);

                long months = startTime.until(endTime, ChronoUnit.MONTHS);
                startTime = startTime.plusMonths(months);

                long days = startTime.until(endTime, ChronoUnit.DAYS);
                startTime = startTime.plusDays(days);

                hours = startTime.until(endTime, ChronoUnit.HOURS);
                startTime = startTime.plusHours(hours);

                minutes = startTime.until(endTime, ChronoUnit.MINUTES);
                startTime = startTime.plusMinutes(minutes);

                seconds = startTime.until(endTime, ChronoUnit.SECONDS);
                startTime = startTime.plusSeconds(seconds);

                milliseconds = startTime.until(endTime, ChronoUnit.MILLIS);
            }

            return hours + " : " + minutes + " : " + seconds + ":" + milliseconds;

        }
            private List<String> getActivityPids(String activityPid, List<Long> userIds) {
        List<String> activityPids;
        List<ActivityDTO> allActivityDTOs = new ArrayList<>();
        if (userIds.isEmpty()) {
            allActivityDTOs.addAll(userActivityService.findAllDistinctByUserActivityByCompany());
        } else {
            Set<UserActivity> userActivities = userActivityService
                    .findUserActivitiesByActivatedTrueAndUserIdIn(userIds);
            allActivityDTOs.addAll(userActivities.stream()
                    .map(usrActvity -> new ActivityDTO(usrActvity.getActivity(), usrActvity.getSaveActivityDuration(),
                            usrActvity.getPlanThrouchOnly(), usrActvity.getExcludeAccountsInPlan(),
                            usrActvity.getInterimSave()))
                    .collect(Collectors.toList()));
        }
        List<ActivityDTO> activityDTOs = new ArrayList<>();
        if (activityPid.equalsIgnoreCase("no")) {
            activityDTOs.addAll(allActivityDTOs);
        } else if (activityPid.equalsIgnoreCase("planed")) {
            allActivityDTOs.forEach(act -> {
                if (act.getPlanThrouchOnly()) {
                    activityDTOs.add(act);
                }
            });
        } else if (activityPid.equalsIgnoreCase("unPlaned")) {
            allActivityDTOs.forEach(act -> {
                if (!act.getPlanThrouchOnly()) {
                    activityDTOs.add(act);
                }
            });
        }
        activityPids = activityDTOs.stream().map(act -> act.getPid()).collect(Collectors.toList());
        return activityPids;

    }
}
