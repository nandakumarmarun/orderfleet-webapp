package com.orderfleet.webapp.web.rest.Features.dashboraddenormalised;

import com.orderfleet.webapp.domain.*;
import com.orderfleet.webapp.domain.enums.CompanyConfig;
import com.orderfleet.webapp.repository.*;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.LocationHierarchyService;
import com.orderfleet.webapp.service.UserService;
import com.orderfleet.webapp.web.rest.DashboardResource;
import com.orderfleet.webapp.web.rest.dto.DashboardChartDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardHeaderSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardSummaryDTO;
import com.orderfleet.webapp.web.rest.dto.DashboardUserDataDTO;
import com.orderfleet.webapp.web.websocket.dto.DashboardAttendanceDTO;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.servlet.ServletContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Controller
@RequestMapping(value = "/web")
public class DashBoardDenormalisedResource {

    private final Logger log = LoggerFactory.getLogger(DashboardResource.class);


    @Inject
    private DashboardDenormalizedService dashboardDenormalizedService;


    @Inject
    private UserService userService;

    @Inject
    private UserRepository userRepository;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private CompanySettingRepository companySettingRepository;

    @Inject
    private ServletContext servletContext;

    @Inject
    private CompanyConfigurationRepository companyConfigurationRepository;

    @Inject
    private DashboardItemGroupRepository dashboardItemGroupRepository;

    @Inject
    private LocationHierarchyService locationHierarchyService;


    @Inject
    private EmployeeHierarchyService employeeHierarchyService;

    @Inject
    private DashboardUserRepository dashboardUserRepository;

    @Inject
    private AttendanceRepository attendanceRepository;

    @Inject
    private EmployeeProfileLocationRepository employeeProfileLocationRepository;




    @RequestMapping(value = {"/new-dashboard" }, method = RequestMethod.GET)
    public String dashboard(Model model) throws JSONException {
        log.info("Web request to get dashboard common page");
        Long companyId = SecurityUtils.getCurrentUsersCompanyId();
        model.addAttribute("companyId", companyId);

        User user = userService.getCurrentUser();
        if (user != null && user.getEnableWebsocket()) {
            model.addAttribute("enableWebsocket", true);
        } else {
            model.addAttribute("enableWebsocket", false);
        }

        Optional<CompanyConfiguration> companyConfiguration = companyConfigurationRepository
                .findByCompanyIdAndName(companyId, CompanyConfig.THEME);

        if (companyConfiguration.isPresent()) {
            servletContext.setAttribute("currentcss", companyConfiguration.get().getValue());
        } else {
            servletContext.setAttribute("currentcss", "white.css");
        }

        List<DashboardItemGroup> dbItemGroups = dashboardItemGroupRepository.findAllByCompanyId();
        if (!dbItemGroups.isEmpty()) {
            JSONArray jsonArr = new JSONArray();
            for (DashboardItemGroup dashboardItemGroup : dbItemGroups) {
                JSONObject jo = new JSONObject();
                jo.put("id", dashboardItemGroup.getId());
                jo.put("name", dashboardItemGroup.getName());
                jsonArr.put(jo);
            }
            model.addAttribute("dbItemGroups", jsonArr.toString());
        }

        System.out.println("loading dashbord....");
        return "company/dashboardDenormalisedNew";
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/dashboard/summary-denormalised", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardHeaderSummaryDTO> getDashboardSummaryDataDenormlaised(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(dashboardDenormalizedService.loadDashboardHeaderSummary(date.atTime(0, 0), date.atTime(23, 59)), HttpStatus.OK);
    }

