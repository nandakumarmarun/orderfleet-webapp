package com.orderfleet.webapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.orderfleet.webapp.domain.Company;
import com.orderfleet.webapp.domain.EmployeeProfile;
import com.orderfleet.webapp.domain.User;
import com.orderfleet.webapp.repository.CompanyRepository;
import com.orderfleet.webapp.repository.DashboardUserRepository;
import com.orderfleet.webapp.repository.EmployeeProfileRepository;
import com.orderfleet.webapp.security.SecurityUtils;
import com.orderfleet.webapp.service.EmployeeHierarchyService;
import com.orderfleet.webapp.service.EmployeeProfileService;
import com.orderfleet.webapp.web.rest.dto.EmployeeProfileDTO;
import com.orderfleet.webapp.web.rest.dto.InternetSpeedDTO;
import com.orderfleet.webapp.web.rest.dto.LiveTrackingDTO;
import com.orderfleet.webapp.web.rest.dto.LocationData;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Controller class for handling internet speed related requests.
 * @author Rakhi Vineeth
 * @version 1.116.2
 * @since  Jan 2024
 */
@Controller
@RequestMapping("/web")
public class InternetSpeedResource {
    private final Logger log = LoggerFactory.getLogger(InternetSpeedResource.class);
    private static final String AUTH_KEY = "AIzaSyB_KOvF4OXz0C6gM7kLE8BrIhgBjs2QLsg";
    private static final String FIREBASE_URL = "https://salesnrich-8ec69-default-rtdb.firebaseio.com/SalesNrich_InternetSpeed";

    @Inject
    private EmployeeProfileService employeeProfileService;

    @Inject
    private CompanyRepository companyRepository;

    @Inject
    private EmployeeHierarchyService employeeHierarchyService;

    @Inject
    private DashboardUserRepository dashboardUserRepository;

    @Inject
    private EmployeeProfileRepository employeeProfileRepository;

    /**
     * Render the internet speed model for the web page.
     *
     * @param model the Spring model
     * @return the name of the internet speed template
     */
    @RequestMapping("/internetSpeed")
    public String internetspeedModel(Model model) {
        model.addAttribute("employeeProfile",
                employeeProfileService.findAllByCompanyAndDeactivatedEmployeeProfile(true));
        return "company/internetSpeed";
    }



    /**
     * Upload user internet speed information.
     *
     * @param employeePid the employee PID
     * @return a list of InternetSpeedDTO objects
     */
    @RequestMapping(value = "/internetSpeed/uploadUserInternetSpeed", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public @ResponseBody List<InternetSpeedDTO> uploadUserLocations( @RequestParam("employeePid") String employeePid){

        log.debug("Inside upload Internet Speed.........");
        Company company = companyRepository.findOne(SecurityUtils.getCurrentUsersCompanyId());
        String companyName = company.getLegalName();
        Long companyId = company.getId();
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

        List<InternetSpeedDTO> internetSpeedDTOS=new ArrayList<>();
        employeeProfiles.forEach(employee -> {
            String login = employee.getUserLogin();
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
            HttpEntity<T> entity = new HttpEntity<>(createTokenAuthHeaders());

            ResponseEntity<Map<String, InternetSpeedDTO>> internetSpeedResponse = restTemplate.exchange(
                    FIREBASE_URL + "/" + companyName.replaceAll("%20", " ") + "/" + login + ".json?orderBy=\"$key\"&limitToLast=1", HttpMethod.GET,
                    entity, new ParameterizedTypeReference<Map<String, InternetSpeedDTO>>() {
                    });

            if (internetSpeedResponse.getBody() != null) {
                Map<String, InternetSpeedDTO> response = internetSpeedResponse.getBody();
                System.out.println("Size :"+response.toString());
               // List<InternetSpeedDTO> internetSpeedDTOS=new ArrayList<>();
                for (Map.Entry<String, InternetSpeedDTO> entries : response.entrySet()) {
                    InternetSpeedDTO insdto = new InternetSpeedDTO();
                    insdto.setUsername(entries.getValue().getUsername());
                    insdto.setUploadSpeed(entries.getValue().getUploadSpeed());
                    insdto.setDownloadSpeed(entries.getValue().getDownloadSpeed());
                    insdto.setCurrentDateTime(entries.getValue().getCurrentDateTime());
                    internetSpeedDTOS.add(insdto);
                }
            }
//            internetSpeedDTOS.add(internetSpeedResponse.getBody());
        });
        return internetSpeedDTOS;
    }


    /**
     * Create token authentication headers for Firebase API.
     *
     * @return the authentication headers
     */
    public static MultiValueMap<String, String> createTokenAuthHeaders() {
        MultiValueMap<String, String> requestHeaders = new LinkedMultiValueMap<String, String>();
        requestHeaders.add("Authorization", AUTH_KEY);
        requestHeaders.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        return requestHeaders;

    }
}
