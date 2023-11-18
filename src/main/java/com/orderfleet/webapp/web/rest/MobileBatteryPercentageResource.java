package com.orderfleet.webapp.web.rest;


import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/web")
public class MobileBatteryPercentageResource {
    private final Logger log = LoggerFactory.getLogger(MobileBatteryPercentageResource.class);
    @Inject
    private DashboardUserRepository dashboardUserRepository;

    @Inject
    private EmployeeProfileService employeeProfileService;


    @Inject
    private EmployeeProfileRepository employeeProfileRepository;



    @RequestMapping(value = "/mobile-battery-percentage", method = RequestMethod.GET)
    @Timed
    @Transactional(readOnly = true)
    public String getModel(Model model) throws URISyntaxException {
        log.debug("Web request to get a page of Mobile Battery Configuration");
        model.addAttribute("employeeProfileLocations",
                employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));

        return "company/mobile-battrey-percentage";
    }

    @RequestMapping(value = "/mobile-battery-percentage/filter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @Timed
    @Transactional(readOnly = true)
    public ResponseEntity<List<EmployeeProfileDTO>> filterEmployeeProfileBattery(
            @RequestParam("employeePid") String employeePid) {
        log.info("filter Employee Profile Locations-----");
        List<EmployeeProfileDTO> employeeProfiles = new ArrayList<>();

        List<User> dashboardUsers = dashboardUserRepository.findUsersByCompanyId();
        List<Long> dashboardUsersIds = dashboardUsers.stream().map(a -> a.getId()).collect(Collectors.toList());

        if (employeePid.equalsIgnoreCase("Dashboard Employee")) {
            employeeProfiles = employeeProfileService.findAllEmployeeByUserIdsIn(dashboardUsersIds);
        } else if (employeePid.equalsIgnoreCase("no")) {
            employeeProfiles = employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true);
        } else if (employeePid != null && !employeePid.equalsIgnoreCase("")) {
            List<Long> userIds = Collections.emptyList();
            Optional<EmployeeProfile> opEmployee = employeeProfileRepository.findOneByPid(employeePid);
            if (opEmployee.isPresent()) {
                userIds = Arrays.asList(opEmployee.get().getUser().getId());
            }
            employeeProfiles = employeeProfileService.findAllEmployeeByUserIdsIn(userIds);
        }

        employeeProfiles
                .sort((EmployeeProfileDTO e1, EmployeeProfileDTO e2) -> e1.getName().compareToIgnoreCase(e2.getName()));

        return new ResponseEntity<>(employeeProfiles, HttpStatus.OK);
   }
}