    @Transactional(readOnly = true)
    @RequestMapping(value = "/dashboard/user-summary-denormalised", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<DashboardUserDataDTO<DashboardSummaryDTO>>>getDashboardUserSummaryDataDenormlaised(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        List<DashboardUserDataDTO<DashboardSummaryDTO>> dashboardUserDataDTOS = new ArrayList<>();
        Optional<User> user = userRepository.findOneByLogin(SecurityUtils.getCurrentUserLogin());
        long parentLocation = 0;
        if(user.isPresent()){
            dashboardUserDataDTOS = dashboardDenormalizedService.loadUserSummaryData(date.atTime(0, 0), date.atTime(23, 59), parentLocation, user.get());
        }
        return new ResponseEntity<>(dashboardUserDataDTOS, HttpStatus.OK);
    }


    @Transactional(readOnly = true)
    @RequestMapping(value = "/dashboard/summary/chart-denormalised", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardChartDTO> getDashboardSummaryFunnelChartData(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(dashboardDenormalizedService.loadSummaryDenormalisedChart(date.atTime(0, 0), date.atTime(23, 59)),
                HttpStatus.OK);
    }


    @RequestMapping(value = "/dashboard/load-summary-denormalised", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardHeaderSummaryDTO> LoadDataDenormlaised(
            @RequestParam(value = "fromDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate, @RequestParam(value = "toDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        System.out.println("from Date : " + fromDate);
        System.out.println("to Date : "   + toDate);
        System.out.println("CompanyId : " + SecurityUtils.getCurrentUsersCompanyId());

        dashboardDenormalizedService.loadDatabetweenTimePeriod(fromDate,toDate, SecurityUtils.getCurrentUsersCompanyId());
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/new-dashboard/delay-time-denormalised", method = RequestMethod.GET)
    public @ResponseBody int getDelayTime() {
        CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
        if (companySetting != null && companySetting.getDashboardConfiguration() != null) {
            return companySetting.getDashboardConfiguration().getDelayTime();
        }
        return 0;
    }

    @RequestMapping(value = "/new-dashboard/update-delay-time-denormalised", method = RequestMethod.POST)
    public @ResponseBody int updateDelayTime(@RequestParam int delayTime) {
        CompanySetting companySetting = companySettingRepository.findOneByCompanyId();
        if (companySetting == null) {
            companySetting = new CompanySetting();
            companySetting.setCompany(companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId()));
        }
        log.debug("delay Time : "+delayTime);
        companySetting.getDashboardConfiguration().setDelayTime(delayTime);
        companySettingRepository.save(companySetting);
        return 0;
    }

    @RequestMapping(value = "/new-dashboard/attendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardAttendanceDTO> getAttendanceData(
            @RequestParam(value = "date", required = false) LocalDate date,
            @RequestParam(value = "territoryId", required = false) Long territoryId) {
        log.info("Web request to get dashboard attendance");
        long totalUsers;
        long attendedUsers;

        User user = userService.getCurrentUser();
        if (territoryId != null && territoryId > 0) {
            List<Long> locationIds = locationHierarchyService.getAllChildrenIdsByParentId(territoryId);
            Set<Long> uniqueUserIds = employeeProfileLocationRepository.findEmployeeUserIdsByLocationIdIn(locationIds);
            List<Long> userIds = new ArrayList<>(uniqueUserIds);
            totalUsers = dashboardUserRepository.countByUserIdIn(userIds);

            attendedUsers = attendanceRepository.countByUserIdInAndDateBetween(userIds, date.atTime(0, 0),
                    date.atTime(23, 59));

        } else {
            if (user.getShowAllUsersData()) {
                Set<Long> dashboardUserIds = dashboardUserRepository.findUserIdsByCompanyId();
                totalUsers = dashboardUserIds.size();

                attendedUsers = attendanceRepository.countByUserIdInAndDateBetween(dashboardUserIds, date.atTime(0, 0),
                        date.atTime(23, 59));

            } else {
                List<Long> userIds = employeeHierarchyService.getCurrentUsersSubordinateIds();
                Set<Long> dbUserIds = dashboardUserRepository.findUserIdsByUserIdIn(userIds);
                totalUsers = dbUserIds.size();
                attendedUsers = attendanceRepository.countByUserIdInAndDateBetween(dbUserIds, date.atTime(0, 0),
                        date.atTime(23, 59));
            }
        }
        return new ResponseEntity<>(new DashboardAttendanceDTO(totalUsers, attendedUsers), HttpStatus.OK);
    }



    @Transactional(readOnly = true)
    @RequestMapping(value = "/new-dashboard/summary/attendance", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<DashboardHeaderSummaryDTO> getDashboardAttendanceSummaryData(
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Web request to get dashboard  summary");
        return new ResponseEntity<>(dashboardDenormalizedService.loadDashboardAttendanceHeader(date.atTime(0, 0), date.atTime(23, 59)),
                HttpStatus.OK);
    }



}
